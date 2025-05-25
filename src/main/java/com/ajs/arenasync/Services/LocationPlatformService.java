package com.ajs.arenasync.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.DTO.LocationPlatformRequestDTO;
import com.ajs.arenasync.DTO.LocationPlatformResponseDTO;
import com.ajs.arenasync.Entities.LocationPlatform;
import com.ajs.arenasync.Exceptions.BusinessException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.LocationPlatformRepository;

@Service
public class LocationPlatformService {

    @Autowired
    private LocationPlatformRepository locationPlatformRepository;

    // Criar nova LocationPlatform com DTO
    public LocationPlatformResponseDTO create(LocationPlatformRequestDTO dto) {
        if (locationPlatformRepository.findAll().stream()
                .anyMatch(lp -> lp.getName().equalsIgnoreCase(dto.getName()))) {
            throw new BusinessException("Já existe um local/plataforma com esse nome.");
        }

        LocationPlatform lp = new LocationPlatform();
        lp.setName(dto.getName());
        lp.setType(dto.getType());

        return toResponseDTO(locationPlatformRepository.save(lp));
    }

    // Buscar por ID e retornar como DTO
    public LocationPlatformResponseDTO findById(Long id) {
        LocationPlatform lp = locationPlatformRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Local/Plataforma", id));
        return toResponseDTO(lp);
    }

    // Listar todos como DTOs
    public List<LocationPlatformResponseDTO> findAll() {
        return locationPlatformRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Atualizar um registro com base no DTO
    public LocationPlatformResponseDTO update(Long id, LocationPlatformRequestDTO dto) {
        LocationPlatform existing = locationPlatformRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Local/Plataforma", id));

        if (!existing.getName().equalsIgnoreCase(dto.getName()) &&
            locationPlatformRepository.findAll().stream()
                .anyMatch(lp -> lp.getName().equalsIgnoreCase(dto.getName()))) {
            throw new BusinessException("Já existe um local/plataforma com esse nome.");
        }

        existing.setName(dto.getName());
        existing.setType(dto.getType());

        return toResponseDTO(locationPlatformRepository.save(existing));
    }

    // Deletar
    public void delete(Long id) {
        LocationPlatform existing = locationPlatformRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Local/Plataforma", id));
        locationPlatformRepository.delete(existing);
    }

    // Conversão para DTO
    private LocationPlatformResponseDTO toResponseDTO(LocationPlatform entity) {
        LocationPlatformResponseDTO dto = new LocationPlatformResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        return dto;
    }
}