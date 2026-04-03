package grupo1;

import grupo1.Clases.Paciente;
import grupo1.Estructuras.ColaTriage;

public class Main {
    /*
    Carlos: Mi idea principalmente era realizar una lista simple, que añadiera los pacientes por triage, y en caso de empate de triage, añadirlos al final por hora, pero, que tuviera un puntero a cada triage, para no tener que recorrer la lista cada vez que se añada un paciente, y así mantener el orden de prioridad. Ya luego preguntandole a GPT, me dijo que lo mejor era usar una cola de prioridad:
        GPT:
            🎯 Conclusión directa
                ✔️ Sí, se puede
                ✔️ Es interesante (más “ingeniería”)
                ⚠️ Pero:
                👉 No es la más óptima en Big O puro
                👉 Es más compleja de implementar correctamente
    Entonces teniendo en cuenta que lo primordial es la eficiencia, y la complejidad, GPT me recomendó usar una cola de prioridad y buckets, que es lo que finalmente implementé. La idea es tener 5 listas enlazadas (una por cada nivel de triage), y cada lista mantiene el orden FIFO de llegada. Para atender, se busca desde el nivel 1 hasta el 5, lo que es O(1) porque el número de niveles de triage es fijo y pequeño. 
    */

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