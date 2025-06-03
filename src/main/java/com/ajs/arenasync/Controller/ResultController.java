package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.ResultRequestDTO;
import com.ajs.arenasync.DTO.ResultResponseDTO;
import com.ajs.arenasync.Services.ResultService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @PostMapping
    public ResponseEntity<ResultResponseDTO> createResult(@Valid @RequestBody ResultRequestDTO dto) {
        ResultResponseDTO created = resultService.save(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultResponseDTO> getResultById(@PathVariable Long id) {
        ResultResponseDTO result = resultService.findById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<ResultResponseDTO>> getAllResults() {
        List<ResultResponseDTO> results = resultService.findAll();
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        resultService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
