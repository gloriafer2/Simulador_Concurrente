/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 *
 * @author Gloria
 */
public class Lista<T> {
    private Nodo<T> cabeza;
    private int tamaño;
    
    public Lista(){
        this.cabeza = null;
        this.tamaño = 0;
        
    }
         
    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            Nodo<T> temp = cabeza;
            while (temp.siguiente != null) {
                temp = temp.siguiente;
            }
            temp.siguiente = nuevo;
        }
        tamaño++;
    }

    // Para saber cuántos elementos hay
    public int getTamaño() {
        return tamaño;
    }

    // Para sacar un elemento de una posición (como el índice de un arreglo)
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamaño) return null;
        Nodo<T> temp = cabeza;
        for (int i = 0; i < indice; i++) {
            temp = temp.siguiente;
        }
        return temp.dato;
    }
    
}
