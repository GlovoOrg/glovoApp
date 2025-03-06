package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.Repositories.CategoryRepository;
import com.api.glovoCRM.Repositories.ImageAssociationsRepository;
import com.api.glovoCRM.Repositories.ImageRepository;
import com.api.glovoCRM.Repositories.SubCategoryRepository;
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
import org.springframework.security.access.prepost.PreAuthorize;
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

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final SubcategorySpecification subcategorySpecification;
    private final CacheManager cacheManager;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public SubCategoryService(SubCategoryRepository subCategoryRepository, CategoryRepository categoryRepository, ImageRepository imageRepository, ImageAssociationsRepository imageAssociationsRepository, MinioService minioService, SubcategorySpecification subcategorySpecification, @Qualifier ("cacheManager") CacheManager cacheManager, ImageCacheService imageCacheService, TransactionTemplate transactionTemplate) {
        super(imageCacheService, imageRepository, imageAssociationsRepository, minioService);
        this.subCategoryRepository = subCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.subcategorySpecification = subcategorySpecification;
        this.cacheManager = cacheManager;
        this.transactionTemplate = transactionTemplate;
    }

    @Transactional (readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Cacheable(value = "app_subcategories", key = "#id")
    @Override
    public SubCategory findById(Long id) {
        return getEntityById(id, subCategoryRepository);
    }

    @Transactional (readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Cacheable(value = "app_subcategories", key = "'all_subcategories'")
    @Override
    public List<SubCategory> findAll() {
        return subCategoryRepository.findAllSubCategories();
    }

    @PreAuthorize ("hasAnyRole('ROLE_ADMIN', 'ROLE_ESTABLISHMENT')")
    @Override
    @Caching(
            put = @CachePut(value = "app_subcategories", key = "#result.id"),
            evict = {
                    @CacheEvict(value = "app_categories", allEntries = true)
            }
    )
    @Transactional (propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SubCategory createEntity(SubCategoryCreateRequest request) {
        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new SuchResourceNotFoundEx("Такой категории нет в системе");
        }
        if (subCategoryRepository.existsByName(request.getName())) {
            throw new AlreadyExistsEx("Такая подкатегория уже существует");
        }
        SubCategory subCategory = new SubCategory();
        subCategory.setName(request.getName());
        subCategory.setCategoryId(request.getCategoryId());
        SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
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
    @PreAuthorize ("hasAnyRole('ROLE_ADMIN', 'ROLE_ESTABLISHMENT')")
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
        SubCategory subCategory = getEntityById(id, subCategoryRepository);
        if (request.getName() != null) {
            subCategory.setName(request.getName());
        }
        super.updateEntityImage(id, request.getImage(), EntityType.SubCategory);
        setCategoryToSubCategory(request, subCategory);

        return subCategoryRepository.save(subCategory);
    }

    private void setCategoryToSubCategory(SubCategoryUpdateRequest request, SubCategory subCategory) {
        if (request.getCategoryId() != null && !request.getCategoryId().equals(subCategory.getCategory().getId())) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Категория не найдена"));
            subCategory.setCategory(newCategory);
        }
    }

    @PreAuthorize ("hasAnyRole('ROLE_ADMIN', 'ROLE_ESTABLISHMENT')")
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
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Подкатегория не найдена"));
        if (request.getName() != null) {
            subCategory.setName(request.getName());
        }
        updateEntityImage(id, request.getImage(), EntityType.SubCategory);
        if (request.getCategoryId() != null && !Objects.equals(request.getCategoryId(), subCategory.getCategory().getId())) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Категория не найдена"));
            subCategory.setCategory(newCategory);
        }
        return subCategoryRepository.save(subCategory);
    }

    @Override
    public List<SubCategory> findSimilarByNameFilter(String name) {
        Specification<SubCategory> spec = subcategorySpecification.getBySimilarNameFilter(name);
        return subCategoryRepository.findAll(spec);
    }
    @PreAuthorize ("hasAnyRole('ROLE_ADMIN', 'ROLE_ESTABLISHMENT')")
    @Override
    @Transactional (propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Caching(
            evict = {
                    @CacheEvict(value = "app_subcategories", key = "#id"),
                    @CacheEvict(value = "app_categories", allEntries = true)
            }
    )
    public void deleteEntity(Long id) {
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Подкатегория не найдена"));
        transactionTemplate.execute(status -> {
            try {
                subCategoryRepository.deleteSubcategoryById(subCategory.getId());
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
