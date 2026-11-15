package br.com.hortech.dto;

public record IrrigacaoAtivaResponse(
        boolean ativa,
        String origem,
        int segundosRestantes
) {}
