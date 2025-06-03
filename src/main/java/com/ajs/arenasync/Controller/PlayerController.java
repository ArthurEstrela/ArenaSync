package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.PlayerRequestDTO;
import com.ajs.arenasync.DTO.PlayerResponseDTO;
import com.ajs.arenasync.Services.PlayerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @PostMapping
    public ResponseEntity<PlayerResponseDTO> createPlayer(@Valid @RequestBody PlayerRequestDTO dto) {
        PlayerResponseDTO created = playerService.saveFromDTO(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponseDTO> getPlayerById(@PathVariable Long id) {
        PlayerResponseDTO player = playerService.findById(id);
        return ResponseEntity.ok(player);
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponseDTO>> getAllPlayers() {
        List<PlayerResponseDTO> players = playerService.findAll();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/free-agents")
    public ResponseEntity<List<PlayerResponseDTO>> getFreeAgents() {
        List<PlayerResponseDTO> freeAgents = playerService.getFreeAgents();
        return ResponseEntity.ok(freeAgents);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
