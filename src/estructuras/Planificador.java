/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 *
 * @author Gloria
 */
public class Planificador {
    private String algoritmoActual = "FIFO"; // Por defecto
    private int posicionCabezal = 0;

    public void ejecutarPlanificacion(ColaProcesos cola) {
        switch (algoritmoActual) {
            case "FIFO":
                // Lógica del primero en llegar
                break;
            case "SSTF":
                // Lógica del más cercano al cabezal
                break;
            case "SCAN":
                // Lógica del elevador
                break;
            case "C-SCAN":
                // Lógica de retorno circular
                break;
        }
    }

    // Método para que el Administrador cambie el modo desde la interfaz
    public void setAlgoritmo(String nuevoAlgoritmo) {
        this.algoritmoActual = nuevoAlgoritmo;
    }
    
}
