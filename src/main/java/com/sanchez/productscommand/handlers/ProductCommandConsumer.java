package com.sanchez.productscommand.handlers;

import com.sanchez.productscommand.models.Command;
import com.sanchez.productscommand.models.dto.ProductDto;
import com.sanchez.productscommand.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class ProductCommandConsumer {

    private final ProductService productService;

    private final Logger logger = LoggerFactory.getLogger(ProductCommandConsumer.class);

    public ProductCommandConsumer(ProductService productService) {
        this.productService = productService;
    }

    @Bean
    public Consumer<Command<ProductDto>> handleCommands() {
        return command -> {
            String type = command.type() == null ? "" : command.type().toUpperCase();

            switch (type) {
                case "CREATE" -> {
                    if (command.body() == null) {
                        logger.warn("command.body is null");
                        return;
                    }

                    ProductDto productDto = command.body();
                    this.productService.create(productDto);
                    logger.info("product created name={},price={}", productDto.name(), productDto.price());
                }
//                case "UPDATE" -> {
//
//                }
//                case "DELETE" -> {
//
//                }
//                case "FIND_ALL" -> {
//
//                }
                default -> logger.warn("Invalid command type={}", type);
            }
        };
    }
}
