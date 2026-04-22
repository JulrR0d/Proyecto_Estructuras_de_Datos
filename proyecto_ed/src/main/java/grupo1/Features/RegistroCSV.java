package grupo1.Features;

import grupo1.Clases.Paciente;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/*
se crea el archivo para pdoer egenrar un resumen/historial de lso pacientes atendidos
archivo llamado historial_atenciones.csv y se crea en el directorio  de ejecucion
se hace mediante la agregacion de cada paciente retirado de COla triage
*/
public class RegistroCSV {

    private static final String ARCHIVO_CSV = "proyecto_ed/src/main/java/grupo1/Features/historial_atenciones.csv";
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String ENCABEZADO = "FechaAtencion,HoraAtencion,ID,Nombre,Edad,Sexo,EPS,NivelTriage,Sintomas";

    /*
     * Registra el paciente al final del CSV
     * Complejidad O(1)
     */
    public void registrarAtencion(Paciente paciente) {
        if (paciente == null) {
            return;
        }

        boolean archivoExiste = new File(ARCHIVO_CSV).exists();
        // permite conservar registros pasados
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(ARCHIVO_CSV, true))) {
            if (!archivoExiste) {
                printWriter.println(ENCABEZADO);
            }
            printWriter.println(construirLinea(paciente));
        } catch (IOException e) {
            System.err.println("error en CSV: " + e.getMessage());
        }
    }

    // contruccion de la informacion ingresada en el CSV
    private String construirLinea(Paciente paciente) {
        String sintomas = paciente.getsintomas() == null ? "" : paciente.getsintomas().replace("\"", "'");
        if (sintomas.contains(",")) {
            sintomas = '\'' + sintomas + '\'';
        }
        return String.join(",",
                LocalDate.now().format(FMT_FECHA),
                LocalTime.now().format(FMT_HORA),
                String.valueOf(paciente.getId()),
                paciente.getNombre(),
                String.valueOf(paciente.getEdad()),
                String.valueOf(paciente.getSexo()),
                paciente.getEPS(),
                String.valueOf(paciente.getNivelTriage()),
                sintomas);
    }
}
