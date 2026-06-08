package grupo1.Estructuras;

import grupo1.Clases.Paciente;
import java.time.LocalTime;

/**
 * Montículo de máxima prioridad para despacho de pacientes en triage.
 *
 * Criterio de prioridad:
 *   1. Menor nivel de triage primero (1 es más urgente que 5)
 *   2. En empate de nivel, menor hora de ingreso primero (llegó antes)
 *
 * Implementación con arreglo dinámico que duplica capacidad al llenarse.
 *
 * Complejidad:
 *   - insertar:   O(log n)
 *   - extraer:    O(log n)  [debe restaurar propiedad del heap]
 *   - top:        O(1)
 */
public class HeapTriage {

    private static final int CAPACIDAD_INICIAL = 64;

    private Paciente[] datos;
    private int tam;

    public HeapTriage() {
        datos = new Paciente[CAPACIDAD_INICIAL];
        tam = 0;
    }

    // Devuelve true si a tiene mayor prioridad que b.
    // Mayor prioridad = triage más bajo; desempate por hora de ingreso más temprana.
    private boolean mayorPrioridad(Paciente a, Paciente b) {
        int ta = a.getNivelTriage();
        int tb = b.getNivelTriage();
        if (ta != tb) return ta < tb;
        // mismo nivel: el que llegó antes
        LocalTime ha = a.getHoraIngreso();
        LocalTime hb = b.getHoraIngreso();
        return ha.isBefore(hb);
    }

    private int padre(int i)  { return (i - 1) / 2; }
    private int izq(int i)    { return 2 * i + 1; }
    private int der(int i)    { return 2 * i + 2; }

    private void swap(int i, int j) {
        Paciente tmp = datos[i];
        datos[i] = datos[j];
        datos[j] = tmp;
    }

    // Sube el nodo en i hasta que la propiedad del heap se restaure. O(log n)
    private void siftUp(int i) {
        while (i > 0 && mayorPrioridad(datos[i], datos[padre(i)])) {
            swap(i, padre(i));
            i = padre(i);
        }
    }

    // Baja el nodo en i hasta que la propiedad del heap se restaure. O(log n)
    private void siftDown(int i) {
        while (true) {
            int mayor = i;
            int l = izq(i);
            int r = der(i);

            if (l < tam && mayorPrioridad(datos[l], datos[mayor])) mayor = l;
            if (r < tam && mayorPrioridad(datos[r], datos[mayor])) mayor = r;

            if (mayor == i) break;
            swap(i, mayor);
            i = mayor;
        }
    }

    /**
     * Inserta un paciente en el heap. Complejidad: O(log n)
     */
    public void insertar(Paciente paciente) {
        if (paciente == null) throw new IllegalArgumentException("El paciente no puede ser null.");
        if (tam == datos.length) redimensionar();

        datos[tam] = paciente;
        siftUp(tam);
        tam++;
    }

    /**
     * Extrae y retorna el paciente de mayor prioridad. Complejidad: O(log n)
     * Retorna null si está vacío.
     */
    public Paciente extraer() {
        if (tam == 0) return null;

        Paciente prioritario = datos[0];
        tam--;
        datos[0] = datos[tam];
        datos[tam] = null;

        if (tam > 0) siftDown(0);

        return prioritario;
    }

    /**
     * Consulta el paciente de mayor prioridad sin extraerlo. Complejidad: O(1)
     */
    public Paciente top() {
        return tam == 0 ? null : datos[0];
    }

    /**
     * Retorna hasta cantidad pacientes en orden de prioridad sin modificar el heap.
     * Se hace una copia del arreglo y se extrae de ella. O(k log n)
     */
    public Paciente[] obtenerSiguientesPacientes(int cantidad) {
        if (cantidad <= 0 || tam == 0) return new Paciente[0];

        // Copia del arreglo para no tocar el heap real
        int n = Math.min(cantidad, tam);
        Paciente[] copia = new Paciente[tam];
        for (int i = 0; i < tam; i++) copia[i] = datos[i];

        Paciente[] resultado = new Paciente[n];
        int tamCopia = tam;

        for (int k = 0; k < n; k++) {
            resultado[k] = copia[0];
            tamCopia--;
            copia[0] = copia[tamCopia];
            copia[tamCopia] = null;
            // siftDown sobre la copia
            int i = 0;
            while (true) {
                int mayor = i;
                int l = 2 * i + 1;
                int r = 2 * i + 2;
                if (l < tamCopia && mayorPrioridad(copia[l], copia[mayor])) mayor = l;
                if (r < tamCopia && mayorPrioridad(copia[r], copia[mayor])) mayor = r;
                if (mayor == i) break;
                Paciente tmp = copia[i]; copia[i] = copia[mayor]; copia[mayor] = tmp;
                i = mayor;
            }
        }

        return resultado;
    }

    public boolean estaVacio() { return tam == 0; }

    public int totalPacientes() { return tam; }

    // Duplica la capacidad del arreglo interno. O(n)
    private void redimensionar() {
        Paciente[] nuevo = new Paciente[datos.length * 2];
        for (int i = 0; i < tam; i++) nuevo[i] = datos[i];
        datos = nuevo;
    }
}
