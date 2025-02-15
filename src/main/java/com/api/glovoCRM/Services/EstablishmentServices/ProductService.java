package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.DAOs.EstablishmentDAO;
import com.api.glovoCRM.DAOs.ImageAssociationsDAO;
import com.api.glovoCRM.DAOs.ImageDAO;
import com.api.glovoCRM.DAOs.ProductDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Services.BaseService;
import com.api.glovoCRM.Models.EstablishmentModels.*;
import com.api.glovoCRM.Rest.Requests.ProductRequests.ProductWithDiscountCreateRequest;
import com.api.glovoCRM.Rest.Requests.ProductRequests.ProductWithDiscountPatchRequest;
import com.api.glovoCRM.Rest.Requests.ProductRequests.ProductWithDiscountUpdateRequest;
import com.api.glovoCRM.Utils.Minio.MinioService;
import com.api.glovoCRM.constants.EntityType;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductService extends BaseService<Product, ProductWithDiscountCreateRequest, ProductWithDiscountUpdateRequest, ProductWithDiscountPatchRequest> {

    private final ProductDAO productDAO;
    private final EstablishmentDAO establishmentDAO;

    @Autowired
    public ProductService(ImageDAO imageDAO, ImageAssociationsDAO imageAssociationsDAO, MinioService minioService,
                          ProductDAO productDAO, EstablishmentDAO establishmentDAO) {
        super(imageDAO, imageAssociationsDAO, minioService);
        this.productDAO = productDAO;
        this.establishmentDAO = establishmentDAO;
    }


    @Override
    public Product findById(Long id) {
        log.info("Находим продукт с id: {}", id);
        return productDAO.findById(id).orElseThrow(
                () -> new SuchResourceNotFoundEx("Такого продукта нет")
        );
    }

    @Override
    public List<Product> findAll() {
        log.info("Получаем все продукты");
        return productDAO.findAll();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Product createEntity(ProductWithDiscountCreateRequest request) {
        try {
            Establishment establishment = establishmentDAO.findById(request.getEstablishmentId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Заведение не найдено"));
            Product product = getProduct(request, establishment);

            Product savedProduct = productDAO.save(product);

            if (request.getImage() != null) {
                createImageRecord(request.getImage(), "products", EntityType.Product, savedProduct.getId());
            }

            return savedProduct;
        } catch (SuchResourceNotFoundEx ex) {
            log.warn("Ошибка при создании продукта: {}", ex.getMessage());
            throw ex;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при создании продукта: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать продукт", e);
        }
    }

    @NotNull
    private static Product getProduct(ProductWithDiscountCreateRequest request, Establishment establishment) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setActive(request.getActive() != null && request.getActive());
        product.setEstablishment(establishment);

        DiscountProduct discountProduct = new DiscountProduct();
        discountProduct.setDiscount(request.getDiscount());
        discountProduct.setActive(true);
        discountProduct.setProduct(product);

        product.setDiscountProduct(discountProduct);
        return product;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteEntity(Long entityId) {
        try {
            Product product = productDAO.findById(entityId)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Продукт не найден"));

            deleteImageRecord(entityId, EntityType.Product);
            
            productDAO.delete(product);
            log.info("Продукт успешно удален. ID: {}", entityId);
        } catch (SuchResourceNotFoundEx ex) {
            log.warn("Ошибка при удалении продукта: {}", ex.getMessage());
            throw ex;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при удалении продукта: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось удалить продукт", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Product updateEntity(Long entityId, ProductWithDiscountUpdateRequest request) {
        try {
            Product existingProduct = productDAO.findById(entityId)
                    .orElseThrow(() -> new SuchResourceNotFoundEx(String.format("Продукт с id %s не найден", entityId)));
            
            existingProduct.setName(request.getName());
            existingProduct.setDescription(request.getDescription());
            existingProduct.setPrice((request.getPrice()));
            existingProduct.setActive((request.getActive()));
            
            DiscountProduct discountProduct = existingProduct.getDiscountProduct();
            if (discountProduct == null) {
                discountProduct = new DiscountProduct();
                discountProduct.setProduct(existingProduct);
            }
            discountProduct.setDiscount(request.getDiscount());
            discountProduct.setActive(true);
            
            if (request.getImage() != null) {
                updateImageRecord(entityId, EntityType.Product, request.getImage());
            }

            return productDAO.save(existingProduct);
        } catch (SuchResourceNotFoundEx ex) {
            log.warn("Ошибка при обновлении продукта: {}", ex.getMessage());
            throw ex;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при обновлении продукта: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось обновить продукт", e);
        }
    }

    @Override
    public Product patchEntity(Long entityId, ProductWithDiscountPatchRequest request) {
        return null;
    }

    @Override
    public Product findByName(String name) {
        return productDAO.findByName(name).orElseThrow(
                () -> new SuchResourceNotFoundEx("Такого продукта нет по имени")
        );
    }
}
