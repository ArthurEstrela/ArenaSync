package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel; // Importe para coleções HATEOAS
import org.springframework.hateoas.Link; // Importe para Links HATEOAS
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Import estático para linkTo e methodOn

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        
        // Links para recursos relacionados (Teams, Tournament, LocationPlatform)
        try {
            created.add(linkTo(methodOn(TeamController.class).getTeamById(dto.getTeamAId())).withRel("team-a"));
            created.add(linkTo(methodOn(TeamController.class).getTeamById(dto.getTeamBId())).withRel("team-b"));
            created.add(linkTo(methodOn(TournamentController.class).getTournamentById(dto.getTournamentId())).withRel("tournament"));
            created.add(linkTo(methodOn(LocationPlatformController.class).findById(dto.getLocationPlatformId())).withRel("location-platform"));
        } catch (Exception e) {
            System.err.println("Erro ao tentar gerar link para recurso relacionado em create Match: " + e.getMessage());
        }

        return ResponseEntity.ok(created);
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
        // Se MatchResponseDTO não expõe os IDs diretamente, é necessário ajustar o DTO ou o serviço
        // para obter esses IDs para a criação dos links HATEOAS.
        /*
        if (response.getTeamAId() != null) { // Exemplo: se TeamAId estivesse no DTO de resposta
            try {
                response.add(linkTo(methodOn(TeamController.class).getTeamById(response.getTeamAId())).withRel("team-a"));
            } catch (Exception e) { }
        }
        */

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