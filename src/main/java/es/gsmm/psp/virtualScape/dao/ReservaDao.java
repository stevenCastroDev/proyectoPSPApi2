package es.gsmm.psp.virtualScape.dao;

import es.gsmm.psp.virtualScape.model.Reserva;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class ReservaDao {
    private final Map<Integer, Reserva> reservas = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);


    public int generarId() {
        return idGenerator.getAndIncrement();
    }
    public void actualizarReserva(Reserva reserva) {
        reservas.put(reserva.getId(), reserva);
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

    // Se actualiza para usar int dia y int hora desde FechaReserva
    public boolean existeReserva(String nombreSala, int dia, int hora) {
        return reservas.values().stream()
                .anyMatch(reserva -> reserva.getNombreSala().equalsIgnoreCase(nombreSala)
                        && reserva.getFecha().getDiaReserva() == dia
                        && reserva.getFecha().getHoraReserva() == hora);
    }

    // Filtra reservas cuyo día (de FechaReserva) coincide con el valor dado
    public List<Reserva> obtenerPorDia(int dia) {
        return reservas.values().stream()
                .filter(reserva -> reserva.getFecha().getDiaReserva() == dia)
                .collect(Collectors.toList());
    }

    // Versión de existeReserva que excluye una reserva en base a su id
    public boolean existeReservaExceptoId(String nombreSala, int dia, int hora, int id) {
        return reservas.values().stream()
                .anyMatch(reserva -> reserva.getId() != id &&
                        reserva.getNombreSala().equalsIgnoreCase(nombreSala) &&
                        reserva.getFecha().getDiaReserva() == dia &&
                        reserva.getFecha().getHoraReserva() == hora);
    }

    public Map<String, Long> contarReservasPorSala() {
        return reservas.values().stream()
                .collect(Collectors.groupingBy(Reserva::getNombreSala, Collectors.counting()));
    }

    // Suma la cantidad total de jugadores en todas las reservas
    public int contarTotalJugadores(){
        return reservas.values().stream()
                .mapToInt(Reserva::getJugadores)
                .sum();
    }
}
