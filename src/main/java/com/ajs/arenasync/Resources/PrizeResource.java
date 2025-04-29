package com.ajs.arenasync.Resources;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ajs.arenasync.Entities.Prize;
import com.ajs.arenasync.Services.PrizeService;

@RestController
@RequestMapping("/prizes")
public class PrizeResource {

    @Autowired
    private PrizeService prizeService;

    @GetMapping("/{id}")
    public ResponseEntity<Prize> findById(@PathVariable Long id) {
        Optional<Prize> obj = prizeService.findById(id);
        return obj.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Prize> insert(@RequestBody Prize prize) {
        Prize savedPrize = prizeService.save(prize);
        return ResponseEntity.ok(savedPrize);
    }

    @PutMapping("/{id}")
public ResponseEntity<Prize> update(@PathVariable Long id, @RequestBody Prize prize) {
    Optional<Prize> obj = prizeService.findById(id);
    if (obj.isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    prize.setId(id);
    Prize updatedPrize = prizeService.save(prize);
    return ResponseEntity.ok(updatedPrize);
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prizeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}