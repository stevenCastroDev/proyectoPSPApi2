package es.gsmm.psp.virtualScape.service;

import es.gsmm.psp.virtualScape.dao.ReservaDao;
import es.gsmm.psp.virtualScape.dao.SalaDao;
import es.gsmm.psp.virtualScape.model.Reserva;
import es.gsmm.psp.virtualScape.model.Sala;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        // Verificar que la sala exista
        Sala sala = salaDao.obtenerPorNombre(reserva.getNombreSala());
        if (sala == null) {
            logger.warn("Intento de reservar en sala inexistente: " + reserva.getNombreSala());
            return "La sala no existe";
        }

        // Validar el número de jugadores respecto a la capacidad de la sala
        if (reserva.getJugadores() < sala.getCapacidadMin() || reserva.getJugadores() > sala.getCapacidadMax()) {
            logger.warn("Número de jugadores fuera del límite: " + reserva.getJugadores() +
                    " (Mín: " + sala.getCapacidadMin() + ", Máx: " + sala.getCapacidadMax() + ")");
            return "El número de jugadores no cumple con los límites de la sala";
        }

        // Usamos la nueva estructura: FechaReserva contiene diaReserva y horaReserva
        int diaReserva = reserva.getFecha().getDiaReserva();
        int horaReserva = reserva.getFecha().getHoraReserva();
        if (reservaDao.existeReserva(reserva.getNombreSala(), diaReserva, horaReserva)) {
            logger.warn("Conflicto de horario detectado para la sala: " + reserva.getNombreSala() +
                    " a la hora: " + horaReserva);
            return "Ya existe una reserva para esta sala en esa fecha y hora";
        }

        // Validar el aforo total
        int jugadoresActuales = reservaDao.contarTotalJugadores();
        if (jugadoresActuales + reserva.getJugadores() > AFORO_MAXIMO) {
            logger.warn("Aforo total excedido: " + (jugadoresActuales + reserva.getJugadores()) + "/30");
            return "No se puede crear la reserva: el aforo total excede los 30 jugadores";
        }

        return null;
    }

    public Reserva crearReserva(Reserva reserva) {
        reserva.setId(reservaDao.generarId());
        reservaDao.guardarReserva(reserva);
        logger.info("Reserva creada: ID " + reserva.getId() + ", Sala: " + reserva.getNombreSala() +
                ", Día: " + reserva.getFecha().getDiaReserva() +
                ", Hora: " + reserva.getFecha().getHoraReserva());
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
    public void actualizarReserva(Reserva reserva) {
        reservaDao.actualizarReserva(reserva);
        logger.info("Reserva actualizada: ID " + reserva.getId() + ", Sala: " + reserva.getNombreSala() +
                ", Día: " + reserva.getFecha().getDiaReserva() + ", Hora: " + reserva.getFecha().getHoraReserva());
    }



    //obtener reserva de un dia especifico
    public List<Map<String, Object>> obtenerReservasConDetallesPorDia(int dia) {
        List<Reserva> reservas = reservaDao.obtenerPorDia(dia);

        return reservas.stream().map(reserva -> {
            Map<String, Object> detalles = new HashMap<>();
            detalles.put("sala", reserva.getNombreSala());
            detalles.put("dia", reserva.getFecha().getDiaReserva());
            detalles.put("hora", reserva.getFecha().getHoraReserva());

            Map<String, Object> contacto = new HashMap<>();
            contacto.put("titular", reserva.getContacto().getTitular());
            contacto.put("telefono", reserva.getContacto().getTelefono());

            detalles.put("contacto", contacto);
            detalles.put("jugadores", reserva.getJugadores());
            return detalles;

        }).collect(Collectors.toList());
    }
}
