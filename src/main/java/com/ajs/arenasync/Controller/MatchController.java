package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.Entities.Match;
import com.ajs.arenasync.Services.MatchService;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    // 🔹 Criar uma nova partida
    @PostMapping
    public ResponseEntity<Match> createMatch(@RequestBody Match match) {
        Match savedMatch = matchService.save(match);
        return ResponseEntity.ok(savedMatch);
    }

    // 🔹 Buscar partida por ID
    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatchById(@PathVariable Long id) {
        Match match = matchService.findById(id);
        return ResponseEntity.ok(match);
    }

    // 🔹 Listar todas as partidas
    @GetMapping
    public ResponseEntity<List<Match>> getAllMatches() {
        List<Match> matches = matchService.findAll();
        return ResponseEntity.ok(matches);
    }

    // 🔹 Atualizar uma partida
    @PutMapping("/{id}")
    public ResponseEntity<Match> updateMatch(@PathVariable Long id, @RequestBody Match updatedMatch) {
        Match existingMatch = matchService.findById(id);
        updatedMatch.setId(existingMatch.getId());
        Match savedMatch = matchService.save(updatedMatch);
        return ResponseEntity.ok(savedMatch);
    }

    // 🔹 Deletar partida por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        matchService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
