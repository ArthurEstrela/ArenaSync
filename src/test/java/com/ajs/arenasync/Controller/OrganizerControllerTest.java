package com.ajs.arenasync.Controller;

import com.ajs.arenasync.DTO.OrganizerRequestDTO;
import com.ajs.arenasync.DTO.OrganizerResponseDTO;
import com.ajs.arenasync.Services.OrganizerService;
import com.ajs.arenasync.Exceptions.BusinessException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrganizerController.class)
public class OrganizerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizerService organizerService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrganizerRequestDTO organizerRequestDTO;
    private OrganizerResponseDTO organizerResponseDTO;
    private Long organizerId = 1L;

    @BeforeEach
    void setUp() {
        organizerRequestDTO = new OrganizerRequestDTO();
        organizerRequestDTO.setName("Arena Corp");
        organizerRequestDTO.setEmail("contact@arenacorp.com");
        organizerRequestDTO.setPhoneNumber("11999998888");
        organizerRequestDTO.setOrganizationName("Arena Corp Inc.");
        organizerRequestDTO.setBio("We organize events.");
        organizerRequestDTO.setSocialLinks("linkedin.com/arenacorp");

        organizerResponseDTO = new OrganizerResponseDTO();
        organizerResponseDTO.setId(organizerId);
        organizerResponseDTO.setName("Arena Corp");
        organizerResponseDTO.setEmail("contact@arenacorp.com");
        organizerResponseDTO.setPhoneNumber("11999998888");
        organizerResponseDTO.setOrganizationName("Arena Corp Inc.");
        organizerResponseDTO.setBio("We organize events.");
        organizerResponseDTO.setSocialLinks("linkedin.com/arenacorp");
    }

    @Test
    void createOrganizer_Success() throws Exception {
        when(organizerService.createOrganizer(any(OrganizerRequestDTO.class))).thenReturn(organizerResponseDTO);

        mockMvc.perform(post("/api/organizers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(organizerRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(organizerResponseDTO.getName())));
    }

    @Test
    void createOrganizer_InvalidDTO_EmailInvalid() throws Exception {
        organizerRequestDTO.setEmail("invalid-email");

        mockMvc.perform(post("/api/organizers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(organizerRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrganizer_ServiceThrowsBusinessException_EmailExists() throws Exception {
        when(organizerService.createOrganizer(any(OrganizerRequestDTO.class)))
            .thenThrow(new BusinessException("Já existe um organizador com este e-mail."));

        mockMvc.perform(post("/api/organizers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(organizerRequestDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getOrganizerById_Success() throws Exception {
        when(organizerService.getOrganizerById(organizerId)).thenReturn(organizerResponseDTO);

        mockMvc.perform(get("/api/organizers/{id}", organizerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$.id", is(organizerId.intValue())))
                .andExpect(jsonPath("$.name", is(organizerResponseDTO.getName())));
    }

    @Test
    void getOrganizerById_NotFound() throws Exception {
        when(organizerService.getOrganizerById(organizerId)).thenThrow(new ResourceNotFoundException("Organizer", organizerId));

        mockMvc.perform(get("/api/organizers/{id}", organizerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllOrganizers_Success() throws Exception {
        when(organizerService.getAllOrganizers()).thenReturn(Collections.singletonList(organizerResponseDTO));

        mockMvc.perform(get("/api/organizers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$._embedded.organizerResponseDTOList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.organizerResponseDTOList[0].name", is(organizerResponseDTO.getName())));
    }
    
    @Test
    void getAllOrganizers_Empty() throws Exception {
        when(organizerService.getAllOrganizers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/organizers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$._embedded").doesNotExist())
                .andExpect(jsonPath("$._links").exists());
    }


    @Test
    void updateOrganizer_Success() throws Exception {
        when(organizerService.updateOrganizer(anyLong(), any(OrganizerRequestDTO.class))).thenReturn(organizerResponseDTO);

        mockMvc.perform(put("/api/organizers/{id}", organizerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(organizerRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(organizerResponseDTO.getName())));
    }
    
    @Test
    void updateOrganizer_NotFound() throws Exception {
         when(organizerService.updateOrganizer(anyLong(), any(OrganizerRequestDTO.class)))
            .thenThrow(new ResourceNotFoundException("Organizer", organizerId));

        mockMvc.perform(put("/api/organizers/{id}", organizerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(organizerRequestDTO)))
                .andExpect(status().isNotFound());
    }


    @Test
    void deleteOrganizer_Success() throws Exception {
        doNothing().when(organizerService).deleteOrganizer(organizerId);

        mockMvc.perform(delete("/api/organizers/{id}", organizerId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteOrganizer_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Organizer", organizerId)).when(organizerService).deleteOrganizer(organizerId);
        
        mockMvc.perform(delete("/api/organizers/{id}", organizerId))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void deleteOrganizer_BusinessException_HasTournaments() throws Exception {
        doThrow(new BusinessException("Não é possível excluir um organizador que possui torneios associados."))
            .when(organizerService).deleteOrganizer(organizerId);
        
        mockMvc.perform(delete("/api/organizers/{id}", organizerId))
                .andExpect(status().isInternalServerError());
    }
}