package com.ajs.arenasync.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.StatisticRequestDTO;
import com.ajs.arenasync.DTO.StatisticResponseDTO;
import com.ajs.arenasync.Services.StatisticService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistic Management", description = "Operações para gerenciar estatísticas de jogadores e partidas")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @PostMapping
    @Operation(summary = "Criar nova estatística", description = "Cria uma nova estatística para um jogador ou partida")
    public ResponseEntity<StatisticResponseDTO> createStatistic(@Valid @RequestBody StatisticRequestDTO dto) {
        StatisticResponseDTO created = statisticService.save(dto);
        // CORREÇÃO AQUI: Adiciona verificação de nulidade antes de adicionar links HATEOAS
        if (created != null) {
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
        }
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter estatística por ID", description = "Retorna uma estatística específica com base no seu ID")
    public ResponseEntity<StatisticResponseDTO> getStatisticById(
            @Parameter(description = "ID da estatística a ser buscada", required = true) @PathVariable Long id) {
        StatisticResponseDTO statistic = statisticService.findById(id);
        statistic.add(linkTo(methodOn(StatisticController.class).getStatisticById(id)).withSelfRel());
        statistic.add(linkTo(methodOn(StatisticController.class).updateStatistic(id, new StatisticRequestDTO())).withRel("edit"));
        statistic.add(linkTo(methodOn(StatisticController.class).deleteStatistic(id)).withRel("delete"));

        statistic.add(linkTo(methodOn(StatisticController.class).getAllStatistics()).withRel("all-statistics"));

        return ResponseEntity.ok(statistic);
    }

    @GetMapping
    @Operation(summary = "Listar todas as estatísticas", description = "Retorna uma lista de todas as estatísticas registradas")
    public ResponseEntity<CollectionModel<StatisticResponseDTO>> getAllStatistics() {
        List<StatisticResponseDTO> statisticsList = statisticService.findAll();

        for (StatisticResponseDTO statistic : statisticsList) {
            // CORREÇÃO AQUI: Adiciona verificação de nulidade antes de adicionar links HATEOAS (para cada item na lista)
            if (statistic != null) {
                statistic.add(linkTo(methodOn(StatisticController.class).getStatisticById(statistic.getId())).withSelfRel());
                statistic.add(linkTo(methodOn(StatisticController.class).updateStatistic(statistic.getId(), new StatisticRequestDTO())).withRel("edit"));
                statistic.add(linkTo(methodOn(StatisticController.class).deleteStatistic(statistic.getId())).withRel("delete"));
            }
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
        updated.add(linkTo(methodOn(StatisticController.class).getAllStatistics()).withRel("all-statistics"));
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