package com.digitaltolk.translationservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Schema(description = "Translation search request parameters")
public class TranslationSearchRequest {

    @Size(max = 500, message = "Key pattern must not exceed 500 characters")
    @Schema(description = "Translation key pattern to search for", example = "app.login")
    private String key;

    @Size(max = 10, message = "Locale must not exceed 10 characters")
    @Schema(description = "Locale to filter by", example = "en")
    private String locale;

    @Size(max = 1000, message = "Content search term must not exceed 1000 characters")
    @Schema(description = "Content to search for", example = "Login")
    private String content;

    @Size(max = 100, message = "Tag name must not exceed 100 characters")
    @Schema(description = "Tag name to filter by", example = "mobile")
    private String tagName;

    @Min(value = 0, message = "Page must be non-negative")
    @Schema(description = "Page number (0-based)", example = "0", defaultValue = "0")
    private int page = 0;

    @Min(value = 1, message = "Size must be positive")
    @Schema(description = "Page size", example = "20", defaultValue = "20")
    private int size = 20;

    @Schema(description = "Sort field", example = "updatedAt", defaultValue = "updatedAt")
    private String sortBy = "updatedAt";

    @Schema(description = "Sort direction", example = "desc", defaultValue = "desc", allowableValues = {"asc", "desc"})
    private String sortDirection = "desc";

    public TranslationSearchRequest() {}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
}
