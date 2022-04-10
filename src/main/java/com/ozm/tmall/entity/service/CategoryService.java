package com.ozm.tmall.entity.service;

import com.ozm.tmall.entity.dao.CategoryDAO;
import com.ozm.tmall.entity.pojo.Category;
import com.ozm.tmall.entity.pojo.Product;
import com.ozm.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @CacheConfig(cacheNames = "categories")
public class CategoryService {
    
    @Autowired
    CategoryDAO categoryDao;
        
    @Cacheable(key = "'categories-all'")
    public List<Category> list() throws Exception {
                Sort sort = new Sort(Sort.Direction.ASC, "id");
        return categoryDao.findAll(sort);
    }


            @Cacheable(key = "'categories-page-'+#p0+'-'+#p1")
    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
                        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
                Page<Category> pageFromJPA = categoryDao.findAll(pageable);
                return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

        @CacheEvict(allEntries = true)
        public void add(Category bean) {
        categoryDao.save(bean);
    }

        @CacheEvict(allEntries = true)
        public void delete(int id) {
        categoryDao.delete(id);
    }

                    @Cacheable(key = "'category-one-'+#p0")
        public Category get(int id) {
        Category c = categoryDao.findOne(id);
        return c;
    }

            @CacheEvict(allEntries = true)
    public void update(Category bean) {
        categoryDao.save(bean);
    }

    



















    

        public void removeCategoryFromProduct(List<Category> categories) {
        for (Category category : categories) {
            removeCategoryFromProduct(category);
        }
    }

    public void removeCategoryFromProduct(Category category) {
        List<Product> products = category.getProducts();
        if (products != null) {
            for (Product product : products) {
                product.setCategory(null);
            }
        }

                List<List<Product>> productsByRow = category.getProductsByRow();
        if (productsByRow != null) {
            for (List<Product> products1 : productsByRow) {
                for (Product product : products1) {
                    product.setCategory(null);
                }
            }
        }

    }



    


}
