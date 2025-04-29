package com.ajs.arenasync.Resources;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ajs.arenasync.Entities.Match;
import com.ajs.arenasync.Services.MatchService;

@RestController
@RequestMapping("/matches")
public class MatchResource {

    @Autowired
    private MatchService matchService;

    @GetMapping("/{id}")
    public ResponseEntity<Match> findById(@PathVariable Long id) {
        Optional<Match> obj = matchService.findById(id);
        return obj.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Match> insert(@RequestBody Match match) {
        Match savedMatch = matchService.save(match);
        return ResponseEntity.ok(savedMatch);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Match> update(@PathVariable Long id, @RequestBody Match match) {
        Optional<Match> obj = matchService.findById(id);
        if (obj.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        match.setId(id);
        Match updatedMatch = matchService.save(match);
        return ResponseEntity.ok(updatedMatch);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        matchService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
