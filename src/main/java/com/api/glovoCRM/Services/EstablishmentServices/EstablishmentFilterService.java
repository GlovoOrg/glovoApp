package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.Repositories.EstablishmentRepository;
import com.api.glovoCRM.Repositories.EstablishmentFilterRepository;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@CacheConfig (cacheNames = "app_filters")
public class EstablishmentFilterService {
    private final EstablishmentRepository establishmentRepository;
    private final EstablishmentFilterRepository establishmentFilterRepository;
    private final EstablishmentFilterSpecification establishmentFilterSpecification;

    @Autowired
    public EstablishmentFilterService(EstablishmentRepository establishmentRepository, EstablishmentFilterRepository establishmentFilterRepository, EstablishmentFilterSpecification establishmentFilterSpecification) {
        this.establishmentRepository = establishmentRepository;
        this.establishmentFilterRepository = establishmentFilterRepository;
        this.establishmentFilterSpecification = establishmentFilterSpecification;
    }
    @PreAuthorize ("hasAnyRole('ROLE_ADMIN', 'ROLE_ESTABLISHMENT')")
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

        Establishment establishment = establishmentRepository.findById(request.getEstablishmentId())
                .orElseThrow(() -> new SuchResourceNotFoundEx("Заведение с указанным ID не найдено"));
        if (establishmentFilterRepository.existsByNameAndEstablishmentId(request.getName(), request.getEstablishmentId())) {
            throw new AlreadyExistsEx("Фильтр с таким именем уже существует для данного заведения");
        }
        EstablishmentFilter filter = new EstablishmentFilter();
        filter.setName(request.getName());
        filter.setEstablishment(establishment);

        EstablishmentFilter savedFilter = establishmentFilterRepository.save(filter);
        log.info("Создан новый фильтр с ID: {}", savedFilter.getId());
        return savedFilter;

    }
    @PreAuthorize ("hasAnyRole('ROLE_ADMIN', 'ROLE_ESTABLISHMENT')")
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
            Establishment establishment = establishmentRepository.findById(request.getEstablishmentId())
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Заведение с указанным ID не найдено"));

            EstablishmentFilter filter = establishmentFilterRepository.findById(id)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Такой фильтр не существует"));
            filter.setName(request.getName());
            filter.setEstablishment(establishment);

            EstablishmentFilter updatedFilter = establishmentFilterRepository.save(filter);
            log.info("Обновлен фильтр с ID: {}", updatedFilter.getId());
            return updatedFilter;
    }
    @PreAuthorize ("hasAnyRole('ROLE_ADMIN', 'ROLE_ESTABLISHMENT')")
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
            EstablishmentFilter filter = establishmentFilterRepository.findById(id)
                    .orElseThrow(() -> new SuchResourceNotFoundEx("Такого фильтра нет"));
            if (request.getName() != null) {
                filter.setName(request.getName());
            }
            if (request.getEstablishmentId() != null) {
                Establishment establishment = establishmentRepository.findById(request.getEstablishmentId())
                        .orElseThrow(() -> new SuchResourceNotFoundEx("Заведение с указанным ID не найдено"));
                filter.setEstablishment(establishment);
            }
            return establishmentFilterRepository.save(filter);
    }
    @PreAuthorize ("hasAnyRole('ROLE_ADMIN', 'ROLE_ESTABLISHMENT')")
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
        if (!establishmentFilterRepository.existsById(id)) {
            throw new SuchResourceNotFoundEx("Такого фильтра не существует");
        }
        establishmentFilterRepository.deleteById(id);
        log.info("Удален фильтр с ID: {}", id);
    }
    @Cacheable()
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<EstablishmentFilter> getAll() {
        return establishmentFilterRepository.findAll();
    }
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Cacheable(value = "app_filter", key = "#id")
    public EstablishmentFilter findById(Long id) {
        return establishmentFilterRepository.findById(id).orElseThrow(
                () -> new SuchResourceNotFoundEx("Такого фильтра не существует")
        );
    }

    public List<EstablishmentFilter> findSimilarByNameFilter(String name) {
        Specification<EstablishmentFilter> spec = establishmentFilterSpecification.getBySimilarNameFilter(name);
        return establishmentFilterRepository.findAll(spec);
    }

}
