package com.ajs.arenasync.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel; // Para retornar listas com HATEOAS
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.StatisticRequestDTO;
import com.ajs.arenasync.DTO.StatisticResponseDTO;
import com.ajs.arenasync.Services.StatisticService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation; // Importe esta anotação
import io.swagger.v3.oas.annotations.tags.Tag; // Importe esta anotação
import io.swagger.v3.oas.annotations.Parameter; // Importe esta anotação para documentar PathVariable/RequestParam

import java.util.List; // Importar List para getAllStatistics

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistic Management", description = "Operações para gerenciar estatísticas de jogadores e partidas") // Anotação na classe
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @PostMapping
    @Operation(summary = "Criar nova estatística", description = "Cria uma nova estatística para um jogador ou partida")
    public ResponseEntity<StatisticResponseDTO> createStatistic(@Valid @RequestBody StatisticRequestDTO dto) {
        StatisticResponseDTO created = statisticService.save(dto);
        created.add(linkTo(methodOn(StatisticController.class).getStatisticById(created.getId())).withSelfRel());
        
        // Adicionar link para o jogador relacionado
        if (dto.getPlayerId() != null) {
            try {
                created.add(linkTo(methodOn(PlayerController.class).getPlayerById(dto.getPlayerId())).withRel("player"));
            } catch (Exception e) {
                System.err.println("Erro ao tentar gerar link para player: " + e.getMessage());
            }
        }
        // Adicionar link para a coleção de todas as estatísticas
        created.add(linkTo(methodOn(StatisticController.class).getAllStatistics()).withRel("all-statistics"));

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter estatística por ID", description = "Retorna uma estatística específica com base no seu ID")
    public ResponseEntity<StatisticResponseDTO> getStatisticById(
            @Parameter(description = "ID da estatística a ser buscada", required = true) @PathVariable Long id) {
        StatisticResponseDTO statistic = statisticService.findById(id);
        statistic.add(linkTo(methodOn(StatisticController.class).getStatisticById(id)).withSelfRel());
        statistic.add(linkTo(methodOn(StatisticController.class).updateStatistic(id, null)).withRel("edit"));
        statistic.add(linkTo(methodOn(StatisticController.class).deleteStatistic(id)).withRel("delete"));

        // Adicionar link para a coleção de todas as estatísticas
        statistic.add(linkTo(methodOn(StatisticController.class).getAllStatistics()).withRel("all-statistics"));

        // Se StatisticResponseDTO contiver playerId (que ele tem, através do getPlayerName()),
        // e você quiser o link para o Player, precisaria que o StatisticResponseDTO expusesse o playerId.
        // Como ele não expõe o ID diretamente, vamos focar nos que são possíveis.

        return ResponseEntity.ok(statistic);
    }

    // NOVO MÉTODO: Listar todas as estatísticas para HATEOAS da coleção
    @GetMapping
    @Operation(summary = "Listar todas as estatísticas", description = "Retorna uma lista de todas as estatísticas registradas")
    public ResponseEntity<CollectionModel<StatisticResponseDTO>> getAllStatistics() {
        List<StatisticResponseDTO> statisticsList = statisticService.findAll(); // Assume que StatisticService.findAll() existe e retorna List<StatisticResponseDTO>

        for (StatisticResponseDTO statistic : statisticsList) {
            statistic.add(linkTo(methodOn(StatisticController.class).getStatisticById(statistic.getId())).withSelfRel());
            statistic.add(linkTo(methodOn(StatisticController.class).updateStatistic(statistic.getId(), null)).withRel("edit"));
            statistic.add(linkTo(methodOn(StatisticController.class).deleteStatistic(statistic.getId())).withRel("delete"));
            // Se o StatisticResponseDTO tivesse o PlayerId, você poderia adicionar o link para o Player aqui também.
        }
        Link selfLink = linkTo(methodOn(StatisticController.class).getAllStatistics()).withSelfRel();
        CollectionModel<StatisticResponseDTO> collectionModel = CollectionModel.of(statisticsList, selfLink);
        return ResponseEntity.ok(collectionModel);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Atualizar estatística existente", description = "Atualiza as informações de uma estatística existente pelo seu ID")
    public ResponseEntity<StatisticResponseDTO> updateStatistic(
            @Parameter(description = "ID da estatística a ser atualizada", required = true) @PathVariable Long id,
            @Valid @RequestBody StatisticRequestDTO dto) {
        StatisticResponseDTO updated = statisticService.update(id, dto);
        updated.add(linkTo(methodOn(StatisticController.class).getStatisticById(id)).withSelfRel());
        updated.add(linkTo(methodOn(StatisticController.class).getAllStatistics()).withRel("all-statistics")); // Adicionado link para a coleção
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar estatística", description = "Deleta uma estatística do sistema pelo seu ID")
    public ResponseEntity<Void> deleteStatistic(
            @Parameter(description = "ID da estatística a ser deletada", required = true) @PathVariable Long id) {
        statisticService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}