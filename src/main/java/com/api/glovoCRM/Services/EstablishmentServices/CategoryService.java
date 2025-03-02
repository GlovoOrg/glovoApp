package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.DAOs.CategoryDAO;
import com.api.glovoCRM.DAOs.ImageAssociationsDAO;
import com.api.glovoCRM.DAOs.ImageDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.FileUploadEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.MinioOperationEx;
import com.api.glovoCRM.Services.ImageCacheService;
import com.api.glovoCRM.Specifications.EstablimentSpecifications.CategorySpecification;
import com.api.glovoCRM.Utils.Minio.MinioService;
import com.api.glovoCRM.Models.EstablishmentModels.Category;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryCreateRequest;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryPatchRequest;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryUpdateRequest;
import com.api.glovoCRM.Services.BaseService;
import com.api.glovoCRM.constants.EntityType;
import io.minio.errors.MinioException;
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


@Slf4j
@Service
@CacheConfig (cacheNames = "app_categories")
public class CategoryService extends BaseService<Category, CategoryCreateRequest, CategoryUpdateRequest, CategoryPatchRequest> {
    private final CategoryDAO categoryDAO;
    private final CategorySpecification categorySpecification;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public CategoryService(ImageCacheService imageCacheService, TransactionTemplate transactionTemplate, CategoryDAO categoryDAO, ImageDAO imageDAO, ImageAssociationsDAO imageAssociationsDAO, MinioService minioService, CategorySpecification categorySpecification) {
        super(imageCacheService, imageDAO, imageAssociationsDAO, minioService);
        this.categoryDAO = categoryDAO;
        this.categorySpecification = categorySpecification;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    @Caching(
            put = @CachePut(value = "app_categories", key = "#result.id"),
            evict = {
                    @CacheEvict(value = "app_subcategories", allEntries = true)
            }
    )
    @Transactional (propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Category createEntity(CategoryCreateRequest request){
        if (categoryDAO.existsByName(request.getName())) {
            throw new AlreadyExistsEx("Такая категория уже существует");
        }
        Category category = new Category();
        category.setName(request.getName());
        Category savedCategory = categoryDAO.save(category);

        try {
            super.createImageRecord(request.getImage(), "categories", EntityType.Category, savedCategory.getId());
            return savedCategory;
        } catch (FileUploadEx e) {
            log.error("Ошибка при сохранении изображения для категории {}: {}", savedCategory.getId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при создании категории {}: {}", savedCategory.getId(), e.getMessage());
            throw new RuntimeException("Не удалось создать категорию", e);
        }
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "app_categories", key = "#categoryId"),
                    @CacheEvict(value = "app_subcategories", allEntries = true)
            }
    )
    public void deleteEntity(Long categoryId) {
        if (!categoryDAO.existsById(categoryId)) {
            throw new SuchResourceNotFoundEx("Данной категории нет в системе");
        }
        transactionTemplate.execute(status -> {
            try {
                categoryDAO.deleteById(categoryId);
                super.deleteImageRecord(categoryId, EntityType.Category);
                return null;
            } catch (MinioException e) {
                log.error("MinIO oshibka during deletion for categoryId: {}", categoryId, e);
                status.setRollbackOnly();
                throw new MinioOperationEx("Ошибка с минио: " + e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error during deletion for categoryId: {}", categoryId, e);
                status.setRollbackOnly();
                throw new RuntimeException("500 во время удаления категории " + e.getMessage(), e);
            }
        });
    }

    @Caching(
            put = @CachePut(value = "app_categories", key = "#categoryId"),
            evict = {
                    @CacheEvict(value = "app_subcategories", allEntries = true)
            }
    )
    @Transactional (propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Category updateEntity(Long categoryId, CategoryUpdateRequest request) {
        Category category = getEntityById(categoryId, categoryDAO);
        category.setName(request.getName());
        super.updateEntityImage(categoryId, request.getImage(), EntityType.Category);
        return categoryDAO.save(category);
    }

    @Transactional (isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Cacheable(value = "app_categories", key = "#id")
    @Override
    public Category findById(Long id) {
        return getEntityById(id, categoryDAO);
    }
    @Override
    @Cacheable(value = "app_categories", key = "'all_categories'")
    @Transactional (isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Category> findAll() {
        return categoryDAO.findAllCategories();
    }

    @Caching(
            put = @CachePut(value = "app_categories", key = "#id"),
            evict = {
                    @CacheEvict(value = "app_subcategories", allEntries = true)
            }
    )
    @Transactional (propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Category patchEntity(Long id, CategoryPatchRequest request) {
        Category category = getEntityById(id, categoryDAO);
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        super.updateEntityImage(id, request.getImage(), EntityType.Category);
        return categoryDAO.save(category);
    }
    public List<Category> getCategoriesWithSubcategoriesOnly() {
        return categoryDAO.findCategoriesWithSubcategoriesOnly();
    }

    public List<Category> getCategoriesWithSubcategoriesAndEstablishmentsOnly() {
        List<Category> categories = categoryDAO.findCategoriesWithSubcategoriesOnly();
        categories.forEach(category -> category.getSubCategories().forEach(
                subCategory -> subCategory.getEstablishments().size()
        ));
        return categories;
    }


    @Cacheable(key = "#name", value = "app_categories")
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Override
    public List<Category> findSimilarByNameFilter(String name) {
        Specification<Category> spec = categorySpecification.getBySimilarNameFilter(name);
        return categoryDAO.findAll(spec);
    }
}
