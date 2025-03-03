package com.api.glovoCRM.Services.UserServices;

import com.api.glovoCRM.Repositories.EstablishmentRepository;
import com.api.glovoCRM.Repositories.ProductRepository;
import com.api.glovoCRM.Repositories.UserDAOs.UserRepository;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.EstablishmentModels.Establishment;
import com.api.glovoCRM.Models.EstablishmentModels.Product;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.UserRequests.AdminFindEstablishmentFilterRequest;
import com.api.glovoCRM.Rest.Requests.UserRequests.AdminFindProductFilterRequest;
import com.api.glovoCRM.Rest.Requests.UserRequests.AdminFindUserFilterRequest;
import com.api.glovoCRM.Rest.Requests.UserRequests.AdminPatchRequest;
import com.api.glovoCRM.Services.BaseUserService;
import com.api.glovoCRM.Specifications.UserSpecifications.AdminSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService extends BaseUserService {

    private final AdminSpecification adminSpecification;
    private final UserRepository userRepository;
    private final EstablishmentRepository establishmentRepository;
    private final ProductRepository productRepository;

    public AdminService(UserRepository userRepository, AdminSpecification adminSpecification, UserRepository userRepository1, EstablishmentRepository establishmentRepository, ProductRepository productRepository) {
        super(userRepository);
        this.adminSpecification = adminSpecification;
        this.userRepository = userRepository1;
        this.establishmentRepository = establishmentRepository;
        this.productRepository = productRepository;
    }


    public List<User> getUsersByFilter(AdminFindUserFilterRequest filter) {
        Specification<User> spec = adminSpecification.getUserByFilter(filter);
        return userRepository.findAll(spec);
    }

    public List<Establishment> getEstablishmentsByFilter(AdminFindEstablishmentFilterRequest filter) {
        Specification<Establishment> spec = adminSpecification.getEstablishmentsByFilter(filter);
        return establishmentRepository.findAll(spec);
    }

    public List<Product> getProductsByFilter(AdminFindProductFilterRequest filter) {
        Specification<Product> spec = adminSpecification.getProductsByFilter(filter);
        return productRepository.findAll(spec);
    }


    public User patchUser(Long id,AdminPatchRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new SuchResourceNotFoundEx("User Not Found"));

        if(request.getUsername() != null) {
            user.setName(request.getUsername());
        }
        if(request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }
        if(request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if(request.getPhone() != null) {
            user.setPhoneNumber(request.getPhone());
        }
        if(request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if(request.getLogin() != null) {
            user.setLogin(request.getLogin());
        }
        if(request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }
        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        if(!userRepository.existsById(id)) {
            throw new SuchResourceNotFoundEx("User Not Found");
        }
        userRepository.deleteById(id);
    }
}
