package com.sanchez.productscommand.handlers;

import com.sanchez.productscommand.models.Command;
import com.sanchez.productscommand.models.CommandType;
import com.sanchez.productscommand.models.ReplyStatus;
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
    public Function<Message<Command<ProductDto>>, Message<Reply<Object>>> handleCommands() {
        return message -> {
            String correlationId = message.getHeaders().get("correlationId").toString();
            logger.info("correlationId={}", correlationId);

            if (correlationId == null || correlationId.isBlank()) {
                return MessageBuilder.withPayload(new Reply<>(ReplyStatus.ERROR, "Missing correlationId", null))
                        .build();
            }
            Command<ProductDto>  command = message.getPayload();

            Reply<Object> reply = switch (command.type()) {
                case CommandType.CREATE -> {
                    if (command.body() == null) {
                        logger.warn("command.body is null");
                        yield  new Reply<>(ReplyStatus.ERROR, "Create empty body", null);
                    }else {

                        ProductDto productSave = this.productService.create(command.body());
                        logger.info("product created name={},price={}", productSave.name(), productSave.price());
                        yield  new Reply<>(ReplyStatus.SUCCESS, "Product created", productSave);
                    }
                }
                case CommandType.READ -> {
                    if (command.id()== null) {
                        logger.warn("command.idis is null");
                        yield  new Reply<>(ReplyStatus.ERROR, "Id is empty", null);
                    }else {

                        ProductDto productDto = this.productService.getById(command.id());
                        logger.info("Reading product by id");
                        yield (productDto == null) ?
                                new Reply<>(ReplyStatus.ERROR, "Product not found", null) :
                                new Reply<>(ReplyStatus.SUCCESS, "Read product name", productDto);
                    }

                }
                case CommandType.READ_ALL -> {
                    logger.info("product find all products");
                    yield  new Reply<>(ReplyStatus.SUCCESS, "Find all products", this.productService.getAll());

                }
                case CommandType.UPDATE -> {
                    if (command.body() == null || command.id() == null) {
                        logger.warn("Id and body is null");
                        yield  new Reply<>(ReplyStatus.ERROR, "Id and body is empty", null);
                    }else {
                        ProductDto productDto = this.productService.update(command.id(), command.body());
                        if (productDto != null) {
                            logger.info("Update product name={}, price={}", productDto.name(), productDto.price());
                            yield  new Reply<>(ReplyStatus.SUCCESS, "Update product", productDto);

                        } else {
                            logger.info("Product not found, null dto");
                            yield  new Reply<>(ReplyStatus.ERROR, "Product not found", null);
                        }
                    }

               }
                case CommandType.DELETE -> {
                    if (command.id() == null) {
                        logger.warn("Id is empty");
                        yield  new Reply<>(ReplyStatus.ERROR, "Id is empty", null);

                    }else {
                        boolean result = this.productService.deleteById(command.id());
                        logger.info("Deleting product");
                        yield (result) ? new Reply<>(ReplyStatus.SUCCESS, "Deleting Product", "deleted") :
                                new Reply<>(ReplyStatus.ERROR, "Product not found", null);
                    }

                }

                default ->
                        {
                            logger.warn("Invalid command type={}", command.type());
                            yield  new Reply<>(ReplyStatus.ERROR, "Unknown command type", null);
                        }

            };

            return  MessageBuilder.withPayload(reply)
                    .setHeader("correlationId", correlationId)
                    .build();

        };
    }
}
