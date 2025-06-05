package com.ajs.arenasync.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.TeamRequestDTO;
import com.ajs.arenasync.DTO.TeamResponseDTO;
import com.ajs.arenasync.Services.TeamService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation; // Importe esta anotação
import io.swagger.v3.oas.annotations.tags.Tag; // Importe esta anotação
import io.swagger.v3.oas.annotations.Parameter; // Importe esta anotação para documentar PathVariable

@RestController
@RequestMapping("/api/teams")
@Tag(name = "Team Management", description = "Operações para gerenciar times no ArenaSync") // Anotação na classe
public class TeamController {

    @Autowired
    private TeamService teamService;

    // CREATE
    @PostMapping
    @Operation(summary = "Criar novo time", description = "Cria um novo time no sistema")
    public ResponseEntity<TeamResponseDTO> createTeam(@Valid @RequestBody TeamRequestDTO dto) {
        TeamResponseDTO created = teamService.save(dto);
        created.add(linkTo(methodOn(TeamController.class).getTeamById(created.getId())).withSelfRel());
        // Adicionar link para a coleção de todos os times (se você criar o método getAllTeams)
        created.add(linkTo(methodOn(TeamController.class).getAllTeams()).withRel("all-teams"));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // READ
    @GetMapping("/{id}")
    @Operation(summary = "Obter time por ID", description = "Retorna um time específico com base no seu ID")
    public ResponseEntity<TeamResponseDTO> getTeamById(
            @Parameter(description = "ID do time a ser buscado", required = true) @PathVariable Long id) {
        TeamResponseDTO team = teamService.findById(id);
        team.add(linkTo(methodOn(TeamController.class).getTeamById(id)).withSelfRel());
        team.add(linkTo(methodOn(TeamController.class).updateTeam(id, null)).withRel("edit"));
        team.add(linkTo(methodOn(TeamController.class).deleteTeam(id)).withRel("delete"));
        team.add(linkTo(methodOn(TeamController.class).getAllTeams()).withRel("all-teams"));
        return ResponseEntity.ok(team);
    }

    // UPDATE
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar time existente", description = "Atualiza as informações de um time existente pelo seu ID")
    public ResponseEntity<TeamResponseDTO> updateTeam(
            @Parameter(description = "ID do time a ser atualizado", required = true) @PathVariable Long id,
            @Valid @RequestBody TeamRequestDTO dto) {
        TeamResponseDTO updated = teamService.update(id, dto);
        updated.add(linkTo(methodOn(TeamController.class).getTeamById(id)).withSelfRel());
        updated.add(linkTo(methodOn(TeamController.class).getAllTeams()).withRel("all-teams"));
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar time", description = "Deleta um time do sistema pelo seu ID")
    public ResponseEntity<Void> deleteTeam(
            @Parameter(description = "ID do time a ser deletado", required = true) @PathVariable Long id) {
        teamService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Método para listar todos os times (Adicionei para completar o HATEOAS da coleção)
    @GetMapping
    @Operation(summary = "Listar todos os times", description = "Retorna uma lista de todos os times registrados")
    public ResponseEntity<CollectionModel<TeamResponseDTO>> getAllTeams() {
        // Supondo que você tenha um método findAll no TeamService
        // Se não tiver, você precisaria implementá-lo.
        List<TeamResponseDTO> teamsList = teamService.findAll().stream()
                                         .map(team -> {
                                             TeamResponseDTO dto = new TeamResponseDTO();
                                             dto.setId(team.getId());
                                             dto.setName(team.getName());
                                             return dto;
                                         })
                                         .collect(Collectors.toList());


        for (TeamResponseDTO team : teamsList) {
            team.add(linkTo(methodOn(TeamController.class).getTeamById(team.getId())).withSelfRel());
            team.add(linkTo(methodOn(TeamController.class).updateTeam(team.getId(), null)).withRel("edit"));
            team.add(linkTo(methodOn(TeamController.class).deleteTeam(team.getId())).withRel("delete"));
        }
        Link selfLink = linkTo(methodOn(TeamController.class).getAllTeams()).withSelfRel();
        CollectionModel<TeamResponseDTO> collectionModel = CollectionModel.of(teamsList, selfLink);
        return ResponseEntity.ok(collectionModel);
    }
}