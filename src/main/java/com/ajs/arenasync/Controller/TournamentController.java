package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.TournamentRequestDTO;
import com.ajs.arenasync.DTO.TournamentResponseDTO;
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
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // READ - find by ID
    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponseDTO> getTournamentById(@PathVariable Long id) {
        TournamentResponseDTO tournament = tournamentService.findById(id);
        return ResponseEntity.ok(tournament);
    }

    // READ - list all
    @GetMapping
    public ResponseEntity<List<TournamentResponseDTO>> getAllTournaments() {
        List<TournamentResponseDTO> tournaments = tournamentService.getAllTournaments();
        return ResponseEntity.ok(tournaments);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<TournamentResponseDTO> updateTournament(
            @PathVariable Long id,
            @Valid @RequestBody TournamentRequestDTO dto) {
        
        TournamentResponseDTO updated = tournamentService.updateTournament(id, dto);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // START
    @PostMapping("/{id}/start")
    public ResponseEntity<TournamentResponseDTO> startTournament(@PathVariable Long id) {
        TournamentResponseDTO started = tournamentService.startTournament(id);
        return ResponseEntity.ok(started);
    }

    // FINISH
    @PostMapping("/{id}/finish")
    public ResponseEntity<TournamentResponseDTO> finishTournament(@PathVariable Long id) {
        TournamentResponseDTO finished = tournamentService.finishTournament(id);
        return ResponseEntity.ok(finished);
    }
}
