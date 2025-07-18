package com.digitaltolk.translationservice.api.controller;

import com.digitaltolk.translationservice.api.dto.PagedResponse;
import com.digitaltolk.translationservice.api.dto.TagDto;
import com.digitaltolk.translationservice.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@Validated
@Tag(name = "Tag Information", description = "Read-only APIs for accessing translation tag information")
@SecurityRequirement(name = "bearerAuth")
public class TagController {

    private static final Logger logger = LoggerFactory.getLogger(TagController.class);

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @Operation(summary = "Get all available tags", description = "Retrieves all available tags for use in translation management")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tags retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR') or hasRole('VIEWER')")
    public ResponseEntity<PagedResponse<TagDto>> getAllTags(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") @Min(1) int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDirection) {
        logger.debug("Fetching all tags - page: {}, size: {}", page, size);
        
        PagedResponse<TagDto> result = tagService.getAllTags(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Search tags by name", description = "Search tags by name pattern for autocomplete and selection")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR') or hasRole('VIEWER')")
    public ResponseEntity<PagedResponse<TagDto>> searchTags(
            @Parameter(description = "Name pattern to search for") @RequestParam @Size(max = 100) String namePattern,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") @Min(1) int size) {
        logger.debug("Searching tags with pattern: {}", namePattern);
        
        PagedResponse<TagDto> result = tagService.searchTags(namePattern, page, size);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get tags by translation key", description = "Retrieves all tags associated with a specific translation key")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tags retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/translation-key/{translationKey}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR') or hasRole('VIEWER')")
    public ResponseEntity<List<TagDto>> getTagsByTranslationKey(
            @Parameter(description = "Translation key") @PathVariable @Size(max = 500) String translationKey) {
        logger.debug("Fetching tags for translation key: {}", translationKey);
        
        List<TagDto> tags = tagService.getTagsByTranslationKey(translationKey);
        return ResponseEntity.ok(tags);
    }

}
