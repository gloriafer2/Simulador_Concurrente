/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 *
 * @author Gloria
 */
public class Disco {
    
    private Bloque[] bloques; // El espacio físico del simulador
    private int tamañoTotal;

    public Disco(int cantidadBloques) {
        this.tamañoTotal = cantidadBloques;
        this.bloques = new Bloque[cantidadBloques];
        
        // Inicializamos el disco vacío
        for (int i = 0; i < cantidadBloques; i++) {
            bloques[i] = new Bloque(i);
        }
    }

    // Método para buscar el primer bloque libre (Muy importante para CREATE)
    public int buscarBloqueLibre() {
        for (int i = 0; i < tamañoTotal; i++) {
            if (!bloques[i].isOcupado()) {
                return i;
            }
        }
        return -1; // Disco lleno
    }

    public Bloque getBloque(int id) {
        return bloques[id];
    }
}
    
