/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 * Representa un proceso de E/S que solicita operar sobre el sistema de archivos.
 */
public class Proceso {
    private String nombre;
    private int bloqueObjetivo; // Primer bloque va el cabezal
    private int tamano;         // Cantidad de bloques que ocupa/solicita
    private String estado;      // NUEVO, LISTO, EJECUTANDO, TERMINADO
    private String operacion;   // CREATE, READ, UPDATE, DELETE
    private String dueno;       // Admin o Usuario

    // Constructor actualizado para que coincida con tu Vista
    public Proceso(String nombre, int bloqueObjetivo, String estado) {
        this.nombre = nombre;
        this.bloqueObjetivo = bloqueObjetivo;
        this.estado = estado;
        this.tamano = 1;        // Valor por defecto
        this.operacion = "CREATE";
        this.dueno = "Admin";
    }

    public Proceso(String nombre, int bloqueObjetivo, int tamano, String operacion, String dueno) {
        this.nombre = nombre;
        this.bloqueObjetivo = bloqueObjetivo;
        this.tamano = tamano;
        this.operacion = operacion;
        this.dueno = dueno;
        this.estado = "NUEVO";
    }

    public String getNombre() { return nombre; }
    public int getBloque() { return bloqueObjetivo; }
    public int getTamano() { return tamano; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getOperacion() { return operacion; }
    public String getDueno() { return dueno; }
}
