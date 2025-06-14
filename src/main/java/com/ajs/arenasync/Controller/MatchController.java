package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel; // Importe para coleções HATEOAS
import org.springframework.hateoas.Link; // Importe para Links HATEOAS
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Import estático para linkTo e methodOn

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.ajs.arenasync.DTO.MatchRequestDTO;
import com.ajs.arenasync.DTO.MatchResponseDTO;
import com.ajs.arenasync.Services.MatchService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation; // Importe esta anotação
import io.swagger.v3.oas.annotations.tags.Tag; // Importe esta anotação
import io.swagger.v3.oas.annotations.Parameter; // Importe esta anotação para documentar PathVariable

@RestController
@RequestMapping("/api/matches")
@Tag(name = "Match Management", description = "Operações para gerenciar partidas de torneios") // Anotação na classe
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PostMapping
    @Operation(summary = "Criar nova partida", description = "Cria uma nova partida com base nas equipes, torneio, local/plataforma e data/hora agendada")
    public ResponseEntity<MatchResponseDTO> create(@Valid @RequestBody MatchRequestDTO dto) {
        MatchResponseDTO created = matchService.saveFromDTO(dto);
        created.add(linkTo(methodOn(MatchController.class).findById(created.getId())).withSelfRel());
        created.add(linkTo(methodOn(MatchController.class).findAll()).withRel("all-matches"));
        
        // Links para recursos relacionados (Teams, Tournament, LocationPlatform) - Agora usamos os IDs do DTO de resposta
        if (created.getTeamAId() != null) {
            created.add(linkTo(methodOn(TeamController.class).getTeamById(created.getTeamAId())).withRel("team-a"));
        }
        if (created.getTeamBId() != null) {
            created.add(linkTo(methodOn(TeamController.class).getTeamById(created.getTeamBId())).withRel("team-b"));
        }
        if (created.getTournamentId() != null) {
            created.add(linkTo(methodOn(TournamentController.class).getTournamentById(created.getTournamentId())).withRel("tournament"));
        }
        if (created.getLocationPlatformId() != null) {
            created.add(linkTo(methodOn(LocationPlatformController.class).findById(created.getLocationPlatformId())).withRel("location-platform"));
        }

        // Retorna com o status 201 Created
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter partida por ID", description = "Retorna uma partida específica com base no seu ID")
    public ResponseEntity<MatchResponseDTO> findById(
            @Parameter(description = "ID da partida a ser buscada", required = true) @PathVariable Long id) {
        MatchResponseDTO response = matchService.findById(id);
        response.add(linkTo(methodOn(MatchController.class).findById(id)).withSelfRel());
        response.add(linkTo(methodOn(MatchController.class).findAll()).withRel("all-matches"));
        response.add(linkTo(methodOn(MatchController.class).deleteById(id)).withRel("delete"));
        
        // Links para recursos relacionados (Teams, Tournament, LocationPlatform)
        if (response.getTeamAId() != null) {
            response.add(linkTo(methodOn(TeamController.class).getTeamById(response.getTeamAId())).withRel("team-a"));
        }
        if (response.getTeamBId() != null) {
            response.add(linkTo(methodOn(TeamController.class).getTeamById(response.getTeamBId())).withRel("team-b"));
        }
        if (response.getTournamentId() != null) {
            response.add(linkTo(methodOn(TournamentController.class).getTournamentById(response.getTournamentId())).withRel("tournament"));
        }
        if (response.getLocationPlatformId() != null) {
            response.add(linkTo(methodOn(LocationPlatformController.class).findById(response.getLocationPlatformId())).withRel("location-platform"));
        }


        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as partidas", description = "Retorna uma lista de todas as partidas agendadas ou concluídas")
    public ResponseEntity<CollectionModel<MatchResponseDTO>> findAll() {
        // Assume que MatchService.findAll() existe e retorna List<MatchResponseDTO>
        List<MatchResponseDTO> list = matchService.findAll();
        for (MatchResponseDTO match : list) {
            match.add(linkTo(methodOn(MatchController.class).findById(match.getId())).withSelfRel());
            match.add(linkTo(methodOn(MatchController.class).deleteById(match.getId())).withRel("delete"));
            // Adicionar links para recursos relacionados individualmente, se os IDs estiverem disponíveis no DTO
            if (match.getTeamAId() != null) {
                match.add(linkTo(methodOn(TeamController.class).getTeamById(match.getTeamAId())).withRel("team-a"));
            }
            if (match.getTeamBId() != null) {
                match.add(linkTo(methodOn(TeamController.class).getTeamById(match.getTeamBId())).withRel("team-b"));
            }
            if (match.getTournamentId() != null) {
                match.add(linkTo(methodOn(TournamentController.class).getTournamentById(match.getTournamentId())).withRel("tournament"));
            }
            if (match.getLocationPlatformId() != null) {
                match.add(linkTo(methodOn(LocationPlatformController.class).findById(match.getLocationPlatformId())).withRel("location-platform"));
            }
        }
        Link selfLink = linkTo(methodOn(MatchController.class).findAll()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(list, selfLink));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar partida", description = "Deleta uma partida do sistema pelo seu ID")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "ID da partida a ser deletada", required = true) @PathVariable Long id) {
        matchService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
