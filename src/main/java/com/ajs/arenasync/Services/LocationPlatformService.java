package com.ajs.arenasync.Services;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ajs.arenasync.Entities.LocationPlatform;
import com.ajs.arenasync.Repositories.LocationPlatformRepository;

@Service
public class LocationPlatformService {

    @Autowired
    private LocationPlatformRepository locationPlatformRepository;

    public LocationPlatform save(LocationPlatform locationPlatform) {
        return locationPlatformRepository.save(locationPlatform);
    }

    public Optional<LocationPlatform> findById(Long id) {
        return locationPlatformRepository.findById(id);
    }

    public void deleteById(Long id) {
        locationPlatformRepository.deleteById(id);
    }
}