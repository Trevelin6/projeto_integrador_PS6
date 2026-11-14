package br.com.hortech.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agendamento_irrigacoes")
public class AgendamentoIrrigacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agendamento")
    private Long idAgendamento;

    @ManyToOne
    @JoinColumn(name = "id_horta", nullable = false)
    private Horta horta;

    @Column(name = "horario_execucao", nullable = false)
    private LocalDateTime horarioExecucao;

    @Column(name = "tempo_irrigacao_segundos", nullable = false)
    private Integer tempoIrrigacaoSegundos;

    public AgendamentoIrrigacao() {}

    public AgendamentoIrrigacao(Long idAgendamento, Horta horta, LocalDateTime horarioExecucao, Integer tempoIrrigacaoSegundos) {
        this.idAgendamento = idAgendamento;
        this.horta = horta;
        this.horarioExecucao = horarioExecucao;
        this.tempoIrrigacaoSegundos = tempoIrrigacaoSegundos;
    }

    public Long getIdAgendamento() { return idAgendamento; }
    public void setIdAgendamento(Long idAgendamento) { this.idAgendamento = idAgendamento; }

    public Horta getHorta() { return horta; }
    public void setHorta(Horta horta) { this.horta = horta; }

    public LocalDateTime getHorarioExecucao() { return horarioExecucao; }
    public void setHorarioExecucao(LocalDateTime horarioExecucao) { this.horarioExecucao = horarioExecucao; }

    public Integer getTempoIrrigacaoSegundos() { return tempoIrrigacaoSegundos; }
    public void setTempoIrrigacaoSegundos(Integer tempoIrrigacaoSegundos) { this.tempoIrrigacaoSegundos = tempoIrrigacaoSegundos; }
}
