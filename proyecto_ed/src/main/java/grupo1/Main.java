package grupo1;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import grupo1.Benchmark.Benchmark;
import grupo1.Clases.Paciente;
import grupo1.Estructuras.ColaTriage;
import grupo1.GUI.GUI;
import grupo1.GUI.SalaEsperaGUI;

public class Main {

        /*
         * Teniendo en cuenta que lo primordial es la eficiencia, y la complejidad, se
         * usa una cola de prioridad y buckets. La idea es tener 5 listas enlazadas (una
         * por cada nivel de triage), y cada lista mantiene el orden FIFO de llegada.
         * Para atender, se
         * busca desde el nivel 1 hasta el 5, lo que es O(1) porque el número de niveles
         * de triage es fijo y pequeño.
         */

        public static void main(String[] args) throws Exception {

                // Descomentar solo un bloque segun lo que se quiera ejecutar

                // SISTEMA. Interfaz grafica
                SwingUtilities.invokeLater(Main::iniciar);

                // BENCHMARK. Pruebas de complejidad empirica
                // Benchmark.main(args);
        }

        private static void iniciar() {
                // Se aplica el look and feel del sistema operativo.
                aplicarTemaBasico();

                // Cola principal de triage por niveles (1..5).
                ColaTriage colaTriage = new ColaTriage();

                // Precarga inicial para pruebas visuales y funcionales.
                precargarPacientes(colaTriage);

                // Ventana de administracion (registro, atencion y estado).
                GUI panelControl = new GUI(colaTriage);
                panelControl.mostrar();

                // Pantalla de sala de espera (llamado en tiempo real).
                SalaEsperaGUI pantallaSala = new SalaEsperaGUI(colaTriage);
                pantallaSala.mostrar();
        }

        private static void precargarPacientes(ColaTriage colaTriage) {
                // Se pre-cargan 10 pacientes iniciales para simular una cola real.
                colaTriage.insertarPaciente(new Paciente(101L, "Ana", 15, 'f',
                                "Famisanar", "Fisura", (byte) 2));
                colaTriage.insertarPaciente(new Paciente(102L, "Luis", 11, 'm',
                                "Sanitas", "Choque anafilactico", (byte) 1));
                colaTriage.insertarPaciente(new Paciente(103L, "Marta", 12, 'f',
                                "Famisanar", "Vomito, dolor de cabeza", (byte) 4));
                colaTriage.insertarPaciente(new Paciente(104L, "Carlos", 20, 'm',
                                "Compensar", "Dolores de pecho", (byte) 3));
                colaTriage.insertarPaciente(new Paciente(105L, "Elena", 13, 'f',
                                "Nueva EPS", "Desmayo", (byte) 2));
                colaTriage.insertarPaciente(new Paciente(106L, "Pablo", 10, 'm',
                                "Sanitas", "Fiebre, malestar general", (byte) 5));
                colaTriage.insertarPaciente(new Paciente(107L, "Sofia", 30, 'f',
                                "Salud Total", "Infarto", (byte) 1));
                colaTriage.insertarPaciente(new Paciente(108L, "Diego", 17, 'm',
                                "Famisanar", "Dolor de cabeza, mareo", (byte) 3));
                colaTriage.insertarPaciente(new Paciente(109L, "Nora", 7, 'f',
                                "Sura", "Hemorragias", (byte) 4));
                colaTriage.insertarPaciente(new Paciente(110L, "Jorge", 40, 'm',
                                "Coosalud", "Insensibilidad en un miembro", (byte) 2));
        }

        private static void aplicarTemaBasico() {
                try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                        // Se conserva el look and feel por defecto si falla el sistema.
                }
        }
}