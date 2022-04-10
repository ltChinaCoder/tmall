package com.ozm.tmall.entity.dao;

import com.ozm.tmall.entity.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryDAO extends JpaRepository<Category, Integer> {

}
