package com.sanchez.productscommand.services;

import com.sanchez.productscommand.entities.Product;
import com.sanchez.productscommand.models.dto.ProductDto;
import com.sanchez.productscommand.models.mappers.Mappers;
import com.sanchez.productscommand.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ProductDto create(ProductDto productDto) {

        //return Mappers.toProductDto(productNew); opcion 2
        return Mappers.toProductDto(this.productRepository.save(Mappers.toProductEntity(productDto)));//opcion 1
    }
}
