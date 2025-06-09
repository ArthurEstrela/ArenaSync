package com.ajs.arenasync.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;

import com.ajs.arenasync.DTO.TournamentRequestDTO;
import com.ajs.arenasync.DTO.TournamentResponseDTO;
import com.ajs.arenasync.Entities.Enums.TournamentStatus;
import com.ajs.arenasync.Entities.Enums.TournamentType;
import com.ajs.arenasync.Entities.Organizer;
import com.ajs.arenasync.Entities.Tournament;
import com.ajs.arenasync.Exceptions.BusinessException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.OrganizerRepository;
import com.ajs.arenasync.Repositories.TournamentRepository;

import org.springframework.data.domain.Page; // Importe Page
import org.springframework.data.domain.Pageable; // Importe Pageable

@Service
@CacheConfig(cacheNames = "tournaments") // Nome do cache para torneios
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @CacheEvict(key = "'allTournaments'", allEntries = true) // Invalida o cache da lista completa
    @CachePut(key = "#result.id") // Adiciona/atualiza o torneio recém-criado no cache
    public TournamentResponseDTO createTournament(Long organizerId, TournamentRequestDTO dto) {
        Organizer organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer", organizerId)); // Lança ResourceNotFoundException se o organizador não for encontrado

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("A data de término deve ser posterior ou igual à data de início."); // Lança BusinessException se a data de término for anterior à de início
        }

        Tournament tournament = toEntity(dto); // Converte DTO para entidade
        tournament.setOrganizer(organizer); // Associa o organizador ao torneio
        tournament.setStatus(TournamentStatus.PENDING); // status inicial

        return toResponseDTO(tournamentRepository.save(tournament)); // Salva o torneio e converte para DTO de resposta
    }

    // Buscar por ID
    @Cacheable(key = "#id") // Armazena em cache o resultado
    public TournamentResponseDTO findById(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id)); // Lança ResourceNotFoundException se o torneio não for encontrado
        return toResponseDTO(tournament); // Converte para DTO de resposta
    }

    // Deletar
    @CacheEvict(key = "#id", allEntries = true) // Remove o torneio específico e invalida a lista completa
    public void deleteById(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id)); // Lança ResourceNotFoundException se o torneio não for encontrado

        if (tournament.getStatus() == TournamentStatus.FINISHED) {
            throw new BusinessException("Não é possível excluir um torneio já finalizado."); // Lança BusinessException se o torneio estiver finalizado
        }

        tournamentRepository.deleteById(id); // Deleta o torneio
    }

    // Iniciar
    @CachePut(key = "#id") // Atualiza o torneio no cache após iniciar
    @CacheEvict(key = "'allTournaments'", allEntries = true) // Invalida o cache da lista completa
    public TournamentResponseDTO startTournament(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id)); // Lança ResourceNotFoundException se o torneio não for encontrado

        if (tournament.getStatus() != TournamentStatus.PENDING) {
            throw new BusinessException("Apenas torneios pendentes podem ser iniciados."); // Lança BusinessException se o torneio não estiver pendente
        }

        tournament.setStatus(TournamentStatus.ONGOING); // Muda o status para ONGOING
        return toResponseDTO(tournamentRepository.save(tournament)); // Salva e converte para DTO de resposta
    }

    // Finalizar
    @CachePut(key = "#id") // Atualiza o torneio no cache após finalizar
    @CacheEvict(key = "'allTournaments'", allEntries = true) // Invalida o cache da lista completa
    public TournamentResponseDTO finishTournament(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id)); // Lança ResourceNotFoundException se o torneio não for encontrado

        if (tournament.getStatus() != TournamentStatus.ONGOING) {
            throw new BusinessException("Apenas torneios em andamento podem ser finalizados."); // Lança BusinessException se o torneio não estiver em andamento
        }

        tournament.setStatus(TournamentStatus.FINISHED); // Muda o status para FINISHED
        return toResponseDTO(tournamentRepository.save(tournament)); // Salva e converte para DTO de resposta
    }

    // Listar todos com paginação
    @Cacheable(key = "'allTournaments_paged_' + #pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort") // Cacheia a lista paginada de torneios
    public Page<TournamentResponseDTO> getAllTournaments(Pageable pageable) {
        return tournamentRepository.findAll(pageable) // Usa findAll(Pageable) para obter a página de entidades
                .map(this::toResponseDTO); // Mapeia a Page de entidades para Page de DTOs
    }

    // Atualizar
    @CachePut(key = "#id") // Atualiza o torneio no cache
    @CacheEvict(key = "'allTournaments'", allEntries = true) // Invalida o cache da lista completa
    public TournamentResponseDTO updateTournament(Long id, TournamentRequestDTO dto) {
        Tournament existing = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id)); // Lança ResourceNotFoundException se o torneio não for encontrado

        existing.setName(dto.getName()); // Atualiza o nome
        existing.setModality(dto.getModality()); // Atualiza a modalidade
        existing.setRules(dto.getRules()); // Atualiza as regras
        existing.setStartDate(dto.getStartDate()); // Atualiza a data de início
        existing.setEndDate(dto.getEndDate()); // Atualiza a data de término
        existing.setType(dto.getType()); // Atualiza o tipo

        return toResponseDTO(tournamentRepository.save(existing)); // Salva o torneio atualizado e converte para DTO de resposta
    }

    // Conversão DTO -> Entidade (sem mudanças)
    private Tournament toEntity(TournamentRequestDTO dto) {
        Tournament t = new Tournament(); // Cria nova entidade Tournament
        t.setName(dto.getName()); // Define o nome
        t.setModality(dto.getModality()); // Define a modalidade
        t.setRules(dto.getRules()); // Define as regras
        t.setStartDate(dto.getStartDate()); // Define a data de início
        t.setEndDate(dto.getEndDate()); // Define a data de término
        t.setType(dto.getType()); // Define o tipo
        return t; // Retorna a entidade Tournament
    }

    // Conversão Entidade -> DTO (sem mudanças)
    private TournamentResponseDTO toResponseDTO(Tournament t) {
        TournamentResponseDTO dto = new TournamentResponseDTO(); // Cria novo DTO de resposta
        dto.setId(t.getId()); // Define o ID
        dto.setName(t.getName()); // Define o nome
        dto.setModality(t.getModality()); // Define a modalidade
        dto.setRules(t.getRules()); // Define as regras
        dto.setStartDate(t.getStartDate()); // Define a data de início
        dto.setEndDate(t.getEndDate()); // Define a data de término
        dto.setType(t.getType()); // Define o tipo
        dto.setStatus(t.getStatus()); // Define o status
        dto.setOrganizerName(t.getOrganizer() != null ? t.getOrganizer().getName() : null); // Define o nome do organizador, se existir
        return dto; // Retorna o DTO de resposta
    }
}