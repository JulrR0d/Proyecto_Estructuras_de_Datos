package grupo1.Estructuras;

import java.util.Arrays;

import grupo1.Clases.Paciente;

public class Heap {

    private Paciente[] monticulo;
    private int tam;
    private static final int CAPACIDAD_INICIAL = 64;

    public Heap() {
        this.monticulo = new Paciente[CAPACIDAD_INICIAL];
        this.tam = 0;
    }

    private int indicePadre(int i) {
        return (i - 1) / 2;
    }

    private int indiceHijoIzq(int i) {
        return 2 * i + 1;
    }

    private int indiceHijoDer(int i) {
        return 2 * i + 2;
    }

    // Compara usando el arreglo principal
    private boolean prioridadmayor(int a, int b) {
        return prioridadmayor(a, b, monticulo);
    }

    // Compara usando cualquier arreglo (necesario para obtenerSiguientesPacientes)
    private boolean prioridadmayor(int a, int b, Paciente[] arr) {
        Paciente PA = arr[a];
        Paciente PB = arr[b];
        if (PA.getNivelTriage() < PB.getNivelTriage()) {
            return true;
        } else if (PA.getNivelTriage().equals(PB.getNivelTriage())) {
            if (PA.getFechaIngreso().isBefore(PB.getFechaIngreso())) {
                return true;
            } else if (PA.getFechaIngreso().isEqual(PB.getFechaIngreso())) {
                return PA.getHoraIngreso().isBefore(PB.getHoraIngreso());
            }
        }
        return false;
    }

    public void insertar(Paciente p) {
        if (p == null)
            return;
        if (tam == monticulo.length)
            redimensionar();
        monticulo[tam] = p;
        tam++;
        flotar(tam - 1);
    }

    private void flotar(int indice) {
        int actual = indice;
        int padre = indicePadre(actual);
        while (actual > 0 && prioridadmayor(actual, padre)) {
            intercambiar(actual, padre);
            actual = padre;
            padre = indicePadre(actual);
        }
    }

    public Paciente extraer() {
        if (vacio())
            return null;
        Paciente pacienteAtendido = monticulo[0];
        monticulo[0] = monticulo[tam - 1];
        monticulo[tam - 1] = null;
        tam--;
        if (tam > 0)
            hundir(0);
        return pacienteAtendido;
    }

    private void hundir(int indice) {
        int actual = indice;
        while (indiceHijoIzq(actual) < tam) {
            int hijoMayor = indiceHijoIzq(actual);
            int hijoDer = indiceHijoDer(actual);
            if (hijoDer < tam && prioridadmayor(hijoDer, hijoMayor)) {
                hijoMayor = hijoDer;
            }
            if (prioridadmayor(actual, hijoMayor))
                break;
            intercambiar(actual, hijoMayor);
            actual = hijoMayor;
        }
    }

    private void intercambiar(int i, int j) {
        Paciente temp = monticulo[i];
        monticulo[i] = monticulo[j];
        monticulo[j] = temp;
    }

    private void redimensionar() {
        Paciente[] nuevo = new Paciente[monticulo.length * 2];
        for (int i = 0; i < monticulo.length; i++)
            nuevo[i] = monticulo[i];
        monticulo = nuevo;
    }

    public Paciente frente() {
        if (vacio())
            return null;
        return monticulo[0];
    }

    public boolean vacio() {
        return tam == 0;
    }

    public int tam() {
        return tam;
    }

    public Paciente[] obtenerArregloInterno() {
        return Arrays.copyOf(monticulo, tam);
    }

    // Retorna hasta cantidad pacientes en orden de prioridad sin modificar el heap.
    public Paciente[] obtenerSiguientesPacientes(int cantidad) {
        if (cantidad <= 0 || tam == 0)
            return new Paciente[0];

        int n = Math.min(cantidad, tam);
        Paciente[] copia = new Paciente[tam];
        for (int i = 0; i < tam; i++)
            copia[i] = monticulo[i];

        Paciente[] resultado = new Paciente[n];
        int tamCopia = tam;

        for (int k = 0; k < n; k++) {
            resultado[k] = copia[0];
            tamCopia--;
            copia[0] = copia[tamCopia];
            copia[tamCopia] = null;
            int i = 0;
            while (indiceHijoIzq(i) < tamCopia) {
                int mayor = indiceHijoIzq(i);
                int der = indiceHijoDer(i);
                if (der < tamCopia && prioridadmayor(der, mayor, copia))
                    mayor = der;
                if (prioridadmayor(i, mayor, copia))
                    break;
                Paciente tmp = copia[i];
                copia[i] = copia[mayor];
                copia[mayor] = tmp;
                i = mayor;
            }
        }

        return resultado;
    }
}
