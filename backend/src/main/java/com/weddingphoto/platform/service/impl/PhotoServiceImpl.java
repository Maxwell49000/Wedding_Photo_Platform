package com.weddingphoto.platform.service.impl;

import com.weddingphoto.platform.dto.PhotoResponse;
import com.weddingphoto.platform.dto.PhotoUpdateRequest;
import com.weddingphoto.platform.entity.Guest;
import com.weddingphoto.platform.entity.Photo;
import com.weddingphoto.platform.exception.NotFoundException;
import com.weddingphoto.platform.mapper.PhotoMapper;
import com.weddingphoto.platform.repository.GuestRepository;
import com.weddingphoto.platform.repository.PhotoRepository;
import com.weddingphoto.platform.service.PhotoService;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.OffsetDateTime;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PhotoServiceImpl implements PhotoService {

  private final PhotoRepository photoRepository;
  private final GuestRepository guestRepository;
  private final Path photosDirectory;
  private final Set<String> allowedContentTypes = Set.of(
      "image/jpeg",
      "image/png",
      "image/webp",
      "image/gif");

  public PhotoServiceImpl(
      PhotoRepository photoRepository,
      GuestRepository guestRepository,
      @Value("${app.storage.photos-dir}") String photosDir) {
    this.photoRepository = photoRepository;
    this.guestRepository = guestRepository;
    this.photosDirectory = Paths.get(photosDir);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PhotoResponse> listPhotos(boolean includeHidden) {
    return (includeHidden ? photoRepository.findAll() : photoRepository.findByVisibleTrue())
        .stream()
        .map(PhotoMapper::toResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public PhotoResponse getPhoto(UUID id) {
    return photoRepository.findById(id)
        .map(PhotoMapper::toResponse)
        .orElseThrow(() -> new NotFoundException("Photo not found"));
  }

  @Override
  @Transactional
  public PhotoResponse updatePhoto(UUID id, PhotoUpdateRequest request) {
    Photo photo = photoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Photo not found"));
    photo.setDescription(request.description());
    photo.setVisible(request.visible() == null || request.visible());
    photo.setUploader(resolveGuest(request.uploaderGuestId()));
    photo.setPhotographer(resolveGuest(request.photographerGuestId()));
    return PhotoMapper.toResponse(photoRepository.save(photo));
  }

  @Override
  @Transactional
  public void deletePhoto(UUID id) {
    Photo photo = photoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Photo not found"));
    deleteStoredFile(photo.getFilepath());
    photoRepository.delete(photo);
  }

  @Override
  @Transactional(readOnly = true)
  public Resource viewPhoto(UUID id) {
    return toResource(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Resource downloadPhoto(UUID id) {
    return toResource(id);
  }

  @Override
  @Transactional
  public List<PhotoResponse> uploadPhotos(
      List<MultipartFile> files,
      String description,
      UUID uploaderGuestId,
      UUID photographerGuestId) {
    if (files == null || files.isEmpty()) {
      throw new IllegalArgumentException("At least one file is required");
    }

    try {
      Files.createDirectories(photosDirectory);
    } catch (IOException exception) {
      throw new IllegalStateException("Unable to prepare photo storage", exception);
    }

    Guest uploader = resolveGuest(uploaderGuestId);
    Guest photographer = resolveGuest(photographerGuestId);

    return files.stream().map(file -> savePhoto(file, description, uploader, photographer)).toList();
  }

  private PhotoResponse savePhoto(
      MultipartFile file,
      String description,
      Guest uploader,
      Guest photographer) {
    validateUpload(file);

    String originalName = file.getOriginalFilename() == null ? "photo" : file.getOriginalFilename();
    String storedName = buildStoredName(originalName);
    Path storedPath = photosDirectory.resolve(storedName).normalize();

    if (!storedPath.startsWith(photosDirectory.normalize())) {
      throw new IllegalArgumentException("Invalid file path");
    }

    try {
      Files.copy(file.getInputStream(), storedPath);
    } catch (IOException exception) {
      throw new IllegalStateException("Unable to store uploaded file", exception);
    }

    Photo photo = Photo.builder()
        .filename(originalName)
        .filepath(storedPath.toString())
        .description(description)
        .uploadDate(OffsetDateTime.now())
        .uploader(uploader)
        .photographer(photographer)
        .visible(true)
        .build();

    return PhotoMapper.toResponse(photoRepository.save(photo));
  }

  private Guest resolveGuest(UUID guestId) {
    if (guestId == null) {
      return null;
    }
    return guestRepository.findById(guestId)
        .orElseThrow(() -> new NotFoundException("Guest not found"));
  }

  private void validateUpload(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("Uploaded file cannot be empty");
    }

    String contentType = file.getContentType();
    if (contentType == null || !allowedContentTypes.contains(contentType)) {
      throw new IllegalArgumentException("Unsupported MIME type");
    }
  }

  private String buildStoredName(String originalName) {
    String extension = "";
    int extensionIndex = originalName.lastIndexOf('.');
    if (extensionIndex >= 0) {
      extension = originalName.substring(extensionIndex);
    }
    return UUID.randomUUID() + extension;
  }

  private void deleteStoredFile(String filePath) {
    if (filePath == null || filePath.isBlank()) {
      return;
    }

    try {
      Files.deleteIfExists(Path.of(filePath));
    } catch (IOException exception) {
      throw new IllegalStateException("Unable to delete stored file", exception);
    }
  }

  private Resource toResource(UUID id) {
    Photo photo = photoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Photo not found"));

    try {
      Resource resource = new UrlResource(Path.of(photo.getFilepath()).toUri());
      if (!resource.exists() || !resource.isReadable()) {
        throw new NotFoundException("Photo file not found");
      }
      return resource;
    } catch (MalformedURLException exception) {
      throw new IllegalStateException("Unable to open photo file", exception);
    }
  }
}
