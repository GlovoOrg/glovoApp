package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.DAOs.CategoryDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Utils.Minio.MinioService;
import com.api.glovoCRM.Models.EstablishmentModels.Category;
import com.api.glovoCRM.Rest.Requests.CategoryPatchRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryDAO categoryDAO;
    private final MinioService minioService;

    public CategoryService(CategoryDAO categoryDAO, MinioService minioService) {
        this.categoryDAO = categoryDAO;
        this.minioService = minioService;
    }
    @Transactional
    public Category createCategory(String name, MultipartFile image) {
        String objectId = "category-" + UUID.randomUUID() + ".img";
        String imageUrl = minioService.uploadFile(image, "categories", objectId);

        Category category = new Category();
        category.setName(name);
        category.setImage(imageUrl);
        return categoryDAO.save(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryDAO.findById(categoryId)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Category not found"));
        minioService.deleteFile("categories", extractObjectName(category.getImage()));
        categoryDAO.delete(category);
    }

    public Category getCategoryById(Long categoryId) {
        return categoryDAO.findById(categoryId)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Category not found"));
    }

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    @Transactional
    public Category updateCategory(Long categoryId, String name, MultipartFile image) {
        Category category = getCategoryById(categoryId);
        category.setName(name);

        if (image != null) {
            updateCategoryImage(category, image);
        }

        return categoryDAO.save(category);
    }

    @Transactional
    public Category patchCategory(Long categoryId, CategoryPatchRequest request) {
        Category category = getCategoryById(categoryId);

        if (request.getName() != null) {
            category.setName(request.getName());
        }

        if (request.getImage() != null) {
            updateCategoryImage(category, request.getImage());
        }

        return categoryDAO.save(category);
    }

    private void updateCategoryImage(Category category, MultipartFile newImage) {
        minioService.deleteFile("categories", extractObjectName(category.getImage()));
        String newObjectId = "category-" + UUID.randomUUID() + ".img";
        String newImageUrl = minioService.uploadFile(newImage, "categories", newObjectId);
        category.setImage(newImageUrl);
    }

    private String extractObjectName(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
}
