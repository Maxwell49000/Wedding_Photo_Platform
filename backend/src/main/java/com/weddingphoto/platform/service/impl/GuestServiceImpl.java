package com.weddingphoto.platform.service.impl;

import com.weddingphoto.platform.dto.GuestCreateRequest;
import com.weddingphoto.platform.dto.GuestResponse;
import com.weddingphoto.platform.dto.GuestUpdateRequest;
import com.weddingphoto.platform.entity.Guest;
import com.weddingphoto.platform.exception.NotFoundException;
import com.weddingphoto.platform.mapper.GuestMapper;
import com.weddingphoto.platform.repository.GuestRepository;
import com.weddingphoto.platform.service.GuestService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GuestServiceImpl implements GuestService {

  private final GuestRepository guestRepository;

  public GuestServiceImpl(GuestRepository guestRepository) {
    this.guestRepository = guestRepository;
  }

  @Override
  public List<GuestResponse> listGuests() {
    return guestRepository.findAll().stream().map(GuestMapper::toResponse).toList();
  }

  @Override
  public GuestResponse createGuest(GuestCreateRequest request) {
    Guest saved = guestRepository.save(GuestMapper.toEntity(request));
    return GuestMapper.toResponse(saved);
  }

  @Override
  public GuestResponse updateGuest(UUID id, GuestUpdateRequest request) {
    Guest guest = guestRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Guest not found"));
    GuestMapper.updateEntity(guest, request);
    return GuestMapper.toResponse(guestRepository.save(guest));
  }

  @Override
  public void deleteGuest(UUID id) {
    if (!guestRepository.existsById(id)) {
      throw new NotFoundException("Guest not found");
    }
    guestRepository.deleteById(id);
  }
}
