package com.ajs.arenasync.Services;

import com.ajs.arenasync.DTO.LocationPlatformRequestDTO;
import com.ajs.arenasync.DTO.LocationPlatformResponseDTO;
import com.ajs.arenasync.Entities.Enums.TournamentType;
import com.ajs.arenasync.Entities.LocationPlatform;
import com.ajs.arenasync.Exceptions.BusinessException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.LocationPlatformRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationPlatformServiceTest {

    @Mock
    private LocationPlatformRepository locationPlatformRepository;

    @InjectMocks
    private LocationPlatformService locationPlatformService;

    private LocationPlatform locationPlatform;
    private LocationPlatformRequestDTO locationPlatformRequestDTO;
    private Long locationId = 1L;

    @BeforeEach
    void setUp() {
        locationPlatform = new LocationPlatform();
        locationPlatform.setId(locationId);
        locationPlatform.setName("Online Venue");
        locationPlatform.setType(TournamentType.ESPORT);

        locationPlatformRequestDTO = new LocationPlatformRequestDTO();
        locationPlatformRequestDTO.setName("Physical Venue DTO");
        locationPlatformRequestDTO.setType(TournamentType.SPORT);
    }

    @Test
    void testCreate_Success() {
        when(locationPlatformRepository.findAll()).thenReturn(new ArrayList<>()); // Nenhum com mesmo nome
        when(locationPlatformRepository.save(any(LocationPlatform.class))).thenAnswer(invocation -> {
            LocationPlatform saved = invocation.getArgument(0);
            saved.setId(locationId);
            return saved;
        });

        LocationPlatformResponseDTO responseDTO = locationPlatformService.create(locationPlatformRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(locationPlatformRequestDTO.getName(), responseDTO.getName());
        assertEquals(locationPlatformRequestDTO.getType(), responseDTO.getType());
        verify(locationPlatformRepository, times(1)).findAll();
        verify(locationPlatformRepository, times(1)).save(any(LocationPlatform.class));
    }

    @Test
    void testCreate_NameAlreadyExists_IgnoreCase() {
        LocationPlatform existingLP = new LocationPlatform();
        existingLP.setId(2L);
        existingLP.setName(locationPlatformRequestDTO.getName().toLowerCase()); // Mesmo nome, caso diferente
        existingLP.setType(TournamentType.ESPORT);

        when(locationPlatformRepository.findAll()).thenReturn(Collections.singletonList(existingLP));

        assertThrows(BusinessException.class, () -> {
            locationPlatformService.create(locationPlatformRequestDTO);
        });

        verify(locationPlatformRepository, times(1)).findAll();
        verify(locationPlatformRepository, never()).save(any(LocationPlatform.class));
    }

    @Test
    void testFindById_Success() {
        when(locationPlatformRepository.findById(locationId)).thenReturn(Optional.of(locationPlatform));

        LocationPlatformResponseDTO responseDTO = locationPlatformService.findById(locationId);

        assertNotNull(responseDTO);
        assertEquals(locationPlatform.getName(), responseDTO.getName());
        verify(locationPlatformRepository, times(1)).findById(locationId);
    }

    @Test
    void testFindById_NotFound() {
        when(locationPlatformRepository.findById(locationId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            locationPlatformService.findById(locationId);
        });
        verify(locationPlatformRepository, times(1)).findById(locationId);
    }

    @Test
    void testFindAll() {
        when(locationPlatformRepository.findAll()).thenReturn(Collections.singletonList(locationPlatform));

        List<LocationPlatformResponseDTO> responseList = locationPlatformService.findAll();

        assertNotNull(responseList);
        assertFalse(responseList.isEmpty());
        assertEquals(1, responseList.size());
        assertEquals(locationPlatform.getName(), responseList.get(0).getName());
        verify(locationPlatformRepository, times(1)).findAll();
    }

    @Test
    void testUpdate_Success() {
        LocationPlatformRequestDTO updateDto = new LocationPlatformRequestDTO();
        updateDto.setName("Updated Venue Name");
        updateDto.setType(TournamentType.SPORT);

        LocationPlatform updatedLp = new LocationPlatform();
        updatedLp.setId(locationId);
        updatedLp.setName(updateDto.getName());
        updatedLp.setType(updateDto.getType());

        when(locationPlatformRepository.findById(locationId)).thenReturn(Optional.of(locationPlatform));
        when(locationPlatformRepository.findAll()).thenReturn(Collections.singletonList(locationPlatform)); // Para checagem de nome existente
        when(locationPlatformRepository.save(any(LocationPlatform.class))).thenReturn(updatedLp);

        LocationPlatformResponseDTO responseDTO = locationPlatformService.update(locationId, updateDto);

        assertNotNull(responseDTO);
        assertEquals(updateDto.getName(), responseDTO.getName());
        assertEquals(updateDto.getType(), responseDTO.getType());
        verify(locationPlatformRepository, times(1)).findById(locationId);
        verify(locationPlatformRepository, times(1)).findAll();
        verify(locationPlatformRepository, times(1)).save(any(LocationPlatform.class));
    }
    
    @Test
    void testUpdate_NameUnchanged() {
        LocationPlatformRequestDTO updateDto = new LocationPlatformRequestDTO();
        updateDto.setName(locationPlatform.getName()); // Mesmo nome
        updateDto.setType(TournamentType.SPORT); // Tipo diferente

        LocationPlatform updatedLp = new LocationPlatform();
        updatedLp.setId(locationId);
        updatedLp.setName(locationPlatform.getName());
        updatedLp.setType(updateDto.getType());

        when(locationPlatformRepository.findById(locationId)).thenReturn(Optional.of(locationPlatform));
        //  A lógica `!existing.getName().equalsIgnoreCase(dto.getName())` será false.
        //  Então, a checagem de `findAll().stream().anyMatch` para o nome não causará problema.
        when(locationPlatformRepository.save(any(LocationPlatform.class))).thenReturn(updatedLp);

        LocationPlatformResponseDTO responseDTO = locationPlatformService.update(locationId, updateDto);

        assertNotNull(responseDTO);
        assertEquals(locationPlatform.getName(), responseDTO.getName());
        assertEquals(updateDto.getType(), responseDTO.getType());
        verify(locationPlatformRepository, times(1)).findById(locationId);
        // `findAll` não será chamado para a lógica de conflito de nome se o nome não mudou e a primeira condição do if falhar.
        verify(locationPlatformRepository, never()).findAll(); // Ajustado
        verify(locationPlatformRepository, times(1)).save(any(LocationPlatform.class));
    }


    @Test
    void testUpdate_NotFound() {
        LocationPlatformRequestDTO updateDto = new LocationPlatformRequestDTO();
        updateDto.setName("Updated Venue Name");
        when(locationPlatformRepository.findById(locationId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            locationPlatformService.update(locationId, updateDto);
        });
        verify(locationPlatformRepository, times(1)).findById(locationId);
        verify(locationPlatformRepository, never()).save(any(LocationPlatform.class));
    }

    @Test
    void testUpdate_NewNameAlreadyExists_IgnoreCase() {
        LocationPlatformRequestDTO updateDto = new LocationPlatformRequestDTO();
        updateDto.setName("Existing Other Name");

        LocationPlatform existingOtherLp = new LocationPlatform();
        existingOtherLp.setId(2L); // ID diferente
        existingOtherLp.setName(updateDto.getName().toUpperCase()); // Mesmo nome, caso diferente

        // locationPlatform é o que estamos tentando atualizar
        // existingOtherLp é um outro registro que já tem o nome desejado
        List<LocationPlatform> allPlatforms = new ArrayList<>();
        allPlatforms.add(locationPlatform);
        allPlatforms.add(existingOtherLp);


        when(locationPlatformRepository.findById(locationId)).thenReturn(Optional.of(locationPlatform));
        when(locationPlatformRepository.findAll()).thenReturn(allPlatforms);

        assertThrows(BusinessException.class, () -> {
            locationPlatformService.update(locationId, updateDto);
        });
        verify(locationPlatformRepository, times(1)).findById(locationId);
        verify(locationPlatformRepository, times(1)).findAll();
        verify(locationPlatformRepository, never()).save(any(LocationPlatform.class));
    }

    @Test
    void testDelete_Success() {
        when(locationPlatformRepository.findById(locationId)).thenReturn(Optional.of(locationPlatform));
        doNothing().when(locationPlatformRepository).delete(locationPlatform);

        assertDoesNotThrow(() -> locationPlatformService.delete(locationId));

        verify(locationPlatformRepository, times(1)).findById(locationId);
        verify(locationPlatformRepository, times(1)).delete(locationPlatform);
    }

    @Test
    void testDelete_NotFound() {
        when(locationPlatformRepository.findById(locationId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            locationPlatformService.delete(locationId);
        });
        verify(locationPlatformRepository, times(1)).findById(locationId);
        verify(locationPlatformRepository, never()).delete(any(LocationPlatform.class));
    }
}