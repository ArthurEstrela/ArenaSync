package com.ajs.arenasync.Controller;

import com.ajs.arenasync.DTO.ReviewRequestDTO;
import com.ajs.arenasync.DTO.ReviewResponseDTO;
import com.ajs.arenasync.Services.ReviewService;
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

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReviewRequestDTO reviewRequestDTO;
    private ReviewResponseDTO reviewResponseDTO;
    private Long reviewId = 1L;
    private Long userId = 1L;
    private Long matchId = 1L;

    @BeforeEach
    void setUp() {
        reviewRequestDTO = new ReviewRequestDTO();
        reviewRequestDTO.setUserId(userId);
        reviewRequestDTO.setMatchId(matchId); // Obrigatório pela validação no service
        reviewRequestDTO.setRating(5);
        reviewRequestDTO.setComment("Excellent!");

        reviewResponseDTO = new ReviewResponseDTO();
        reviewResponseDTO.setId(reviewId);
        reviewResponseDTO.setRating(5);
        reviewResponseDTO.setComment("Excellent!");
        reviewResponseDTO.setUserName("User Test");
        reviewResponseDTO.setMatchInfo("Partida ID: " + matchId);
    }

    @Test
    void createReview_Success() throws Exception {
        when(reviewService.save(any(ReviewRequestDTO.class))).thenReturn(reviewResponseDTO);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comment", is(reviewResponseDTO.getComment())));
    }

    @Test
    void createReview_InvalidDTO_RatingNull() throws Exception {
        reviewRequestDTO.setRating(null); // Viola @NotNull

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReview_ServiceThrowsBadRequest_UserNotFound() throws Exception {
        when(reviewService.save(any(ReviewRequestDTO.class)))
            .thenThrow(new BadRequestException("Usuário não encontrado."));

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDTO)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void createReview_ServiceThrowsBadRequest_MatchIdNull() throws Exception {
        // Este teste simula o service lançando a exceção, que é o comportamento
        // se a validação do service for atingida.
        when(reviewService.save(any(ReviewRequestDTO.class)))
            .thenThrow(new BadRequestException("É necessário informar o ID da partida para avaliar."));
        
        // Para este DTO, passaremos o matchId nulo para o controller
        ReviewRequestDTO dtoWithNullMatchId = new ReviewRequestDTO();
        dtoWithNullMatchId.setUserId(userId);
        dtoWithNullMatchId.setMatchId(null); // Match ID nulo
        dtoWithNullMatchId.setRating(5);


        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoWithNullMatchId)))
                .andExpect(status().isBadRequest()); // GlobalExceptionHandler mapeia BadRequestException para 400
    }


    @Test
    void getReviewById_Success() throws Exception {
        when(reviewService.findById(reviewId)).thenReturn(reviewResponseDTO);

        mockMvc.perform(get("/api/reviews/{id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(reviewId.intValue())))
                .andExpect(jsonPath("$.comment", is(reviewResponseDTO.getComment())));
    }

    @Test
    void getReviewById_NotFound() throws Exception {
        when(reviewService.findById(reviewId)).thenThrow(new ResourceNotFoundException("Avaliação", reviewId));

        mockMvc.perform(get("/api/reviews/{id}", reviewId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllReviews_Success() throws Exception {
        when(reviewService.findAll()).thenReturn(Collections.singletonList(reviewResponseDTO));

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].comment", is(reviewResponseDTO.getComment())));
    }

    @Test
    void getAllReviews_Empty() throws Exception {
        when(reviewService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void deleteReview_Success() throws Exception {
        doNothing().when(reviewService).deleteById(reviewId);

        mockMvc.perform(delete("/api/reviews/{id}", reviewId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReview_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Avaliação", reviewId)).when(reviewService).deleteById(reviewId);

        mockMvc.perform(delete("/api/reviews/{id}", reviewId))
                .andExpect(status().isNotFound());
    }
}