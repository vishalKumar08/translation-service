package com.digitaltolk.translationservice.service;

import com.digitaltolk.translationservice.api.dto.PagedResponse;
import com.digitaltolk.translationservice.api.dto.TagDto;
import com.digitaltolk.translationservice.api.mapper.TagMapper;
import com.digitaltolk.translationservice.domain.entity.Tag;
import com.digitaltolk.translationservice.domain.repository.TagRepository;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagService tagService;

    private Tag tag;
    private TagDto tagDto;
    private List<Tag> tags;
    private List<TagDto> tagDtos;

    @BeforeEach
    void setUp() {
        tag = new Tag();
        tag.setId(1L);
        tag.setName("web");
        tag.setDescription("Web application");

        tagDto = new TagDto();
        tagDto.setId(1L);
        tagDto.setName("web");
        tagDto.setDescription("Web application");

        Tag tag2 = new Tag();
        tag2.setId(2L);
        tag2.setName("mobile");
        tag2.setDescription("Mobile application");

        TagDto tagDto2 = new TagDto();
        tagDto2.setId(2L);
        tagDto2.setName("mobile");
        tagDto2.setDescription("Mobile application");

        tags = Arrays.asList(tag, tag2);
        tagDtos = Arrays.asList(tagDto, tagDto2);
    }

    @Test
    void getAllTags_Success() {
        Page<Tag> page = new PageImpl<>(tags);
        when(tagRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(tagMapper.toDtoList(anyList())).thenReturn(tagDtos);

        PagedResponse<TagDto> result = tagService.getAllTags(0, 10, "name", "asc");

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getPage());
        assertEquals(1, result.getTotalPages());
        verify(tagRepository).findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name")));
        verify(tagMapper).toDtoList(tags);
    }

    @Test
    void getAllTags_WithDescendingSort() {
        Page<Tag> page = new PageImpl<>(tags);
        when(tagRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(tagMapper.toDtoList(anyList())).thenReturn(tagDtos);

        PagedResponse<TagDto> result = tagService.getAllTags(0, 10, "name", "desc");

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(tagRepository).findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name")));
        verify(tagMapper).toDtoList(tags);
    }

    @Test
    void getAllTags_WithInvalidSortDirection() {
        Page<Tag> page = new PageImpl<>(tags);
        when(tagRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(tagMapper.toDtoList(anyList())).thenReturn(tagDtos);

        PagedResponse<TagDto> result = tagService.getAllTags(0, 10, "name", "invalid");

        assertNotNull(result);
        verify(tagRepository).findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name")));
    }

    @Test
    void searchTags_Success() {
        Page<Tag> page = new PageImpl<>(tags);
        when(tagRepository.findByNameContainingIgnoreCase(anyString(), any(Pageable.class))).thenReturn(page);
        when(tagMapper.toDtoList(anyList())).thenReturn(tagDtos);

        PagedResponse<TagDto> result = tagService.searchTags("web", 0, 10);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getPage());
        assertEquals(1, result.getTotalPages());
        verify(tagRepository).findByNameContainingIgnoreCase("web", PageRequest.of(0, 10, Sort.by("name")));
        verify(tagMapper).toDtoList(tags);
    }

    @Test
    void getTagsByTranslationKey_Success() {
        when(tagRepository.findByTranslationKey(anyString())).thenReturn(tags);
        when(tagMapper.toDtoList(anyList())).thenReturn(tagDtos);

        List<TagDto> result = tagService.getTagsByTranslationKey("app.title");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("web", result.get(0).getName());
        assertEquals("mobile", result.get(1).getName());
        verify(tagRepository).findByTranslationKey("app.title");
        verify(tagMapper).toDtoList(tags);
    }

    @Test
    void getTagsByTranslationKey_EmptyResult() {
        when(tagRepository.findByTranslationKey(anyString())).thenReturn(Arrays.asList());
        when(tagMapper.toDtoList(anyList())).thenReturn(Arrays.asList());

        List<TagDto> result = tagService.getTagsByTranslationKey("nonexistent.key");

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(tagRepository).findByTranslationKey("nonexistent.key");
        verify(tagMapper).toDtoList(Arrays.asList());
    }
}
