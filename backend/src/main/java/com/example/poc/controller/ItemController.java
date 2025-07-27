package com.example.poc.controller;

import com.example.poc.model.Item;
import com.example.poc.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:4200") // Allow Angular frontend
@Tag(name = "Items", description = "Item management operations")
public class ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    @Operation(summary = "Get all items", description = "Retrieve a paginated list of items with optional search functionality")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Items retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Item.class))),
        @ApiResponse(responseCode = "400", description = "Invalid parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Item>> getAllItems(
            @Parameter(description = "Search term to filter items by name or description")
            @RequestParam(required = false) String search,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        logger.info("GET /api/items - search: {}, page: {}, size: {}", search, page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        if (search != null && !search.trim().isEmpty()) {
            Page<Item> itemsPage = itemService.search(search, pageable);
            return ResponseEntity.ok()
                    .header("X-Total-Count", String.valueOf(itemsPage.getTotalElements()))
                    .header("X-Total-Pages", String.valueOf(itemsPage.getTotalPages()))
                    .body(itemsPage.getContent());
        } else {
            Page<Item> itemsPage = itemService.findAll(pageable);
            return ResponseEntity.ok()
                    .header("X-Total-Count", String.valueOf(itemsPage.getTotalElements()))
                    .header("X-Total-Pages", String.valueOf(itemsPage.getTotalPages()))
                    .body(itemsPage.getContent());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID", description = "Retrieve a specific item by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Item.class))),
        @ApiResponse(responseCode = "404", description = "Item not found"),
        @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    public ResponseEntity<Item> getItemById(
            @Parameter(description = "Unique identifier of the item", example = "1")
            @PathVariable Long id) {
        logger.info("GET /api/items/{}", id);
        Item item = itemService.findById(id);
        return ResponseEntity.ok(item);
    }

    @PostMapping
    @Operation(summary = "Create new item", description = "Create a new item with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Item created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Item.class))),
        @ApiResponse(responseCode = "400", description = "Invalid item data"),
        @ApiResponse(responseCode = "409", description = "Item already exists")
    })
    public ResponseEntity<Item> createItem(
            @Parameter(description = "Item data to create", required = true)
            @Valid @RequestBody Item item) {
        logger.info("POST /api/items - Creating item: {}", item.getName());
        Item createdItem = itemService.save(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update item", description = "Update an existing item with new information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Item.class))),
        @ApiResponse(responseCode = "404", description = "Item not found"),
        @ApiResponse(responseCode = "400", description = "Invalid item data")
    })
    public ResponseEntity<Item> updateItem(
            @Parameter(description = "Unique identifier of the item to update", example = "1")
            @PathVariable Long id, 
            @Parameter(description = "Updated item data", required = true)
            @Valid @RequestBody Item item) {
        logger.info("PUT /api/items/{} - Updating item", id);
        Item updatedItem = itemService.update(id, item);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item", description = "Delete an item by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Item deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Item not found"),
        @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    public ResponseEntity<Void> deleteItem(
            @Parameter(description = "Unique identifier of the item to delete", example = "1")
            @PathVariable Long id) {
        logger.info("DELETE /api/items/{}", id);
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search items", description = "Search for items using a search term")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Item.class))),
        @ApiResponse(responseCode = "400", description = "Invalid search term")
    })
    public ResponseEntity<List<Item>> searchItems(
            @Parameter(description = "Search term to find items", example = "laptop", required = true)
            @RequestParam String term) {
        logger.info("GET /api/items/search - term: {}", term);
        List<Item> items = itemService.search(term);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the item service is running properly")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service is healthy",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Item service is healthy")))
    })
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Item service is healthy");
    }
}