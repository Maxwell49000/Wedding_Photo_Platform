package com.weddingphoto.platform.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

  @GetMapping
  public Map<String, String> search(@RequestParam(required = false, defaultValue = "") String q) {
    return Map.of("query", q, "message", "Search endpoint ready");
  }
}
