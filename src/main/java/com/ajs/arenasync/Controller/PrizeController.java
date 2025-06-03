package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.PrizeRequestDTO;
import com.ajs.arenasync.DTO.PrizeResponseDTO;
import com.ajs.arenasync.Services.PrizeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/prizes")
public class PrizeController {

    @Autowired
    private PrizeService prizeService;

    @PostMapping
    public ResponseEntity<PrizeResponseDTO> createPrize(@Valid @RequestBody PrizeRequestDTO dto) {
        PrizeResponseDTO created = prizeService.save(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrizeResponseDTO> getPrizeById(@PathVariable Long id) {
        PrizeResponseDTO prize = prizeService.findById(id);
        return ResponseEntity.ok(prize);
    }

    @GetMapping
    public ResponseEntity<List<PrizeResponseDTO>> getAllPrizes() {
        List<PrizeResponseDTO> prizes = prizeService.findAll();
        return ResponseEntity.ok(prizes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrize(@PathVariable Long id) {
        prizeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
