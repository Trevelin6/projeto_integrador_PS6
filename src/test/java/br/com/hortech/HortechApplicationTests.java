package br.com.hortech;

import br.com.hortech.model.HistoricoAcionamentoBomba;
import br.com.hortech.model.HistoricoLeituraSensor;
import br.com.hortech.model.AgendamentoIrrigacao;
import br.com.hortech.model.Horta;
import br.com.hortech.repository.AcionamentoRepository;
import br.com.hortech.repository.AgendamentoIrrigacaoRepository;
import br.com.hortech.repository.HortaRepository;
import br.com.hortech.repository.SensorRepository;
import br.com.hortech.service.SensorEmulationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class HortechApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HortaRepository hortaRepository;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private AcionamentoRepository acionamentoRepository;

    @Autowired
    private AgendamentoIrrigacaoRepository agendamentoRepository;

    @Autowired
    private SensorEmulationService sensorEmulationService;

    @Test
    void contextLoads() {
    }

    @Test
    @Transactional
    void acionamentosRetornamIdsDasLeiturasVinculadas() throws Exception {
        Horta horta = new Horta();
        horta.setNomeHorta("Horta de teste");
        horta.setUmidadeLimite(45);
        horta.setTemperaturaLimite(30);
        horta.setLuminosidadeLimite(600);
        horta.setModoAutomacao("MANUAL");
        horta.setTempoIrrigacaoPadrao(600);
        horta = hortaRepository.save(horta);

        HistoricoLeituraSensor leitura = new HistoricoLeituraSensor();
        leitura.setHorta(horta);
        leitura.setUmidade(40);
        leitura.setTemperatura(24);
        leitura.setLuminosidade(500);
        leitura = sensorRepository.save(leitura);

        HistoricoAcionamentoBomba acionamento = new HistoricoAcionamentoBomba();
        acionamento.setLeitura(leitura);
        acionamento.setOrigem("MANUAL_FORCADO");
        acionamentoRepository.save(acionamento);

        mockMvc.perform(get("/api/horta/{id}/acionamentos", horta.getIdHorta()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idLeitura").value(leitura.getIdLeitura()))
                .andExpect(jsonPath("$[0].origem").value("MANUAL_FORCADO"))
                .andExpect(jsonPath("$[0].dataHoraAcionamento").isNotEmpty());
    }

    @Test
    @Transactional
    void configuracaoEAgendamentosDiariosPodemSerAtualizados() throws Exception {
        Horta horta = new Horta();
        horta.setNomeHorta("Horta de configuração");
        horta.setUmidadeLimite(45);
        horta.setTemperaturaLimite(30);
        horta.setLuminosidadeLimite(600);
        horta.setModoAutomacao("AUTOMATICO");
        horta.setTempoIrrigacaoPadrao(600);
        horta = hortaRepository.save(horta);
        Long idHorta = horta.getIdHorta();

        mockMvc.perform(put("/api/horta/{id}/configuracao", idHorta)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "umidadeLimite": 38,
                                  "luminosidadeLimite": 700,
                                  "temperaturaLimite": 32,
                                  "tempoIrrigacaoPadrao": 180,
                                  "modoAutomacao": "MANUAL"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.umidadeLimite").value(38))
                .andExpect(jsonPath("$.tempoIrrigacaoPadrao").value(180))
                .andExpect(jsonPath("$.modoAutomacao").value("MANUAL"));

        String createdSchedule = mockMvc.perform(post("/api/horta/{id}/agendamentos", idHorta)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "horarioExecucao": "06:30:00",
                                  "tempoIrrigacaoSegundos": 90
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tempoIrrigacaoSegundos").value(90))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long idAgendamento = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(createdSchedule)
                .get("idAgendamento")
                .asLong();

        mockMvc.perform(get("/api/horta/{id}/agendamentos", idHorta))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].horarioExecucao").value("06:30:00"));

        mockMvc.perform(delete("/api/horta/{id}/agendamentos/{idAgendamento}", idHorta, idAgendamento))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    @Transactional
    void agendamentoNoModoManualRegistraAcionamentoNaLeituraMaisRecente() {
        Horta horta = new Horta();
        horta.setNomeHorta("Horta agendada");
        horta.setUmidadeLimite(45);
        horta.setTemperaturaLimite(30);
        horta.setLuminosidadeLimite(600);
        horta.setModoAutomacao("MANUAL");
        horta.setTempoIrrigacaoPadrao(600);
        horta = hortaRepository.save(horta);

        HistoricoLeituraSensor leitura = new HistoricoLeituraSensor();
        leitura.setHorta(horta);
        leitura.setUmidade(60);
        leitura.setTemperatura(24);
        leitura.setLuminosidade(500);
        leitura = sensorRepository.save(leitura);

        AgendamentoIrrigacao agendamento = new AgendamentoIrrigacao();
        agendamento.setHorta(horta);
        LocalTime horarioAgendado = LocalTime.now(ZoneId.of("America/Sao_Paulo")).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime instanteAgendado = LocalDate.now(ZoneId.of("America/Sao_Paulo")).atTime(horarioAgendado);
        agendamento.setHorarioExecucao(horarioAgendado);
        agendamento.setTempoIrrigacaoSegundos(90);
        agendamentoRepository.save(agendamento);

        sensorEmulationService.executarAgendamentosDiarios();

        List<HistoricoAcionamentoBomba> acionamentos =
                acionamentoRepository.findByLeitura_IdLeituraIn(List.of(leitura.getIdLeitura()));
        org.junit.jupiter.api.Assertions.assertTrue(
                acionamentos.stream().anyMatch(acionamento -> "AGENDADO".equals(acionamento.getOrigem())));
        org.junit.jupiter.api.Assertions.assertFalse(
                acionamentos.stream()
                        .filter(acionamento -> "AGENDADO".equals(acionamento.getOrigem()))
                        .anyMatch(acionamento -> acionamento.getDataHoraAcionamento().isBefore(instanteAgendado)));
    }

    @Test
    @Transactional
    void modoDesligadoBloqueiaIrrigacaoForcadaELeiturasSimuladas() throws Exception {
        Horta horta = new Horta();
        horta.setNomeHorta("Horta desligada");
        horta.setUmidadeLimite(45);
        horta.setTemperaturaLimite(30);
        horta.setLuminosidadeLimite(600);
        horta.setModoAutomacao("DESLIGADO");
        horta.setTempoIrrigacaoPadrao(600);
        horta = hortaRepository.save(horta);

        mockMvc.perform(post("/api/horta/{id}/irrigacao-forcada", horta.getIdHorta()))
                .andExpect(status().isConflict());

        sensorEmulationService.processarLeiturasEAutomacao();

        org.junit.jupiter.api.Assertions.assertEquals(0, sensorRepository.countByHortaIdHorta(horta.getIdHorta()));
    }

    @Test
    @Transactional
    void irrigacaoForcadaNaoSobrepoeOutraEPermiteParada() throws Exception {
        Horta horta = new Horta();
        horta.setNomeHorta("Horta com irrigação ativa");
        horta.setUmidadeLimite(45);
        horta.setTemperaturaLimite(30);
        horta.setLuminosidadeLimite(600);
        horta.setModoAutomacao("MANUAL");
        horta.setTempoIrrigacaoPadrao(600);
        horta = hortaRepository.save(horta);

        HistoricoLeituraSensor leitura = new HistoricoLeituraSensor();
        leitura.setHorta(horta);
        leitura.setUmidade(40);
        leitura.setTemperatura(24);
        leitura.setLuminosidade(500);
        leitura = sensorRepository.save(leitura);

        HistoricoAcionamentoBomba agendamentoAtivo = new HistoricoAcionamentoBomba();
        agendamentoAtivo.setLeitura(leitura);
        agendamentoAtivo.setOrigem("AGENDADO");
        agendamentoAtivo.setTempoIrrigacaoSegundos(600);
        agendamentoAtivo.setDataHoraAcionamento(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        acionamentoRepository.save(agendamentoAtivo);

        mockMvc.perform(get("/api/horta/{id}/irrigacao-ativa", horta.getIdHorta()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativa").value(true))
                .andExpect(jsonPath("$.origem").value("AGENDADO"))
                .andExpect(jsonPath("$.segundosRestantes").isNumber());

        mockMvc.perform(post("/api/horta/{id}/irrigacao-forcada", horta.getIdHorta()))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/horta/{id}/irrigacao-ativa/parar", horta.getIdHorta()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/horta/{id}/irrigacao-ativa", horta.getIdHorta()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativa").value(false));

        mockMvc.perform(put("/api/horta/{id}/configuracao", horta.getIdHorta())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "umidadeLimite": 45,
                                  "luminosidadeLimite": 600,
                                  "temperaturaLimite": 30,
                                  "tempoIrrigacaoPadrao": 600,
                                  "modoAutomacao": "DESLIGADO"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/horta/{id}/irrigacao-ativa", horta.getIdHorta()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativa").value(false));
    }

    @Test
    @Transactional
    void irrigacaoForcadaPodeSerEncerradaAntesDoTempoPadrao() throws Exception {
        Horta horta = new Horta();
        horta.setNomeHorta("Horta com irrigação forçada");
        horta.setUmidadeLimite(45);
        horta.setTemperaturaLimite(30);
        horta.setLuminosidadeLimite(600);
        horta.setModoAutomacao("MANUAL");
        horta.setTempoIrrigacaoPadrao(600);
        horta = hortaRepository.save(horta);

        HistoricoLeituraSensor leitura = new HistoricoLeituraSensor();
        leitura.setHorta(horta);
        leitura.setUmidade(40);
        leitura.setTemperatura(24);
        leitura.setLuminosidade(500);
        sensorRepository.save(leitura);

        mockMvc.perform(post("/api/horta/{id}/irrigacao-forcada", horta.getIdHorta()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/horta/{id}/irrigacao-ativa/parar", horta.getIdHorta()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/horta/{id}/irrigacao-ativa", horta.getIdHorta()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativa").value(false));
    }

    @Test
    @Transactional
    void novaLeituraAbaixoDoLimiteDisparaIrrigacaoAutomatica() throws Exception {
        Horta horta = new Horta();
        horta.setNomeHorta("Horta telemetria");
        horta.setUmidadeLimite(45);
        horta.setTemperaturaLimite(30);
        horta.setLuminosidadeLimite(600);
        horta.setModoAutomacao("AUTOMATICO");
        horta.setTempoIrrigacaoPadrao(120);
        horta = hortaRepository.save(horta);
        Long idHorta = horta.getIdHorta();

        String response = mockMvc.perform(post("/api/horta/{id}/leituras", idHorta)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "umidade": 20,
                                  "temperatura": 27,
                                  "luminosidade": 550
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.umidade").value(20))
                .andExpect(jsonPath("$.temperatura").value(27))
                .andExpect(jsonPath("$.luminosidade").value(550))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long idLeitura = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response)
                .get("idLeitura")
                .asLong();

        List<HistoricoAcionamentoBomba> acionamentos =
                acionamentoRepository.findByLeitura_IdLeituraIn(List.of(idLeitura));

        org.junit.jupiter.api.Assertions.assertEquals(1, acionamentos.size());
        org.junit.jupiter.api.Assertions.assertEquals("AUTOMATICO", acionamentos.get(0).getOrigem());
    }
}
