package com.weddingphoto.platform.repository;

import com.weddingphoto.platform.entity.Guest;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, UUID> {
}
