package grupo1.Estructuras;

import grupo1.Clases.Paciente;

/**
 * Arbol AVL para almacenar referencias de pacientes por su ID.
 *
 * Complejidad:
 * - Insercion, eliminacion, busqueda: O(log n)
 * - Recorrido inorden: O(n)
 * 
 * Segunda version despues de estudiar los temas de AVL con el contenido del
 * cronograma. Se organiza mejor y se comenta a fondo los metodos, de igual
 * manera se explica las pequeñas diferencias con un arbol AVL comun y
 * corriente (los arreglos en insercion y eliminacion) teniendo en cuenta el
 * sistema de triage en mente, de igual manera
 * se puede utilizar en otros contextos
 */

public class ArbolAVL {

    // NODO INTERNO, privado
    private class Nodo {
        long id; // ID del paciente
        Paciente valor;
        Nodo izq, der;
        int altura;

        // Complejidad: O(1)
        Nodo(long id, Paciente valor) {
            this.id = id; // El ID se usa como clave para ordenar el arbol
            this.valor = valor; // El valor es el paciente asociado a ese ID, esto es la referencia mas no una
                                // copia del paciente
            this.altura = 1; // Altura inicial de un nodo hoja es 1 para facilitar calculos de balance
                             // factor. Un nodo null se considera altura 0.
        }
    }

    private Nodo raiz; // Raiz del arbol AVL
    private int tam; // Cantidad de nodos en el arbol AVL

    // Complejidad: O(1)
    public ArbolAVL() {
        raiz = null; // El arbol inicialmente esta vacio
        tam = 0;
    }

    // Altura de un nodo, 0 si es null. Complejidad: O(1)
    private int altura(Nodo n) {
        return n == null ? 0 : n.altura;
    }

    // Actualiza la altura desde los hijos. Complejidad: O(1)
    private void actualizarAltura(Nodo n) {
        n.altura = 1 + Math.max(altura(n.izq), altura(n.der)); // La altura de un nodo es 1 + la altura del hijo mas
                                                               // alto
    }

    // Factor de balance: altura(izq) - altura(der). Complejidad: O(1)
    private int balance(Nodo n) {
        return n == null ? 0 : altura(n.izq) - altura(n.der);
    }

    // Rotacion simple a la derecha (caso LL). Complejidad: O(1)
    private Nodo rotDer(Nodo a) {
        Nodo b = a.izq; // El hijo izquierdo se convierte en la nueva raiz de este subarbol
        Nodo t = b.der; // El hijo derecho de b se convierte en el hijo izquierdo de a

        b.der = a; // a se convierte en el hijo derecho de b
        a.izq = t; // El hijo derecho de b que es t, se convierte en el hijo izquierdo de a

        actualizarAltura(a); // Se actualiza la altura de a antes de b porque a ahora es hijo de b
        actualizarAltura(b); // Se actualiza la altura de b la nueva raiz del subarbol

        return b; // Se retorna la nueva raiz del subarbol despues de la rotacion
    }

    // Rotacion simple a la izquierda (caso RR). Complejidad: O(1)
    private Nodo rotIzq(Nodo a) {
        Nodo b = a.der; // El hijo derecho se convierte en la nueva raiz de este subarbol
        Nodo t = b.izq; // El hijo izquierdo de b se convierte en el hijo derecho de a

        b.izq = a; // a se convierte en el hijo izquierdo de b
        a.der = t; // El hijo izquierdo de b que es t, se convierte en el hijo derecho de a

        actualizarAltura(a); // Se actualiza la altura de a antes de b porque a ahora es hijo de b
        actualizarAltura(b); // Se actualiza la altura de b la nueva raiz del subarbol

        return b; // Se retorna la nueva raiz del subarbol despues de la rotacion
    }

    // Rebalancea el nodo aplicando la rotacion correcta. Complejidad: O(1)
    private Nodo rebalancear(Nodo n) {

        actualizarAltura(n); // Se actualiza la altura antes de calcular el balance factor porque puede haber
                             // cambiado por la insercion o eliminacion en los hijos

        int bf = balance(n); // Balance factor del nodo n

        if (bf > 1) {
            if (balance(n.izq) < 0) // Se mira nuevamente el balance del hijo izquierdo para decidir rotacion porque
                                    // puede ser LR o LL
                n.izq = rotIzq(n.izq); // Caso LR
            return rotDer(n); // Caso LL
        }

        if (bf < -1) {
            if (balance(n.der) > 0) // Se mira nuevamente el balance del hijo derecho para decidir rotacion porque
                                    // puede ser RL o RR
                n.der = rotDer(n.der); // Caso RL
            return rotIzq(n); // Caso RR
        }

        return n;
    }

    // Inserta o reemplaza un paciente por su ID. Complejidad: O(log n)
    public void insertar(long id, Paciente p) {
        boolean[] nuevo = { false }; // Arreglo de booleano para indicar si se inserto un nuevo nodo o se reemplazo
                                     // uno existente. Necesario porque el metodo recursivo no puede retornar ambos
                                     // el nodo raiz y si se inserto o no, se usa arreglo por el paso por referencia.

        raiz = insertar(raiz, id, p, nuevo); // Llama al metodo recursivo para insertar el paciente en el arbol AVL,
                                             // actualizando la raiz del arbol despues de la insercion y rebalanceo

        if (nuevo[0]) // Si se inserto un nuevo nodo, se incrementa el tamaño del arbol. Ya que puede
                      // que solo se haya reemplazado el paciente de un nodo existente, en ese caso no
                      // se incrementa el tamaño.
            tam++;
    }

    private Nodo insertar(Nodo n, long id, Paciente p, boolean[] nuevo) {
        if (n == null) { // Si se llega a un nodo null, se inserta el nuevo paciente aqui
            nuevo[0] = true; // Se marca que se inserto un nuevo nodo
            return new Nodo(id, p); // Se crea un nuevo nodo con el ID y paciente dado, este nodo es una hoja con
                                    // altura 1
        }

        if (id < n.id) {// Si el ID a insertar es menor que el ID del nodo actual, se inserta
                        // recursivamente en el subarbol izquierdo
            n.izq = insertar(n.izq, id, p, nuevo);
        } else if (id > n.id) { // Si el ID a insertar es mayor que el ID del nodo actual, se inserta
                                // recursivamente en el subarbol derecho
            n.der = insertar(n.der, id, p, nuevo);
        } else {
            n.valor = p; // Es el mismo ID, se reemplaza el paciente del nodo actual por el nuevo
                         // paciente dado.
        }

        return rebalancear(n); // Se rebalancea el nodo actual despues de la insercion recursiva en alguno de
                               // sus hijos, y se retorna el nodo
                               // raiz del subarbol resultante despues de la insercion y rebalanceo.
    }

    // Elimina el nodo con la id dada. Retorna el paciente o null. Complejidad:
    // O(log n)
    public Paciente eliminar(long id) {
        Paciente[] ret = new Paciente[1]; // Arreglo de paciente para retornar el paciente eliminado, necesario por el
                                          // paso por referencia en la recursividad del metodo eliminar.

        raiz = eliminar(raiz, id, ret); // Llama al metodo recursivo para eliminar el nodo con el ID dado, actualizando
                                        // la raiz del arbol despues de la eliminacion y rebalanceo. El paciente
                                        // eliminado se guarda en ret[0]

        if (ret[0] != null) // Si se elimino un nodo, se decrementa el tamaño del arbol.
            tam--;
        return ret[0]; // Se retorna el paciente eliminado, o null si no se encontro
    }

    private Nodo eliminar(Nodo n, long id, Paciente[] ret) {
        if (n == null) {// Si se llega a un nodo null, no se encontro el ID a eliminar, se retorna null
            return null;
        }

        if (id < n.id) { // Si el ID a eliminar es menor que el ID del nodo actual, se elimina
                         // recursivamente en el subarbol izquierdo

            n.izq = eliminar(n.izq, id, ret);

        } else if (id > n.id) { // Si el ID a eliminar es mayor que el ID del nodo actual, se elimina
                                // recursivamente en el subarbol derecho
            n.der = eliminar(n.der, id, ret);

        } else {
            ret[0] = n.valor; // Es el nodo a eliminar, se guarda el paciente del nodo actual en ret[0] para
                              // retornar despues

            // Caso con 0 o 1 hijo, se reemplaza el nodo actual por su unico hijo o por null
            // si no tiene hijos

            if (n.izq == null) {
                return n.der;
            }

            if (n.der == null) {
                return n.izq;
            }

            // Caso con dos hijos, reemplazar con el sucesor inorden qie es el minimo
            // del subarbol derecho

            Nodo suc = minNodo(n.der);
            n.id = suc.id; // Se copia el ID del sucesor al nodo actual
            Paciente[] eliminadoExtra = new Paciente[1]; // Arreglo para almacenar el paciente eliminado pero este
                                                         // paciente no se retorna, es solo para eliminar el nodo del
                                                         // sucesor que se va a mover al lugar del nodo actual

            // Este eliminadoExtra es necesario para no sobrescribir el paciente eliminado
            // que se va a retornar en ret[0] al eliminar el nodo actual

            n.der = eliminar(n.der, suc.id, eliminadoExtra);
            // Se elimina el nodo del sucesor en el subarbol derecho,
            // ya que ese nodo se va a mover al lugar del nodo actual.
            // El paciente eliminado de esta eliminacion no se retorna
            // porque es el mismo paciente que ya se guardo en ret[0]
            // al eliminar el nodo actual, este paso es solo para
            // eliminar el nodo del sucesor que se va a mover al lugar
            // del nodo actual.

        }
        return rebalancear(n); // Se rebalancea el nodo actual despues de la eliminacion recursiva en alguno de
                               // sus hijos, y se retorna el nodo
                               // raiz del subarbol resultante despues de la eliminacion y rebalanceo.
    }

    // Busca un paciente por ID. Se retorna null si no existe. Complejidad: O(log n)
    public Paciente buscar(long id) {
        Nodo n = raiz;

        // Busqueda igual que en un arbol BST
        while (n != null) {
            if (id < n.id) {
                n = n.izq;
            } else if (id > n.id) {
                n = n.der;
            } else {
                return n.valor;
            }
        }
        return null;
    }

    // Retorna el nodo mas a la izquierda que es el minimo. Complejidad: O(log n)
    private Nodo minNodo(Nodo n) {
        while (n.izq != null)
            n = n.izq;
        return n;
    }

    // Recorrido inorden, este imprime pacientes en orden ascendente de ID.
    // Complejidad: O(n)
    public void recorridoInorden() {
        System.out.println("AVL inorden (por ID):");
        if (raiz == null) {
            System.out.println("Vacio");
            return;
        }
        inorden(raiz);
        System.out.println();
    }

    private void inorden(Nodo n) {
        if (n.izq != null) {
            inorden(n.izq);
        }

        System.out.print(" [" + n.id + ":" + n.valor.getNombre() + "]");

        if (n.der != null) {
            inorden(n.der);
        }
    }

    // Los siguientes son de complejidad O(1)

    // Cantidad de nodos en el arbol AVL
    public int tam() {
        return tam;
    }

    // Vacio o no
    public boolean vacio() {
        return raiz == null;
    }

    // Altura del arbol AVL
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