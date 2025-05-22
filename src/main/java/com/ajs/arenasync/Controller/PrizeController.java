package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.Entities.Prize;
import com.ajs.arenasync.Services.PrizeService;

@RestController
@RequestMapping("/api/prizes")
public class PrizeController {

    @Autowired
    private PrizeService prizeService;

    // 🔹 Criar um novo prêmio
    @PostMapping
    public ResponseEntity<Prize> createPrize(@RequestBody Prize prize) {
        Prize savedPrize = prizeService.save(prize);
        return ResponseEntity.ok(savedPrize);
    }

    // 🔹 Buscar prêmio por ID
    @GetMapping("/{id}")
    public ResponseEntity<Prize> getPrizeById(@PathVariable Long id) {
        Prize prize = prizeService.findById(id);
        return ResponseEntity.ok(prize);
    }

    // 🔹 Listar todos os prêmios
    @GetMapping
    public ResponseEntity<List<Prize>> getAllPrizes() {
        List<Prize> prizes = prizeService.findAll();
        return ResponseEntity.ok(prizes);
    }

    // 🔹 Atualizar um prêmio existente
    @PutMapping("/{id}")
    public ResponseEntity<Prize> updatePrize(@PathVariable Long id, @RequestBody Prize updatedPrize) {
        Prize existingPrize = prizeService.findById(id);
        updatedPrize.setId(existingPrize.getId());
        Prize savedPrize = prizeService.save(updatedPrize);
        return ResponseEntity.ok(savedPrize);
    }

    // 🔹 Deletar prêmio
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrize(@PathVariable Long id) {
        prizeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
