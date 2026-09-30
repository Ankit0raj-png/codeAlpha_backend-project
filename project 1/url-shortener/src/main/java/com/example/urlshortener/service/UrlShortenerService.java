package com.example.urlshortener.service;

import com.example.urlshortener.dto.ShortenRequest;
import com.example.urlshortener.dto.ShortenResponse;
import com.example.urlshortener.entity.UrlMapping;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * This is where the actual "business logic" lives.
 * The Controller receives HTTP requests and delegates the real work to this Service.
 * Keeping logic here (instead of in the controller) keeps things testable and organized.
 */
@Service
public class UrlShortenerService {

    // Characters allowed in a generated short code (Base62: 0-9, a-z, A-Z)
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UrlMappingRepository repository;

    // Injected from application.properties, e.g. "http://localhost:8080/"
    @Value("${app.base-url}")
    private String baseUrl;

    // Constructor injection: Spring automatically supplies the repository bean here.
    public UrlShortenerService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a new short URL for the given original URL (or reuses a custom alias if provided).
     */
    @Transactional
    public ShortenResponse shortenUrl(ShortenRequest request) {
        String code;

        if (request.getCustomAlias() != null && !request.getCustomAlias().isBlank()) {
            code = request.getCustomAlias().trim();
            if (repository.existsByShortCode(code)) {
                throw new IllegalArgumentException("Custom alias '" + code + "' is already taken");
            }
        } else {
            code = generateUniqueCode();
        }

        LocalDateTime now = LocalDateTime.now();
        UrlMapping mapping = new UrlMapping(request.getUrl(), code, now, null);
        repository.save(mapping);

        return toResponse(mapping);
    }

    /**
     * Looks up the original URL for a short code and increments its click counter.
     * Called when someone actually visits http://yourdomain.com/{shortCode}.
     */
    @Transactional
    public String resolveAndRegisterClick(String shortCode) {
        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("No URL found for code: " + shortCode));

        if (mapping.getExpiresAt() != null && mapping.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlNotFoundException("This short link has expired: " + shortCode);
        }

        mapping.setClickCount(mapping.getClickCount() + 1);
        repository.save(mapping);

        return mapping.getOriginalUrl();
    }

    /**
     * Returns stats (click count, created date, etc.) for a given short code, without redirecting.
     */
    public ShortenResponse getStats(String shortCode) {
        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("No URL found for code: " + shortCode));
        return toResponse(mapping);
    }

    public long getClickCount(String shortCode) {
        return repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("No URL found for code: " + shortCode))
                .getClickCount();
    }

    // --- Private helpers ---

    /**
     * Generates a random 6-character alphanumeric code and retries if it happens
     * to already exist in the database (collisions are extremely rare, but we handle them).
     */
    private String generateUniqueCode() {
        String code;
        do {
            code = randomCode(CODE_LENGTH);
        } while (repository.existsByShortCode(code));
        return code;
    }

    private String randomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private ShortenResponse toResponse(UrlMapping mapping) {
        String fullShortUrl = baseUrl.endsWith("/") ? baseUrl + mapping.getShortCode()
                                                     : baseUrl + "/" + mapping.getShortCode();
        return new ShortenResponse(
                mapping.getOriginalUrl(),
                mapping.getShortCode(),
                fullShortUrl,
                mapping.getCreatedAt(),
                mapping.getExpiresAt()
        );
    }
}
