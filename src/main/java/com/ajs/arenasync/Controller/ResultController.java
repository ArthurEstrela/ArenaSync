package com.ajs.arenasync.Controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ajs.arenasync.Entities.Result;
import com.ajs.arenasync.Services.ResultService;

@RestController
@RequestMapping("/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @GetMapping("/{id}")
    public ResponseEntity<Result> findById(@PathVariable Long id) {
        Optional<Result> obj = resultService.findById(id);
        return obj.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Result> insert(@RequestBody Result result) {
        Result savedResult = resultService.save(result);
        return ResponseEntity.ok(savedResult);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result> update(@PathVariable Long id, @RequestBody Result result) {
    Optional<Result> obj = resultService.findById(id);
    if (obj.isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    result.setId(id);
    Result updatedResult = resultService.save(result);
    return ResponseEntity.ok(updatedResult);
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resultService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}