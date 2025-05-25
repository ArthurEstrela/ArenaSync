package com.ajs.arenasync.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ajs.arenasync.Entities.Statistic;
import com.ajs.arenasync.Services.StatisticService;

@RestController
@RequestMapping("/statistics")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

  

    @PostMapping
    public ResponseEntity<Statistic> insert(@RequestBody Statistic statistic) {
        Statistic savedStatistic = statisticService.save(statistic);
        return ResponseEntity.ok(savedStatistic);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        statisticService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}