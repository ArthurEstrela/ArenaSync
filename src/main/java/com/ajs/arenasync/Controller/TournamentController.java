package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Import estático

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.TournamentRequestDTO;
import com.ajs.arenasync.DTO.TournamentResponseDTO;
import com.ajs.arenasync.Entities.Enums.TournamentStatus; // Para checar status ao adicionar links
import com.ajs.arenasync.Services.TournamentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    // CREATE
    @PostMapping("/organizer/{organizerId}")
    public ResponseEntity<TournamentResponseDTO> createTournament(
            @PathVariable Long organizerId,
            @Valid @RequestBody TournamentRequestDTO dto) {
        
        TournamentResponseDTO created = tournamentService.createTournament(organizerId, dto);
        // Adiciona link "self"
        created.add(linkTo(methodOn(TournamentController.class).getTournamentById(created.getId())).withSelfRel());
        // Adiciona link para a coleção de todos os torneios
        created.add(linkTo(methodOn(TournamentController.class).getAllTournaments()).withRel("all-tournaments"));
        // Adiciona links de ação dependendo do status inicial (PENDING)
        addLinksBasedOnStatus(created);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // READ - find by ID
    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponseDTO> getTournamentById(@PathVariable Long id) {
        TournamentResponseDTO tournament = tournamentService.findById(id);
        // Adiciona link "self"
        tournament.add(linkTo(methodOn(TournamentController.class).getTournamentById(id)).withSelfRel());
        // Adiciona link para a coleção de todos os torneios
        tournament.add(linkTo(methodOn(TournamentController.class).getAllTournaments()).withRel("all-tournaments"));
        // Adiciona links de ação baseados no status atual do torneio
        addLinksBasedOnStatus(tournament);

        // Exemplo: Se você tivesse o ID do organizador no DTO e um endpoint para buscar organizador
        // if (tournament.getOrganizerId() != null) {
        //     tournament.add(linkTo(methodOn(OrganizerController.class).getOrganizerById(tournament.getOrganizerId())).withRel("organizer"));
        // }

        return ResponseEntity.ok(tournament);
    }

    // READ - list all
    @GetMapping
    public ResponseEntity<CollectionModel<TournamentResponseDTO>> getAllTournaments() {
        List<TournamentResponseDTO> tournamentsList = tournamentService.getAllTournaments();
        
        for (TournamentResponseDTO tournament : tournamentsList) {
            // Adiciona link "self" para cada torneio
            tournament.add(linkTo(methodOn(TournamentController.class).getTournamentById(tournament.getId())).withSelfRel());
            // Adiciona links de ação baseados no status de cada torneio
            addLinksBasedOnStatus(tournament);
        }
        // Adiciona link "self" para a coleção
        Link selfLink = linkTo(methodOn(TournamentController.class).getAllTournaments()).withSelfRel();
        CollectionModel<TournamentResponseDTO> collectionModel = CollectionModel.of(tournamentsList, selfLink);
        
        return ResponseEntity.ok(collectionModel);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<TournamentResponseDTO> updateTournament(
            @PathVariable Long id,
            @Valid @RequestBody TournamentRequestDTO dto) {
        
        TournamentResponseDTO updated = tournamentService.updateTournament(id, dto);
        // Adiciona link "self"
        updated.add(linkTo(methodOn(TournamentController.class).getTournamentById(id)).withSelfRel());
        // Adiciona links de ação baseados no status atualizado do torneio
        addLinksBasedOnStatus(updated);
        return ResponseEntity.ok(updated);
    }

    // DELETE - Respostas 204 geralmente não têm corpo para links.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // START
    @PostMapping("/{id}/start")
    public ResponseEntity<TournamentResponseDTO> startTournament(@PathVariable Long id) {
        TournamentResponseDTO started = tournamentService.startTournament(id);
        // Adiciona link "self"
        started.add(linkTo(methodOn(TournamentController.class).getTournamentById(id)).withSelfRel());
        // Adiciona links de ação baseados no novo status (ONGOING)
        addLinksBasedOnStatus(started);
        return ResponseEntity.ok(started);
    }

    // FINISH
    @PostMapping("/{id}/finish")
    public ResponseEntity<TournamentResponseDTO> finishTournament(@PathVariable Long id) {
        TournamentResponseDTO finished = tournamentService.finishTournament(id);
        // Adiciona link "self"
        finished.add(linkTo(methodOn(TournamentController.class).getTournamentById(id)).withSelfRel());
        // Adiciona links de ação baseados no novo status (FINISHED)
        addLinksBasedOnStatus(finished);
        return ResponseEntity.ok(finished);
    }

    /**
     * Método auxiliar para adicionar links de ação baseados no status do torneio.
     * @param tournament DTO do torneio ao qual os links serão adicionados.
     */
    private void addLinksBasedOnStatus(TournamentResponseDTO tournament) {
        if (tournament == null || tournament.getId() == null || tournament.getStatus() == null) {
            return;
        }

        Long id = tournament.getId();

        // Link para editar é geralmente sempre possível (dependendo da regra de negócio)
        // Aqui, vamos permitir editar se não estiver finalizado.
        if (tournament.getStatus() != TournamentStatus.FINISHED) {
             tournament.add(linkTo(methodOn(TournamentController.class).updateTournament(id, null)).withRel("edit")); // null para DTO no methodOn
        }

        // Link para deletar, se não estiver finalizado
        if (tournament.getStatus() != TournamentStatus.FINISHED) {
             tournament.add(linkTo(methodOn(TournamentController.class).deleteTournament(id)).withRel("delete"));
        }

        if (tournament.getStatus() == TournamentStatus.PENDING) {
            tournament.add(linkTo(methodOn(TournamentController.class).startTournament(id)).withRel("start"));
        } else if (tournament.getStatus() == TournamentStatus.ONGOING) {
            tournament.add(linkTo(methodOn(TournamentController.class).finishTournament(id)).withRel("finish"));
        }
        // Para torneios finalizados, você pode querer adicionar outros links, como "view-results", "view-prizes", etc.
    }
}