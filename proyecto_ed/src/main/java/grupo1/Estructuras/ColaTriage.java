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
    private heap heap;

    public ColaTriage() {
        this.heap = new heap();
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
        return heap.extraerMaximo();
    }

    /**
     * Ve el sisguiente pacietne en cola
     */
    public Paciente verSiguientePaciente() {
        return heap.frente();
    }

    /**
     * Retorna hasta cantidad pacientes en el orden de atencion esperados
     */
    public Paciente[] obtenerSiguientesPacientes(int cantidad) {
        int total = heap.tam();
        if (cantidad <= 0 || total == 0) {
            return new Paciente[0];
        }

        int limite = Math.min(cantidad, total);
        Paciente[] resultado = new Paciente[limite];
        
        //se extraen los elementos del heap actual a un arreglo pero temporal
        Paciente[] copia = heap.obtenerArray();
        for (int i = 0; i < limite; i++) {
            resultado[i] = copia[i];
        }

            // se ordena el temparray 
        for (int i = 1; i < limite; i++) {
            Paciente clave = resultado[i];
            int j = i - 1;

            while (j >= 0 && debeIrAntes(clave, resultado[j])) {
                resultado[j + 1] = resultado[j];
                j--;
            }
            resultado[j + 1] = clave;
        }

        return resultado;
    }

    // Función auxiliar para el ordenamiento de la GUI
    private boolean debeIrAntes(Paciente a, Paciente b) {
        if (a == null) return false;
        if (b == null) return true;
        
        if (a.getNivelTriage() < b.getNivelTriage()) return true;//comparamos nivel triage
        if (a.getNivelTriage().equals(b.getNivelTriage())) {
            if (a.getFechaIngreso().isBefore(b.getFechaIngreso())) return true;//if nivel triage es igual --> comaparamos fecha
            if (a.getFechaIngreso().isEqual(b.getFechaIngreso())) {
                return a.getHoraIngreso().isBefore(b.getHoraIngreso());// else misma fecha --> comparamos la Hora
            }
        }
        return false;
    }

    //Retorna la cantidad total de pacientes
    public int totalPacientes() {
        return heap.tam();
    }

    //Retorna la cantidad de pacientes en un nivel de triage especifico
    public int pacientesPorNivel(int nivel) {
        validarNivelTriage(nivel);
        int contador = 0;
        
        Paciente[] pacientes = heap.obtenerArray();
        int total = heap.tam();
        
        for (int i = 0; i < total; i++) {
            if (pacientes[i] != null && pacientes[i].getNivelTriage() == nivel) {
                contador++;
            }
        }
        return contador;
    }

    //Indica si la estructura completa esta vacia
    public boolean estaVacia() {
        return heap.vacio();
    }

    //Valida que el nivel de triage este en el rango permitido

    private void validarNivelTriage(int nivel) {
        if (nivel < TRIAGE_MIN || nivel > TRIAGE_MAX) {
            throw new IllegalArgumentException(
                    "Nivel de triage invalido: " + nivel + ". Debe estar entre 1 y 5.");
        }
    }
}
