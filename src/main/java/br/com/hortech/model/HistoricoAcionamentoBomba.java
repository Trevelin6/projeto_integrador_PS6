package br.com.hortech.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_acionamento_bomba")
public class HistoricoAcionamentoBomba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acionamento")
    private Long idAcionamento;

    @ManyToOne
    @JoinColumn(name = "id_leitura", nullable = false)
    private HistoricoLeituraSensor leitura;

    @Column(name = "data_hora_acionamento", nullable = false)
    private LocalDateTime dataHoraAcionamento = LocalDateTime.now();

    @Column(nullable = false, length = 20)
    private String origem;

    public HistoricoAcionamentoBomba() {}

    public HistoricoAcionamentoBomba(Long idAcionamento, HistoricoLeituraSensor leitura, LocalDateTime dataHoraAcionamento, String origem) {
        this.idAcionamento = idAcionamento;
        this.leitura = leitura;
        this.dataHoraAcionamento = dataHoraAcionamento;
        this.origem = origem;
    }

    public Long getIdAcionamento() { return idAcionamento; }
    public void setIdAcionamento(Long idAcionamento) { this.idAcionamento = idAcionamento; }

    public HistoricoLeituraSensor getLeitura() { return leitura; }
    public void setLeitura(HistoricoLeituraSensor leitura) { this.leitura = leitura; }

    public LocalDateTime getDataHoraAcionamento() { return dataHoraAcionamento; }
    public void setDataHoraAcionamento(LocalDateTime dataHoraAcionamento) { this.dataHoraAcionamento = dataHoraAcionamento; }

    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }
}
