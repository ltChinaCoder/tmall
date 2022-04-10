package com.ozm.tmall.entity.dao;

import com.ozm.tmall.entity.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, Integer> {
        User findByName(String name);

        User getByNameAndPassword(String name, String password);
}
