package com.digitaltolk.translationservice.domain.repository;

import com.digitaltolk.translationservice.domain.entity.Translation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {

    @Query("SELECT t FROM Translation t WHERE t.key = :key AND t.locale = :locale")
    Optional<Translation> findByKeyAndLocale(@Param("key") String key, @Param("locale") String locale);

    @Query("SELECT t FROM Translation t WHERE t.locale = :locale ORDER BY t.key")
    Page<Translation> findByLocale(@Param("locale") String locale, Pageable pageable);

    @Query("SELECT t FROM Translation t WHERE t.key LIKE :keyPattern ORDER BY t.key, t.locale")
    Page<Translation> findByKeyContaining(@Param("keyPattern") String keyPattern, Pageable pageable);

    @Query("SELECT t FROM Translation t WHERE LOWER(t.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY t.updatedAt DESC")
    Page<Translation> findByContentContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT DISTINCT t FROM Translation t JOIN t.tags tag WHERE tag.name = :tagName ORDER BY t.key, t.locale")
    Page<Translation> findByTagName(@Param("tagName") String tagName, Pageable pageable);

    @Query("SELECT DISTINCT t FROM Translation t JOIN t.tags tag WHERE tag.name IN :tagNames ORDER BY t.key, t.locale")
    Page<Translation> findByTagNames(@Param("tagNames") List<String> tagNames, Pageable pageable);

    @Query("""
    SELECT DISTINCT t FROM Translation t 
    LEFT JOIN t.tags tag 
    WHERE (:keyPattern IS NULL OR t.key LIKE :keyPattern)
    AND (:locale IS NULL OR t.locale = :locale)
    AND (:contentPattern IS NULL OR LOWER(t.content) LIKE :contentPattern)
    AND (:tagName IS NULL OR tag.name = :tagName)
    ORDER BY t.updatedAt DESC
    """)
    Page<Translation> searchTranslations(
            @Param("keyPattern") String keyPattern,
            @Param("locale") String locale,
            @Param("contentPattern") String contentPattern,
            @Param("tagName") String tagName,
            Pageable pageable
    );


    @Query("SELECT t FROM Translation t ORDER BY t.locale, t.key")
    List<Translation> findAllForExport();

    @Query("SELECT t FROM Translation t WHERE t.locale = :locale ORDER BY t.key")
    List<Translation> findByLocaleForExport(@Param("locale") String locale);

    @Query("SELECT t FROM Translation t WHERE t.updatedAt > :timestamp ORDER BY t.locale, t.key")
    List<Translation> findUpdatedAfter(@Param("timestamp") LocalDateTime timestamp);

    @Query("SELECT COUNT(t) FROM Translation t WHERE t.locale = :locale")
    long countByLocale(@Param("locale") String locale);

    @Query("SELECT DISTINCT t.locale FROM Translation t ORDER BY t.locale")
    List<String> findDistinctLocales();

    @Modifying
    @Query("UPDATE Translation t SET t.content = :content, t.updatedAt = CURRENT_TIMESTAMP WHERE t.key = :key AND t.locale = :locale")
    int updateContentByKeyAndLocale(@Param("key") String key, @Param("locale") String locale, @Param("content") String content);

    @Modifying
    @Query("DELETE FROM Translation t WHERE t.locale = :locale")
    int deleteByLocale(@Param("locale") String locale);

    @Query("SELECT t FROM Translation t WHERE t.key IN :keys AND t.locale = :locale")
    List<Translation> findByKeysAndLocale(@Param("keys") List<String> keys, @Param("locale") String locale);

    @Query("SELECT COUNT(t) > 0 FROM Translation t WHERE t.key = :key AND t.locale = :locale")
    boolean existsByKeyAndLocale(@Param("key") String key, @Param("locale") String locale);

    @Query("SELECT t FROM Translation t ORDER BY t.updatedAt DESC")
    Page<Translation> findLatestUpdated(Pageable pageable);
}
