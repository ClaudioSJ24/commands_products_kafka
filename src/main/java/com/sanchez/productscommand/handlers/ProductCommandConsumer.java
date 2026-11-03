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
            Reply<ProductDto> reply;
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
//                case "UPDATE" -> {
//
//                }
//                case "DELETE" -> {
//
//                }
//                case "FIND_ALL" -> {
//
//                }
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
