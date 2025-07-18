package com.digitaltolk.translationservice.api.controller;

import com.digitaltolk.translationservice.api.dto.PagedResponse;
import com.digitaltolk.translationservice.api.dto.TranslationDto;
import com.digitaltolk.translationservice.api.dto.TranslationExportResponse;
import com.digitaltolk.translationservice.api.dto.TranslationSearchRequest;
import com.digitaltolk.translationservice.service.TranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/translations")
@Validated
@Tag(name = "Translation Management", description = "APIs for managing translations")
@SecurityRequirement(name = "bearerAuth")
public class TranslationController {

    private static final Logger logger = LoggerFactory.getLogger(TranslationController.class);

    private final TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Operation(summary = "Create a new translation", description = "Creates a new translation with the provided key, locale, and content")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Translation created successfully",
                    content = @Content(schema = @Schema(implementation = TranslationDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Translation with same key and locale already exists"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    public ResponseEntity<TranslationDto> createTranslation(@Valid @RequestBody TranslationDto translationDto) {
        logger.info("Creating translation with key: {} and locale: {}", 
                   translationDto.getKey(), translationDto.getLocale());
        
        TranslationDto createdTranslation = translationService.createTranslation(translationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTranslation);
    }

    @Operation(summary = "Update an existing translation", description = "Updates an existing translation by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Translation updated successfully",
                    content = @Content(schema = @Schema(implementation = TranslationDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Translation not found"),
        @ApiResponse(responseCode = "409", description = "Translation with same key and locale already exists"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    public ResponseEntity<TranslationDto> updateTranslation(
            @Parameter(description = "Translation ID") @PathVariable Long id,
            @Valid @RequestBody TranslationDto translationDto) {
        logger.info("Updating translation with ID: {}", id);
        
        TranslationDto updatedTranslation = translationService.updateTranslation(id, translationDto);
        return ResponseEntity.ok(updatedTranslation);
    }

    @Operation(summary = "Get translation by ID", description = "Retrieves a translation by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Translation found",
                    content = @Content(schema = @Schema(implementation = TranslationDto.class))),
        @ApiResponse(responseCode = "404", description = "Translation not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR') or hasRole('VIEWER')")
    public ResponseEntity<TranslationDto> getTranslationById(
            @Parameter(description = "Translation ID") @PathVariable Long id) {
        logger.debug("Fetching translation with ID: {}", id);
        
        TranslationDto translation = translationService.getTranslationById(id);
        return ResponseEntity.ok(translation);
    }

    @Operation(summary = "Get translation by key and locale", description = "Retrieves a translation by its key and locale")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Translation found",
                    content = @Content(schema = @Schema(implementation = TranslationDto.class))),
        @ApiResponse(responseCode = "404", description = "Translation not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/key/{key}/locale/{locale}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR') or hasRole('VIEWER')")
    public ResponseEntity<TranslationDto> getTranslationByKeyAndLocale(
            @Parameter(description = "Translation key") @PathVariable @Size(max = 500) String key,
            @Parameter(description = "Locale code") @PathVariable @Size(max = 10) String locale) {
        logger.debug("Fetching translation with key: {} and locale: {}", key, locale);
        
        TranslationDto translation = translationService.getTranslationByKeyAndLocale(key, locale);
        return ResponseEntity.ok(translation);
    }

    @Operation(summary = "Search translations", description = "Search translations with various filters and pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR') or hasRole('VIEWER')")
    public ResponseEntity<PagedResponse<TranslationDto>> searchTranslations(
            @Parameter(description = "Translation key pattern") @RequestParam(required = false) @Size(max = 500) String key,
            @Parameter(description = "Locale filter") @RequestParam(required = false) @Size(max = 10) String locale,
            @Parameter(description = "Content search term") @RequestParam(required = false) @Size(max = 1000) String content,
            @Parameter(description = "Tag name filter") @RequestParam(required = false) @Size(max = 100) String tagName,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") @Min(1) int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "updatedAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDirection) {
        
        TranslationSearchRequest searchRequest = new TranslationSearchRequest();
        searchRequest.setKey(key);
        searchRequest.setLocale(locale);
        searchRequest.setContent(content);
        searchRequest.setTagName(tagName);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);
        
        logger.debug("Searching translations with filters - key: {}, locale: {}, content: {}, tag: {}", 
                    key, locale, content, tagName);
        
        PagedResponse<TranslationDto> result = translationService.searchTranslations(searchRequest);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get translations by locale", description = "Retrieves all translations for a specific locale with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Translations retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/locale/{locale}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR') or hasRole('VIEWER')")
    public ResponseEntity<PagedResponse<TranslationDto>> getTranslationsByLocale(
            @Parameter(description = "Locale code") @PathVariable @Size(max = 10) String locale,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") @Min(1) int size) {
        logger.debug("Fetching translations for locale: {}", locale);
        
        PagedResponse<TranslationDto> result = translationService.getTranslationsByLocale(locale, page, size);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Export translations", description = "Export translations in JSON format for frontend consumption")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Export completed successfully",
                    content = @Content(schema = @Schema(implementation = TranslationExportResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/export")
    public ResponseEntity<TranslationExportResponse> exportTranslations(
            @Parameter(description = "Locale filter (optional)") @RequestParam(required = false) @Size(max = 10) String locale) {
        logger.info("Exporting translations for locale: {}", locale != null ? locale : "all");
        
        TranslationExportResponse export = translationService.exportTranslations(locale);
        return ResponseEntity.ok(export);
    }

    @Operation(summary = "Delete translation", description = "Deletes a translation by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Translation deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Translation not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTranslation(
            @Parameter(description = "Translation ID") @PathVariable Long id) {
        logger.info("Deleting translation with ID: {}", id);
        
        translationService.deleteTranslation(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get available locales", description = "Retrieves all available locales in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Locales retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/locales")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR') or hasRole('VIEWER')")
    public ResponseEntity<List<String>> getAvailableLocales() {
        logger.debug("Fetching available locales");
        
        List<String> locales = translationService.getAvailableLocales();
        return ResponseEntity.ok(locales);
    }

    @Operation(summary = "Get translation count by locale", description = "Gets the number of translations for a specific locale")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/locale/{locale}/count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR') or hasRole('VIEWER')")
    public ResponseEntity<Long> getTranslationCountByLocale(
            @Parameter(description = "Locale code") @PathVariable @Size(max = 10) String locale) {
        logger.debug("Getting translation count for locale: {}", locale);
        
        long count = translationService.getTranslationCountByLocale(locale);
        return ResponseEntity.ok(count);
    }
}
