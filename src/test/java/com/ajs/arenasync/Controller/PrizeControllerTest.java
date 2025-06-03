package com.ajs.arenasync.Controller;

import com.ajs.arenasync.DTO.PrizeRequestDTO;
import com.ajs.arenasync.DTO.PrizeResponseDTO;
import com.ajs.arenasync.Services.PrizeService;
import com.ajs.arenasync.Exceptions.BadRequestException;
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

@WebMvcTest(PrizeController.class)
public class PrizeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PrizeService prizeService;

    @Autowired
    private ObjectMapper objectMapper;

    private PrizeRequestDTO prizeRequestDTO;
    private PrizeResponseDTO prizeResponseDTO;
    private Long prizeId = 1L;
    private Long tournamentId = 1L;

    @BeforeEach
    void setUp() {
        prizeRequestDTO = new PrizeRequestDTO();
        prizeRequestDTO.setTournamentId(tournamentId);
        prizeRequestDTO.setDescription("Grand Prize");
        prizeRequestDTO.setValue(1000.0);

        prizeResponseDTO = new PrizeResponseDTO();
        prizeResponseDTO.setId(prizeId);
        prizeResponseDTO.setDescription("Grand Prize");
        prizeResponseDTO.setValue(1000.0);
        prizeResponseDTO.setTournamentName("Championship");
    }

    @Test
    void createPrize_Success() throws Exception {
        when(prizeService.save(any(PrizeRequestDTO.class))).thenReturn(prizeResponseDTO);

        mockMvc.perform(post("/api/prizes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prizeRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is(prizeResponseDTO.getDescription())));
    }

    @Test
    void createPrize_InvalidDTO_DescriptionBlank() throws Exception {
        prizeRequestDTO.setDescription(""); // Viola @NotBlank

        mockMvc.perform(post("/api/prizes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prizeRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPrize_ServiceThrowsBadRequest_TournamentNotFound() throws Exception {
        when(prizeService.save(any(PrizeRequestDTO.class)))
            .thenThrow(new BadRequestException("Torneio associado ao prêmio não encontrado."));

        mockMvc.perform(post("/api/prizes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prizeRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPrizeById_Success() throws Exception {
        when(prizeService.findById(prizeId)).thenReturn(prizeResponseDTO);

        mockMvc.perform(get("/api/prizes/{id}", prizeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(prizeId.intValue())))
                .andExpect(jsonPath("$.description", is(prizeResponseDTO.getDescription())));
    }

    @Test
    void getPrizeById_NotFound() throws Exception {
        when(prizeService.findById(prizeId)).thenThrow(new ResourceNotFoundException("Prêmio", prizeId));

        mockMvc.perform(get("/api/prizes/{id}", prizeId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPrizes_Success() throws Exception {
        when(prizeService.findAll()).thenReturn(Collections.singletonList(prizeResponseDTO));

        mockMvc.perform(get("/api/prizes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(prizeResponseDTO.getDescription())));
    }
    
    @Test
    void getAllPrizes_Empty() throws Exception {
        when(prizeService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/prizes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void deletePrize_Success() throws Exception {
        doNothing().when(prizeService).deleteById(prizeId);

        mockMvc.perform(delete("/api/prizes/{id}", prizeId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePrize_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Prêmio", prizeId)).when(prizeService).deleteById(prizeId);

        mockMvc.perform(delete("/api/prizes/{id}", prizeId))
                .andExpect(status().isNotFound());
    }
}