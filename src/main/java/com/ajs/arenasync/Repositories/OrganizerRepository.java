package com.ajs.arenasync.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajs.arenasync.Entities.Organizer;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, Long> {

    // Buscar por nome do organizador (parcial e ignorando maiúsculas)
    List<Organizer> findByNameContainingIgnoreCase(String name);

    // Buscar por nome da organização
    List<Organizer> findByOrganizationNameContainingIgnoreCase(String organizationName);

    // Buscar por e-mail (usado em login ou validação)
    Organizer findByEmail(String email);

    // Verificar se já existe um telefone cadastrado (evita duplicidade)
    boolean existsByPhoneNumber(String phoneNumber);
}
