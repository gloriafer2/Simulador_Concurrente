/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 *
 * @author Gloria
 */
public class Proceso {
    private int id;
    private String nombre;
    private String estado; // nuevo, listo, ejecutando, bloqueado, terminado
    private int bloqueObjetivo; // El bloque del disco al que quiere ir
    private String operacion; // READ, CREATE, UPDATE, DELETE
    
    public Proceso(int id, String nombre, String operacion, int bloqueObjetivo) {
        this.id = id;
        this.nombre = nombre;
        this.operacion = operacion;
        this.bloqueObjetivo = bloqueObjetivo;
        this.estado = "nuevo";
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * @return the bloqueObjetivo
     */
    public int getBloqueObjetivo() {
        return bloqueObjetivo;
    }

    /**
     * @param bloqueObjetivo the bloqueObjetivo to set
     */
    public void setBloqueObjetivo(int bloqueObjetivo) {
        this.bloqueObjetivo = bloqueObjetivo;
    }

    /**
     * @return the operacion
     */
    public String getOperacion() {
        return operacion;
    }

    /**
     * @param operacion the operacion to set
     */
    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }
}
