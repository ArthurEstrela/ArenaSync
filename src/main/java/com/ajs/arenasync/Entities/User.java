package com.ajs.arenasync.Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Inheritance; // Importe esta anotação
import jakarta.persistence.InheritanceType; // Importe esta anotação

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // ADICIONE ESTA LINHA!
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "O nome é obrigatório")
    private String name;
    @NotBlank(message = "A idade é obrigatório")
    private Integer age;
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String password;
    @Email(message = "Email inválido")
    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "organizer")
    private List<Tournament> tournaments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();

    public User() {
    }

    public User(Long id, @NotBlank(message = "O nome é obrigatório") String name,
            @NotBlank(message = "A idade é obrigatório") Integer age,
            @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres") String password,
            @Email(message = "Email inválido") String email, List<Tournament> tournaments, List<Review> reviews) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.password = password;
        this.email = email;
        this.tournaments = tournaments;
        this.reviews = reviews;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Tournament> getTournaments() {
        return tournaments;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
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
        User other = (User) obj;
        if (id != other.id)
            return false;
        return true;
    }

}
