package br.com.hortech.model;

import jakarta.persistence.*;

@Entity
@Table(name = "relacionamento_usuario_horta")
public class RelacionamentoUsuarioHorta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_relacionamento")
    private Long idRelacionamento;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_horta", nullable = false)
    private Horta horta;

    @Column(nullable = false, length = 20)
    private String papel;

    public RelacionamentoUsuarioHorta() {}

    public Long getIdRelacionamento() { return idRelacionamento; }
    public void setIdRelacionamento(Long idRelacionamento) { this.idRelacionamento = idRelacionamento; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Horta getHorta() { return horta; }
    public void setHorta(Horta horta) { this.horta = horta; }

    public String getPapel() { return papel; }
    public void setPapel(String papel) { this.papel = papel; }
}
