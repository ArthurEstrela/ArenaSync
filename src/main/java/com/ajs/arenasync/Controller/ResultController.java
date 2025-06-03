package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Import estático

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.ResultRequestDTO;
import com.ajs.arenasync.DTO.ResultResponseDTO;
import com.ajs.arenasync.Services.ResultService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @PostMapping
    public ResponseEntity<ResultResponseDTO> createResult(@Valid @RequestBody ResultRequestDTO dto) {
        ResultResponseDTO created = resultService.save(dto);
        // Adicionar link "self"
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
    public ResponseEntity<ResultResponseDTO> getResultById(@PathVariable Long id) {
        ResultResponseDTO result = resultService.findById(id);
        // Adicionar link "self"
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
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<ResultResponseDTO>> getAllResults() {
        List<ResultResponseDTO> resultsList = resultService.findAll();
        
        for (ResultResponseDTO result : resultsList) {
            result.add(linkTo(methodOn(ResultController.class).getResultById(result.getId())).withSelfRel());
            if (result.getMatchId() != null) {
                try {
                    result.add(linkTo(methodOn(MatchController.class).findById(result.getMatchId())).withRel("match"));
                } catch (Exception e) {
                    System.err.println("Erro ao tentar gerar link para match em getAllResults: " + e.getMessage());
                }
            }
        }
        Link selfLink = linkTo(methodOn(ResultController.class).getAllResults()).withSelfRel();
        CollectionModel<ResultResponseDTO> collectionModel = CollectionModel.of(resultsList, selfLink);
        
        return ResponseEntity.ok(collectionModel);
    }

    // DELETE - Respostas 204 não têm corpo para links.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        resultService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}