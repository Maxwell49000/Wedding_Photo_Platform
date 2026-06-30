package com.weddingphoto.platform.config;

import com.weddingphoto.platform.entity.Guest;
import com.weddingphoto.platform.entity.Photo;
import com.weddingphoto.platform.repository.GuestRepository;
import com.weddingphoto.platform.repository.PhotoRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedDataConfig {

  @Bean
  public ApplicationRunner seedData(GuestRepository guestRepository, PhotoRepository photoRepository) {
    return args -> {
      if (guestRepository.count() > 0 || photoRepository.count() > 0) {
        return;
      }

      Guest alice = guestRepository.save(Guest.builder()
          .firstname("Alice")
          .lastname("Martin")
          .displayName("Alice")
          .category("guest")
          .tableNumber("12")
          .active(true)
          .build());

      Guest luc = guestRepository.save(Guest.builder()
          .firstname("Luc")
          .lastname("Bernard")
          .displayName("Luc")
          .category("photographer")
          .tableNumber("3")
          .active(true)
          .build());

      Photo photo = Photo.builder()
          .filename("placeholder.jpg")
          .filepath("/data/photos/placeholder.jpg")
          .description("Sample wedding photo")
          .uploadDate(OffsetDateTime.now())
          .uploader(alice)
          .photographer(luc)
          .visible(true)
          .build();

      photoRepository.save(photo);
    };
  }
}
