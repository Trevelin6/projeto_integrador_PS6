package br.com.hortech.dto;

import jakarta.validation.constraints.NotNull;

public record LeituraSensorRequest(
        @NotNull Integer umidade,
        @NotNull Integer temperatura,
        @NotNull Integer luminosidade
) {}