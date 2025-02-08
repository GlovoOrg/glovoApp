package com.api.glovoCRM.EntablishmrntServices;

import com.api.glovoCRM.DAOs.CategoryDAO;
import com.api.glovoCRM.DAOs.ImageAssociationsDAO;
import com.api.glovoCRM.DAOs.ImageDAO;
import com.api.glovoCRM.Models.EstablishmentModels.Category;
import com.api.glovoCRM.Rest.Requests.CategoryRequests.CategoryCreateRequest;
import com.api.glovoCRM.Services.EstablishmentServices.CategoryService;
import com.api.glovoCRM.Utils.Minio.MinioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryDAO categoryDAO;


    @Mock
    private ImageDAO imageDAO;

    @Mock
    private ImageAssociationsDAO imageAssociationsDAO;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void testConstructor() {
        // Проверяем, что зависимости успешно внедрены
        assertNotNull(categoryService, "CategoryService должен быть создан");
        assertNotNull(categoryDAO, "CategoryDAO должен быть внедрен");
        assertNotNull(imageDAO, "ImageDAO должен быть внедрен");
        assertNotNull(imageAssociationsDAO, "ImageAssociationsDAO должен быть внедрен");
        assertNotNull(minioService, "MinioService должен быть внедрен");
    }
    @Test
    void testCreateEntity_Success() {
            Category category = new Category();
            category.setName("Test Category");
        // Подготовка тестовых данных
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("Test Category");
        request.setImage(null); // Без изображения

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Test Category");


        // Мокируем поведение categoryDAO
        when(categoryDAO.existsByName("Test Category")).thenReturn(false);
        when(categoryDAO.save(any(Category.class))).thenReturn(savedCategory);

        // Вызов метода
        Category result = categoryService.createEntity(request);


        // Проверки
        assertNotNull(result);
        assertEquals("Test Category", result.getName());
        verify(categoryDAO, times(1)).existsByName("Test Category");
        verify(categoryDAO, times(1)).save(any(Category.class));
    }






}