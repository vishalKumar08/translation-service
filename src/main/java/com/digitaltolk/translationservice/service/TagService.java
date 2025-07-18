package com.digitaltolk.translationservice.service;

import com.digitaltolk.translationservice.api.dto.PagedResponse;
import com.digitaltolk.translationservice.api.dto.TagDto;
import com.digitaltolk.translationservice.api.mapper.TagMapper;
import com.digitaltolk.translationservice.domain.entity.Tag;
import com.digitaltolk.translationservice.domain.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TagService {

    private static final Logger logger = LoggerFactory.getLogger(TagService.class);

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public TagService(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    @Transactional(readOnly = true)
    public PagedResponse<TagDto> getAllTags(int page, int size, String sortBy, String sortDirection) {
        logger.debug("Fetching all tags - page: {}, size: {}", page, size);

        Sort sort = Sort.by(
            "desc".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.DESC : Sort.Direction.ASC,
            sortBy
        );

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Tag> tagsPage = tagRepository.findAll(pageable);

        List<TagDto> tagDtos = tagMapper.toDtoList(tagsPage.getContent());

        return new PagedResponse<>(
            tagDtos,
            tagsPage.getNumber(),
            tagsPage.getSize(),
            tagsPage.getTotalElements(),
            tagsPage.getTotalPages()
        );
    }


    @Transactional(readOnly = true)
    public PagedResponse<TagDto> searchTags(String namePattern, int page, int size) {
        logger.debug("Searching tags with pattern: {}", namePattern);

        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        Page<Tag> tagsPage = tagRepository.findByNameContainingIgnoreCase(namePattern, pageable);

        List<TagDto> tagDtos = tagMapper.toDtoList(tagsPage.getContent());

        return new PagedResponse<>(
            tagDtos,
            tagsPage.getNumber(),
            tagsPage.getSize(),
            tagsPage.getTotalElements(),
            tagsPage.getTotalPages()
        );
    }


    @Transactional(readOnly = true)
    public List<TagDto> getTagsByTranslationKey(String translationKey) {
        logger.debug("Fetching tags for translation key: {}", translationKey);

        List<Tag> tags = tagRepository.findByTranslationKey(translationKey);
        return tagMapper.toDtoList(tags);
    }

}
