package com.weddingphoto.platform.mapper;

import com.weddingphoto.platform.dto.GuestCreateRequest;
import com.weddingphoto.platform.dto.GuestResponse;
import com.weddingphoto.platform.dto.GuestUpdateRequest;
import com.weddingphoto.platform.entity.Guest;

public final class GuestMapper {

  private GuestMapper() {
  }

  public static GuestResponse toResponse(Guest guest) {
    return new GuestResponse(
        guest.getId(),
        guest.getFirstname(),
        guest.getLastname(),
        guest.getDisplayName(),
        guest.getCategory(),
        guest.getTableNumber(),
        guest.isActive());
  }

  public static Guest toEntity(GuestCreateRequest request) {
    return Guest.builder()
        .firstname(request.firstname())
        .lastname(request.lastname())
        .displayName(request.displayName())
        .category(request.category())
        .tableNumber(request.tableNumber())
        .active(request.active() == null || request.active())
        .build();
  }

  public static void updateEntity(Guest guest, GuestUpdateRequest request) {
    guest.setFirstname(request.firstname());
    guest.setLastname(request.lastname());
    guest.setDisplayName(request.displayName());
    guest.setCategory(request.category());
    guest.setTableNumber(request.tableNumber());
    guest.setActive(request.active() == null || request.active());
  }
}
