package com.ajs.arenasync.Controller;

import com.ajs.arenasync.DTO.PlayerRequestDTO;
import com.ajs.arenasync.DTO.PlayerResponseDTO;
import com.ajs.arenasync.Services.PlayerService;
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
// Importar nullValue não é mais necessário aqui, pois usaremos doesNotExist()
// import static org.hamcrest.Matchers.nullValue;

@WebMvcTest(PlayerController.class)
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    @Autowired
    private ObjectMapper objectMapper;

    private PlayerRequestDTO playerRequestDTO;
    private PlayerResponseDTO playerResponseDTO;
    private Long playerId = 1L;
    private Long teamId = 1L;

    @BeforeEach
    void setUp() {
        playerRequestDTO = new PlayerRequestDTO();
        playerRequestDTO.setName("Test Player");
        playerRequestDTO.setEmail("player@example.com");
        playerRequestDTO.setPosition("Mid Laner"); // Adicionado para teste
        playerRequestDTO.setTeamId(teamId);

        playerResponseDTO = new PlayerResponseDTO();
        playerResponseDTO.setId(playerId);
        playerResponseDTO.setName("Test Player");
        playerResponseDTO.setEmail("player@example.com");
        playerResponseDTO.setPosition("Mid Laner"); // Adicionado para teste
        playerResponseDTO.setTeamName("Test Team");
    }

    @Test
    void createPlayer_Success() throws Exception {
        when(playerService.saveFromDTO(any(PlayerRequestDTO.class))).thenReturn(playerResponseDTO);

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(playerResponseDTO.getName())))
                .andExpect(jsonPath("$.position", is(playerResponseDTO.getPosition()))); // Verifica a posição
    }

    @Test
    void createPlayer_InvalidDTO_NameBlank() throws Exception {
        playerRequestDTO.setName("");

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPlayer_ServiceThrowsBadRequest_EmailExists() throws Exception {
        when(playerService.saveFromDTO(any(PlayerRequestDTO.class)))
            .thenThrow(new BadRequestException("Já existe um jogador com esse e-mail."));

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPlayerById_Success() throws Exception {
        when(playerService.findById(playerId)).thenReturn(playerResponseDTO);

        mockMvc.perform(get("/api/players/{id}", playerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$.id", is(playerId.intValue())))
                .andExpect(jsonPath("$.name", is(playerResponseDTO.getName())))
                .andExpect(jsonPath("$.position", is(playerResponseDTO.getPosition()))); // Verifica a posição
    }

    @Test
    void getPlayerById_NotFound() throws Exception {
        when(playerService.findById(playerId)).thenThrow(new ResourceNotFoundException("Player", playerId));

        mockMvc.perform(get("/api/players/{id}", playerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPlayers_Success() throws Exception {
        when(playerService.findAll()).thenReturn(Collections.singletonList(playerResponseDTO));

        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$._embedded.playerResponseDTOList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.playerResponseDTOList[0].name", is(playerResponseDTO.getName())))
                .andExpect(jsonPath("$._embedded.playerResponseDTOList[0].position", is(playerResponseDTO.getPosition()))); // Verifica a posição
    }
    
    @Test
    void getAllPlayers_Empty() throws Exception {
        when(playerService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$._embedded").doesNotExist())
                .andExpect(jsonPath("$._links").exists());
    }


    @Test
    void getFreeAgents_Success() throws Exception {
        PlayerResponseDTO freeAgentResponse = new PlayerResponseDTO();
        freeAgentResponse.setId(2L);
        freeAgentResponse.setName("Free Agent");
        freeAgentResponse.setEmail("free@example.com");
        freeAgentResponse.setPosition(null); // Agente livre pode não ter posição definida
        freeAgentResponse.setTeamName(null);

        when(playerService.getFreeAgents()).thenReturn(Collections.singletonList(freeAgentResponse));

        mockMvc.perform(get("/api/players/free-agents"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$._embedded.playerResponseDTOList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.playerResponseDTOList[0].name", is("Free Agent")))
                // CORREÇÃO: Usar .doesNotExist() para verificar a ausência do campo 'position'
                .andExpect(jsonPath("$._embedded.playerResponseDTOList[0].position").doesNotExist()) 
                .andExpect(jsonPath("$._embedded.playerResponseDTOList[0].teamName").doesNotExist());
    }

    @Test
    void deletePlayer_Success() throws Exception {
        doNothing().when(playerService).deleteById(playerId);

        mockMvc.perform(delete("/api/players/{id}", playerId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePlayer_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Player", playerId)).when(playerService).deleteById(playerId);

        mockMvc.perform(delete("/api/players/{id}", playerId))
                .andExpect(status().isNotFound());
    }
}
