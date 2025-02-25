package es.gsmm.psp.virtualScape.service;

import es.gsmm.psp.virtualScape.dao.ReservaDao;
import es.gsmm.psp.virtualScape.dao.SalaDao;
import es.gsmm.psp.virtualScape.model.Reserva;
import es.gsmm.psp.virtualScape.model.Sala;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservaService {
    private final ReservaDao reservaDao;
    private final SalaDao salaDao;
    private static final int AFORO_MAXIMO = 30;
    private static final Logger logger = LoggerFactory.getLogger(ReservaService.class);

    public ReservaService(ReservaDao reservaDao, SalaDao salaDao) {
        this.reservaDao = reservaDao;
        this.salaDao = salaDao;
    }

    public String validarReserva(Reserva reserva) {
        if (reserva == null) return "Reserva no puede ser nula";

        // Validar reserva
        String error = reserva.validarReserva();
        if (error != null) {
            logger.warn("Validación fallida: " + error);
            return error;
        }

        // Verificar existencia de la sala
        Sala sala = salaDao.obtenerPorNombre(reserva.getNombreSala());
        if (sala == null) {
            logger.warn("Intento de reservar en sala inexistente: " + reserva.getNombreSala());
            return "La sala no existe";
        }

        // Verificar límite de jugadores
        if (reserva.getJugadores() < sala.getCapacidadMin() || reserva.getJugadores() > sala.getCapacidadMax()) {
            logger.warn("Número de jugadores fuera del límite de la sala");
            return "El número de jugadores no cumple con los límites de la sala";
        }

        // Verificar conflicto de horarios
        if (reservaDao.existeReserva(reserva.getNombreSala(), reserva.getFechaReserva(), reserva.getHoraReserva())) {
            logger.warn("Conflicto de horario detectado para la sala: " + reserva.getNombreSala());
            return "Ya existe una reserva para esta sala en esa fecha y hora";
        }

        // Verificar aforo total
        int jugadoresActuales = reservaDao.contarTotalJugadores();
        if (jugadoresActuales + reserva.getJugadores() > AFORO_MAXIMO) {
            logger.warn("Aforo total excedido: " + (jugadoresActuales + reserva.getJugadores()) + "/30");
            return "No se puede crear la reserva: el aforo total excede los 30 jugadores";
        }

        return null; // Todo está bien
    }

    public Reserva crearReserva(Reserva reserva) {
        reserva.setId(reservaDao.generarId());
        reservaDao.guardarReserva(reserva);
        logger.info("Reserva creada: ID " + reserva.getId());
        return reserva;
    }

    public List<Reserva> obtenerReservas() {
        return reservaDao.obtenerTodas();
    }

    public Reserva obtenerReservaPorId(int id) {
        return reservaDao.obtenerPorId(id);
    }

    public boolean eliminarReserva(int id) {
        boolean eliminada = reservaDao.eliminarReserva(id);
        if (eliminada) logger.info("Reserva eliminada: ID " + id);
        return eliminada;
    }

    public List<Reserva> obtenerReservasPorFecha(LocalDate fecha) {
        return reservaDao.obtenerPorFecha(fecha);
    }
}
