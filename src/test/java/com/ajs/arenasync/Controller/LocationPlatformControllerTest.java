package com.ajs.arenasync.Controller;

import com.ajs.arenasync.DTO.LocationPlatformRequestDTO;
import com.ajs.arenasync.DTO.LocationPlatformResponseDTO;
import com.ajs.arenasync.Entities.Enums.TournamentType;
import com.ajs.arenasync.Services.LocationPlatformService;
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

@WebMvcTest(LocationPlatformController.class)
public class LocationPlatformControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationPlatformService locationPlatformService;

    @Autowired
    private ObjectMapper objectMapper;

    private LocationPlatformRequestDTO requestDTO;
    private LocationPlatformResponseDTO responseDTO;
    private Long locationId = 1L;

    @BeforeEach
    void setUp() {
        requestDTO = new LocationPlatformRequestDTO();
        requestDTO.setName("Local Arena");
        requestDTO.setType(TournamentType.SPORT);

        responseDTO = new LocationPlatformResponseDTO();
        responseDTO.setId(locationId);
        responseDTO.setName("Local Arena");
        responseDTO.setType(TournamentType.SPORT);
    }

    @Test
    void createLocationPlatform_Success() throws Exception {
        when(locationPlatformService.save(any(LocationPlatformRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/location-platforms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated()) // CORREÇÃO AQUI: Espera 201 Created
                .andExpect(jsonPath("$.name", is(responseDTO.getName())));
    }

    @Test
    void createLocationPlatform_InvalidDTO_NameBlank() throws Exception {
        requestDTO.setName("");

        mockMvc.perform(post("/api/location-platforms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
                // Removidas asserções jsonPath que acessariam o DTO 'created'
    }

    @Test
    void createLocationPlatform_ServiceThrowsBusinessException_NameExists() throws Exception {
        when(locationPlatformService.save(any(LocationPlatformRequestDTO.class)))
            .thenThrow(new BusinessException("Já existe um local/plataforma com esse nome."));

        mockMvc.perform(post("/api/location-platforms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findLocationPlatformById_Success() throws Exception {
        when(locationPlatformService.findById(locationId)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/location-platforms/{id}", locationId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$.id", is(locationId.intValue())))
                .andExpect(jsonPath("$.name", is(responseDTO.getName())));
    }

    @Test
    void findLocationPlatformById_NotFound() throws Exception {
        when(locationPlatformService.findById(locationId)).thenThrow(new ResourceNotFoundException("Local/Plataforma", locationId));

        mockMvc.perform(get("/api/location-platforms/{id}", locationId))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllLocationPlatforms_Success() throws Exception {
        when(locationPlatformService.findAll()).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/api/location-platforms"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$._embedded.locationPlatformResponseDTOList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.locationPlatformResponseDTOList[0].name", is(responseDTO.getName())));
    }
    
    @Test
    void findAllLocationPlatforms_Empty() throws Exception {
        when(locationPlatformService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/location-platforms"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$._embedded").doesNotExist())
                .andExpect(jsonPath("$._links").exists());
    }


    @Test
    void updateLocationPlatform_Success() throws Exception {
        when(locationPlatformService.update(anyLong(), any(LocationPlatformRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/location-platforms/{id}", locationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(responseDTO.getName())));
    }
    
    @Test
    void updateLocationPlatform_NotFound() throws Exception {
        when(locationPlatformService.update(anyLong(), any(LocationPlatformRequestDTO.class)))
            .thenThrow(new ResourceNotFoundException("Local/Plataforma", locationId));

        mockMvc.perform(put("/api/location-platforms/{id}", locationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteLocationPlatform_Success() throws Exception {
        doNothing().when(locationPlatformService).delete(locationId);

        mockMvc.perform(delete("/api/location-platforms/{id}", locationId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteLocationPlatform_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Local/Plataforma", locationId)).when(locationPlatformService).delete(locationId);

        mockMvc.perform(delete("/api/location-platforms/{id}", locationId))
                .andExpect(status().isNotFound());
    }
}