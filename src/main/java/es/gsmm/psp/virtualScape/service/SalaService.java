package es.gsmm.psp.virtualScape.service;

import es.gsmm.psp.virtualScape.dao.ReservaDao;
import es.gsmm.psp.virtualScape.dao.SalaDao;
import es.gsmm.psp.virtualScape.model.Sala;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SalaService {
    private final SalaDao salaDao;
    private final ReservaDao reservaDao;
    private static final Logger logger = LoggerFactory.getLogger(SalaService.class);

    public SalaService(SalaDao salaDao, ReservaDao reservaDao) {
        this.salaDao = salaDao;
        this.reservaDao = reservaDao;
    }

    public String validarSala(Sala sala) {
        if (sala == null) return "Sala no puede ser nula";

        String error = sala.validarSala();
        if (error != null) {
            logger.warn("Validación fallida: " + error);
            return error;
        }

        Sala salaExistente = salaDao.obtenerPorNombre(sala.getNombre());
        if (salaExistente != null) {
            logger.warn("Intento de crear sala duplicada: " + sala.getNombre());
            return "Ya existe una sala con ese nombre";
        }

        return null;
    }

    public Sala crearSala(Sala sala) {
        sala.setId(salaDao.generarId());
        salaDao.guardarSala(sala);
        logger.info("Sala creada: ID " + sala.getId() + ", Nombre: " + sala.getNombre());
        return sala;
    }

    public List<Sala> obtenerTodasLasSalas() {
        return salaDao.obtenerTodas();
    }

    public Sala obtenerSalaPorId(int id) {
        return salaDao.obtenerPorId(id);
    }

    public boolean eliminarSala(int id) {
        boolean eliminada = salaDao.eliminarSala(id);
        if (eliminada) logger.info("Sala eliminada: ID " + id);
        return eliminada;
    }
    public List<Sala> buscarPorTematica(String tematica) {
        return salaDao.buscarPorTematica(tematica);
    }
    public List<Map<String, Object>> obtenerSalasMasReservadas(int limite) {
        Map<String, Long> reservasPorSala = reservaDao.contarReservasPorSala();

        List<Map<String, Object>> resultado = new ArrayList<>();

        reservasPorSala.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue())) // Ordenar de mayor a menor
                .limit(limite) // Limitar a las 2 más reservadas
                .forEach(entry -> {
                    Map<String, Object> salaInfo = new HashMap<>();
                    salaInfo.put("sala", entry.getKey());
                    salaInfo.put("totalReservas", entry.getValue());
                    resultado.add(salaInfo);
                });

        return resultado;
    }


}
