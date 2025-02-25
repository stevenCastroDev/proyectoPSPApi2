package es.gsmm.psp.virtualScape.controller;




import es.gsmm.psp.virtualScape.dao.SalaDao;
import es.gsmm.psp.virtualScape.model.Sala;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/virtual-escape/salas")
public class SalaController {
    private final SalaDao salaDao;

    public SalaController(SalaDao salaDao) {
        this.salaDao = salaDao;
    }

    /**
     * Crear una nueva sala
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearSala(@RequestBody Sala sala) {
        String error = sala.validarSala();
        if (error != null) {
            return respuesta(false, error, null, HttpStatus.BAD_REQUEST);
        }

        if (salaDao.obtenerPorNombre(sala.getNombre()) != null) {
            return respuesta(false, "Ya existe una sala con ese nombre", null, HttpStatus.CONFLICT);
        }

        sala.setId(salaDao.generarId());
        salaDao.guardarSala(sala);
        return respuesta(true, "Sala creada correctamente", sala.getId(), HttpStatus.CREATED);
    }

    /**
     * Listar todas las salas
     */
    @GetMapping
    public ResponseEntity<List<Sala>> listarSalas() {
        List<Sala> salas = salaDao.obtenerTodas();
        if (salas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(salas);
    }

    /**
     * Obtener una sala específica
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerSala(@PathVariable int id) {
        Sala sala = salaDao.obtenerPorId(id);
        if (sala == null) {
            return respuesta(false, "Sala no encontrada", null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(sala);
    }

    /**
     * Modificar una sala existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarSala(@PathVariable int id, @RequestBody Sala salaActualizada) {
        Sala salaExistente = salaDao.obtenerPorId(id);
        if (salaExistente == null) {
            return respuesta(false, "Sala no encontrada", null, HttpStatus.NOT_FOUND);
        }

        String error = salaActualizada.validarSala();
        if (error != null) {
            return respuesta(false, error, null, HttpStatus.BAD_REQUEST);
        }

        salaActualizada.setId(id);
        salaDao.guardarSala(salaActualizada);
        return respuesta(true, "Sala actualizada correctamente", id, HttpStatus.OK);
    }

    /**
     * Eliminar una sala
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarSala(@PathVariable int id) {
        boolean eliminada = salaDao.eliminarSala(id);
        if (!eliminada) {
            return respuesta(false, "Sala no encontrada", null, HttpStatus.NOT_FOUND);
        }
        return respuesta(true, "Sala eliminada con éxito", id, HttpStatus.OK);
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

