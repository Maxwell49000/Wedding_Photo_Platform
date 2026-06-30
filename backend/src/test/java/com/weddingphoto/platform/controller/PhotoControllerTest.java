package com.weddingphoto.platform.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weddingphoto.platform.dto.PhotoResponse;
import com.weddingphoto.platform.dto.PhotoUpdateRequest;
import com.weddingphoto.platform.exception.ApiExceptionHandler;
import com.weddingphoto.platform.service.AdminAccessService;
import com.weddingphoto.platform.service.PhotoService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

@WebMvcTest(PhotoController.class)
@Import(ApiExceptionHandler.class)
class PhotoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PhotoService photoService;

  @MockBean
  private AdminAccessService adminAccessService;

  @Test
  void listsPhotos() throws Exception {
    PhotoResponse response = photoResponse();
    when(photoService.listPhotos(false)).thenReturn(List.of(response));

    mockMvc.perform(get("/api/photos"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(response.id().toString()));

    verify(photoService).listPhotos(false);
  }

  @Test
  void listsHiddenPhotosForAdminMode() throws Exception {
    PhotoResponse response = photoResponse();
    when(photoService.listPhotos(true)).thenReturn(List.of(response));

    mockMvc.perform(get("/api/photos").param("includeHidden", "true").header("X-Admin-Code", "admin-demo"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(response.id().toString()));

    verify(adminAccessService).requireAdminCode("admin-demo");
    verify(photoService).listPhotos(true);
  }

  @Test
  void uploadsPhotos() throws Exception {
    MockMultipartFile file = new MockMultipartFile("files", "photo.jpg", "image/jpeg", "data".getBytes());
    when(photoService.uploadPhotos(any(), eq("summer"), any(), any())).thenReturn(List.of(photoResponse()));

    mockMvc.perform(multipart("/api/photos/upload")
            .file(file)
            .param("description", "summer")
            .param("uploaderGuestId", UUID.randomUUID().toString())
            .param("photographerGuestId", UUID.randomUUID().toString()))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void downloadsPhotoWithAttachmentHeader() throws Exception {
    UUID id = UUID.randomUUID();
    when(photoService.downloadPhoto(id)).thenReturn(new ByteArrayResource("file".getBytes()));
    when(photoService.getPhoto(id)).thenReturn(photoResponse(id));

    mockMvc.perform(get("/api/photos/{id}/download", id))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("attachment")))
        .andExpect(content().bytes("file".getBytes()));
  }

  @Test
  void updatesPhoto() throws Exception {
    UUID id = UUID.randomUUID();
    when(photoService.updatePhoto(eq(id), any(PhotoUpdateRequest.class))).thenReturn(photoResponse(id));

    mockMvc.perform(put("/api/photos/{id}", id)
            .header("X-Admin-Code", "admin-demo")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new PhotoUpdateRequest("desc", true, null, null))))
        .andExpect(status().isOk());

    verify(adminAccessService).requireAdminCode("admin-demo");
    verify(photoService).updatePhoto(eq(id), any(PhotoUpdateRequest.class));
  }

  @Test
  void deletesPhoto() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc.perform(delete("/api/photos/{id}", id).header("X-Admin-Code", "admin-demo"))
        .andExpect(status().isNoContent());

    verify(adminAccessService).requireAdminCode("admin-demo");
    verify(photoService).deletePhoto(id);
  }

  private PhotoResponse photoResponse() {
    return photoResponse(UUID.randomUUID());
  }

  private PhotoResponse photoResponse(UUID id) {
    return new PhotoResponse(
        id,
        "photo.jpg",
        "/tmp/photo.jpg",
        "desc",
        java.time.OffsetDateTime.parse("2026-06-30T20:00:00Z"),
        null,
        null,
        true,
        "/api/photos/" + id + "/view",
        "/api/photos/" + id + "/download");
  }
}
