package com.sanchez.productscommand.models.mappers;

import com.sanchez.productscommand.entities.Product;
import com.sanchez.productscommand.models.dto.ProductDto;

public final class Mappers {

    private Mappers() {}

    static public ProductDto toProductDto(Product product) {
        return new ProductDto(product.getId(), product.getName(), product.getPrice());
    }

    static public Product toProductEntity(ProductDto productDto) {
        Product entity = new Product(productDto.name(), productDto.price());
        entity.setId(productDto.id());
        return entity;
    }
}
