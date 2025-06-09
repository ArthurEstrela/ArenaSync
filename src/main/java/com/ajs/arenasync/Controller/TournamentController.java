package com.ajs.arenasync.Controller;



import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.TournamentRequestDTO;
import com.ajs.arenasync.DTO.TournamentResponseDTO;
import com.ajs.arenasync.Entities.Enums.TournamentStatus;
import com.ajs.arenasync.Services.TournamentService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.data.domain.Page; // Importe Page
import org.springframework.data.domain.Pageable; // Importe Pageable
import org.springframework.data.web.PageableDefault; // Importe PageableDefault

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
        // Se você quiser que o link "all-tournaments" aponte para a versão paginada, você pode removê-lo ou ajustar.
        // created.add(linkTo(methodOn(TournamentController.class).getAllTournaments()).withRel("all-tournaments")); // Este link agora seria para a versão paginada
        addLinksBasedOnStatus(created);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // READ - find by ID
    @GetMapping("/{id}")
    @Operation(summary = "Obter torneio por ID", description = "Retorna um torneio específico com base no seu ID")
    public ResponseEntity<TournamentResponseDTO> getTournamentById(
            @Parameter(description = "ID do torneio a ser buscado", required = true) @PathVariable Long id) {
        TournamentResponseDTO tournament = tournamentService.findById(id);
        tournament.add(linkTo(methodOn(TournamentController.class).getTournamentById(id)).withSelfRel());
        // Ajuste aqui se o link para "all-tournaments" deve ser paginado
        // tournament.add(linkTo(methodOn(TournamentController.class).getAllTournaments()).withRel("all-tournaments"));
        addLinksBasedOnStatus(tournament);

        return ResponseEntity.ok(tournament);
    }

    // READ - list all (AGORA COM PAGINAÇÃO)
    @GetMapping
    @Operation(summary = "Listar todos os torneios com paginação", description = "Retorna uma lista paginada de todos os torneios registrados")
    public ResponseEntity<Page<TournamentResponseDTO>> getAllTournaments(
            @Parameter(description = "Detalhes da paginação (page=0, size=10, sort=id,asc)", required = false)
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) { // Default page=0, size=10, sort by ID
        
        Page<TournamentResponseDTO> page = tournamentService.getAllTournaments(pageable); // Chama o serviço com Pageable
        
        // Adicionar links HATEOAS para cada item na página
        for (TournamentResponseDTO tournament : page.getContent()) {
            tournament.add(linkTo(methodOn(TournamentController.class).getTournamentById(tournament.getId())).withSelfRel());
            addLinksBasedOnStatus(tournament);
        }
        
        // Links da própria coleção paginada
        // Você pode adicionar links para a próxima/anterior página, se quiser ser mais RESTful,
        // mas o Page do Spring já contém essas informações.
        
        return ResponseEntity.ok(page); // Retorna a página diretamente
    }

    // UPDATE
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar torneio existente", description = "Atualiza as informações de um torneio existente pelo seu ID")
    public ResponseEntity<TournamentResponseDTO> updateTournament(
            @Parameter(description = "ID do torneio a ser atualizado", required = true) @PathVariable Long id,
            @Valid @RequestBody TournamentRequestDTO dto) {
        
        TournamentResponseDTO updated = tournamentService.updateTournament(id, dto);
        updated.add(linkTo(methodOn(TournamentController.class).getTournamentById(id)).withSelfRel());
        // updated.add(linkTo(methodOn(TournamentController.class).getAllTournaments()).withRel("all-tournaments")); // Se necessário, ajustar para paginado
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