package grupo1.Estructuras;
import grupo1.Clases.Paciente;

public class heap {

    private Paciente[] monticulo;
    private int tam;
    private static final int CAPACIDAD_INICIAL = 64;

    public heap(){
        this.monticulo = new Paciente[CAPACIDAD_INICIAL];
        this.tam = 0;
    }
    
    private int indicePadre(int i) { return (i - 1) / 2; }
    private int indiceHijoIzq(int i) { return 2 * i + 1; }
    private int indiceHijoDer(int i) { return 2 * i + 2; }
    
    private boolean prioridadmayor(int a, int b){
        Paciente PA = monticulo[a];
        Paciente PB = monticulo[b];

        if (PA.getNivelTriage() < PB.getNivelTriage()) { //comparamos nivel triage
            return true;
        } else if (PA.getNivelTriage().equals(PB.getNivelTriage())) { 
            if (PA.getFechaIngreso().isBefore(PB.getFechaIngreso())) { //if nivel triage es igual --> comaparamos fecha
                return true;
            } else if (PA.getFechaIngreso().isEqual(PB.getFechaIngreso())) {
                return PA.getHoraIngreso().isBefore(PB.getHoraIngreso()); // else misma fecha --> comparamos la Hora
            }
        }
        return false;
    }

    public void insertar(Paciente p) {
        if (p == null) return;
        if (tam == monticulo.length) redimensionar();
        
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
    public Paciente extraerMaximo() {
        if (vacio()) return null;

        Paciente pacienteAtendido = monticulo[0];
        monticulo[0] = monticulo[tam - 1];
        monticulo[tam - 1] = null; 
        tam--;

        if (tam > 0) hundir(0);

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

            if (prioridadmayor(actual, hijoMayor)) break; 

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
        for (int i = 0; i < monticulo.length; i++) nuevo[i] = monticulo[i];
        monticulo = nuevo;
    }

    public Paciente frente() {
        if (vacio()) {
            return null;
        }
        return monticulo[0];
    }

    public boolean vacio() { return tam == 0; }

    public int tam() { return tam; }

    public Paciente[] obtenerArregloInterno() {
    return this.monticulo;
}
}


