package com.weddingphoto.platform.dto;

import java.util.UUID;

public record GuestResponse(
    UUID id,
    String firstname,
    String lastname,
    String displayName,
    String category,
    String tableNumber,
    boolean active) {
}
