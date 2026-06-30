package com.weddingphoto.platform.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PhotoResponse(
    UUID id,
    String filename,
    String filepath,
    String description,
    OffsetDateTime uploadDate,
    UUID uploaderGuestId,
    UUID photographerGuestId,
    boolean visible,
    String viewUrl,
    String downloadUrl) {
}
