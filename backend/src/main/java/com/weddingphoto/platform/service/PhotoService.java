package com.weddingphoto.platform.service;

import com.weddingphoto.platform.dto.PhotoResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {

  List<PhotoResponse> listPhotos();

  PhotoResponse getPhoto(UUID id);

  PhotoResponse markPhotoUpdated(UUID id);

  void deletePhoto(UUID id);

  List<PhotoResponse> uploadPhotos(
      List<MultipartFile> files,
      String description,
      UUID uploaderGuestId,
      UUID photographerGuestId);
}
