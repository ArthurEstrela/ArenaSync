package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.ResultRequestDTO;
import com.ajs.arenasync.DTO.ResultResponseDTO;
import com.ajs.arenasync.Services.ResultService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation; // Importe esta anotação
import io.swagger.v3.oas.annotations.tags.Tag; // Importe esta anotação
import io.swagger.v3.oas.annotations.Parameter; // Importe esta anotação para documentar PathVariable

@RestController
@RequestMapping("/api/results")
@Tag(name = "Result Management", description = "Operações para gerenciar resultados de partidas") // Anotação na classe
public class ResultController {

    @Autowired
    private ResultService resultService;

    @PostMapping
    @Operation(summary = "Criar novo resultado", description = "Cria um novo resultado para uma partida. Uma partida só pode ter um resultado.")
    public ResponseEntity<ResultResponseDTO> createResult(@Valid @RequestBody ResultRequestDTO dto) {
        ResultResponseDTO created = resultService.save(dto);
        created.add(linkTo(methodOn(ResultController.class).getResultById(created.getId())).withSelfRel());
        // Adicionar link para a partida relacionada
        if (created.getMatchId() != null) {
            try {
                created.add(linkTo(methodOn(MatchController.class).findById(created.getMatchId())).withRel("match"));
            } catch (Exception e) {
                 System.err.println("Erro ao tentar gerar link para match em createResult: " + e.getMessage());
            }
        }
        created.add(linkTo(methodOn(ResultController.class).getAllResults()).withRel("all-results"));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter resultado por ID", description = "Retorna um resultado específico com base no seu ID")
    public ResponseEntity<ResultResponseDTO> getResultById(
            @Parameter(description = "ID do resultado a ser buscado", required = true) @PathVariable Long id) {
        ResultResponseDTO result = resultService.findById(id);
        result.add(linkTo(methodOn(ResultController.class).getResultById(id)).withSelfRel());
        // Adicionar link para a partida relacionada
        if (result.getMatchId() != null) {
             try {
                result.add(linkTo(methodOn(MatchController.class).findById(result.getMatchId())).withRel("match"));
            } catch (Exception e) {
                 System.err.println("Erro ao tentar gerar link para match em getResultById: " + e.getMessage());
            }
        }
        result.add(linkTo(methodOn(ResultController.class).getAllResults()).withRel("all-results"));
        result.add(linkTo(methodOn(ResultController.class).deleteResult(id)).withRel("delete")); // Link para DELETE
        return ResponseEntity.ok(result);
    }

    @GetMapping
    @Operation(summary = "Listar todos os resultados", description = "Retorna uma lista de todos os resultados registrados")
    public ResponseEntity<CollectionModel<ResultResponseDTO>> getAllResults() {
        List<ResultResponseDTO> resultsList = resultService.findAll(); // Assume que ResultService.findAll() existe e retorna List<ResultResponseDTO>
        
        for (ResultResponseDTO result : resultsList) {
            result.add(linkTo(methodOn(ResultController.class).getResultById(result.getId())).withSelfRel());
            if (result.getMatchId() != null) {
                try {
                    result.add(linkTo(methodOn(MatchController.class).findById(result.getMatchId())).withRel("match"));
                } catch (Exception e) {
                    System.err.println("Erro ao tentar gerar link para match em getAllResults: " + e.getMessage());
                }
            }
            result.add(linkTo(methodOn(ResultController.class).deleteResult(result.getId())).withRel("delete"));
        }
        Link selfLink = linkTo(methodOn(ResultController.class).getAllResults()).withSelfRel();
        CollectionModel<ResultResponseDTO> collectionModel = CollectionModel.of(resultsList, selfLink);
        
        return ResponseEntity.ok(collectionModel);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar resultado", description = "Deleta um resultado do sistema pelo seu ID")
    public ResponseEntity<Void> deleteResult(
            @Parameter(description = "ID do resultado a ser deletado", required = true) @PathVariable Long id) {
        resultService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}