package com.ajs.arenasync.Services;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ajs.arenasync.Entities.Prize;
import com.ajs.arenasync.Repositories.PrizeRepository;

@Service
public class PrizeService {

    @Autowired
    private PrizeRepository prizeRepository;

    public Prize save(Prize prize) {
        return prizeRepository.save(prize);
    }

    public Optional<Prize> findById(Long id) {
        return prizeRepository.findById(id);
    }

    public void deleteById(Long id) {
        prizeRepository.deleteById(id);
    }
}