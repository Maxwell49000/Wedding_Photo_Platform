package com.weddingphoto.platform.controller;

import com.weddingphoto.platform.dto.GuestCreateRequest;
import com.weddingphoto.platform.dto.GuestResponse;
import com.weddingphoto.platform.dto.GuestUpdateRequest;
import com.weddingphoto.platform.service.GuestService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

  private final GuestService guestService;

  public GuestController(GuestService guestService) {
    this.guestService = guestService;
  }

  @GetMapping
  public List<GuestResponse> listGuests() {
    return guestService.listGuests();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public GuestResponse createGuest(@Valid @RequestBody GuestCreateRequest request) {
    return guestService.createGuest(request);
  }

  @PutMapping("/{id}")
  public GuestResponse updateGuest(@PathVariable UUID id, @Valid @RequestBody GuestUpdateRequest request) {
    return guestService.updateGuest(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteGuest(@PathVariable UUID id) {
    guestService.deleteGuest(id);
  }
}
