package com.ajs.arenasync.Controller;

import com.ajs.arenasync.DTO.EnrollmentRequestDTO;
import com.ajs.arenasync.DTO.EnrollmentResponseDTO;
import com.ajs.arenasync.Services.EnrollmentService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    // 🔹 Criar uma nova inscrição
    @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> createEnrollment(@RequestBody @Valid EnrollmentRequestDTO dto) {
        EnrollmentResponseDTO savedEnrollment = enrollmentService.saveFromDTO(dto);
        return ResponseEntity.ok(savedEnrollment);
    }

    // 🔹 Buscar inscrição por ID
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDTO> getEnrollmentById(@PathVariable Long id) {
        EnrollmentResponseDTO dto = enrollmentService.findById(id);
        return ResponseEntity.ok(dto);
    }

    // 🔹 Listar todas as inscrições
    @GetMapping
    public ResponseEntity<List<EnrollmentResponseDTO>> getAllEnrollments() {
        List<EnrollmentResponseDTO> list = enrollmentService.findAll();
        return ResponseEntity.ok(list);
    }

    // 🔹 Deletar inscrição
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}