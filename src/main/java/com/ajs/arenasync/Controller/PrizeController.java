package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.PrizeRequestDTO;
import com.ajs.arenasync.DTO.PrizeResponseDTO;
import com.ajs.arenasync.Services.PrizeService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/prizes")
@Tag(name = "Prize Management", description = "Operações para gerenciar prêmios de torneios")
public class PrizeController {

    @Autowired
    private PrizeService prizeService;

    @PostMapping
    @Operation(summary = "Criar novo prêmio", description = "Cria um novo prêmio e o associa a um torneio existente")
    public ResponseEntity<PrizeResponseDTO> createPrize(@Valid @RequestBody PrizeRequestDTO dto) {
        PrizeResponseDTO created = prizeService.save(dto);
        // CORREÇÃO AQUI: Adiciona verificação de nulidade antes de adicionar links HATEOAS
        if (created != null) {
            created.add(linkTo(methodOn(PrizeController.class).getPrizeById(created.getId())).withSelfRel());
            // Adicionar link para o torneio relacionado
            if (dto.getTournamentId() != null) {
                try {
                    created.add(linkTo(methodOn(TournamentController.class).getTournamentById(dto.getTournamentId())).withRel("tournament"));
                } catch (Exception e) {
                     System.err.println("Erro ao tentar gerar link para tournament em createPrize: " + e.getMessage());
                }
            }
            created.add(linkTo(methodOn(PrizeController.class).getAllPrizes()).withRel("all-prizes"));
        }
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter prêmio por ID", description = "Retorna um prêmio específico com base no seu ID")
    public ResponseEntity<PrizeResponseDTO> getPrizeById(
            @Parameter(description = "ID do prêmio a ser buscado", required = true) @PathVariable Long id) {
        PrizeResponseDTO prize = prizeService.findById(id);
        prize.add(linkTo(methodOn(PrizeController.class).getPrizeById(id)).withSelfRel());
        prize.add(linkTo(methodOn(PrizeController.class).getAllPrizes()).withRel("all-prizes"));
        prize.add(linkTo(methodOn(PrizeController.class).deletePrize(id)).withRel("delete"));
        
        return ResponseEntity.ok(prize);
    }

    @GetMapping
    @Operation(summary = "Listar todos os prêmios", description = "Retorna uma lista de todos os prêmios registrados")
    public ResponseEntity<CollectionModel<PrizeResponseDTO>> getAllPrizes() {
        List<PrizeResponseDTO> prizesList = prizeService.findAll();
        
        for (PrizeResponseDTO prize : prizesList) {
            prize.add(linkTo(methodOn(PrizeController.class).getPrizeById(prize.getId())).withSelfRel());
            prize.add(linkTo(methodOn(PrizeController.class).deletePrize(prize.getId())).withRel("delete"));
        }
        Link selfLink = linkTo(methodOn(PrizeController.class).getAllPrizes()).withSelfRel();
        CollectionModel<PrizeResponseDTO> collectionModel = CollectionModel.of(prizesList, selfLink);
        
        return ResponseEntity.ok(collectionModel);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar prêmio", description = "Deleta um prêmio do sistema pelo seu ID")
    public ResponseEntity<Void> deletePrize(
            @Parameter(description = "ID do prêmio a ser deletado", required = true) @PathVariable Long id) {
        prizeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}