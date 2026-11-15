package br.com.hortech.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ConfiguracaoHortaRequest(
        @NotNull @Min(0) @Max(100) Integer umidadeLimite,
        @NotNull @Min(0) Integer luminosidadeLimite,
        @NotNull @Min(0) Integer temperaturaLimite,
        @NotNull @Min(1) Integer tempoIrrigacaoPadrao,
        @NotBlank @Pattern(regexp = "AUTOMATICO|MANUAL|DESLIGADO") String modoAutomacao
) {}
