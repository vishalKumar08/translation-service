package com.digitaltolk.translationservice.api.mapper;

import com.digitaltolk.translationservice.api.dto.TranslationDto;
import com.digitaltolk.translationservice.domain.entity.Translation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TranslationMapper {

    private final TagMapper tagMapper;

    public TranslationMapper(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    public TranslationDto toDto(Translation translation) {
        if (translation == null) {
            return null;
        }

        TranslationDto dto = new TranslationDto();
        dto.setId(translation.getId());
        dto.setKey(translation.getKey());
        dto.setLocale(translation.getLocale());
        dto.setContent(translation.getContent());
        dto.setCreatedAt(translation.getCreatedAt());
        dto.setUpdatedAt(translation.getUpdatedAt());
        dto.setVersion(translation.getVersion());

        if (translation.getTags() != null) {
            dto.setTags(translation.getTags().stream()
                    .map(tagMapper::toDto)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    public Translation toEntity(TranslationDto dto) {
        if (dto == null) {
            return null;
        }

        Translation translation = new Translation();
        translation.setId(dto.getId());
        translation.setKey(dto.getKey());
        translation.setLocale(dto.getLocale());
        translation.setContent(dto.getContent());
        translation.setVersion(dto.getVersion());

        if (dto.getTags() != null) {
            translation.setTags(dto.getTags().stream()
                    .map(tagMapper::toEntity)
                    .collect(Collectors.toSet()));
        }

        return translation;
    }

    public void updateEntity(Translation translation, TranslationDto dto) {
        if (translation == null || dto == null) {
            return;
        }

        translation.setKey(dto.getKey());
        translation.setLocale(dto.getLocale());
        translation.setContent(dto.getContent());

        if (dto.getTags() != null) {
            translation.getTags().clear();
            translation.getTags().addAll(dto.getTags().stream()
                    .map(tagMapper::toEntity)
                    .collect(Collectors.toSet()));
        }
    }

    public List<TranslationDto> toDtoList(List<Translation> translations) {
        if (translations == null) {
            return null;
        }

        return translations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Translation> toEntityList(List<TranslationDto> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public TranslationDto toMinimalDto(Translation translation) {
        if (translation == null) {
            return null;
        }

        TranslationDto dto = new TranslationDto();
        dto.setKey(translation.getKey());
        dto.setLocale(translation.getLocale());
        dto.setContent(translation.getContent());

        return dto;
    }
}
