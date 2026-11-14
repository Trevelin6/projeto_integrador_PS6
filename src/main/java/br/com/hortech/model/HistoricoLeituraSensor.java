package br.com.hortech.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_leitura_sensores")
public class HistoricoLeituraSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_leitura")
    private Long idLeitura;

    @ManyToOne
    @JoinColumn(name = "id_horta", nullable = false)
    private Horta horta;

    @Column(name = "data_hora_leitura", nullable = false)
    private LocalDateTime dataHoraLeitura = LocalDateTime.now();

    @Column(nullable = false)
    private Integer umidade;

    @Column(nullable = false)
    private Integer temperatura;

    @Column(nullable = false)
    private Integer luminosidade;

    public HistoricoLeituraSensor() {}

    public HistoricoLeituraSensor(Long idLeitura, Horta horta, LocalDateTime dataHoraLeitura, Integer umidade, Integer temperatura, Integer luminosidade) {
        this.idLeitura = idLeitura;
        this.horta = horta;
        this.dataHoraLeitura = dataHoraLeitura;
        this.umidade = umidade;
        this.temperatura = temperatura;
        this.luminosidade = luminosidade;
    }

    public Long getIdLeitura() { return idLeitura; }
    public void setIdLeitura(Long idLeitura) { this.idLeitura = idLeitura; }

    public Horta getHorta() { return horta; }
    public void setHorta(Horta horta) { this.horta = horta; }

    public LocalDateTime getDataHoraLeitura() { return dataHoraLeitura; }
    public void setDataHoraLeitura(LocalDateTime dataHoraLeitura) { this.dataHoraLeitura = dataHoraLeitura; }

    public Integer getUmidade() { return umidade; }
    public void setUmidade(Integer umidade) { this.umidade = umidade; }

    public Integer getTemperatura() { return temperatura; }
    public void setTemperatura(Integer temperatura) { this.temperatura = temperatura; }

    public Integer getLuminosidade() { return luminosidade; }
    public void setLuminosidade(Integer luminosidade) { this.luminosidade = luminosidade; }
}
