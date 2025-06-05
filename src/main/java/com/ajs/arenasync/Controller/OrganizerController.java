package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.OrganizerRequestDTO;
import com.ajs.arenasync.DTO.OrganizerResponseDTO;
import com.ajs.arenasync.Services.OrganizerService;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/organizers")
public class OrganizerController {

    @Autowired
    private OrganizerService organizerService;

    @GetMapping("/{id}")
    public ResponseEntity<OrganizerResponseDTO> getOrganizerById(@PathVariable Long id) {
        OrganizerResponseDTO organizer = organizerService.getOrganizerById(id);
        organizer.add(linkTo(methodOn(OrganizerController.class).getOrganizerById(id)).withSelfRel());
        organizer.add(linkTo(methodOn(OrganizerController.class).getAllOrganizers()).withRel("all-organizers"));
        organizer.add(linkTo(methodOn(OrganizerController.class).updateOrganizer(id, null)).withRel("update")); // Link para PUT
        organizer.add(linkTo(methodOn(OrganizerController.class).deleteOrganizer(id)).withRel("delete")); // Link para DELETE
        return ResponseEntity.ok(organizer);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<OrganizerResponseDTO>> getAllOrganizers() {
        List<OrganizerResponseDTO> organizersList = organizerService.getAllOrganizers();
        for (OrganizerResponseDTO organizer : organizersList) {
            organizer.add(linkTo(methodOn(OrganizerController.class).getOrganizerById(organizer.getId())).withSelfRel());
            organizer.add(linkTo(methodOn(OrganizerController.class).updateOrganizer(organizer.getId(), null)).withRel("update"));
            organizer.add(linkTo(methodOn(OrganizerController.class).deleteOrganizer(organizer.getId())).withRel("delete"));
        }
        Link selfLink = linkTo(methodOn(OrganizerController.class).getAllOrganizers()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(organizersList, selfLink));
    }

    @PostMapping
    public ResponseEntity<OrganizerResponseDTO> createOrganizer(
            @Valid @RequestBody OrganizerRequestDTO dto) {
        OrganizerResponseDTO created = organizerService.createOrganizer(dto);
        created.add(linkTo(methodOn(OrganizerController.class).getOrganizerById(created.getId())).withSelfRel());
        created.add(linkTo(methodOn(OrganizerController.class).getAllOrganizers()).withRel("all-organizers"));
        created.add(linkTo(methodOn(OrganizerController.class).updateOrganizer(created.getId(), null)).withRel("update"));
        created.add(linkTo(methodOn(OrganizerController.class).deleteOrganizer(created.getId())).withRel("delete"));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizerResponseDTO> updateOrganizer(
            @PathVariable Long id,
            @Valid @RequestBody OrganizerRequestDTO dto) {
        OrganizerResponseDTO updated = organizerService.updateOrganizer(id, dto);
        updated.add(linkTo(methodOn(OrganizerController.class).getOrganizerById(id)).withSelfRel());
        updated.add(linkTo(methodOn(OrganizerController.class).getAllOrganizers()).withRel("all-organizers"));
        updated.add(linkTo(methodOn(OrganizerController.class).deleteOrganizer(id)).withRel("delete"));
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganizer(@PathVariable Long id) {
        organizerService.deleteOrganizer(id);
        return ResponseEntity.noContent().build();
    }
}