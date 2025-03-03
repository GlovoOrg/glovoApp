package com.api.glovoCRM.Services.UserServices;

import com.api.glovoCRM.Repositories.EstablishmentRepository;
import com.api.glovoCRM.Repositories.ProductRepository;
import com.api.glovoCRM.Repositories.UserDAOs.UserRepository;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.UserRequests.AdminFindProductFilterRequest;
import com.api.glovoCRM.Services.BaseUserService;
import com.api.glovoCRM.Specifications.UserSpecifications.AdminSpecification;
import com.api.glovoCRM.Specifications.UserSpecifications.UserSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserEstablishmentService extends BaseUserService {
    private final EstablishmentRepository establishmentRepository;
    private final ProductRepository productRepository;
    private final UserSpecification userSpecification;
    private final UserRepository userRepository;
    private final AdminSpecification adminSpecification;

    public UserEstablishmentService(EstablishmentRepository establishmentRepository, ProductRepository productRepository, UserSpecification userSpecification, UserRepository userRepository, AdminSpecification adminSpecification) {
        super(userRepository);
        this.establishmentRepository = establishmentRepository;
        this.productRepository = productRepository;
        this.userSpecification = userSpecification;
        this.userRepository = userRepository;
        this.adminSpecification = adminSpecification;
    }

    public List<User> getCustomersByEstablishmentId(Long establishmentId) {
        Specification<User> spec = userSpecification.getCustomersByEstablishmentId(establishmentId);
        return userRepository.findAll(spec);
    }

    public List<Product> getProductsByFilter(AdminFindProductFilterRequest filter) {
        Specification<Product> spec = adminSpecification.getProductsByFilter(filter);
        return productRepository.findAll(spec);
    }


}
