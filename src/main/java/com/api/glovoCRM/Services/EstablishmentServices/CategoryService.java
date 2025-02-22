package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.DAOs.CategoryDAO;
import com.api.glovoCRM.DAOs.ImageAssociationsDAO;
import com.api.glovoCRM.DAOs.ImageDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.EstablishmentModels.SubCategory;
import com.api.glovoCRM.Specifications.BaseSpecification;
import com.api.glovoCRM.Specifications.EstablimentSpecifications.CategorySpecification;
import com.api.glovoCRM.Utils.Minio.MinioService;
import com.api.glovoCRM.Models.EstablishmentModels.Category;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryCreateRequest;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryPatchRequest;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryUpdateRequest;
import com.api.glovoCRM.Services.BaseService;
import com.api.glovoCRM.constants.EntityType;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Slf4j
@Service
@CacheConfig(cacheNames = "categories")
public class CategoryService extends BaseService<Category, CategoryCreateRequest, CategoryUpdateRequest, CategoryPatchRequest> {
    private final CategoryDAO categoryDAO;
    private final CategorySpecification categorySpecification;
    private final CacheManager cacheManager;

    @Autowired
    public CategoryService(CategoryDAO categoryDAO, ImageDAO imageDAO, ImageAssociationsDAO imageAssociationsDAO, MinioService minioService, CategorySpecification categorySpecification, @Qualifier ("cacheManager") CacheManager cacheManager) {
        super(imageDAO, imageAssociationsDAO, minioService);
        this.categoryDAO = categoryDAO;
        this.categorySpecification = categorySpecification;
        this.cacheManager = cacheManager;
    }
    public Category findByIdWithSubCategories(Long categoryId) {
        return categoryDAO.findById(categoryId)
                .map(category -> {
                    // Принудительно инициализируем подкатегории
                    Hibernate.initialize(category.getSubCategories());
                    category.getSubCategories().forEach(subCategory -> {
                        Hibernate.initialize(subCategory.getEstablishments());
                    });
                    return category;
                })
                .orElseThrow(() -> new SuchResourceNotFoundEx("Категория не найдена"));
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    public Category createEntity(CategoryCreateRequest request) {

        if (categoryDAO.existsByName(request.getName())) {
            throw new AlreadyExistsEx("Такая категория уже существует");
        }

        Category category = new Category();
        category.setName(request.getName());
        Category savedCategory = categoryDAO.save(category);
        try {
            if (request.getImage() != null) {
                super.createImageRecord(request.getImage(), "categories", EntityType.Category, savedCategory.getId());
            }
            return savedCategory;
        } catch (Exception e) {
            categoryDAO.delete(savedCategory);
            throw new RuntimeException("Не удалось создать категорию", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public void deleteEntity(Long categoryId) {
        Category category = findByIdWithSubCategories(categoryId);
        Hibernate.initialize(category.getSubCategories());
        category.getSubCategories().forEach(subCategory -> subCategory.setCategory(null));
        category.getSubCategories().clear();

        categoryDAO.save(category);

        super.deleteImageRecord(categoryId, EntityType.Category);

        categoryDAO.delete(category);

        cacheManager.getCache("categories").evict(categoryId);
        cacheManager.getCache("subcategories").clear();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public Category updateEntity(Long categoryId, CategoryUpdateRequest request) {
        Category category = categoryDAO.findById(categoryId)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Категория не найдена"));

        category.setName(request.getName());

        if (request.getImage() != null) {
            super.updateImageRecord(categoryId, EntityType.Category, request.getImage());
        }

        return categoryDAO.save(category);
    }

    @Override
    @Cacheable(key = "#id")
    public Category findById(Long id) {
        return categoryDAO.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Категория не найдена"));
    }

    @Override
    @Cacheable
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryDAO.findAllCategories();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public Category patchEntity(Long id, CategoryPatchRequest request) {
        Category category = categoryDAO.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Такой категории нет"));
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getImage() != null) {
            super.updateImageRecord(id, EntityType.Category, request.getImage());
        }
        return categoryDAO.save(category);
    }

    @Override
    @Cacheable(key = "#name")
    public List<Category> findSimilarByNameFilter(String name) {
        Specification<Category> spec = categorySpecification.getBySimilarNameFilter(name);
        return categoryDAO.findAll(spec);
    }
}
