package com.api.glovoCRM.Repositories.Redis;

import com.api.glovoCRM.Models.OrderDetailModels.Cart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CartRepository extends CrudRepository<Cart, String> {
    Optional<Cart> findCartByUserId(Long userId);

}
