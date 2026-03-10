/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package estructuras;

public class Bloque {
    private int id;
    private boolean ocupado;
    private String nombreArchivo;
    private int siguienteBloque; // PUNTERO al siguiente bloque

    public Bloque(int id) {
        this.id = id;
        this.ocupado = false;
        this.nombreArchivo = "LIBRE";
        this.siguienteBloque = -1; // -1 significa que es el último o único
    }

    // --- GETTERS Y SETTERS ---

    public int getId() { 
        return id; 
    }

    public boolean isOcupado() { 
        return ocupado; 
    }
    
    public void setOcupado(boolean o) { 
        this.ocupado = o; 
    }

    // ¡EL MÉTODO QUE FALTABA!
    public String getNombreArchivo() { 
        return nombreArchivo; 
    }
    
    public void setNombreArchivo(String n) { 
        this.nombreArchivo = n; 
    }

    public int getSiguienteBloque() { 
        return siguienteBloque; 
    }
    
    public void setSiguienteBloque(int sig) { 
        this.siguienteBloque = sig; 
    }
}