package com.api.glovoCRM.Services.EstablishmentServices;

import com.api.glovoCRM.DAOs.EstablishmentDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class EstablishmentService {

    private final EstablishmentDAO establishmentDAO;

    @Autowired
    public EstablishmentService(EstablishmentDAO establishmentDAO) {
        this.establishmentDAO = establishmentDAO;
    }

    @Cacheable(value = "establishmentsCache")
    public List<Establishment> findAll() {
        log.info("Fetching all establishments from DB");
        return establishmentDAO.findAll();
    }

    @Cacheable(value = "establishmentCache", key = "#id")
    public Establishment findById(Long id) {
        return establishmentDAO.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("Такого заведения нет"));
    }

    public boolean isOpen(Establishment establishment) {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(establishment.getOpenTime())
                && now.isBefore(establishment.getCloseTime());
    }
}
