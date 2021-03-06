package com.ozm.tmall.entity.service;

import com.ozm.tmall.entity.dao.ReviewDAO;
import com.ozm.tmall.entity.pojo.Product;
import com.ozm.tmall.entity.pojo.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "reviews")
public class ReviewService {

    @Autowired
    ReviewDAO reviewDAO;
        @Autowired
    ProductService productService;


        @Cacheable(key = "'reviews-pid-'+ #p0.id")
    public List<Review> list(Product product) {
        return reviewDAO.findByProductOrderByIdDesc(product);
    }


        @Cacheable(key = "'reviews-count-pid-'+ #p0.id")
    public int getCount(Product product) {
        return reviewDAO.countByProduct(product);
    }

        @CacheEvict(allEntries = true)
    public void add(Review review) {
        reviewDAO.save(review);
    }

}
