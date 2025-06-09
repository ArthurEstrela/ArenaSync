package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size; // Importe esta anotação se usar Size
import jakarta.validation.constraints.Min; // Importe esta anotação se usar Min para idade

import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

public class UserRequestDTO {

    @NotNull(message = "O nome é obrigatório.")
    @Schema(description = "Nome completo do usuário", example = "Maria da Silva")
    private String name;

    @NotNull(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    @Schema(description = "Endereço de e-mail do usuário (deve ser único)", example = "maria.silva@example.com")
    private String email;

    @NotNull(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres") // Certifique-se de ter esta validação se quiser documentá-la
    @Schema(description = "Senha do usuário (mínimo 6 caracteres)", example = "senha@123")
    private String password;

    @NotNull(message = "A idade é obrigatória.") // Nota: @NotNull é para Strings. Para Integer use @NotNull e/ou @Min
    @Min(value = 1, message = "A idade deve ser um valor positivo.") // Adicione uma validação numérica se for um Integer
    @Schema(description = "Idade do usuário", example = "30")
    private Integer age;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}