package com.sanchez.productscommand.services;

import com.sanchez.productscommand.entities.Product;
import com.sanchez.productscommand.models.dto.ProductDto;
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
        Product product = new Product(productDto.name(), productDto.price());
        Product productNew = this.productRepository.save(product);
        return new ProductDto(productNew.getName(), productNew.getPrice());
    }
}
