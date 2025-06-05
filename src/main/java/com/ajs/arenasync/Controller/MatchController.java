package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.MatchRequestDTO;
import com.ajs.arenasync.DTO.MatchResponseDTO;
import com.ajs.arenasync.Services.MatchService;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PostMapping
    public ResponseEntity<MatchResponseDTO> create(@RequestBody @Valid MatchRequestDTO dto) {
        MatchResponseDTO created = matchService.saveFromDTO(dto);
        created.add(linkTo(methodOn(MatchController.class).findById(created.getId())).withSelfRel());
        created.add(linkTo(methodOn(MatchController.class).findAll()).withRel("all-matches"));
        
        // Links para recursos relacionados (Teams, Tournament, LocationPlatform)
        try {
            created.add(linkTo(methodOn(TeamController.class).getTeamById(dto.getTeamAId())).withRel("team-a"));
            created.add(linkTo(methodOn(TeamController.class).getTeamById(dto.getTeamBId())).withRel("team-b"));
            created.add(linkTo(methodOn(TournamentController.class).getTournamentById(dto.getTournamentId())).withRel("tournament"));
            created.add(linkTo(methodOn(LocationPlatformController.class).findById(dto.getLocationPlatformId())).withRel("location-platform"));
        } catch (Exception e) {
            System.err.println("Erro ao tentar gerar link para recurso relacionado em create Match: " + e.getMessage());
        }

        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponseDTO> findById(@PathVariable Long id) {
        MatchResponseDTO response = matchService.findById(id);
        response.add(linkTo(methodOn(MatchController.class).findById(id)).withSelfRel());
        response.add(linkTo(methodOn(MatchController.class).findAll()).withRel("all-matches"));
        response.add(linkTo(methodOn(MatchController.class).deleteById(id)).withRel("delete"));
        
        // Links para recursos relacionados (Teams, Tournament, LocationPlatform)
        // Supondo que MatchResponseDTO tenha os IDs para gerar esses links
        // Se MatchResponseDTO não expõe os IDs diretamente, seria necessário ajustar o DTO ou o serviço
        // para obter esses IDs para a criação dos links HATEOAS.
        // Por exemplo, se você tem match.getTeamA().getId() no serviço, o responseDTO precisaria do teamAId.
        // Como o DTO de resposta atual não possui os IDs diretos das entidades relacionadas, não é possível criar esses links aqui apenas com o DTO de resposta.
        // Para fins de demonstração, vou comentar a parte que exige os IDs no DTO de resposta.
        /*
        if (response.getTeamAId() != null) { // Exemplo: se TeamAId estivesse no DTO de resposta
            try {
                response.add(linkTo(methodOn(TeamController.class).getTeamById(response.getTeamAId())).withRel("team-a"));
            } catch (Exception e) { }
        }
        */

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<MatchResponseDTO>> findAll() {
        List<MatchResponseDTO> list = matchService.findAll();
        for (MatchResponseDTO match : list) {
            match.add(linkTo(methodOn(MatchController.class).findById(match.getId())).withSelfRel());
            match.add(linkTo(methodOn(MatchController.class).deleteById(match.getId())).withRel("delete"));
            // Adicionar links para recursos relacionados individualmente, se os IDs estiverem disponíveis no DTO
        }
        Link selfLink = linkTo(methodOn(MatchController.class).findAll()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(list, selfLink));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        matchService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}