package com.ajs.arenasync.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.DTO.EnrollmentRequestDTO;
import com.ajs.arenasync.DTO.EnrollmentResponseDTO;
import com.ajs.arenasync.Entities.Enrollment;
import com.ajs.arenasync.Entities.Team;
import com.ajs.arenasync.Entities.Tournament;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.EnrollmentRepository;
import com.ajs.arenasync.Repositories.TeamRepository;
import com.ajs.arenasync.Repositories.TournamentRepository;

@Service
@CacheConfig(cacheNames = "enrollments")
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @CacheEvict(allEntries = true)
    public EnrollmentResponseDTO saveFromDTO(EnrollmentRequestDTO dto) {
        Enrollment enrollment = toEntity(dto);
        validateEnrollment(enrollment);
        Enrollment saved = enrollmentRepository.save(enrollment);
        return toResponseDTO(saved);
    }

    @Cacheable(key = "#id")
    public EnrollmentResponseDTO findById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscrição", id));
        return toResponseDTO(enrollment);
    }

    @Cacheable
    public List<EnrollmentResponseDTO> findAll() {
        return enrollmentRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Caching(evict = {
        @CacheEvict(key = "#id"),
        @CacheEvict(allEntries = true)
    })
    public void deleteById(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inscrição", id);
        }
        enrollmentRepository.deleteById(id);
    }

    private Enrollment toEntity(EnrollmentRequestDTO dto) {
        Team team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new BadRequestException("Time informado não encontrado."));

        Tournament tournament = tournamentRepository.findById(dto.getTournamentId())
                .orElseThrow(() -> new BadRequestException("Torneio informado não encontrado."));

        Enrollment enrollment = new Enrollment();
        enrollment.setStatus(dto.getStatus());
        enrollment.setTeam(team);
        enrollment.setTournament(tournament);
        return enrollment;
    }

    public EnrollmentResponseDTO toResponseDTO(Enrollment enrollment) {
        EnrollmentResponseDTO dto = new EnrollmentResponseDTO();
        dto.setId(enrollment.getId());
        // Popula os IDs no DTO de resposta
        dto.setTeamId(enrollment.getTeam().getId());
        dto.setTournamentId(enrollment.getTournament().getId());
        
        dto.setTeamName(enrollment.getTeam().getName());
        dto.setTournamentName(enrollment.getTournament().getName());
        dto.setStatus(enrollment.getStatus().name());
        return dto;
    }

    private void validateEnrollment(Enrollment enrollment) {
        Team team = enrollment.getTeam();
        Tournament tournament = enrollment.getTournament();

        if (team == null || team.getId() == null) {
            throw new BadRequestException("O time da inscrição deve ser informado.");
        }

        if (tournament == null || tournament.getId() == null) {
            throw new BadRequestException("O torneio da inscrição deve ser informado.");
        }

        if (enrollment.getStatus() == null) {
            throw new BadRequestException("O status da inscrição é obrigatório.");
        }

        if (enrollmentRepository.existsByTeamAndTournament(team, tournament)
                && (enrollment.getId() == null)) {
            throw new BadRequestException("Esse time já está inscrito neste torneio.");
        }
    }
}
