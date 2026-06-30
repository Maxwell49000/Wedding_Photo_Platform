package com.weddingphoto.platform.mapper;

import com.weddingphoto.platform.dto.PhotoResponse;
import com.weddingphoto.platform.entity.Photo;

public final class PhotoMapper {

  private PhotoMapper() {
  }

  public static PhotoResponse toResponse(Photo photo) {
    return new PhotoResponse(
        photo.getId(),
        photo.getFilename(),
        photo.getFilepath(),
        photo.getDescription(),
        photo.getUploadDate(),
        photo.getUploader() == null ? null : photo.getUploader().getId(),
        photo.getPhotographer() == null ? null : photo.getPhotographer().getId(),
        photo.isVisible());
  }
}
