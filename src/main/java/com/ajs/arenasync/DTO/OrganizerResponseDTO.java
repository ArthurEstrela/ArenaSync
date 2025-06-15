package com.ajs.arenasync.DTO;

import org.springframework.hateoas.RepresentationModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(Include.NON_NULL)
@Schema(description = "Detalhes do organizador retornado pela API")
public class OrganizerResponseDTO extends RepresentationModel<OrganizerResponseDTO>{

    @Schema(description = "ID único do organizador", example = "1")
    private Long id;

    @Schema(description = "Nome do organizador", example = "Empresa Eventos Ltda.")
    private String name;

    @Schema(description = "Idade do organizador", example = "35")
    private Integer age; // Campo 'age' herdado de User

    @Schema(description = "Endereço de e-mail do organizador", example = "contato@empresaeventos.com")
    private String email;

    @Schema(description = "Número de telefone do organizador", example = "5511987654321")
    private String phoneNumber;

    @Schema(description = "Nome da organização", example = "Mega Eventos S.A.")
    private String organizationName;

    @Schema(description = "Biografia ou descrição do organizador", example = "Especialistas em eventos esportivos e de e-sports.")
    private String bio;

    @Schema(description = "Links para redes sociais ou website do organizador", example = "linkedin.com/empresaeventos")
    private String socialLinks;

    // Getters e Setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
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
}
