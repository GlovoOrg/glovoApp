package com.api.glovoCRM;

import com.api.glovoCRM.Controllers.Establishment.CategoryController;
import com.api.glovoCRM.Models.EstablishmentModels.Category;
import com.api.glovoCRM.Services.EstablishmentServices.CategoryService;
import com.api.glovoCRM.mappers.CategoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {


    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private CategoryController categoryController;
    private MockMvc mockMvc;
    @BeforeEach
    void SetUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }
    @Test
    void AllCategories() throws Exception{

        Mockito.when(categoryService.getCategoryById(1l)).thenReturn(new Category());
        mockMvc.perform(get("/api/v1/categories/{id}", 1L)).andExpect(status().isOk());
    }

    @Test
    void AllCategories1() throws Exception{

        Mockito.when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/api/v1/categories")).andExpect(status().isOk());
    }
}
