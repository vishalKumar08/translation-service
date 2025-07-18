package com.digitaltolk.translationservice.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;

@Schema(description = "Translation export response for frontend consumption")
public class TranslationExportResponse {

    @Schema(description = "Translations organized by locale and key", 
            example = "{\"en\": {\"app.login.title\": \"Login\", \"app.login.button\": \"Sign In\"}, \"fr\": {\"app.login.title\": \"Connexion\", \"app.login.button\": \"Se connecter\"}}")
    private Map<String, Map<String, String>> translations;

    @Schema(description = "Available locales", example = "[\"en\", \"fr\", \"es\"]")
    private java.util.HashSet<String> locales;

    @Schema(description = "Total number of translation keys", example = "1250")
    private long totalKeys;

    @Schema(description = "Total number of translations", example = "3750")
    private long totalTranslations;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Export generation timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime generatedAt;

    @Schema(description = "Export format version", example = "1.0")
    private String version = "1.0";

    @Schema(description = "CDN URL if CDN is enabled", example = "https://cdn.example.com/translations/export.json")
    private String cdnUrl;

    @Schema(description = "Cache TTL in seconds", example = "300")
    private Long cacheTtl;


    public TranslationExportResponse() {
        this.generatedAt = LocalDateTime.now();
    }

    public TranslationExportResponse(Map<String, Map<String, String>> translations) {
        this();
        this.translations = translations;
        this.locales = (translations != null)
                ? new HashSet<>(translations.keySet())
                : new HashSet<>();
        ;
        this.totalKeys = translations.values().stream()
                .mapToLong(Map::size)
                .max()
                .orElse(0);
        this.totalTranslations = translations.values().stream()
                .mapToLong(Map::size)
                .sum();
    }

    public Map<String, Map<String, String>> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, Map<String, String>> translations) {
        this.translations = translations;
        if (translations != null) {
            this.locales =  (translations != null)
                    ? new HashSet<>(translations.keySet())
                    : new HashSet<>();

            this.totalKeys = translations.values().stream()
                    .mapToLong(Map::size)
                    .max()
                    .orElse(0);
            this.totalTranslations = translations.values().stream()
                    .mapToLong(Map::size)
                    .sum();
        }
    }

    public java.util.Set<String> getLocales() {
        return locales;
    }

    public void setLocales(java.util.HashSet<String> locales) {
        this.locales = locales;
    }

    public long getTotalKeys() {
        return totalKeys;
    }

    public void setTotalKeys(long totalKeys) {
        this.totalKeys = totalKeys;
    }

    public long getTotalTranslations() {
        return totalTranslations;
    }

    public void setTotalTranslations(long totalTranslations) {
        this.totalTranslations = totalTranslations;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCdnUrl() {
        return cdnUrl;
    }

    public void setCdnUrl(String cdnUrl) {
        this.cdnUrl = cdnUrl;
    }

    public Long getCacheTtl() {
        return cacheTtl;
    }

    public void setCacheTtl(Long cacheTtl) {
        this.cacheTtl = cacheTtl;
    }
}
