package br.com.hortech.model;

import jakarta.persistence.*;

@Entity
@Table(name = "horta")
public class Horta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horta")
    private Long idHorta;

    @Column(name = "nome_horta", nullable = false, length = 100)
    private String nomeHorta;

    @Column(name = "umidade_limite", nullable = false)
    private Integer umidadeLimite;

    @Column(name = "temperatura_limite", nullable = false)
    private Integer temperaturaLimite;

    @Column(name = "luminosidade_limite", nullable = false)
    private Integer luminosidadeLimite;

    @Column(name = "modo_automacao", nullable = false, length = 50)
    private String modoAutomacao;

    @Column(name = "tempo_irrigacao_padrao", nullable = false)
    private Integer tempoIrrigacaoPadrao;

    public Horta() {}

    public Horta(Long idHorta, String nomeHorta, Integer umidadeLimite, Integer temperaturaLimite, Integer luminosidadeLimite, String modoAutomacao, Integer tempoIrrigacaoPadrao) {
        this.idHorta = idHorta;
        this.nomeHorta = nomeHorta;
        this.umidadeLimite = umidadeLimite;
        this.temperaturaLimite = temperaturaLimite;
        this.luminosidadeLimite = luminosidadeLimite;
        this.modoAutomacao = modoAutomacao;
        this.tempoIrrigacaoPadrao = tempoIrrigacaoPadrao;
    }

    public Long getIdHorta() { return idHorta; }
    public void setIdHorta(Long idHorta) { this.idHorta = idHorta; }

    public String getNomeHorta() { return nomeHorta; }
    public void setNomeHorta(String nomeHorta) { this.nomeHorta = nomeHorta; }

    public Integer getUmidadeLimite() { return umidadeLimite; }
    public void setUmidadeLimite(Integer umidadeLimite) { this.umidadeLimite = umidadeLimite; }

    public Integer getTemperaturaLimite() { return temperaturaLimite; }
    public void setTemperaturaLimite(Integer temperaturaLimite) { this.temperaturaLimite = temperaturaLimite; }

    public Integer getLuminosidadeLimite() { return luminosidadeLimite; }
    public void setLuminosidadeLimite(Integer luminosidadeLimite) { this.luminosidadeLimite = luminosidadeLimite; }

    public String getModoAutomacao() { return modoAutomacao; }
    public void setModoAutomacao(String modoAutomacao) { this.modoAutomacao = modoAutomacao; }

    public Integer getTempoIrrigacaoPadrao() { return tempoIrrigacaoPadrao; }
    public void setTempoIrrigacaoPadrao(Integer tempoIrrigacaoPadrao) { this.tempoIrrigacaoPadrao = tempoIrrigacaoPadrao; }
}
