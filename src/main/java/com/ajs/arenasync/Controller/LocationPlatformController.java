package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.Entities.LocationPlatform;
import com.ajs.arenasync.Services.LocationPlatformService;

@RestController
@RequestMapping("/locations")
public class LocationPlatformController {

    @Autowired
    private LocationPlatformService locationPlatformService;

    // Criar local ou plataforma
    @PostMapping
    public ResponseEntity<LocationPlatform> create(@RequestBody LocationPlatform locationPlatform) {
        LocationPlatform created = locationPlatformService.create(locationPlatform);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<LocationPlatform> getById(@PathVariable Long id) {
        LocationPlatform location = locationPlatformService.findById(id);
        return ResponseEntity.ok(location);
    }

    // Listar todos
    @GetMapping
    public ResponseEntity<List<LocationPlatform>> getAll() {
        return ResponseEntity.ok(locationPlatformService.findAll());
    }

    // Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<LocationPlatform> update(@PathVariable Long id, @RequestBody LocationPlatform updatedData) {
        LocationPlatform updated = locationPlatformService.update(id, updatedData);
        return ResponseEntity.ok(updated);
    }

    // Deletar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        locationPlatformService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
