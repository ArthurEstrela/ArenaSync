package com.ajs.arenasync.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.DTO.LocationPlatformRequestDTO;
import com.ajs.arenasync.DTO.LocationPlatformResponseDTO;
import com.ajs.arenasync.Entities.LocationPlatform;
import com.ajs.arenasync.Exceptions.BusinessException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.LocationPlatformRepository;

@Service
@CacheConfig(cacheNames = "locationPlatforms")
public class LocationPlatformService {

    @Autowired
    private LocationPlatformRepository locationPlatformRepository;

    @CacheEvict(allEntries = true)
    public LocationPlatformResponseDTO save(LocationPlatformRequestDTO dto) {
        // CORREÇÃO: Usar existsByNameIgnoreCase diretamente
        if (locationPlatformRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new BusinessException("Já existe um local/plataforma com esse nome.");
        }

        LocationPlatform lp = new LocationPlatform();
        lp.setName(dto.getName());
        lp.setType(dto.getType());

        return toResponseDTO(locationPlatformRepository.save(lp));
    }

    @Cacheable(key = "#id")
    public LocationPlatformResponseDTO findById(Long id) {
        LocationPlatform lp = locationPlatformRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Local/Plataforma", id));
        return toResponseDTO(lp);
    }

    @Cacheable
    public List<LocationPlatformResponseDTO> findAll() {
        return locationPlatformRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Caching(evict = {
        @CacheEvict(key = "#id"),
        @CacheEvict(allEntries = true)
    })
    public LocationPlatformResponseDTO update(Long id, LocationPlatformRequestDTO dto) {
        LocationPlatform existing = locationPlatformRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Local/Plataforma", id));

        // CORREÇÃO: Usar existsByNameIgnoreCaseAndIdNot diretamente
        if (!existing.getName().equalsIgnoreCase(dto.getName()) &&
            locationPlatformRepository.existsByNameIgnoreCaseAndIdNot(dto.getName(), id)) {
            throw new BusinessException("Já existe um local/plataforma com esse nome.");
        }

        existing.setName(dto.getName());
        existing.setType(dto.getType());

        return toResponseDTO(locationPlatformRepository.save(existing));
    }

    @Caching(evict = {
        @CacheEvict(key = "#id"),
        @CacheEvict(allEntries = true)
    })
    public void delete(Long id) {
        LocationPlatform existing = locationPlatformRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Local/Plataforma", id));
        locationPlatformRepository.delete(existing);
    }

    private LocationPlatformResponseDTO toResponseDTO(LocationPlatform entity) {
        LocationPlatformResponseDTO dto = new LocationPlatformResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        return dto;
    }
}
