package com.ajs.arenasync.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.StatisticRequestDTO;
import com.ajs.arenasync.DTO.StatisticResponseDTO;
import com.ajs.arenasync.Services.StatisticService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/statistics")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @PostMapping
    public ResponseEntity<StatisticResponseDTO> createStatistic(@Valid @RequestBody StatisticRequestDTO dto) {
        StatisticResponseDTO created = statisticService.save(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatisticResponseDTO> getStatisticById(@PathVariable Long id) {
        StatisticResponseDTO statistic = statisticService.findById(id);
        return ResponseEntity.ok(statistic);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatisticResponseDTO> updateStatistic(@PathVariable Long id, @Valid @RequestBody StatisticRequestDTO dto) {
        StatisticResponseDTO updated = statisticService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatistic(@PathVariable Long id) {
        statisticService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
