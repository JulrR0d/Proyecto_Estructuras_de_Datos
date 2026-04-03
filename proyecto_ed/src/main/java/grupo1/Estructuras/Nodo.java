package grupo1.Estructuras;

import grupo1.Clases.Paciente;

/**
 * Nodo simple para lista enlazada de pacientes.
 *
 * Esta clase se implementa manualmente para cumplir el requisito
 * de no usar estructuras predefinidas del lenguaje.
 */
public class Nodo {

    // Paciente almacenado en este nodo.
    private Paciente dato;

    // Referencia al siguiente nodo en la lista.
    private Nodo siguiente;

    /**
     * Construye un nodo con un paciente.
     *
     * @param dato paciente a almacenar en el nodo.
     */
    public Nodo(Paciente dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public Paciente getDato() {
        return dato;
    }

    public void setDato(Paciente dato) {
        this.dato = dato;
    }

    public Nodo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }
}
