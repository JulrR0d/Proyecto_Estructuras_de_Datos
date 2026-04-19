package grupo1.Estructuras;

import grupo1.Clases.Paciente;

import grupo1.Features.RegistroCSV;
/**
 * Cola de prioridad por buckets para triage medico.
 *
 * Diseno:
 * - 5 listas enlazadas (una por cada nivel de triage 1..5)
 * - cada lista mantiene orden FIFO de llegada
 * - para atender, se busca desde nivel 1 hasta 5
 *
 * Importante:
 * El escaneo 1..5 es constante porque el numero de niveles de triage
 * es fijo y pequeno. Por eso atender tambien es O(1) en este problema.
 */
public class ColaTriage {

    // Niveles validos de triage en el sistema.
    public static final int TRIAGE_MIN = 1;
    public static final int TRIAGE_MAX = 5;

    // Arreglo de listas (bucket 0 => triage 1, ..., bucket 4 => triage 5).
    private final Lista[] buckets;

    // Cantidad total de pacientes en todas las listas.
    private int totalPacientes;

    // Pila de historial: registra cada paciente atendido en orden LIFO.
    private final Pila historialAtenciones;
    /**
     * Construye la cola de triage con 5 listas vacias.
     */
    private final RegistroCSV registro = new RegistroCSV();
    /**
    *    creacion del objeto para poder escribir el archivo csv
    */
    public ColaTriage() {
        buckets = new Lista[TRIAGE_MAX];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new Lista();
        }
        totalPacientes = 0;
        historialAtenciones = new Pila();
    }

    /**
     * Inserta un paciente en el bucket correspondiente a su nivel de triage.
     *
     * Complejidad: O(1)
     *
     * @param paciente paciente a registrar.
     */
    public void insertarPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser null.");
        }

        int nivel = paciente.getNivelTriage();
        validarNivelTriage(nivel);

        // Mapeo 1..5 -> 0..4
        buckets[nivel - 1].encolar(paciente);
        totalPacientes++;
    }

    /**
     * Atiende y retorna el paciente de mayor prioridad disponible.
     *
     * Reglas:
     * 1) Menor nivel de triage sale primero (1 antes que 5)
     * 2) Si hay empate de nivel, se respeta FIFO por lista
     *
     * Complejidad: O(1) en este contexto (maximo 5 buckets a revisar)
     *
     * @return paciente atendido o null si no hay pacientes.
     */
  public Paciente atenderPaciente() {
    for (int nivel = TRIAGE_MIN; nivel <= TRIAGE_MAX; nivel++) {
        Lista lista = buckets[nivel - 1];
        if (!lista.estaVacia()) {
            totalPacientes--;
            Paciente atendido = lista.desencolar();
            historialAtenciones.push(atendido); // registro LIFO
            registro.registrarAtencion(atendido); //anota el paciente en le csv
            return atendido;
        }
    }
    return null;
}

    /**
     * Consulta el siguiente paciente a atender sin extraerlo.
     *
     * Complejidad: O(1) en este contexto (5 buckets fijos)
     *
     * @return siguiente paciente o null si no hay pacientes.
     */
    public Paciente verSiguientePaciente() {
        for (int nivel = TRIAGE_MIN; nivel <= TRIAGE_MAX; nivel++) {
            Lista lista = buckets[nivel - 1];
            if (!lista.estaVacia()) {
                return lista.frente();
            }
        }

        return null;
    }

    /**
     * Retorna hasta cantidad pacientes en el orden real de atencion
     * sin retirarlos de la estructura.
     */
    public Paciente[] obtenerSiguientesPacientes(int cantidad) {
        if (cantidad <= 0 || totalPacientes == 0) {
            return new Paciente[0];
        }

        Paciente[] resultado = new Paciente[Math.min(cantidad, totalPacientes)];
        int indice = 0;

        for (int nivel = TRIAGE_MIN; nivel <= TRIAGE_MAX && indice < resultado.length; nivel++) {
            Lista lista = buckets[nivel - 1];
            int copiados = lista.copiarPrimeros(resultado, indice, resultado.length - indice);
            indice += copiados;
        }

        return resultado;
    }

    /**
     * Retorna la cantidad total de pacientes.
     *
     * @return total en la cola de triage.
     */
    public int totalPacientes() {
        return totalPacientes;
    }

    /**
     * Retorna la cantidad de pacientes en un nivel de triage especifico.
     *
     * @param nivel nivel de triage (1..5)
     * @return cantidad de pacientes en ese nivel.
     */
    public int pacientesPorNivel(int nivel) {
        validarNivelTriage(nivel);
        return buckets[nivel - 1].size();
    }

    /**
     * Indica si la estructura completa esta vacia.
     *
     * @return true si no hay pacientes.
     */
    public boolean estaVacia() {
        return totalPacientes == 0;
    }

    /**
     * Valida que el nivel de triage este en el rango permitido.
     */
    private void validarNivelTriage(int nivel) {
        if (nivel < TRIAGE_MIN || nivel > TRIAGE_MAX) {
            throw new IllegalArgumentException(
                "Nivel de triage invalido: " + nivel + ". Debe estar entre 1 y 5."
            );
        }
    }

    public boolean deshacerUltimaAtencion() {
        Paciente p = historialAtenciones.pop();
        if (p == null) return false;
        insertarPaciente(p);
        return true;
    }

    // Retorna historial de atenciones en orden LIFO (mas reciente primero).
    public Paciente[] getHistorialAtenciones() {
        return historialAtenciones.obtenerHistorial();
    }

    // Indica si hay historial disponible.
    public boolean hayHistorial() {
        return !historialAtenciones.vacia();
    }

    // Cuantos pacientes han sido atendidos en total.
    public int totalAtendidos() {
        return historialAtenciones.tam();
    }

    // Expone la pila para la GUI.
    public Pila getPila() {
        return historialAtenciones;
    }
}
