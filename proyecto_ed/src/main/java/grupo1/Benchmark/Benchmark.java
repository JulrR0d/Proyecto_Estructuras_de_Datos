package grupo1.Benchmark;

import grupo1.Clases.Paciente;
import grupo1.Estructuras.ArbolAVL;
import grupo1.Estructuras.ColaTriage;

import java.io.*;
import java.nio.file.*;
import java.util.*;

// Benchmark de complejidad empirica para ColaTriage y ArbolAVL.
// Realizado con base en la guia de Moodle, indicaciones del monitor y
// desarrollo iterativo con apoyo de IA para garantizar integridad de mediciones.
//
// USO: descomenta un bloque run en main(), ejecuta, repite para cada metodo.
// Los datos se acumulan en benchmark/datos.csv con append.
// Corre graficador.py al terminar para generar la grafica.
//
// METODOLOGIA:
// Fill siempre con la operacion de insercion O(1)de cada estructura. 
// K operaciones medidas por run y promediadas para reducir varianza de posicion. Los targets de busqueda/eliminacion son IDs que existen en la estructura,
// construidos desde la misma semilla del fill.
// Semilla fija SEED para reproducibilidad entre ejecuciones.

// Para N=10^8 ejecutar con: java -Xmx4g
public class Benchmark {

    static final int RUNS = 5; // mediciones reales por N, se reporta la mediana
    static final int K = 5; // operaciones medidas por run, se promedian
    static final int WM = 3; // pasadas de warmup para que el JIT compile antes de medir
    static final long SEED = 42L; // semilla fija: hace reproducibles las pruebas
    static final String CSV = "proyecto_ed/src/main/java/grupo1/Benchmark/datos.csv"; // ruta del CSV de salida

    // Tamanos de N
    static final long[] NS = {
            10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L
    };

    // Genera un Paciente con ID dado y nivel de triage aleatorio entre 1 y 5.
    // Se usa en el fill para poblar las estructuras con datos realistas del
    // proyecto.
    static Paciente paciente(long id, Random rng) {
        byte nivel = (byte) (Math.abs(rng.nextInt()) % 5 + 1);
        return new Paciente(id, "P" + id, 0, 'M', "EPS", "ABC", nivel);
    }

    // Construye un arreglo de hasta MUESTRA_MAX IDs que fueron insertados durante
    // el fill con la misma semilla. Se usa para elegir targets de
    // busqueda/eliminacion que existen en la estructura
    static final int MUESTRA_MAX = 500_000;

    static long[] construirMuestra(long n) {
        int tam = (int) Math.min(n, MUESTRA_MAX);
        long[] muestra = new long[tam];
        Random rng = new Random(SEED);
        if (n <= MUESTRA_MAX) {
            // N cabe completo, guarda todos los IDs insertados en orden
            for (int i = 0; i < tam; i++) {
                muestra[i] = Math.abs(rng.nextLong()) % (n * 10) + 1;
            }
        } else {
            // N grande, muestrea posiciones uniformes para cubrir toda la estructura
            long paso = n / tam;
            long pos = 0;
            for (int i = 0; i < tam; i++) {
                muestra[i] = Math.abs(new Random(SEED + pos).nextLong()) % (n * 10) + 1;
                pos += paso;
            }
        }
        return muestra;
    }

    // Elige el K target de la muestra con semilla unica por (run, k).
    static long elegirTarget(long[] muestra, int run, int k) {
        return muestra[(int) (Math.abs(new Random(SEED + (long) run * 1000 + k).nextLong()) % muestra.length)];
    }

    // Llena la estructura con N elementos usando fill, luego mide K operaciones
    // de measure y devuelve el promedio en ns.
    // usarMuestra=true - los K targets son IDs que existen (para buscar/eliminar).
    // usarMuestra=false - los K targets son valores arbitrarios (para insertar).
    static double medirUnaVez(Runnable fill, java.util.function.LongConsumer measure,
            long n, int run, boolean usarMuestra) {
        Random rng = new Random(SEED + run);
        for (long i = 0; i < n; i++)
            fill.run(); // fill sin cronometro
        System.gc(); // GC una sola vez antes de las K mediciones, no entre ellas
        long[] muestra = usarMuestra ? construirMuestra(n) : null;
        long total = 0;
        for (int k = 0; k < K; k++) {
            long arg = usarMuestra
                    ? elegirTarget(muestra, run, k) // ID que existe en la estructura
                    : Math.abs(rng.nextLong()) % (n * 10) + n + 1; // ID nuevo, no existe
            // Inicio medicion
            long t0 = System.nanoTime();
            measure.accept(arg);
            long t1 = System.nanoTime();
            // Fin medicion
            total += t1 - t0;
        }
        return (double) total / K;
    }

    // Warmup JIT con N pequeno, luego RUNS mediciones reales.
    // factory crea la instancia limpia antes de cada run para evitar estado
    // acumulado.
    static double[] bench(Runnable[] refFactory, Runnable fill,
            java.util.function.LongConsumer measure,
            long n, boolean usarMuestra) {
        long nW = Math.min(n, 1_000); // warmup rapido pero significativo
        for (int w = 0; w < WM; w++) {
            refFactory[0].run(); // instancia limpia para warmup
            medirUnaVez(fill, measure, nW, -w, usarMuestra);
        }
        double[] res = new double[RUNS];
        for (int r = 0; r < RUNS; r++) {
            refFactory[0].run(); // instancia limpia por cada run real
            System.gc();
            res[r] = medirUnaVez(fill, measure, n, r, usarMuestra);
        }
        return res;
    }

    static double mediana(double[] a) {
        double[] s = a.clone();
        Arrays.sort(s);
        return s[s.length / 2];
    }

    // Guarda con append en CSV. Crea el archivo con cabecera si no existe.
    // Formato compatible con graficador.py del proyecto.
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

    // Corre la prueba para todos los N, imprime mediana en consola y guarda en CSV.
    // usarMuestra=true para operaciones de busqueda/eliminacion con targets
    // existentes
    // usarMuestra=false para operaciones de insercion con targets nuevos
    static void run(String est, String met, Runnable[] refFactory,
            Runnable fill, java.util.function.LongConsumer measure,
            boolean usarMuestra) throws IOException {
        System.out.printf("%-20s %-20s |", est, met);
        double[][] data = new double[NS.length][];
        for (int i = 0; i < NS.length; i++) {
            data[i] = bench(refFactory, fill, measure, NS[i], usarMuestra);
            System.out.printf(" N=%-10d %8.1f ns |", NS[i], mediana(data[i]));
            System.out.flush();
        }
        System.out.println();
        guardarCSV(est, met, data);
    }

    public static void main(String[] args) throws Exception {

        // ColaTriage: arreglo de 5 listas FIFO por nivel de triage.
        // Fill con insertarPaciente (O(1)) para construir N pacientes rapido.
        // Los niveles de triage 1-5 se asignan aleatoriamente en paciente().
        {
            ColaTriage[] cola = new ColaTriage[1];
            Random rng = new Random(SEED);

            // Factory: crea cola vacia antes de cada run
            Runnable[] factory = { () -> {
                cola[0] = new ColaTriage();
                rng.setSeed(SEED);
            } };

            // Fill: inserta un paciente con ID secuencial y nivel aleatorio. O(1).
            Runnable fill = () -> cola[0].insertarPaciente(
                    paciente(Math.abs(rng.nextLong()) % 1_000_000_000L + 1, rng));

            // insertarPaciente O(1): insercion en bucket por nivel, FIFO
            run("ColaTriage", "insertarPaciente", factory, fill,
                    v -> cola[0].insertarPaciente(paciente(v, new Random(v))), false);

            // atenderPaciente O(1): extrae el de mayor prioridad (nivel 1 primero)
            run("ColaTriage", "atenderPaciente", factory, fill,
                    v -> cola[0].atenderPaciente(), false);

            // verSiguientePaciente O(1): consulta sin extraer
            run("ColaTriage", "verSiguientePaciente", factory, fill,
                    v -> cola[0].verSiguientePaciente(), false);

            // obtenerSiguientesPacientes O(k): retorna hasta k pacientes sin extraer
            run("ColaTriage", "obtenerSiguientes", factory, fill,
                    v -> cola[0].obtenerSiguientesPacientes(10), false);
        }

        // ArbolAVL: arbol balanceado por ID de paciente.
        // Fill con insertar (O(log n)) porque es la unica forma de agregar nodos.
        // IDs aleatorios
        // Si colisiona se reintenta hasta insertar, garantizando N exacto.
        // muestraAvl guarda IDs reales insertados para buscar/eliminar sin sesgo de no
        // encontrado.
        {
            ArbolAVL[] avl = new ArbolAVL[1];
            Random rngAvl = new Random(SEED);
            long[] muestraAvl = new long[MUESTRA_MAX];
            int[] idxMuestra = { 0 };

            // Factory crea arbol vacio, resetea rng y muestra antes de cada run
            Runnable[] factory = { () -> {
                avl[0] = new ArbolAVL();
                rngAvl.setSeed(SEED);
                idxMuestra[0] = 0;
            } };

            // Fill, ID aleatorio
            // Reintenta en igual para llegar siempre a N exacto.
            // Guarda hasta MUESTRA_MAX IDs reales para buscar o eliminar.
            Runnable fill = () -> {
                boolean insertado = false;
                while (!insertado) {
                    long id = Math.abs(rngAvl.nextLong()) + 1;
                    try {
                        avl[0].insertar(id, paciente(id, new Random(id)));
                        if (idxMuestra[0] < MUESTRA_MAX)
                            muestraAvl[idxMuestra[0]++] = id; // ID real guardado
                        insertado = true;
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            };

            // insertar O(log n), insercion con rebalanceo AVL
            // La lambda mide solo inserciones nuevas, si el ID ya existe se reintenta
            // con uno distinto para no mezclar el costo de la excepcion en la medicion.
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
                                id = Math.abs(r.nextLong()) + 1; // nuevo ID si colisiona
                            }
                        }
                    }, false);

            // buscar O(log n) busqueda por ID garantizado existente en el arbol
            run("ArbolAVL", "buscar", factory, fill,
                    v -> avl[0].buscar(muestraAvl[(int) (Math.abs(new Random(v).nextLong()) %
                            idxMuestra[0])]),
                    true);

            // eliminar O(log n): eliminacion por ID garantizado existente con rebalanceo
            run("ArbolAVL", "eliminar", factory, fill,
                    v -> avl[0].eliminar(muestraAvl[(int) (Math.abs(new Random(v).nextLong()) %
                            idxMuestra[0])]),
                    true);
        }

        // Lanza graficador.py al terminar para generar la imagen
        new ProcessBuilder("python", "proyecto_ed/src/main/java/grupo1/Benchmark/graficador.py")
                .inheritIO().start().waitFor();
    }
}