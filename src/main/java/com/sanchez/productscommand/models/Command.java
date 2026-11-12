package com.sanchez.productscommand.models;

public record Command<T>(
        CommandType type,
        Long id,
        T body
) {
}
