package grupo1.Clases;

import java.time.*;

public class Paciente {

    // Atributos
    private long id;
    private String nombre;
    private LocalDate fechaIngreso;
    private LocalTime horaIngreso;
    private Byte nivelTriage;

    // Constructor
    public Paciente(long id, String nombre, Byte nivelTraige) {
        this.id = id;
        this.nombre = nombre;
        this.fechaIngreso = LocalDate.now();
        this.horaIngreso = LocalTime.now();
        this.nivelTriage = nivelTraige;
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
        this.nivelTriage = nivelTriage;
    }
    
}
