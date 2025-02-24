package com.api.glovoCRM.DAOs.Redis;

import com.api.glovoCRM.Models.OrderDetailModels.Cart;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CartDAO extends CrudRepository<Cart, String> {
    Optional<Cart> findCartByUserId(Long userId);

}
