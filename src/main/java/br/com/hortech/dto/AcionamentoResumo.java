package br.com.hortech.dto;

import java.time.LocalDateTime;

public record AcionamentoResumo(
        Long idLeitura,
        String origem,
        LocalDateTime dataHoraAcionamento
) {}
