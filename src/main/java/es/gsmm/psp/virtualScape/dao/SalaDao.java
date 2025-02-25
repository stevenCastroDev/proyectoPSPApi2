package es.gsmm.psp.virtualScape.dao;

import es.gsmm.psp.virtualScape.model.Sala;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SalaDao {
    private final Map<Integer, Sala> salas = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    public int generarId() {
        return idGenerator.getAndIncrement();
    }

    public void guardarSala(Sala sala) {
        salas.put(sala.getId(), sala);
    }
    public Sala obtenerPorId(int id) {
        return salas.get(id);
    }

    public List<Sala> obtenerTodas() {
        return new ArrayList<>(salas.values());
    }

    public Sala obtenerPorNombre(String nombre) {
        return salas.values().stream()
                .filter(s -> s.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    public boolean eliminarSala(int id) {
        return salas.remove(id) != null;
    }

    public List<Sala> buscarPorTematica(String tematica) {
        return salas.values().stream()
                .filter(sala -> sala.getTematicas().contains(tematica))
                .collect(Collectors.toList());
    }

    public List<Sala> salasConMasReservas(Map<String, Long> reservasPorSala) {
        return reservasPorSala.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(entry -> obtenerPorNombre(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
