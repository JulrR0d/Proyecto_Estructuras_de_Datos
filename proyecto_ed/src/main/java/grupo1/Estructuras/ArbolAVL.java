package grupo1.Estructuras;

import grupo1.Clases.Paciente;

/**
 * Arbol AVL para almacenar referencias de pacientes por su ID.
 *
 * Complejidad
 * - Insercion, eliminacion, busqueda O(log n)
 * - Recorrido inorden O(n)
 * 
 * Tercera version aplicando lo visto en clase en especial el quiz de AVL del
 * miercoles 15 de abril
 */

public class ArbolAVL {

    // NODO INTERNO, privado
    private class Nodo {
        long id; // ID del paciente
        Paciente valor;
        Nodo izq, der, padre;
        int altura;

        // Complejidad O(1)
        Nodo(long id, Paciente valor, Nodo padre) {
            this.id = id; // El ID se usa como clave para ordenar el arbol
            this.valor = valor; // El valor es el paciente asociado a ese ID, esto es la referencia mas no una
                                // copia del paciente
            this.padre = padre; // Referencia al nodo padre, null si es raiz
            this.altura = 1; // Altura inicial de un nodo hoja es 1 para facilitar calculos de balance
                             // factor. Un nodo null se considera altura 0.
        }
    }

    private Nodo raiz; // Raiz del arbol AVL
    private int tam; // Cantidad de nodos en el arbol AVL

    // Complejidad O(1)
    public ArbolAVL() {
        raiz = null; // El arbol inicialmente esta vacio
        tam = 0;
    }

    // Altura de un nodo, 0 si es null. Complejidad O(1)
    private int altura(Nodo n) {
        return n == null ? 0 : n.altura;
    }

    // Actualiza la altura desde los hijos. Complejidad O(1)
    private void actualizarAltura(Nodo n) {
        n.altura = 1 + Math.max(altura(n.izq), altura(n.der)); // La altura de un nodo es 1 + la altura del hijo mas
                                                               // alto
    }

    // Factor de balance altura(izq) - altura(der). Complejidad O(1)
    private int balance(Nodo n) {
        return n == null ? 0 : altura(n.izq) - altura(n.der);
    }

    // Rotacion simple a la derecha (caso LL).
    // Complejidad O(1)
    private Nodo rotDer(Nodo x) {
        Nodo p = x.padre; // Padre de x antes de la rotacion
        Nodo y = x.izq; // El hijo izquierdo se convierte en la nueva raiz de este subarbol
        Nodo b = y.der; // El hijo derecho de Y pasa a ser hijo izquierdo de X

        // Y sube, su padre pasa a ser el padre de X
        y.padre = p;
        // X baja su padre pasa a ser Y
        x.padre = y;
        y.der = x; // X se convierte en el hijo derecho de Y

        // B se reasigna como hijo izquierdo de X
        x.izq = b;
        if (b != null) {
            b.padre = x; // Se actualiza el padre de B si no es null
        }

        actualizarAltura(x); // Se actualiza la altura de X antes de Y porque X ahora es hijo de Y
        actualizarAltura(y); // Se actualiza la altura de Y la nueva raiz del subarbol

        // Si Y sube a la raiz, se actualiza la raiz del arbol
        if (p == null) {
            raiz = y;
        } else if (p.izq == x) {
            p.izq = y; // Y reemplaza a X como hijo izquierdo del padre
        } else {
            p.der = y; // Y reemplaza a X como hijo derecho del padre
        }

        return y; // Se retorna la nueva raiz del subarbol despues de la rotacion
    }

    // Rotacion simple a la izquierda (caso RR).
    private Nodo rotIzq(Nodo x) {
        Nodo p = x.padre; // Padre de x antes de la rotacion
        Nodo y = x.der; // El hijo derecho se convierte en la nueva raiz de este subarbol
        Nodo b = y.izq; // El hijo izquierdo de Y pasa a ser hijo derecho de X

        // Y sube su padre pasa a ser el padre de X
        y.padre = p;
        // X baja su padre pasa a ser Y
        x.padre = y;
        y.izq = x; // X se convierte en el hijo izquierdo de Y

        // B se reasigna como hijo derecho de X
        x.der = b;
        if (b != null) {
            b.padre = x; // Se actualiza el padre de B si no es null
        }

        actualizarAltura(x); // Se actualiza la altura de X antes de Y porque X ahora es hijo de Y
        actualizarAltura(y); // Se actualiza la altura de Y la nueva raiz del subarbol

        // Si Y sube a la raiz, se actualiza la raiz del arbol
        if (p == null) {
            raiz = y;
        } else if (p.izq == x) {
            p.izq = y; // Y reemplaza a X como hijo izquierdo del padre
        } else {
            p.der = y; // Y reemplaza a X como hijo derecho del padre
        }

        return y; // Se retorna la nueva raiz del subarbol despues de la rotacion
    }

    // Rebalancea el nodo n y luego sube recursivamente hacia la raiz por el padre
    private void rebalancear(Nodo n) {
        if (n == null)
            return;

        Nodo p = n.padre; // Se guarda el padre antes de cualquier rotacion porque puede cambiar

        if (altura(n.izq) > altura(n.der) + 1) {
            rebalancearDerecha(n);
        } else if (altura(n.der) > altura(n.izq) + 1) {
            rebalancearIzquierda(n);
        } else {
            // No hay desbalance solo se actualiza la altura
            actualizarAltura(n);
        }

        // Subir recursivamente hacia la raiz
        rebalancear(p);
    }

    // Si el hijo derecho de M es mas alto que el izquierdo,
    // rotar izquierda primero que es el caso LR
    // Luego rotar derecha sobre N. Complejidad O(1)
    private void rebalancearDerecha(Nodo n) {
        Nodo m = n.izq;
        if (altura(m.der) > altura(m.izq)) {
            // Caso LR el hijo derecho de M es mas alto, toca hacer rotacion izquierda
            // previa
            rotIzq(m);
        }
        // Cual sea el caso toca rotar derecha sobre N
        rotDer(n);
    }

    // Simetrico a rebalancearDerecha. Complejidad O(1)
    private void rebalancearIzquierda(Nodo n) {
        Nodo m = n.der;
        if (altura(m.izq) > altura(m.der)) {
            // Caso RL el hijo izquierdo de M es mas alto, toca hacer rotacion derecha
            // previa
            rotDer(m);
        }
        // Cual sea el caso toca rotar izquierda sobre N
        rotIzq(n);
    }

    // Inserta un paciente por su ID. Complejidad O(log n)
    public void insertar(long id, Paciente p) {
        // Se inserta el nodo en el BST normal y se obtiene el nodo insertado
        Nodo insertado = insertar(raiz, id, p, null);

        tam++; // Se incrementa el tamaño del arbol

        // Rebalancear desde el nodo insertado hacia la raiz
        rebalancear(insertado);
    }

    // Insercion del tipo BST. Complejidad O(log n)
    private Nodo insertar(Nodo n, long id, Paciente p, Nodo padre) {
        if (n == null) { // Si se llega a un nodo null, se inserta el nuevo paciente aqui
            Nodo creado = new Nodo(id, p, padre); // Se crea con referencia al padre
            // Se enlaza al padre segun corresponda
            if (padre == null) {
                raiz = creado; // Es la raiz
            } else if (id < padre.id) {
                padre.izq = creado;
            } else {
                padre.der = creado;
            }
            return creado; // Se retorna el nodo recien creado
        }

        if (id < n.id) { // Si el ID a insertar es menor que el ID del nodo actual, se inserta
                         // recursivamente en el subarbol izquierdo
            return insertar(n.izq, id, p, n);
        } else if (id > n.id) { // Si el ID a insertar es mayor que el ID del nodo actual, se inserta
                                // recursivamente en el subarbol derecho
            return insertar(n.der, id, p, n);
        } else {
            // ID duplicado, no se permite insertar. Se lanza excepcion para manejar como se
            // quiera
            throw new IllegalArgumentException("Ya existe un paciente con el ID " + id);
        }
    }

    // Elimina el nodo con la id dada. Retorna el paciente o null.
    // Complejidad O(log n)
    public Paciente eliminar(long id) {
        Paciente[] ret = new Paciente[1]; // Arreglo de paciente para retornar el paciente eliminado, necesario por el
                                          // paso por referencia en la recursividad del metodo eliminar.

        Nodo[] puntoRebalanceo = new Nodo[1]; // Nodo desde donde se inicia el rebalanceo

        eliminar(raiz, id, ret, puntoRebalanceo); // Llama al metodo recursivo para eliminar el nodo con el ID dado.
                                                  // El paciente eliminado se guarda en ret[0].

        if (ret[0] != null) { // Si se elimino un nodo, se decrementa el tamaño del arbol.
            tam--;
            // Rebalancear desde el padre del nodo reemplazante hacia la raiz
            rebalancear(puntoRebalanceo[0]);
        }
        return ret[0]; // Se retorna el paciente eliminado, o null si no se encontro
    }

    // Eliminacion.
    // puntoRebalanceo[0] se asigna al padre del nodo removido
    private void eliminar(Nodo n, long id, Paciente[] ret, Nodo[] puntoRebalanceo) {
        if (n == null) { // Si se llega a un nodo null, no se encontro el ID a eliminar
            return;
        }

        if (id < n.id) { // Si el ID a eliminar es menor que el ID del nodo actual, se elimina
                         // recursivamente en el subarbol izquierdo
            eliminar(n.izq, id, ret, puntoRebalanceo);

        } else if (id > n.id) { // Si el ID a eliminar es mayor que el ID del nodo actual, se elimina
                                // recursivamente en el subarbol derecho
            eliminar(n.der, id, ret, puntoRebalanceo);

        } else {
            ret[0] = n.valor; // Es el nodo a eliminar, se guarda el paciente del nodo actual en ret[0] para
                              // retornar despues

            if (n.izq == null && n.der == null) {
                // Caso sin hijos se desenlaza del padre directamente
                puntoRebalanceo[0] = n.padre; // El padre es el punto de rebalanceo
                reemplazarNodo(n, null);

            } else if (n.izq == null) {
                // Caso con solo hijo derecho
                puntoRebalanceo[0] = n.padre; // El padre es el punto de rebalanceo
                n.der.padre = n.padre; // Se actualiza el padre del hijo
                reemplazarNodo(n, n.der);

            } else if (n.der == null) {
                // Caso con solo hijo izquierdo
                puntoRebalanceo[0] = n.padre; // El padre es el punto de rebalanceo
                n.izq.padre = n.padre; // Se actualiza el padre del hijo
                reemplazarNodo(n, n.izq);

            } else {
                // Caso con dos hijos reemplazar con el sucesor inorden (minimo del subarbol
                // derecho)
                Nodo suc = minNodo(n.der);

                // El punto de rebalanceo es el padre del sucesor antes de moverlo
                // Si el sucesor es hijo directo de n, el punto de rebalanceo sera n despues de
                // la copia
                puntoRebalanceo[0] = (suc.padre == n) ? suc : suc.padre;

                // Se copia la clave y valor del sucesor al nodo a eliminar
                n.id = suc.id;
                n.valor = suc.valor;

                // Se elimina el sucesor fisicamente (tiene a lo sumo un hijo derecho por ser el
                // minimo)
                if (suc.der != null)
                    suc.der.padre = suc.padre;
                reemplazarNodo(suc, suc.der);
            }
        }
    }

    // Reemplaza el nodo viejo por el nuevo en la estructura del padre.
    // Complejidad O(1)
    // Este es un metodo auxiliar para mantener las referencias del padre al hijo
    // actualizadas
    private void reemplazarNodo(Nodo viejo, Nodo nuevo) {
        if (viejo.padre == null) {
            raiz = nuevo; // Era la raiz
        } else if (viejo.padre.izq == viejo) {
            viejo.padre.izq = nuevo; // Era hijo izquierdo
        } else {
            viejo.padre.der = nuevo; // Era hijo derecho
        }
        if (nuevo != null)
            nuevo.padre = viejo.padre; // Se actualiza el padre del nuevo nodo
    }

    // Busca un paciente por ID. Se retorna null si no existe. Complejidad O(log n)
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

    // Retorna el nodo mas a la izquierda que es el minimo. Complejidad O(log n)
    private Nodo minNodo(Nodo n) {
        while (n.izq != null)
            n = n.izq;
        return n;
    }

    // Recorrido inorden, este imprime pacientes en orden ascendente de ID.
    // Complejidad O(n)
    public void recorridoInorden() {
        System.out.println("AVL inorden (por ID) ");
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

        System.out.print(" [" + n.id + " " + n.valor.getNombre() + "]");

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

    // METODOS PARA GUI

    // Retorna arreglo con los IDs visitados al buscar, en orden de visita
    public long[] obtenerCaminoBusqueda(long id) {
        long[] camino = new long[altura() + 1];
        int i = 0;
        Nodo n = raiz;
        while (n != null) {
            camino[i++] = n.id;
            if (id == n.id)
                break;
            n = id < n.id ? n.izq : n.der;
        }
        // Recortar al tamaño real
        long[] recortado = new long[i];
        for (int j = 0; j < i; j++)
            recortado[j] = camino[j];
        return recortado;
    }

    /**
     * Llena el arreglo destino con los nodos en inorden, retorna cuántos llenó
     * Se usa para que AVLPanel sepa qué nodos existen y en qué orden
     */

    public int volcarInorden(Paciente[] destino, int maxSize) {
        int[] idx = { 0 };
        volcarInorden(raiz, destino, idx, maxSize);
        return idx[0];
    }

    private void volcarInorden(Nodo n, Paciente[] destino, int[] idx, int max) {
        if (n == null || idx[0] >= max)
            return;
        volcarInorden(n.izq, destino, idx, max);
        destino[idx[0]++] = n.valor;
        volcarInorden(n.der, destino, idx, max);
    }

    // Snapshot plano de un nodo para que AVLPanel dibuje sin acceder a Nodo interno
    public static class NodoVista {
        public final long id;
        public final String nombre;
        public final int nivel; // profundidad, raiz = 0
        public final int pos; // posicion inorden (0,1,2...)
        public final long idPadre; // -1 si es raiz
        public final int balance;

        public NodoVista(long id, String nombre, int nivel, int pos, long idPadre, int balance) {
            this.id = id;
            this.nombre = nombre;
            this.nivel = nivel;
            this.pos = pos;
            this.idPadre = idPadre;
            this.balance = balance;
        }
    }

    // Llena arreglo de NodoVista en inorden, retorna cuántos nodos hay
    public int obtenerVistas(NodoVista[] destino) {
        int[] contador = { 0 };
        llenarVistas(raiz, 0, -1L, destino, contador);
        return contador[0];
    }

    private void llenarVistas(Nodo n, int nivel, long idPadre, NodoVista[] dest, int[] contador) {
        if (n == null || contador[0] >= dest.length)
            return;
        llenarVistas(n.izq, nivel + 1, n.id, dest, contador);
        dest[contador[0]] = new NodoVista(
                n.id, n.valor.getNombre(), nivel,
                contador[0], // pos inorden = índice actual
                idPadre, balance(n));
        contador[0]++;
        llenarVistas(n.der, nivel + 1, n.id, dest, contador);
    }
}