/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

public class ColaProcesos {
    private Nodo cabeza;

    public ColaProcesos() {
        this.cabeza = null;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public void encolar(Proceso nuevoProceso) {
        Nodo nuevoNodo = new Nodo(nuevoProceso);
        if (estaVacia()) {
            cabeza = nuevoNodo;
        } else {
            Nodo temp = cabeza;
            while (temp.getSiguiente() != null) {
                temp = temp.getSiguiente();
            }
            temp.setSiguiente(nuevoNodo);
        }
    }

    public Proceso desencolar() {
        if (estaVacia()) return null;
        Proceso p = cabeza.getProceso();
        cabeza = cabeza.getSiguiente();
        return p;
    }

    public Proceso extraerSSTF(int posActual) {
        if (estaVacia()) return null;

        Nodo tempActual = cabeza;
        Nodo tempAnterior = null;
        Nodo mejorNodo = cabeza;
        Nodo anteriorMejor = null;

        int distanciaMinima = Math.abs(cabeza.getProceso().getBloque() - posActual);

        while (tempActual != null) {
            int distanciaActual = Math.abs(tempActual.getProceso().getBloque() - posActual);
            if (distanciaActual < distanciaMinima) {
                distanciaMinima = distanciaActual;
                mejorNodo = tempActual;
                anteriorMejor = tempAnterior;
            }
            tempAnterior = tempActual;
            tempActual = tempActual.getSiguiente();
        }

        if (mejorNodo == cabeza) {
            cabeza = cabeza.getSiguiente();
        } else {
            anteriorMejor.setSiguiente(mejorNodo.getSiguiente());
        }
        return mejorNodo.getProceso();
    }
    
    // Cambia esto en ColaProcesos.java
    public Nodo getInicio() {
        return cabeza;
    }
}