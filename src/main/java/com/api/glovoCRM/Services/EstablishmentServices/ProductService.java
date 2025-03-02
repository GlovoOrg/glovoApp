package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.DAOs.*;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.FileUploadEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.MinioOperationEx;
import com.api.glovoCRM.Services.BaseService;
import com.api.glovoCRM.Models.EstablishmentModels.*;
import com.api.glovoCRM.Rest.Requests.ProductRequests.ProductWithDiscountCreateRequest;
import com.api.glovoCRM.Rest.Requests.ProductRequests.ProductWithDiscountPatchRequest;
import com.api.glovoCRM.Rest.Requests.ProductRequests.ProductWithDiscountUpdateRequest;
import com.api.glovoCRM.Services.ImageCacheService;
import com.api.glovoCRM.Specifications.EstablimentSpecifications.ProductSpecification;
import com.api.glovoCRM.Utils.Minio.MinioService;
import com.api.glovoCRM.constants.EntityType;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Slf4j
@Service
@CacheConfig (cacheNames = "app_products")
public class ProductService extends BaseService<Product, ProductWithDiscountCreateRequest, ProductWithDiscountUpdateRequest, ProductWithDiscountPatchRequest> {

    private final ProductDAO productDAO;
    private final EstablishmentDAO establishmentDAO;
    private final ProductSpecification productSpecification;
    private final EstablishmentFilterDAO establishmentFilterDAO;
    private final TransactionTemplate transactionTemplate;


    @Autowired
    public ProductService(ImageDAO imageDAO, ImageAssociationsDAO imageAssociationsDAO, MinioService minioService,
                          ProductDAO productDAO, EstablishmentDAO establishmentDAO, ProductSpecification productSpecification, ImageCacheService imageCacheService, EstablishmentFilterDAO establishmentFilterDAO, TransactionTemplate transactionTemplate) {
        super(imageCacheService, imageDAO, imageAssociationsDAO, minioService);
        this.productDAO = productDAO;
        this.establishmentDAO = establishmentDAO;
        this.productSpecification = productSpecification;
        this.establishmentFilterDAO = establishmentFilterDAO;
        this.transactionTemplate = transactionTemplate;
    }

    @Cacheable (value = "app_products", key = "#id")
    @Transactional (readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Override
    public Product findById(Long id) {
        log.info("Находим продукт с id: {}", id);
        return productDAO.findById(id).orElseThrow(
                () -> new SuchResourceNotFoundEx("Такого продукта нет")
        );
    }

    @Cacheable ()
    @Transactional (readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Override
    public List<Product> findAll() {
        log.info("Получаем все продукты");
        return productDAO.findAll();
    }

    @Caching (
            put = @CachePut (value = "app_products", key = "#result.id"),
            evict = {
                    @CacheEvict (value = "app_categories", allEntries = true),
                    @CacheEvict (value = "app_subcategories", allEntries = true),
                    @CacheEvict (value = "app_establishments", allEntries = true),
                    @CacheEvict (value = "app_filters", allEntries = true)
            }
    )
    @Override
    @Transactional (propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Product createEntity(ProductWithDiscountCreateRequest request) {
        Establishment establishment = establishmentDAO.findById(request.getEstablishmentId())
                .orElseThrow(() -> new SuchResourceNotFoundEx("Заведение не найдено"));
        EstablishmentFilter establishmentFilter = establishmentFilterDAO.findById(request.getEstablishmentId())
                .orElseThrow(() -> new SuchResourceNotFoundEx("Заведение фильтр не найдено"));
        Product product = getProduct(request, establishment, establishmentFilter);

        Product savedProduct = productDAO.save(product);
        try {
            createImageRecord(request.getImage(), "products", EntityType.Product, savedProduct.getId());
            return savedProduct;
        } catch (FileUploadEx e) {
            log.error("Ошибка при сохранении изображения в MinIO для продукта {}: {}", savedProduct.getId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при создании заведения {}: {}", savedProduct.getId(), e.getMessage());
            throw new RuntimeException("Не удалось создать заведения", e);
        }
    }

    private static Product getProduct(ProductWithDiscountCreateRequest request, Establishment establishment, EstablishmentFilter establishmentFilter) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setActive(request.getActive() != null && request.getActive());
        product.setEstablishment(establishment);
        product.setEstablishmentFilter(establishmentFilter);

        DiscountProduct discountProduct = new DiscountProduct();
        discountProduct.setDiscount(request.getDiscount());
        discountProduct.setActive(true);
        discountProduct.setProduct(product);

        product.setDiscountProduct(discountProduct);
        return product;
    }

    @Caching (
            evict = {
                    @CacheEvict (value = "app_products", key = "#entityId"),
                    @CacheEvict (value = "app_categories", allEntries = true),
                    @CacheEvict (value = "app_subcategories", allEntries = true),
                    @CacheEvict (value = "app_establishments", allEntries = true),
                    @CacheEvict (value = "app_filters", allEntries = true)
            }
    )
    @Override
    public void deleteEntity(Long entityId) {
        Product product = productDAO.findById(entityId)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Продукт не найден"));

        transactionTemplate.execute(status -> {
            try {
                productDAO.deleteByProductId(product.getId());
                deleteImageRecord(entityId, EntityType.Product);
                log.debug("Продукт успешно удалена. ID: {}", entityId);
                return null;
            } catch (MinioException e) {
                log.error("MinIO oshibka during deletion for productId: {}", entityId, e);
                status.setRollbackOnly();
                throw new MinioOperationEx("Ошибка с минио: " + e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error during deletion for productId: {}", entityId, e);
                status.setRollbackOnly();
                throw new RuntimeException("500 во время удаления product " + e.getMessage(), e);
            }
        });

    }

    @Caching (
            put = @CachePut (value = "app_products", key = "#entityId"),
            evict = {
                    @CacheEvict (value = "app_categories", allEntries = true),
                    @CacheEvict (value = "app_subcategories", allEntries = true),
                    @CacheEvict (value = "app_establishments", allEntries = true),
                    @CacheEvict (value = "app_filters", allEntries = true)
            }
    )
    @Override
    @Transactional (propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Product updateEntity(Long entityId, ProductWithDiscountUpdateRequest request) {

        Product existingProduct = productDAO.findById(entityId)
                .orElseThrow(() -> new SuchResourceNotFoundEx(String.format("Продукт с id %s не найден", entityId)));

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice((request.getPrice()));
        existingProduct.setActive((request.getActive()));
        updateEntityImage(entityId, request.getImage(), EntityType.Product);
        DiscountProduct discountProduct = existingProduct.getDiscountProduct();
        if (discountProduct == null) {
            discountProduct = new DiscountProduct();
            discountProduct.setProduct(existingProduct);
        }
        discountProduct.setDiscount(request.getDiscount());
        discountProduct.setActive(true);
        return productDAO.save(existingProduct);
    }

    @Caching (
            put = @CachePut (value = "app_products", key = "#entityId"),
            evict = {
                    @CacheEvict (value = "app_categories", allEntries = true),
                    @CacheEvict (value = "app_subcategories", allEntries = true),
                    @CacheEvict (value = "app_establishments", allEntries = true),
                    @CacheEvict (value = "app_filters", allEntries = true)
            }
    )
    @Override
    public Product patchEntity(Long entityId, ProductWithDiscountPatchRequest request) {
        Product existingProduct = productDAO.findById(entityId).orElseThrow(
                () -> new SuchResourceNotFoundEx(String.format("Продукт с id %s не найден", entityId))
        );
        if (request.getName() != null) {
            existingProduct.setName(request.getName());
        }
        if (request.getDescription() != null) {
            existingProduct.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            existingProduct.setPrice(request.getPrice());
        }
        if (request.getActive() != null) {
            existingProduct.setActive(request.getActive());
        }
        DiscountProduct discountProduct = existingProduct.getDiscountProduct();
        discountProduct.setDiscount(request.getDiscount());
        discountProduct.setActive(true);
        if(request.getImage() != null) {
            updateEntityImage(entityId, request.getImage(), EntityType.Product);
        }
        return productDAO.save(existingProduct);
    }

    @Override
    public List<Product> findSimilarByNameFilter(String name) {
        Specification<Product> spec = productSpecification.getBySimilarNameFilter(name);
        return productDAO.findAll(spec);
    }


}
