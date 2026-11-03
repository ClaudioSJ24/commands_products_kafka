package com.sanchez.productscommand.models.dto;

public record Reply<T>(
        String status,
        String message,
        T body
) {
}
