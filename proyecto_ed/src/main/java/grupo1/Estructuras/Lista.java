package grupo1.Estructuras;

import grupo1.Clases.Paciente;

/**
 * Lista enlazada simple con punteros a cabeza y cola.
 *
 * Se usa como cola FIFO por nivel de triage:
 * - inserta al final en O(1)
 * - extrae al inicio en O(1)
 */
public class Lista {

    // Primer nodo de la lista.
    private Nodo head;

    // Ultimo nodo de la lista.
    private Nodo tail;

    // Cantidad de elementos de la lista.
    private int size;

    /**
     * Construye una lista inicialmente vacia.
     */
    public Lista() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Inserta un paciente al final de la lista (FIFO).
     *
     * Complejidad: O(1)
     *
     * @param paciente paciente a encolar en esta lista.
     */
    public void encolar(Paciente paciente) {
        Nodo nuevo = new Nodo(paciente);

        if (head == null) {
            // Si estaba vacia, el nuevo nodo es cabeza y cola.
            head = nuevo;
            tail = nuevo;
        } else {
            // Si no estaba vacia, enlazamos al final y movemos tail.
            tail.setSiguiente(nuevo);
            tail = nuevo;
        }

        size++;
    }

    /**
     * Extrae y retorna el paciente al inicio de la lista.
     *
     * Complejidad: O(1)
     *
     * @return paciente atendido o null si la lista esta vacia.
     */
    public Paciente desencolar() {
        if (head == null) {
            return null;
        }

        Paciente paciente = head.getDato();
        head = head.getSiguiente();

        // Si al desencolar quedo vacia, tail tambien debe ser null.
        if (head == null) {
            tail = null;
        }

        size--;
        return paciente;
    }

    /**
     * Retorna el paciente al inicio sin extraer.
     *
     * Complejidad: O(1)
     *
     * @return primer paciente o null si esta vacia.
     */
    public Paciente frente() {
        if (head == null) {
            return null;
        }
        return head.getDato();
    }

    /**
     * Indica si la lista esta vacia.
     *
     * @return true si no hay nodos.
     */
    public boolean estaVacia() {
        return head == null;
    }

    /**
     * Retorna la cantidad de pacientes de esta lista.
     *
     * @return tamano actual.
     */
    public int size() {
        return size;
    }

    /**
     * Copia hasta max elementos de la lista al arreglo destino
     * sin modificar el contenido de la cola.
     */
    public int copiarPrimeros(Paciente[] destino, int desde, int max) {
        if (destino == null || max <= 0 || desde < 0 || desde >= destino.length) {
            return 0;
        }

        int limite = Math.min(max, destino.length - desde);
        int copiados = 0;
        Nodo actual = head;

        while (actual != null && copiados < limite) {
            destino[desde + copiados] = actual.getDato();
            copiados++;
            actual = actual.getSiguiente();
        }

        return copiados;
    }
}
