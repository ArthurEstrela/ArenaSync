package com.ajs.arenasync.Services;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ajs.arenasync.Entities.Result;
import com.ajs.arenasync.Repositories.ResultRepository;

@Service
public class ResultService {

    @Autowired
    private ResultRepository resultRepository;

    public Result save(Result result) {
        return resultRepository.save(result);
    }

    public Optional<Result> findById(Long id) {
        return resultRepository.findById(id);
    }

    public void deleteById(Long id) {
        resultRepository.deleteById(id);
    }
}