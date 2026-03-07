/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

public class ColaProcesos {
    private Nodo cabeza; // Tu puntero inicial

    public ColaProcesos() {
        this.cabeza = null;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public Proceso verPrimero() {
        if (estaVacia()) return null;
        return cabeza.getProceso(); 
    }

    public Proceso desencolar() {
        if (estaVacia()) return null;
        
        Proceso p = cabeza.getProceso();
        cabeza = cabeza.getSiguiente(); // Movemos el puntero al siguiente
        return p;
    }
    
    // Método para agregar al final (si no lo tienes)
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
}