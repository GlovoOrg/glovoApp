//package com.api.glovoCRM.Services.EstablishmentServices;
//
//import com.api.glovoCRM.DAOs.ImageAssociationsDAO;
//import com.api.glovoCRM.DAOs.ImageDAO;
//import com.api.glovoCRM.DAOs.SubCategoryDAO;
//import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
//import com.api.glovoCRM.Minio.MinioService;
//import com.api.glovoCRM.Models.EstablishmentModels.SubCategory;
//import com.api.glovoCRM.Services.BaseService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class SubCategoryService extends BaseService<SubCategory> {
//
//    private final SubCategoryDAO subCategoryDAO;
//
//    @Autowired
//    public SubCategoryService(SubCategoryDAO subCategoryDAO, ImageDAO imageDAO, ImageAssociationsDAO imageAssociationsDAO, MinioService minioService) {
//        super(imageDAO, imageAssociationsDAO, minioService);
//        this.subCategoryDAO = subCategoryDAO;
//    }
//
//    @Override
//    public SubCategory findById(Long id) {
//        return subCategoryDAO.findById(id).orElseThrow(()-> new SuchResourceNotFoundEx("Такой подкатегории нет по данному id"));
//    }
//
//    @Override
//    public List<SubCategory> findAll() {
//        return subCategoryDAO.findAll();
//    }
//
//    @Override
//    public SubCategory createEntity(Object request) {
//        return null;
//    }
//
//    @Override
//    public void deleteEntity(Long entityId) {
//
//    }
//
//    @Override
//    public SubCategory updateEntity(Long entityId, Object request) {
//        return null;
//    }
//
//    @Override
//    public SubCategory patchEntity(Long entityId, Object request) {
//        return null;
//    }
//
//}
