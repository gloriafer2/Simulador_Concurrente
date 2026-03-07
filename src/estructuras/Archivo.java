/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 *
 * @author Gloria
 */
public class Archivo {
    private String nombre;
    private int primerBloque; // El ID del bloque donde empieza en el Disco
    private int tamañoBloques; // Cuántos bloques ocupa
    private String dueño;      // "Admin" o "Usuario1"
    private String color;      // Para la visualización en el SD 

    public Archivo(String nombre, int primerBloque, int tamañoBloques, String dueño, String color) {
        this.nombre = nombre;
        this.primerBloque = primerBloque;
        this.tamañoBloques = tamañoBloques;
        this.dueño = dueño;
        this.color = color;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public int getPrimerBloque() { return primerBloque; }
    public int getTamañoBloques() { return tamañoBloques; }
    public String getDueño() { return dueño; }
    public String getColor() { return color; }
}
