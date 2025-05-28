package org.inventory.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.AttributeDTO;
import org.inventory.app.service.AttributeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/attributes")
@RequiredArgsConstructor
@Tag(name = "Attributes", description = "Operations related to product attributes")
public class AttributeController {

    private final AttributeService attributeService;

    @GetMapping
    @Operation(summary = "Get all attributes", description = "Retrieve a list of all available product attributes")
    public ResponseEntity<List<AttributeDTO>> getAttributes() {
        return ResponseEntity.ok(attributeService.getAttributes().getValue());
    }
}
