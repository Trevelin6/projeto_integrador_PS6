package br.com.hortech.service;

import br.com.hortech.model.*;
import br.com.hortech.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
public class SensorEmulationService {

    private final HortaRepository hortaRepository;
    private final SensorRepository sensorRepository;
    private final NotificacaoRepository notificacaoRepository;
    private final AgendamentoIrrigacaoRepository agendamentoRepository;
    private final IrrigacaoControlService irrigacaoControlService;
    private final Random random = new Random();

    public SensorEmulationService(HortaRepository hortaRepository,
                                  SensorRepository sensorRepository,
                                  NotificacaoRepository notificacaoRepository,
                                  AgendamentoIrrigacaoRepository agendamentoRepository,
                                  IrrigacaoControlService irrigacaoControlService) {
        this.hortaRepository = hortaRepository;
        this.sensorRepository = sensorRepository;
        this.notificacaoRepository = notificacaoRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.irrigacaoControlService = irrigacaoControlService;
    }

    @Scheduled(fixedRate = 60000)
    public void processarLeiturasEAutomacao() {
        hortaRepository.findAll().forEach(horta -> {
            if ("DESLIGADO".equalsIgnoreCase(horta.getModoAutomacao())) {
                return;
            }

            int umidade = random.nextInt(40) + 30;
            int temperatura = random.nextInt(15) + 20;
            int luminosidade = random.nextInt(500) + 300;

            HistoricoLeituraSensor leitura = new HistoricoLeituraSensor();
            leitura.setHorta(horta);
            leitura.setDataHoraLeitura(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
            leitura.setUmidade(umidade);
            leitura.setTemperatura(temperatura);
            leitura.setLuminosidade(luminosidade);
            sensorRepository.save(leitura);

            boolean temperaturaElevada = temperatura > horta.getTemperaturaLimite();
            boolean umidadeBaixa = umidade < horta.getUmidadeLimite();

            if (temperaturaElevada) {
                Notificacao alerta = new Notificacao();
                alerta.setHorta(horta);
                alerta.setAssuntoAlerta("Alerta: Temperatura elevada (" + temperatura + "°C)");
                notificacaoRepository.save(alerta);
            }

            if (umidadeBaixa) {
                Notificacao alerta = new Notificacao();
                alerta.setHorta(horta);
                alerta.setAssuntoAlerta("Alerta: Umidade do solo baixa (" + umidade + "%) - Irrigação solicitada");
                notificacaoRepository.save(alerta);
            }

            if ("AUTOMATICO".equalsIgnoreCase(horta.getModoAutomacao())
                    && (umidadeBaixa || temperaturaElevada)) {
                irrigacaoControlService.iniciar(horta, leitura, "AUTOMATICO",
                        horta.getTempoIrrigacaoPadrao());
            }

            if (luminosidade > horta.getLuminosidadeLimite()) {
                Notificacao alerta = new Notificacao();
                alerta.setHorta(horta);
                alerta.setAssuntoAlerta("Alerta: Alta incidencia solar (" + luminosidade + " Lux)");
                notificacaoRepository.save(alerta);
            }
        });
    }

    @Scheduled(cron = "1 * * * * *", zone = "America/Sao_Paulo")
    public void executarAgendamentosDiarios() {
        LocalTime horarioAtual = LocalTime.now(ZoneId.of("America/Sao_Paulo")).truncatedTo(ChronoUnit.MINUTES);
        List<AgendamentoIrrigacao> agendamentos = agendamentoRepository.findByHorarioExecucao(horarioAtual);

        for (AgendamentoIrrigacao agendamento : agendamentos) {
            Horta horta = agendamento.getHorta();
            if (!"MANUAL".equalsIgnoreCase(horta.getModoAutomacao())) {
                continue;
            }

            List<HistoricoLeituraSensor> leituras =
                    sensorRepository.findTop10ByHortaIdHortaOrderByIdLeituraDesc(horta.getIdHorta());
            if (leituras.isEmpty()) {
                continue;
            }

            boolean iniciou = irrigacaoControlService.iniciar(horta, leituras.get(0), "AGENDADO",
                    agendamento.getTempoIrrigacaoSegundos());
            if (!iniciou) {
                Notificacao alerta = new Notificacao();
                alerta.setHorta(horta);
                alerta.setAssuntoAlerta("Agendamento ignorado: outra irrigação estava em andamento.");
                notificacaoRepository.save(alerta);
            }
        }
    }
}
