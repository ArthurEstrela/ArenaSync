package com.ajs.arenasync.Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ajs.arenasync.Entities.Enums.TournamentType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Tournament implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "O nome é obrigatório")  // Não pode ser nulo ou vazio
    private String name;
    @NotBlank(message = "O tipo é obrigatório")  // Não pode ser nulo ou vazio
    private TournamentType type;
    @NotBlank(message = "A modalidade é obrigatório")  // Não pode ser nulo ou vazio
    private String modality;
    private String rules;
    private Date startDate;
    private Date endDate;

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

    public Tournament(Long id, String name, TournamentType type, String modality, String rules, Date startDate,
            Date endDate, List<Enrollment> enrollments, List<Match> matches, List<Prize> prizes, List<Review> reviews,
            User organizer) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.modality = modality;
        this.rules = rules;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
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
