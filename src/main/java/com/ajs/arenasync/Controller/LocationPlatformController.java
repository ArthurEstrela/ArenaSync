package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.LocationPlatformRequestDTO;
import com.ajs.arenasync.DTO.LocationPlatformResponseDTO;
import com.ajs.arenasync.Services.LocationPlatformService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/location-platforms")
public class LocationPlatformController {

    @Autowired
    private LocationPlatformService locationPlatformService;

    // Criar nova LocationPlatform
    @PostMapping
    public ResponseEntity<LocationPlatformResponseDTO> create(@RequestBody @Valid LocationPlatformRequestDTO dto) {
        LocationPlatformResponseDTO created = locationPlatformService.create(dto);
        return ResponseEntity.ok(created);
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<LocationPlatformResponseDTO> findById(@PathVariable Long id) {
        LocationPlatformResponseDTO response = locationPlatformService.findById(id);
        return ResponseEntity.ok(response);
    }

    // Listar todos
    @GetMapping
    public ResponseEntity<List<LocationPlatformResponseDTO>> findAll() {
        List<LocationPlatformResponseDTO> list = locationPlatformService.findAll();
        return ResponseEntity.ok(list);
    }

    // Atualizar por ID
    @PutMapping("/{id}")
    public ResponseEntity<LocationPlatformResponseDTO> update(@PathVariable Long id,
            @RequestBody @Valid LocationPlatformRequestDTO dto) {
        LocationPlatformResponseDTO updated = locationPlatformService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Deletar por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        locationPlatformService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
