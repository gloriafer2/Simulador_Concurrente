/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 * Encargado de decidir el orden de atención de las solicitudes de E/S.
 */
public class Planificador {
    private String algoritmoActual = "FIFO";
    private boolean subiendo = true; // Para lógica de SCAN y C-SCAN

    /**
     * Selecciona el siguiente proceso de la cola según la política activa.
     */
    public Proceso obtenerSiguiente(ColaProcesos cola, int posCabezal) {
        if (cola.estaVacia()) return null;

        switch (algoritmoActual) {
            case "SSTF":
                // Usamos el nombre exacto que tienes en ColaProcesos.java
                return cola.extraerSSTF(posCabezal);
            
            case "SCAN":
                return obtenerSCAN(cola, posCabezal);
                
            case "C-SCAN":
                return obtenerCSCAN(cola, posCabezal);
                
            case "FIFO":
            default:
                return cola.desencolar();
        }
    }

    private Proceso obtenerSCAN(ColaProcesos cola, int posCabezal) {
        // En un SCAN real, buscas el más cercano en la dirección actual.
        // Si no hay más en esa dirección, cambias 'subiendo = !subiendo'.
        // Por ahora, para que compile, usamos SSTF como base:
        return cola.extraerSSTF(posCabezal);
    }

    private Proceso obtenerCSCAN(ColaProcesos cola, int posCabezal) {
        // En C-SCAN solo se atiende en una dirección y luego vuelve al inicio.
        return cola.desencolar(); 
    }

    public void setAlgoritmo(String nuevoAlgoritmo) {
        this.algoritmoActual = nuevoAlgoritmo;
    }

    public String getAlgoritmoActual() {
        return algoritmoActual;
    }
}