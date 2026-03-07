/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

public class Proceso {
    private String nombre;
    private int bloqueObjetivo; // El bloque en el disco donde se "escribe"
    private String estado;      // "En Espera", "Escribiendo" o "Terminado"
    private String operacion;   // "CREATE" (según tu imagen)

    // Constructor completo
    public Proceso(String nombre, int bloqueObjetivo, String estado) {
        this.nombre = nombre;
        this.bloqueObjetivo = bloqueObjetivo;
        this.estado = estado;
        this.operacion = "CREATE"; // Valor por defecto visto en tu tabla
    }

    // GETTERS (Muy importantes para que el Hilo sepa a dónde ir)
    public String getNombre() {
        return nombre;
    }

    public int getBloque() {
        return bloqueObjetivo; // El cabezal usará esto para moverse
    }

    public String getEstado() {
        return estado;
    }

    public String getOperacion() {
        return operacion;
    }

    // SETTER (Para que el hilo cambie el estado a "Terminado" al llegar)
    public void setEstado(String estado) {
        this.estado = estado;
    }
}