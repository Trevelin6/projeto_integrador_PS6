package br.com.hortech.controller;

import br.com.hortech.model.*;
import br.com.hortech.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/horta")
@CrossOrigin(origins = "*")
public class HortaController {

    private final HortaRepository hortaRepository;
    private final SensorRepository sensorRepository;
    private final AcionamentoRepository acionamentoRepository;
    private final NotificacaoRepository notificacaoRepository;

    public HortaController(HortaRepository hortaRepository,
                           SensorRepository sensorRepository,
                           AcionamentoRepository acionamentoRepository,
                           NotificacaoRepository notificacaoRepository) {
        this.hortaRepository = hortaRepository;
        this.sensorRepository = sensorRepository;
        this.acionamentoRepository = acionamentoRepository;
        this.notificacaoRepository = notificacaoRepository;
    }

    @GetMapping("/{id}/leituras")
    public List<HistoricoLeituraSensor> leituras(@PathVariable Long id) {
        return sensorRepository.findTop10ByHortaIdHortaOrderByIdLeituraDesc(id);
    }

    @GetMapping("/{id}/notificacoes")
    public List<Notificacao> notificacoesNaoLidas(@PathVariable Long id) {
        return notificacaoRepository.findByHortaIdHortaAndNotificacaoVisualizadaFalse(id);
    }

    @PostMapping("/{id}/irrigacao-forcada")
    public ResponseEntity<String> acionarIrrigacaoForcada(@PathVariable Long id) {
        Horta horta = hortaRepository.findById(id).orElseThrow();
        List<HistoricoLeituraSensor> ultimas = sensorRepository.findTop10ByHortaIdHortaOrderByIdLeituraDesc(id);

        if (!ultimas.isEmpty()) {
            HistoricoAcionamentoBomba acionamento = new HistoricoAcionamentoBomba();
            acionamento.setLeitura(ultimas.get(0));
            acionamento.setOrigem("MANUAL_FORCADO");
            acionamentoRepository.save(acionamento);
        }

        return ResponseEntity.ok("Irrigacao forcada ativada por " + horta.getTempoIrrigacaoPadrao() + " segundos.");
    }
}
