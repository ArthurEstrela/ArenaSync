package com.ajs.arenasync.Controller;

import com.ajs.arenasync.DTO.TournamentRequestDTO;
import com.ajs.arenasync.DTO.TournamentResponseDTO;
import com.ajs.arenasync.Entities.Enums.TournamentStatus;
import com.ajs.arenasync.Entities.Enums.TournamentType;
import com.ajs.arenasync.Services.TournamentService;
import com.ajs.arenasync.Exceptions.BusinessException;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TournamentController.class)
public class TournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TournamentService tournamentService;

    @Autowired
    private ObjectMapper objectMapper;

    private TournamentRequestDTO tournamentRequestDTO;
    private TournamentResponseDTO tournamentResponseDTO;
    private Long organizerId = 1L;
    private Long tournamentId = 1L;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        tournamentRequestDTO = new TournamentRequestDTO();
        tournamentRequestDTO.setName("Spring Championship");
        tournamentRequestDTO.setModality("Valorant");
        tournamentRequestDTO.setRules("Standard pro rules");
        tournamentRequestDTO.setStartDate(LocalDate.now().plusDays(10));
        tournamentRequestDTO.setEndDate(LocalDate.now().plusDays(12));
        tournamentRequestDTO.setType(TournamentType.ESPORT);

        tournamentResponseDTO = new TournamentResponseDTO();
        tournamentResponseDTO.setId(tournamentId);
        tournamentResponseDTO.setName("Spring Championship");
        tournamentResponseDTO.setModality("Valorant");
        tournamentResponseDTO.setRules("Standard pro rules");
        tournamentResponseDTO.setStartDate(LocalDate.now().plusDays(10));
        tournamentResponseDTO.setEndDate(LocalDate.now().plusDays(12));
        tournamentResponseDTO.setType(TournamentType.ESPORT);
        tournamentResponseDTO.setStatus(TournamentStatus.PENDING);
        tournamentResponseDTO.setOrganizerName("Org Test");
    }

    @Test
    void createTournament_Success() throws Exception {
        when(tournamentService.createTournament(anyLong(), any(TournamentRequestDTO.class))).thenReturn(tournamentResponseDTO);

        mockMvc.perform(post("/api/tournaments/organizer/{organizerId}", organizerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tournamentRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(tournamentResponseDTO.getName())));
    }

    @Test
    void createTournament_InvalidDTO_MissingName() throws Exception {
        tournamentRequestDTO.setName(null); // Viola @NotBlank

        mockMvc.perform(post("/api/tournaments/organizer/{organizerId}", organizerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tournamentRequestDTO)))
                .andExpect(status().isBadRequest()); // @Valid falhará
    }
    
    @Test
    void createTournament_ServiceThrowsResourceNotFound_Organizer() throws Exception {
        when(tournamentService.createTournament(anyLong(), any(TournamentRequestDTO.class)))
            .thenThrow(new ResourceNotFoundException("Organizer", organizerId));

        mockMvc.perform(post("/api/tournaments/organizer/{organizerId}", organizerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tournamentRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTournament_ServiceThrowsBusinessException_DateValidation() throws Exception {
        when(tournamentService.createTournament(anyLong(), any(TournamentRequestDTO.class)))
            .thenThrow(new BusinessException("A data de término deve ser posterior ou igual à data de início."));

        mockMvc.perform(post("/api/tournaments/organizer/{organizerId}", organizerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tournamentRequestDTO)))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void getTournamentById_Success() throws Exception {
        when(tournamentService.findById(tournamentId)).thenReturn(tournamentResponseDTO);

        mockMvc.perform(get("/api/tournaments/{id}", tournamentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$.id", is(tournamentId.intValue())))
                .andExpect(jsonPath("$.name", is(tournamentResponseDTO.getName())));
    }

    @Test
    void getTournamentById_NotFound() throws Exception {
        when(tournamentService.findById(tournamentId)).thenThrow(new ResourceNotFoundException("Tournament", tournamentId));

        mockMvc.perform(get("/api/tournaments/{id}", tournamentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTournaments_Success_Paged() throws Exception {
        List<TournamentResponseDTO> content = Collections.singletonList(tournamentResponseDTO);
        Pageable pageable = PageRequest.of(0, 10);
        Page<TournamentResponseDTO> page = new PageImpl<>(content, pageable, 1);

        when(tournamentService.getAllTournaments(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/tournaments?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // CORREÇÃO AQUI: Espera JSON simples para Page
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(tournamentResponseDTO.getName())))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
                .andExpect(jsonPath("$.pageable.pageSize", is(10)))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }
    
    @Test
    void getAllTournaments_Empty_Paged() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TournamentResponseDTO> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(tournamentService.getAllTournaments(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/tournaments?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // CORREÇÃO AQUI: Espera JSON simples para Page
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    void updateTournament_Success() throws Exception {
        when(tournamentService.updateTournament(anyLong(), any(TournamentRequestDTO.class))).thenReturn(tournamentResponseDTO);

        mockMvc.perform(put("/api/tournaments/{id}", tournamentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tournamentRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(tournamentResponseDTO.getName())));
    }

    @Test
    void updateTournament_NotFound() throws Exception {
        when(tournamentService.updateTournament(anyLong(), any(TournamentRequestDTO.class)))
            .thenThrow(new ResourceNotFoundException("Tournament", tournamentId));

        mockMvc.perform(put("/api/tournaments/{id}", tournamentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tournamentRequestDTO)))
                .andExpect(status().isNotFound());
    }


    @Test
    void deleteTournament_Success() throws Exception {
        doNothing().when(tournamentService).deleteById(tournamentId);

        mockMvc.perform(delete("/api/tournaments/{id}", tournamentId))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void deleteTournament_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Tournament", tournamentId)).when(tournamentService).deleteById(tournamentId);

        mockMvc.perform(delete("/api/tournaments/{id}", tournamentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTournament_BusinessException_Finished() throws Exception {
        doThrow(new BusinessException("Não é possível excluir um torneio já finalizado.")).when(tournamentService).deleteById(tournamentId);

        mockMvc.perform(delete("/api/tournaments/{id}", tournamentId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void startTournament_Success() throws Exception {
        tournamentResponseDTO.setStatus(TournamentStatus.ONGOING);
        when(tournamentService.startTournament(tournamentId)).thenReturn(tournamentResponseDTO);

        mockMvc.perform(post("/api/tournaments/{id}/start", tournamentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(TournamentStatus.ONGOING.name())));
    }
    
    @Test
    void startTournament_NotFound() throws Exception {
        when(tournamentService.startTournament(tournamentId))
            .thenThrow(new ResourceNotFoundException("Tournament", tournamentId));

        mockMvc.perform(post("/api/tournaments/{id}/start", tournamentId))
                .andExpect(status().isNotFound());
    }

     @Test
    void startTournament_BusinessException_NotPending() throws Exception {
        when(tournamentService.startTournament(tournamentId))
            .thenThrow(new BusinessException("Apenas torneios pendentes podem ser iniciados."));

        mockMvc.perform(post("/api/tournaments/{id}/start", tournamentId))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void finishTournament_Success() throws Exception {
        tournamentResponseDTO.setStatus(TournamentStatus.FINISHED);
        when(tournamentService.finishTournament(tournamentId)).thenReturn(tournamentResponseDTO);

        mockMvc.perform(post("/api/tournaments/{id}/finish", tournamentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(TournamentStatus.FINISHED.name())));
    }
}