package br.com.hortech.repository;

import br.com.hortech.model.HistoricoAcionamentoBomba;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcionamentoRepository extends JpaRepository<HistoricoAcionamentoBomba, Long> {}
