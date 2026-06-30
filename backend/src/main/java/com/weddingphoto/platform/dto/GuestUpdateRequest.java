package com.weddingphoto.platform.dto;

import jakarta.validation.constraints.NotBlank;

public record GuestUpdateRequest(
    @NotBlank String firstname,
    @NotBlank String lastname,
    @NotBlank String displayName,
    @NotBlank String category,
    String tableNumber,
    Boolean active) {
}
