package br.com.hortech.repository;

import br.com.hortech.model.AgendamentoIrrigacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgendamentoIrrigacaoRepository extends JpaRepository<AgendamentoIrrigacao, Long> {
    List<AgendamentoIrrigacao> findByHortaIdHortaOrderByHorarioExecucaoAsc(Long idHorta);
    List<AgendamentoIrrigacao> findByHorarioExecucao(java.time.LocalTime horarioExecucao);
}
