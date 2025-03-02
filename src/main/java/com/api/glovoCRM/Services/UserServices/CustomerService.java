package com.api.glovoCRM.Services.UserServices;

import com.api.glovoCRM.DAOs.UserDAOs.UserDAO;
import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Models.OrderDetailModels.Order;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.UserRequests.CustomerPatchRequest;
import com.api.glovoCRM.Services.BaseUserService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService extends BaseUserService {
    private final UserDAO userDAO;

    protected CustomerService( UserDAO userDAO) {
        super(userDAO);
        this.userDAO = userDAO;
    }

    public List<Order> getAllOrders() {
        User user = getUserFromSecurityContext();
        return user.getOrderEntities();
    }

    public void deleteUserProfile() {
        Long user = getUserFromSecurityContext().getId();
        userDAO.deleteById(user);
    }

    public User patchCustomer(CustomerPatchRequest request) {
        User user = getUserFromSecurityContext();

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
        return userDAO.save(user);
    }


}
