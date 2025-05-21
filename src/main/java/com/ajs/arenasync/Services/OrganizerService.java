package com.ajs.arenasync.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.Entities.Organizer;
import com.ajs.arenasync.Repositories.OrganizerRepository;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;

@Service
public class OrganizerService {

    @Autowired
    private OrganizerRepository organizerRepository;

    // Buscar organizador por ID
    public Organizer getOrganizerById(Long id) {
        return organizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with id: " , id));
    }

    // Listar todos os organizadores
    public List<Organizer> getAllOrganizers() {
        return organizerRepository.findAll();
    }

    // Criar um novo organizador
    public Organizer createOrganizer(Organizer organizer) {
        return organizerRepository.save(organizer);
    }

    // Atualizar um organizador existente
    public Organizer updateOrganizer(Long id, Organizer updatedOrganizer) {
        Organizer existing = organizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with id: ", id));

        // Atualizar campos
        existing.setName(updatedOrganizer.getName());
        existing.setEmail(updatedOrganizer.getEmail());
        existing.setPhoneNumber(updatedOrganizer.getPhoneNumber());
        existing.setOrganizationName(updatedOrganizer.getOrganizationName());
        existing.setBio(updatedOrganizer.getBio());
        existing.setSocialLinks(updatedOrganizer.getSocialLinks());
        // Senha e idade podem ser atualizados conforme sua lógica

        return organizerRepository.save(existing);
    }

    // Deletar organizador
    public void deleteOrganizer(Long id) {
        Organizer existing = organizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with id: ", id));

        organizerRepository.delete(existing);
    }
}
