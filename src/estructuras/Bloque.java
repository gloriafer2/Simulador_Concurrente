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
    
    private String tipoLock;     // Puede ser: "LIBRE", "LECTURA" (Compartido) o "ESCRITURA" (Exclusivo)
    private int lectoresActivos; // Cuántos procesos están leyendo este archivo al mismo tiempo

    public Bloque(int id) {
        this.id = id;
        this.ocupado = false;
        this.nombreArchivo = "LIBRE";
        this.siguienteBloque = -1; // -1 significa que es el último o único
        
        // Inicializamos los candados como libres por defecto
        this.tipoLock = "LIBRE";
        this.lectoresActivos = 0;
    }

    // --- GETTERS Y SETTERS ORIGINALES ---

    public int getId() { 
        return id; 
    }

    public boolean isOcupado() { 
        return ocupado; 
    }
    
    public void setOcupado(boolean o) { 
        this.ocupado = o; 
    }

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
    
    // --- GETTERS Y SETTERS NUEVOS (LOCKS) ---

    public String getTipoLock() { 
        return tipoLock; 
    }
    
    public void setTipoLock(String tipoLock) { 
        this.tipoLock = tipoLock; 
    }

    public int getLectoresActivos() { 
        return lectoresActivos; 
    }
    
    public void agregarLector() { 
        this.lectoresActivos++; 
    }
    
    public void quitarLector() { 
        this.lectoresActivos--; 
        if (this.lectoresActivos <= 0) {
            this.tipoLock = "LIBRE"; // Si ya no hay nadie leyendo, se quita el candado
            this.lectoresActivos = 0;
        }
    }
}