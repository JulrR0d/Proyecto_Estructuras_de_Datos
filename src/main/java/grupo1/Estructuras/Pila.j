package grupo1.Estructuras;

import grupo1.Clases.Paciente;

/**
 * Pila (LIFO) de pacientes atendidos.
 *
 * Usos en el sistema:
 * - Registrar historial de atenciones en orden inverso (mas reciente primero)
 * - Permitir deshacer la ultima atencion (reinsertar paciente en cola)
 *
 * Implementacion con arreglo dinamico que duplica capacidad cuando se llena.
 *
 * Complejidad:
 * - push, pop, peek: O(1) amortizado
 * - obtenerHistorial: O(n)
 */
public class Pila {

    private static final int CAPACIDAD_INICIAL = 64;

    private Paciente[] datos;
    private int tope; // -1 si vacia

    public Pila() {
        datos = new Paciente[CAPACIDAD_INICIAL];
        tope = -1;
    }

    // Apila un paciente. Complejidad amortizada: O(1)
    public void push(Paciente paciente) {
        if (paciente == null) return;
        if (tope + 1 == datos.length) redimensionar();
        datos[++tope] = paciente;
    }

    // Desapila y retorna el tope. Complejidad: O(1)
    public Paciente pop() {
        if (vacia()) return null;
        Paciente p = datos[tope];
        datos[tope--] = null;
        return p;
    }

    // Consulta el tope sin extraer. Complejidad: O(1)
    public Paciente peek() {
        return vacia() ? null : datos[tope];
    }

    public boolean vacia() { return tope == -1; }

    public int tam() { return tope + 1; }

    // Retorna arreglo con pacientes en orden LIFO (mas reciente primero).
    // No modifica la pila. Complejidad: O(n)
    public Paciente[] obtenerHistorial() {
        Paciente[] historial = new Paciente[tam()];
        for (int i = 0; i < historial.length; i++) {
            historial[i] = datos[tope - i];
        }
        return historial;
    }

    // Duplica capacidad. Complejidad: O(n)
    private void redimensionar() {
        Paciente[] nuevo = new Paciente[datos.length * 2];
        for (int i = 0; i < datos.length; i++) nuevo[i] = datos[i];
        datos = nuevo;
    }
}
