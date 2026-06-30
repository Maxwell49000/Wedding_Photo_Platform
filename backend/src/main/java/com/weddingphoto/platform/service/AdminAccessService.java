package com.weddingphoto.platform.service;

public interface AdminAccessService {

  void requireAdminCode(String adminCode);
}
