package com.ajs.arenasync.Entities;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Organizer extends User implements Serializable {
    private static final long serialVersionUID = 1L;


    private String organizationName;
    private String phoneNumber;
    private String bio;
    private String socialLinks;
    

    @OneToMany(mappedBy = "organizer")
    private List<Tournament> tournaments;


    public Organizer(Long id, @NotBlank(message = "O nome é obrigatório") String name,
            @NotBlank(message = "A idade é obrigatório") Integer age,
            @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres") String password,
            @Email(message = "Email inválido") String email, List<Tournament> tournaments, List<Review> reviews,
            String organizationName, String phoneNumber, String bio, String socialLinks,
            List<Tournament> tournaments2) {
        super(id, name, age, password, email, tournaments, reviews);
        this.organizationName = organizationName;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
        this.socialLinks = socialLinks;
        tournaments = tournaments2;
    }


    public Organizer(String organizationName, String phoneNumber, String bio, String socialLinks,
            List<Tournament> tournaments) {
        this.organizationName = organizationName;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
        this.socialLinks = socialLinks;
        this.tournaments = tournaments;
    }


    public String getOrganizationName() {
        return organizationName;
    }


    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getBio() {
        return bio;
    }


    public void setBio(String bio) {
        this.bio = bio;
    }


    public String getSocialLinks() {
        return socialLinks;
    }


    public void setSocialLinks(String socialLinks) {
        this.socialLinks = socialLinks;
    }


    public List<Tournament> getTournaments() {
        return tournaments;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((organizationName == null) ? 0 : organizationName.hashCode());
        result = prime * result + ((tournaments == null) ? 0 : tournaments.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Organizer other = (Organizer) obj;
        if (organizationName == null) {
            if (other.organizationName != null)
                return false;
        } else if (!organizationName.equals(other.organizationName))
            return false;
        if (tournaments == null) {
            if (other.tournaments != null)
                return false;
        } else if (!tournaments.equals(other.tournaments))
            return false;
        return true;
    }

    

    
}
