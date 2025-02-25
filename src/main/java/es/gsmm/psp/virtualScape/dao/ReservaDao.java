package es.gsmm.psp.virtualScape.dao;

import es.gsmm.psp.virtualScape.model.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ReservaDao {
    private final Map<Integer, Reserva> reservas = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);


    public List<Reserva> findAll(){
        return new ArrayList<>(reservas.values());
    }

    public int generarId() {
        return idGenerator.getAndIncrement();
    }

    public void guardarReserva(Reserva reserva) {
        reservas.put(reserva.getId(), reserva);
    }

    public List<Reserva> obtenerTodas() {
        return new ArrayList<>(reservas.values());
    }

    public Reserva obtenerPorId(int id) {
        return reservas.get(id);
    }

    public boolean eliminarReserva(int id) {
        return reservas.remove(id) != null;
    }

    public boolean existeReserva(String nombreSala, LocalDate fecha, LocalTime hora) {
        return reservas.values().stream()
                .anyMatch(reserva -> reserva.getNombreSala().equalsIgnoreCase(nombreSala)
                        && reserva.getFechaReserva().equals(fecha)
                        && reserva.getHoraReserva().equals(hora));
    }
    public List<Reserva> obtenerPorFecha(LocalDate fecha) {
        return reservas.values().stream()
                .filter(reserva -> reserva.getFechaReserva().equals(fecha))
                .collect(Collectors.toList());
    }

    //
    public boolean existeReservaExceptoId(String nombreSala, LocalDate fecha, LocalTime hora, int id) {
        return reservas.values().stream()
                .anyMatch(reserva -> reserva.getId() != id &&
                        reserva.getNombreSala().equalsIgnoreCase(nombreSala) &&
                        reserva.getFechaReserva().equals(fecha) &&
                        reserva.getHoraReserva().equals(hora));
    }

    public Map<String, Long> contarReservasPorSala() {
        return reservas.values().stream()
                .collect(Collectors.groupingBy(Reserva::getNombreSala, Collectors.counting()));
    }

    //contar la cantidad total de jugadores en todas las reservas
    public int contarTotalJugadores(){
        return reservas.values().stream()
                .mapToInt(Reserva::getJugadores)
                .sum();
    }
}
