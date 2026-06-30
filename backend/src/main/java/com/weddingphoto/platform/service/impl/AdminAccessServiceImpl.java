package com.weddingphoto.platform.service.impl;

import com.weddingphoto.platform.service.AdminAccessService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminAccessServiceImpl implements AdminAccessService {

  private final String expectedAdminCode;

  public AdminAccessServiceImpl(@Value("${app.admin.code}") String expectedAdminCode) {
    this.expectedAdminCode = expectedAdminCode;
  }

  @Override
  public void requireAdminCode(String adminCode) {
    if (expectedAdminCode == null || expectedAdminCode.isBlank()) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access is not configured");
    }

    if (!expectedAdminCode.equals(adminCode)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid admin code");
    }
  }
}
