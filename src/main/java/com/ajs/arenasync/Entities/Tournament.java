package com.ajs.arenasync.Entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ajs.arenasync.Entities.Enums.TournamentStatus;
import com.ajs.arenasync.Entities.Enums.TournamentType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Tournament implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "O nome é obrigatório") // Não pode ser nulo ou vazio
    private String name;

    @NotNull(message = "O tipo é obrigatório") // Altere de @NotBlank para @NotNull
    @Enumerated(EnumType.STRING) // ADICIONE ESTA LINHA!
    private TournamentType type;
    
    @NotBlank(message = "A modalidade é obrigatório") // Não pode ser nulo ou vazio
    private String modality;
    private String rules;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private TournamentStatus status;

    @OneToMany(mappedBy = "tournament")
    private List<Enrollment> enrollments = new ArrayList<>();
    @OneToMany(mappedBy = "tournament")
    private List<Match> matches = new ArrayList<>();
    @OneToMany(mappedBy = "tournament")
    private List<Prize> prizes = new ArrayList<>();
    @OneToMany(mappedBy = "tournament")
    private List<Review> reviews = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizer;

    public Tournament() {
    }

    public Tournament(Long id, @NotBlank(message = "O nome é obrigatório") String name,
            @NotBlank(message = "O tipo é obrigatório") TournamentType type,
            @NotBlank(message = "A modalidade é obrigatório") String modality, String rules, LocalDate startDate,
            LocalDate endDate, TournamentStatus status, List<Enrollment> enrollments, List<Match> matches,
            List<Prize> prizes, List<Review> reviews, User organizer) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.modality = modality;
        this.rules = rules;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.enrollments = enrollments;
        this.matches = matches;
        this.prizes = prizes;
        this.reviews = reviews;
        this.organizer = organizer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TournamentType getType() {
        return type;
    }

    public void setType(TournamentType type) {
        this.type = type;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public List<Prize> getPrizes() {
        return prizes;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Tournament other = (Tournament) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
