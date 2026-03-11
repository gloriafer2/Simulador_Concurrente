

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author Gloria
 */



import estructuras.Proceso;
import estructuras.Nodo;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class VistaSimulador extends javax.swing.JFrame {

    // 2. VARIABLES GLOBALES (Deben ir justo después de "public class VistaSimulador...")
    private estructuras.Disco miDisco = new estructuras.Disco(100); 
    private estructuras.ColaProcesos colaDisco = new estructuras.ColaProcesos(); 
    private estructuras.Planificador planificador = new estructuras.Planificador();
    private estructuras.Lista historialProcesos = new estructuras.Lista();
    int posicionCabezal = 0; 
    int distanciaTotal = 0; 

    
    public VistaSimulador() {
        initComponents(); 
        
        iniciarPlanificador(); 
        
        panelDisco.addHierarchyListener(e -> {
            if (panelDisco.isShowing()) {
                java.awt.EventQueue.invokeLater(() -> dibujarDisco());
            }
        });
    }

   private void iniciarPlanificador() {
    Thread hilo = new Thread(() -> {
        while (true) {
            try {
                // 1. El planificador saca el siguiente proceso de la fila
                estructuras.Proceso p = planificador.obtenerSiguiente(colaDisco, posicionCabezal);
                
                if (p != null) {
                    // 2. Marcamos que el proceso ya empezó a trabajar
                    p.setEstado("Ejecutando");
                    actualizarTabla(); // Se ve en la tabla izquierda que ya arrancó

                    int destino = p.getBloque();

                    // --- 3. ANIMACIÓN DEL CABEZAL ---
                    while (posicionCabezal != destino) {
                        if (posicionCabezal < destino) posicionCabezal++;
                        else posicionCabezal--;
                        
                        dibujarDisco(); 
                        Thread.sleep(200); // Velocidad del cuadrito verde
                    }

                    // --- 4. LLEGADA AL DESTINO (EJECUCIÓN DE LA OPERACIÓN) ---
                    if (p.getOperacion() != null && p.getOperacion().equalsIgnoreCase("CREATE")) {
                        // Pinta de rojo todos los bloques (Asignación Encadenada)
                        miDisco.asignarEspacio(p.getTamano(), p.getNombre());
                        
                        actualizarArbol(p.getNombre());
                        
                        javax.swing.table.DefaultTableModel modeloAsignacion = (javax.swing.table.DefaultTableModel) jTable1.getModel();
                        Object[] fila = new Object[] { p.getNombre(), p.getTamano(), p.getBloque(), "Rojo" };
                        modeloAsignacion.addRow(fila);
                        
                    } else if (p.getOperacion() != null && p.getOperacion().equalsIgnoreCase("DELETE")) {
                        // --- LA LÓGICA DE ELIMINAR ---
                        
                        // 1. Liberar los bloques en el disco (volverlos grises)
                        for (int i = 0; i < miDisco.getTamano(); i++) {
                            estructuras.Bloque b = miDisco.getBloque(i);
                            if (b.isOcupado() && b.getNombreArchivo() != null && b.getNombreArchivo().equals(p.getNombre())) {
                                b.setOcupado(false); // Lo marcamos como libre
                                b.setNombreArchivo(null); // Le quitamos el nombre
                            }
                        }

                        // 2. Eliminar el archivo de la Tabla de Asignación (la derecha)
                        javax.swing.table.DefaultTableModel modeloAsignacion = (javax.swing.table.DefaultTableModel) jTable1.getModel();
                        for (int i = 0; i < modeloAsignacion.getRowCount(); i++) {
                            if (modeloAsignacion.getValueAt(i, 0).toString().equals(p.getNombre())) {
                                modeloAsignacion.removeRow(i);
                                break; // Ya lo borró, salimos del ciclo
                            }
                        }
                    }

                    p.setEstado("Terminado");
                    actualizarTabla();              // Desaparece de la tabla izquierda (gracias a tu filtro)
                    dibujarDisco();                 // Se redibuja el disco para ver los bloques rojos/grises
                    
                    jTable1.revalidate();
                    jTable1.repaint();
                    tablaProcesos.revalidate();
                    tablaProcesos.repaint();
                    
                    Thread.sleep(2000); // Pausa de un segundo antes de ir por el siguiente en la cola
                } else {
                    Thread.sleep(500); 
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });
    hilo.start();
}
   
   @Override
    public void paint(java.awt.Graphics g) {
        super.paint(g); 
        dibujarDisco(); 
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        panelDisco = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        cbRol = new javax.swing.JComboBox<>();
        cbPolitica = new javax.swing.JComboBox<>();
        btnCrear = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaProcesos = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane1.setViewportView(jTree1);

        panelDisco.setBackground(new java.awt.Color(153, 153, 153));
        panelDisco.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelDiscoLayout = new javax.swing.GroupLayout(panelDisco);
        panelDisco.setLayout(panelDiscoLayout);
        panelDiscoLayout.setHorizontalGroup(
            panelDiscoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 269, Short.MAX_VALUE)
        );
        panelDiscoLayout.setVerticalGroup(
            panelDiscoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 202, Short.MAX_VALUE)
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Archivo", "Bloques", "Inicio", "Color"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);

        cbRol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Administrador", "Usuario" }));
        cbRol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRolActionPerformed(evt);
            }
        });

        cbPolitica.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FIFO", "SSTF", "SCAN", "C-SCAN" }));
        cbPolitica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPoliticaActionPerformed(evt);
            }
        });

        btnCrear.setText("Crear Archivo");
        btnCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearActionPerformed(evt);
            }
        });

        btnEliminar.setText("eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        tablaProcesos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Nombre", "Bloque", "Operacion", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tablaProcesos);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(cbRol, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(42, 42, 42)
                                    .addComponent(cbPolitica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(panelDisco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnCrear)
                                .addGap(18, 18, 18)
                                .addComponent(btnEliminar)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbPolitica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(panelDisco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCrear)
                    .addComponent(btnEliminar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbRolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRolActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbRolActionPerformed

    private void btnCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearActionPerformed
      String nombre = JOptionPane.showInputDialog(this, "Nombre del archivo:");
    if (nombre == null || nombre.trim().isEmpty()) return;

    String tamanoStr = JOptionPane.showInputDialog(this, "Tamaño en bloques (ej. 4):");
    if (tamanoStr == null || tamanoStr.trim().isEmpty()) return;

    int cantidadBloques = 0;
    
    try {
        cantidadBloques = Integer.parseInt(tamanoStr);
        if (cantidadBloques <= 0) throw new NumberFormatException();
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Error: Por favor ingresa un número entero válido mayor a 0.");
        return; 
    }

    // 1. Buscamos si hay suficientes bloques libres en TOTAL
    int libres = 0;
    int primerLibre = -1;
    for (int i = 0; i < miDisco.getTamano(); i++) {
        if (!miDisco.getBloque(i).isOcupado()) {
            libres++;
            if (primerLibre == -1) primerLibre = i; // Guardamos el primer bloque para el cabezal
        }
    }

    if (libres >= cantidadBloques) {
        estructuras.Proceso nuevo = new estructuras.Proceso(nombre, primerLibre, cantidadBloques, "CREATE", "Admin");
        nuevo.setEstado("En Espera");
        
        colaDisco.encolar(nuevo);
        historialProcesos.insertar(nuevo);
        actualizarTabla(); 
    } else {
        JOptionPane.showMessageDialog(this, "No hay espacio suficiente. Solo quedan " + libres + " bloques libres.");
    }

    }//GEN-LAST:event_btnCrearActionPerformed

    private void cbPoliticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPoliticaActionPerformed
   
    }//GEN-LAST:event_cbPoliticaActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
      DefaultTreeModel modeloArbol = (DefaultTreeModel) jTree1.getModel();
        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
        
        if (nodoSeleccionado == null || nodoSeleccionado.isRoot()) {
            JOptionPane.showMessageDialog(this, "Seleccione un archivo para eliminar.");
            return;
        }

        String nombreArchivo = nodoSeleccionado.getUserObject().toString();

        int bloqueInicio = -1;
        for (int i = 0; i < miDisco.getTamano(); i++) {
            estructuras.Bloque b = miDisco.getBloque(i);
            if (b != null && b.isOcupado() && b.getNombreArchivo().equals(nombreArchivo)) {
                bloqueInicio = i;
                break;
            }
        }

        if (bloqueInicio != -1) {
            miDisco.eliminarArchivo(bloqueInicio);
            modeloArbol.removeNodeFromParent(nodoSeleccionado);
            
          
            this.repaint(); // Obliga a la ventana a actualizarse 
            
            JOptionPane.showMessageDialog(this, "Archivo '" + nombreArchivo + "' eliminado.");
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VistaSimulador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VistaSimulador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VistaSimulador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VistaSimulador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VistaSimulador().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCrear;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JComboBox<String> cbPolitica;
    private javax.swing.JComboBox<String> cbRol;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTree jTree1;
    private javax.swing.JPanel panelDisco;
    private javax.swing.JTable tablaProcesos;
    // End of variables declaration//GEN-END:variables

   private void actualizarTabla() {
    javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) tablaProcesos.getModel();
    modelo.setRowCount(0);

    estructuras.Nodo aux = historialProcesos.getInicio(); 
    
    while (aux != null) {
        estructuras.Proceso p = (estructuras.Proceso) aux.getDato(); 
        
        String estado = p.getEstado() != null ? p.getEstado().trim() : "";
        
        // Si el estado NO es "Terminado", lo mostramos
        if (!estado.equalsIgnoreCase("Terminado")) {
            Object[] fila = new Object[] {
                p.getNombre(),    
                p.getBloque(),    
                p.getOperacion(),         
                p.getEstado()     
            };
            modelo.addRow(fila);
        }
        
        aux = aux.getSiguiente();
    }
}
    private void actualizarArbol(String nombreArchivo) {
        DefaultTreeModel modeloArbol = (DefaultTreeModel) jTree1.getModel();
        DefaultMutableTreeNode raiz = (DefaultMutableTreeNode) modeloArbol.getRoot();
        raiz.add(new DefaultMutableTreeNode(nombreArchivo));
        modeloArbol.reload();
    }

    private void dibujarDisco() {
        java.awt.Graphics g = panelDisco.getGraphics();
        if (g == null) return;

        // Limpiar fondo
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, panelDisco.getWidth(), panelDisco.getHeight());

        int x = 10;
        int y = 10;

        for (int i = 0; i < miDisco.getTamano(); i++) {
            if (i == posicionCabezal) {
                g.setColor(java.awt.Color.GREEN); // Cabezal
            } else if (miDisco.estaOcupado(i)) {
                g.setColor(java.awt.Color.RED);   // Bloque ocupado
            } else {
                g.setColor(java.awt.Color.LIGHT_GRAY); // Bloque libre
            }

            g.fillRect(x, y, 20, 20); 
            g.setColor(java.awt.Color.BLACK); 
            g.drawRect(x, y, 20, 20);

            x += 25; 
            if ((i + 1) % 10 == 0) {
                x = 10;
                y += 25;
            }
        }
    }
    }
