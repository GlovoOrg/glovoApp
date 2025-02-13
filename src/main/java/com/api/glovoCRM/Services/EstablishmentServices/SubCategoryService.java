package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.DAOs.CategoryDAO;
import com.api.glovoCRM.DAOs.ImageAssociationsDAO;
import com.api.glovoCRM.DAOs.ImageDAO;
import com.api.glovoCRM.DAOs.SubCategoryDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.EstablishmentModels.Category;
import com.api.glovoCRM.Models.EstablishmentModels.SubCategory;
import com.api.glovoCRM.Rest.Requests.SubCategoryRequests.SubCategoryCreateRequest;
import com.api.glovoCRM.Rest.Requests.SubCategoryRequests.SubCategoryPatchRequest;
import com.api.glovoCRM.Rest.Requests.SubCategoryRequests.SubCategoryUpdateRequest;
import com.api.glovoCRM.Services.BaseService;
import com.api.glovoCRM.Specifications.EstablimentSpecifications.SubcategorySpecification;
import com.api.glovoCRM.Utils.Minio.MinioService;
import com.api.glovoCRM.constants.EntityType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
@CacheConfig(cacheNames = BaseService.CACHE_PREFIX + "subcategories")
public class SubCategoryService extends BaseService<SubCategory, SubCategoryCreateRequest, SubCategoryUpdateRequest, SubCategoryPatchRequest> {

    private final SubCategoryDAO subCategoryDAO;
    private final CategoryDAO categoryDAO;
    private final SubcategorySpecification subcategorySpecification;

    @Autowired
    public SubCategoryService(SubCategoryDAO subCategoryDAO, CategoryDAO categoryDAO, ImageDAO imageDAO, ImageAssociationsDAO imageAssociationsDAO, MinioService minioService, SubcategorySpecification subcategorySpecification) {
        super(imageDAO, imageAssociationsDAO, minioService);
        this.subCategoryDAO = subCategoryDAO;
        this.categoryDAO = categoryDAO;
        this.subcategorySpecification = subcategorySpecification;
    }

    @Override
    //@Cacheable(key = "#id")
    public SubCategory findById(Long id) {
        log.info("Находим подкатегорию с id: {}", id);
        return subCategoryDAO.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Такой подкатегории нет"));
    }
    //@Cacheable
    @Override
    public List<SubCategory> findAll() {
        log.info("Получаем все подкатегории");
        return subCategoryDAO.findAllSubCategories();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    //@CacheEvict(allEntries = true)
    public SubCategory createEntity(SubCategoryCreateRequest request) {
        try {
            if (!categoryDAO.existsById(request.getCategoryId())) {
                throw new SuchResourceNotFoundEx("Такой подкатегории нет в системе");
            }
            if (subCategoryDAO.existsByName(request.getName())) {
                throw new AlreadyExistsEx("Такая подкатегория уже существует");
            }

            SubCategory subCategory = new SubCategory();
            subCategory.setName(request.getName());
            subCategory.setCategoryId(request.getCategoryId());

            SubCategory savedSubCategory = subCategoryDAO.save(subCategory);
            log.debug("Подкатегория создана. ID: {}", savedSubCategory.getId());

            if (request.getImage() != null) {
                createImageRecord(request.getImage(), "subcategories", EntityType.SubCategory, savedSubCategory.getId());
            }

            return savedSubCategory;
        } catch (AlreadyExistsEx ex) {
            log.warn("Ошибка при создании подкатегории AlreadyExistsEx: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Неожиданная ошибка при создании подкатегории: {}", ex.getMessage(), ex);
            throw new RuntimeException("Не удалось создать подкатегорию", ex);
        }
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    //@CacheEvict(allEntries = true)
    public SubCategory updateEntity(Long id, SubCategoryUpdateRequest request) {
        SubCategory subCategory = subCategoryDAO.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Подкатегория не найдена"));
        if (request.getName() != null) {
            subCategory.setName(request.getName());
        }
        if (request.getImage() != null) {
            updateImageRecord(id, EntityType.SubCategory, request.getImage());
        }
        if (request.getCategoryId() != null && !request.getCategoryId().equals(subCategory.getCategory().getId())) {
            Category newCategory = categoryDAO.findById(request.getCategoryId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Категория не найдена"));
            subCategory.setCategory(newCategory);
        }
        return subCategoryDAO.save(subCategory);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    //@CacheEvict(allEntries = true)
    public SubCategory patchEntity(Long id, SubCategoryPatchRequest request) {
        SubCategory subCategory = subCategoryDAO.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Подкатегория не найдена"));

        if (request.getName() != null) {
            subCategory.setName(request.getName());
        }

        if (request.getCategoryId() != null && !request.getCategoryId().equals(subCategory.getCategory().getId())) {
            Category newCategory = categoryDAO.findById(request.getCategoryId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Категория не найдена"));
            subCategory.setCategory(newCategory);
        }
        return subCategoryDAO.save(subCategory);
    }

    @Override
    //@Cacheable(key = "#name")
    public List<SubCategory> findSimilarByNameFilter(String name) {
        Specification<SubCategory> spec = subcategorySpecification.getBySimilarNameFilter(name);
        return subCategoryDAO.findAll(spec);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    //@CacheEvict( allEntries = true)
    public void deleteEntity(Long id) {
        try {
            SubCategory subCategory = subCategoryDAO.findById(id)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Подкатегория не найдена"));
            deleteImageRecord(id, EntityType.SubCategory);
            subCategoryDAO.delete(subCategory);
            log.debug("Подкатегория успешно удалена. ID: {}", id);
        } catch (SuchResourceNotFoundEx e) {
            log.warn("Ошибка при удалении подкатегории: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при удалении подкатегории: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось удалить подкатегорию", e);
        }
    }
}
