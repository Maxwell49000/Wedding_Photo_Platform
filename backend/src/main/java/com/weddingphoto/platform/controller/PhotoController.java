package com.weddingphoto.platform.controller;

import com.weddingphoto.platform.dto.PhotoResponse;
import com.weddingphoto.platform.dto.PhotoUpdateRequest;
import com.weddingphoto.platform.service.AdminAccessService;
import com.weddingphoto.platform.service.PhotoService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

  private final PhotoService photoService;
  private final AdminAccessService adminAccessService;

  public PhotoController(PhotoService photoService, AdminAccessService adminAccessService) {
    this.photoService = photoService;
    this.adminAccessService = adminAccessService;
  }

  @GetMapping
  public List<PhotoResponse> listPhotos(
      @RequestParam(value = "includeHidden", defaultValue = "false") boolean includeHidden,
      @RequestHeader(value = "X-Admin-Code", required = false) String adminCode) {
    if (includeHidden) {
      adminAccessService.requireAdminCode(adminCode);
    }
    return photoService.listPhotos(includeHidden);
  }

  @GetMapping("/{id}")
  public PhotoResponse getPhoto(@PathVariable UUID id) {
    return photoService.getPhoto(id);
  }

  @GetMapping("/{id}/view")
  public ResponseEntity<Resource> viewPhoto(@PathVariable UUID id) {
    Resource resource = photoService.viewPhoto(id);
    return ResponseEntity.ok()
        .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
        .body(resource);
  }

  @GetMapping("/{id}/download")
  public ResponseEntity<Resource> downloadPhoto(@PathVariable UUID id) {
    Resource resource = photoService.downloadPhoto(id);
    String filename = photoService.getPhoto(id).filename();
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment().filename(filename).build().toString())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(resource);
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
  public PhotoResponse updatePhoto(
      @PathVariable UUID id,
      @Valid @RequestBody PhotoUpdateRequest request,
      @RequestHeader(value = "X-Admin-Code", required = false) String adminCode) {
    adminAccessService.requireAdminCode(adminCode);
    return photoService.updatePhoto(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePhoto(
      @PathVariable UUID id,
      @RequestHeader(value = "X-Admin-Code", required = false) String adminCode) {
    adminAccessService.requireAdminCode(adminCode);
    photoService.deletePhoto(id);
  }
}
