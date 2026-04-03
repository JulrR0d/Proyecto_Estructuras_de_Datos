package grupo1.Clases;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidad que representa un paciente en el sistema de triage.
 */
public class Paciente {

    // Atributos
    private long id;
    private String nombre;
    private LocalDate fechaIngreso;
    private LocalTime horaIngreso;
    private Byte nivelTriage;

    // Constructor
    public Paciente(long id, String nombre, Byte nivelTriage) {
        this.id = id;
        this.nombre = nombre;
        this.fechaIngreso = LocalDate.now();
        this.horaIngreso = LocalTime.now();
        validarNivelTriage(nivelTriage);
        this.nivelTriage = nivelTriage;
    }

    // Métodos Get
    public long getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }
    public LocalTime getHoraIngreso() {
        return horaIngreso;
    }
    public Byte getNivelTriage() {
        return nivelTriage;
    }

    // Métodos Set
    public void setId(long id) {
        this.id = id;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
    public void setHoraIngreso(LocalTime horaIngreso) {
        this.horaIngreso = horaIngreso;
    }
    public void setNivelTriage(Byte nivelTriage) {
        validarNivelTriage(nivelTriage);
        this.nivelTriage = nivelTriage;
    }

    /**
     * Valida que el nivel de triage este dentro del rango permitido.
     */
    private void validarNivelTriage(Byte nivelTriage) {
        if (nivelTriage == null || nivelTriage < 1 || nivelTriage > 5) {
            throw new IllegalArgumentException("El nivel de triage debe estar entre 1 y 5.");
        }
    }

    @Override
    public String toString() {
        return "Paciente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", nivelTriage=" + nivelTriage +
                ", fechaIngreso=" + fechaIngreso +
                ", horaIngreso=" + horaIngreso +
                '}';
    }
    
}
