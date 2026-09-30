package com.example.urlshortener.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * This class represents one row in the "url_mapping" SQL table.
 * Spring Data JPA turns this Java class into a table automatically (that's what @Entity does),
 * so you don't have to write CREATE TABLE statements by hand.
 */
@Entity
@Table(name = "url_mapping")
public class UrlMapping {

    // Primary key, auto-incremented by the database (1, 2, 3, ...)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The original, long URL the user wants to shorten
    @Column(nullable = false, length = 2048)
    private String originalUrl;

    // The generated short code, e.g. "aZ3xQ1" -> becomes yourdomain.com/aZ3xQ1
    @Column(nullable = false, unique = true, length = 20)
    private String shortCode;

    // When this mapping was created
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Optional expiry date/time; null means "never expires"
    private LocalDateTime expiresAt;

    // How many times the short link has been visited
    @Column(nullable = false)
    private long clickCount = 0;

    // --- Constructors ---

    public UrlMapping() {
        // JPA requires a no-argument constructor
    }

    public UrlMapping(String originalUrl, String shortCode, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    // --- Getters and setters (JPA and Jackson/JSON need these) ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public long getClickCount() {
        return clickCount;
    }

    public void setClickCount(long clickCount) {
        this.clickCount = clickCount;
    }
}
