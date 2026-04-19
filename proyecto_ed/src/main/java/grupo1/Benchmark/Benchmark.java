package grupo1.Benchmark;

import grupo1.Clases.Paciente;
import grupo1.Estructuras.ArbolAVL;
import grupo1.Estructuras.ColaTriage;

import java.io.*;
import java.nio.file.*;
import java.util.*;

// Benchmark de complejidad empirica para ColaTriage y ArbolAVL.
// Realizado con base en la guia de Moodle de la tarea previa, indicaciones y correciones del monitor y
// desarrollo con apoyo de IA para garantizar integridad de mediciones.
//
// USO: Se ejecuta directamente y los datos se acumulan en benchmark/datos.csv con append. Al final se corre el graficador
//
// METODOLOGIA:
// - Llenado (fill) siempre con la operacion de insercion de cada estructura.
// - K operaciones medidas por run y promediadas para reducir varianza.
// - Los targets de busqueda/eliminacion son IDs que existen en la estructura
// - Semilla fija SEED para reproducibilidad entre ejecuciones.
// - WM pasadas de warmup con N representativo para que el JIT
//   compile el camino caliente antes de medir.
//

public class Benchmark {

    static final int RUNS = 7; // mediciones reales por N, se reporta la mediana
    static final int K = 20; // operaciones medidas por run, se promedian
    static final int WM = 5; // pasadas de warmup
    static final long SEED = 42L;
    static final String CSV = "proyecto_ed/src/main/java/grupo1/Benchmark/datos.csv"; // Tanto CSV y grafico sera
                                                                                      // nombrado "datos" para no
                                                                                      // sobreescribir benchmarks de la
                                                                                      // entrega

    // Tamanos de N hasta 10 a la 7
    static final long[] NS = {
            10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L, 10_000_000L
    };

    // N usado en warmup lo suficientemente grande para compilar rotaciones AVL
    // y recorridos de lista, pero sin tardar demasiado.
    static final long N_WARMUP = 10_000L;

    // Genera un Paciente con ID dado y nivel de triage aleatorio entre 1 y 5.
    static Paciente paciente(long id, Random rng) {
        byte nivel = (byte) (Math.abs(rng.nextInt()) % 5 + 1);
        return new Paciente(id, "P" + id, 0, 'M', "EPS", "ABC", nivel);
    }

    // -------------------------------------------------------------------------
    // Mecanismo central de medicion
    // -------------------------------------------------------------------------

    // Llena la estructura con N elementos (fill), luego mide K operaciones
    // (measure) y devuelve el promedio en ns.
    // getTargets() devuelve los IDs que existen en la estructura para
    // busqueda o eliminacion; null si la operacion no necesita targets.
    static double medirUnaVez(
            Runnable fill, // llenado sin medir
            java.util.function.LongConsumer measure, // operacion a medir con un long como argumento
            long n, // cantidad de elementos a llenar antes de medir
            int run, // numero de run actual
            java.util.function.Supplier<long[]> getTargets) { // proveedor de IDs para busqueda o eliminacion

        for (long i = 0; i < n; i++)
            fill.run(); // llenar sin cronometrar

        System.gc(); // GC antes de las K mediciones, no entre ellas

        long[] targets = getTargets != null ? getTargets.get() : null; // obtiene los targets existentes en la
                                                                       // estructura para ese run
        Random rng = new Random(SEED + run); // RNG con semilla fija por run para reproducibilidad

        long total = 0; // acumulador de tiempo total para K operaciones
        for (int k = 0; k < K; k++) { // mide K operaciones y promediar
            long arg;
            if (targets != null && targets.length > 0) {
                // target que existe en la estructura
                arg = targets[(int) (Math.abs(new Random(SEED + (long) run * 1000 + k)
                        .nextLong()) % targets.length)];
            } else {
                // ID nuevo para insercion que no existe aun
                arg = Math.abs(rng.nextLong()) % (n * 10) + n + 1;
            }
            long t0 = System.nanoTime(); // inicia cronometro
            measure.accept(arg);
            long t1 = System.nanoTime(); // termina cronometro
            total += t1 - t0; // acumula tiempo para promediar luego
        }
        return (double) total / K; // devuelve el promedio en ns por operacion
    }

    // Warmup con N_WARMUP para compilar y calentar, luego RUNS mediciones.
    // Factory crea la instancia limpia antes de cada run.
    static double[] bench(
            Runnable[] refFactory, // factory para crear la estructura limpia antes de cada run
            Runnable fill, // llenado sin medir
            java.util.function.LongConsumer measure, // operacion a medir con un long como argumento
            long n, // cantidad de elementos a llenar antes de medir
            java.util.function.Supplier<long[]> getTargets) { // proveedor de IDs para busqueda o eliminacion, null si
                                                              // no se necesitan

        // Warmup con N representativo
        for (int w = 0; w < WM; w++) {
            refFactory[0].run();
            medirUnaVez(fill, measure, N_WARMUP, -w - 1, getTargets);
        }

        double[] res = new double[RUNS]; // resultados de cada run real
        for (int r = 0; r < RUNS; r++) {
            refFactory[0].run(); // instancia limpia por cada run real
            System.gc(); // GC entre runs para reducir varianza por memoria
            res[r] = medirUnaVez(fill, measure, n, r, getTargets); // mide y guarda el promedio de este run
        }
        return res; // devuelve el array de promedios por run, se reporta la mediana luego
    }

    static double mediana(double[] a) { // devuelve la mediana de un array de doubles
        double[] s = a.clone();
        Arrays.sort(s);
        return s[s.length / 2];
    }

    // Guarda con append en CSV. Crea el archivo con cabecera si no existe.
    static void guardarCSV(String est, String met, double[][] data) throws IOException {
        Path ruta = Paths.get(CSV);
        boolean esNuevo = !Files.exists(ruta);
        try (PrintWriter pw = new PrintWriter(new FileWriter(ruta.toFile(), true))) {
            if (esNuevo)
                pw.println("Estructura,Metodo,N,Run,Tiempo_ns");
            for (int i = 0; i < NS.length; i++)
                for (int r = 0; r < data[i].length; r++)
                    pw.printf(Locale.US, "%s,%s,%d,%d,%.2f%n",
                            est, met, NS[i], r, data[i][r]);
        }
        System.out.println("[CSV] " + est + "." + met + " -> " + CSV);
    }

    // Corre la prueba para todos los N, imprime mediana y guarda en CSV.
    static void run(
            String est, String met,
            Runnable[] refFactory,
            Runnable fill,
            java.util.function.LongConsumer measure,
            java.util.function.Supplier<long[]> getTargets) throws IOException {

        System.out.printf("%-20s %-25s |", est, met);
        double[][] data = new double[NS.length][];
        for (int i = 0; i < NS.length; i++) {
            data[i] = bench(refFactory, fill, measure, NS[i], getTargets);
            System.out.printf(" N=%-10d %8.1f ns |", NS[i], mediana(data[i]));
            System.out.flush();
        }
        System.out.println();
        guardarCSV(est, met, data);
    }

    // -------------------------------------------------------------------------
    // main
    // -------------------------------------------------------------------------
    public static void main(String[] args) throws Exception {

        // ColaTriage
        // Buckets de 5 listas FIFO. Todas las operaciones son O(1) porque el
        // numero de niveles de triage es constante (5).
        {
            ColaTriage[] cola = new ColaTriage[1];
            Random rng = new Random(SEED);

            Runnable[] factory = { () -> { // factory, nueva cola vacia y RNG reseteado
                cola[0] = new ColaTriage();
                rng.setSeed(SEED);
            } };

            // Llenado (fill), inserta un paciente con ID aleatorio. O(1).
            Runnable fill = () -> cola[0].insertarPaciente(
                    paciente(Math.abs(rng.nextLong()) % 1_000_000_000L + 1, rng));

            // insertarPaciente O(1)
            run("ColaTriage", "insertarPaciente", factory, fill,
                    v -> cola[0].insertarPaciente(paciente(v, new Random(v))),
                    null); // no necesita targets existentes

            // atenderPaciente O(1) extrae el de mayor prioridad
            run("ColaTriage", "atenderPaciente", factory, fill,
                    v -> cola[0].atenderPaciente(),
                    null);

            // verSiguientePaciente O(1) consulta sin extraer
            run("ColaTriage", "verSiguientePaciente", factory, fill,
                    v -> cola[0].verSiguientePaciente(),
                    null);

            // obtenerSiguientesPacientes O(k)
            run("ColaTriage", "obtenerSiguientes", factory, fill,
                    v -> cola[0].obtenerSiguientesPacientes(10),
                    null);
        }

        // ArbolAVL
        // Arbol balanceado por ID. Insertar, buscar y eliminar son O(log n).
        {
            ArbolAVL[] avl = new ArbolAVL[1];
            Random rngAvl = new Random(SEED);

            // Capacidad maxima de la muestra de IDs reales
            final int CAP_MUESTRA = 500_000;
            long[] muestraAvl = new long[CAP_MUESTRA];
            int[] idxMuestra = { 0 };

            // Factory. Arbol vacio, rng y muestra reseteados
            Runnable[] factory = { () -> {
                avl[0] = new ArbolAVL();
                rngAvl.setSeed(SEED);
                idxMuestra[0] = 0;
                // Limpia la muestra para que no queden IDs del run anterior
                Arrays.fill(muestraAvl, 0L);
            } };

            // Llenado (fill), inserta un paciente con ID aleatorio.
            // Si el ID ya existe (hay una colision), reintenta hasta lograr N exacto.
            // Guarda cada ID real insertado en muestraAvl.
            Runnable fill = () -> {
                boolean insertado = false;
                while (!insertado) {
                    long id = Math.abs(rngAvl.nextLong()) + 1;
                    try {
                        avl[0].insertar(id, paciente(id, new Random(id)));
                        if (idxMuestra[0] < CAP_MUESTRA)
                            muestraAvl[idxMuestra[0]++] = id; // ID real guardado
                        insertado = true;
                    } catch (IllegalArgumentException ignored) {
                        // ID duplicado, reintenta
                    }
                }
            };

            // getTargets, devuelve una copia de los IDs reales insertados.
            // Se llama DESPUES del fill, por lo que contednra exactamente los IDs
            // que estan en el arbol para ese run
            java.util.function.Supplier<long[]> targets = () -> {
                int tam = idxMuestra[0];
                return Arrays.copyOf(muestraAvl, tam);
            };

            // insertar O(log n)
            // La lambda usa un ID nuevo garantizando que no colisiona.
            run("ArbolAVL", "insertar", factory, fill,
                    v -> {
                        long id = v;
                        boolean ok = false;
                        Random r = new Random(v);
                        while (!ok) {
                            try {
                                avl[0].insertar(id, paciente(id, r));
                                ok = true;
                            } catch (IllegalArgumentException ignored) {
                                id = Math.abs(r.nextLong()) + 1;
                            }
                        }
                    },
                    null); // insertar no necesita buscar targets existentes

            // buscar O(log n), target existente
            run("ArbolAVL", "buscar", factory, fill,
                    v -> avl[0].buscar(v),
                    targets);

            // eliminar O(log n), target existente con rebalanceo
            run("ArbolAVL", "eliminar", factory, fill,
                    v -> avl[0].eliminar(v),
                    targets);
        }

        // Se llama al graficador.py al terminar y grafica los resultados acumulados en
        // datos.csv
        new ProcessBuilder("python",
                "proyecto_ed/src/main/java/grupo1/Benchmark/graficador.py")
                .inheritIO().start().waitFor();
    }
}