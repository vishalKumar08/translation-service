package com.digitaltolk.translationservice.domain.repository;

import com.digitaltolk.translationservice.domain.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);


    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :namePattern, '%')) ORDER BY t.name")
    Page<Tag> findByNameContainingIgnoreCase(@Param("namePattern") String namePattern, Pageable pageable);

    @Query("SELECT DISTINCT t FROM Tag t JOIN t.translations tr WHERE tr.key = :translationKey")
    List<Tag> findByTranslationKey(@Param("translationKey") String translationKey);

}
