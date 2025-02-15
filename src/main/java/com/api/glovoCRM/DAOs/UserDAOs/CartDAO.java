package com.api.glovoCRM.DAOs.UserDAOs;

import com.api.glovoCRM.Models.OrderDetailModels.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartDAO extends JpaRepository<Cart, Long> {

}
