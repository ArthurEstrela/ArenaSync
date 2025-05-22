package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.Entities.Result;
import com.ajs.arenasync.Services.ResultService;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    // 🔹 Criar um novo resultado
    @PostMapping
    public ResponseEntity<Result> createResult(@RequestBody Result result) {
        Result savedResult = resultService.save(result);
        return ResponseEntity.ok(savedResult);
    }

    // 🔹 Buscar resultado por ID
    @GetMapping("/{id}")
    public ResponseEntity<Result> getResultById(@PathVariable Long id) {
        Result result = resultService.findById(id);
        return ResponseEntity.ok(result);
    }

    // 🔹 Listar todos os resultados
    @GetMapping
    public ResponseEntity<List<Result>> getAllResults() {
        List<Result> results = resultService.findAll();
        return ResponseEntity.ok(results);
    }

    // 🔹 Atualizar um resultado
    @PutMapping("/{id}")
    public ResponseEntity<Result> updateResult(@PathVariable Long id, @RequestBody Result updatedResult) {
        Result existingResult = resultService.findById(id);
        updatedResult.setId(existingResult.getId());
        Result savedResult = resultService.save(updatedResult);
        return ResponseEntity.ok(savedResult);
    }

    // 🔹 Deletar resultado por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        resultService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
