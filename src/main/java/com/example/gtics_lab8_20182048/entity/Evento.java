package com.example.gtics_lab8_20182048.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "eventos")
@Getter
@Setter
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idevento")
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria")
    private Categoria categoria;

    @Column(name = "capacidadmaxima", nullable = false)
    private Integer capacidadMaxima;

    @Column(name = "reservasactuales", nullable = false)
    private Integer reservasActuales = 0;

    public enum Categoria {
        Conferencia, Exposicion, Taller, Concierto
    }
}

