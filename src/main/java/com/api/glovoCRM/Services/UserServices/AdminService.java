package com.api.glovoCRM.Services.UserServices;

import com.api.glovoCRM.DAOs.EstablishmentDAO;
import com.api.glovoCRM.DAOs.ProductDAO;
import com.api.glovoCRM.DAOs.UserDAOs.UserDAO;
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
    private final UserDAO userDAO;
    private final EstablishmentDAO establishmentDAO;
    private final ProductDAO productDAO;

    public AdminService(UserDAO userDAO, AdminSpecification adminSpecification, UserDAO userDAO1, EstablishmentDAO establishmentDAO, ProductDAO productDAO) {
        super(userDAO);
        this.adminSpecification = adminSpecification;
        this.userDAO = userDAO1;
        this.establishmentDAO = establishmentDAO;
        this.productDAO = productDAO;
    }


    public List<User> getUsersByFilter(AdminFindUserFilterRequest filter) {
        Specification<User> spec = adminSpecification.getUserByFilter(filter);
        return userDAO.findAll(spec);
    }

    public List<Establishment> getEstablishmentsByFilter(AdminFindEstablishmentFilterRequest filter) {
        Specification<Establishment> spec = adminSpecification.getEstablishmentsByFilter(filter);
        return establishmentDAO.findAll(spec);
    }

    public List<Product> getProductsByFilter(AdminFindProductFilterRequest filter) {
        Specification<Product> spec = adminSpecification.getProductsByFilter(filter);
        return productDAO.findAll(spec);
    }


    public User patchUser(Long id,AdminPatchRequest request) {
        User user = userDAO.findById(id)
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
        return userDAO.save(user);
    }

    public void deleteUserById(Long id) {
        if(!userDAO.existsById(id)) {
            throw new SuchResourceNotFoundEx("User Not Found");
        }
        userDAO.deleteById(id);
    }
}
