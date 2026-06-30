package com.weddingphoto.platform.service;

import com.weddingphoto.platform.dto.GuestCreateRequest;
import com.weddingphoto.platform.dto.GuestResponse;
import com.weddingphoto.platform.dto.GuestUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface GuestService {

  List<GuestResponse> listGuests();

  GuestResponse createGuest(GuestCreateRequest request);

  GuestResponse updateGuest(UUID id, GuestUpdateRequest request);

  void deleteGuest(UUID id);
}
