package com.ajs.arenasync.Controller;

import com.ajs.arenasync.DTO.ResultRequestDTO;
import com.ajs.arenasync.DTO.ResultResponseDTO;
import com.ajs.arenasync.Services.ResultService;
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

@WebMvcTest(ResultController.class)
public class ResultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResultService resultService;

    @Autowired
    private ObjectMapper objectMapper;

    private ResultRequestDTO resultRequestDTO;
    private ResultResponseDTO resultResponseDTO;
    private Long resultId = 1L;
    private Long matchId = 1L;

    @BeforeEach
    void setUp() {
        resultRequestDTO = new ResultRequestDTO();
        resultRequestDTO.setMatchId(matchId);
        resultRequestDTO.setScoreTeamA(3);
        resultRequestDTO.setScoreTeamB(2);

        resultResponseDTO = new ResultResponseDTO();
        resultResponseDTO.setId(resultId);
        resultResponseDTO.setMatchId(matchId);
        resultResponseDTO.setScoreTeamA(3);
        resultResponseDTO.setScoreTeamB(2);
    }

    @Test
    void createResult_Success() throws Exception {
        when(resultService.save(any(ResultRequestDTO.class))).thenReturn(resultResponseDTO);

        mockMvc.perform(post("/api/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resultRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.scoreTeamA", is(resultResponseDTO.getScoreTeamA())));
    }

    @Test
    void createResult_InvalidDTO_MatchIdNull() throws Exception {
        resultRequestDTO.setMatchId(null); // Viola @NotNull

        mockMvc.perform(post("/api/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resultRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createResult_ServiceThrowsResourceNotFound_Match() throws Exception {
        when(resultService.save(any(ResultRequestDTO.class)))
            .thenThrow(new ResourceNotFoundException("Partida", matchId));

        mockMvc.perform(post("/api/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resultRequestDTO)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void createResult_ServiceThrowsBadRequest_ResultExists() throws Exception {
        when(resultService.save(any(ResultRequestDTO.class)))
            .thenThrow(new BadRequestException("Já existe um resultado registrado para esta partida."));

        mockMvc.perform(post("/api/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resultRequestDTO)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getResultById_Success() throws Exception {
        when(resultService.findById(resultId)).thenReturn(resultResponseDTO);

        mockMvc.perform(get("/api/results/{id}", resultId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(resultId.intValue())))
                .andExpect(jsonPath("$.scoreTeamA", is(resultResponseDTO.getScoreTeamA())));
    }

    @Test
    void getResultById_NotFound() throws Exception {
        when(resultService.findById(resultId)).thenThrow(new ResourceNotFoundException("Resultado", resultId));

        mockMvc.perform(get("/api/results/{id}", resultId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllResults_Success() throws Exception {
        when(resultService.findAll()).thenReturn(Collections.singletonList(resultResponseDTO));

        mockMvc.perform(get("/api/results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].scoreTeamA", is(resultResponseDTO.getScoreTeamA())));
    }

    @Test
    void getAllResults_Empty() throws Exception {
        when(resultService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void deleteResult_Success() throws Exception {
        doNothing().when(resultService).deleteById(resultId);

        mockMvc.perform(delete("/api/results/{id}", resultId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteResult_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Resultado", resultId)).when(resultService).deleteById(resultId);

        mockMvc.perform(delete("/api/results/{id}", resultId))
                .andExpect(status().isNotFound());
    }
}