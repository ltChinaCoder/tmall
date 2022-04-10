package com.ozm.tmall.entity.service;

import com.ozm.tmall.entity.dao.ProductImageDAO;
import com.ozm.tmall.entity.pojo.OrderItem;
import com.ozm.tmall.entity.pojo.Product;
import com.ozm.tmall.entity.pojo.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "productImages")
public class ProductImageService {
            public static final String type_single = "single";
    public static final String type_detail = "detail";

    @Autowired
    ProductImageDAO productImageDAO;

        @CacheEvict(allEntries = true)
    public void add(ProductImage bean) {
        productImageDAO.save(bean);
    }

        @Cacheable(key = "'productImages-single-pid-'+ #p0.id")
    public List<ProductImage> listSingleProductImages(Product product) {
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product, type_single);
    }

        @Cacheable(key = "'productImages-detail-pid-'+ #p0.id")
    public List<ProductImage> listDetailProductImages(Product product) {
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product, type_detail);
    }

    @Cacheable(key = "'productImages-one-'+ #p0")
    public ProductImage get(int id) {
        return productImageDAO.findOne(id);
    }

    @CacheEvict(allEntries = true)
        public void delete(int id) {
        productImageDAO.delete(id);
    }


        public void setFirstProdutImage(Product product) {
                List<ProductImage> list = listSingleProductImages(product);
                if (!list.isEmpty()) {
            product.setFirstProductImage(list.get(0));
        } else {
            product.setFirstProductImage(new ProductImage());
        }
    }

            public void setFirstProdutImages(List<Product> products) {
        for (Product product : products)
            setFirstProdutImage(product);
    }

    public void setFirstProdutImagesOnOrderItems(List<OrderItem> ois) {
        for (OrderItem orderItem : ois) {
            setFirstProdutImage(orderItem.getProduct());
        }
    }


}
