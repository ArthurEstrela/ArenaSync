package com.ajs.arenasync.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Import estático

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.StatisticRequestDTO;
import com.ajs.arenasync.DTO.StatisticResponseDTO;
import com.ajs.arenasync.Services.StatisticService;

import jakarta.validation.Valid;
// Não há um método getAllStatistics neste controller, então CollectionModel não é usado aqui.

@RestController
@RequestMapping("/api/statistics")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @PostMapping
    public ResponseEntity<StatisticResponseDTO> createStatistic(@Valid @RequestBody StatisticRequestDTO dto) {
        StatisticResponseDTO created = statisticService.save(dto);
        // Adicionar link "self"
        created.add(linkTo(methodOn(StatisticController.class).getStatisticById(created.getId())).withSelfRel());
        
        // Adicionar link para o jogador relacionado, se você tiver um PlayerController
        // Supondo que StatisticRequestDTO tenha playerId e você tenha PlayerController.getPlayerById(playerId)
        if (dto.getPlayerId() != null) {
            try {
                 // Você precisaria de uma forma de obter o ID do Player se ele não estiver no DTO de resposta,
                 // ou se o DTO de resposta tiver um campo para o ID do player.
                 // Para este exemplo, vamos supor que o PlayerController existe.
                created.add(linkTo(methodOn(PlayerController.class).getPlayerById(dto.getPlayerId())).withRel("player"));
            } catch (Exception e) {
                // Tratar exceção se o PlayerController ou método não existir ou se houver problema ao gerar link
                // Logar o erro, mas não quebrar a resposta principal.
                System.err.println("Erro ao tentar gerar link para player: " + e.getMessage());
            }
        }
        // Se houver um Match associado e um MatchController:
        // if (created.getMatchId() != null) { // Supondo que haja um getMatchId() ou similar
        //    created.add(linkTo(methodOn(MatchController.class).findById(created.getMatchId())).withRel("match"));
        // }

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatisticResponseDTO> getStatisticById(@PathVariable Long id) {
        StatisticResponseDTO statistic = statisticService.findById(id);
        // Adicionar link "self"
        statistic.add(linkTo(methodOn(StatisticController.class).getStatisticById(id)).withSelfRel());
        // Adicionar link para editar
        statistic.add(linkTo(methodOn(StatisticController.class).updateStatistic(id, null)).withRel("edit")); // null para DTO
        // Adicionar link para deletar
        statistic.add(linkTo(methodOn(StatisticController.class).deleteStatistic(id)).withRel("delete"));

        // Se StatisticResponseDTO contiver playerId ou matchId, adicione links para eles como no método create.
        // Exemplo (supondo que StatisticResponseDTO tenha getPlayerName() mas não o ID diretamente,
        // e StatisticRequestDTO foi usado para obter o playerId no `create`):
        // Para adicionar link ao player aqui, você precisaria do ID do player.
        // Se `statistic.getPlayerName()` não for suficiente para derivar um ID para o link,
        // você pode precisar adicionar `playerId` ao `StatisticResponseDTO`.

        return ResponseEntity.ok(statistic);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatisticResponseDTO> updateStatistic(@PathVariable Long id, @Valid @RequestBody StatisticRequestDTO dto) {
        StatisticResponseDTO updated = statisticService.update(id, dto);
        // Adicionar link "self"
        updated.add(linkTo(methodOn(StatisticController.class).getStatisticById(id)).withSelfRel());
        return ResponseEntity.ok(updated);
    }

    // DELETE - Respostas 204 não têm corpo para links.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatistic(@PathVariable Long id) {
        statisticService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}