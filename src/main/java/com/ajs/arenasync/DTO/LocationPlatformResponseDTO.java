package com.ajs.arenasync.DTO;

import org.springframework.hateoas.RepresentationModel;

import com.ajs.arenasync.Entities.Enums.TournamentType; // Importe este enum
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(Include.NON_NULL)
@Schema(description = "Detalhes do local/plataforma retornado pela API")
public class LocationPlatformResponseDTO extends RepresentationModel<LocationPlatformResponseDTO>{

    @Schema(description = "ID único do local/plataforma", example = "1")
    private Long id;

    @Schema(description = "Nome do local ou plataforma", example = "Arena Gaming X")
    private String name;

    @Schema(description = "Descrição do local/plataforma", example = "Local para e-sports com equipamentos de alta qualidade.")
    private String description;

    @Schema(description = "Tipo do local/plataforma", example = "ESPORT", allowableValues = {"SPORT", "ESPORT"}) // CORRIGIDO: Exemplo para enum
    private TournamentType type; // CORRIGIDO: Tipo é TournamentType

    @Schema(description = "Endereço do local (se aplicável)", example = "Rua dos Jogos, 123")
    private String address;

    @Schema(description = "URL da plataforma (se aplicável)", example = "www.arenagamingx.com")
    private String url;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TournamentType getType() { // O getter agora retorna TournamentType
        return type;
    }

    public void setType(TournamentType type) { // O setter agora recebe TournamentType
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}