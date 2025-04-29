package com.ajs.arenasync.Resources;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ajs.arenasync.Entities.Statistic;
import com.ajs.arenasync.Services.StatisticService;

@RestController
@RequestMapping("/statistics")
public class StatisticResource {

    @Autowired
    private StatisticService statisticService;

    @GetMapping("/{id}")
    public ResponseEntity<Statistic> findById(@PathVariable Long id) {
        Optional<Statistic> obj = statisticService.findById(id);
        return obj.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Statistic> insert(@RequestBody Statistic statistic) {
        Statistic savedStatistic = statisticService.save(statistic);
        return ResponseEntity.ok(savedStatistic);
    }

    @PutMapping("/{id}")
public ResponseEntity<Statistic> update(@PathVariable Long id, @RequestBody Statistic statistic) {
    Optional<Statistic> obj = statisticService.findById(id);
    if (obj.isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    statistic.setId(id);
    Statistic updatedStatistic = statisticService.save(statistic);
    return ResponseEntity.ok(updatedStatistic);
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        statisticService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}