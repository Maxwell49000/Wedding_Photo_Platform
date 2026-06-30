package com.weddingphoto.platform.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.weddingphoto.platform.dto.PhotoUpdateRequest;
import com.weddingphoto.platform.entity.Guest;
import com.weddingphoto.platform.entity.Photo;
import com.weddingphoto.platform.exception.NotFoundException;
import com.weddingphoto.platform.repository.GuestRepository;
import com.weddingphoto.platform.repository.PhotoRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class PhotoServiceImplTest {

  @Mock
  private PhotoRepository photoRepository;

  @Mock
  private GuestRepository guestRepository;

  @TempDir
  Path tempDir;

  private PhotoServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new PhotoServiceImpl(photoRepository, guestRepository, tempDir.toString());
  }

  @Test
  void listPhotosOnlyReturnsVisiblePhotosByDefault() {
    Photo visiblePhoto = Photo.builder()
        .id(UUID.randomUUID())
        .filename("photo.jpg")
        .filepath(tempDir.resolve("photo.jpg").toString())
        .uploadDate(OffsetDateTime.now())
        .visible(true)
        .build();

    when(photoRepository.findByVisibleTrue()).thenReturn(List.of(visiblePhoto));

    var responses = service.listPhotos(false);

    assertThat(responses).hasSize(1);
    verify(photoRepository).findByVisibleTrue();
  }

  @Test
  void listPhotosCanIncludeHiddenPhotosForAdmin() {
    Photo hiddenPhoto = Photo.builder()
        .id(UUID.randomUUID())
        .filename("hidden.jpg")
        .filepath(tempDir.resolve("hidden.jpg").toString())
        .uploadDate(OffsetDateTime.now())
        .visible(false)
        .build();

    when(photoRepository.findAll()).thenReturn(List.of(hiddenPhoto));

    var responses = service.listPhotos(true);

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).visible()).isFalse();
    verify(photoRepository).findAll();
  }

  @Test
  void uploadPhotosStoresFileAndReturnsResponse() throws Exception {
    Guest uploader = Guest.builder().id(UUID.randomUUID()).displayName("Alice").build();
    Guest photographer = Guest.builder().id(UUID.randomUUID()).displayName("Luc").build();
    UUID photoId = UUID.randomUUID();
    MockMultipartFile file = new MockMultipartFile(
        "files",
        "wedding.jpg",
        "image/jpeg",
        "photo-bytes".getBytes());

    when(guestRepository.findById(uploader.getId())).thenReturn(Optional.of(uploader));
    when(guestRepository.findById(photographer.getId())).thenReturn(Optional.of(photographer));
    when(photoRepository.save(any(Photo.class))).thenAnswer(invocation -> {
      Photo photo = invocation.getArgument(0);
      photo.setId(photoId);
      return photo;
    });

    var responses = service.uploadPhotos(List.of(file), "A beautiful day", uploader.getId(), photographer.getId());

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).filename()).isEqualTo("wedding.jpg");
    assertThat(Files.list(tempDir).count()).isEqualTo(1);
  }

  @Test
  void viewPhotoReturnsReadableResource() throws Exception {
    Path stored = tempDir.resolve("photo.jpg");
    Files.writeString(stored, "content");
    Photo photo = Photo.builder()
        .id(UUID.randomUUID())
        .filename("photo.jpg")
        .filepath(stored.toString())
        .description("desc")
        .uploadDate(OffsetDateTime.now())
        .visible(true)
        .build();

    when(photoRepository.findById(photo.getId())).thenReturn(Optional.of(photo));

    Resource resource = service.viewPhoto(photo.getId());

    assertThat(resource.exists()).isTrue();
    assertThat(resource.isReadable()).isTrue();
  }

  @Test
  void updatePhotoUpdatesMetadata() {
    UUID photoId = UUID.randomUUID();
    Guest uploader = Guest.builder().id(UUID.randomUUID()).displayName("Alice").build();
    Photo photo = Photo.builder()
        .id(photoId)
        .filename("photo.jpg")
        .filepath(tempDir.resolve("photo.jpg").toString())
        .description("old")
        .uploadDate(OffsetDateTime.now())
        .uploader(uploader)
        .visible(true)
        .build();
    Guest photographer = Guest.builder().id(UUID.randomUUID()).displayName("Luc").build();

    when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
    when(guestRepository.findById(uploader.getId())).thenReturn(Optional.of(uploader));
    when(guestRepository.findById(photographer.getId())).thenReturn(Optional.of(photographer));
    when(photoRepository.save(any(Photo.class))).thenAnswer(invocation -> invocation.getArgument(0));

    var response = service.updatePhoto(photoId, new PhotoUpdateRequest("new desc", false, uploader.getId(), photographer.getId()));

    assertThat(response.description()).isEqualTo("new desc");
    assertThat(response.visible()).isFalse();
    assertThat(response.photographerGuestId()).isEqualTo(photographer.getId());
  }

  @Test
  void deletePhotoDeletesFileAndEntity() throws Exception {
    UUID photoId = UUID.randomUUID();
    Path stored = tempDir.resolve("photo.jpg");
    Files.writeString(stored, "content");
    Photo photo = Photo.builder()
        .id(photoId)
        .filename("photo.jpg")
        .filepath(stored.toString())
        .uploadDate(OffsetDateTime.now())
        .visible(true)
        .build();

    when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));

    service.deletePhoto(photoId);

    assertThat(Files.exists(stored)).isFalse();
    verify(photoRepository).delete(photo);
  }

  @Test
  void getPhotoThrowsWhenMissing() {
    UUID id = UUID.randomUUID();
    when(photoRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.getPhoto(id));
    verify(photoRepository, never()).save(any(Photo.class));
  }
}
