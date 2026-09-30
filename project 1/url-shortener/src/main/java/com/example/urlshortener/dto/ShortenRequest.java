package com.example.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO = Data Transfer Object.
 * This represents the JSON body the client sends us when asking to shorten a URL:
 * { "url": "https://example.com/some/very/long/path" }
 *
 * We keep this separate from the UrlMapping entity so that what clients send us
 * is never directly tied to our database structure. This is good practice.
 */
public class ShortenRequest {

    @NotBlank(message = "url must not be blank")
    @Pattern(regexp = "^https?://.+", message = "url must start with http:// or https://")
    private String url;

    // Optional: let the user request a custom alias instead of a random code
    private String customAlias;

    public ShortenRequest() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }
}
