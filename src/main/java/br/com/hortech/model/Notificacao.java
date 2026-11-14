package br.com.hortech.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacao")
public class Notificacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacao")
    private Long idNotificacao;

    @ManyToOne
    @JoinColumn(name = "id_horta", nullable = false)
    private Horta horta;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    @Column(name = "assunto_alerta", nullable = false, length = 150)
    private String assuntoAlerta;

    @ManyToOne
    @JoinColumn(name = "visualizada_por")
    private Usuario visualizadaPor;

    public Notificacao() {}

    public Long getIdNotificacao() { return idNotificacao; }
    public void setIdNotificacao(Long idNotificacao) { this.idNotificacao = idNotificacao; }

    public Horta getHorta() { return horta; }
    public void setHorta(Horta horta) { this.horta = horta; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getAssuntoAlerta() { return assuntoAlerta; }
    public void setAssuntoAlerta(String assuntoAlerta) { this.assuntoAlerta = assuntoAlerta; }

    public Usuario getVisualizadaPor() { return visualizadaPor; }
    public void setVisualizadaPor(Usuario visualizadaPor) { this.visualizadaPor = visualizadaPor; }
}
