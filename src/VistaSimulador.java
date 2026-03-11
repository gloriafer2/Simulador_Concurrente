/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author Gloria
 */



public class VistaSimulador extends javax.swing.JFrame {
    private estructuras.Disco miDisco = new estructuras.Disco(100); 
    private estructuras.ColaProcesos colaDisco = new estructuras.ColaProcesos(); 
    private estructuras.Planificador planificador = new estructuras.Planificador();
    /**
     * Creates new form VistaSimulador
     */
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane1.setViewportView(jTree1);

        panelDisco.setBackground(new java.awt.Color(153, 153, 153));
        panelDisco.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelDiscoLayout = new javax.swing.GroupLayout(panelDisco);
        panelDisco.setLayout(panelDiscoLayout);
        panelDiscoLayout.setHorizontalGroup(
            panelDiscoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 149, Short.MAX_VALUE)
        );
        panelDiscoLayout.setVerticalGroup(
            panelDiscoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 130, Short.MAX_VALUE)
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

        cbPolitica.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FIFO", "SSTF", "SCAN" }));

        btnCrear.setText("Crear Archivo");
        btnCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(panelDisco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addComponent(cbRol, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(cbPolitica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(163, 163, 163)
                        .addComponent(btnCrear)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                        .addGap(57, 57, 57)
                        .addComponent(panelDisco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(btnCrear)
                .addContainerGap(240, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbRolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRolActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbRolActionPerformed

    private void btnCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearActionPerformed
        // TODO add your handling code here:

    int cantidadBloques = 0;
    
    try {
            String nombre = javax.swing.JOptionPane.showInputDialog("Nombre del archivo:");
            String sTamano = javax.swing.JOptionPane.showInputDialog("Tamaño en bloques:");

            if (nombre != null && sTamano != null) {
                int tamano = Integer.parseInt(sTamano);

                String dueñoActual = cbRol.getSelectedItem().toString();
                estructuras.Proceso nuevoP = new estructuras.Proceso(1, nombre, "CREATE", 0); 

                colaDisco.encolar(nuevoP);

                planificador.ejecutarPlanificacion(colaDisco);

                actualizarTablaYArbol();
            }
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error en los datos: " + e.getMessage());
        }
    }//GEN-LAST:event_btnCrearActionPerformed

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
    private javax.swing.JComboBox<String> cbPolitica;
    private javax.swing.JComboBox<String> cbRol;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTree jTree1;
    private javax.swing.JPanel panelDisco;
    // End of variables declaration//GEN-END:variables

        private void actualizarTablaYArbol() {
        System.out.println("¡Proceso creado y planificado con éxito!");
    javax.swing.table.DefaultTableModel modeloTabla = (javax.swing.table.DefaultTableModel) jTable1.getModel();
    
       modeloTabla.addRow(new Object[]{
        "1",                    // ID 
        "CREATE",               // Operación
        "En Espera",            // Estado inicial
        "0"                     // Bloque inicial
    });
    
    dibujarDisco();
    
    javax.swing.tree.DefaultTreeModel modelo = (javax.swing.tree.DefaultTreeModel) jTree1.getModel();
    javax.swing.tree.DefaultMutableTreeNode raiz = (javax.swing.tree.DefaultMutableTreeNode) modelo.getRoot();
    raiz.add(new javax.swing.tree.DefaultMutableTreeNode("archivo_nuevo.txt"));
    modelo.reload();
        }
        
        String estado = p.getEstado() != null ? p.getEstado().trim() : "";
        
        
        public void dibujarDisco() {
    java.awt.Graphics g = panelDisco.getGraphics();
    int x = 10, y = 10;
    int tamañoCuadro = 20;
    int espacio = 5;

    for (int i = 0; i < 100; i++) {
        
        if (i < 5) { 
            g.setColor(java.awt.Color.RED); 
        } else {
            g.setColor(java.awt.Color.LIGHT_GRAY);
        }
        
        g.fillRect(x, y, tamañoCuadro, tamañoCuadro);
        g.setColor(java.awt.Color.BLACK); // El borde
        g.drawRect(x, y, tamañoCuadro, tamañoCuadro);

        x += tamañoCuadro + espacio;
        if (x > panelDisco.getWidth() - 30) {
            x = 10;
            y += tamañoCuadro + espacio;
        }
    }
}
       

    }
