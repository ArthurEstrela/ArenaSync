package com.ajs.arenasync.Resources;

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
import com.ajs.arenasync.Services.TournamentService;

@RestController
@RequestMapping("/tournaments")
public class TournamentResource {

    @Autowired
    private TournamentService tournamentService;

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> findById(@PathVariable Long id) {
        Optional<Tournament> obj = tournamentService.findById(id);
        return obj.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tournament> insert(@RequestBody Tournament tournament) {
        Tournament savedTournament = tournamentService.save(tournament);
        return ResponseEntity.ok(savedTournament);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tournament> update(@PathVariable Long id, @RequestBody Tournament tournament) {
        Optional<Tournament> obj = tournamentService.findById(id);
        if (obj.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        tournament.setId(id);
        Tournament updatedTournament = tournamentService.save(tournament);
        return ResponseEntity.ok(updatedTournament);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tournamentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
