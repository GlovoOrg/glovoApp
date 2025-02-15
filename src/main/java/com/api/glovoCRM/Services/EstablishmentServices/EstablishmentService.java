package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.DAOs.EstablishmentDAO;
import com.api.glovoCRM.DAOs.ImageAssociationsDAO;
import com.api.glovoCRM.DAOs.ImageDAO;
import com.api.glovoCRM.DAOs.SubCategoryDAO;
import com.api.glovoCRM.Embeddable.EstablishmentDetails;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import com.api.glovoCRM.Models.EstablishmentModels.EstablishmentAddress;
import com.api.glovoCRM.Models.EstablishmentModels.SubCategory;
import com.api.glovoCRM.Rest.Requests.EstablishmentsRequests.EstablishmentCreateRequest;
import com.api.glovoCRM.Rest.Requests.EstablishmentsRequests.EstablishmentPatchRequest;
import com.api.glovoCRM.Rest.Requests.EstablishmentsRequests.EstablishmentUpdateRequest;
import com.api.glovoCRM.Services.BaseService;
import com.api.glovoCRM.Utils.Minio.MinioService;
import com.api.glovoCRM.constants.EntityType;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@CacheConfig(cacheNames = BaseService.CACHE_PREFIX + "establishments")
public class EstablishmentService extends BaseService<Establishment, EstablishmentCreateRequest, EstablishmentUpdateRequest, EstablishmentPatchRequest> {

    private final EstablishmentDAO establishmentDAO;
    private final SubCategoryDAO subCategoryDAO;

    @Autowired
    public EstablishmentService(ImageDAO imageDAO, ImageAssociationsDAO imageAssociationsDAO, MinioService minioService, EstablishmentDAO establishmentDAO, SubCategoryDAO subCategoryDAO) {
        super(imageDAO, imageAssociationsDAO, minioService);
        this.establishmentDAO = establishmentDAO;
        this.subCategoryDAO = subCategoryDAO;
    }

    @Override
//    @Cacheable(key = "#id")
    public Establishment findById(Long id) {
        log.info("Находим заведение с id: {}", id);
        return establishmentDAO.findById(id).orElseThrow(
                () -> new SuchResourceNotFoundEx(String.format("Заведение с id %s не найдено", id))
        );
    }
//    @Cacheable
    @Override
    public List<Establishment> findAll() {
        log.info("Получаем все заведения");
        return establishmentDAO.findAll();
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
//    @CacheEvict(allEntries = true)
    public Establishment createEntity(EstablishmentCreateRequest request) {
        try {
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
            if (request.getImage() != null) {
                createImageRecord(request.getImage(), "establishments", EntityType.Establishment, savedEstablishment.getId());
            }

            return savedEstablishment;
        } catch (AlreadyExistsEx | SuchResourceNotFoundEx exception) {
            log.warn("Ошибка при создании заведения: {}", exception.getMessage());
            throw exception;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при создании заведения: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать заведение", e);
        }
    }



    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
//    @CacheEvict(allEntries = true)
    public void deleteEntity(Long entityId) {
        try {
            Establishment establishment = establishmentDAO.findById(entityId)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Заведение не найдено"));

            SubCategory subCategory = establishment.getSubcategory();
            if (subCategory != null) {
                subCategory.getEstablishments().remove(establishment);
                subCategoryDAO.save(subCategory);
            }

            deleteImageRecord(entityId, EntityType.Establishment);
            establishmentDAO.delete(establishment);
            log.info("Подкатегория успешно удалена. ID: {}", entityId);
        } catch (SuchResourceNotFoundEx e) {
            log.warn("Ошибка при удалении подкатегории: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при удалении подкатегории: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось удалить подкатегорию", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
//    @CacheEvict(allEntries = true)
    public Establishment updateEntity(Long entityId, EstablishmentUpdateRequest request) {
        try {
            Establishment existingEstablishment = establishmentDAO.findById(entityId)
                    .orElseThrow(() -> new SuchResourceNotFoundEx(String.format("Заведение с id %s не найдено", entityId)));

            SubCategory subCategory = subCategoryDAO.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Такой подкатегории нет в системе"));

            existingEstablishment.setName(request.getName());
            existingEstablishment.setPriceOfDelivery(request.getPriceOfDelivery());
            existingEstablishment.setTimeOfDelivery(request.getTimeOfDelivery());
            existingEstablishment.setOpenTime(request.getOpenTime());
            existingEstablishment.setCloseTime(request.getCloseTime());
            existingEstablishment.setSubcategory(subCategory);

            EstablishmentAddress address = existingEstablishment.getEstablishmentAddress();
            if (address == null) {
                address = new EstablishmentAddress();
                existingEstablishment.setEstablishmentAddress(address);
            }
            address.setAddressLine(request.getAddressLine());
            address.setLatitude(request.getLatitude());
            address.setLongitude(request.getLongitude());
            address.setEstablishment(existingEstablishment);

            EstablishmentDetails details = existingEstablishment.getDetails();
            if (details == null) {
                details = new EstablishmentDetails();
                existingEstablishment.setDetails(details);
            }
            details.setSubCategoryId(request.getSubCategoryId());

            return establishmentDAO.save(existingEstablishment);
        } catch (SuchResourceNotFoundEx ex) {
            log.warn("Ошибка при обновлении заведения: {}", ex.getMessage());
            throw ex;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при обновлении заведения: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось обновить заведение", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
//    @CacheEvict(allEntries = true)
    public Establishment patchEntity(Long entityId, EstablishmentPatchRequest request) {
        try {
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
            if (request.getSubCategoryId() != null) {
                SubCategory subCategory = subCategoryDAO.findById(request.getSubCategoryId())
                        .orElseThrow(() -> new SuchResourceNotFoundEx("Такой подкатегории нет в системе"));
                existingEstablishment.setSubcategory(subCategory);
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
        } catch (SuchResourceNotFoundEx ex) {
            log.warn("Ошибка при частичном обновлении заведения: {}", ex.getMessage());
            throw ex;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при частичном обновлении заведения: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось выполнить частичное обновление заведения", e);
        }
    }

    @Override
//    @Cacheable(key = "#name")
    public Establishment findByName(String name) {
        return establishmentDAO.findByName(name).orElseThrow(
                () -> new SuchResourceNotFoundEx("Такого заведения нет по имени в системе")
        );
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
    private static Establishment getEstablishment(EstablishmentCreateRequest request, SubCategory subCategory, EstablishmentAddress address) {
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
}
