package com.weddingphoto.platform.repository;

import com.weddingphoto.platform.entity.Photo;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {

  List<Photo> findByVisibleTrue();
}
