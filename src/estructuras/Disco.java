/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 * Clase que representa la Simulación de un Disco (SD).
 * Implementa asignación encadenada y gestión de bloques.
 */
public class Disco {
    private int tamano;
    private Bloque[] bloques; // Arreglo de objetos Bloque para manejar punteros

    public Disco(int tamano) {
        this.tamano = tamano;
        this.bloques = new Bloque[tamano];
        for (int i = 0; i < tamano; i++) {
            bloques[i] = new Bloque(i); // Inicializa cada bloque con su ID
        }
    }

    /**
     * Busca bloques libres y los asigna mediante una lista enlazada.
     * Requisito: Asignación Encadenada.
     * @param cantidad Bloques necesarios para el archivo.
     * @param nombreArchivo Nombre del archivo dueño.
     * @return El índice del primer bloque asignado o -1 si no hay espacio.
     */
    public int asignarEspacio(int cantidad, String nombreArchivo) {
        if (contarBloquesLibres() < cantidad) {
            return -1; // No hay espacio suficiente
        }

        int primerBloqueIndice = -1;
        int bloqueAnteriorIndice = -1;
        int asignados = 0;

        for (int i = 0; i < tamano && asignados < cantidad; i++) {
            if (!bloques[i].isOcupado()) {
                // Marcar bloque como ocupado
                bloques[i].setOcupado(true);
                bloques[i].setNombreArchivo(nombreArchivo);

                if (primerBloqueIndice == -1) {
                    primerBloqueIndice = i; // Guardamos dónde empieza el archivo
                }

                // Si no es el primer bloque, creamos el enlace (puntero)
                if (bloqueAnteriorIndice != -1) {
                    bloques[bloqueAnteriorIndice].setSiguienteBloque(i);
                }

                bloqueAnteriorIndice = i;
                asignados++;
            }
        }
        
        // El último bloque ya tiene por defecto -1 en siguienteBloque (fin de archivo)
        return primerBloqueIndice;
    }

    /**
     * Libera los bloques de un archivo siguiendo la cadena de punteros.
     */
    public void eliminarArchivo(int inicio) {
        int actual = inicio;
        while (actual != -1) {
            int siguiente = bloques[actual].getSiguienteBloque();
            
            // Limpiar el bloque actual
            bloques[actual].setOcupado(false);
            bloques[actual].setNombreArchivo("LIBRE");
            bloques[actual].setSiguienteBloque(-1);
            
            actual = siguiente;
        }
    }

    // --- MÉTODOS DE APOYO PARA LA VISTA ---

    public int contarBloquesLibres() {
        int libres = 0;
        for (Bloque b : bloques) {
            if (!b.isOcupado()) libres++;
        }
        return libres;
    }

    public boolean estaOcupado(int i) {
        if (i >= 0 && i < tamano) {
            return bloques[i].isOcupado();
        }
        return false;
    }

    public int getTamano() {
        return tamano;
    }

    public Bloque getBloque(int i) {
        if (i >= 0 && i < tamano) {
            return bloques[i];
        }
        return null;
    }
}