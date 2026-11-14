package br.com.hortech.repository;

import br.com.hortech.model.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByHortaIdHortaAndNotificacaoVisualizadaFalse(Long idHorta);
}
