package grupo1;

import grupo1.Clases.Paciente;
import grupo1.Estructuras.ColaTriage;

public class Main {
    public static void main(String[] args) {
        // Cola principal de triage por niveles (1..5).
        ColaTriage colaTriage = new ColaTriage();

        // Se insertan pacientes en distinto orden para comprobar:
        // 1) prioridad por triage
        // 2) FIFO cuando el triage es igual
        colaTriage.insertarPaciente(new Paciente(1L, "Ana", (byte) 3));
        colaTriage.insertarPaciente(new Paciente(2L, "Luis", (byte) 1));
        colaTriage.insertarPaciente(new Paciente(3L, "Marta", (byte) 2));
        colaTriage.insertarPaciente(new Paciente(4L, "Carlos", (byte) 1));
        colaTriage.insertarPaciente(new Paciente(5L, "Elena", (byte) 5));

        System.out.println("Total pacientes inicial: " + colaTriage.totalPacientes());
        System.out.println("Siguiente paciente: " + colaTriage.verSiguientePaciente());
        System.out.println();

        // Se atienden en orden esperado:
        // triage 1: Luis -> Carlos (FIFO)
        // triage 2: Marta
        // triage 3: Ana
        // triage 5: Elena
        while (!colaTriage.estaVacia()) {
            Paciente atendido = colaTriage.atenderPaciente();
            System.out.println("Atendido: " + atendido);
        }

        System.out.println();
        System.out.println("Total pacientes final: " + colaTriage.totalPacientes());
    }
}