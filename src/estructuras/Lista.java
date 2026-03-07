/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 *
 * @author Gloria
 */
public class Lista {
    private Nodo inicio;
    private int tamano;

    public Lista() {
        this.inicio = null;
        this.tamano = 0;
    }

    public void insertar(Object dato) {
        Nodo nuevo = new Nodo(dato);
        if (inicio == null) {
            inicio = nuevo;
        } else {
            Nodo aux = inicio;
            while (aux.getSiguiente() != null) {
                aux = aux.getSiguiente();
            }
            aux.setSiguiente(nuevo);
        }
        tamano++;
    }

    // Método para buscar un elemento (útil para el JTree)
    public Object obtener(int indice) {
        if (indice < 0 || indice >= tamano) return null;
        Nodo aux = inicio;
        for (int i = 0; i < indice; i++) {
            aux = aux.getSiguiente();
        }
        return aux.getDato();
    }

    // Getters básicos
    public int getTamano() {
        return tamano;
    }
    
    public Nodo getInicio() {
        return inicio;
    }
    
}
