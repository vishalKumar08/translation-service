package com.digitaltolk.translationservice.service;

import com.digitaltolk.translationservice.api.dto.PagedResponse;
import com.digitaltolk.translationservice.api.dto.TranslationDto;
import com.digitaltolk.translationservice.api.dto.TranslationExportResponse;
import com.digitaltolk.translationservice.api.dto.TranslationSearchRequest;
import com.digitaltolk.translationservice.api.mapper.TranslationMapper;
import com.digitaltolk.translationservice.domain.entity.Tag;
import com.digitaltolk.translationservice.domain.entity.Translation;
import com.digitaltolk.translationservice.domain.repository.TagRepository;
import com.digitaltolk.translationservice.domain.repository.TranslationRepository;
import com.digitaltolk.translationservice.exception.ResourceNotFoundException;
import com.digitaltolk.translationservice.exception.DuplicateResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TranslationService {

    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);

    private final TranslationRepository translationRepository;
    private final TagRepository tagRepository;
    private final TranslationMapper translationMapper;

    @Value("${app.performance.max-export-size:100000}")
    private int maxExportSize;

    @Value("${app.performance.cache-ttl:300}")
    private long cacheTtl;

    @Value("${app.cdn.enabled:false}")
    private boolean cdnEnabled;

    @Value("${app.cdn.base-url:}")
    private String cdnBaseUrl;

    public TranslationService(TranslationRepository translationRepository,
                            TagRepository tagRepository,
                            TranslationMapper translationMapper) {
        this.translationRepository = translationRepository;
        this.tagRepository = tagRepository;
        this.translationMapper = translationMapper;
    }

    @CacheEvict(value = {"translations", "export"}, allEntries = true)
    public TranslationDto createTranslation(TranslationDto translationDto) {
        logger.debug("Creating translation with key: {} and locale: {}", 
                    translationDto.getKey(), translationDto.getLocale());

        if (translationRepository.existsByKeyAndLocale(translationDto.getKey(), translationDto.getLocale())) {
            throw new DuplicateResourceException(
                String.format("Translation with key '%s' and locale '%s' already exists", 
                            translationDto.getKey(), translationDto.getLocale()));
        }

        Translation translation = translationMapper.toEntity(translationDto);
        
        if (translationDto.getTags() != null && !translationDto.getTags().isEmpty()) {
            Set<Tag> tags = resolveOrCreateTags(translationDto.getTags().stream()
                    .map(tagDto -> tagDto.getName())
                    .collect(Collectors.toSet()));
            translation.setTags(tags);
        }

        Translation savedTranslation = translationRepository.save(translation);
        logger.info("Created translation with ID: {}", savedTranslation.getId());

        return translationMapper.toDto(savedTranslation);
    }

    @CacheEvict(value = {"translations", "export"}, allEntries = true)
    public TranslationDto updateTranslation(Long id, TranslationDto translationDto) {
        logger.debug("Updating translation with ID: {}", id);

        Translation existingTranslation = translationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Translation not found with ID: " + id));

        if (!existingTranslation.getKey().equals(translationDto.getKey()) ||
            !existingTranslation.getLocale().equals(translationDto.getLocale())) {
            if (translationRepository.existsByKeyAndLocale(translationDto.getKey(), translationDto.getLocale())) {
                throw new DuplicateResourceException(
                    String.format("Translation with key '%s' and locale '%s' already exists", 
                                translationDto.getKey(), translationDto.getLocale()));
            }
        }

        existingTranslation.setKey(translationDto.getKey());
        existingTranslation.setLocale(translationDto.getLocale());
        existingTranslation.setContent(translationDto.getContent());

        existingTranslation.getTags().clear();
        if (translationDto.getTags() != null && !translationDto.getTags().isEmpty()) {
            Set<Tag> tags = resolveOrCreateTags(translationDto.getTags().stream()
                    .map(tagDto -> tagDto.getName())
                    .collect(Collectors.toSet()));
            existingTranslation.setTags(tags);
        }

        Translation savedTranslation = translationRepository.save(existingTranslation);
        logger.info("Updated translation with ID: {}", savedTranslation.getId());

        return translationMapper.toDto(savedTranslation);
    }

    @Cacheable(value = "translations", key = "#id")
    @Transactional(readOnly = true)
    public TranslationDto getTranslationById(Long id) {
        logger.debug("Fetching translation with ID: {}", id);

        Translation translation = translationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Translation not found with ID: " + id));

        return translationMapper.toDto(translation);
    }

    @Cacheable(value = "translations", key = "#key + '_' + #locale")
    @Transactional(readOnly = true)
    public TranslationDto getTranslationByKeyAndLocale(String key, String locale) {
        logger.debug("Fetching translation with key: {} and locale: {}", key, locale);

        Translation translation = translationRepository.findByKeyAndLocale(key, locale)
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format("Translation not found with key '%s' and locale '%s'", key, locale)));

        return translationMapper.toDto(translation);
    }

    @Transactional(readOnly = true)
    public PagedResponse<TranslationDto> searchTranslations(TranslationSearchRequest request) {
        logger.debug("Searching translations with request: {}", request);

        Sort sort = Sort.by(
            "desc".equalsIgnoreCase(request.getSortDirection()) ? 
                Sort.Direction.DESC : Sort.Direction.ASC,
            request.getSortBy()
        );
        String keyPattern = request.getKey() != null && !request.getKey().isBlank() ? "%" + request.getKey() + "%" : null;
        String contentPattern = request.getContent() != null && !request.getContent().isBlank() ? "%" + request.getContent().toLowerCase() + "%" : null;


        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Translation> translationsPage = translationRepository.searchTranslations(
            keyPattern,
            request.getLocale(),
            contentPattern,
            request.getTagName(),
            pageable
        );

        List<TranslationDto> translationDtos = translationMapper.toDtoList(translationsPage.getContent());

        return new PagedResponse<>(
            translationDtos,
            translationsPage.getNumber(),
            translationsPage.getSize(),
            translationsPage.getTotalElements(),
            translationsPage.getTotalPages()
        );
    }


    @Transactional(readOnly = true)
    public PagedResponse<TranslationDto> getTranslationsByLocale(String locale, int page, int size) {
        logger.debug("Fetching translations for locale: {}", locale);

        Pageable pageable = PageRequest.of(page, size, Sort.by("key"));
        Page<Translation> translationsPage = translationRepository.findByLocale(locale, pageable);

        List<TranslationDto> translationDtos = translationMapper.toDtoList(translationsPage.getContent());

        return new PagedResponse<>(
            translationDtos,
            translationsPage.getNumber(),
            translationsPage.getSize(),
            translationsPage.getTotalElements(),
            translationsPage.getTotalPages()
        );
    }

    @Cacheable(value = "export", key = "#locale != null ? #locale : 'all'")
    @Transactional(readOnly = true)
    public TranslationExportResponse exportTranslations(String locale) {
        logger.debug("Exporting translations for locale: {}", locale);

        List<Translation> translations;
        if (locale != null && !locale.trim().isEmpty()) {
            translations = translationRepository.findByLocaleForExport(locale);
        } else {
            translations = translationRepository.findAllForExport();
        }

        if (translations.size() > maxExportSize) {
            logger.warn("Export size ({}) exceeds maximum allowed ({})", translations.size(), maxExportSize);
        }

        Map<String, Map<String, String>> translationMap = new HashMap<>();

        for (Translation translation : translations) {
            translationMap
                .computeIfAbsent(translation.getLocale(), k -> new HashMap<>())
                .put(translation.getKey(), translation.getContent());
        }

        TranslationExportResponse response = new TranslationExportResponse(translationMap);
        response.setCacheTtl(cacheTtl);

        if (cdnEnabled && cdnBaseUrl != null && !cdnBaseUrl.trim().isEmpty()) {
            String cdnUrl = cdnBaseUrl + "/translations/export.json";
            if (locale != null && !locale.trim().isEmpty()) {
                cdnUrl = cdnBaseUrl + "/translations/export_" + locale + ".json";
            }
            response.setCdnUrl(cdnUrl);
        }

        logger.info("Exported {} translations across {} locales", 
                   response.getTotalTranslations(), response.getLocales().size());

        return response;
    }

    @CacheEvict(value = {"translations", "export"}, allEntries = true)
    public void deleteTranslation(Long id) {
        logger.debug("Deleting translation with ID: {}", id);

        if (!translationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Translation not found with ID: " + id);
        }

        translationRepository.deleteById(id);
        logger.info("Deleted translation with ID: {}", id);
    }


    @Cacheable(value = "locales")
    @Transactional(readOnly = true)
    public List<String> getAvailableLocales() {
        logger.debug("Fetching available locales");
        return translationRepository.findDistinctLocales();
    }


    @Transactional(readOnly = true)
    public long getTranslationCountByLocale(String locale) {
        return translationRepository.countByLocale(locale);
    }

    private Set<Tag> resolveOrCreateTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag(tagName);
                        return tagRepository.save(newTag);
                    });
            tags.add(tag);
        }
        
        return tags;
    }
}
