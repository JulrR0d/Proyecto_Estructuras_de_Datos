package grupo1.Features;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ResumenTXT {
    private static final String ARCHIVO_CSV = "proyecto_ed/src/main/java/grupo1/Reportes/historial_atenciones.csv";
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //genera resumen con lso apcientes atendidos en el dia directo del csv

    public void generarResumenDia() {
        String hoy = LocalDate.now().format(FMT_FECHA);
        String archivoResumen = "proyecto_ed/src/main/java/grupo1/Reportes/resumen_" + hoy + ".txt";

        File csvFile = new File(ARCHIVO_CSV);
        if (!csvFile.exists()) {
            System.out.println("nadie ha sido atendido hoy");
            return;
        }

        int[] porNivel = new int[6]; // triage
        int totalAtendidos = 0;
        StringBuilder detalle = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_CSV))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false; // saltar encabezado
                    continue;
                }

                if (!linea.startsWith(hoy)) {// registros solo hoy
                    continue; 
            
                }                
                totalAtendidos++;
                String[] campos = linea.split(",", -1);

                try {
                    int nivel = Integer.parseInt(campos[7].trim()); // columna NivelTriage
                    if (nivel >= 1 && nivel <= 5) porNivel[nivel]++;
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {}

                detalle.append("  ").append(linea).append("\n");
            }

        } catch (IOException e) {
            System.err.println("Error en el CSV " + e.getMessage());
            return;
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(archivoResumen))) {
            pw.println("=========================================");
            pw.println("   RESUMEN ATENCION - " + hoy);
            pw.println("=========================================");
            pw.println();
            pw.println("total de pacientes atendidos el " + hoy + "--->" + totalAtendidos);
            pw.println();
            pw.println("pacientes atendidos por nivel de triage:");
            for (int i = 1; i <= 5; i++) {
                pw.printf("  Nivel %d: %d paciente(s)%n", i, porNivel[i]);
            }
            pw.println();
            pw.println("-----------------------------------------");
            pw.println("paciente: |Fecha | Hora | ID | Nombre | Edad | Sexo | EPS | Nivel | Sintomas |");
            pw.println("-----------------------------------------");
            pw.print(detalle);
            pw.println("=========================================");

            System.out.println("resumen guardado en ----> " + archivoResumen);

        } catch (IOException e) {
            System.err.println("Error en TXT " + e.getMessage());
        }
    }
}
