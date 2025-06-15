package com.ajs.arenasync.Controller;

import com.ajs.arenasync.DTO.MatchRequestDTO;
import com.ajs.arenasync.DTO.MatchResponseDTO;
import com.ajs.arenasync.Services.MatchService;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.HttpStatus; // Importe HttpStatus

@WebMvcTest(MatchController.class)
public class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MatchService matchService;

    @Autowired
    private ObjectMapper objectMapper;

    private MatchRequestDTO matchRequestDTO;
    private MatchResponseDTO matchResponseDTO;
    private Long matchId = 1L;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        matchRequestDTO = new MatchRequestDTO();
        matchRequestDTO.setTeamAId(1L);
        matchRequestDTO.setTeamBId(2L);
        matchRequestDTO.setTournamentId(1L);
        matchRequestDTO.setLocationPlatformId(1L);
        matchRequestDTO.setScheduledDateTime(LocalDateTime.now().plusHours(24));
        matchRequestDTO.setScoreTeamA(null);
        matchRequestDTO.setScoreTeamB(null);

        matchResponseDTO = new MatchResponseDTO();
        matchResponseDTO.setId(matchId);
        matchResponseDTO.setTeamAName("Team Alpha");
        matchResponseDTO.setTeamBName("Team Beta");
        matchResponseDTO.setTournamentName("Main Event");
        matchResponseDTO.setLocationPlatformName("Online Arena");
        matchResponseDTO.setScheduledDateTime(matchRequestDTO.getScheduledDateTime());
        matchResponseDTO.setScoreTeamA(null);
        matchResponseDTO.setScoreTeamB(null);
    }

    @Test
    void createMatch_Success() throws Exception {
        when(matchService.saveFromDTO(any(MatchRequestDTO.class))).thenReturn(matchResponseDTO);

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchRequestDTO)))
                // CORREÇÃO: Mudar para isCreated (201)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teamAName", is(matchResponseDTO.getTeamAName())));
    }

    @Test
    void createMatch_InvalidDTO_ScheduledDateTimeInPast() throws Exception {
        matchRequestDTO.setScheduledDateTime(LocalDateTime.now().minusHours(1));

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMatch_ServiceThrowsBadRequest_TeamNotFound() throws Exception {
        when(matchService.saveFromDTO(any(MatchRequestDTO.class)))
            .thenThrow(new BadRequestException("Equipe A não encontrada."));

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findMatchById_Success() throws Exception {
        when(matchService.findById(matchId)).thenReturn(matchResponseDTO);

        mockMvc.perform(get("/api/matches/{id}", matchId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$.id", is(matchId.intValue())))
                .andExpect(jsonPath("$.teamAName", is(matchResponseDTO.getTeamAName())));
    }

    @Test
    void findMatchById_NotFound() throws Exception {
        when(matchService.findById(matchId)).thenThrow(new ResourceNotFoundException("Partida", matchId));

        mockMvc.perform(get("/api/matches/{id}", matchId))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllMatches_Success() throws Exception {
        when(matchService.findAll()).thenReturn(Collections.singletonList(matchResponseDTO));

        mockMvc.perform(get("/api/matches"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$._embedded.matchResponseDTOList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.matchResponseDTOList[0].teamAName", is(matchResponseDTO.getTeamAName())));
    }
    
    @Test
    void findAllMatches_Empty() throws Exception {
        when(matchService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/matches"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$._embedded").doesNotExist())
                .andExpect(jsonPath("$._links").exists());
    }

    @Test
    void deleteMatchById_Success() throws Exception {
        doNothing().when(matchService).deleteById(matchId);

        mockMvc.perform(delete("/api/matches/{id}", matchId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteMatchById_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Partida", matchId)).when(matchService).deleteById(matchId);

        mockMvc.perform(delete("/api/matches/{id}", matchId))
                .andExpect(status().isNotFound());
    }
}
