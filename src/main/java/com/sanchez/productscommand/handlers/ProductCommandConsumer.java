package com.sanchez.productscommand.handlers;

import com.sanchez.productscommand.models.Command;
import com.sanchez.productscommand.models.dto.ProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class ProductCommandConsumer {

    private final Logger logger = LoggerFactory.getLogger(ProductCommandConsumer.class);
    @Bean
    public Consumer<Command<ProductDto>> handleCommands() {
        return command -> {
            logger.info("Comando recibido y consumido con exito: type={}, body={}",command.type(), command.body());
        };
    }
}
