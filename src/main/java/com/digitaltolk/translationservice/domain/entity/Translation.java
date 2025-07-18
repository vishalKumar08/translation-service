package com.digitaltolk.translationservice.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "translations", indexes = {
    @Index(name = "idx_translation_key", columnList = "translation_key"),
    @Index(name = "idx_locale", columnList = "locale"),
    @Index(name = "idx_key_locale", columnList = "translation_key, locale", unique = true),
    @Index(name = "idx_content_fulltext", columnList = "content"),
    @Index(name = "idx_updated_at", columnList = "updated_at")
})
@EntityListeners(AuditingEntityListener.class)
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 500)
    @Column(name = "translation_key", nullable = false, length = 500)
    private String key;

    @NotBlank
    @Size(max = 10)
    @Column(name = "locale", nullable = false, length = 10)
    private String locale;

    @NotBlank
    @Size(max = 5000)
    @Column(name = "content", nullable = false, length = 5000)
    private String content;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "translation_tags",
        joinColumns = @JoinColumn(name = "translation_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id"),
        indexes = {
            @Index(name = "idx_translation_tags_translation", columnList = "translation_id"),
            @Index(name = "idx_translation_tags_tag", columnList = "tag_id")
        }
    )
    private Set<Tag> tags = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public Translation() {}

    public Translation(String key, String locale, String content) {
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

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getTranslations().add(this);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getTranslations().remove(this);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Translation that = (Translation) o;
        return Objects.equals(key, that.key) && Objects.equals(locale, that.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, locale);
    }

    @Override
    public String toString() {
        return "Translation{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", locale='" + locale + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
