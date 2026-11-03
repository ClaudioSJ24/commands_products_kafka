package com.sanchez.productscommand.repositories;

import com.sanchez.productscommand.entities.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository  extends CrudRepository<Product, Long> {
}
