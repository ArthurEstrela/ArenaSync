package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.Entities.Organizer;
import com.ajs.arenasync.Services.OrganizerService;

@RestController
@RequestMapping("/organizers")
public class OrganizerController {

    @Autowired
    private OrganizerService organizerService;

    // Criar organizador
    @PostMapping
    public ResponseEntity<Organizer> create(@RequestBody Organizer organizer) {
        Organizer created = organizerService.createOrganizer(organizer);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Buscar organizador por ID
    @GetMapping("/{id}")
    public ResponseEntity<Organizer> getById(@PathVariable Long id) {
        Organizer organizer = organizerService.getOrganizerById(id);
        return ResponseEntity.ok(organizer);
    }

    // Listar todos os organizadores
    @GetMapping
    public ResponseEntity<List<Organizer>> getAll() {
        List<Organizer> organizers = organizerService.getAllOrganizers();
        return ResponseEntity.ok(organizers);
    }

    // Atualizar organizador
    @PutMapping("/{id}")
    public ResponseEntity<Organizer> update(@PathVariable Long id, @RequestBody Organizer updatedOrganizer) {
        Organizer updated = organizerService.updateOrganizer(id, updatedOrganizer);
        return ResponseEntity.ok(updated);
    }

    // Deletar organizador
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        organizerService.deleteOrganizer(id);
        return ResponseEntity.noContent().build();
    }
}
