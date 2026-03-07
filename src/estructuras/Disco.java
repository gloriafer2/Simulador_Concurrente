/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

public class Disco {
    private int tamano;
    private boolean[] bloques; // Arreglo manual para el mapa de bits

    public Disco(int tamano) {
        this.tamano = tamano;
        this.bloques = new boolean[tamano]; // Inicializa todos en false (libre)
    }

    // Algoritmo First Fit: Busca el primer hueco disponible
    public int buscarBloqueLibre() {
        for (int i = 0; i < tamano; i++) {
            if (!bloques[i]) {
                return i; // Retorna el índice del primer bloque vacío
            }
        }
        return -1; // Retorna -1 si el disco está lleno
    }

    // Marca un bloque como ocupado (Color Rojo en tu GUI)
    public void ocupar(int posicion) {
        if (posicion >= 0 && posicion < tamano) {
            bloques[posicion] = true;
        }
    }

    // Libera un bloque (para cuando implementes borrar archivos)
    public void liberar(int posicion) {
        if (posicion >= 0 && posicion < tamano) {
            bloques[posicion] = false;
        }
    }

    // Método vital para el dibujo de los cuadritos
    public boolean estaOcupado(int i) {
        if (i >= 0 && i < tamano) {
            return bloques[i];
        }
        return false;
    }

    public int getTamano() {
        return tamano;
    }
}