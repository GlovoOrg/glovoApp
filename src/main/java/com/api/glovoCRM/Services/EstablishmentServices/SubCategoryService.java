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
import com.api.glovoCRM.Utils.Minio.MinioService;
import com.api.glovoCRM.constants.EntityType;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class SubCategoryService extends BaseService<SubCategory, SubCategoryCreateRequest, SubCategoryUpdateRequest, SubCategoryPatchRequest> {

    private final SubCategoryDAO subCategoryDAO;
    private final CategoryDAO categoryDAO;

    @Autowired
    public SubCategoryService(SubCategoryDAO subCategoryDAO, CategoryDAO categoryDAO, ImageDAO imageDAO, ImageAssociationsDAO imageAssociationsDAO, MinioService minioService) {
        super(imageDAO, imageAssociationsDAO, minioService);
        this.subCategoryDAO = subCategoryDAO;
        this.categoryDAO = categoryDAO;
    }

    @Override
    public SubCategory findById(Long id) {
        log.info("Находим подкатегорию с id: {}", id);
        return subCategoryDAO.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Такой подкатегории нет"));
    }

    @Override
    public List<SubCategory> findAll() {
        log.info("Получаем все подкатегории");
        return subCategoryDAO.findAll();
    }

    @Override
    @Transactional
    public SubCategory createEntity(SubCategoryCreateRequest request) {
        try {
            if (subCategoryDAO.existsByName(request.getName())) {
                throw new AlreadyExistsEx("Такая подкатегория уже существует");
            }

            Category category = categoryDAO.findById(request.getCategoryId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Такой Категории не существует"));

            SubCategory subCategory = new SubCategory();
            subCategory.setName(request.getName());
            subCategory.setCategory(category);

            SubCategory savedSubCategory = subCategoryDAO.save(subCategory);
            log.debug("Подкатегория создана. ID: {}", savedSubCategory.getId());

            if (request.getImage() != null) {
                createImageRecord(request.getImage(), "subcategories", EntityType.SubCategory, savedSubCategory.getId());
            }

            return savedSubCategory;
        } catch (AlreadyExistsEx ex) {
            log.warn("Ошибка при создании подкатегории: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Неожиданная ошибка при создании подкатегории: {}", ex.getMessage(), ex);
            throw new RuntimeException("Не удалось создать подкатегорию", ex);
        }
    }

    @Override
    @Transactional
    public SubCategory updateEntity(Long id, SubCategoryUpdateRequest request) {
        SubCategory subCategory = subCategoryDAO.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Подкатегория не найдена"));

        if (request.getName() != null) {
            subCategory.setName(request.getName());
        }

        if (request.getImage() != null) {
            updateImageRecord(id, EntityType.SubCategory, request.getImage());
        }

        return subCategoryDAO.save(subCategory);
    }

    @Override
    @Transactional
    public SubCategory patchEntity(Long id, SubCategoryPatchRequest request) {
        SubCategory subCategory = subCategoryDAO.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Подкатегория не найдена"));

        if (request.getName() != null) {
            subCategory.setName(request.getName());
        }

        if (request.getImage() != null) {
            updateImageRecord(id, EntityType.SubCategory, request.getImage());
        }

        return subCategoryDAO.save(subCategory);
    }

    @Override
    public SubCategory findByName(String name) {
        log.info("Получение подкатегории для: {}", name);
        return subCategoryDAO.findByName(name).orElseThrow( () -> new SuchResourceNotFoundEx("Такой подкатегории нет"));
    }

    @Override
    @Transactional
    public void deleteEntity(Long id) {
        try {
            deleteImageRecord(id, EntityType.SubCategory);
            subCategoryDAO.deleteById(id);
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
