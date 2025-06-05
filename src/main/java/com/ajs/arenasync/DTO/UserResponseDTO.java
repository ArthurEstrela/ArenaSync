package com.ajs.arenasync.DTO;

import org.springframework.hateoas.RepresentationModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@JsonInclude(Include.NON_NULL)
@Schema(description = "Detalhes do usuário retornado pela API") // Anotação na classe do DTO de resposta
public class UserResponseDTO extends RepresentationModel<UserResponseDTO>{

    @Schema(description = "ID único do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome completo do usuário", example = "Maria da Silva")
    private String name;

    @Schema(description = "Endereço de e-mail do usuário", example = "maria.silva@example.com")
    private String email;

    @Schema(description = "Idade do usuário", example = "30")
    private Integer age;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}