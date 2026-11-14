package br.com.hortech.service;

import br.com.hortech.model.*;
import br.com.hortech.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class SensorEmulationService {

    private final HortaRepository hortaRepository;
    private final SensorRepository sensorRepository;
    private final AcionamentoRepository acionamentoRepository;
    private final NotificacaoRepository notificacaoRepository;
    private final Random random = new Random();

    public SensorEmulationService(HortaRepository hortaRepository,
                                  SensorRepository sensorRepository,
                                  AcionamentoRepository acionamentoRepository,
                                  NotificacaoRepository notificacaoRepository) {
        this.hortaRepository = hortaRepository;
        this.sensorRepository = sensorRepository;
        this.acionamentoRepository = acionamentoRepository;
        this.notificacaoRepository = notificacaoRepository;
    }

    @Scheduled(fixedRate = 5000)
    public void processarLeiturasEAutomacao() {
        hortaRepository.findAll().forEach(horta -> {
            int umidade = random.nextInt(40) + 30;
            int temperatura = random.nextInt(15) + 20;
            int luminosidade = random.nextInt(500) + 300;

            HistoricoLeituraSensor leitura = new HistoricoLeituraSensor();
            leitura.setHorta(horta);
            leitura.setUmidade(umidade);
            leitura.setTemperatura(temperatura);
            leitura.setLuminosidade(luminosidade);
            sensorRepository.save(leitura);

            if (temperatura > horta.getTemperaturaLimite()) {
                Notificacao alerta = new Notificacao();
                alerta.setHorta(horta);
                alerta.setAssuntoAlerta("Temperatura elevada: " + temperatura + "C");
                notificacaoRepository.save(alerta);
            }

            if ("AUTOMATICO".equalsIgnoreCase(horta.getModoAutomacao()) && umidade < horta.getUmidadeLimite()) {
                HistoricoAcionamentoBomba acionamento = new HistoricoAcionamentoBomba();
                acionamento.setLeitura(leitura);
                acionamento.setOrigem("AUTOMATICO");
                acionamentoRepository.save(acionamento);
            }
        });
    }
}
