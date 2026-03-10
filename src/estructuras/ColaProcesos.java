/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 *
 * @author Gloria
 */
public class ColaProcesos {
    private Nodo cabeza; 
    private Nodo ultimo;

    public void encolar(Proceso p) {
        Nodo nuevo = new Nodo(p); 
        if (cabeza == null) {
            cabeza = nuevo;
            ultimo = nuevo;
        } else {
            ultimo.setSiguiente(nuevo);
            ultimo = nuevo;
        }
    }

    public Proceso desencolar() {
        if (cabeza == null) return null;
        Proceso p = (Proceso) cabeza.getDato();
        cabeza = cabeza.getSiguiente();
        return p;
    }
}
