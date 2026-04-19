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

    private static final String ARCHIVO_CSV = "historial_atenciones.csv";
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_HORA  = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String ENCABEZADO =
        "FechaAtencion,HoraAtencion,ID,Nombre,Edad,Sexo,EPS,NivelTriage,Sintomas";
/*
  Registra el paciente al final del CSV
  BigO = O(1)
  */
    public void registrarAtencion(Paciente ) {
        if (Paciente == null) {
          return;
        }

        boolean archivoExiste = new File(ARCHIVO_CSV).exists();
        //permite conservar registros pasados
        try (PrintWriter = new PrintWriter(new FileWriter(ARCHIVO_CSV, true))) {
            if (!archivoExiste) {
                PrintWriter.println(ENCABEZADO);
            }
            PrintWriter.println(construirLinea(p));
        } catch (IOException ) {
            System.err.println("error en CSV: " + IOException.getMessage());
        }
    }
    //contruccion de la informacion ingresada en el CSV
    private String construirLinea(Paciente) {
        String sintomas = Paciente.getsintomas() == null ? "" : Paciente.getsintomas().replace("\"", "'");
        if (sintomas.contains(",")) {
            sintomas = "\"" + sintomas + "\"";
        }
        return String.join(",",
            LocalDate.now().format(FMT_FECHA),
            LocalTime.now().format(FMT_HORA),
            String.valueOf(Paciente.getId()),
            Paciente.getNombre(),
            String.valueOf(Paciente.getEdad()),
            String.valueOf(Paciente.getSexo()),
            Paciente.getEPS(),
            String.valueOf(Paciente.getNivelTriage()),
            sintomas
        );
    }
}
