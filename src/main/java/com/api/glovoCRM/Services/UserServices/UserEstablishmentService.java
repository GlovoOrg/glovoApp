package com.api.glovoCRM.Services.UserServices;

import com.api.glovoCRM.DAOs.EstablishmentDAO;
import com.api.glovoCRM.DAOs.ProductDAO;
import com.api.glovoCRM.DAOs.UserDAOs.UserDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.UserRequests.AdminFindProductFilterRequest;
import com.api.glovoCRM.Rest.Requests.UserRequests.AdminPatchRequest;
import com.api.glovoCRM.Services.BaseUserService;
import com.api.glovoCRM.Specifications.UserSpecifications.AdminSpecification;
import com.api.glovoCRM.Specifications.UserSpecifications.UserSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserEstablishmentService extends BaseUserService {
    private final EstablishmentDAO establishmentDAO;
    private final ProductDAO productDAO;
    private final UserSpecification userSpecification;
    private final UserDAO userDAO;
    private final AdminSpecification adminSpecification;

    public UserEstablishmentService(EstablishmentDAO establishmentDAO, ProductDAO productDAO, UserSpecification userSpecification, UserDAO userDAO, AdminSpecification adminSpecification) {
        super(userDAO);
        this.establishmentDAO = establishmentDAO;
        this.productDAO = productDAO;
        this.userSpecification = userSpecification;
        this.userDAO = userDAO;
        this.adminSpecification = adminSpecification;
    }

    public List<User> getCustomersByEstablishmentId(Long establishmentId) {
        Specification<User> spec = userSpecification.getCustomersByEstablishmentId(establishmentId);
        return userDAO.findAll(spec);
    }

    public List<Product> getProductsByFilter(AdminFindProductFilterRequest filter) {
        Specification<Product> spec = adminSpecification.getProductsByFilter(filter);
        return productDAO.findAll(spec);
    }


}
