package com.ozm.tmall.entity.service;

import com.ozm.tmall.entity.dao.PropertyValueDAO;
import com.ozm.tmall.entity.pojo.Product;
import com.ozm.tmall.entity.pojo.Property;
import com.ozm.tmall.entity.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "propertyValues")
public class PropertyValueService {

    @Autowired
    PropertyService propertyService;
    @Autowired
    PropertyValueDAO propertyValueDAO;

                public void init(Product product) {
                List<Property> properties = propertyService.listByCategory(product.getCategory());
                for (Property property : properties) {
                        PropertyValue propertyValue = getByPropertyAndProduct(product, property);
            if (null == propertyValue) {
                propertyValue = new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValueDAO.save(propertyValue);
            }

        }
    }

    @Cacheable(key = "'propertyValues-pid-'+ #p0.id")
    public List<PropertyValue> list(Product product) {
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }

    @Cacheable(key = "'propertyValues-one-pid-'+#p0.id+ '-ptid-' + #p1.id")
    public PropertyValue getByPropertyAndProduct(Product product, Property property) {
        return propertyValueDAO.getByPropertyAndProduct(property, product);
    }

        @CacheEvict(allEntries = true)
    public void update(PropertyValue propertyValue) {
        propertyValueDAO.save(propertyValue);
    }


}
