package com.ajs.arenasync.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.Entities.Player;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.PlayerRepository;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    public Player save(Player player) {
        if (playerRepository.existsByEmail(player.getEmail())) {
            throw new BadRequestException("Já existe um jogador com esse e-mail.");
        }
        return playerRepository.save(player);
    }

    public Player findById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player", id));
    }

    public void deleteById(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Player", id);
        }

        playerRepository.deleteById(id);
    }

    public List<Player> getFreeAgents() {
        return playerRepository.findByTeamIsNull();
    }

}
