package es.gsmm.psp.virtualScape.controller;

import es.gsmm.psp.virtualScape.model.Reserva;
import es.gsmm.psp.virtualScape.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/virtual-escape/reservas")
@Tag(name = "Reservas", description = "API para gestionar reservas de salas de juego")
public class ReservaController {
    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    @Operation(summary = "Crear una reserva", description = "Registra una nueva reserva en la base de datos")
    public ResponseEntity<Map<String, Object>> crearReserva(@RequestBody Reserva reserva) {
        String error = reservaService.validarReserva(reserva);

        if (error != null) {
            if (error.equals("Ya existe una reserva para esta sala en esa fecha y hora")) {
                return respuesta(false, error, null, HttpStatus.CONFLICT);
            }
            return respuesta(false, error, null, HttpStatus.BAD_REQUEST);
        }

        Reserva nuevaReserva = reservaService.crearReserva(reserva);
        return respuesta(true, "Reserva creada correctamente", nuevaReserva.getId(), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todas las reservas", description = "Devuelve una lista con todas las reservas registradas")
    public ResponseEntity<List<Reserva>> listarReservas() {
        List<Reserva> reservas = reservaService.obtenerReservas();
        if (reservas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reservas);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una reserva", description = "Modifica una reserva existente según su ID")
    public ResponseEntity<Map<String, Object>> actualizarReserva(@PathVariable int id, @RequestBody Reserva reserva) {
        if (reservaService.obtenerReservaPorId(id) == null) {
            return respuesta(false, "Reserva no encontrada", null, HttpStatus.NOT_FOUND);
        }

        String error = reservaService.validarReserva(reserva);
        if (error != null) {
            if (error.equals("Ya existe una reserva para esta sala en esa fecha y hora")) {
                return respuesta(false, error, null, HttpStatus.CONFLICT);
            }
            return respuesta(false, error, null, HttpStatus.BAD_REQUEST);
        }

        reserva.setId(id);
        reservaService.actualizarReserva(reserva);
        return respuesta(true, "Reserva actualizada correctamente", id, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalles de una reserva", description = "Recupera los datos de una reserva específica por ID")
    public ResponseEntity<?> obtenerReserva(@PathVariable int id) {
        Reserva reserva = reservaService.obtenerReservaPorId(id);
        if (reserva == null) {
            return respuesta(false, "Reserva no encontrada", null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(reserva);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una reserva", description = "Elimina una reserva de la base de datos")
    public ResponseEntity<Map<String, Object>> eliminarReserva(@PathVariable int id) {
        boolean eliminada = reservaService.eliminarReserva(id);
        if (!eliminada) {
            return respuesta(false, "Reserva no encontrada", null, HttpStatus.NOT_FOUND);
        }
        return respuesta(true, "Reserva eliminada con éxito", id, HttpStatus.CREATED);
    }

    @GetMapping("/dia/{numDia}")
    @Operation(summary = "Obtener reservas por día", description = "Recupera una lista de reservas en una fecha específica")
    public ResponseEntity<?> obtenerReservasPorDia(@PathVariable int numDia) {
        List<Map<String, Object>> reservas = reservaService.obtenerReservasConDetallesPorDia(numDia);

        if (reservas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reservas);
    }

    private ResponseEntity<Map<String, Object>> respuesta(boolean exito, String mensaje, Integer id, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("éxito", exito);
        response.put("mensaje", mensaje);
        response.put("idGenerado", id);
        return new ResponseEntity<>(response, status);
    }
}
