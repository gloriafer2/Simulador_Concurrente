/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 *
 * @author Gloria
 */
public class Bloque {
    private int id;              // Posición en el disco (0, 1, 2...)
    private boolean ocupado;      // ¿Tiene datos?
    private String nombreArchivo; // Para saber a qué archivo pertenece
    private int siguienteBloque;  // PUNTERO: El ID del próximo bloque (Asignación Encadenada)

    public Bloque(int id) {
        this.id = id;
        this.ocupado = false;
        this.nombreArchivo = "LIBRE";
        this.siguienteBloque = -1; // -1 significa que es el último bloque o está vacío
    }

    // Getters y Setters para poder modificar el bloque después
    public int getId() { return id; }
    public boolean isOcupado() { return ocupado; }
    public void setOcupado(boolean ocupado) { this.ocupado = ocupado; }
    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }
    public int getSiguienteBloque() { return siguienteBloque; }
    public void setSiguienteBloque(int siguienteBloque) { this.siguienteBloque = siguienteBloque; }
    
}
