package br.com.hortech.repository;

import br.com.hortech.model.HistoricoLeituraSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SensorRepository extends JpaRepository<HistoricoLeituraSensor, Long> {
    List<HistoricoLeituraSensor> findTop10ByHortaIdHortaOrderByIdLeituraDesc(Long idHorta);
}
