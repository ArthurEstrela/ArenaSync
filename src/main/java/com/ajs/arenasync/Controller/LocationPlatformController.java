package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.LocationPlatformRequestDTO;
import com.ajs.arenasync.DTO.LocationPlatformResponseDTO;
import com.ajs.arenasync.Services.LocationPlatformService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/location-platforms")
@Tag(name = "Location/Platform Management", description = "Operações para gerenciar locais ou plataformas de jogo")
public class LocationPlatformController {

    @Autowired
    private LocationPlatformService locationPlatformService;

    @GetMapping("/{id}")
    @Operation(summary = "Obter local/plataforma por ID", description = "Retorna um local ou plataforma específico com base no seu ID")
    public ResponseEntity<LocationPlatformResponseDTO> findById(
            @Parameter(description = "ID do local/plataforma a ser buscado", required = true) @PathVariable Long id) {
        LocationPlatformResponseDTO locationPlatform = locationPlatformService.findById(id);
        locationPlatform.add(linkTo(methodOn(LocationPlatformController.class).findById(id)).withSelfRel());
        locationPlatform.add(linkTo(methodOn(LocationPlatformController.class).findAll()).withRel("all-locations-platforms"));
        locationPlatform.add(linkTo(methodOn(LocationPlatformController.class).update(id, new LocationPlatformRequestDTO())).withRel("update"));
        locationPlatform.add(linkTo(methodOn(LocationPlatformController.class).deleteById(id)).withRel("delete"));
        return ResponseEntity.ok(locationPlatform);
    }

    @GetMapping
    @Operation(summary = "Listar todos os locais/plataformas", description = "Retorna uma lista de todos os locais ou plataformas registrados")
    public ResponseEntity<CollectionModel<LocationPlatformResponseDTO>> findAll() {
        List<LocationPlatformResponseDTO> locationsPlatformsList = locationPlatformService.findAll();
        for (LocationPlatformResponseDTO locationPlatform : locationsPlatformsList) {
            locationPlatform.add(linkTo(methodOn(LocationPlatformController.class).findById(locationPlatform.getId())).withSelfRel());
            locationPlatform.add(linkTo(methodOn(LocationPlatformController.class).update(locationPlatform.getId(), new LocationPlatformRequestDTO())).withRel("update"));
            locationPlatform.add(linkTo(methodOn(LocationPlatformController.class).deleteById(locationPlatform.getId())).withRel("delete"));
        }
        Link selfLink = linkTo(methodOn(LocationPlatformController.class).findAll()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(locationsPlatformsList, selfLink));
    }

    @PostMapping
    @Operation(summary = "Criar novo local/plataforma", description = "Cria um novo local ou plataforma no sistema")
    public ResponseEntity<LocationPlatformResponseDTO> create(
            @Valid @RequestBody LocationPlatformRequestDTO dto) {
        LocationPlatformResponseDTO created = locationPlatformService.save(dto);
        if (created != null) {
            created.add(linkTo(methodOn(LocationPlatformController.class).findById(created.getId())).withSelfRel());
            created.add(linkTo(methodOn(LocationPlatformController.class).findAll()).withRel("all-locations-platforms"));
            created.add(linkTo(methodOn(LocationPlatformController.class).update(created.getId(), new LocationPlatformRequestDTO())).withRel("update"));
            created.add(linkTo(methodOn(LocationPlatformController.class).deleteById(created.getId())).withRel("delete"));
        }
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar local/plataforma existente", description = "Atualiza as informações de um local ou plataforma existente pelo seu ID")
    public ResponseEntity<LocationPlatformResponseDTO> update(
            @Parameter(description = "ID do local/plataforma a ser atualizado", required = true) @PathVariable Long id,
            @Valid @RequestBody LocationPlatformRequestDTO dto) {
        LocationPlatformResponseDTO updated = locationPlatformService.update(id, dto);
        updated.add(linkTo(methodOn(LocationPlatformController.class).findById(id)).withSelfRel());
        updated.add(linkTo(methodOn(LocationPlatformController.class).findAll()).withRel("all-locations-platforms"));
        updated.add(linkTo(methodOn(LocationPlatformController.class).deleteById(id)).withRel("delete"));
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar local/plataforma", description = "Deleta um local ou plataforma do sistema pelo seu ID")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "ID do local/plataforma a ser deletado", required = true) @PathVariable Long id) {
        locationPlatformService.delete(id);
        return ResponseEntity.noContent().build();
    }
}