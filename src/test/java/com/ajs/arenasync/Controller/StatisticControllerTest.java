package com.ajs.arenasync.Controller;

import com.ajs.arenasync.DTO.StatisticRequestDTO;
import com.ajs.arenasync.DTO.StatisticResponseDTO;
import com.ajs.arenasync.Services.StatisticService;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(StatisticController.class)
public class StatisticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticService statisticService;

    @Autowired
    private ObjectMapper objectMapper;

    private StatisticRequestDTO statisticRequestDTO;
    private StatisticResponseDTO statisticResponseDTO;
    private Long statisticId = 1L;
    private Long playerId = 1L;

    @BeforeEach
    void setUp() {
        statisticRequestDTO = new StatisticRequestDTO();
        statisticRequestDTO.setPlayerId(playerId);
        statisticRequestDTO.setGamesPlayed(10);
        statisticRequestDTO.setWins(5);
        statisticRequestDTO.setScore(100);
        statisticRequestDTO.setAssists(10); // Adicionado para teste

        statisticResponseDTO = new StatisticResponseDTO();
        statisticResponseDTO.setId(statisticId);
        statisticResponseDTO.setPlayerName("Player Test");
        statisticResponseDTO.setGamesPlayed(10);
        statisticResponseDTO.setWins(5);
        statisticResponseDTO.setScore(100);
        statisticResponseDTO.setAssists(10); // Adicionado para teste
    }

    @Test
    void createStatistic_Success() throws Exception {
        when(statisticService.save(any(StatisticRequestDTO.class))).thenReturn(statisticResponseDTO);

        mockMvc.perform(post("/api/statistics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statisticRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.playerName", is(statisticResponseDTO.getPlayerName())))
                .andExpect(jsonPath("$.assists", is(statisticResponseDTO.getAssists()))); // Verifica assists
    }

    @Test
    void createStatistic_InvalidDTO_PlayerIdNull() throws Exception {
        statisticRequestDTO.setPlayerId(null);

        mockMvc.perform(post("/api/statistics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statisticRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createStatistic_ServiceThrowsBadRequest_PlayerNotFound() throws Exception {
        when(statisticService.save(any(StatisticRequestDTO.class)))
            .thenThrow(new BadRequestException("Jogador não encontrado."));

        mockMvc.perform(post("/api/statistics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statisticRequestDTO)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getStatisticById_Success() throws Exception {
        when(statisticService.findById(statisticId)).thenReturn(statisticResponseDTO);

        mockMvc.perform(get("/api/statistics/{id}", statisticId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$.id", is(statisticId.intValue())))
                .andExpect(jsonPath("$.playerName", is(statisticResponseDTO.getPlayerName())))
                .andExpect(jsonPath("$.assists", is(statisticResponseDTO.getAssists()))); // Verifica assists
    }

    @Test
    void getStatisticById_NotFound() throws Exception {
        when(statisticService.findById(statisticId)).thenThrow(new ResourceNotFoundException("Estatística", statisticId));

        mockMvc.perform(get("/api/statistics/{id}", statisticId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatistic_Success() throws Exception {
        when(statisticService.update(anyLong(), any(StatisticRequestDTO.class))).thenReturn(statisticResponseDTO);

        mockMvc.perform(put("/api/statistics/{id}", statisticId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statisticRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerName", is(statisticResponseDTO.getPlayerName())))
                .andExpect(jsonPath("$.assists", is(statisticResponseDTO.getAssists()))); // Verifica assists
    }
    
    @Test
    void updateStatistic_NotFound() throws Exception {
        when(statisticService.update(anyLong(), any(StatisticRequestDTO.class)))
            .thenThrow(new ResourceNotFoundException("Estatística", statisticId));

        mockMvc.perform(put("/api/statistics/{id}", statisticId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statisticRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStatistic_Success() throws Exception {
        doNothing().when(statisticService).deleteById(statisticId);

        mockMvc.perform(delete("/api/statistics/{id}", statisticId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteStatistic_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Estatística", statisticId)).when(statisticService).deleteById(statisticId);
        
        mockMvc.perform(delete("/api/statistics/{id}", statisticId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllStatistics_Success() throws Exception {
        when(statisticService.findAll()).thenReturn(Collections.singletonList(statisticResponseDTO));

        mockMvc.perform(get("/api/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$._embedded.statisticResponseDTOList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.statisticResponseDTOList[0].playerName", is(statisticResponseDTO.getPlayerName())))
                .andExpect(jsonPath("$._embedded.statisticResponseDTOList[0].assists", is(statisticResponseDTO.getAssists()))); // Verifica assists
    }

    @Test
    void getAllStatistics_Empty() throws Exception {
        when(statisticService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$._embedded").doesNotExist())
                .andExpect(jsonPath("$._links").exists());
    }
}
