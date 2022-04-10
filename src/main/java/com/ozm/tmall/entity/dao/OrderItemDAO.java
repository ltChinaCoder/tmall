package com.ozm.tmall.entity.dao;

import com.ozm.tmall.entity.pojo.Order;
import com.ozm.tmall.entity.pojo.OrderItem;
import com.ozm.tmall.entity.pojo.Product;
import com.ozm.tmall.entity.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemDAO extends JpaRepository<OrderItem, Integer> {
        List<OrderItem> findByOrderOrderByIdDesc(Order order);

        List<OrderItem> findByProduct(Product product);

        List<OrderItem> findByUserAndOrderIsNull(User user);


}
