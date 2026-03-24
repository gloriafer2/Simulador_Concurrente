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
    
    public Proceso extraerSCAN(int posActual, boolean subiendo) {
        if (estaVacia()) return null;

        Nodo tempActual = cabeza;
        Nodo tempAnterior = null;
        Nodo mejorNodo = null;
        Nodo anteriorMejor = null;

        int distanciaMinima = Integer.MAX_VALUE;
        boolean encontroEnDireccion = false;

        // Buscamos el más cercano respetando la dirección (subiendo o bajando)
        while (tempActual != null) {
            int posProceso = tempActual.getProceso().getBloque();
            boolean vaEnDireccion = subiendo ? (posProceso >= posActual) : (posProceso <= posActual);

            if (vaEnDireccion) {
                int distanciaActual = Math.abs(posProceso - posActual);
                if (distanciaActual < distanciaMinima) {
                    distanciaMinima = distanciaActual;
                    mejorNodo = tempActual;
                    anteriorMejor = tempAnterior;
                    encontroEnDireccion = true;
                }
            }
            tempAnterior = tempActual;
            tempActual = tempActual.getSiguiente();
        }

        // Si no hay más procesos en esa dirección, retornamos null para que el Planificador voltee el cabezal
        if (!encontroEnDireccion) {
            return null;
        }

        // Extraemos el nodo de la lista enlazada
        if (mejorNodo == cabeza) {
            cabeza = cabeza.getSiguiente();
        } else {
            anteriorMejor.setSiguiente(mejorNodo.getSiguiente());
        }
        return mejorNodo.getProceso();
    }

    public Proceso extraerCSCAN(int posActual) {
        if (estaVacia()) return null;
        
        Nodo tempActual = cabeza;
        Nodo tempAnterior = null;
        Nodo mejorNodo = null;
        Nodo anteriorMejor = null;

        int distanciaMinima = Integer.MAX_VALUE;
        boolean encontroHaciaAdelante = false;

        // 1. Buscamos el más cercano hacia adelante (siempre subiendo en C-SCAN)
        while (tempActual != null) {
            int posProceso = tempActual.getProceso().getBloque();
            if (posProceso >= posActual) { 
                int distanciaActual = posProceso - posActual;
                if (distanciaActual < distanciaMinima) {
                    distanciaMinima = distanciaActual;
                    mejorNodo = tempActual;
                    anteriorMejor = tempAnterior;
                    encontroHaciaAdelante = true;
                }
            }
            tempAnterior = tempActual;
            tempActual = tempActual.getSiguiente();
        }

        // 2. Si ya no hay nada más adelante, saltamos al inicio del disco (bloque 0) y buscamos el más bajo
        if (!encontroHaciaAdelante) {
            tempActual = cabeza;
            tempAnterior = null;
            int posMinima = Integer.MAX_VALUE;
            
            while (tempActual != null) {
                int posProceso = tempActual.getProceso().getBloque();
                if (posProceso < posMinima) {
                    posMinima = posProceso;
                    mejorNodo = tempActual;
                    anteriorMejor = tempAnterior;
                }
                tempAnterior = tempActual;
                tempActual = tempActual.getSiguiente();
            }
        }

        // Extraemos el nodo
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