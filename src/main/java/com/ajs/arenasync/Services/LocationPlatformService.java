package com.ajs.arenasync.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.Entities.LocationPlatform;
import com.ajs.arenasync.Exceptions.BusinessException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.LocationPlatformRepository;

@Service
public class LocationPlatformService {

    @Autowired
    private LocationPlatformRepository locationPlatformRepository;

    // Criar nova LocationPlatform com validação de nome único
    public LocationPlatform create(LocationPlatform locationPlatform) {
        // Validação simples por nome (se quiser implementar isso no repositório)
        if (locationPlatformRepository.findAll().stream()
                .anyMatch(lp -> lp.getName().equalsIgnoreCase(locationPlatform.getName()))) {
            throw new BusinessException("Já existe um local/plataforma com esse nome.");
        }

        return locationPlatformRepository.save(locationPlatform);
    }

    // Buscar por ID
    public LocationPlatform findById(Long id) {
        return locationPlatformRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location/Platform", id));
    }

    // Listar todos
    public List<LocationPlatform> findAll() {
        return locationPlatformRepository.findAll();
    }

    // Atualizar local/plataforma
    public LocationPlatform update(Long id, LocationPlatform updatedData) {
        LocationPlatform existing = findById(id);

        // Verifica se está tentando alterar o nome para um que já existe em outro local
        if (!existing.getName().equalsIgnoreCase(updatedData.getName()) &&
            locationPlatformRepository.findAll().stream()
                .anyMatch(lp -> lp.getName().equalsIgnoreCase(updatedData.getName()))) {
            throw new BusinessException("Já existe um local/plataforma com esse nome.");
        }

        existing.setName(updatedData.getName());
        existing.setType(updatedData.getType());

        return locationPlatformRepository.save(existing);
    }

    // Deletar por ID
    public void delete(Long id) {
        LocationPlatform existing = findById(id);
        locationPlatformRepository.delete(existing);
    }
}
