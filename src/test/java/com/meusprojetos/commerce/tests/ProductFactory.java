package com.meusprojetos.commerce.tests;

import com.meusprojetos.commerce.entities.Category;
import com.meusprojetos.commerce.entities.Product;

public class ProductFactory {

    public static Product createProduct() {

        Category category = CategoryFactory.createCategory();
        Product product = new Product(1L, "Console Playstation 5", "O melhor console do mundo", 3990.0, "http://www.playstation.com");
        product.getCategories().add(category);
        return product;
    }

    public static Product createProduct(String name) {

        Product product = createProduct();
        product.setName(name);
        return product;
    }
}
