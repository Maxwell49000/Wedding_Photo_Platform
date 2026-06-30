package com.weddingphoto.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "photo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Photo {

  @Id
  @UuidGenerator
  @GeneratedValue
  private UUID id;

  @Column(nullable = false)
  private String filename;

  @Column(nullable = false)
  private String filepath;

  @Column(length = 1000)
  private String description;

  @Column(name = "upload_date", nullable = false)
  private OffsetDateTime uploadDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "uploader_guest_id")
  private Guest uploader;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "photographer_guest_id")
  private Guest photographer;

  @Column(name = "is_visible", nullable = false)
  private boolean visible;
}
