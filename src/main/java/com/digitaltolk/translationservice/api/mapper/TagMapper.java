package com.digitaltolk.translationservice.api.mapper;

import com.digitaltolk.translationservice.api.dto.TagDto;
import com.digitaltolk.translationservice.domain.entity.Tag;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TagMapper {

    public TagDto toDto(Tag tag) {
        if (tag == null) {
            return null;
        }

        TagDto dto = new TagDto();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setDescription(tag.getDescription());
        dto.setCreatedAt(tag.getCreatedAt());
        dto.setUpdatedAt(tag.getUpdatedAt());

        if (tag.getTranslations() != null) {
            dto.setTranslationCount((long) tag.getTranslations().size());
        }

        return dto;
    }

    public Tag toEntity(TagDto dto) {
        if (dto == null) {
            return null;
        }

        Tag tag = new Tag();
        tag.setId(dto.getId());
        tag.setName(dto.getName());
        tag.setDescription(dto.getDescription());

        return tag;
    }

    public void updateEntity(Tag tag, TagDto dto) {
        if (tag == null || dto == null) {
            return;
        }

        tag.setName(dto.getName());
        tag.setDescription(dto.getDescription());
    }

    public List<TagDto> toDtoList(List<Tag> tags) {
        if (tags == null) {
            return null;
        }

        return tags.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    public Set<TagDto> toDtoSet(Set<Tag> tags) {
        if (tags == null) {
            return null;
        }

        return tags.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }

    public List<Tag> toEntityList(List<TagDto> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public Set<Tag> toEntitySet(Set<TagDto> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toSet());
    }

    public TagDto toDtoWithCount(Object[] result) {
        if (result == null || result.length < 2) {
            return null;
        }

        Tag tag = (Tag) result[0];
        Long count = (Long) result[1];

        TagDto dto = toDto(tag);
        dto.setTranslationCount(count);

        return dto;
    }

    public List<TagDto> toDtoListWithCounts(List<Object[]> results) {
        if (results == null) {
            return null;
        }

        return results.stream()
                .map(this::toDtoWithCount)
                .collect(Collectors.toList());
    }
}
