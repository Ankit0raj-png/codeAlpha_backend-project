package com.example.urlshortener.controller;

import com.example.urlshortener.dto.ShortenRequest;
import com.example.urlshortener.dto.ShortenResponse;
import com.example.urlshortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * This is the "web layer". It only handles HTTP concerns (paths, status codes, request/response
 * bodies) and delegates all real work to UrlShortenerService.
 */
@RestController
public class UrlController {

    private final UrlShortenerService service;

    public UrlController(UrlShortenerService service) {
        this.service = service;
    }

    /**
     * POST /api/shorten
     * Body: { "url": "https://example.com/very/long/path" }
     * Returns: 201 Created with the short URL details.
     */
    @PostMapping("/api/shorten")
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest request) {
        ShortenResponse response = service.shortenUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /{shortCode}
     * This is the endpoint people actually click on. It looks up the original URL
     * and issues an HTTP 302 redirect to it, so the browser is sent to the real destination.
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = service.resolveAndRegisterClick(shortCode);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302
    }

    /**
     * GET /api/stats/{shortCode}
     * Returns metadata about a short code WITHOUT redirecting or counting a click -
     * useful for a dashboard/analytics view.
     */
    @GetMapping("/api/stats/{shortCode}")
    public ResponseEntity<ShortenResponse> stats(@PathVariable String shortCode) {
        return ResponseEntity.ok(service.getStats(shortCode));
    }
}
