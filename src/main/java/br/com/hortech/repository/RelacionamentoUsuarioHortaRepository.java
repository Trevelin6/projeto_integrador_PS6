package br.com.hortech.repository;

import br.com.hortech.model.RelacionamentoUsuarioHorta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RelacionamentoUsuarioHortaRepository extends JpaRepository<RelacionamentoUsuarioHorta, Long> {
    List<RelacionamentoUsuarioHorta> findByUsuarioIdUsuario(Long idUsuario);
}
