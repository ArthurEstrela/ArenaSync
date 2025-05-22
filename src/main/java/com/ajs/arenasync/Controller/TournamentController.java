package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.Entities.Tournament;
import com.ajs.arenasync.Services.TournamentService;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    // Criar torneio - passando organizerId por parâmetro
    @PostMapping
    public ResponseEntity<Tournament> createTournament(
            @RequestParam Long organizerId,
            @RequestBody Tournament tournament) {
        Tournament created = tournamentService.createTournament(organizerId, tournament);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Buscar torneio por ID
    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getById(@PathVariable Long id) {
        Tournament tournament = tournamentService.findById(id);
        return ResponseEntity.ok(tournament);
    }

    // Listar todos os torneios
    @GetMapping
    public ResponseEntity<List<Tournament>> getAll() {
        List<Tournament> tournaments = tournamentService.getAllTournaments();
        return ResponseEntity.ok(tournaments);
    }

    // Atualizar torneio
    @PutMapping("/{id}")
    public ResponseEntity<Tournament> updateTournament(
            @PathVariable Long id,
            @RequestBody Tournament updatedData) {
        Tournament updated = tournamentService.updateTournament(id, updatedData);
        return ResponseEntity.ok(updated);
    }

    // Deletar torneio
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Iniciar torneio
    @PutMapping("/{id}/start")
    public ResponseEntity<Tournament> startTournament(@PathVariable Long id) {
        Tournament started = tournamentService.startTournament(id);
        return ResponseEntity.ok(started);
    }

    // Finalizar torneio
    @PutMapping("/{id}/finish")
    public ResponseEntity<Tournament> finishTournament(@PathVariable Long id) {
        Tournament finished = tournamentService.finishTournament(id);
        return ResponseEntity.ok(finished);
    }
}
