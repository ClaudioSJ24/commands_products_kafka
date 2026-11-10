package com.sanchez.productscommand.handlers;

import com.sanchez.productscommand.models.Command;
import com.sanchez.productscommand.models.dto.ProductDto;
import com.sanchez.productscommand.models.dto.Reply;
import com.sanchez.productscommand.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.function.Consumer;
import java.util.function.Function;

@Configuration
public class ProductCommandConsumer {

    private final ProductService productService;

    private final Logger logger = LoggerFactory.getLogger(ProductCommandConsumer.class);

    public ProductCommandConsumer(ProductService productService) {
        this.productService = productService;
    }

    @Bean
    public Function<Message<Command<ProductDto>>, Message<Reply<?>>> handleCommands() {
        return message -> {

            Command<ProductDto>  command = message.getPayload();
            String type = command.type() == null ? "" : command.type().toUpperCase();
            Reply<?> reply;
            switch (type) {
                case "CREATE" -> {
                    if (command.body() == null) {
                        logger.warn("command.body is null");
                        reply = new Reply<>("Error", "Create empty body", null);
                    }

                    ProductDto productSave = this.productService.create(command.body());
                    logger.info("product created name={},price={}", productSave.name(), productSave.price());
                    reply = new Reply<>("SUCCESS", "Product created", productSave);
                }
                case "READ" -> {
                    if (command.id()== null) {
                        logger.warn("command.idis is null");
                        reply = new Reply<>("Error", "Id is empty", null);
                    }
                    ProductDto productDto = this.productService.getById(command.id());
                    if (productDto.id() == null) {
                        logger.warn("product id is null");
                        reply = new Reply<>("Error", "Id is empty", null);
                    }
                    logger.info("product read name={},price={}", productDto.name(), productDto.price());
                    reply = new Reply<>("SUCCESS", "Product read", productDto);

                }
                case "READ_ALL" -> {
                    reply = new Reply<>("SUCCESS", "Find all products", this.productService.getAll());
                    logger.info("product find all products");

                }
                case "UPDATE" -> {
                    if (command.body() == null || command.id() == null) {
                        logger.warn("Id and body is null");
                        reply = new Reply<>("Error", "Id and body is empty", null);
                    }else {
                        ProductDto productDto = this.productService.update(command.id(), command.body());
                        if (productDto == null) {
                            reply = new Reply<>("Error", "Product not found", null);
                        } else {
                            reply = new Reply<>("SUCCESS", "Update product", productDto);
                            logger.info("Update product name={}, price={}", productDto.name(), productDto.price());
                        }
                    }

               }
                case "DELETE" -> {
                    if (command.id() == null) {
                        logger.warn("Id is empty");
                        reply = new Reply<>("Error", "Id is empty", null);

                    }
                    boolean result = this.productService.deleteById(command.id());
                    if (!result) {
                        reply = new Reply<>("Error", "Product not found", null);
                        logger.info("Product not found");
                    }else {
                        reply = new Reply<>("SUCCESS", "Product deleted", result);
                        logger.info("product delete product");
                    }

                }

                default ->
                        {
                            logger.warn("Invalid command type={}", type);
                            reply = new Reply<>("ERROR", "Unknown command type", null);
                        }

            }
            String correlationId = message.getHeaders().get("correlationId").toString();
            logger.info("correlationId={}", correlationId);
            MessageBuilder<Reply<?>> out = MessageBuilder.withPayload(reply);
            if (correlationId != null) {
                out.setHeader("correlationId", correlationId);
            }

            return out.build();
        };
    }
}
