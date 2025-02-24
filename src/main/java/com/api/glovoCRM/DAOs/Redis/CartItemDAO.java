package com.api.glovoCRM.DAOs.Redis;

import com.api.glovoCRM.Models.OrderDetailModels.CartItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemDAO extends CrudRepository<CartItem, String> {

}
