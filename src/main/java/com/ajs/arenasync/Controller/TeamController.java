package com.ajs.arenasync.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Import estático

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.TeamRequestDTO;
import com.ajs.arenasync.DTO.TeamResponseDTO;
import com.ajs.arenasync.Services.TeamService;

import jakarta.validation.Valid;
import java.util.List; // Importar List
import java.util.stream.Collectors; // Importar Collectors

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    // CREATE
    @PostMapping
    public ResponseEntity<TeamResponseDTO> createTeam(@Valid @RequestBody TeamRequestDTO dto) {
        TeamResponseDTO created = teamService.save(dto);
        // Adicionar link "self"
        created.add(linkTo(methodOn(TeamController.class).getTeamById(created.getId())).withSelfRel());
        // Adicionar link para a coleção (se houver um endpoint para listar todos)
        // Ex: created.add(linkTo(methodOn(TeamController.class).getAllTeams()).withRel("all-teams"));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // READ
    @GetMapping("/{id}")
    public ResponseEntity<TeamResponseDTO> getTeamById(@PathVariable Long id) {
        TeamResponseDTO team = teamService.findById(id);
        // Adicionar link "self"
        team.add(linkTo(methodOn(TeamController.class).getTeamById(id)).withSelfRel());
        // Adicionar link para editar (PUT)
        team.add(linkTo(methodOn(TeamController.class).updateTeam(id, null)).withRel("edit")); // null para DTO no methodOn
        // Adicionar link para deletar (DELETE)
        team.add(linkTo(methodOn(TeamController.class).deleteTeam(id)).withRel("delete"));
        // Adicionar link para a coleção (se houver um endpoint para listar todos)
        // Ex: team.add(linkTo(methodOn(TeamController.class).getAllTeams()).withRel("all-teams"));
        return ResponseEntity.ok(team);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<TeamResponseDTO> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamRequestDTO dto) {
        TeamResponseDTO updated = teamService.update(id, dto);
        // Adicionar link "self"
        updated.add(linkTo(methodOn(TeamController.class).getTeamById(id)).withSelfRel());
        return ResponseEntity.ok(updated);
    }

    // DELETE - Respostas 204 não têm corpo para links.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Se você adicionar um método para listar todos os times, seria algo como:
    /*
    @GetMapping
    public ResponseEntity<CollectionModel<TeamResponseDTO>> getAllTeams() {
        List<TeamResponseDTO> teamsList = teamService.findAll(); // Supondo que exista teamService.findAll()

        for (TeamResponseDTO team : teamsList) {
            team.add(linkTo(methodOn(TeamController.class).getTeamById(team.getId())).withSelfRel());
        }
        Link selfLink = linkTo(methodOn(TeamController.class).getAllTeams()).withSelfRel();
        CollectionModel<TeamResponseDTO> collectionModel = CollectionModel.of(teamsList, selfLink);
        return ResponseEntity.ok(collectionModel);
    }
    */
}