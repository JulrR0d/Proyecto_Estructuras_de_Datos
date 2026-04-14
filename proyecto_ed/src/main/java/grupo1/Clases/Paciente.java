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
    private int edad;
    private char sexo; // m/f
    private String EPS;
    private String sintomas;
    private LocalDate fechaIngreso;
    private LocalTime horaIngreso;
    private Byte nivelTriage;

    // Constructor
    public Paciente(long id, String nombre, int edad, char sexo, String EPS, String sintomas, Byte nivelTriage) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.sexo = sexo;
        this.EPS = EPS;
        this.sintomas = sintomas;
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
    public int getEdad() {
        return edad;
    }
    public char getSexo() {
        return sexo;
    }
    public String getEPS(){
        retunr EPS;
    }
    public String getsintomas(){
        return sintomas;
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
    public void setId(long id) { //pdoriamso eliminar este pq igual es mejor no editar el id si es la manera en la que vamos a clasificarlos para el arbol
        this.id = id;
    }
    public void setNombre(String nombre) {
        if (nombre  == null || nombre.trim().isEmpty()){
            throw new RunTimeException("Debes ingresar un nombre.");
        }
        this.nombre = nombre;
    }
    public void setEdad(int edad) {
        if (edad < 0 || edad > 100) {
            throw new RunTimeException("Edad invalida.");
        }
        this.edad = edad;
    }
    public void setSexo(char sexo) {
        if (sexo == 'M' || sexo == 'F') {
            this.sexo = sexo;
        } else {
            throw new RunTimeException("Sexo invalido (M o F)");
        }
    }
    public void setEps(String eps) {
        this.eps = eps;
    }
    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
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
        return "Paciente {" +
                "id =" + id +
                ", nombre ='" + nombre + '\'' +
                ", edad = " + edad + 
                ", EPS = " + EPS +
                ", nivelTriage =" + nivelTriage +
                ", fechaIngreso =" + fechaIngreso + ", a las " + horaIngreso +
                ", sintomas =" + sintomas +
                '}';
    }
    
}
