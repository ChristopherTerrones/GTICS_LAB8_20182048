package com.example.gtics_lab8_20182048.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reservas")
@Getter
@Setter
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idreservas")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idevento", nullable = false)
    private Evento evento;

    @Column(name = "nombre", length = 45)
    private String nombre;

    @Column(name = "correo", length = 45)
    private String correo;

    @Column(name = "cupos", length = 45)
    private Integer cupos;
}

