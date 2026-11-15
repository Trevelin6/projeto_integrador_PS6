package br.com.hortech.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record AgendamentoIrrigacaoRequest(
        @NotNull LocalTime horarioExecucao,
        @NotNull @Min(1) Integer tempoIrrigacaoSegundos
) {}
