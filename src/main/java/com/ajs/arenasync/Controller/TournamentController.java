package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.TournamentRequestDTO;
import com.ajs.arenasync.DTO.TournamentResponseDTO;
import com.ajs.arenasync.Entities.Enums.TournamentStatus;
import com.ajs.arenasync.Services.TournamentService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation; // Importe esta anotação
import io.swagger.v3.oas.annotations.tags.Tag; // Importe esta anotação
import io.swagger.v3.oas.annotations.Parameter; // Importe esta anotação para documentar PathVariable

@RestController
@RequestMapping("/api/tournaments")
@Tag(name = "Tournament Management", description = "Operações para gerenciar torneios no ArenaSync") // Anotação na classe
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    // CREATE
    @PostMapping("/organizer/{organizerId}")
    @Operation(summary = "Criar novo torneio", description = "Cria um novo torneio associado a um organizador específico")
    public ResponseEntity<TournamentResponseDTO> createTournament(
            @Parameter(description = "ID do organizador do torneio", required = true) @PathVariable Long organizerId,
            @Valid @RequestBody TournamentRequestDTO dto) {
        
        TournamentResponseDTO created = tournamentService.createTournament(organizerId, dto);
        created.add(linkTo(methodOn(TournamentController.class).getTournamentById(created.getId())).withSelfRel());
        created.add(linkTo(methodOn(TournamentController.class).getAllTournaments()).withRel("all-tournaments"));
        addLinksBasedOnStatus(created); // Método auxiliar para adicionar links de HATEOAS

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // READ - find by ID
    @GetMapping("/{id}")
    @Operation(summary = "Obter torneio por ID", description = "Retorna um torneio específico com base no seu ID")
    public ResponseEntity<TournamentResponseDTO> getTournamentById(
            @Parameter(description = "ID do torneio a ser buscado", required = true) @PathVariable Long id) {
        TournamentResponseDTO tournament = tournamentService.findById(id);
        tournament.add(linkTo(methodOn(TournamentController.class).getTournamentById(id)).withSelfRel());
        tournament.add(linkTo(methodOn(TournamentController.class).getAllTournaments()).withRel("all-tournaments"));
        addLinksBasedOnStatus(tournament);

        return ResponseEntity.ok(tournament);
    }

    // READ - list all
    @GetMapping
    @Operation(summary = "Listar todos os torneios", description = "Retorna uma lista de todos os torneios registrados")
    public ResponseEntity<CollectionModel<TournamentResponseDTO>> getAllTournaments() {
        List<TournamentResponseDTO> tournamentsList = tournamentService.getAllTournaments();
        
        for (TournamentResponseDTO tournament : tournamentsList) {
            tournament.add(linkTo(methodOn(TournamentController.class).getTournamentById(tournament.getId())).withSelfRel());
            addLinksBasedOnStatus(tournament);
        }
        Link selfLink = linkTo(methodOn(TournamentController.class).getAllTournaments()).withSelfRel();
        CollectionModel<TournamentResponseDTO> collectionModel = CollectionModel.of(tournamentsList, selfLink);
        
        return ResponseEntity.ok(collectionModel);
    }

    // UPDATE
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar torneio existente", description = "Atualiza as informações de um torneio existente pelo seu ID")
    public ResponseEntity<TournamentResponseDTO> updateTournament(
            @Parameter(description = "ID do torneio a ser atualizado", required = true) @PathVariable Long id,
            @Valid @RequestBody TournamentRequestDTO dto) {
        
        TournamentResponseDTO updated = tournamentService.updateTournament(id, dto);
        updated.add(linkTo(methodOn(TournamentController.class).getTournamentById(id)).withSelfRel());
        addLinksBasedOnStatus(updated);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar torneio", description = "Deleta um torneio do sistema pelo seu ID. Não é possível deletar torneios finalizados.")
    public ResponseEntity<Void> deleteTournament(
            @Parameter(description = "ID do torneio a ser deletado", required = true) @PathVariable Long id) {
        tournamentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // START
    @PostMapping("/{id}/start")
    @Operation(summary = "Iniciar um torneio", description = "Muda o status de um torneio de PENDING para ONGOING. Apenas torneios pendentes podem ser iniciados.")
    public ResponseEntity<TournamentResponseDTO> startTournament(
            @Parameter(description = "ID do torneio a ser iniciado", required = true) @PathVariable Long id) {
        TournamentResponseDTO started = tournamentService.startTournament(id);
        started.add(linkTo(methodOn(TournamentController.class).getTournamentById(id)).withSelfRel());
        addLinksBasedOnStatus(started);
        return ResponseEntity.ok(started);
    }

    // FINISH
    @PostMapping("/{id}/finish")
    @Operation(summary = "Finalizar um torneio", description = "Muda o status de um torneio de ONGOING para FINISHED. Apenas torneios em andamento podem ser finalizados.")
    public ResponseEntity<TournamentResponseDTO> finishTournament(
            @Parameter(description = "ID do torneio a ser finalizado", required = true) @PathVariable Long id) {
        TournamentResponseDTO finished = tournamentService.finishTournament(id);
        finished.add(linkTo(methodOn(TournamentController.class).getTournamentById(id)).withSelfRel());
        addLinksBasedOnStatus(finished);
        return ResponseEntity.ok(finished);
    }

    private void addLinksBasedOnStatus(TournamentResponseDTO tournament) {
        if (tournament == null || tournament.getId() == null || tournament.getStatus() == null) {
            return;
        }

        Long id = tournament.getId();

        if (tournament.getStatus() != TournamentStatus.FINISHED) {
             tournament.add(linkTo(methodOn(TournamentController.class).updateTournament(id, null)).withRel("edit"));
        }

        if (tournament.getStatus() != TournamentStatus.FINISHED) {
             tournament.add(linkTo(methodOn(TournamentController.class).deleteTournament(id)).withRel("delete"));
        }

        if (tournament.getStatus() == TournamentStatus.PENDING) {
            tournament.add(linkTo(methodOn(TournamentController.class).startTournament(id)).withRel("start"));
        } else if (tournament.getStatus() == TournamentStatus.ONGOING) {
            tournament.add(linkTo(methodOn(TournamentController.class).finishTournament(id)).withRel("finish"));
        }
    }
}