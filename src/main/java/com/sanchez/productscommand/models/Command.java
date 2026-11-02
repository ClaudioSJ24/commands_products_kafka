package com.sanchez.productscommand.models;

public record Command<T>(
        String type,
        Long id,
        T body
) {
}
