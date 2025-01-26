package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.DAOs.*;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Utils.Minio.MinioService;
import com.api.glovoCRM.Models.EstablishmentModels.*;
import com.api.glovoCRM.Rest.Requests.DiscountProductRequest;
import com.api.glovoCRM.Rest.Requests.ProductCreateRequest;
import com.api.glovoCRM.Rest.Requests.ProductPatchRequest;
import com.api.glovoCRM.Rest.Requests.ProductUpdateRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


    @Service
    public class ProductService {

        private final ProductDAO productDAO;
        private final EstablishmentDAO establishmentDAO;
        private final EstablishmentFilterDAO establishmentFilterDAO;
        private final MinioService minioService;

        @Autowired
        public ProductService(
                ProductDAO productDAO,
                EstablishmentDAO establishmentDAO,
                EstablishmentFilterDAO establishmentFilterDAO,
                MinioService minioService
        ) {
            this.productDAO = productDAO;
            this.establishmentDAO = establishmentDAO;
            this.establishmentFilterDAO = establishmentFilterDAO;
            this.minioService = minioService;
        }

        @Transactional
        public Product createProductWithRelations(ProductCreateRequest request) {
            String objectId = "product-" + UUID.randomUUID() + "-" + LocalDateTime.now() + ".img";
            String image = minioService.uploadFile(request.getImage(), "products", objectId);

            Establishment establishment = establishmentDAO.findById(request.getEstablishmentId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Establishment not found"));
            EstablishmentFilter filter = establishmentFilterDAO.findById(request.getEstablishmentFilterId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Establishment filter not found"));

            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setImage(image);
            product.setEstablishment(establishment);
            product.setEstablishmentFilters(List.of(filter));

            if (request.getDiscountProduct() != null) {
                DiscountProduct discountProduct = new DiscountProduct();
                discountProduct.setActive(request.getDiscountProduct().getActive());
                discountProduct.setProduct(product);
                product.setDiscountProduct(discountProduct);
            }
            return productDAO.save(product);
        }


        @Transactional
        public void deleteProductById(Long productId) {
            Product product = productDAO.findById(productId)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Product not found"));
            minioService.deleteFile("products", extractObjectName(product.getImage()));
            productDAO.delete(product);
        }

        private String extractObjectName(String imageUrl) {
            //доработать
            return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        }
        @Transactional
        public Product getProductById(Long productId) {
            return productDAO.findById(productId)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Product not found"));
        }

        public List<Product> getAllProducts() {
            return productDAO.findAll();
        }
        /*
            public List<ProductDTO> getAllProducts() {
            return productDAO.findAll().stream()
                    .map(productMapper::toDTO)
                    .collect(Collectors.toList());
        } маппинг только в controller
         */
        @Transactional
        public Product updateProduct(Long productId, ProductUpdateRequest request) {
            Product product = productDAO.findById(productId)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Product not found"));

            // Обновление полей
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setActive(request.isActive());

            // Обновление изображения
            if (request.getImage() != null) {
                updateProductImage(product, request.getImage());
            }

            // Обновление скидки
            updateDiscount(product, request.getDiscount());

            return productDAO.save(product);
        }

        private void updateProductImage(Product product, MultipartFile newImage) {
            minioService.deleteFile("products", extractObjectName(product.getImage()));

            String newObjectId = "product-" + UUID.randomUUID() + ".img";
            String newImageUrl = minioService.uploadFile(newImage, "products", newObjectId);
            product.setImage(newImageUrl);
        }

        private void updateDiscount(Product product, DiscountProductRequest discountRequest) {
            if (discountRequest == null) return;

            DiscountProduct discount = product.getDiscountProduct();
            if (discount == null) {
                discount = new DiscountProduct();
                discount.setProduct(product);
            }
            discount.setDiscount(discountRequest.getDiscount());
            discount.setActive(discountRequest.getActive());
            product.setDiscountProduct(discount);
        }

        @Transactional
        public Product patchProduct(Long productId, ProductPatchRequest request) {
            Product product = productDAO.findById(productId)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Product not found"));

            // Обновление имени (если передано)
            if (request.getName() != null) {
                product.setName(request.getName());
            }

            if (request.getDescription() != null) {
                product.setDescription(request.getDescription());
            }

            if (request.getPrice() != null) {
                product.setPrice(request.getPrice());
            }

            if (request.getActive() != null) {
                product.setActive(request.getActive());
            }
            if (request.getImage() != null) {
                updateProductImage(product, request.getImage());
            }

            if (request.getDiscount() != null) {
                updateDiscount(product, request.getDiscount());
            }

            return productDAO.save(product);
        }
}
