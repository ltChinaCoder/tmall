package com.ozm.tmall.comparator;

import com.ozm.tmall.entity.pojo.Product;

import java.util.Comparator;


public class ProductAllComparator implements Comparator<Product> {
    @Override
    public int compare(Product p1, Product p2) {

        return p2.getReviewCount() * p2.getSaleCount() - p1.getReviewCount() * p1.getSaleCount();

    }
}
