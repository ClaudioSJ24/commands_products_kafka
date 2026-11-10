package com.sanchez.productscommand.services;

import com.sanchez.productscommand.models.dto.ProductDto;

import java.util.List;

public interface ProductService {

    ProductDto create(ProductDto productDto);
    ProductDto getById(Long id);
    ProductDto update(Long id, ProductDto productDto);
    boolean deleteById(Long id);
    List<ProductDto> getAll();

}
