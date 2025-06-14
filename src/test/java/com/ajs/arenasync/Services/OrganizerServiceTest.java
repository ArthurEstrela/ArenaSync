package com.ajs.arenasync.Services;

import com.ajs.arenasync.DTO.OrganizerRequestDTO;
import com.ajs.arenasync.DTO.OrganizerResponseDTO;
import com.ajs.arenasync.Entities.Organizer;
import com.ajs.arenasync.Entities.Tournament;
import com.ajs.arenasync.Exceptions.BusinessException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.OrganizerRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrganizerServiceTest {

    @Mock
    private OrganizerRepository organizerRepository;

    @InjectMocks
    private OrganizerService organizerService;

    private Organizer organizer;
    private OrganizerRequestDTO organizerRequestDTO;
    private Long organizerId = 1L;

    @BeforeEach
    void setUp() {
        organizer = new Organizer();
        organizer.setId(organizerId);
        organizer.setName("Test Organizer");
        organizer.setEmail("test@organizer.com");
        organizer.setPhoneNumber("1234567890");
        organizer.setOrganizationName("Test Org");
        organizer.setTournaments(new ArrayList<>()); // Inicializa lista de torneios vazia

        organizerRequestDTO = new OrganizerRequestDTO();
        organizerRequestDTO.setName("DTO Organizer");
        organizerRequestDTO.setEmail("dto@organizer.com");
        organizerRequestDTO.setPhoneNumber("0987654321");
        organizerRequestDTO.setOrganizationName("DTO Org");
        organizerRequestDTO.setBio("Bio");
        organizerRequestDTO.setSocialLinks("links");
    }

    @Test
    void testGetOrganizerById_Success() {
        when(organizerRepository.findById(organizerId)).thenReturn(Optional.of(organizer));

        OrganizerResponseDTO responseDTO = organizerService.getOrganizerById(organizerId);

        assertNotNull(responseDTO);
        assertEquals(organizer.getName(), responseDTO.getName());
        verify(organizerRepository, times(1)).findById(organizerId);
    }

    @Test
    void testGetOrganizerById_NotFound() {
        when(organizerRepository.findById(organizerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            organizerService.getOrganizerById(organizerId);
        });
        verify(organizerRepository, times(1)).findById(organizerId);
    }

    @Test
    void testGetAllOrganizers() {
        when(organizerRepository.findAll()).thenReturn(Collections.singletonList(organizer));

        List<OrganizerResponseDTO> organizers = organizerService.getAllOrganizers();

        assertNotNull(organizers);
        assertFalse(organizers.isEmpty());
        assertEquals(1, organizers.size());
        assertEquals(organizer.getName(), organizers.get(0).getName());
        verify(organizerRepository, times(1)).findAll();
    }

    @Test
    void testCreateOrganizer_Success() {
        when(organizerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(organizerRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(organizerRepository.save(any(Organizer.class))).thenAnswer(invocation -> {
            Organizer saved = invocation.getArgument(0);
            saved.setId(organizerId); // Simula ID
            return saved;
        });

        OrganizerResponseDTO responseDTO = organizerService.saveOrganizer(organizerRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(organizerRequestDTO.getName(), responseDTO.getName());
        verify(organizerRepository, times(1)).findByEmail(organizerRequestDTO.getEmail());
        verify(organizerRepository, times(1)).existsByPhoneNumber(organizerRequestDTO.getPhoneNumber());
        verify(organizerRepository, times(1)).save(any(Organizer.class));
    }

    @Test
    void testCreateOrganizer_EmailAlreadyExists() {
        when(organizerRepository.findByEmail(anyString())).thenReturn(Optional.of(organizer)); // Email já existe

        assertThrows(BusinessException.class, () -> {
            organizerService.saveOrganizer(organizerRequestDTO);
        });
        verify(organizerRepository, times(1)).findByEmail(organizerRequestDTO.getEmail());
        verify(organizerRepository, never()).existsByPhoneNumber(anyString());
        verify(organizerRepository, never()).save(any(Organizer.class));
    }

    @Test
    void testCreateOrganizer_PhoneNumberAlreadyExists() {
        when(organizerRepository.findByEmail(anyString())).thenReturn(Optional.empty()); // Email é único
        when(organizerRepository.existsByPhoneNumber(anyString())).thenReturn(true); // Telefone já existe

        assertThrows(BusinessException.class, () -> {
            organizerService.saveOrganizer(organizerRequestDTO);
        });
        verify(organizerRepository, times(1)).findByEmail(organizerRequestDTO.getEmail());
        verify(organizerRepository, times(1)).existsByPhoneNumber(organizerRequestDTO.getPhoneNumber());
        verify(organizerRepository, never()).save(any(Organizer.class));
    }

    @Test
    void testUpdateOrganizer_Success() {
        OrganizerRequestDTO updateDto = new OrganizerRequestDTO();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated@organizer.com");
        updateDto.setPhoneNumber("111222333");
        updateDto.setOrganizationName("Updated Org");

        Organizer updatedOrganizer = new Organizer(); // Simula o Organizer atualizado
        updatedOrganizer.setId(organizerId);
        updatedOrganizer.setName(updateDto.getName());
        updatedOrganizer.setEmail(updateDto.getEmail());
        // ... outros campos

        when(organizerRepository.findById(organizerId)).thenReturn(Optional.of(organizer));
        when(organizerRepository.findByEmail(updateDto.getEmail())).thenReturn(Optional.empty()); // Novo email não existe
        when(organizerRepository.existsByPhoneNumber(updateDto.getPhoneNumber())).thenReturn(false); // Novo tel não existe
        when(organizerRepository.save(any(Organizer.class))).thenReturn(updatedOrganizer);

        OrganizerResponseDTO responseDTO = organizerService.updateOrganizer(organizerId, updateDto);

        assertNotNull(responseDTO);
        assertEquals(updateDto.getName(), responseDTO.getName());
        assertEquals(updateDto.getEmail(), responseDTO.getEmail());
        verify(organizerRepository, times(1)).findById(organizerId);
        verify(organizerRepository, times(1)).findByEmail(updateDto.getEmail());
        verify(organizerRepository, times(1)).existsByPhoneNumber(updateDto.getPhoneNumber());
        verify(organizerRepository, times(1)).save(any(Organizer.class));
    }

    @Test
    void testUpdateOrganizer_NotFound() {
        when(organizerRepository.findById(organizerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            organizerService.updateOrganizer(organizerId, organizerRequestDTO);
        });
        verify(organizerRepository, times(1)).findById(organizerId);
        verify(organizerRepository, never()).save(any(Organizer.class));
    }

    @Test
    void testUpdateOrganizer_NewEmailAlreadyExists() {
        organizerRequestDTO.setEmail("existingother@organizer.com"); // Email que já pertence a outro
        when(organizerRepository.findById(organizerId)).thenReturn(Optional.of(organizer)); // organizer original: test@organizer.com
        when(organizerRepository.findByEmail(organizerRequestDTO.getEmail())).thenReturn(Optional.of(new Organizer())); // Novo email já existe

        assertThrows(BusinessException.class, () -> {
            organizerService.updateOrganizer(organizerId, organizerRequestDTO);
        });
        verify(organizerRepository, times(1)).findById(organizerId);
        verify(organizerRepository, times(1)).findByEmail(organizerRequestDTO.getEmail());
        verify(organizerRepository, never()).save(any(Organizer.class));
    }
    
    @Test
    void testUpdateOrganizer_EmailUnchanged() {
        // Testa o caso em que o email não é alterado.
        organizerRequestDTO.setEmail(organizer.getEmail()); // Mesmo email do organizador existente

        when(organizerRepository.findById(organizerId)).thenReturn(Optional.of(organizer));
        // Se o email não mudou, `!existing.getEmail().equals(dto.getEmail())` será false.
        // Portanto, `organizerRepository.findByEmail(dto.getEmail())` para checagem de conflito não será chamado.
        when(organizerRepository.existsByPhoneNumber(organizerRequestDTO.getPhoneNumber())).thenReturn(false); // Assumindo que o telefone é válido ou também não mudou
        when(organizerRepository.save(any(Organizer.class))).thenReturn(organizer);


        OrganizerResponseDTO responseDTO = organizerService.updateOrganizer(organizerId, organizerRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(organizer.getEmail(), responseDTO.getEmail());
        verify(organizerRepository, times(1)).findById(organizerId);
        verify(organizerRepository, never()).findByEmail(organizerRequestDTO.getEmail()); // Não deve ser chamado para conflito se o email não mudou
        verify(organizerRepository, times(1)).save(any(Organizer.class));
    }


    @Test
    void testDeleteOrganizer_Success_NoTournaments() {
        when(organizerRepository.findById(organizerId)).thenReturn(Optional.of(organizer)); // organizer.getTournaments() é lista vazia
        doNothing().when(organizerRepository).delete(organizer);

        assertDoesNotThrow(() -> organizerService.deleteOrganizer(organizerId));

        verify(organizerRepository, times(1)).findById(organizerId);
        verify(organizerRepository, times(1)).delete(organizer);
    }

    @Test
    void testDeleteOrganizer_NotFound() {
        when(organizerRepository.findById(organizerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            organizerService.deleteOrganizer(organizerId);
        });
        verify(organizerRepository, times(1)).findById(organizerId);
        verify(organizerRepository, never()).delete(any(Organizer.class));
    }

    @Test
    void testDeleteOrganizer_HasTournaments() {
        Tournament t = new Tournament();
        organizer.getTournaments().add(t); // Adiciona um torneio ao organizador
        when(organizerRepository.findById(organizerId)).thenReturn(Optional.of(organizer));

        assertThrows(BusinessException.class, () -> {
            organizerService.deleteOrganizer(organizerId);
        });
        verify(organizerRepository, times(1)).findById(organizerId);
        verify(organizerRepository, never()).delete(any(Organizer.class));
    }
}