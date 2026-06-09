package grupo1.Estructuras;

import grupo1.Clases.Paciente;

import java.math.BigInteger; // Se uso BigInteger para evitar problemas de overflow en la funcion hash universal

/**
 * Tabla hash con chaining de listas enlazadas para almacenar referencias de
 * pacientes por su ID.
 *
 * Diseno basado en la teoria de Michael Levis vista en clase y los quices de
 * OnlineGDB:
 * - Arreglo de m listas enlazadas (buckets), cada entrada guarda pares de (id,
 * paciente)
 * - Funcion hash de familia universal para enteros: h(x) = ((a*x + b) mod p)
 * mod m
 * - Rehash automatico cuando el factor de carga supera LOAD_FACTOR
 *
 * Complejidad promedio con la familia universal y alpha constante:
 * - Insercion, eliminacion, busqueda: O(1)
 * - Recorrido: O(n + m)
 */

public class TablaHash {

    // ENTRADA, privada. Par (id, paciente) en el encadenamiento del bucket.
    private class Entrada {
        long id;
        Paciente valor;
        Entrada siguiente; // Encadenamiento por lista enlazada manual

        // Complejidad O(1)
        Entrada(long id, Paciente valor) {
            this.id = id;
            this.valor = valor;
            this.siguiente = null;
        }
    }

    // Parametros de la funcion hash universal h(x) = ((a*x + b) mod p) mod m
    // p primo grande mayor que cualquier ID posible para una cedula colombiana
    // actualmente
    private static final long P = 10_000_000_000_037L; // Primo grande fijo
    private static final long A = 6_364_136_223_846L; // a entre [1, p-1], fijo para reproducibilidad
    private static final long B = 1_442_695_040_888L; // b entre [0, p-1], fijo para reproducibilidad

    private static final double LOAD_FACTOR = 0.75; // Factor de carga
    private static final int CAPACIDAD_INICIAL = 16; // Baja y un numero que permite distribuir bien al inicio

    private Entrada[] buckets; // Arreglo de buckets
    private int capacidad; // Este es el 'm', la cardinalidad actual de la tabla
    private int tam; // Este es el 's', la cantidad de entradas almacenadas

    // Complejidad O(m) para inicializar los buckets
    public TablaHash() {
        capacidad = CAPACIDAD_INICIAL;
        buckets = new Entrada[capacidad];
        tam = 0;
        // Los buckets se dejan null porque se tratan como listas vacias
    }

    // Familia universal de funciones hash para enteros
    // h(x) = ((A*x + B) mod P) mod m. Complejidad O(1)
    // Se utilizo Math.floorMod para garantizar resultado positivo ante
    // desbordamiento

    // Se usa big integer para evitar problemas de overflow con A*x + B, es una
    // medida preventiva
    private int calcularHash(long id, int cap) {
        BigInteger valId = BigInteger.valueOf(id);
        BigInteger valA = BigInteger.valueOf(A);
        BigInteger valB = BigInteger.valueOf(B);
        BigInteger valP = BigInteger.valueOf(P);

        // ((A * id) + B) mod P
        BigInteger r = valA.multiply(valId).add(valB).mod(valP);

        // r.longValue() mantiene el numero positivo real porque P cabe en un long.
        // Al aplicar % cap se garantiza un indice valido entre 0 y (cap - 1) como
        // indica la teoria
        return (int) (r.longValue() % cap);
    }

    private int hash(long id) {
        return calcularHash(id, this.capacidad);
    }

    // Inserta un paciente por su ID.
    // Lanza IllegalArgumentException si el ID ya existe.
    // Complejidad O(1) promedio
    public void insertar(long id, Paciente p) {
        // Rehash antes de insertar si se supera el factor de carga
        if ((double) (tam + 1) / capacidad > LOAD_FACTOR)
            rehash();

        int idx = hash(id);
        Entrada actual = buckets[idx];

        // Recorrer la cadena para detectar duplicado
        while (actual != null) {
            if (actual.id == id)
                throw new IllegalArgumentException("Ya existe un paciente con el ID " + id);
            actual = actual.siguiente;
        }

        // Insertar al inicio de la cadena: O(1)
        Entrada nueva = new Entrada(id, p);
        nueva.siguiente = buckets[idx];
        buckets[idx] = nueva;
        tam++;
    }

    // Busca un paciente por ID. Retorna null si no existe
    // Complejidad O(1) promedio
    public Paciente buscar(long id) {
        int idx = hash(id);
        Entrada actual = buckets[idx];

        while (actual != null) {
            if (actual.id == id)
                return actual.valor; // Se encontro el Paciente con ese ID
            actual = actual.siguiente;
        }
        return null; // No existe el Paciente con ese ID
    }

    // Elimina el nodo con la ID dada. Retorna el paciente eliminado o null.
    // Complejidad O(1) promedio
    public Paciente eliminar(long id) {
        int idx = hash(id);
        Entrada actual = buckets[idx];
        Entrada anterior = null;

        while (actual != null) {
            if (actual.id == id) {
                // Se desenlaza el nodo de la cadena
                if (anterior == null) {
                    buckets[idx] = actual.siguiente; // Este nodo era la cabeza
                } else {
                    anterior.siguiente = actual.siguiente; // Este nodo es intermedio o final
                }
                tam--;
                return actual.valor; // Retorna el Paciente con ese ID eliminado
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return null; // No existe el Paciente con ese ID
    }

    // Imprime todas las entradas recorriendo los buckets por medio de su orden de
    // indice. Dentro de cada bucket el orden es LIFO ya que el ultimo insertado es
    // el primero.
    // Complejidad O(n + m)
    public void recorrer() {
        System.out.println("Pacientes en Tabla Hash, recorriedno por buckets:");
        if (tam == 0) {
            System.out.println("Vacia");
            return;
        }
        for (int i = 0; i < capacidad; i++) {
            if (buckets[i] != null) {
                System.out.print(" [bucket " + i + "]");
                Entrada actual = buckets[i];
                while (actual != null) {
                    System.out.print(" -> [" + actual.id + ":" + actual.valor.getNombre() + "]");
                    actual = actual.siguiente;
                }
                System.out.println();
            }
        }
    }

    // Rehash. Duplica la capacidad y reinserta todas las entradas.
    // Complejidad O(n + m), como ocurre pocas veces tiene costo amortizado O(1) por
    // insercion
    private void rehash() {
        int nuevaCapacidad = capacidad * 2;
        Entrada[] nuevosBuckets = new Entrada[nuevaCapacidad];

        // Reinsertar cada entrada distribuyendola en el nuevo espacio
        for (int i = 0; i < capacidad; i++) {
            Entrada actual = buckets[i];
            while (actual != null) {
                Entrada siguiente = actual.siguiente; // Guardar referencia

                int nuevoIdx = calcularHash(actual.id, nuevaCapacidad);

                // Reinsertar al inicio del nuevo bucket
                actual.siguiente = nuevosBuckets[nuevoIdx];
                nuevosBuckets[nuevoIdx] = actual;

                actual = siguiente;
            }
        }

        this.capacidad = nuevaCapacidad;
        this.buckets = nuevosBuckets;
    }

    // Los siguientes son de complejidad O(1)

    // Cantidad de entradas en la tabla
    public int tam() {
        return tam;
    }

    // Vacia o no
    public boolean vacio() {
        return tam == 0;
    }

    // Factor de carga actual alpha = n/m
    public double factorCarga() {
        return (double) tam / capacidad;
    }

    // Capacidad actual de la tabla (m)
    public int capacidad() {
        return capacidad;
    }

    // METODOS PARA GUI
    
    // Clase de Buckets estatica
    /**
     * Imágen de un bucket para que HashPanel dibuje
     * sin acceder a las clases internas de TablaHash.
     *
     * ids[i]     → ID de la i-ésima entrada en la cadena
     * nombres[i] → nombre del paciente en esa entrada
     * La longitud de ambos arreglos es la misma e igual a la
     * cantidad de entradas en ese bucket.
     */
    public static class BucketVista {
        public final long[]   ids;
        public final String[] nombres;
        public BucketVista(long[] ids, String[] nombres) {
            this.ids     = ids;
            this.nombres = nombres;
        }
    }

    //VIsta

    /**
     * Devuelve una imágen del estado completo de la tabla.
     * Retorna un arreglo de BucketVista de longitud == capacidad actual.
     * Complejidad O(n + m).
     */
    public BucketVista[] obtenerVistas() {
        BucketVista[] result = new BucketVista[capacidad];
        for (int i = 0; i < capacidad; i++) {
            // Contar entradas en este bucket
            int count = 0;
            Entrada e = buckets[i];
            while (e != null) { count++; e = e.siguiente; }
            long[]   ids     = new long[count];
            String[] nombres = new String[count];
            int k = 0;
            e = buckets[i];
            while (e != null) {
                ids[k]     = e.id;
                nombres[k] = e.valor.getNombre();
                k++;
                e = e.siguiente;
            }
            result[i] = new BucketVista(ids, nombres);
        }
        return result;
    }
    /**
     * Calcula el índice de bucket que le correspondería al ID dado
     * con la capacidad actual de la tabla.
     * Útil para que GUI.java le diga a HashPanel qué bucket animar
     * ANTES de insertar / eliminar / buscar.
     * Complejidad O(1).
     */
    public int indiceBucket(long id) {
        return hash(id);
    }
}