package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.OrganizerRequestDTO;
import com.ajs.arenasync.DTO.OrganizerResponseDTO;
import com.ajs.arenasync.Services.OrganizerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/organizers")
public class OrganizerController {

    @Autowired
    private OrganizerService organizerService;

    @GetMapping("/{id}")
    public ResponseEntity<OrganizerResponseDTO> getOrganizerById(@PathVariable Long id) {
        OrganizerResponseDTO organizer = organizerService.getOrganizerById(id);
        return ResponseEntity.ok(organizer);
    }

    @GetMapping
    public ResponseEntity<List<OrganizerResponseDTO>> getAllOrganizers() {
        List<OrganizerResponseDTO> organizers = organizerService.getAllOrganizers();
        return ResponseEntity.ok(organizers);
    }

    @PostMapping
    public ResponseEntity<OrganizerResponseDTO> createOrganizer(
            @Valid @RequestBody OrganizerRequestDTO dto) {
        OrganizerResponseDTO created = organizerService.createOrganizer(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizerResponseDTO> updateOrganizer(
            @PathVariable Long id,
            @Valid @RequestBody OrganizerRequestDTO dto) {
        OrganizerResponseDTO updated = organizerService.updateOrganizer(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganizer(@PathVariable Long id) {
        organizerService.deleteOrganizer(id);
        return ResponseEntity.noContent().build();
    }
}
