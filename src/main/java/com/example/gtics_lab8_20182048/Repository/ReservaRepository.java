package com.example.gtics_lab8_20182048.Repository;

import com.example.gtics_lab8_20182048.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
}
