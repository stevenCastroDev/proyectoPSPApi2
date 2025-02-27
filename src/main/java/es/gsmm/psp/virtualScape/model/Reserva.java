package es.gsmm.psp.virtualScape.model;

public class Reserva {
    private int id;
    private String nombreSala;
    private FechaReserva fecha;       // Contiene diaReserva y horaReserva
    private ContactoReserva contacto; // Contiene titular y telefono
    private int jugadores;

    // Getters y setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNombreSala() {
        return nombreSala;
    }
    public void setNombreSala(String nombreSala) {
        this.nombreSala = nombreSala;
    }
    public FechaReserva getFecha() {
        return fecha;
    }
    public void setFecha(FechaReserva fecha) {
        this.fecha = fecha;
    }
    public ContactoReserva getContacto() {
        return contacto;
    }
    public void setContacto(ContactoReserva contacto) {
        this.contacto = contacto;
    }
    public int getJugadores() {
        return jugadores;
    }
    public void setJugadores(int jugadores) {
        this.jugadores = jugadores;
    }

    //validacion de reserva que contenga todos los datos requeridos
    public String validarReserva() {
        if (nombreSala == null || nombreSala.trim().isEmpty()) {
            return "El nombre de la sala es obligatorio";
        }
        if (fecha == null) {
            return "La fecha de la reserva es obligatoria";
        }
        String errorFecha = fecha.validarFecha();
        if (errorFecha != null) {
            return errorFecha;
        }
        if (contacto == null) {
            return "Los datos de contacto son obligatorios";
        }
        String errorContacto = contacto.validarContacto();
        if (errorContacto != null) {
            return errorContacto;
        }
        if (jugadores < 1) {
            return "Debe haber al menos 1 jugador";
        }
        return null;
    }
}
