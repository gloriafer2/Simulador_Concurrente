/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 *
 * @author Gloria
 */
public class Nodo {
    private Object dato;    // Aquí se guarda el Proceso o el Archivo
    private Nodo siguiente; // El puntero al siguiente eslabón

    public Nodo(Object dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    
    public Object getDato() {
        return dato;
    }

    public void setDato(Object dato) {
        this.dato = dato;
    }

    public Nodo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }

    Proceso getProceso() {
        return (Proceso) this.dato;
    }
    
}
