package com.ajs.arenasync.Controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ajs.arenasync.Entities.LocationPlatform;
import com.ajs.arenasync.Services.LocationPlatformService;

@RestController
@RequestMapping("/location-platforms")
public class LocationPlatformController {

    @Autowired
    private LocationPlatformService locationPlatformService;

    @GetMapping("/{id}")
    public ResponseEntity<LocationPlatform> findById(@PathVariable Long id) {
        Optional<LocationPlatform> obj = locationPlatformService.findById(id);
        return obj.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LocationPlatform> insert(@RequestBody LocationPlatform locationPlatform) {
        LocationPlatform savedLocationPlatform = locationPlatformService.save(locationPlatform);
        return ResponseEntity.ok(savedLocationPlatform);
    }

    @PutMapping("/{id}")
public ResponseEntity<LocationPlatform> update(@PathVariable Long id, @RequestBody LocationPlatform locationPlatform) {
    Optional<LocationPlatform> obj = locationPlatformService.findById(id);
    if (obj.isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    locationPlatform.setId(id);
    LocationPlatform updatedLocationPlatform = locationPlatformService.save(locationPlatform);
    return ResponseEntity.ok(updatedLocationPlatform);
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        locationPlatformService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}