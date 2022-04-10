package com.ozm.tmall.comparator;

import com.ozm.tmall.entity.pojo.Product;

import java.util.Comparator;


public class ProductDateComparator implements Comparator<Product> {
    @Override
    public int compare(Product p1, Product p2) {

        return p1.getCreateDate().compareTo(p2.getCreateDate());
    }
}
