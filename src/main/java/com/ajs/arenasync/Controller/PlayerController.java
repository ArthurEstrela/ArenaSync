package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.PlayerRequestDTO;
import com.ajs.arenasync.DTO.PlayerResponseDTO;
import com.ajs.arenasync.Services.PlayerService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation; // Importe esta anotação
import io.swagger.v3.oas.annotations.tags.Tag; // Importe esta anotação
import io.swagger.v3.oas.annotations.Parameter; // Importe esta anotação para documentar PathVariable

@RestController
@RequestMapping("/api/players")
@Tag(name = "Player Management", description = "Operações para gerenciar jogadores") // Anotação na classe
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @PostMapping
    @Operation(summary = "Criar novo jogador", description = "Cria um novo jogador no sistema, com ou sem associação a um time")
    public ResponseEntity<PlayerResponseDTO> createPlayer(@Valid @RequestBody PlayerRequestDTO dto) {
        PlayerResponseDTO created = playerService.saveFromDTO(dto);
        created.add(linkTo(methodOn(PlayerController.class).getPlayerById(created.getId())).withSelfRel());
        // Adicionar link para o time, se houver e se tivermos o ID do time
        if (dto.getTeamId() != null) {
            try {
                // Supondo que TeamController.getTeamById() exista e aceite Long como parâmetro.
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
    @Operation(summary = "Obter jogador por ID", description = "Retorna um jogador específico com base no seu ID")
    public ResponseEntity<PlayerResponseDTO> getPlayerById(
            @Parameter(description = "ID do jogador a ser buscado", required = true) @PathVariable Long id) {
        PlayerResponseDTO player = playerService.findById(id);
        player.add(linkTo(methodOn(PlayerController.class).getPlayerById(id)).withSelfRel());
        player.add(linkTo(methodOn(PlayerController.class).getAllPlayers()).withRel("all-players"));
        player.add(linkTo(methodOn(PlayerController.class).getFreeAgents()).withRel("free-agents"));
        player.add(linkTo(methodOn(PlayerController.class).deletePlayer(id)).withRel("delete")); // Link para DELETE

        // Se PlayerResponseDTO tivesse teamId, poderíamos adicionar o link para o time aqui.
        // Exemplo:
        // if (player.getTeamId() != null) { // Supondo que PlayerResponseDTO tivesse getTeamId()
        //     try {
        //        player.add(linkTo(methodOn(TeamController.class).getTeamById(player.getTeamId())).withRel("team"));
        //     } catch (Exception e) {
        //        System.err.println("Erro ao tentar gerar link para team em getPlayerById: " + e.getMessage());
        //     }
        // }
        return ResponseEntity.ok(player);
    }

    @GetMapping
    @Operation(summary = "Listar todos os jogadores", description = "Retorna uma lista de todos os jogadores registrados")
    public ResponseEntity<CollectionModel<PlayerResponseDTO>> getAllPlayers() {
        // Assume que PlayerService.findAll() existe e retorna List<PlayerResponseDTO>
        List<PlayerResponseDTO> playersList = playerService.findAll(); 
        
        for (PlayerResponseDTO player : playersList) {
            player.add(linkTo(methodOn(PlayerController.class).getPlayerById(player.getId())).withSelfRel());
            player.add(linkTo(methodOn(PlayerController.class).deletePlayer(player.getId())).withRel("delete"));
            // Adicionar link para o time de cada jogador se a informação (ID) estiver no DTO
        }
        Link selfLink = linkTo(methodOn(PlayerController.class).getAllPlayers()).withSelfRel();
        Link freeAgentsLink = linkTo(methodOn(PlayerController.class).getFreeAgents()).withRel("free-agents");
        CollectionModel<PlayerResponseDTO> collectionModel = CollectionModel.of(playersList, selfLink, freeAgentsLink);
        
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/free-agents")
    @Operation(summary = "Listar agentes livres", description = "Retorna uma lista de todos os jogadores que não estão associados a nenhum time")
    public ResponseEntity<CollectionModel<PlayerResponseDTO>> getFreeAgents() {
        // Assume que PlayerService.getFreeAgents() existe e retorna List<PlayerResponseDTO>
        List<PlayerResponseDTO> freeAgentsList = playerService.getFreeAgents(); 
        
        for (PlayerResponseDTO player : freeAgentsList) {
            player.add(linkTo(methodOn(PlayerController.class).getPlayerById(player.getId())).withSelfRel());
            player.add(linkTo(methodOn(PlayerController.class).deletePlayer(player.getId())).withRel("delete"));
        }
        Link selfLink = linkTo(methodOn(PlayerController.class).getFreeAgents()).withSelfRel();
        Link allPlayersLink = linkTo(methodOn(PlayerController.class).getAllPlayers()).withRel("all-players");
        CollectionModel<PlayerResponseDTO> collectionModel = CollectionModel.of(freeAgentsList, selfLink, allPlayersLink);
        
        return ResponseEntity.ok(collectionModel);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar jogador", description = "Deleta um jogador do sistema pelo seu ID")
    public ResponseEntity<Void> deletePlayer(
            @Parameter(description = "ID do jogador a ser deletado", required = true) @PathVariable Long id) {
        playerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}