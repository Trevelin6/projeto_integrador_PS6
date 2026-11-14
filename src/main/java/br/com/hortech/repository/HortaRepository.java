package br.com.hortech.repository;

import br.com.hortech.model.Horta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HortaRepository extends JpaRepository<Horta, Long> {}
