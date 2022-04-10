package com.ozm.tmall.entity.controller;


import com.ozm.tmall.entity.pojo.Product;
import com.ozm.tmall.entity.pojo.PropertyValue;
import com.ozm.tmall.entity.service.ProductService;
import com.ozm.tmall.entity.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PropertyValueController {

    @Autowired
    ProductService productService;
    @Autowired
    PropertyValueService propertyValueService;


            @GetMapping("/products/{pid}/propertyValues")
    public List<PropertyValue> list(@PathVariable("pid") int pid) throws Exception {
        Product product = productService.get(pid);
                propertyValueService.init(product);
                List<PropertyValue> propertyValues = propertyValueService.list(product);
        return propertyValues;
    }

        @PutMapping("/propertyValues")
    public Object update(@RequestBody PropertyValue bean) throws Exception {
        propertyValueService.update(bean);
        return bean;
    }


}
