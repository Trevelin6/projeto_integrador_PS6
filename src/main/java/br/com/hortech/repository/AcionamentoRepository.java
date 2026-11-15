package br.com.hortech.repository;

import br.com.hortech.model.HistoricoAcionamentoBomba;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AcionamentoRepository extends JpaRepository<HistoricoAcionamentoBomba, Long> {
    List<HistoricoAcionamentoBomba> findTop10ByOrderByIdAcionamentoDesc();
    List<HistoricoAcionamentoBomba> findByLeitura_IdLeituraIn(List<Long> idsLeitura);
    Optional<HistoricoAcionamentoBomba> findTopByLeitura_Horta_IdHortaOrderByIdAcionamentoDesc(Long idHorta);
}
