package com.example.gtics_lab8_20182048.controller;

import com.example.gtics_lab8_20182048.Repository.EventoRepository;
import com.example.gtics_lab8_20182048.Repository.ReservaRepository;
import com.example.gtics_lab8_20182048.entity.Evento;
import com.example.gtics_lab8_20182048.entity.Reserva;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final EventoRepository eventoRepository;
    private final ReservaRepository reservaRepository;

    public EventoController(EventoRepository eventoRepository, ReservaRepository reservaRepository) {
        this.eventoRepository = eventoRepository;
        this.reservaRepository = reservaRepository;
    }

    @GetMapping("/listar")
    public ResponseEntity<HashMap<String, Object>> listarEventos(@RequestParam(value = "fecha",required = false) String fechaStr) {
        HashMap<String, Object> respuesta = new HashMap<>();
        if(fechaStr==null){
            respuesta.put("result","error");
            respuesta.put("msg","Ingrese una fecha");
            return ResponseEntity.badRequest().body(respuesta);
        }else {
            try{
                LocalDate fecha = LocalDate.parse(fechaStr);
                List<Evento> eventos;
                eventos = eventoRepository.findByFecha(fecha);
                if (!eventos.isEmpty()) {
                    respuesta.put("result", "ok");
                    respuesta.put("eventos", eventos);
                } else {
                    respuesta.put("result", "no existen eventos");
                }
                return ResponseEntity.ok(respuesta);
            }catch (DateTimeParseException ex){
                respuesta.put("result","error");
                respuesta.put("msg","El parámetro ingresado debe ser una fecha");
                return ResponseEntity.badRequest().body(respuesta);
            }
        }
    }

    @PostMapping(value = {"", "/"})
    public ResponseEntity<HashMap<String, Object>> guardarProducto(
            @RequestBody Evento evento,
            @RequestParam(value = "fetchId", required = false) boolean fetchId) {

        HashMap<String, Object> responseJson = new HashMap<>();
        if (evento.getFecha().isBefore(LocalDate.now())) {
            responseJson.put("result", "error");
            responseJson.put("msg", "La fecha del evento debe ser en el futuro.");
            return ResponseEntity.badRequest().body(responseJson);
        }
        evento.setReservasActuales(0);
        eventoRepository.save(evento);
        if (fetchId) {
            responseJson.put("id", evento.getId());
        }
        responseJson.put("result", "ok");
        responseJson.put("estado", "creado");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseJson);
    }

    @PostMapping("/reservar")
    public ResponseEntity<HashMap<String, Object>> reservarLugar(
            @RequestBody Reserva reserva,
            @RequestParam(value = "fetchId", required = false) boolean fetchId) {

        HashMap<String, Object> responseJson = new HashMap<>();
        Optional<Evento> eventoOptional = eventoRepository.findById(reserva.getEvento().getId());

        if (eventoOptional.isEmpty()) {
            responseJson.put("result", "error");
            responseJson.put("msg", "El evento no existe.");
            return ResponseEntity.badRequest().body(responseJson);
        }

        Evento evento = eventoOptional.get();
        if (evento.getReservasActuales() + reserva.getCupos() > evento.getCapacidadMaxima()) {
            responseJson.put("result", "error");
            responseJson.put("msg", "No hay suficientes cupos disponibles.");
            return ResponseEntity.badRequest().body(responseJson);
        }
        reservaRepository.save(reserva);

        evento.setReservasActuales(evento.getReservasActuales() + reserva.getCupos());
        eventoRepository.save(evento);

        responseJson.put("result", "ok");
        responseJson.put("msg", "Reserva realizada exitosamente.");
        return ResponseEntity.ok(responseJson);
    }
    @DeleteMapping("/cancelar/{id}")
    public ResponseEntity<HashMap<String, Object>> cancelarReserva(@PathVariable String id) {
        HashMap<String, Object> responseJson = new HashMap<>();
        try {
            Integer id_reserva = Integer.parseInt(id);
            Optional<Reserva> reservaOptional = reservaRepository.findById(id_reserva);

            if (reservaOptional.isEmpty()) {
                responseJson.put("result", "error");
                responseJson.put("msg", "La reserva no existe.");
                return ResponseEntity.badRequest().body(responseJson);
            }

            Reserva reserva = reservaOptional.get();
            Evento evento = reserva.getEvento();
            int cuposReservados = reserva.getCupos();
            reservaRepository.delete(reserva);
            evento.setReservasActuales(evento.getReservasActuales() - cuposReservados);
            eventoRepository.save(evento);

            responseJson.put("result", "ok");
            responseJson.put("msg", "Reserva cancelada exitosamente.");
            return ResponseEntity.ok(responseJson);
        } catch (NumberFormatException e) {
            responseJson.put("result","error");
            responseJson.put("msg","Debe ingresar un id adecuado (número)");
            return ResponseEntity.badRequest().body(responseJson);
        }

    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HashMap<String, String>> gestionException(HttpServletRequest request, HttpMessageNotReadableException ex) {
        HashMap<String, String> responseMap = new HashMap<>();

        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException cause = (InvalidFormatException) ex.getCause();

            if (cause.getTargetType().isEnum()) {
                responseMap.put("result", "error");
                responseMap.put("msg", "La categoría debe ser Conferencia, Exposicion, Taller o Concierto");
                return ResponseEntity.badRequest().body(responseMap);
            }
        }
        if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
            responseMap.put("result", "error");
            responseMap.put("msg", "Debe enviar un evento");
        }

        return ResponseEntity.badRequest().body(responseMap);
    }
}
