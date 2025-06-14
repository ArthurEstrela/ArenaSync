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
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/enrollments")
@Tag(name = "Enrollment Management", description = "Operações para gerenciar inscrições de times em torneios")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    // 🔹 Criar uma nova inscrição
    @PostMapping
    @Operation(summary = "Criar nova inscrição", description = "Cria uma nova inscrição de um time em um torneio")
    public ResponseEntity<EnrollmentResponseDTO> createEnrollment(@RequestBody @Valid EnrollmentRequestDTO dto) {
        EnrollmentResponseDTO savedEnrollment = enrollmentService.saveFromDTO(dto);
        savedEnrollment.add(linkTo(methodOn(EnrollmentController.class).getEnrollmentById(savedEnrollment.getId())).withSelfRel());
        savedEnrollment.add(linkTo(methodOn(EnrollmentController.class).getAllEnrollments()).withRel("all-enrollments"));

        // Links para recursos relacionados (Team, Tournament) - Agora usamos os IDs do DTO de resposta
        if (savedEnrollment.getTeamId() != null) {
            savedEnrollment.add(linkTo(methodOn(TeamController.class).getTeamById(savedEnrollment.getTeamId())).withRel("team"));
        }
        if (savedEnrollment.getTournamentId() != null) {
            savedEnrollment.add(linkTo(methodOn(TournamentController.class).getTournamentById(savedEnrollment.getTournamentId())).withRel("tournament"));
        }

        // Retorna com o status 201 Created
        return new ResponseEntity<>(savedEnrollment, HttpStatus.CREATED);
    }

    // 🔹 Buscar inscrição por ID
    @GetMapping("/{id}")
    @Operation(summary = "Obter inscrição por ID", description = "Retorna uma inscrição específica com base no seu ID")
    public ResponseEntity<EnrollmentResponseDTO> getEnrollmentById(
            @Parameter(description = "ID da inscrição a ser buscada", required = true) @PathVariable Long id) {
        EnrollmentResponseDTO dto = enrollmentService.findById(id);
        dto.add(linkTo(methodOn(EnrollmentController.class).getEnrollmentById(id)).withSelfRel());
        dto.add(linkTo(methodOn(EnrollmentController.class).getAllEnrollments()).withRel("all-enrollments"));
        dto.add(linkTo(methodOn(EnrollmentController.class).deleteEnrollment(id)).withRel("delete"));
        
        // Links para recursos relacionados (Team, Tournament)
        if (dto.getTeamId() != null) {
            dto.add(linkTo(methodOn(TeamController.class).getTeamById(dto.getTeamId())).withRel("team"));
        }
        if (dto.getTournamentId() != null) {
            dto.add(linkTo(methodOn(TournamentController.class).getTournamentById(dto.getTournamentId())).withRel("tournament"));
        }

        return ResponseEntity.ok(dto);
    }

    // 🔹 Listar todas as inscrições
    @GetMapping
    @Operation(summary = "Listar todas as inscrições", description = "Retorna uma lista de todas as inscrições registradas")
    public ResponseEntity<CollectionModel<EnrollmentResponseDTO>> getAllEnrollments() {
        List<EnrollmentResponseDTO> list = enrollmentService.findAll();
        
        for (EnrollmentResponseDTO enrollment : list) {
            enrollment.add(linkTo(methodOn(EnrollmentController.class).getEnrollmentById(enrollment.getId())).withSelfRel());
            enrollment.add(linkTo(methodOn(EnrollmentController.class).deleteEnrollment(enrollment.getId())).withRel("delete"));
            // Adicionar outros links relevantes para cada item, se necessário (team, tournament)
            if (enrollment.getTeamId() != null) {
                enrollment.add(linkTo(methodOn(TeamController.class).getTeamById(enrollment.getTeamId())).withRel("team"));
            }
            if (enrollment.getTournamentId() != null) {
                enrollment.add(linkTo(methodOn(TournamentController.class).getTournamentById(enrollment.getTournamentId())).withRel("tournament"));
            }
        }
        Link selfLink = linkTo(methodOn(EnrollmentController.class).getAllEnrollments()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(list, selfLink));
    }

    // 🔹 Deletar inscrição
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar inscrição", description = "Deleta uma inscrição do sistema pelo seu ID")
    public ResponseEntity<Void> deleteEnrollment(
            @Parameter(description = "ID da inscrição a ser deletada", required = true) @PathVariable Long id) {
        enrollmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
