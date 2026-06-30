package com.weddingphoto.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "guest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guest {

  @Id
  @UuidGenerator
  @GeneratedValue
  private UUID id;

  @Column(nullable = false)
  private String firstname;

  @Column(nullable = false)
  private String lastname;

  @Column(name = "display_name", nullable = false)
  private String displayName;

  @Column(nullable = false)
  private String category;

  @Column(name = "table_number")
  private String tableNumber;

  @Column(name = "is_active", nullable = false)
  private boolean active;
}
