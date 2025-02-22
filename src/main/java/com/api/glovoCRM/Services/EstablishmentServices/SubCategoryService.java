package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.DAOs.CategoryDAO;
import com.api.glovoCRM.DAOs.ImageAssociationsDAO;
import com.api.glovoCRM.DAOs.ImageDAO;
import com.api.glovoCRM.DAOs.SubCategoryDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.FileDeleteEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.FileUploadEx;
import com.api.glovoCRM.Models.EstablishmentModels.Category;
import com.api.glovoCRM.Models.EstablishmentModels.SubCategory;
import com.api.glovoCRM.Rest.Requests.SubCategoryRequests.SubCategoryCreateRequest;
import com.api.glovoCRM.Rest.Requests.SubCategoryRequests.SubCategoryPatchRequest;
import com.api.glovoCRM.Rest.Requests.SubCategoryRequests.SubCategoryUpdateRequest;
import com.api.glovoCRM.Services.BaseService;
import com.api.glovoCRM.Specifications.EstablimentSpecifications.SubcategorySpecification;
import com.api.glovoCRM.Utils.Minio.MinioService;
import com.api.glovoCRM.constants.EntityType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@CacheConfig(cacheNames ="subcategories")
public class SubCategoryService extends BaseService<SubCategory, SubCategoryCreateRequest, SubCategoryUpdateRequest, SubCategoryPatchRequest> {

    private final SubCategoryDAO subCategoryDAO;
    private final CategoryDAO categoryDAO;
    private final SubcategorySpecification subcategorySpecification;
    private final CacheManager cacheManager;

    @Autowired
    public SubCategoryService(SubCategoryDAO subCategoryDAO, CategoryDAO categoryDAO, ImageDAO imageDAO, ImageAssociationsDAO imageAssociationsDAO, MinioService minioService, SubcategorySpecification subcategorySpecification, @Qualifier ("cacheManager") CacheManager cacheManager) {
        super(imageDAO, imageAssociationsDAO, minioService);
        this.subCategoryDAO = subCategoryDAO;
        this.categoryDAO = categoryDAO;
        this.subcategorySpecification = subcategorySpecification;
        this.cacheManager = cacheManager;
    }
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Cacheable(key = "#id")
    @Override
    public SubCategory findById(Long id) {
        log.info("Находим подкатегорию с id: {}", id);
        return subCategoryDAO.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Такой подкатегории нет"));
    }
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Cacheable
    @Override
    public List<SubCategory> findAll() {
        log.info("Получаем все подкатегории");
        return subCategoryDAO.findAllSubCategories();
    }

    @Override
    @CacheEvict(allEntries = true, cacheNames = {"subcategories", "categories"})
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SubCategory createEntity(SubCategoryCreateRequest request) {
        validateRequest(request);
        SubCategory subCategory = new SubCategory();
        subCategory.setName(request.getName());
        subCategory.setCategory(categoryDAO.getCategoryById(request.getCategoryId()).orElseThrow(
                () -> new AlreadyExistsEx("такой категории уже существует")
        ));
        log.debug("Подкатегория создана. ID: {}", subCategory.getId());
        return saveAndEvictCache(subCategory);
    }
    private void validateRequest(SubCategoryCreateRequest request) {
        if (!categoryDAO.existsById(request.getCategoryId())) {
            throw new SuchResourceNotFoundEx("Такой категории нет в системе");
        }
        if (subCategoryDAO.existsByName(request.getName())) {
            throw new AlreadyExistsEx("Такая подкатегория уже существует");
        }
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvict(allEntries = true, cacheNames = {"subcategories", "categories"})
    public SubCategory updateEntity(Long id, SubCategoryUpdateRequest request) {
        SubCategory subCategory = subCategoryDAO.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Подкатегория не найдена"));
        if (request.getName() != null) {
            subCategory.setName(request.getName());
        }
        setCategoryToSubCategory(request, subCategory);
        updateImageIfPresent(id, request.getImage());
        return saveAndEvictCache(subCategory);
    }
    private <R extends SubCategoryUpdateRequest> void setCategoryToSubCategory(R request, SubCategory subCategory) {
        if (request.getCategoryId() != null &&
                !request.getCategoryId().equals(subCategory.getCategory().getId())) {

            Category newCategory = categoryDAO.findById(request.getCategoryId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Категория не найдена"));

            subCategory.setCategory(newCategory);
            log.debug("Подкатегории назначена новая категория. ID новой категории: {}", newCategory.getId());
        }
    }
    private SubCategory saveAndEvictCache(SubCategory subCategory) {
        SubCategory savedSubCategory = subCategoryDAO.save(subCategory);

        Category category = savedSubCategory.getCategory();
        if (cacheManager.getCache("categories") != null) {
            Objects.requireNonNull(cacheManager.getCache("categories")).evict(category.getId());
            log.debug("Кэш очищен для категории ID: {}", category.getId());
        }

        return savedSubCategory;
    }
    private void updateImageIfPresent(Long id, MultipartFile image) {
        if (image != null) {
            try {
                super.updateImageRecord(id, EntityType.SubCategory, image);
                log.debug("Изображение обновлено для подкатегории ID: {}", id);
            } catch (Exception ex) {
                log.error("Ошибка при обновлении изображения в Minio: {}", ex.getMessage(), ex);
                throw new FileUploadEx("Ошибка в Minio при обновлении изображения");
            }
        }
    }


    @Override
    @CacheEvict(allEntries = true, cacheNames = {"subcategories", "categories"})
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SubCategory patchEntity(Long id, SubCategoryPatchRequest request) {
        SubCategory subCategory = subCategoryDAO.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Подкатегория не найдена"));

        if (request.getName() != null) {
            subCategory.setName(request.getName());
        }

        if (request.getCategoryId() != null &&
                !request.getCategoryId().equals(subCategory.getCategory().getId())) {

            Category newCategory = categoryDAO.findById(request.getCategoryId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Категория не найдена"));

            subCategory.setCategory(newCategory);
            log.info("Подкатегории назначена новая категория. ID новой категории: {}", newCategory.getId());
        }
        updateImageIfPresent(id, request.getImage());
        return saveAndEvictCache(subCategory);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Override
    public List<SubCategory> findSimilarByNameFilter(String name) {
        Specification<SubCategory> spec = subcategorySpecification.getBySimilarNameFilter(name);
        return subCategoryDAO.findAll(spec);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvict(allEntries = true, cacheNames = {"subcategories", "categories"})
    public synchronized void deleteEntity(Long id) {
            SubCategory subCategory = subCategoryDAO.findById(id)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Подкатегория не найдена"));
            subCategoryDAO.delete(subCategory);
            log.debug("Подкатегория успешно удалена. ID: {}", id);
        try{
            super.deleteImageRecord(id, EntityType.SubCategory);
        } catch (SuchResourceNotFoundEx e) {
            log.warn("Ошибка при удалении подкатегории: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("ошибка при удалении подкатегории: {}", e.getMessage(), e);
            try {
                subCategoryDAO.save(subCategory);
                log.warn("Подкатегория восстановлена из-за ошибки при удалении изображения. Минио дерьмо, ID: {}", id);
                Objects.requireNonNull(cacheManager.getCache("subcategories")).put(id, subCategory);
            } catch (Exception restoreEx) {
                log.error("Ошибка при восстановлении подкатегории: {}", restoreEx.getMessage(), restoreEx);
                throw new RuntimeException("Фатал, Малик, зря ты за java взялся! Подкатегория не может быть восстановлена.", restoreEx);
            }
            throw new FileDeleteEx("Не удалось удалить подкатегорию, изображение не удалено.");
        }
    }
}
