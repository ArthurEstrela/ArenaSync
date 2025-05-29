package com.ajs.arenasync.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.DTO.OrganizerRequestDTO;
import com.ajs.arenasync.DTO.OrganizerResponseDTO;
import com.ajs.arenasync.Entities.Organizer;
import com.ajs.arenasync.Exceptions.BusinessException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.OrganizerRepository;

@Service
public class OrganizerService {

    @Autowired
    private OrganizerRepository organizerRepository;

    public OrganizerResponseDTO getOrganizerById(Long id) {
        Organizer organizer = organizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer", id));
        return toResponseDTO(organizer);
    }

    public List<OrganizerResponseDTO> getAllOrganizers() {
        return organizerRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public OrganizerResponseDTO createOrganizer(OrganizerRequestDTO dto) {
        if (organizerRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("Já existe um organizador com este e-mail.");
        }

        if (organizerRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new BusinessException("Já existe um organizador com este número de telefone.");
        }

        Organizer organizer = toEntity(dto);
        return toResponseDTO(organizerRepository.save(organizer));
    }

    public OrganizerResponseDTO updateOrganizer(Long id, OrganizerRequestDTO dto) {
        Organizer existing = organizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer", id));

        if (!existing.getEmail().equals(dto.getEmail()) &&
                organizerRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("E-mail já está em uso por outro organizador.");
        }

        if (!existing.getPhoneNumber().equals(dto.getPhoneNumber()) &&
                organizerRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new BusinessException("Número de telefone já está em uso por outro organizador.");
        }

        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setPhoneNumber(dto.getPhoneNumber());
        existing.setOrganizationName(dto.getOrganizationName());
        existing.setBio(dto.getBio());
        existing.setSocialLinks(dto.getSocialLinks());

        return toResponseDTO(organizerRepository.save(existing));
    }

    public void deleteOrganizer(Long id) {
        Organizer existing = organizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer", id));

        if (existing.getTournaments() != null && !existing.getTournaments().isEmpty()) {
            throw new BusinessException("Não é possível excluir um organizador que possui torneios associados.");
        }

        organizerRepository.delete(existing);
    }

    // Conversões DTO <-> Entidade
    private Organizer toEntity(OrganizerRequestDTO dto) {
        Organizer organizer = new Organizer();
        organizer.setName(dto.getName());
        organizer.setEmail(dto.getEmail());
        organizer.setPhoneNumber(dto.getPhoneNumber());
        organizer.setOrganizationName(dto.getOrganizationName());
        organizer.setBio(dto.getBio());
        organizer.setSocialLinks(dto.getSocialLinks());
        return organizer;
    }

    private OrganizerResponseDTO toResponseDTO(Organizer entity) {
        OrganizerResponseDTO dto = new OrganizerResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setOrganizationName(entity.getOrganizationName());
        dto.setBio(entity.getBio());
        dto.setSocialLinks(entity.getSocialLinks());
        return dto;
    }
}
