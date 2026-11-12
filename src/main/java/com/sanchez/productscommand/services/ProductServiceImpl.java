package com.sanchez.productscommand.services;

import com.sanchez.productscommand.entities.Product;
import com.sanchez.productscommand.models.dto.ProductDto;
import com.sanchez.productscommand.models.mappers.Mappers;
import com.sanchez.productscommand.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    @Override
    @Transactional(readOnly = true)
    public ProductDto getById(Long id) {
        return this.productRepository.findById(id).map(Mappers::toProductDto).orElse(null);
    }

    @Override
    @Transactional
    public ProductDto update(Long id, ProductDto productDto) {
        Optional<Product> optionalProduct = this.productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product entity = optionalProduct.orElse(null);
            entity.setName(productDto.name());
            entity.setPrice(productDto.price());
            return Mappers.toProductDto(this.productRepository.save(entity));

        }
        return null;

//      OPCION 1
//        Product productUpdated = this.productRepository.save(entity);
//        return Mappers.toProductDto(productUpdated);
        //      OPCION 2

    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        boolean result = this.productRepository.existsById(id);
        if (result) {
            this.productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAll() {
        /**
         * /OPCION 1
         * Con CrudRepository<Product, Long>

        return ((List<Product>) this.productRepository.findAll())
                .stream()
                .map(Mappers::toProductDto)
                .toList();

        */

        /** /OPCION 2
         * Con JpaRepository<Product, Long>
         */
         return this.productRepository.findAll()
         .stream()
         .map(Mappers::toProductDto)
         .toList();


    }
}
