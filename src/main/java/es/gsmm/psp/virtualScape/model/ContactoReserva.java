package es.gsmm.psp.virtualScape.model;

public class ContactoReserva {
 private String titular;
    private String telefono;

    // Getters y Setters
    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    //valida los datos de contacto
    public String validarContacto() {
        if (titular == null || titular.trim().isEmpty()) {
            return "El nombre del titular es obligatorio";
        }
        if (telefono == null || telefono.trim().isEmpty()) {
            return "El teléfono es obligatorio";
        }
        // Se espera que el teléfono tenga exactamente 9 dígitos
        if (!telefono.matches("\\d{9}")) {
            return "El teléfono debe tener 9 dígitos";
        }
        return null;
    }
}