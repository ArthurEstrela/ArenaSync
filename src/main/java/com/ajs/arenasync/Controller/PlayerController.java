package com.ajs.arenasync.Controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ajs.arenasync.Entities.Player;
import com.ajs.arenasync.Services.PlayerService;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping("/{id}")
    public ResponseEntity<Player> findById(@PathVariable Long id) {
        Player obj = playerService.findById(id);
        return ResponseEntity.ok(obj); // Já lança exceção se não achar
    }

    @PostMapping
    public ResponseEntity<Player> insert(@RequestBody Player player) {
        Player savedPlayer = playerService.save(player);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlayer); // 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<Player> update(@PathVariable Long id, @RequestBody Player player) {
        Player existingPlayer = playerService.findById(id); // Lança exceção se não encontrar
        player.setId(existingPlayer.getId());
        Player updatedPlayer = playerService.save(player);
        return ResponseEntity.ok(updatedPlayer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        playerService.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/free-agents")
    public ResponseEntity<List<Player>> getFreeAgents() {
        List<Player> freeAgents = playerService.getFreeAgents();
        return ResponseEntity.ok(freeAgents);
    }
}
