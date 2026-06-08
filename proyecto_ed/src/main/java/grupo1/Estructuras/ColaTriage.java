package grupo1.Estructuras;

import grupo1.Clases.Paciente;

/**
 * Cola de prioridad con Heap
 */
public class ColaTriage {

    // Niveles triage
    public static final int TRIAGE_MIN = 1;
    public static final int TRIAGE_MAX = 5;

    // En vez de arreglos de lsitas se usan heap
    private Heap heap;

    public ColaTriage() {
        this.heap = new Heap();
    }

    /**
     * Inserta un paciente usando el Heap.
     */
    public void insertarPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser null.");
        }
        int nivel = paciente.getNivelTriage().intValue();
        validarNivelTriage(nivel);

        heap.insertar(paciente);
    }

    /**
     * retorna el paciente que tenga mayor prioridad
     */
    public Paciente atenderPaciente() {
        return heap.extraer();
    }

    /**
     * Ve el sisguiente pacietne en cola
     */
    public Paciente verSiguientePaciente() {
        return heap.frente();
    }

    // Retorna la cantidad total de pacientes
    public int totalPacientes() {
        return heap.tam();
    }

    // Retorna la cantidad de pacientes en un nivel de triage especifico
    public int pacientesPorNivel(int nivel) {
        validarNivelTriage(nivel);
        int contador = 0;

        Paciente[] pacientes = heap.obtenerArregloInterno();
        int total = heap.tam();

        for (int i = 0; i < total; i++) {
            if (pacientes[i] != null && pacientes[i].getNivelTriage() == nivel) {
                contador++;
            }
        }
        return contador;
    }

    // Indica si la estructura completa esta vacia
    public boolean estaVacia() {
        return heap.vacio();
    }

    // Valida que el nivel de triage este en el rango permitido

    private void validarNivelTriage(int nivel) {
        if (nivel < TRIAGE_MIN || nivel > TRIAGE_MAX) {
            throw new IllegalArgumentException(
                    "Nivel de triage invalido: " + nivel + ". Debe estar entre 1 y 5.");
        }
    }

    /**
     * Retorna hasta cantidad pacientes en el orden de atencion esperados
     */
    public Paciente[] obtenerSiguientesPacientes(int cantidad) {
        Paciente[] resultado = heap.obtenerSiguientesPacientes(cantidad);
        return resultado;
    }
}
