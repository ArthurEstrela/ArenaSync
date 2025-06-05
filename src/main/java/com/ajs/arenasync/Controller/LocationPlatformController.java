package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.LocationPlatformRequestDTO;
import com.ajs.arenasync.DTO.LocationPlatformResponseDTO;
import com.ajs.arenasync.Services.LocationPlatformService;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/location-platforms")
public class LocationPlatformController {

    @Autowired
    private LocationPlatformService locationPlatformService;

    // Criar nova LocationPlatform
    @PostMapping
    public ResponseEntity<LocationPlatformResponseDTO> create(@RequestBody @Valid LocationPlatformRequestDTO dto) {
        LocationPlatformResponseDTO created = locationPlatformService.create(dto);
        created.add(linkTo(methodOn(LocationPlatformController.class).findById(created.getId())).withSelfRel());
        created.add(linkTo(methodOn(LocationPlatformController.class).findAll()).withRel("all-location-platforms"));
        created.add(linkTo(methodOn(LocationPlatformController.class).update(created.getId(), null)).withRel("update"));
        created.add(linkTo(methodOn(LocationPlatformController.class).delete(created.getId())).withRel("delete"));
        return ResponseEntity.ok(created);
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<LocationPlatformResponseDTO> findById(@PathVariable Long id) {
        LocationPlatformResponseDTO response = locationPlatformService.findById(id);
        response.add(linkTo(methodOn(LocationPlatformController.class).findById(id)).withSelfRel());
        response.add(linkTo(methodOn(LocationPlatformController.class).findAll()).withRel("all-location-platforms"));
        response.add(linkTo(methodOn(LocationPlatformController.class).update(id, null)).withRel("update"));
        response.add(linkTo(methodOn(LocationPlatformController.class).delete(id)).withRel("delete"));
        return ResponseEntity.ok(response);
    }

    // Listar todos
    @GetMapping
    public ResponseEntity<CollectionModel<LocationPlatformResponseDTO>> findAll() {
        List<LocationPlatformResponseDTO> list = locationPlatformService.findAll();
        for (LocationPlatformResponseDTO lp : list) {
            lp.add(linkTo(methodOn(LocationPlatformController.class).findById(lp.getId())).withSelfRel());
            lp.add(linkTo(methodOn(LocationPlatformController.class).update(lp.getId(), null)).withRel("update"));
            lp.add(linkTo(methodOn(LocationPlatformController.class).delete(lp.getId())).withRel("delete"));
        }
        Link selfLink = linkTo(methodOn(LocationPlatformController.class).findAll()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(list, selfLink));
    }

    // Atualizar por ID
    @PutMapping("/{id}")
    public ResponseEntity<LocationPlatformResponseDTO> update(@PathVariable Long id,
            @RequestBody @Valid LocationPlatformRequestDTO dto) {
        LocationPlatformResponseDTO updated = locationPlatformService.update(id, dto);
        updated.add(linkTo(methodOn(LocationPlatformController.class).findById(id)).withSelfRel());
        updated.add(linkTo(methodOn(LocationPlatformController.class).findAll()).withRel("all-location-platforms"));
        updated.add(linkTo(methodOn(LocationPlatformController.class).delete(id)).withRel("delete"));
        return ResponseEntity.ok(updated);
    }

    // Deletar por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        locationPlatformService.delete(id);
        return ResponseEntity.noContent().build();
    }
}