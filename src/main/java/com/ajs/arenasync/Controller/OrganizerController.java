package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel; // Importe para coleções HATEOAS
import org.springframework.hateoas.Link; // Importe para Links HATEOAS
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Import estático para linkTo e methodOn

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.OrganizerRequestDTO;
import com.ajs.arenasync.DTO.OrganizerResponseDTO;
import com.ajs.arenasync.Services.OrganizerService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation; // Importe esta anotação
import io.swagger.v3.oas.annotations.tags.Tag; // Importe esta anotação
import io.swagger.v3.oas.annotations.Parameter; // Importe esta anotação para documentar PathVariable

@RestController
@RequestMapping("/api/organizers")
@Tag(name = "Organizer Management", description = "Operações para gerenciar organizadores de torneios") // Anotação na classe
public class OrganizerController {

    @Autowired
    private OrganizerService organizerService;

    @GetMapping("/{id}")
    @Operation(summary = "Obter organizador por ID", description = "Retorna um organizador específico com base no seu ID")
    public ResponseEntity<OrganizerResponseDTO> getOrganizerById(
            @Parameter(description = "ID do organizador a ser buscado", required = true) @PathVariable Long id) {
        OrganizerResponseDTO organizer = organizerService.getOrganizerById(id);
        organizer.add(linkTo(methodOn(OrganizerController.class).getOrganizerById(id)).withSelfRel());
        organizer.add(linkTo(methodOn(OrganizerController.class).getAllOrganizers()).withRel("all-organizers"));
        organizer.add(linkTo(methodOn(OrganizerController.class).updateOrganizer(id,  new OrganizerRequestDTO())).withRel("update"));
        organizer.add(linkTo(methodOn(OrganizerController.class).deleteOrganizer(id)).withRel("delete"));
        return ResponseEntity.ok(organizer);
    }

    @GetMapping
    @Operation(summary = "Listar todos os organizadores", description = "Retorna uma lista de todos os organizadores registrados")
    public ResponseEntity<CollectionModel<OrganizerResponseDTO>> getAllOrganizers() {
        // Assume que OrganizerService.getAllOrganizers() existe e retorna List<OrganizerResponseDTO>
        List<OrganizerResponseDTO> organizersList = organizerService.getAllOrganizers();
        for (OrganizerResponseDTO organizer : organizersList) {
            organizer.add(linkTo(methodOn(OrganizerController.class).getOrganizerById(organizer.getId())).withSelfRel());
            organizer.add(linkTo(methodOn(OrganizerController.class).updateOrganizer(organizer.getId(),  new OrganizerRequestDTO())).withRel("update"));
            organizer.add(linkTo(methodOn(OrganizerController.class).deleteOrganizer(organizer.getId())).withRel("delete"));
        }
        Link selfLink = linkTo(methodOn(OrganizerController.class).getAllOrganizers()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(organizersList, selfLink));
    }

    @PostMapping
    @Operation(summary = "Criar novo organizador", description = "Cria um novo organizador no sistema")
    public ResponseEntity<OrganizerResponseDTO> createOrganizer(
            @Valid @RequestBody OrganizerRequestDTO dto) {
        OrganizerResponseDTO created = organizerService.saveOrganizer(dto);
        created.add(linkTo(methodOn(OrganizerController.class).getOrganizerById(created.getId())).withSelfRel());
        created.add(linkTo(methodOn(OrganizerController.class).getAllOrganizers()).withRel("all-organizers"));
        created.add(linkTo(methodOn(OrganizerController.class).updateOrganizer(created.getId(),  new OrganizerRequestDTO())).withRel("update"));
        created.add(linkTo(methodOn(OrganizerController.class).deleteOrganizer(created.getId())).withRel("delete"));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar organizador existente", description = "Atualiza as informações de um organizador existente pelo seu ID")
    public ResponseEntity<OrganizerResponseDTO> updateOrganizer(
            @Parameter(description = "ID do organizador a ser atualizado", required = true) @PathVariable Long id,
            @Valid @RequestBody OrganizerRequestDTO dto) {
        OrganizerResponseDTO updated = organizerService.updateOrganizer(id, dto);
        updated.add(linkTo(methodOn(OrganizerController.class).getOrganizerById(id)).withSelfRel());
        updated.add(linkTo(methodOn(OrganizerController.class).getAllOrganizers()).withRel("all-organizers"));
        updated.add(linkTo(methodOn(OrganizerController.class).deleteOrganizer(id)).withRel("delete"));
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar organizador", description = "Deleta um organizador do sistema pelo seu ID. Não é possível deletar organizadores com torneios associados.")
    public ResponseEntity<Void> deleteOrganizer(
            @Parameter(description = "ID do organizador a ser deletado", required = true) @PathVariable Long id) {
        organizerService.deleteOrganizer(id);
        return ResponseEntity.noContent().build();
    }
}