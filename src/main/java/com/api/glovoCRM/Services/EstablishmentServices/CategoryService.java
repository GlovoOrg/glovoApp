package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.DAOs.CategoryDAO;
import com.api.glovoCRM.DAOs.ImageAssociationsDAO;
import com.api.glovoCRM.DAOs.ImageDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Utils.Minio.MinioService;
import com.api.glovoCRM.Models.EstablishmentModels.Category;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryCreateRequest;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryPatchRequest;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryUpdateRequest;
import com.api.glovoCRM.Services.BaseService;
import com.api.glovoCRM.constants.EntityType;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Slf4j
@Service
public class CategoryService extends BaseService<Category, CategoryCreateRequest, CategoryUpdateRequest, CategoryPatchRequest> {
    private final CategoryDAO categoryDAO;

    @Autowired
    public CategoryService(CategoryDAO categoryDAO, ImageDAO imageDAO, ImageAssociationsDAO imageAssociationsDAO, MinioService minioService) {
        super(imageDAO, imageAssociationsDAO, minioService);
        this.categoryDAO = categoryDAO;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public Category createEntity(CategoryCreateRequest request) {
        try {
            if (categoryDAO.existsByName(request.getName())) {
                throw new AlreadyExistsEx("Такая категория уже существует");
            }
            Category category = new Category();
            category.setName(request.getName());
            Category savedCategory = categoryDAO.save(category);
            log.debug("Категория успешно создана. ID: {}", savedCategory.getId());
            if (request.getImage() != null) {
                createImageRecord(request.getImage(), "categories", EntityType.Category, savedCategory.getId());
            }
            return savedCategory;
        } catch (AlreadyExistsEx e) {
            log.warn("Ошибка при создании категории: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при создании категории: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать категорию", e);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void deleteEntity(Long categoryId) {
        try {
            deleteImageRecord(categoryId, EntityType.Category);
            categoryDAO.deleteById(categoryId);
            log.debug("Категория успешно удалена. ID: {}", categoryId);
        } catch (SuchResourceNotFoundEx e) {
            log.warn("Ошибка при удалении категории: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при удалении категории: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось удалить категорию", e);
        }
    }

    @Transactional
    @Override
    public Category updateEntity(Long categoryId, CategoryUpdateRequest request) {
        try {
            Category category = categoryDAO.findById(categoryId)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Такой категории нет"));
            category.setName(request.getName());
            if (request.getImage() != null) {
                updateImageRecord(categoryId, EntityType.Category, request.getImage());
            }
            return categoryDAO.save(category);
        } catch (SuchResourceNotFoundEx e) {
            log.warn("Ошибка при обновлении категории: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при обновлении категории: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось обновить категорию", e);
        }
    }

    @Override
    public Category findById(Long id) {
        return categoryDAO.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Категория не найдена"));
    }

    @Override
    public List<Category> findAll() {
        return categoryDAO.findAll();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public Category patchEntity(Long id, CategoryPatchRequest request) {
        Category category = categoryDAO.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Такой категории нет"));
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getImage() != null) {
            updateImageRecord(id, EntityType.Category, request.getImage());
        }
        return categoryDAO.save(category);
    }
}
