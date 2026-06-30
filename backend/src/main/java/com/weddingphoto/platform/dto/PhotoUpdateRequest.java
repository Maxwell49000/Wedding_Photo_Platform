package com.weddingphoto.platform.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record PhotoUpdateRequest(
    @NotBlank String description,
    Boolean visible,
    UUID uploaderGuestId,
    UUID photographerGuestId) {
}
