package com.ajs.arenasync.Controller;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ajs.arenasync.Entities.Tournament;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Services.TournamentService;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getById(@PathVariable Long id) {
        Tournament tournament = tournamentService.findById(id); // exceção tratada no service
        return ResponseEntity.ok(tournament);
    }

    @PostMapping
    public ResponseEntity<Tournament> create(@RequestBody Tournament tournament) {
        Tournament savedTournament = tournamentService.save(tournament);
        URI location = URI.create("/tournaments/" + savedTournament.getId());
        return ResponseEntity.created(location).body(savedTournament);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tournament> update(@PathVariable Long id, @RequestBody Tournament tournament) {
        tournamentService.findById(id); // valida se existe
        tournament.setId(id);
        Tournament updatedTournament = tournamentService.save(tournament);
        return ResponseEntity.ok(updatedTournament);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tournamentService.findById(id); // valida se existe
        tournamentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
