package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Import estático

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.PrizeRequestDTO;
import com.ajs.arenasync.DTO.PrizeResponseDTO;
import com.ajs.arenasync.Services.PrizeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/prizes")
public class PrizeController {

    @Autowired
    private PrizeService prizeService;

    @PostMapping
    public ResponseEntity<PrizeResponseDTO> createPrize(@Valid @RequestBody PrizeRequestDTO dto) {
        PrizeResponseDTO created = prizeService.save(dto);
        // Adicionar link "self"
        created.add(linkTo(methodOn(PrizeController.class).getPrizeById(created.getId())).withSelfRel());
        // Adicionar link para o torneio relacionado
        // Para isso, precisaríamos do ID do torneio. Se PrizeRequestDTO tem tournamentId:
        if (dto.getTournamentId() != null) {
            try {
                created.add(linkTo(methodOn(TournamentController.class).getTournamentById(dto.getTournamentId())).withRel("tournament"));
            } catch (Exception e) {
                 System.err.println("Erro ao tentar gerar link para tournament em createPrize: " + e.getMessage());
            }
        }
        created.add(linkTo(methodOn(PrizeController.class).getAllPrizes()).withRel("all-prizes"));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrizeResponseDTO> getPrizeById(@PathVariable Long id) {
        PrizeResponseDTO prize = prizeService.findById(id);
        // Adicionar link "self"
        prize.add(linkTo(methodOn(PrizeController.class).getPrizeById(id)).withSelfRel());
        prize.add(linkTo(methodOn(PrizeController.class).getAllPrizes()).withRel("all-prizes"));
        
        // Se PrizeResponseDTO tivesse tournamentId, poderíamos adicionar o link para o torneio aqui.
        // Exemplo:
        // if (prize.getTournamentId() != null) {
        //     try {
        //        prize.add(linkTo(methodOn(TournamentController.class).getTournamentById(prize.getTournamentId())).withRel("tournament"));
        //     } catch (Exception e) {
        //        System.err.println("Erro ao tentar gerar link para tournament em getPrizeById: " + e.getMessage());
        //     }
        // }
        return ResponseEntity.ok(prize);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<PrizeResponseDTO>> getAllPrizes() {
        List<PrizeResponseDTO> prizesList = prizeService.findAll();
        
        for (PrizeResponseDTO prize : prizesList) {
            prize.add(linkTo(methodOn(PrizeController.class).getPrizeById(prize.getId())).withSelfRel());
            // Adicionar link para o respectivo torneio se a informação (ID) estiver disponível no DTO
        }
        Link selfLink = linkTo(methodOn(PrizeController.class).getAllPrizes()).withSelfRel();
        CollectionModel<PrizeResponseDTO> collectionModel = CollectionModel.of(prizesList, selfLink);
        
        return ResponseEntity.ok(collectionModel);
    }

    // DELETE - Respostas 204 não têm corpo para links.
    // Não há PUT para prêmios neste controller.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrize(@PathVariable Long id) {
        prizeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}