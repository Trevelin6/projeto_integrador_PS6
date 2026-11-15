package br.com.hortech.service;

import br.com.hortech.dto.IrrigacaoAtivaResponse;
import br.com.hortech.model.HistoricoAcionamentoBomba;
import br.com.hortech.model.HistoricoLeituraSensor;
import br.com.hortech.model.Horta;
import br.com.hortech.repository.AcionamentoRepository;
import br.com.hortech.repository.HortaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class IrrigacaoControlService {

    private static final ZoneId BRASILIA = ZoneId.of("America/Sao_Paulo");

    private final AcionamentoRepository acionamentoRepository;
    private final HortaRepository hortaRepository;

    public IrrigacaoControlService(AcionamentoRepository acionamentoRepository, HortaRepository hortaRepository) {
        this.acionamentoRepository = acionamentoRepository;
        this.hortaRepository = hortaRepository;
    }

    @Transactional
    public synchronized boolean iniciar(Horta horta, HistoricoLeituraSensor leitura, String origem, int duracaoSegundos) {
        Optional<Horta> hortaAtual = hortaRepository.findById(horta.getIdHorta());
        if (hortaAtual.isEmpty() || !modoPermiteIrrigacao(hortaAtual.get().getModoAutomacao(), origem)) {
            return false;
        }

        if (obterAtiva(horta.getIdHorta(), LocalDateTime.now(BRASILIA)).isPresent()) {
            return false;
        }

        HistoricoAcionamentoBomba acionamento = new HistoricoAcionamentoBomba();
        acionamento.setLeitura(leitura);
        acionamento.setOrigem(origem);
        acionamento.setDataHoraAcionamento(LocalDateTime.now(BRASILIA));
        acionamento.setTempoIrrigacaoSegundos(duracaoSegundos);
        acionamentoRepository.save(acionamento);
        return true;
    }

    private boolean modoPermiteIrrigacao(String modo, String origem) {
        return switch (origem) {
            case "AUTOMATICO" -> "AUTOMATICO".equalsIgnoreCase(modo);
            case "AGENDADO" -> "MANUAL".equalsIgnoreCase(modo);
            case "MANUAL_FORCADO" -> !"DESLIGADO".equalsIgnoreCase(modo);
            default -> false;
        };
    }

    @Transactional(readOnly = true)
    public synchronized IrrigacaoAtivaResponse consultarAtiva(Long idHorta) {
        Optional<HistoricoAcionamentoBomba> acionamento = obterAtiva(idHorta, LocalDateTime.now(BRASILIA));
        if (acionamento.isEmpty()) {
            return new IrrigacaoAtivaResponse(false, null, 0);
        }

        HistoricoAcionamentoBomba ativa = acionamento.get();
        LocalDateTime terminoPrevisto = ativa.getDataHoraAcionamento()
                .plusSeconds(ativa.getTempoIrrigacaoSegundos());
        long segundosRestantes = Math.max(1, (long) Math.ceil(
                Duration.between(LocalDateTime.now(BRASILIA), terminoPrevisto).toMillis() / 1000.0));

        return new IrrigacaoAtivaResponse(true, ativa.getOrigem(), (int) segundosRestantes);
    }

    @Transactional
    public synchronized boolean pararAtiva(Long idHorta, String origemPermitida) {
        Optional<HistoricoAcionamentoBomba> acionamento =
                obterAtiva(idHorta, LocalDateTime.now(BRASILIA));
        if (acionamento.isEmpty()
                || (origemPermitida != null && !origemPermitida.equals(acionamento.get().getOrigem()))) {
            return false;
        }

        acionamento.get().setDataHoraFinalizacao(LocalDateTime.now(BRASILIA));
        acionamentoRepository.save(acionamento.get());
        return true;
    }

    private Optional<HistoricoAcionamentoBomba> obterAtiva(Long idHorta, LocalDateTime agora) {
        return acionamentoRepository
                .findTopByLeitura_Horta_IdHortaOrderByIdAcionamentoDesc(idHorta)
                .filter(acionamento -> acionamento.getDataHoraFinalizacao() == null)
                .filter(acionamento -> acionamento.getTempoIrrigacaoSegundos() != null)
                .filter(acionamento -> agora.isBefore(acionamento.getDataHoraAcionamento()
                        .plusSeconds(acionamento.getTempoIrrigacaoSegundos())));
    }
}
