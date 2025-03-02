package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.DAOs.EstablishmentDAO;
import com.api.glovoCRM.DAOs.ImageAssociationsDAO;
import com.api.glovoCRM.DAOs.ImageDAO;
import com.api.glovoCRM.DAOs.SubCategoryDAO;
import com.api.glovoCRM.Embeddable.EstablishmentDetails;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.FileUploadEx;
import com.api.glovoCRM.Exceptions.MinioExceptions.MinioOperationEx;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import com.api.glovoCRM.Models.EstablishmentModels.EstablishmentAddress;
import com.api.glovoCRM.Models.EstablishmentModels.SubCategory;
import com.api.glovoCRM.Rest.Requests.EstablishmentsRequests.EstablishmentCreateRequest;
import com.api.glovoCRM.Rest.Requests.EstablishmentsRequests.EstablishmentPatchRequest;
import com.api.glovoCRM.Rest.Requests.EstablishmentsRequests.EstablishmentUpdateRequest;
import com.api.glovoCRM.Services.BaseService;
import com.api.glovoCRM.Services.ImageCacheService;
import com.api.glovoCRM.Specifications.EstablimentSpecifications.EstablismentSpecification;
import com.api.glovoCRM.Utils.Minio.MinioService;
import com.api.glovoCRM.constants.EntityType;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;


import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@CacheConfig (cacheNames = "app_establishments")
public class EstablishmentService extends BaseService<Establishment, EstablishmentCreateRequest, EstablishmentUpdateRequest, EstablishmentPatchRequest> {

    private final EstablishmentDAO establishmentDAO;
    private final SubCategoryDAO subCategoryDAO;
    private final EstablismentSpecification establismentSpecification;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public EstablishmentService(ImageDAO imageDAO, ImageAssociationsDAO imageAssociationsDAO, MinioService minioService, EstablishmentDAO establishmentDAO, SubCategoryDAO subCategoryDAO, EstablismentSpecification establismentSpecification, ImageCacheService imageCacheService, TransactionTemplate transactionTemplate) {
        super(imageCacheService, imageDAO, imageAssociationsDAO, minioService);
        this.establishmentDAO = establishmentDAO;
        this.subCategoryDAO = subCategoryDAO;
        this.establismentSpecification = establismentSpecification;
        this.transactionTemplate = transactionTemplate;
    }

    @Cacheable (key = "#id")
    @Transactional (readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Override
    public Establishment findById(Long id) {
        log.info("Находим заведение с id: {}", id);
        return establishmentDAO.findById(id).orElseThrow(
                () -> new SuchResourceNotFoundEx(String.format("Заведение с id %s не найдено", id))
        );
    }

    @Cacheable
    @Transactional (readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Override
    public List<Establishment> findAll() {
        return establishmentDAO.findAll();
    }
    @Override
    @Transactional (propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Caching(
            put = @CachePut(value = "app_establishments", key = "#result.id"),
            evict = {
                    @CacheEvict(value = "app_subcategories", allEntries = true),
                    @CacheEvict(value = "app_categories", allEntries = true),
                    @CacheEvict(value = "app_products", allEntries = true),
                    @CacheEvict(value = "app_filters", allEntries = true)
            }
    )
    public Establishment createEntity(EstablishmentCreateRequest request) {
        if (!subCategoryDAO.existsById(request.getSubCategoryId())) {
            throw new SuchResourceNotFoundEx("Такой подкатегории нет в системе");
        }
        if (establishmentDAO.existsByName(request.getName())) {
            throw new AlreadyExistsEx("Такое заведение уже существует");
        }

        Establishment establishment = createEstablishment(request);
        Establishment savedEstablishment = establishmentDAO.save(establishment);

        EstablishmentAddress address = savedEstablishment.getEstablishmentAddress();
        if (address != null) {
            address.setEstablishment(savedEstablishment);
        }
        try {
            createImageRecord(request.getImage(), "establishments", EntityType.Establishment, savedEstablishment.getId());
            return savedEstablishment;
        } catch (FileUploadEx e) {
            log.error("Ошибка при сохранении изображения в MinIO для заведения {}: {}", savedEstablishment.getId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при создании заведения {}: {}", savedEstablishment.getId(), e.getMessage());
            throw new RuntimeException("Не удалось создать заведения", e);
        }
    }


    @Override
    @Transactional (propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Caching(
            evict = {
                    @CacheEvict(value = "app_establishments", key = "#entityId"),
                    @CacheEvict(value = "app_subcategories", allEntries = true),
                    @CacheEvict(value = "app_categories", allEntries = true),
                    @CacheEvict(value = "app_products", allEntries = true),
                    @CacheEvict(value = "app_filters", allEntries = true)
            }
    )
    public void deleteEntity(Long entityId) {
        Establishment establishment = establishmentDAO.findById(entityId)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Заведение не найдено"));
        transactionTemplate.execute(status -> {
            try {
                establishmentDAO.deleteEstablishmentById(establishment.getId());
                deleteImageRecord(entityId, EntityType.Establishment);
                log.debug("Подкатегория успешно удалена. ID: {}", entityId);
                return null;
            } catch (MinioException e) {
                log.error("MinIO oshibka during deletion for establihsmentId: {}", entityId, e);
                status.setRollbackOnly();
                throw new MinioOperationEx("Ошибка с минио: " + e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error during deletion for establishmentId: {}", entityId, e);
                status.setRollbackOnly();
                throw new RuntimeException("500 во время удаления категории " + e.getMessage(), e);
            }
        });
    }

    @Override
    @Transactional (propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Caching(
            put = @CachePut(value = "app_establishments", key = "#entityId"),
            evict = {
                    @CacheEvict(value = "app_subcategories", allEntries = true),
                    @CacheEvict(value = "app_categories", allEntries = true),
                    @CacheEvict(value = "app_products", allEntries = true),
                    @CacheEvict(value = "app_filters", allEntries = true)
            }
    )
    public Establishment updateEntity(Long entityId, EstablishmentUpdateRequest request) {
        Establishment establishment = getEntityById(entityId, establishmentDAO);

        establishment.setName(request.getName());
        establishment.setPriceOfDelivery(request.getPriceOfDelivery());
        establishment.setTimeOfDelivery(request.getTimeOfDelivery());
        establishment.setOpenTime(request.getOpenTime());
        establishment.setCloseTime(request.getCloseTime());
        updateEntityImage(entityId, request.getImage(), EntityType.Establishment);
        if (request.getSubCategoryId() != null && !request.getSubCategoryId().equals(establishment.getSubcategory().getId())) {
            SubCategory newSubCategory = subCategoryDAO.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Категория не найдена"));
            establishment.setSubcategory(newSubCategory);
        }
        EstablishmentAddress address = establishment.getEstablishmentAddress();
        if (address == null) {
            address = new EstablishmentAddress();
            establishment.setEstablishmentAddress(address);
        }
        address.setAddressLine(request.getAddressLine());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setEstablishment(establishment);

        EstablishmentDetails details = establishment.getDetails();
        if (details == null) {
            details = new EstablishmentDetails();
            establishment.setDetails(details);
        }
        details.setSubCategoryId(request.getSubCategoryId());
        return establishmentDAO.save(establishment);
    }

    @Override
    @Transactional (propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Caching(
            put = @CachePut(value = "app_establishments", key = "#entityId"),
            evict = {
                    @CacheEvict(value = "app_subcategories", allEntries = true),
                    @CacheEvict(value = "app_categories", allEntries = true),
                    @CacheEvict(value = "app_products", allEntries = true),
                    @CacheEvict(value = "app_filters", allEntries = true)
            }
    )
    public Establishment patchEntity(Long entityId, EstablishmentPatchRequest request) {
        Establishment existingEstablishment = establishmentDAO.findById(entityId)
                .orElseThrow(() -> new SuchResourceNotFoundEx(String.format("Заведение с id %s не найдено", entityId)));
        if (request.getName() != null) {
            existingEstablishment.setName(request.getName());
        }
        if (request.getPriceOfDelivery() != null) {
            existingEstablishment.setPriceOfDelivery(request.getPriceOfDelivery());
        }
        if (request.getTimeOfDelivery() != null) {
            existingEstablishment.setTimeOfDelivery(request.getTimeOfDelivery());
        }
        if (request.getOpenTime() != null) {
            existingEstablishment.setOpenTime(request.getOpenTime());
        }
        if (request.getCloseTime() != null) {
            existingEstablishment.setCloseTime(request.getCloseTime());
        }
        if (request.getSubCategoryId() != null && !Objects.equals(request.getSubCategoryId(), existingEstablishment.getSubcategory().getId())) {
            SubCategory newSubCategory = subCategoryDAO.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Подкатегория не найдена"));
            existingEstablishment.setSubcategory(newSubCategory);
        }

        EstablishmentAddress address = existingEstablishment.getEstablishmentAddress();
        if (address == null) {
            address = new EstablishmentAddress();
            existingEstablishment.setEstablishmentAddress(address);
        }
        if (request.getAddressLine() != null) {
            address.setAddressLine(request.getAddressLine());
        }
        if (request.getLatitude() != null) {
            address.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            address.setLongitude(request.getLongitude());
        }
        address.setEstablishment(existingEstablishment);

        EstablishmentDetails details = existingEstablishment.getDetails();
        if (details == null) {
            details = new EstablishmentDetails();
            existingEstablishment.setDetails(details);
        }
        if (request.getSubCategoryId() != null) {
            details.setSubCategoryId(request.getSubCategoryId());
        }
        return establishmentDAO.save(existingEstablishment);
    }

    @Override
    public List<Establishment> findSimilarByNameFilter(String name) {
        Specification<Establishment> spec = establismentSpecification.getBySimilarNameFilter(name);
        return establishmentDAO.findAll(spec);
    }

    private Establishment createEstablishment(EstablishmentCreateRequest request) {
        SubCategory subCategory = subCategoryDAO.findById(request.getSubCategoryId())
                .orElseThrow(() -> new SuchResourceNotFoundEx("Такой подкатегории нет в системе"));

        EstablishmentAddress address = new EstablishmentAddress();
        address.setAddressLine(request.getAddressLine());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());

        Establishment establishment = getEstablishment(request, subCategory, address);
        address.setEstablishment(establishment);

        return establishment;
    }

    @NotNull
    private static Establishment getEstablishment(EstablishmentCreateRequest request, SubCategory
            subCategory, EstablishmentAddress address) {
        Establishment establishment = new Establishment();
        establishment.setName(request.getName());
        establishment.setPriceOfDelivery(request.getPriceOfDelivery());
        establishment.setTimeOfDelivery(request.getTimeOfDelivery());
        establishment.setOpenTime(request.getOpenTime());
        establishment.setCloseTime(request.getCloseTime());
        establishment.setSubcategory(subCategory);

        EstablishmentDetails details = new EstablishmentDetails();
        details.setSubCategoryId(request.getSubCategoryId());
        establishment.setDetails(details);

        establishment.setEstablishmentAddress(address);
        return establishment;
    }
    public List<Establishment> getEstablishmentsByRatingAscFilter() {
        return establishmentDAO.findAll(establismentSpecification.getEstablishmentByRatingAscFilter());
    }

    public List<Establishment> getEstablishmentsByRatingDescFilter() {
        return establishmentDAO.findAll(establismentSpecification.getEstablishmentByRatingDescFilter());
    }

    public List<Establishment> getEstablishmentsByDeliveryPriceAscFilter() {
        return establishmentDAO.findAll(establismentSpecification.getEstablishmentByDeliveryPriceAscFilter());
    }

    public List<Establishment> getEstablishmentsByDeliveryPriceDescFilter() {
        return establishmentDAO.findAll(establismentSpecification.getEstablishmentByDeliveryPriceDescFilter());
    }
}
