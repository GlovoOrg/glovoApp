package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.DAOs.EstablishmentDAO;
import com.api.glovoCRM.DAOs.EstablishmentFilterDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.AlreadyExistsEx;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import com.api.glovoCRM.Models.EstablishmentModels.EstablishmentFilter;
import com.api.glovoCRM.Rest.Requests.EstablishmentFilterRequests.EstablishmentFilterCreateRequest;
import com.api.glovoCRM.Rest.Requests.EstablishmentFilterRequests.EstablishmentFilterPatchRequest;
import com.api.glovoCRM.Rest.Requests.EstablishmentFilterRequests.EstablishmentFilterUpdateRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EstablishmentFilterService{
    private final EstablishmentDAO establishmentDAO;
    private final EstablishmentFilterDAO establishmentFilterDAO;

    @Autowired
    public EstablishmentFilterService(EstablishmentDAO establishmentDAO, EstablishmentFilterDAO establishmentFilterDAO) {
        this.establishmentDAO = establishmentDAO;
        this.establishmentFilterDAO = establishmentFilterDAO;
    }

    @Transactional
    public EstablishmentFilter createEntity(EstablishmentFilterCreateRequest request) {
        try {
            Establishment establishment = establishmentDAO.findById(request.getEstablishmentId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Заведение с указанным ID не найдено"));
            if (establishmentFilterDAO.existsByNameAndEstablishmentId(request.getName(), request.getEstablishmentId())) {
                throw new AlreadyExistsEx("Фильтр с таким именем уже существует для данного заведения");
            }
            EstablishmentFilter filter = new EstablishmentFilter();
            filter.setName(request.getName());
            filter.setEstablishment(establishment);

            EstablishmentFilter savedFilter = establishmentFilterDAO.save(filter);
            log.info("Создан новый фильтр с ID: {}", savedFilter.getId());
            return savedFilter;
        } catch (AlreadyExistsEx | SuchResourceNotFoundEx ex) {
            log.warn("Ошибка при создании фильтра: {}", ex.getMessage());
            throw ex;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при создании фильтра: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать фильтр", e);
        }
    }

    @Transactional
    public EstablishmentFilter updateEntity(Long id, EstablishmentFilterUpdateRequest request) {
        try {
            Establishment establishment = establishmentDAO.findById(request.getEstablishmentId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Заведение с указанным ID не найдено"));

            EstablishmentFilter filter = establishmentFilterDAO.findById(id)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Такой фильтр не существует"));
            filter.setName(request.getName());
            filter.setEstablishment(establishment);

            EstablishmentFilter updatedFilter = establishmentFilterDAO.save(filter);
            log.info("Обновлен фильтр с ID: {}", updatedFilter.getId());
            return updatedFilter;
        } catch (SuchResourceNotFoundEx | AlreadyExistsEx e) {
            log.warn("Ошибка при обновлении фильтра: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public EstablishmentFilter patchEntity(Long id, EstablishmentFilterPatchRequest request) {
        try {
            EstablishmentFilter filter = establishmentFilterDAO.findById(id)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Такого фильтра нет"));
            if (request.getName() != null) {
                filter.setName(request.getName());
            }
            if (request.getEstablishmentId() != null) {
                Establishment establishment = establishmentDAO.findById(request.getEstablishmentId())
                        .orElseThrow(() -> new SuchResourceNotFoundEx("Заведение с указанным ID не найдено"));
                filter.setEstablishment(establishment);
            }
            return establishmentFilterDAO.save(filter);
        } catch (SuchResourceNotFoundEx e) {
            log.warn("Ошибка при частичном обновлении фильтра: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при частичном обновлении фильтра: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось обновить фильтр", e);
        }
    }
    @Transactional
    public void deleteEntity(Long id) {
        if (!establishmentFilterDAO.existsById(id)) {
            throw new SuchResourceNotFoundEx("Такого фильтра не существует");
        }
        establishmentFilterDAO.deleteById(id);
        log.info("Удален фильтр с ID: {}", id);
    }
    public List<EstablishmentFilter> getAll(){
        return establishmentFilterDAO.findAll();
    }
    public EstablishmentFilter findById(Long id) {
        return establishmentFilterDAO.findById(id).orElseThrow(
                ()-> new SuchResourceNotFoundEx("Такого фильтра не существует")
        );
    }
}
