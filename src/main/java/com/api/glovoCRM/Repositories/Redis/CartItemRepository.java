package com.api.glovoCRM.Repositories.Redis;

import com.api.glovoCRM.Models.OrderDetailModels.CartItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends CrudRepository<CartItem, String> {

}
