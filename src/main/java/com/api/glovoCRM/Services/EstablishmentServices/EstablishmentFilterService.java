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
import com.api.glovoCRM.Specifications.EstablimentSpecifications.EstablishmentFilterSpecification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@CacheConfig (cacheNames = "app_filters")
public class EstablishmentFilterService {
    private final EstablishmentDAO establishmentDAO;
    private final EstablishmentFilterDAO establishmentFilterDAO;
    private final EstablishmentFilterSpecification establishmentFilterSpecification;

    @Autowired
    public EstablishmentFilterService(EstablishmentDAO establishmentDAO, EstablishmentFilterDAO establishmentFilterDAO, EstablishmentFilterSpecification establishmentFilterSpecification) {
        this.establishmentDAO = establishmentDAO;
        this.establishmentFilterDAO = establishmentFilterDAO;
        this.establishmentFilterSpecification = establishmentFilterSpecification;
    }

    @Caching (
            put = @CachePut (value = "app_filters", key = "#result.id"),
            evict = {
                    @CacheEvict (value = "app_categories", allEntries = true),
                    @CacheEvict (value = "app_subcategories", allEntries = true),
                    @CacheEvict (value = "app_establishments", allEntries = true),
                    @CacheEvict (value = "app_products", allEntries = true)
            }
    )
    @Transactional
    public EstablishmentFilter createEntity(EstablishmentFilterCreateRequest request) {

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

    }

    @Transactional
    @Caching (
            put = @CachePut (value = "app_filters", key = "#id"),
            evict = {
                    @CacheEvict (value = "app_categories", allEntries = true),
                    @CacheEvict (value = "app_subcategories", allEntries = true),
                    @CacheEvict (value = "app_establishments", allEntries = true),
                    @CacheEvict (value = "app_products", allEntries = true)
            }
    )
    public EstablishmentFilter updateEntity(Long id, EstablishmentFilterUpdateRequest request) {
            Establishment establishment = establishmentDAO.findById(request.getEstablishmentId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Заведение с указанным ID не найдено"));

            EstablishmentFilter filter = establishmentFilterDAO.findById(id)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Такой фильтр не существует"));
            filter.setName(request.getName());
            filter.setEstablishment(establishment);

            EstablishmentFilter updatedFilter = establishmentFilterDAO.save(filter);
            log.info("Обновлен фильтр с ID: {}", updatedFilter.getId());
            return updatedFilter;
    }

    @Caching (
            put = @CachePut (value = "app_filters", key = "#id"),
            evict = {
                    @CacheEvict (value = "app_categories", allEntries = true),
                    @CacheEvict (value = "app_subcategories", allEntries = true),
                    @CacheEvict (value = "app_establishments", allEntries = true),
                    @CacheEvict (value = "app_products", allEntries = true)
            }
    )
    @Transactional
    public EstablishmentFilter patchEntity(Long id, EstablishmentFilterPatchRequest request) {
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
    }

    @Caching (
            evict = {
                    @CacheEvict (value = "app_filters", key = "#id"),
                    @CacheEvict (value = "app_categories", allEntries = true),
                    @CacheEvict (value = "app_subcategories", allEntries = true),
                    @CacheEvict (value = "app_establishments", allEntries = true),
                    @CacheEvict (value = "app_products", allEntries = true)
            }
    )
    @Transactional
    public void deleteEntity(Long id) {
        if (!establishmentFilterDAO.existsById(id)) {
            throw new SuchResourceNotFoundEx("Такого фильтра не существует");
        }
        establishmentFilterDAO.deleteById(id);
        log.info("Удален фильтр с ID: {}", id);
    }
    @Cacheable()
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<EstablishmentFilter> getAll() {
        return establishmentFilterDAO.findAll();
    }
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Cacheable(value = "app_filter", key = "#id")
    public EstablishmentFilter findById(Long id) {
        return establishmentFilterDAO.findById(id).orElseThrow(
                () -> new SuchResourceNotFoundEx("Такого фильтра не существует")
        );
    }

    public List<EstablishmentFilter> findSimilarByNameFilter(String name) {
        Specification<EstablishmentFilter> spec = establishmentFilterSpecification.getBySimilarNameFilter(name);
        return establishmentFilterDAO.findAll(spec);
    }
}
