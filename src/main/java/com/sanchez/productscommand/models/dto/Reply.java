package com.sanchez.productscommand.models.dto;

import com.sanchez.productscommand.models.ReplyStatus;

public record Reply<T>(
        ReplyStatus status,
        String message,
        T body
) {
}
