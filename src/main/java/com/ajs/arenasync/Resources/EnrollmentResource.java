package com.ajs.arenasync.Resources;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ajs.arenasync.Entities.Enrollment;
import com.ajs.arenasync.Services.EnrollmentService;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentResource {

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping("/{id}")
    public ResponseEntity<Enrollment> findById(@PathVariable Long id) {
        Optional<Enrollment> obj = enrollmentService.findById(id);
        return obj.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Enrollment> insert(@RequestBody Enrollment enrollment) {
        Enrollment savedEnrollment = enrollmentService.save(enrollment);
        return ResponseEntity.ok(savedEnrollment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Enrollment> update(@PathVariable Long id, @RequestBody Enrollment enrollment) {
        Optional<Enrollment> obj = enrollmentService.findById(id);
        if (obj.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        enrollment.setId(id);
        Enrollment updatedEnrollment = enrollmentService.save(enrollment);
        return ResponseEntity.ok(updatedEnrollment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        enrollmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}