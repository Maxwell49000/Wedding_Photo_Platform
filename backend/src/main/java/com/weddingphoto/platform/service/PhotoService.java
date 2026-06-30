package com.weddingphoto.platform.service;

import com.weddingphoto.platform.dto.PhotoResponse;
import com.weddingphoto.platform.dto.PhotoUpdateRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {

  List<PhotoResponse> listPhotos(boolean includeHidden);

  PhotoResponse getPhoto(UUID id);

  PhotoResponse updatePhoto(UUID id, PhotoUpdateRequest request);

  void deletePhoto(UUID id);

  Resource viewPhoto(UUID id);

  Resource downloadPhoto(UUID id);

  List<PhotoResponse> uploadPhotos(
      List<MultipartFile> files,
      String description,
      UUID uploaderGuestId,
      UUID photographerGuestId);
}
