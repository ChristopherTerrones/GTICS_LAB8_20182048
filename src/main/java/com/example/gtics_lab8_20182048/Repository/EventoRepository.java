package com.example.gtics_lab8_20182048.Repository;

import com.example.gtics_lab8_20182048.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Integer> {
    List<Evento> findAllByOrderByFechaAsc();
    List<Evento> findByFechaGreaterThanEqualOrderByFechaAsc(LocalDate fecha);

}
