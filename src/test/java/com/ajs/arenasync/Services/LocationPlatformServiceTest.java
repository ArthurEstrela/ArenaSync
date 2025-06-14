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


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
        // Mock do novo método existsByNameIgnoreCase
        when(locationPlatformRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(locationPlatformRepository.save(any(LocationPlatform.class))).thenAnswer(invocation -> {
            LocationPlatform saved = invocation.getArgument(0);
            saved.setId(locationId);
            return saved;
        });

        LocationPlatformResponseDTO responseDTO = locationPlatformService.save(locationPlatformRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(locationPlatformRequestDTO.getName(), responseDTO.getName());
        assertEquals(locationPlatformRequestDTO.getType(), responseDTO.getType());
        // Verifica se o novo método foi chamado e o findAll não
        verify(locationPlatformRepository, times(1)).existsByNameIgnoreCase(locationPlatformRequestDTO.getName());
        verify(locationPlatformRepository, never()).findAll();
        verify(locationPlatformRepository, times(1)).save(any(LocationPlatform.class));
    }

    @Test
    void testCreate_NameAlreadyExists_IgnoreCase() {
        // Mock do novo método existsByNameIgnoreCase para retornar true
        when(locationPlatformRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);

        assertThrows(BusinessException.class, () -> {
            locationPlatformService.save(locationPlatformRequestDTO);
        });

        // Verifica se o novo método foi chamado e o findAll não
        verify(locationPlatformRepository, times(1)).existsByNameIgnoreCase(locationPlatformRequestDTO.getName());
        verify(locationPlatformRepository, never()).findAll();
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
        // Mock do novo método existsByNameIgnoreCaseAndIdNot
        when(locationPlatformRepository.existsByNameIgnoreCaseAndIdNot(updateDto.getName(), locationId)).thenReturn(false);
        when(locationPlatformRepository.save(any(LocationPlatform.class))).thenReturn(updatedLp);

        LocationPlatformResponseDTO responseDTO = locationPlatformService.update(locationId, updateDto);

        assertNotNull(responseDTO);
        assertEquals(updateDto.getName(), responseDTO.getName());
        assertEquals(updateDto.getType(), responseDTO.getType());
        verify(locationPlatformRepository, times(1)).findById(locationId);
        // Verifica se o novo método foi chamado e o findAll não
        verify(locationPlatformRepository, times(1)).existsByNameIgnoreCaseAndIdNot(updateDto.getName(), locationId);
        verify(locationPlatformRepository, never()).findAll(); // Certifica que findAll não foi chamado
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
        // Quando o nome não muda, a primeira parte da condição `!existing.getName().equalsIgnoreCase(dto.getName())`
        // será falsa, então `existsByNameIgnoreCaseAndIdNot` não deve ser chamado.
        when(locationPlatformRepository.save(any(LocationPlatform.class))).thenReturn(updatedLp);

        LocationPlatformResponseDTO responseDTO = locationPlatformService.update(locationId, updateDto);

        assertNotNull(responseDTO);
        assertEquals(locationPlatform.getName(), responseDTO.getName());
        assertEquals(updateDto.getType(), responseDTO.getType());
        verify(locationPlatformRepository, times(1)).findById(locationId);
        verify(locationPlatformRepository, never()).existsByNameIgnoreCaseAndIdNot(anyString(), anyLong()); // Não deve chamar este método
        verify(locationPlatformRepository, never()).findAll(); // E ainda não deve chamar findAll
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
        verify(locationPlatformRepository, never()).existsByNameIgnoreCaseAndIdNot(anyString(), anyLong()); // Não deve chamar
        verify(locationPlatformRepository, never()).save(any(LocationPlatform.class));
    }

    @Test
    void testUpdate_NewNameAlreadyExists_IgnoreCase() {
        LocationPlatformRequestDTO updateDto = new LocationPlatformRequestDTO();
        updateDto.setName("Existing Other Name");

        // Simula que existe outro LocationPlatform com o nome desejado (mas ID diferente)
        when(locationPlatformRepository.findById(locationId)).thenReturn(Optional.of(locationPlatform));
        when(locationPlatformRepository.existsByNameIgnoreCaseAndIdNot(updateDto.getName(), locationId)).thenReturn(true);

        assertThrows(BusinessException.class, () -> {
            locationPlatformService.update(locationId, updateDto);
        });
        verify(locationPlatformRepository, times(1)).findById(locationId);
        verify(locationPlatformRepository, times(1)).existsByNameIgnoreCaseAndIdNot(updateDto.getName(), locationId);
        verify(locationPlatformRepository, never()).findAll(); // Certifica que findAll não foi chamado
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
