package es.gsmm.psp.virtualScape.controller;

import es.gsmm.psp.virtualScape.model.Sala;
import es.gsmm.psp.virtualScape.service.SalaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/virtual-escape/salas")
@Tag(name = "Salas", description = "API para gestionar salas de juego")
public class SalaController {
    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva sala", description = "Registra una nueva sala en la base de datos")
    public ResponseEntity<Map<String, Object>> crearSala(@RequestBody Sala sala) {
        String error = salaService.validarSala(sala);
        if (error != null) {
            return respuesta(false, error, null, HttpStatus.BAD_REQUEST);
        }

        Sala nuevaSala = salaService.crearSala(sala);
        return respuesta(true, "Sala creada correctamente", nuevaSala.getId(), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todas las salas", description = "Devuelve una lista con todas las salas registradas")
    public ResponseEntity<?> listarSalas() {
        List<Sala> salas = salaService.obtenerTodasLasSalas();
        if (salas.isEmpty()) {
            return respuesta(false, "No hay salas registradas", null, HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(salas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalles de una sala", description = "Recupera los datos de una sala específica por su ID")
    public ResponseEntity<?> obtenerSala(@PathVariable int id) {
        Sala sala = salaService.obtenerSalaPorId(id);
        if (sala == null) {
            return respuesta(false, "Sala no encontrada", null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(sala);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una sala", description = "Modifica los datos de una sala existente según su ID")
    public ResponseEntity<Map<String, Object>> actualizarSala(@PathVariable int id, @RequestBody Sala salaActualizada) {
        Sala salaExistente = salaService.obtenerSalaPorId(id);
        if (salaExistente == null) {
            return respuesta(false, "Sala no encontrada", null, HttpStatus.NOT_FOUND);
        }

        String error = salaService.validarSala(salaActualizada);
        if (error != null) {
            return respuesta(false, error, null, HttpStatus.BAD_REQUEST);
        }

        salaActualizada.setId(id);
        salaService.crearSala(salaActualizada);
        return respuesta(true, "Sala actualizada correctamente", id, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una sala", description = "Elimina una sala de la base de datos por su ID")
    public ResponseEntity<Map<String, Object>> eliminarSala(@PathVariable int id) {
        boolean eliminada = salaService.eliminarSala(id);
        if (!eliminada) {
            return respuesta(false, "Sala no encontrada", null, HttpStatus.NOT_FOUND);
        }
        return respuesta(true, "Sala eliminada con éxito", id, HttpStatus.OK);
    }

    @GetMapping("/tematica/{tematica}")
    @Operation(summary = "Buscar salas por temática", description = "Recupera una lista de salas según la temática especificada")
    public ResponseEntity<?> buscarSalasPorTematica(@PathVariable String tematica) {
        List<Sala> salas = salaService.buscarPorTematica(tematica);
        if (salas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(salas);
    }

    @GetMapping("/mas-reservadas")
    @Operation(summary = "Listar salas más reservadas", description = "Obtiene las dos salas con más reservas registradas")
    public ResponseEntity<?> listarSalasMasReservadas() {
        List<Map<String, Object>> salas = salaService.obtenerSalasMasReservadas(2);
        if (salas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(salas);
    }

    private ResponseEntity<Map<String, Object>> respuesta(boolean exito, String mensaje, Integer id, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("éxito", exito);
        response.put("mensaje", mensaje);
        response.put("idGenerado", id);
        return new ResponseEntity<>(response, status);
    }
}
