package com.digitaltolk.translationservice.service;

import com.digitaltolk.translationservice.api.dto.PagedResponse;
import com.digitaltolk.translationservice.api.dto.TagDto;
import com.digitaltolk.translationservice.api.dto.TranslationDto;
import com.digitaltolk.translationservice.api.dto.TranslationExportResponse;
import com.digitaltolk.translationservice.api.dto.TranslationSearchRequest;
import com.digitaltolk.translationservice.api.mapper.TranslationMapper;
import com.digitaltolk.translationservice.domain.entity.Tag;
import com.digitaltolk.translationservice.domain.entity.Translation;
import com.digitaltolk.translationservice.domain.repository.TagRepository;
import com.digitaltolk.translationservice.domain.repository.TranslationRepository;
import com.digitaltolk.translationservice.exception.DuplicateResourceException;
import com.digitaltolk.translationservice.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class TranslationServiceTest {

    @Mock
    private TranslationRepository translationRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TranslationMapper translationMapper;

    @InjectMocks
    private TranslationService translationService;

    private TranslationDto translationDto;
    private Translation translation;
    private Tag tag;
    private TagDto tagDto;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(translationService, "maxExportSize", 100000);
        ReflectionTestUtils.setField(translationService, "cacheTtl", 300L);
        ReflectionTestUtils.setField(translationService, "cdnEnabled", false);
        ReflectionTestUtils.setField(translationService, "cdnBaseUrl", "");

        tag = new Tag();
        tag.setId(1L);
        tag.setName("web");

        tagDto = new TagDto();
        tagDto.setId(1L);
        tagDto.setName("web");

        translationDto = new TranslationDto();
        translationDto.setKey("test.key");
        translationDto.setLocale("en");
        translationDto.setContent("Test Content");
        translationDto.setTags(Set.of(tagDto));

        translation = new Translation();
        translation.setId(1L);
        translation.setKey("test.key");
        translation.setLocale("en");
        translation.setContent("Test Content");
        translation.setTags(Set.of(tag));
    }

    @Test
    void createTranslation_Success() {
        when(translationRepository.existsByKeyAndLocale(anyString(), anyString())).thenReturn(false);
        when(translationMapper.toEntity(any(TranslationDto.class))).thenReturn(translation);
        when(tagRepository.findByName(anyString())).thenReturn(Optional.of(tag));
        when(translationRepository.save(any(Translation.class))).thenReturn(translation);
        when(translationMapper.toDto(any(Translation.class))).thenReturn(translationDto);

        TranslationDto result = translationService.createTranslation(translationDto);

        assertNotNull(result);
        assertEquals("test.key", result.getKey());
        verify(translationRepository).existsByKeyAndLocale("test.key", "en");
        verify(translationRepository).save(any(Translation.class));
    }

    @Test
    void createTranslation_WithNewTag_Success() {
        when(translationRepository.existsByKeyAndLocale(anyString(), anyString())).thenReturn(false);
        when(translationMapper.toEntity(any(TranslationDto.class))).thenReturn(translation);
        when(tagRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        when(translationRepository.save(any(Translation.class))).thenReturn(translation);
        when(translationMapper.toDto(any(Translation.class))).thenReturn(translationDto);

        TranslationDto result = translationService.createTranslation(translationDto);

        assertNotNull(result);
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void createTranslation_WithoutTags_Success() {
        translationDto.setTags(null);
        when(translationRepository.existsByKeyAndLocale(anyString(), anyString())).thenReturn(false);
        when(translationMapper.toEntity(any(TranslationDto.class))).thenReturn(translation);
        when(translationRepository.save(any(Translation.class))).thenReturn(translation);
        when(translationMapper.toDto(any(Translation.class))).thenReturn(translationDto);

        TranslationDto result = translationService.createTranslation(translationDto);

        assertNotNull(result);
        verify(tagRepository, never()).findByName(anyString());
    }

    @Test
    void createTranslation_WithEmptyTags_Success() {
        translationDto.setTags(Collections.emptySet());
        when(translationRepository.existsByKeyAndLocale(anyString(), anyString())).thenReturn(false);
        when(translationMapper.toEntity(any(TranslationDto.class))).thenReturn(translation);
        when(translationRepository.save(any(Translation.class))).thenReturn(translation);
        when(translationMapper.toDto(any(Translation.class))).thenReturn(translationDto);

        TranslationDto result = translationService.createTranslation(translationDto);

        assertNotNull(result);
        verify(tagRepository, never()).findByName(anyString());
    }

    @Test
    void createTranslation_DuplicateKey_ThrowsException() {
        when(translationRepository.existsByKeyAndLocale(anyString(), anyString())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> translationService.createTranslation(translationDto)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(translationRepository, never()).save(any(Translation.class));
    }



    @Test
    void updateTranslation_DifferentKeyExists_ThrowsException() {
        Long translationId = 1L;
        TranslationDto updateDto = new TranslationDto();
        updateDto.setKey("different.key");
        updateDto.setLocale("en");
        updateDto.setContent("Updated Content");

        when(translationRepository.findById(translationId)).thenReturn(Optional.of(translation));
        when(translationRepository.existsByKeyAndLocale("different.key", "en")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, 
            () -> translationService.updateTranslation(translationId, updateDto));
    }

    @Test
    void updateTranslation_NotFound_ThrowsException() {
        Long translationId = 999L;
        when(translationRepository.findById(translationId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> translationService.updateTranslation(translationId, translationDto));
    }

    @Test
    void getTranslationById_Success() {
        Long translationId = 1L;
        when(translationRepository.findById(translationId)).thenReturn(Optional.of(translation));
        when(translationMapper.toDto(any(Translation.class))).thenReturn(translationDto);

        TranslationDto result = translationService.getTranslationById(translationId);

        assertNotNull(result);
        assertEquals("test.key", result.getKey());
    }

    @Test
    void getTranslationById_NotFound_ThrowsException() {
        Long translationId = 999L;
        when(translationRepository.findById(translationId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> translationService.getTranslationById(translationId));
    }

    @Test
    void getTranslationByKeyAndLocale_Success() {
        String key = "test.key";
        String locale = "en";
        when(translationRepository.findByKeyAndLocale(key, locale)).thenReturn(Optional.of(translation));
        when(translationMapper.toDto(any(Translation.class))).thenReturn(translationDto);

        TranslationDto result = translationService.getTranslationByKeyAndLocale(key, locale);

        assertNotNull(result);
        assertEquals(key, result.getKey());
        assertEquals(locale, result.getLocale());
    }

    @Test
    void getTranslationByKeyAndLocale_NotFound_ThrowsException() {
        String key = "nonexistent.key";
        String locale = "en";
        when(translationRepository.findByKeyAndLocale(key, locale)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> translationService.getTranslationByKeyAndLocale(key, locale));
    }

    @Test
    void searchTranslations_Success() {
        TranslationSearchRequest request = new TranslationSearchRequest();
        request.setKey("test");
        request.setLocale("en");
        request.setContent("content");
        request.setTagName("web");
        request.setPage(0);
        request.setSize(10);
        request.setSortBy("key");
        request.setSortDirection("asc");

        Page<Translation> page = new PageImpl<>(Arrays.asList(translation));
        when(translationRepository.searchTranslations(anyString(), anyString(), anyString(), anyString(), any(Pageable.class)))
            .thenReturn(page);
        when(translationMapper.toDtoList(anyList())).thenReturn(Arrays.asList(translationDto));

        PagedResponse<TranslationDto> result = translationService.searchTranslations(request);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void searchTranslations_WithDescSort_Success() {
        TranslationSearchRequest request = new TranslationSearchRequest();
        request.setKey("test");
        request.setSortDirection("desc");
        request.setPage(0);
        request.setSize(10);
        request.setSortBy("key");

        Page<Translation> page = new PageImpl<>(Arrays.asList(translation));
        when(translationRepository.searchTranslations(anyString(), isNull(), isNull(), isNull(), any(Pageable.class)))
            .thenReturn(page);
        when(translationMapper.toDtoList(anyList())).thenReturn(Arrays.asList(translationDto));

        PagedResponse<TranslationDto> result = translationService.searchTranslations(request);

        assertNotNull(result);
        verify(translationRepository).searchTranslations(eq("%test%"), isNull(), isNull(), isNull(), 
            eq(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "key"))));
    }

    @Test
    void searchTranslations_WithBlankFields_Success() {
        TranslationSearchRequest request = new TranslationSearchRequest();
        request.setKey("");
        request.setContent("  ");
        request.setPage(0);
        request.setSize(10);
        request.setSortBy("key");
        request.setSortDirection("asc");

        Page<Translation> page = new PageImpl<>(Arrays.asList(translation));
        when(translationRepository.searchTranslations(isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
            .thenReturn(page);
        when(translationMapper.toDtoList(anyList())).thenReturn(Arrays.asList(translationDto));

        PagedResponse<TranslationDto> result = translationService.searchTranslations(request);

        assertNotNull(result);
        verify(translationRepository).searchTranslations(isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    void getTranslationsByLocale_Success() {
        String locale = "en";
        Page<Translation> page = new PageImpl<>(Arrays.asList(translation));
        when(translationRepository.findByLocale(eq(locale), any(Pageable.class))).thenReturn(page);
        when(translationMapper.toDtoList(anyList())).thenReturn(Arrays.asList(translationDto));

        PagedResponse<TranslationDto> result = translationService.getTranslationsByLocale(locale, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(translationRepository).findByLocale(eq(locale), eq(PageRequest.of(0, 10, Sort.by("key"))));
    }

    @Test
    void exportTranslations_WithLocale_Success() {
        String locale = "en";
        when(translationRepository.findByLocaleForExport(locale)).thenReturn(Arrays.asList(translation));

        TranslationExportResponse result = translationService.exportTranslations(locale);

        assertNotNull(result);
        assertEquals(1, result.getTotalTranslations());
        assertTrue(result.getTranslations().containsKey("en"));
        assertEquals("Test Content", result.getTranslations().get("en").get("test.key"));
        assertEquals(300L, result.getCacheTtl());
        assertNull(result.getCdnUrl());
    }

    @Test
    void exportTranslations_AllLocales_Success() {
        when(translationRepository.findAllForExport()).thenReturn(Arrays.asList(translation));

        TranslationExportResponse result = translationService.exportTranslations(null);

        assertNotNull(result);
        assertEquals(1, result.getTotalTranslations());
        verify(translationRepository).findAllForExport();
    }

    @Test
    void exportTranslations_EmptyLocale_Success() {
        when(translationRepository.findAllForExport()).thenReturn(Arrays.asList(translation));

        TranslationExportResponse result = translationService.exportTranslations("  ");

        assertNotNull(result);
        verify(translationRepository).findAllForExport();
    }

    @Test
    void exportTranslations_WithCdn_Success() {
        ReflectionTestUtils.setField(translationService, "cdnEnabled", true);
        ReflectionTestUtils.setField(translationService, "cdnBaseUrl", "https://cdn.example.com");

        String locale = "en";
        when(translationRepository.findByLocaleForExport(locale)).thenReturn(Arrays.asList(translation));

        TranslationExportResponse result = translationService.exportTranslations(locale);

        assertNotNull(result);
        assertEquals("https://cdn.example.com/translations/export_en.json", result.getCdnUrl());
    }

    @Test
    void exportTranslations_WithCdnAllLocales_Success() {
        ReflectionTestUtils.setField(translationService, "cdnEnabled", true);
        ReflectionTestUtils.setField(translationService, "cdnBaseUrl", "https://cdn.example.com");

        when(translationRepository.findAllForExport()).thenReturn(Arrays.asList(translation));

        TranslationExportResponse result = translationService.exportTranslations(null);

        assertNotNull(result);
        assertEquals("https://cdn.example.com/translations/export.json", result.getCdnUrl());
    }

    @Test
    void exportTranslations_ExceedsMaxSize_LogsWarning() {
        ReflectionTestUtils.setField(translationService, "maxExportSize", 0);
        when(translationRepository.findAllForExport()).thenReturn(Arrays.asList(translation));

        TranslationExportResponse result = translationService.exportTranslations(null);

        assertNotNull(result);
        assertEquals(1, result.getTotalTranslations());
    }

    @Test
    void deleteTranslation_Success() {
        Long translationId = 1L;
        when(translationRepository.existsById(translationId)).thenReturn(true);

        translationService.deleteTranslation(translationId);

        verify(translationRepository).deleteById(translationId);
    }

    @Test
    void deleteTranslation_NotFound_ThrowsException() {
        Long translationId = 999L;
        when(translationRepository.existsById(translationId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> translationService.deleteTranslation(translationId));
        verify(translationRepository, never()).deleteById(any());
    }

    @Test
    void getAvailableLocales_Success() {
        List<String> locales = Arrays.asList("en", "fr", "es");
        when(translationRepository.findDistinctLocales()).thenReturn(locales);

        List<String> result = translationService.getAvailableLocales();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("en"));
        assertTrue(result.contains("fr"));
        assertTrue(result.contains("es"));
    }

    @Test
    void getTranslationCountByLocale_Success() {
        String locale = "en";
        when(translationRepository.countByLocale(locale)).thenReturn(5L);

        long result = translationService.getTranslationCountByLocale(locale);

        assertEquals(5L, result);
        verify(translationRepository).countByLocale(locale);
    }
}
