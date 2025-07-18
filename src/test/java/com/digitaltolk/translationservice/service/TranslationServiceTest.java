package com.digitaltolk.translationservice.service;

import com.digitaltolk.translationservice.api.dto.TranslationDto;
import com.digitaltolk.translationservice.api.mapper.TranslationMapper;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @BeforeEach
    void setUp() {
        translationDto = new TranslationDto();
        translationDto.setKey("test.key");
        translationDto.setLocale("en");
        translationDto.setContent("Test Content");

        translation = new Translation();
        translation.setId(1L);
        translation.setKey("test.key");
        translation.setLocale("en");
        translation.setContent("Test Content");
    }

    @Test
    void createTranslation_Success() {
        // Given
        when(translationRepository.existsByKeyAndLocale(anyString(), anyString())).thenReturn(false);
        when(translationMapper.toEntity(any(TranslationDto.class))).thenReturn(translation);
        when(translationRepository.save(any(Translation.class))).thenReturn(translation);
        when(translationMapper.toDto(any(Translation.class))).thenReturn(translationDto);

        // When
        TranslationDto result = translationService.createTranslation(translationDto);

        // Then
        assertNotNull(result);
        assertEquals("test.key", result.getKey());
        assertEquals("en", result.getLocale());
        assertEquals("Test Content", result.getContent());

        verify(translationRepository).existsByKeyAndLocale("test.key", "en");
        verify(translationRepository).save(any(Translation.class));
        verify(translationMapper).toEntity(translationDto);
        verify(translationMapper).toDto(translation);
    }

    @Test
    void createTranslation_DuplicateKey_ThrowsException() {
        // Given
        when(translationRepository.existsByKeyAndLocale(anyString(), anyString())).thenReturn(true);

        // When & Then
        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> translationService.createTranslation(translationDto)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(translationRepository).existsByKeyAndLocale("test.key", "en");
        verify(translationRepository, never()).save(any(Translation.class));
    }

    @Test
    void getTranslationById_Success() {
        // Given
        Long translationId = 1L;
        when(translationRepository.findById(translationId)).thenReturn(Optional.of(translation));
        when(translationMapper.toDto(any(Translation.class))).thenReturn(translationDto);

        // When
        TranslationDto result = translationService.getTranslationById(translationId);

        // Then
        assertNotNull(result);
        assertEquals("test.key", result.getKey());
        verify(translationRepository).findById(translationId);
        verify(translationMapper).toDto(translation);
    }

    @Test
    void getTranslationById_NotFound_ThrowsException() {
        // Given
        Long translationId = 999L;
        when(translationRepository.findById(translationId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> translationService.getTranslationById(translationId)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(translationRepository).findById(translationId);
        verify(translationMapper, never()).toDto(any(Translation.class));
    }

    @Test
    void updateTranslation_Success() {
        // Given
        Long translationId = 1L;
        TranslationDto updateDto = new TranslationDto();
        updateDto.setKey("updated.key");
        updateDto.setLocale("en");
        updateDto.setContent("Updated Content");

        Translation existingTranslation = new Translation();
        existingTranslation.setId(translationId);
        existingTranslation.setKey("test.key");
        existingTranslation.setLocale("en");
        existingTranslation.setContent("Test Content");

        Translation updatedTranslation = new Translation();
        updatedTranslation.setId(translationId);
        updatedTranslation.setKey("updated.key");
        updatedTranslation.setLocale("en");
        updatedTranslation.setContent("Updated Content");

        when(translationRepository.findById(translationId)).thenReturn(Optional.of(existingTranslation));
        when(translationRepository.existsByKeyAndLocale("updated.key", "en")).thenReturn(false);
        when(translationRepository.save(any(Translation.class))).thenReturn(updatedTranslation);
        when(translationMapper.toDto(any(Translation.class))).thenReturn(updateDto);

        // When
        TranslationDto result = translationService.updateTranslation(translationId, updateDto);

        // Then
        assertNotNull(result);
        assertEquals("updated.key", result.getKey());
        assertEquals("Updated Content", result.getContent());

        verify(translationRepository).findById(translationId);
        verify(translationRepository).existsByKeyAndLocale("updated.key", "en");
        verify(translationRepository).save(any(Translation.class));
    }

    @Test
    void deleteTranslation_Success() {
        // Given
        Long translationId = 1L;
        when(translationRepository.existsById(translationId)).thenReturn(true);

        // When
        translationService.deleteTranslation(translationId);

        // Then
        verify(translationRepository).existsById(translationId);
        verify(translationRepository).deleteById(translationId);
    }

    @Test
    void deleteTranslation_NotFound_ThrowsException() {
        // Given
        Long translationId = 999L;
        when(translationRepository.existsById(translationId)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> translationService.deleteTranslation(translationId)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(translationRepository).existsById(translationId);
        verify(translationRepository, never()).deleteById(any());
    }

    @Test
    void getTranslationByKeyAndLocale_Success() {
        // Given
        String key = "test.key";
        String locale = "en";
        when(translationRepository.findByKeyAndLocale(key, locale)).thenReturn(Optional.of(translation));
        when(translationMapper.toDto(any(Translation.class))).thenReturn(translationDto);

        // When
        TranslationDto result = translationService.getTranslationByKeyAndLocale(key, locale);

        // Then
        assertNotNull(result);
        assertEquals(key, result.getKey());
        assertEquals(locale, result.getLocale());
        verify(translationRepository).findByKeyAndLocale(key, locale);
        verify(translationMapper).toDto(translation);
    }

    @Test
    void getTranslationByKeyAndLocale_NotFound_ThrowsException() {
        // Given
        String key = "nonexistent.key";
        String locale = "en";
        when(translationRepository.findByKeyAndLocale(key, locale)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> translationService.getTranslationByKeyAndLocale(key, locale)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(translationRepository).findByKeyAndLocale(key, locale);
        verify(translationMapper, never()).toDto(any(Translation.class));
    }
}
