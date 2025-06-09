package com.ajs.arenasync.Controller;

import com.ajs.arenasync.DTO.EnrollmentRequestDTO;
import com.ajs.arenasync.DTO.EnrollmentResponseDTO;
import com.ajs.arenasync.Entities.Enums.Status;
import com.ajs.arenasync.Services.EnrollmentService;
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

@WebMvcTest(EnrollmentController.class)
public class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnrollmentService enrollmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private EnrollmentRequestDTO requestDTO;
    private EnrollmentResponseDTO responseDTO;
    private Long enrollmentId = 1L;

    @BeforeEach
    void setUp() {
        requestDTO = new EnrollmentRequestDTO();
        requestDTO.setTeamId(1L);
        requestDTO.setTournamentId(1L);
        requestDTO.setStatus(Status.PENDING);

        responseDTO = new EnrollmentResponseDTO();
        responseDTO.setId(enrollmentId);
        responseDTO.setTeamName("Team Titans");
        responseDTO.setTournamentName("Summer Cup");
        responseDTO.setStatus(Status.PENDING.name());
    }

    @Test
    void createEnrollment_Success() throws Exception {
        when(enrollmentService.saveFromDTO(any(EnrollmentRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName", is(responseDTO.getTeamName())));
    }

    @Test
    void createEnrollment_InvalidDTO_StatusNull() throws Exception {
        requestDTO.setStatus(null);

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEnrollment_ServiceThrowsBadRequest_TeamNotFound() throws Exception {
        when(enrollmentService.saveFromDTO(any(EnrollmentRequestDTO.class)))
            .thenThrow(new BadRequestException("Time informado não encontrado."));

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEnrollmentById_Success() throws Exception {
        when(enrollmentService.findById(enrollmentId)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/enrollments/{id}", enrollmentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$.id", is(enrollmentId.intValue())))
                .andExpect(jsonPath("$.teamName", is(responseDTO.getTeamName())));
    }

    @Test
    void getEnrollmentById_NotFound() throws Exception {
        when(enrollmentService.findById(enrollmentId)).thenThrow(new ResourceNotFoundException("Inscrição", enrollmentId));

        mockMvc.perform(get("/api/enrollments/{id}", enrollmentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllEnrollments_Success() throws Exception {
        when(enrollmentService.findAll()).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/api/enrollments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$._embedded.enrollmentResponseDTOList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.enrollmentResponseDTOList[0].teamName", is(responseDTO.getTeamName())));
    }
    
    @Test
    void getAllEnrollments_Empty() throws Exception {
        when(enrollmentService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/enrollments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$._embedded").doesNotExist())
                .andExpect(jsonPath("$._links").exists());
    }


    @Test
    void deleteEnrollment_Success() throws Exception {
        doNothing().when(enrollmentService).deleteById(enrollmentId);

        mockMvc.perform(delete("/api/enrollments/{id}", enrollmentId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEnrollment_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Inscrição", enrollmentId)).when(enrollmentService).deleteById(enrollmentId);

        mockMvc.perform(delete("/api/enrollments/{id}", enrollmentId))
                .andExpect(status().isNotFound());
    }
}