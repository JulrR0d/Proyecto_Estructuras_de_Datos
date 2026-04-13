package grupo1.Estructuras;

import grupo1.Clases.Paciente;

/**
 * Arbol AVL para almacenar pacientes por su ID.
 *
 * Complejidad:
 * - Insercion, eliminacion, busqueda: O(log n)
 * - Recorrido inorden: O(n)
 * 
 * Primera version antes de ver AVL en clase. Basado del BST del libro de Streib
 * y en este repositorio de "surajsubramanian" que hace una implementacion del
 * AVL en Java https://github.com/surajsubramanian/AVL-Trees
 */

public class ArbolAVL {

    // NODO INTERNO
    private class Nodo {
        long id; // ID del paciente
        Paciente valor;
        Nodo izq, der;
        int altura;

        // Complejidad: O(1)
        Nodo(long id, Paciente valor) {
            this.id = id;
            this.valor = valor;
            this.altura = 1;
        }
    }

    private Nodo raiz;
    private int tam;

    // Complejidad: O(1)
    public ArbolAVL() {
        raiz = null;
        tam = 0;
    }

    // Altura de un nodo, 0 si es null. Complejidad: O(1)
    private int altura(Nodo n) {
        return n == null ? 0 : n.altura;
    }

    // Actualiza la altura desde los hijos. Complejidad: O(1)
    private void actualizarAltura(Nodo n) {
        n.altura = 1 + Math.max(altura(n.izq), altura(n.der));
    }

    // Factor de balance: altura(izq) - altura(der). Complejidad: O(1)
    private int balance(Nodo n) {
        return n == null ? 0 : altura(n.izq) - altura(n.der);
    }

    // Rotacion simple a la derecha (caso LL). Complejidad: O(1)
    private Nodo rotDer(Nodo a) {
        Nodo b = a.izq, t2 = b.der;
        b.der = a;
        a.izq = t2;
        actualizarAltura(a);
        actualizarAltura(b);
        return b;
    }

    // Rotacion simple a la izquierda (caso RR). Complejidad: O(1)
    private Nodo rotIzq(Nodo a) {
        Nodo b = a.der, t2 = b.izq;
        b.izq = a;
        a.der = t2;
        actualizarAltura(a);
        actualizarAltura(b);
        return b;
    }

    // Rebalancea el nodo aplicando la rotacion correcta. Complejidad: O(1)
    private Nodo rebalancear(Nodo n) {
        actualizarAltura(n);
        int bf = balance(n);
        if (bf > 1) {
            if (balance(n.izq) < 0)
                n.izq = rotIzq(n.izq); // Caso LR
            return rotDer(n);
        }
        if (bf < -1) {
            if (balance(n.der) > 0)
                n.der = rotDer(n.der); // Caso RL
            return rotIzq(n);
        }
        return n;
    }

    // Inserta o reemplaza un paciente por su ID. Complejidad: O(log n)
    public void insertar(long id, Paciente p) {
        boolean[] nuevo = { false };
        raiz = insertar(raiz, id, p, nuevo);
        if (nuevo[0])
            tam++;
    }

    private Nodo insertar(Nodo n, long id, Paciente p, boolean[] nuevo) {
        if (n == null) {
            nuevo[0] = true;
            return new Nodo(id, p);
        }
        if (id < n.id)
            n.izq = insertar(n.izq, id, p, nuevo);
        else if (id > n.id)
            n.der = insertar(n.der, id, p, nuevo);
        else
            n.valor = p; // duplicado: reemplaza valor
        return rebalancear(n);
    }

    // Elimina el nodo con la id dada. Retorna el paciente o null. Complejidad:
    // O(log n)
    public Paciente eliminar(long id) {
        Paciente[] ret = new Paciente[1];
        raiz = eliminar(raiz, id, ret);
        if (ret[0] != null)
            tam--;
        return ret[0];
    }

    private Nodo eliminar(Nodo n, long id, Paciente[] ret) {
        if (n == null)
            return null;
        if (id < n.id)
            n.izq = eliminar(n.izq, id, ret);
        else if (id > n.id)
            n.der = eliminar(n.der, id, ret);
        else {
            ret[0] = n.valor;
            if (n.izq == null)
                return n.der;
            if (n.der == null)
                return n.izq;
            // Dos hijos: reemplazar con el sucesor inorden (minimo del subarbol derecho)
            Nodo suc = minNodo(n.der);
            n.id = suc.id;
            n.valor = suc.valor;
            Paciente[] dummy = new Paciente[1];
            n.der = eliminar(n.der, suc.id, dummy);
        }
        return rebalancear(n);
    }

    // Busca un paciente por ID. Retorna null si no existe. Complejidad: O(log n)
    public Paciente buscar(long id) {
        Nodo n = raiz;
        while (n != null) {
            if (id < n.id)
                n = n.izq;
            else if (id > n.id)
                n = n.der;
            else
                return n.valor;
        }
        return null;
    }

    // Retorna el nodo mas a la izquierda (minimo). Complejidad: O(log n)
    private Nodo minNodo(Nodo n) {
        while (n.izq != null)
            n = n.izq;
        return n;
    }

    // Recorrido inorden: imprime pacientes en orden ascendente de ID. Complejidad:
    // O(n)
    public void recorridoInorden() {
        System.out.print("AVL inorden (por ID):");
        if (raiz == null) {
            System.out.println(" Vacio");
            return;
        }
        inorden(raiz);
        System.out.println();
    }

    private void inorden(Nodo n) {
        if (n.izq != null)
            inorden(n.izq);
        System.out.print(" [" + n.id + ":" + n.valor.getNombre() + "]");
        if (n.der != null)
            inorden(n.der);
    }

    // Complejidad: O(1)
    public int tam() {
        return tam;
    }

    public boolean vacio() {
        return raiz == null;
    }

    public int altura() {
        return altura(raiz);
    }
    
    // Retorna arreglo con los IDs visitados al buscar, en orden de visita
    public long[] obtenerCaminoBusqueda(long id) {
        long[] camino = new long[altura() + 1];
        int i = 0;
        Nodo n = raiz;
        while (n != null) {
            camino[i++] = n.id;
            if (id == n.id) break;
            n = id < n.id ? n.izq : n.der;
        }
        // Recortar al tamaño real
        long[] recortado = new long[i];
        for (int j = 0; j < i; j++) recortado[j] = camino[j];
        return recortado;
    }

    /** 
     * Llena el arreglo destino con los nodos en inorden, retorna cuántos llenó
     *  Se usa para que AVLPanel sepa qué nodos existen y en qué orden
    */ 

    public int volcarInorden(Paciente[] destino, int maxSize) {
        int[] idx = {0};
        volcarInorden(raiz, destino, idx, maxSize);
        return idx[0];
    }
    private void volcarInorden(Nodo n, Paciente[] destino, int[] idx, int max) {
        if (n == null || idx[0] >= max) return;
        volcarInorden(n.izq, destino, idx, max);
        destino[idx[0]++] = n.valor;
        volcarInorden(n.der, destino, idx, max);
    }

    // Snapshot plano de un nodo para que AVLPanel dibuje sin acceder a Nodo interno
    public static class NodoVista {
        public final long id;
        public final String nombre;
        public final int nivel;       // profundidad, raiz = 0
        public final int pos;         // posicion inorden (0,1,2...)
        public final long idPadre;    // -1 si es raiz
        public final int balance;

        public NodoVista(long id, String nombre, int nivel, int pos, long idPadre, int balance) {
            this.id = id; this.nombre = nombre; this.nivel = nivel;
            this.pos = pos; this.idPadre = idPadre; this.balance = balance;
        }
    }

    // Llena arreglo de NodoVista en inorden, retorna cuántos nodos hay
    public int obtenerVistas(NodoVista[] destino) {
        int[] contador = {0};
        llenarVistas(raiz, 0, -1L, destino, contador);
        return contador[0];
    }

    private void llenarVistas(Nodo n, int nivel, long idPadre, NodoVista[] dest, int[] contador) {
        if (n == null || contador[0] >= dest.length) return;
        llenarVistas(n.izq, nivel + 1, n.id, dest, contador);
        dest[contador[0]] = new NodoVista(
            n.id, n.valor.getNombre(), nivel,
            contador[0],              // pos inorden = índice actual
            idPadre, balance(n)
        );
        contador[0]++;
        llenarVistas(n.der, nivel + 1, n.id, dest, contador);
    }
}