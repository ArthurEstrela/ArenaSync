package com.ajs.arenasync.Controller;

import com.ajs.arenasync.DTO.TeamRequestDTO;
import com.ajs.arenasync.DTO.TeamResponseDTO;
import com.ajs.arenasync.Services.TeamService;
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

@WebMvcTest(TeamController.class)
public class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @Autowired
    private ObjectMapper objectMapper;

    private TeamRequestDTO teamRequestDTO;
    private TeamResponseDTO teamResponseDTO;
    private Long teamId = 1L;

    @BeforeEach
    void setUp() {
        teamRequestDTO = new TeamRequestDTO();
        teamRequestDTO.setName("Warriors");

        teamResponseDTO = new TeamResponseDTO();
        teamResponseDTO.setId(teamId);
        teamResponseDTO.setName("Warriors");
    }

    @Test
    void createTeam_Success() throws Exception {
        when(teamService.save(any(TeamRequestDTO.class))).thenReturn(teamResponseDTO);

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(teamResponseDTO.getName())));
    }

    @Test
    void createTeam_InvalidDTO_NameBlank() throws Exception {
        teamRequestDTO.setName("");

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTeam_ServiceThrowsBadRequest_NameExists() throws Exception {
        when(teamService.save(any(TeamRequestDTO.class)))
            .thenThrow(new BadRequestException("Já existe um time com esse nome."));

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTeamById_Success() throws Exception {
        when(teamService.findById(teamId)).thenReturn(teamResponseDTO);

        mockMvc.perform(get("/api/teams/{id}", teamId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$.id", is(teamId.intValue())))
                .andExpect(jsonPath("$.name", is(teamResponseDTO.getName())));
    }

    @Test
    void getTeamById_NotFound() throws Exception {
        when(teamService.findById(teamId)).thenThrow(new ResourceNotFoundException("Time", teamId));

        mockMvc.perform(get("/api/teams/{id}", teamId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTeam_Success() throws Exception {
        when(teamService.update(anyLong(), any(TeamRequestDTO.class))).thenReturn(teamResponseDTO);

        mockMvc.perform(put("/api/teams/{id}", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(teamResponseDTO.getName())));
    }

    @Test
    void updateTeam_NotFound() throws Exception {
        when(teamService.update(anyLong(), any(TeamRequestDTO.class)))
            .thenThrow(new ResourceNotFoundException("Time", teamId));
        
        mockMvc.perform(put("/api/teams/{id}", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDTO)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void updateTeam_ServiceThrowsBadRequest_NewNameExists() throws Exception {
        when(teamService.update(anyLong(), any(TeamRequestDTO.class)))
            .thenThrow(new BadRequestException("Já existe outro time com esse nome."));

        mockMvc.perform(put("/api/teams/{id}", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDTO)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void deleteTeam_Success() throws Exception {
        doNothing().when(teamService).deleteById(teamId);

        mockMvc.perform(delete("/api/teams/{id}", teamId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTeam_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Time", teamId)).when(teamService).deleteById(teamId);

        mockMvc.perform(delete("/api/teams/{id}", teamId))
                .andExpect(status().isNotFound());
    }

    // Este método testará o getAllTeams() do controlador real.
    // Ele não deve ser o método getAllTeams() do controlador copiado para cá.
    @Test
    void testGetAllTeams_Success() throws Exception {
        when(teamService.findAll()).thenReturn(Collections.singletonList(teamResponseDTO)); // Mocka o serviço

        mockMvc.perform(get("/api/teams")) // Chama o endpoint do controlador
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json"))) // Espera HATEOAS
                .andExpect(jsonPath("$._embedded.teamResponseDTOList", hasSize(1))) // Verifica a estrutura HATEOAS
                .andExpect(jsonPath("$._embedded.teamResponseDTOList[0].name", is(teamResponseDTO.getName())));
    }

    @Test
    void testGetAllTeams_Empty() throws Exception {
        when(teamService.findAll()).thenReturn(Collections.emptyList()); // Mocka o serviço para retornar vazio

        mockMvc.perform(get("/api/teams")) // Chama o endpoint do controlador
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json"))) // Espera HATEOAS
                .andExpect(jsonPath("$._embedded").doesNotExist()) // Não deve ter _embedded se a lista estiver vazia
                .andExpect(jsonPath("$._links").exists()); // Mas deve ter os links HATEOAS
    }

}