package org.inventory.app.controller;

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
public class AttributeController {

    private final AttributeService attributeService;

    @GetMapping
    public ResponseEntity<List<AttributeDTO>> getAttributes() {
        return ResponseEntity.ok(attributeService.getAttributes());
    }
}
