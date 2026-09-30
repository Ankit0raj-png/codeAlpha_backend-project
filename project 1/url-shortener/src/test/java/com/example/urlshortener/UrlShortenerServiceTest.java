package com.example.urlshortener;

import com.example.urlshortener.dto.ShortenRequest;
import com.example.urlshortener.dto.ShortenResponse;
import com.example.urlshortener.service.UrlShortenerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A simple test that boots the whole Spring context (using the H2 in-memory DB)
 * and checks that shortening + resolving a URL actually works end to end.
 * Run with:  mvn test
 */
@SpringBootTest
class UrlShortenerServiceTest {

    @Autowired
    private UrlShortenerService service;

    @Test
    void shortenUrl_thenResolveIt_returnsOriginalUrl() {
        ShortenRequest request = new ShortenRequest();
        request.setUrl("https://www.example.com/some/long/path");

        ShortenResponse response = service.shortenUrl(request);

        assertNotNull(response.getShortCode());
        assertEquals(6, response.getShortCode().length());

        String resolved = service.resolveAndRegisterClick(response.getShortCode());
        assertEquals("https://www.example.com/some/long/path", resolved);

        // Clicking increments the click count
        assertEquals(1, service.getClickCount(response.getShortCode()));
    }
}
