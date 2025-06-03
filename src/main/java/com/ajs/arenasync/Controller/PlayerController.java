package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Import estático

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
        // Adicionar link "self"
        created.add(linkTo(methodOn(PlayerController.class).getPlayerById(created.getId())).withSelfRel());
        // Adicionar link para o time, se houver e se tivermos o ID do time
        if (dto.getTeamId() != null) { // Usando o ID do DTO de requisição para criar o link
            try {
                created.add(linkTo(methodOn(TeamController.class).getTeamById(dto.getTeamId())).withRel("team"));
            } catch (Exception e) {
                 System.err.println("Erro ao tentar gerar link para team em createPlayer: " + e.getMessage());
            }
        }
        created.add(linkTo(methodOn(PlayerController.class).getAllPlayers()).withRel("all-players"));
        created.add(linkTo(methodOn(PlayerController.class).getFreeAgents()).withRel("free-agents"));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponseDTO> getPlayerById(@PathVariable Long id) {
        PlayerResponseDTO player = playerService.findById(id);
        // Adicionar link "self"
        player.add(linkTo(methodOn(PlayerController.class).getPlayerById(id)).withSelfRel());
        player.add(linkTo(methodOn(PlayerController.class).getAllPlayers()).withRel("all-players"));
        player.add(linkTo(methodOn(PlayerController.class).getFreeAgents()).withRel("free-agents"));

        // Se PlayerResponseDTO tivesse teamId, poderíamos adicionar o link para o time aqui.
        // Exemplo:
        // if (player.getTeamId() != null) {
        //     try {
        //        player.add(linkTo(methodOn(TeamController.class).getTeamById(player.getTeamId())).withRel("team"));
        //     } catch (Exception e) {
        //        System.err.println("Erro ao tentar gerar link para team em getPlayerById: " + e.getMessage());
        //     }
        // }
        return ResponseEntity.ok(player);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<PlayerResponseDTO>> getAllPlayers() {
        List<PlayerResponseDTO> playersList = playerService.findAll();
        
        for (PlayerResponseDTO player : playersList) {
            player.add(linkTo(methodOn(PlayerController.class).getPlayerById(player.getId())).withSelfRel());
            // Adicionar link para o time de cada jogador se a informação (ID) estiver no DTO
        }
        Link selfLink = linkTo(methodOn(PlayerController.class).getAllPlayers()).withSelfRel();
        Link freeAgentsLink = linkTo(methodOn(PlayerController.class).getFreeAgents()).withRel("free-agents");
        CollectionModel<PlayerResponseDTO> collectionModel = CollectionModel.of(playersList, selfLink, freeAgentsLink);
        
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/free-agents")
    public ResponseEntity<CollectionModel<PlayerResponseDTO>> getFreeAgents() {
        List<PlayerResponseDTO> freeAgentsList = playerService.getFreeAgents();
        
        for (PlayerResponseDTO player : freeAgentsList) {
            player.add(linkTo(methodOn(PlayerController.class).getPlayerById(player.getId())).withSelfRel());
        }
        Link selfLink = linkTo(methodOn(PlayerController.class).getFreeAgents()).withSelfRel();
        Link allPlayersLink = linkTo(methodOn(PlayerController.class).getAllPlayers()).withRel("all-players");
        CollectionModel<PlayerResponseDTO> collectionModel = CollectionModel.of(freeAgentsList, selfLink, allPlayersLink);
        
        return ResponseEntity.ok(collectionModel);
    }

    // DELETE - Respostas 204 não têm corpo para links.
    // Não há PUT para jogadores neste controller.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}