package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.DAOs.CategoryDAO;
import com.api.glovoCRM.DAOs.ImageAssociationsDAO;
import com.api.glovoCRM.DAOs.ImageDAO;
import com.api.glovoCRM.DAOs.SubCategoryDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.FileUploadEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.MinioOperationEx;
import com.api.glovoCRM.Models.EstablishmentModels.Category;
import com.api.glovoCRM.Models.EstablishmentModels.SubCategory;
import com.api.glovoCRM.Rest.Requests.SubCategoryRequests.SubCategoryCreateRequest;
import com.api.glovoCRM.Rest.Requests.SubCategoryRequests.SubCategoryPatchRequest;
import com.api.glovoCRM.Rest.Requests.SubCategoryRequests.SubCategoryUpdateRequest;
import com.api.glovoCRM.Services.BaseService;
import com.api.glovoCRM.Services.ImageCacheService;
import com.api.glovoCRM.Specifications.EstablimentSpecifications.SubcategorySpecification;
import com.api.glovoCRM.Utils.Minio.MinioService;
import com.api.glovoCRM.constants.EntityType;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@CacheConfig (cacheNames = "app_subcategories")
public class SubCategoryService extends BaseService<SubCategory, SubCategoryCreateRequest, SubCategoryUpdateRequest, SubCategoryPatchRequest> {

    private final SubCategoryDAO subCategoryDAO;
    private final CategoryDAO categoryDAO;
    private final SubcategorySpecification subcategorySpecification;
    private final CacheManager cacheManager;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public SubCategoryService(SubCategoryDAO subCategoryDAO, CategoryDAO categoryDAO, ImageDAO imageDAO, ImageAssociationsDAO imageAssociationsDAO, MinioService minioService, SubcategorySpecification subcategorySpecification, @Qualifier ("cacheManager") CacheManager cacheManager, ImageCacheService imageCacheService, TransactionTemplate transactionTemplate) {
        super(imageCacheService, imageDAO, imageAssociationsDAO, minioService);
        this.subCategoryDAO = subCategoryDAO;
        this.categoryDAO = categoryDAO;
        this.subcategorySpecification = subcategorySpecification;
        this.cacheManager = cacheManager;
        this.transactionTemplate = transactionTemplate;
    }

    @Transactional (readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Cacheable(value = "app_subcategories", key = "#id")
    @Override
    public SubCategory findById(Long id) {
        return getEntityById(id, subCategoryDAO);
    }

    @Transactional (readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Cacheable(value = "app_subcategories", key = "'all_subcategories'")
    @Override
    public List<SubCategory> findAll() {
        return subCategoryDAO.findAllSubCategories();
    }

    @Override
    @Caching(
            put = @CachePut(value = "app_subcategories", key = "#result.id"),
            evict = {
                    @CacheEvict(value = "app_categories", allEntries = true)
            }
    )
    @Transactional (propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SubCategory createEntity(SubCategoryCreateRequest request) {
        if (!categoryDAO.existsById(request.getCategoryId())) {
            throw new SuchResourceNotFoundEx("Такой категории нет в системе");
        }
        if (subCategoryDAO.existsByName(request.getName())) {
            throw new AlreadyExistsEx("Такая подкатегория уже существует");
        }
        SubCategory subCategory = new SubCategory();
        subCategory.setName(request.getName());
        subCategory.setCategoryId(request.getCategoryId());
        SubCategory savedSubCategory = subCategoryDAO.save(subCategory);
        log.debug("Подкатегория создана. ID: {}", savedSubCategory.getId());
        try {
            createImageRecord(request.getImage(), "subcategories", EntityType.SubCategory, savedSubCategory.getId());
            return savedSubCategory;
        } catch (FileUploadEx e) {
            log.error("Ошибка при сохранении изображения в MinIO для подкатегории {}: {}", savedSubCategory.getId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при создании подкатегории {}: {}", savedSubCategory.getId(), e.getMessage());
            throw new RuntimeException("Не удалось создать подкатегорию", e);
        }
    }

    @Override
    @Transactional (propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Caching(
            put = @CachePut(value = "app_subcategories", key = "#id"),
            evict = {
                    @CacheEvict(value = "app_categories", allEntries = true),
                    @CacheEvict(value = "app_subcategories", allEntries = true)
            }
    )
    public SubCategory updateEntity(Long id, SubCategoryUpdateRequest request) {
        SubCategory subCategory = getEntityById(id, subCategoryDAO);
        if (request.getName() != null) {
            subCategory.setName(request.getName());
        }
        super.updateEntityImage(id, request.getImage(), EntityType.SubCategory);
        setCategoryToSubCategory(request, subCategory);

        return subCategoryDAO.save(subCategory);
    }

    private void setCategoryToSubCategory(SubCategoryUpdateRequest request, SubCategory subCategory) {
        if (request.getCategoryId() != null && !request.getCategoryId().equals(subCategory.getCategory().getId())) {
            Category newCategory = categoryDAO.findById(request.getCategoryId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Категория не найдена"));
            subCategory.setCategory(newCategory);
        }
    }

    @Override
    @Caching(
            put = @CachePut(value = "app_subcategories", key = "#id"),
            evict = {
                    @CacheEvict(value = "app_categories", allEntries = true),
                    @CacheEvict(value = "app_subcategories", allEntries = true)
            }
    )
    @Transactional (propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SubCategory patchEntity(Long id, SubCategoryPatchRequest request) {
        SubCategory subCategory = subCategoryDAO.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Подкатегория не найдена"));
        if (request.getName() != null) {
            subCategory.setName(request.getName());
        }
        updateEntityImage(id, request.getImage(), EntityType.SubCategory);
        if (request.getCategoryId() != null && !Objects.equals(request.getCategoryId(), subCategory.getCategory().getId())) {
            Category newCategory = categoryDAO.findById(request.getCategoryId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Категория не найдена"));
            subCategory.setCategory(newCategory);
        }
        return subCategoryDAO.save(subCategory);
    }

    @Override
    public List<SubCategory> findSimilarByNameFilter(String name) {
        Specification<SubCategory> spec = subcategorySpecification.getBySimilarNameFilter(name);
        return subCategoryDAO.findAll(spec);
    }

    @Override
    @Transactional (propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Caching(
            evict = {
                    @CacheEvict(value = "app_subcategories", key = "#id"),
                    @CacheEvict(value = "app_categories", allEntries = true)
            }
    )
    public void deleteEntity(Long id) {
        SubCategory subCategory = subCategoryDAO.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Подкатегория не найдена"));
        transactionTemplate.execute(status -> {
            try {
                subCategoryDAO.deleteSubcategoryById(subCategory.getId());
                deleteImageRecord(id, EntityType.SubCategory);
                log.debug("Подкатегория успешно удалена. ID: {}", id);
                return null;
            }  catch (MinioException e) {
                log.error("MinIO oshibka during deletion for categoryId: {}", id, e);
                status.setRollbackOnly();
                throw new MinioOperationEx("Ошибка с минио: " + e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error during deletion for categoryId: {}", id, e);
                status.setRollbackOnly();
                throw new RuntimeException("500 во время удаления категории " + e.getMessage(), e);
            }
        });
    }
}
