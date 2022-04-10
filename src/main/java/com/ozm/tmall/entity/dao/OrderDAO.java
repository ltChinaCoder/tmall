package com.ozm.tmall.entity.dao;

import com.ozm.tmall.entity.pojo.Order;
import com.ozm.tmall.entity.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDAO extends JpaRepository<Order, Integer> {
            public List<Order> findByUserAndStatusNotOrderByIdDesc(User user, String status);
}
