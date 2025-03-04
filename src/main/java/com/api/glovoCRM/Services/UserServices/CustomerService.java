package com.api.glovoCRM.Services.UserServices;

import com.api.glovoCRM.Exceptions.BaseExceptions.SuchResourceNotFoundEx;
import com.api.glovoCRM.Repositories.UserRepositories.UserRepository;
import com.api.glovoCRM.Models.OrderDetailModels.Order;
import com.api.glovoCRM.Models.UserModels.User;
import com.api.glovoCRM.Rest.Requests.UserRequests.CustomerPatchRequest;
import com.api.glovoCRM.Security.AuthenticationTokens.PhoneAuthenticationToken;
import com.api.glovoCRM.Services.BaseUserService;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService extends BaseUserService {
    private final UserRepository userRepository;

    protected CustomerService( UserRepository userRepository) {
        super(userRepository);
        this.userRepository = userRepository;
    }

    public User getUserProfileFromContext() {
        return super.getUserFromSecurityContext();
    }



    public List<Order> getAllOrders() {
        User user = getUserFromSecurityContext();
        return user.getOrderEntities();
    }

    public void deleteUserProfile() {
        Long user = getUserFromSecurityContext().getId();
        userRepository.deleteById(user);
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
        return userRepository.save(user);
    }


}
