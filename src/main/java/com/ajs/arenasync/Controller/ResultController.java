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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/results")
@Tag(name = "Result Management", description = "Operações para gerenciar resultados de partidas")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @PostMapping
    @Operation(summary = "Criar novo resultado", description = "Cria um novo resultado para uma partida. Uma partida só pode ter um resultado.")
    public ResponseEntity<ResultResponseDTO> createResult(@Valid @RequestBody ResultRequestDTO dto) {
        ResultResponseDTO created = resultService.save(dto);
        // CORREÇÃO AQUI: Adiciona verificação de nulidade antes de adicionar links HATEOAS
        if (created != null) {
            created.add(linkTo(methodOn(ResultController.class).getResultById(created.getId())).withSelfRel());
            // Adicionar link para a partida relacionada
            if (created.getMatchId() != null) {
                try {
                    created.add(linkTo(methodOn(MatchController.class).findById(created.getMatchId())).withRel("match"));
                } catch (Exception e) {
                     System.err.println("Erro ao tentar gerar link para match em createResult: " + e.getMessage());
                }
            }
            // Use ResultController.class para o método findAll que retorna CollectionModel
            created.add(linkTo(methodOn(ResultController.class).getAllResults()).withRel("all-results"));
        }
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter resultado por ID", description = "Retorna um resultado específico com base no seu ID")
    public ResponseEntity<ResultResponseDTO> getResultById(
            @Parameter(description = "ID do resultado a ser buscado", required = true) @PathVariable Long id) {
        ResultResponseDTO result = resultService.findById(id);
        if (result != null) {
            result.add(linkTo(methodOn(ResultController.class).getResultById(id)).withSelfRel());
            if (result.getMatchId() != null) {
                 try {
                    result.add(linkTo(methodOn(MatchController.class).findById(result.getMatchId())).withRel("match"));
                } catch (Exception e) {
                     System.err.println("Erro ao tentar gerar link para match em getResultById: " + e.getMessage());
                }
            }
            // Use ResultController.class para o método findAll que retorna CollectionModel
            result.add(linkTo(methodOn(ResultController.class).getAllResults()).withRel("all-results"));
            result.add(linkTo(methodOn(ResultController.class).deleteResult(id)).withRel("delete"));
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping
    @Operation(summary = "Listar todos os resultados", description = "Retorna uma lista de todos os resultados registrados")
    public ResponseEntity<CollectionModel<ResultResponseDTO>> getAllResults() {
        List<ResultResponseDTO> resultsList = resultService.findAll();
        
        for (ResultResponseDTO result : resultsList) {
            if (result != null) {
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
        }
        // CORREÇÃO AQUI: Use ResultController.class diretamente para o método findAll
        Link selfLink = linkTo(methodOn(ResultController.class).getAllResults()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(resultsList, selfLink));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar resultado", description = "Deleta um resultado do sistema pelo seu ID")
    public ResponseEntity<Void> deleteResult(
            @Parameter(description = "ID do resultado a ser deletado", required = true) @PathVariable Long id) {
        resultService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}