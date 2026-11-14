package br.com.hortech.repository;

import br.com.hortech.model.HistoricoAcionamentoBomba;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AcionamentoRepository extends JpaRepository<HistoricoAcionamentoBomba, Long> {
    List<HistoricoAcionamentoBomba> findTop10ByOrderByIdAcionamentoDesc();
}
