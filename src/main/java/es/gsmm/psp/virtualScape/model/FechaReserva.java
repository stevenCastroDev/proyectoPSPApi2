package es.gsmm.psp.virtualScape.model;

public class FechaReserva {
    private int diaReserva;
    private int horaReserva;

    // Getters y Setters
    public int getDiaReserva() {
        return diaReserva;
    }

    public void setDiaReserva(int diaReserva) {
        this.diaReserva = diaReserva;
    }

    public int getHoraReserva() {
        return horaReserva;
    }

    public void setHoraReserva(int horaReserva) {
        this.horaReserva = horaReserva;
    }

    //valida que el dia y la hora esten bien
    public String validarFecha() {
        if (diaReserva < 1 || diaReserva > 31) {
            return "El día de la reserva es inválido";
        }
        if (horaReserva < 0 || horaReserva > 23) {
            return "La hora de la reserva es inválida";
        }
        return null;
    }
}
