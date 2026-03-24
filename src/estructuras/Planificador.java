/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;



public class Planificador {
    private String algoritmoActual = "FIFO";
    private boolean subiendo = true; // El ascensor empieza subiendo

    public Proceso obtenerSiguiente(ColaProcesos cola, int posCabezal) {
        if (cola.estaVacia()) return null;

        switch (algoritmoActual) {
            case "SSTF":
                return cola.extraerSSTF(posCabezal);
            
            case "SCAN":
                // Intentamos buscar en la dirección actual
                Proceso pSCAN = cola.extraerSCAN(posCabezal, subiendo);
                
                // Si retorna null, es porque llegamos al final de las peticiones en esa dirección
                if (pSCAN == null) {
                    subiendo = !subiendo; // Volteamos la dirección del ascensor
                    pSCAN = cola.extraerSCAN(posCabezal, subiendo); // Buscamos de nuevo
                }
                return pSCAN;
                
            case "C-SCAN":
                // C-SCAN siempre sube, y cuando llega al final salta al inicio
                return cola.extraerCSCAN(posCabezal);
                
            case "FIFO":
            default:
                return cola.desencolar();
        }
    }

    public void setAlgoritmo(String nuevoAlgoritmo) {
        this.algoritmoActual = nuevoAlgoritmo;
        // Reiniciamos la dirección por defecto al cambiar de política
        if (nuevoAlgoritmo.equals("SCAN") || nuevoAlgoritmo.equals("C-SCAN")) {
            this.subiendo = true; 
        }
    }

    public String getAlgoritmoActual() {
        return algoritmoActual;
    }
}