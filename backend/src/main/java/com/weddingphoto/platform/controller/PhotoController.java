package com.weddingphoto.platform.controller;

import com.weddingphoto.platform.dto.PhotoResponse;
import com.weddingphoto.platform.service.PhotoService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

  private final PhotoService photoService;

  public PhotoController(PhotoService photoService) {
    this.photoService = photoService;
  }

  @GetMapping
  public List<PhotoResponse> listPhotos() {
    return photoService.listPhotos();
  }

  @GetMapping("/{id}")
  public PhotoResponse getPhoto(@PathVariable UUID id) {
    return photoService.getPhoto(id);
  }

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public List<PhotoResponse> uploadPhoto(
      @RequestParam("files") List<MultipartFile> files,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "uploaderGuestId", required = false) UUID uploaderGuestId,
      @RequestParam(value = "photographerGuestId", required = false) UUID photographerGuestId) {
    return photoService.uploadPhotos(
        files == null ? Collections.emptyList() : files,
        description,
        uploaderGuestId,
        photographerGuestId);
  }

  @PutMapping("/{id}")
  public PhotoResponse updatePhoto(@PathVariable UUID id) {
    return photoService.markPhotoUpdated(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePhoto(@PathVariable UUID id) {
    photoService.deletePhoto(id);
  }
}
