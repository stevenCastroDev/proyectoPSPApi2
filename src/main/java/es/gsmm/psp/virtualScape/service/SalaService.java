package es.gsmm.psp.virtualScape.service;

import es.gsmm.psp.virtualScape.dao.SalaDao;
import es.gsmm.psp.virtualScape.model.Sala;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaService {
    private final SalaDao salaDao;
    private static final Logger logger = LoggerFactory.getLogger(SalaService.class);

    public SalaService(SalaDao salaDao) {
        this.salaDao = salaDao;
    }

    public String validarSala(Sala sala) {
        if (sala == null) return "Sala no puede ser nula";

        String error = sala.validarSala();
        if (error != null) {
            logger.warn("Validación fallida: " + error);
            return error;
        }

        if (salaDao.obtenerPorNombre(sala.getNombre()) != null) {
            logger.warn("Intento de crear sala duplicada: " + sala.getNombre());
            return "Ya existe una sala con ese nombre";
        }

        return null;
    }

    public Sala crearSala(Sala sala) {
        sala.setId(salaDao.generarId());
        salaDao.guardarSala(sala);
        logger.info("Sala creada: ID " + sala.getId());
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
}
