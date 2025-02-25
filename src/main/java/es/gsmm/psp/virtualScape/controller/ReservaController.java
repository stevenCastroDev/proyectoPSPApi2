package es.gsmm.psp.virtualScape.controller;

import es.gsmm.psp.virtualScape.dao.ReservaDao;
import es.gsmm.psp.virtualScape.dao.SalaDao;
import es.gsmm.psp.virtualScape.model.Reserva;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/virtual-escape/reservas")
public class ReservaController {
    private final ReservaDao reservaDao;
    private final SalaDao salaDao;
    private static final int AFORO_MAXIMO = 30;

    public ReservaController(ReservaDao reservaDao, SalaDao salaDao) {
        this.reservaDao = reservaDao;
        this.salaDao = salaDao;
    }

    /**
     * Crear una nueva reserva
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearReserva(@RequestBody Reserva reserva) {
        String error = reserva.validarReserva();
        if (error != null) {
            return respuesta(false, error, null, HttpStatus.BAD_REQUEST);
        }

        // Verificar si la sala existe
        if (salaDao.obtenerPorNombre(reserva.getNombreSala()) == null) {
            return respuesta(false, "La sala no existe", null, HttpStatus.NOT_FOUND);
        }

        // Verificar conflicto de horarios
        if (reservaDao.existeReserva(reserva.getNombreSala(), reserva.getFechaReserva(), reserva.getHoraReserva())) {
            return respuesta(false, "Ya existe una reserva para esta sala en esa fecha y hora", null, HttpStatus.CONFLICT);
        }

        // Verificar límite de jugadores de la sala
        var sala = salaDao.obtenerPorNombre(reserva.getNombreSala());
        if (reserva.getJugadores() < sala.getCapacidadMin() || reserva.getJugadores() > sala.getCapacidadMax()) {
            return respuesta(false, "El número de jugadores no cumple con los límites de la sala", null, HttpStatus.BAD_REQUEST);
        }

        // Verificar aforo total
        int jugadoresActuales = reservaDao.contarTotalJugadores();
        if (jugadoresActuales + reserva.getJugadores() > AFORO_MAXIMO) {
            return respuesta(false, "No se puede crear la reserva: el aforo total excede los 30 jugadores", null, HttpStatus.CONFLICT);
        }

        // Crear la reserva
        reserva.setId(reservaDao.generarId());
        reservaDao.guardarReserva(reserva);

        return respuesta(true, "Reserva creada correctamente", reserva.getId(), HttpStatus.CREATED);
    }

    /**
     * Listar todas las reservas
     */
    @GetMapping
    public ResponseEntity<List<Reserva>> listarReservas() {
        List<Reserva> reservas = reservaDao.obtenerTodas();
        if (reservas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reservas);
    }

    /**
     * Obtener una reserva específica
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerReserva(@PathVariable int id) {
        Reserva reserva = reservaDao.obtenerPorId(id);
        if (reserva == null) {
            return respuesta(false, "Reserva no encontrada", null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(reserva);
    }

    /**
     * Eliminar una reserva
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarReserva(@PathVariable int id) {
        boolean eliminada = reservaDao.eliminarReserva(id);
        if (!eliminada) {
            return respuesta(false, "Reserva no encontrada", null, HttpStatus.NOT_FOUND);
        }
        return respuesta(true, "Reserva eliminada con éxito", id, HttpStatus.OK);
    }

    /**
     * Obtener reservas por día
     */
    @GetMapping("/dia/{fecha}")
    public ResponseEntity<?> obtenerReservasPorDia(@PathVariable LocalDate fecha) {
        List<Reserva> reservas = reservaDao.obtenerPorFecha(fecha);
        if (reservas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reservas);
    }

    /**
     * Formato de respuesta estándar
     */
    private ResponseEntity<Map<String, Object>> respuesta(boolean exito, String mensaje, Integer id, HttpStatus status) {
        Map<String, Object> response = Map.of(
                "éxito", exito,
                "mensaje", mensaje,
                "idGenerado", id
        );
        return new ResponseEntity<>(response, status);
    }
}