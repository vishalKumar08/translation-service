package com.digitaltolk.translationservice.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Translation data transfer object")
public class TranslationDto {

    @Schema(description = "Translation ID", example = "1")
    private Long id;

    @NotBlank(message = "Translation key is required")
    @Size(max = 500, message = "Translation key must not exceed 500 characters")
    @Schema(description = "Translation key", example = "app.login.title", required = true)
    private String key;

    @NotBlank(message = "Locale is required")
    @Size(max = 10, message = "Locale must not exceed 10 characters")
    @Schema(description = "Locale code", example = "en", required = true)
    private String locale;

    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    @Schema(description = "Translation content", example = "Login", required = true)
    private String content;

    @Schema(description = "Associated tags")
    private Set<TagDto> tags;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Creation timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Last update timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Version for optimistic locking", example = "1")
    private Long version;

    public TranslationDto() {}

    public TranslationDto(String key, String locale, String content) {
        this.key = key;
        this.locale = locale;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Set<TagDto> getTags() {
        return tags;
    }

    public void setTags(Set<TagDto> tags) {
        this.tags = tags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
