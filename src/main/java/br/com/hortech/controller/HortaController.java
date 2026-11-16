package br.com.hortech.controller;

import br.com.hortech.dto.AcionamentoResumo;
import br.com.hortech.dto.AgendamentoIrrigacaoRequest;
import br.com.hortech.dto.ConfiguracaoHortaRequest;
import br.com.hortech.dto.IrrigacaoAtivaResponse;
import br.com.hortech.dto.LeituraSensorRequest;
import br.com.hortech.model.*;
import br.com.hortech.repository.*;
import br.com.hortech.service.IrrigacaoControlService;
import jakarta.validation.Valid;
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
    private final AgendamentoIrrigacaoRepository agendamentoRepository;
    private final IrrigacaoControlService irrigacaoControlService;

    public HortaController(HortaRepository hortaRepository,
                           SensorRepository sensorRepository,
                           AcionamentoRepository acionamentoRepository,
                           NotificacaoRepository notificacaoRepository,
                           AgendamentoIrrigacaoRepository agendamentoRepository,
                           IrrigacaoControlService irrigacaoControlService) {
        this.hortaRepository = hortaRepository;
        this.sensorRepository = sensorRepository;
        this.acionamentoRepository = acionamentoRepository;
        this.notificacaoRepository = notificacaoRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.irrigacaoControlService = irrigacaoControlService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Horta> buscarPorId(@PathVariable Long id) {
        return hortaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/configuracao")
    public ResponseEntity<Horta> atualizarConfiguracao(
            @PathVariable Long id,
            @Valid @RequestBody ConfiguracaoHortaRequest request) {
        return hortaRepository.findById(id).map(horta -> {
            horta.setUmidadeLimite(request.umidadeLimite());
            horta.setLuminosidadeLimite(request.luminosidadeLimite());
            horta.setTemperaturaLimite(request.temperaturaLimite());
            horta.setTempoIrrigacaoPadrao(request.tempoIrrigacaoPadrao());
            horta.setModoAutomacao(request.modoAutomacao());
            Horta salva = hortaRepository.save(horta);
            if ("DESLIGADO".equalsIgnoreCase(salva.getModoAutomacao())) {
                irrigacaoControlService.pararAtiva(id, null);
            }
            return ResponseEntity.ok(salva);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/leituras")
    public List<HistoricoLeituraSensor> leituras(@PathVariable Long id) {
        return sensorRepository.findTop10ByHortaIdHortaOrderByIdLeituraDesc(id);
    }

    @GetMapping("/{id}/acionamentos")
    public List<AcionamentoResumo> acionamentos(@PathVariable Long id) {
        List<Long> idsLeitura = sensorRepository.findTop10ByHortaIdHortaOrderByIdLeituraDesc(id).stream()
                .map(HistoricoLeituraSensor::getIdLeitura)
                .toList();

        return acionamentoRepository.findByLeitura_IdLeituraIn(idsLeitura).stream()
                .map(acionamento -> new AcionamentoResumo(
                        acionamento.getLeitura().getIdLeitura(),
                        acionamento.getOrigem(),
                        acionamento.getDataHoraAcionamento()))
                .toList();
    }

    @GetMapping("/{id}/notificacoes")
    public List<Notificacao> notificacoes(@PathVariable Long id) {
        return notificacaoRepository.findByHortaIdHorta(id);
    }

    @GetMapping("/{id}/agendamentos")
    public ResponseEntity<List<AgendamentoIrrigacao>> agendamentos(@PathVariable Long id) {
        if (!hortaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(agendamentoRepository.findByHortaIdHortaOrderByHorarioExecucaoAsc(id));
    }

    @GetMapping("/{id}/irrigacao-ativa")
    public ResponseEntity<IrrigacaoAtivaResponse> consultarIrrigacaoAtiva(@PathVariable Long id) {
        if (!hortaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(irrigacaoControlService.consultarAtiva(id));
    }

    @PostMapping("/{id}/agendamentos")
    public ResponseEntity<AgendamentoIrrigacao> criarAgendamento(
            @PathVariable Long id,
            @Valid @RequestBody AgendamentoIrrigacaoRequest request) {
        return hortaRepository.findById(id).map(horta -> {
            AgendamentoIrrigacao agendamento = new AgendamentoIrrigacao();
            agendamento.setHorta(horta);
            agendamento.setHorarioExecucao(request.horarioExecucao());
            agendamento.setTempoIrrigacaoSegundos(request.tempoIrrigacaoSegundos());
            return ResponseEntity.status(201).body(agendamentoRepository.save(agendamento));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/agendamentos/{idAgendamento}")
    public ResponseEntity<Void> excluirAgendamento(
            @PathVariable Long id,
            @PathVariable Long idAgendamento) {
        return agendamentoRepository.findById(idAgendamento)
                .filter(agendamento -> agendamento.getHorta().getIdHorta().equals(id))
                .map(agendamento -> {
                    agendamentoRepository.delete(agendamento);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/irrigacao-forcada")
    public ResponseEntity<String> acionarIrrigacaoForcada(@PathVariable Long id) {
        Horta horta = hortaRepository.findById(id).orElseThrow();
        if ("DESLIGADO".equalsIgnoreCase(horta.getModoAutomacao())) {
            return ResponseEntity.status(409).body("Irrigação indisponível: automação desligada.");
        }

        List<HistoricoLeituraSensor> ultimas = sensorRepository.findTop10ByHortaIdHortaOrderByIdLeituraDesc(id);

        if (ultimas.isEmpty()) {
            return ResponseEntity.status(409).body("Irrigação indisponível: ainda não há leituras de sensores.");
        }

        if (!irrigacaoControlService.iniciar(horta, ultimas.get(0), "MANUAL_FORCADO",
                horta.getTempoIrrigacaoPadrao())) {
            return ResponseEntity.status(409).body("Irrigação indisponível: outra irrigação está em andamento.");
        }
        return ResponseEntity.ok("Irrigacao forcada ativada com sucesso por " + horta.getTempoIrrigacaoPadrao() + " segundos.");
    }

    @PostMapping("/{id}/irrigacao-ativa/parar")
    public ResponseEntity<String> pararIrrigacaoAtiva(@PathVariable Long id) {
        if (!hortaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (!irrigacaoControlService.pararAtiva(id, null)) {
            return ResponseEntity.status(409).body("Não há irrigação ativa para parar.");
        }
        return ResponseEntity.ok("Irrigação parada.");
    }

    @PostMapping("/{id}/leituras")
    public ResponseEntity<HistoricoLeituraSensor> registrarLeitura(
            @PathVariable Long id,
            @Valid @RequestBody LeituraSensorRequest request) {
        return hortaRepository.findById(id).map(horta -> {
            HistoricoLeituraSensor leitura = new HistoricoLeituraSensor();
            leitura.setHorta(horta);
            leitura.setUmidade(request.umidade());
            leitura.setTemperatura(request.temperatura());
            leitura.setLuminosidade(request.luminosidade());
            HistoricoLeituraSensor salva = sensorRepository.save(leitura);

            // Dispara a automação se o modo estiver ativo e a umidade estiver abaixo do limite
            if ("AUTOMATICO".equalsIgnoreCase(horta.getModoAutomacao())
                    && salva.getUmidade() < horta.getUmidadeLimite()) {
                irrigacaoControlService.iniciar(horta, salva, "AUTOMATICO", horta.getTempoIrrigacaoPadrao());
            }

            return ResponseEntity.status(201).body(salva);
        }).orElse(ResponseEntity.notFound().build());
    }
}
