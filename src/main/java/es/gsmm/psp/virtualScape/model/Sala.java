package es.gsmm.psp.virtualScape.model;

import java.util.List;

public class Sala {
    private int id;
    private String nombre;
    private int capacidadMin;
    private int capacidadMax;
    private List<String> tematicas;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCapacidadMin() {
        return capacidadMin;
    }

    public void setCapacidadMin(int capacidadMin) {
        this.capacidadMin = capacidadMin;
    }

    public int getCapacidadMax() {
        return capacidadMax;
    }

    public void setCapacidadMax(int capacidadMax) {
        this.capacidadMax = capacidadMax;
    }

    public List<String> getTematicas() {
        return tematicas;
    }

    public void setTematicas(List<String> tematicas) {
        this.tematicas = tematicas;
    }

    public String validarSala() {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El nombre de la sala es obligatorio";
        }
        if (capacidadMin < 1 || capacidadMax > 8 || capacidadMin > capacidadMax) {
            return "La capacidad mínima debe ser al menos 1 y la máxima no puede superar 8 jugadores";
        }
        if (tematicas == null || tematicas.isEmpty()) {
            return "La sala debe tener al menos una temática";
        }
        return null; // Sin errores
    }
}
