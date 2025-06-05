package com.ajs.arenasync.Controller;

import com.ajs.arenasync.DTO.EnrollmentRequestDTO;
import com.ajs.arenasync.DTO.EnrollmentResponseDTO;
import com.ajs.arenasync.Services.EnrollmentService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    // 🔹 Criar uma nova inscrição
    @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> createEnrollment(@RequestBody @Valid EnrollmentRequestDTO dto) {
        EnrollmentResponseDTO savedEnrollment = enrollmentService.saveFromDTO(dto);
        savedEnrollment.add(linkTo(methodOn(EnrollmentController.class).getEnrollmentById(savedEnrollment.getId())).withSelfRel());
        savedEnrollment.add(linkTo(methodOn(EnrollmentController.class).getAllEnrollments()).withRel("all-enrollments"));

        // Links para recursos relacionados (Team, Tournament)
        try {
            savedEnrollment.add(linkTo(methodOn(TeamController.class).getTeamById(dto.getTeamId())).withRel("team"));
            savedEnrollment.add(linkTo(methodOn(TournamentController.class).getTournamentById(dto.getTournamentId())).withRel("tournament"));
        } catch (Exception e) {
            System.err.println("Erro ao tentar gerar link para recurso relacionado em createEnrollment: " + e.getMessage());
        }

        return ResponseEntity.ok(savedEnrollment);
    }

    // 🔹 Buscar inscrição por ID
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDTO> getEnrollmentById(@PathVariable Long id) {
        EnrollmentResponseDTO dto = enrollmentService.findById(id);
        dto.add(linkTo(methodOn(EnrollmentController.class).getEnrollmentById(id)).withSelfRel());
        dto.add(linkTo(methodOn(EnrollmentController.class).getAllEnrollments()).withRel("all-enrollments"));
        dto.add(linkTo(methodOn(EnrollmentController.class).deleteEnrollment(id)).withRel("delete"));
        
        // Links para recursos relacionados (Team, Tournament)
        // Como o DTO de resposta não possui os IDs diretos das entidades relacionadas, não é possível criar esses links aqui apenas com o DTO de resposta.
        // Se `EnrollmentResponseDTO` tivesse `teamId` e `tournamentId`, você poderia adicionar:
        /*
        if (dto.getTeamId() != null) {
            try {
                dto.add(linkTo(methodOn(TeamController.class).getTeamById(dto.getTeamId())).withRel("team"));
            } catch (Exception e) { }
        }
        if (dto.getTournamentId() != null) {
            try {
                dto.add(linkTo(methodOn(TournamentController.class).getTournamentById(dto.getTournamentId())).withRel("tournament"));
            } catch (Exception e) { }
        }
        */

        return ResponseEntity.ok(dto);
    }

    // 🔹 Listar todas as inscrições
    @GetMapping
    public ResponseEntity<CollectionModel<EnrollmentResponseDTO>> getAllEnrollments() {
        List<EnrollmentResponseDTO> list = enrollmentService.findAll();
        for (EnrollmentResponseDTO enrollment : list) {
            enrollment.add(linkTo(methodOn(EnrollmentController.class).getEnrollmentById(enrollment.getId())).withSelfRel());
            enrollment.add(linkTo(methodOn(EnrollmentController.class).deleteEnrollment(enrollment.getId())).withRel("delete"));
            // Adicionar links para recursos relacionados individualmente, se os IDs estiverem disponíveis no DTO
        }
        Link selfLink = linkTo(methodOn(EnrollmentController.class).getAllEnrollments()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(list, selfLink));
    }

    // 🔹 Deletar inscrição
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 