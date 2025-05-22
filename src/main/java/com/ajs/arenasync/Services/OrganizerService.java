package com.ajs.arenasync.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.Entities.Organizer;
import com.ajs.arenasync.Repositories.OrganizerRepository;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Exceptions.BusinessException;


@Service
public class OrganizerService {

    @Autowired
    private OrganizerRepository organizerRepository;

    // Buscar organizador por ID
    public Organizer getOrganizerById(Long id) {
        return organizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer", id));
    }

    // Listar todos os organizadores
    public List<Organizer> getAllOrganizers() {
        return organizerRepository.findAll();
    }

    // Criar um novo organizador
    public Organizer createOrganizer(Organizer organizer) {
        if (organizerRepository.findByEmail(organizer.getEmail()).isPresent()) {
            throw new BusinessException("Já existe um organizador com este e-mail.");
        }

        if (organizerRepository.existsByPhoneNumber(organizer.getPhoneNumber())) {
            throw new BusinessException("Já existe um organizador com este número de telefone.");
        }

        return organizerRepository.save(organizer);
    }

    // Atualizar um organizador existente
    public Organizer updateOrganizer(Long id, Organizer updatedOrganizer) {
        Organizer existing = organizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer", id));

        if (!existing.getEmail().equals(updatedOrganizer.getEmail()) &&
                organizerRepository.findByEmail(updatedOrganizer.getEmail()).isPresent()) {
            throw new BusinessException("E-mail já está em uso por outro organizador.");
        }

        if (!existing.getPhoneNumber().equals(updatedOrganizer.getPhoneNumber()) &&
                organizerRepository.existsByPhoneNumber(updatedOrganizer.getPhoneNumber())) {
            throw new BusinessException("Número de telefone já está em uso por outro organizador.");
        }

        existing.setName(updatedOrganizer.getName());
        existing.setEmail(updatedOrganizer.getEmail());
        existing.setPhoneNumber(updatedOrganizer.getPhoneNumber());
        existing.setOrganizationName(updatedOrganizer.getOrganizationName());
        existing.setBio(updatedOrganizer.getBio());
        existing.setSocialLinks(updatedOrganizer.getSocialLinks());

        return organizerRepository.save(existing);
    }

    // Deletar organizador
    public void deleteOrganizer(Long id) {
        Organizer existing = organizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer", id));

        if (existing.getTournaments() != null && !existing.getTournaments().isEmpty()) {
            throw new BusinessException("Não é possível excluir um organizador que possui torneios associados.");
        }

        organizerRepository.delete(existing);
    }
}
