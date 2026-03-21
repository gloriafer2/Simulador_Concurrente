

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
    private estructuras.Disco miDisco = new estructuras.Disco(250); 
    private estructuras.ColaProcesos colaDisco = new estructuras.ColaProcesos(); 
    private estructuras.Planificador planificador = new estructuras.Planificador();
    private estructuras.Lista historialProcesos = new estructuras.Lista();
    int posicionCabezal = 0; 
    int distanciaTotal = 0; 
    volatile boolean falloActivo = false;
    volatile boolean cargandoJson = false; // <--- NUEVA VARIABLE PARA EL CANDADO
    

    
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
                    
                    String politicaElegida = cbPolitica.getSelectedItem().toString();
                    if (!politicaElegida.equals(planificador.getAlgoritmoActual())) {
                        planificador.setAlgoritmo(politicaElegida);
                    }

                    // El planificador saca el siguiente proceso de la fila
                    estructuras.Proceso p = planificador.obtenerSiguiente(colaDisco, posicionCabezal);
                    
                    if (p != null) {
                        // 2. Marcamos que el proceso ya empezó a trabajar
                        p.setEstado("Ejecutando");
                        actualizarTabla(); // Se ve en la tabla izquierda que ya arrancó

                        // Solo registro operaciones críticas (CREATE o DELETE)
                        if (p.getOperacion().equalsIgnoreCase("CREATE") || p.getOperacion().equalsIgnoreCase("DELETE")) {
                            escribirJournal(p.getOperacion(), p.getNombre(), "PENDIENTE");
                        }

                        int destino = p.getBloque();

                        // ANIMACIÓN DEL CABEZAL
                        while (posicionCabezal != destino) {
                            if (posicionCabezal < destino) posicionCabezal++;
                            else posicionCabezal--;
                            
                            dibujarDisco(); 
                            Thread.sleep(300); // Velocidad del cuadrito verde
                        }
                        
                        //  MANEJO DEL FALLO (UNDO REAL) 
                        if (falloActivo) {
                            falloActivo = false; // Reseteamos el switch eléctrico
                            p.setEstado("ABORTADO"); // Reflejamos el UNDO en la tabla
                            actualizarTabla();
                            
                            continue; 
                        }

                        // LLEGADA AL DESTINO (EJECUCIÓN DE LA OPERACIÓN) 
                        if (p.getOperacion() != null && p.getOperacion().equalsIgnoreCase("CREATE")) {
                            
                            // ANIMACIÓN DE ESCRITURA: BLOQUE POR BLOQUE
                            int bloquesEscritos = 0;
                            for (int i = 0; i < miDisco.getTamano() && bloquesEscritos < p.getTamano(); i++) {
                                if (falloActivo) break; // 

                                estructuras.Bloque b = miDisco.getBloque(i);
                                if (!b.isOcupado()) {
                                    // 1. Mueve el cabezal al bloque libre
                                    while (posicionCabezal != i) {
                                        if (falloActivo) break;
                                        if (posicionCabezal < i) posicionCabezal++;
                                        else posicionCabezal--;
                                        dibujarDisco();
                                        Thread.sleep(50); // Velocidad buscando el espacio
                                    }
                                    if (falloActivo) break;

                                    // 2. Pinta de rojo 
                                    b.setOcupado(true);
                                    b.setNombreArchivo(p.getNombre());
                                    dibujarDisco();
                                    bloquesEscritos++;
                                    
                                    Thread.sleep(400); 
                                }
                            }

                            if (falloActivo) {
                                falloActivo = false; 
                                
                                // UNDO REAL: Limpiamos los cuadritos rojos que quedaron a medias
                                for (int i = 0; i < miDisco.getTamano(); i++) {
                                    estructuras.Bloque b = miDisco.getBloque(i);
                                    if (b.isOcupado() && p.getNombre().equals(b.getNombreArchivo())) {
                                        b.setOcupado(false);
                                        b.setNombreArchivo(null);
                                    }
                                }
                                
                                p.setEstado("ABORTADO");
                                actualizarTabla();
                                dibujarDisco();
                                continue; // Aborto la misión y paso al siguiente archivo
                            }

                            // Si no hubo fallo y terminó de escribir todo
                            actualizarArbol(p.getNombre());
                            javax.swing.table.DefaultTableModel modeloAsignacion = (javax.swing.table.DefaultTableModel) jTable1.getModel();
                            modeloAsignacion.addRow(new Object[] { p.getNombre(), p.getTamano(), p.getBloque(), "Rojo" });
                            
                        } else if (p.getOperacion() != null && p.getOperacion().equalsIgnoreCase("DELETE")) {
        
                            // 1. Liberar los bloques en el disco (volverlos grises) UNO POR UNO
                            for (int i = 0; i < miDisco.getTamano(); i++) {
                                estructuras.Bloque b = miDisco.getBloque(i);
                                if (b.isOcupado() && b.getNombreArchivo() != null && b.getNombreArchivo().equals(p.getNombre())) {

                                    // ANIMACIÓN DE CABEZAL AL BORRAR
                                    while (posicionCabezal != i) {
                                        if (posicionCabezal < i) posicionCabezal++;
                                        else posicionCabezal--;
                                        dibujarDisco();
                                        Thread.sleep(50); // Velocidad del cabezal buscando el bloque
                                    }

                                    // Libero el bloque y cambia a gris
                                    b.setOcupado(false);
                                    b.setNombreArchivo(null);
                                    dibujarDisco(); 

                                    Thread.sleep(400); // Pausa para que se note cómo se apaga cada bloque
                                }
                            }
                            
                            

                            // 2. Eliminar el archivo de la Tabla de asignacion (AHORA ES SEGURO CONTRA NULLS)
                            try {
                                javax.swing.table.DefaultTableModel modeloAsignacion = (javax.swing.table.DefaultTableModel) jTable1.getModel();
                                for (int i = 0; i < modeloAsignacion.getRowCount(); i++) {
                                    Object valorFila = modeloAsignacion.getValueAt(i, 0); // Obtenemos el valor primero
                                    
                                    // Verificamos que la fila NO esté vacía antes de intentar leer su nombre
                                    if (valorFila != null && valorFila.toString().equals(p.getNombre())) {
                                        modeloAsignacion.removeRow(i);
                                        break; 
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("Error menor ignorado al limpiar tabla: " + e.getMessage());
                            }
                            
                        }
                        
                        else if (p.getOperacion() != null && p.getOperacion().equalsIgnoreCase("UPDATE")) {
                            
                         // La versión blindada contra errores:
                         String[] nombres = p.getNombre().split("->");
                         String nombreViejo = nombres[0];
                         // Si tiene "->", usamos el nombre nuevo. Si no (ej. si viene del JSON), mantenemos el mismo nombre.
                         String nombreNuevo = (nombres.length > 1) ? nombres[1] : nombreViejo;
                            // Renombramos físicamente en el disco
                            for (int i = 0; i < miDisco.getTamano(); i++) {
                                estructuras.Bloque b = miDisco.getBloque(i);
                                if (b.isOcupado() && b.getNombreArchivo() != null && b.getNombreArchivo().equals(nombreViejo)) {
                                    b.setNombreArchivo(nombreNuevo);
                                }
                            }
                            Thread.sleep(500); // Pausa visual simulando escritura

                            // Actualizamos la tabla de Asignación
                            try {
                                javax.swing.table.DefaultTableModel modeloAsignacion = (javax.swing.table.DefaultTableModel) jTable1.getModel();
                                for (int i = 0; i < modeloAsignacion.getRowCount(); i++) {
                                    Object valorFila = modeloAsignacion.getValueAt(i, 0);
                                    if (valorFila != null && valorFila.toString().equals(nombreViejo)) {
                                        modeloAsignacion.setValueAt(nombreNuevo, i, 0); 
                                    }
                                }
                            } catch (Exception e) { }

                            // Actualizamos el JTree
                            javax.swing.tree.DefaultTreeModel modeloArbol = (javax.swing.tree.DefaultTreeModel) jTree1.getModel();
                            javax.swing.tree.DefaultMutableTreeNode raiz = (javax.swing.tree.DefaultMutableTreeNode) modeloArbol.getRoot();
                            java.util.Enumeration<javax.swing.tree.TreeNode> e = raiz.breadthFirstEnumeration();
                            while (e.hasMoreElements()) {
                                javax.swing.tree.DefaultMutableTreeNode nodo = (javax.swing.tree.DefaultMutableTreeNode) e.nextElement();
                                if (nodo.getUserObject().toString().equals(nombreViejo)) {
                                    nodo.setUserObject(nombreNuevo);
                                    modeloArbol.nodeChanged(nodo);
                                    break;
                                }
                            }
                        }
                        
                        // ==========================================
                        // CIERRE COMÚN PARA TODAS LAS OPERACIONES
                        // ==========================================
                        
                        // REGISTRO CONFIRMADA EN EL JOURNAL
                        if (p.getOperacion().equalsIgnoreCase("CREATE") || p.getOperacion().equalsIgnoreCase("DELETE") || p.getOperacion().equalsIgnoreCase("UPDATE")) {
                            escribirJournal(p.getOperacion(), p.getNombre(), "CONFIRMADA (Commit)");
                        }

                        p.setEstado("Terminado");
                        actualizarTabla();              
                        dibujarDisco();                 
                        
                        try {
                            jTable1.revalidate();
                            jTable1.repaint();
                            if (tablaProcesos != null) {
                                tablaProcesos.revalidate();
                                tablaProcesos.repaint();
                            }
                        } catch (Exception e) {}
                        
                        Thread.sleep(2000); 
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
        btnCargar = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtJournal = new javax.swing.JTextArea();
        btnSimularFallo = new javax.swing.JButton();
        btnRenombrar = new javax.swing.JButton();
        btnCarpeta = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane1.setViewportView(jTree1);

        panelDisco.setBackground(new java.awt.Color(153, 153, 153));
        panelDisco.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelDiscoLayout = new javax.swing.GroupLayout(panelDisco);
        panelDisco.setLayout(panelDiscoLayout);
        panelDiscoLayout.setHorizontalGroup(
            panelDiscoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );
        panelDiscoLayout.setVerticalGroup(
            panelDiscoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 416, Short.MAX_VALUE)
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

        btnCargar.setText("Cargar Json");
        btnCargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarActionPerformed(evt);
            }
        });

        txtJournal.setColumns(20);
        txtJournal.setRows(5);
        jScrollPane4.setViewportView(txtJournal);

        btnSimularFallo.setText("simular Fallo");
        btnSimularFallo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimularFalloActionPerformed(evt);
            }
        });

        btnRenombrar.setText("Renombrar");
        btnRenombrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRenombrarActionPerformed(evt);
            }
        });

        btnCarpeta.setText("Crear carpeta");
        btnCarpeta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCarpetaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(cbRol, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(cbPolitica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelDisco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(41, 41, 41)
                                        .addComponent(btnCargar)
                                        .addGap(54, 54, 54)
                                        .addComponent(btnEliminar)
                                        .addGap(34, 34, 34))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(24, 24, 24)
                                        .addComponent(btnCrear)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnSimularFallo)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap())))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(117, 117, 117)
                                .addComponent(btnRenombrar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCarpeta)
                                .addGap(107, 107, 107))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(132, 132, 132)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbPolitica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCargar)
                            .addComponent(btnEliminar)
                            .addComponent(btnCrear)
                            .addComponent(btnSimularFallo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnRenombrar)
                            .addComponent(btnCarpeta))
                        .addGap(26, 26, 26)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(panelDisco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(231, 348, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbRolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRolActionPerformed
                // TODO add your handling code here:
           String rol = cbRol.getSelectedItem().toString();
        
        if (rol.equalsIgnoreCase("Usuario")) {
            // Modo Usuario: Solo lectura. Bloqueamos botones críticos.
            btnCrear.setEnabled(false);
            btnEliminar.setEnabled(false);
            btnCargar.setEnabled(false); 
            JOptionPane.showMessageDialog(this, "Modo Usuario activado: Permisos restringidos a solo lectura.");
        } else {
            // Modo Administrador: Desbloqueamos todo.
            btnCrear.setEnabled(true);
            btnEliminar.setEnabled(true);
            btnCargar.setEnabled(true);
            
        }
        
        // Obligamos a Java a redibujar los cuadritos rojos y grises inmediatamente
        dibujarDisco(); 
        this.repaint();
                
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
        return; // Detiene la ejecución si el dato es inválido
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
        actualizarTabla(); // Se actualiza la cola de espera
    } else {
        JOptionPane.showMessageDialog(this, "No hay espacio suficiente. Solo quedan " + libres + " bloques libres.");
    }
    }//GEN-LAST:event_btnCrearActionPerformed

    private void cbPoliticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPoliticaActionPerformed
        if (planificador != null && cbPolitica.getSelectedItem() != null) {
            String nuevaPolitica = cbPolitica.getSelectedItem().toString();
            
            if (!nuevaPolitica.equals(planificador.getAlgoritmoActual())) {
                planificador.setAlgoritmo(nuevaPolitica);
                System.out.println("Política actualizada a: " + nuevaPolitica);
            }
        }
    }//GEN-LAST:event_cbPoliticaActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
      javax.swing.tree.DefaultMutableTreeNode nodoSeleccionado = (javax.swing.tree.DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
    
    if (nodoSeleccionado == null || nodoSeleccionado.isRoot()) {
        javax.swing.JOptionPane.showMessageDialog(this, "Por favor, selecciona un archivo o directorio en el árbol para eliminar.");
        return;
    }

    // 1. Ejecutamos el borrado en cascada (encola todos los archivos internos)
    encolarEliminacionRecursiva(nodoSeleccionado);

    // 2. Destruimos la carpeta (o archivo) visualmente del JTree de un solo golpe
    javax.swing.tree.DefaultTreeModel modeloArbol = (javax.swing.tree.DefaultTreeModel) jTree1.getModel();
    modeloArbol.removeNodeFromParent(nodoSeleccionado);
    
    // 3. Refrescamos la UI para ver las solicitudes en la tabla
    actualizarTabla(); 
    
    javax.swing.JOptionPane.showMessageDialog(this, "Solicitud de eliminación en cascada enviada a la cola de E/S.");
    }//GEN-LAST:event_btnEliminarActionPerformed
    
    private void encolarEliminacionRecursiva(javax.swing.tree.DefaultMutableTreeNode nodo) {
    // 1. Si el nodo tiene hijos (es una carpeta), entramos a revisar cada hijo primero
    if (nodo.getChildCount() > 0) {
        for (int i = 0; i < nodo.getChildCount(); i++) {
            javax.swing.tree.DefaultMutableTreeNode hijo = (javax.swing.tree.DefaultMutableTreeNode) nodo.getChildAt(i);
            encolarEliminacionRecursiva(hijo); // ¡Magia! Se llama a sí misma para escanear a fondo
        }
    }

    // 2. Extraemos el nombre de ESTE nodo específico
    String nombreElemento = nodo.getUserObject().toString();

    // 3. Buscamos si tiene bloques asignados en el disco físico
    int bloqueInicio = -1;
    int tamano = 0;
    for (int i = 0; i < miDisco.getTamano(); i++) {
        estructuras.Bloque b = miDisco.getBloque(i);
        if (b.isOcupado() && b.getNombreArchivo() != null && b.getNombreArchivo().equals(nombreElemento)) {
            if (bloqueInicio == -1) bloqueInicio = i;
            tamano++;
        }
    }

    // 4. Si encontramos bloques (es un archivo real), lo mandamos a la cola del planificador
    if (bloqueInicio != -1) {
        estructuras.Proceso pEliminar = new estructuras.Proceso(nombreElemento, bloqueInicio, tamano, "DELETE", "Admin");
        pEliminar.setEstado("En Espera");
        
        colaDisco.encolar(pEliminar);
        historialProcesos.insertar(pEliminar);
    }
}
    
    
    private void btnCargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarActionPerformed
     // 1. Abrimos el buscador de archivos de Windows
        javax.swing.JFileChooser selector = new javax.swing.JFileChooser();
        selector.setDialogTitle("Selecciona el JSON de la prueba");

        if (selector.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File archivo = selector.getSelectedFile();

            try {
                String contenido = new String(java.nio.file.Files.readAllBytes(archivo.toPath()));

                // 3. Convierto el texto a un Objeto JSON 
                org.json.JSONObject json = new org.json.JSONObject(contenido);

                reiniciarSimulador(); 

                // === NUEVO: 1. CERRAMOS LA PUERTA (El planificador se pausa) ===
                cargandoJson = true; 

                posicionCabezal = json.getInt("initial_head");

                //  Cargar los System Files
                org.json.JSONObject systemFiles = json.getJSONObject("system_files");
                for (String keyPos : systemFiles.keySet()) {
                    int posInicio = Integer.parseInt(keyPos);
                    org.json.JSONObject data = systemFiles.getJSONObject(keyPos);
                    cargarSystemFile(data.getString("name"), posInicio, data.getInt("blocks"));
                }

                //  Cargar las solicitudes (Requests para la cola)
                org.json.JSONArray requests = json.getJSONArray("requests");
                for (int i = 0; i < requests.length(); i++) {
                    org.json.JSONObject req = requests.getJSONObject(i);
                    int pos = req.getInt("pos");
                    String op = req.getString("op");

                    // Busco el nombre del archivo en esa posición para que la cola se vea bien
                    String nombreArchivo = "Nuevo_" + pos;
                    int tamano = 1;

                    estructuras.Bloque b = miDisco.getBloque(pos);
                    if (b != null && b.isOcupado() && b.getNombreArchivo() != null) {
                        nombreArchivo = b.getNombreArchivo();
                        tamano = 0;
                        for(int j = 0; j < miDisco.getTamano(); j++) {
                            if(miDisco.getBloque(j).getNombreArchivo() != null && miDisco.getBloque(j).getNombreArchivo().equals(nombreArchivo)) {
                                tamano++;
                            }
                        }
                    }
                    encolarSolicitud(nombreArchivo, pos, tamano, op);
                }

                actualizarTabla();
                dibujarDisco();
                this.repaint();

                //  ABRIMOS LA PUERTA (El planificador ya puede ver la fila completa) ===
                cargandoJson = false; 

                javax.swing.JOptionPane.showMessageDialog(this, "Prueba cargada desde: " + archivo.getName());

            } catch (Exception e) {
                // Por si acaso hay un error, abrimos la puerta para no trancar el programa
                cargandoJson = false; 
                javax.swing.JOptionPane.showMessageDialog(this, "Error al cargar: " + e.getMessage());
                e.printStackTrace();
            }
        }
    
    }//GEN-LAST:event_btnCargarActionPerformed

    private void btnSimularFalloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimularFalloActionPerformed
     String logActual = txtJournal.getText();
    
    // Buscamos las posiciones de la última vez que se escribieron estas palabras
    int ultimoPendiente = logActual.lastIndexOf("PENDIENTE");
    int ultimoConfirmada = logActual.lastIndexOf("CONFIRMADA");

    if (ultimoPendiente > ultimoConfirmada) {
        
        // Detiene el cabezal verde)
        falloActivo = true;
        
        // Vaciamos la cola para cancelar las demás operaciones en espera
        colaDisco = new estructuras.ColaProcesos(); 
        
        javax.swing.JOptionPane.showMessageDialog(this, 
            "¡APAGÓN! El sistema se detuvo en medio del viaje.\nAplicando UNDO: Operación descartada para proteger el disco.", 
            "Fallo Crítico", javax.swing.JOptionPane.WARNING_MESSAGE);

        txtJournal.append(">> REINICIO DEL SISTEMA... Aplicando UNDO.\n");
        txtJournal.append(">> OPERACIÓN ABORTADA. El archivo no se creó ni se borró.\n");
        
    } else {
        javax.swing.JOptionPane.showMessageDialog(this, 
            "El sistema verificó el Journal. No hay operaciones a medias. Todo seguro.", 
            "Sistema Seguro", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    }//GEN-LAST:event_btnSimularFalloActionPerformed

    private void btnRenombrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRenombrarActionPerformed
        // 1. Verificamos que sea Admin
    if (cbRol.getSelectedItem() != null && cbRol.getSelectedItem().toString().equalsIgnoreCase("Usuario")) {
        javax.swing.JOptionPane.showMessageDialog(this, "Acceso denegado. Solo los administradores pueden renombrar.");
        return;
    }

    // 2. Vemos qué seleccionó en el JTree
    javax.swing.tree.DefaultMutableTreeNode nodoSeleccionado = (javax.swing.tree.DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
    if (nodoSeleccionado == null || nodoSeleccionado.isRoot()) {
        javax.swing.JOptionPane.showMessageDialog(this, "Por favor, selecciona un elemento en el árbol.");
        return;
    }

    String nombreViejo = nodoSeleccionado.getUserObject().toString();

    // 3. Buscamos el archivo en el disco para ver dónde empieza y cuánto pesa
    int bloqueInicio = -1;
    int tamano = 0;
    for (int i = 0; i < miDisco.getTamano(); i++) {
        estructuras.Bloque b = miDisco.getBloque(i);
        if (b.isOcupado() && nombreViejo.equals(b.getNombreArchivo())) {
            if (bloqueInicio == -1) bloqueInicio = i;
            tamano++;
        }
    }

    // 4. Pedimos el nombre nuevo
    String nombreNuevo = javax.swing.JOptionPane.showInputDialog(this, "Ingresa el nuevo nombre para '" + nombreViejo + "':");
    if (nombreNuevo == null || nombreNuevo.trim().isEmpty() || nombreNuevo.equals(nombreViejo)) {
        return; 
    }

    // Si es una carpeta (no tiene bloques en el disco físico), la renombramos de inmediato
    if (bloqueInicio == -1) {
        nodoSeleccionado.setUserObject(nombreNuevo);
        ((javax.swing.tree.DefaultTreeModel) jTree1.getModel()).nodeChanged(nodoSeleccionado);
        return;
    }

    // 5. Si es un archivo real, ENCOLAMOS EL PROCESO (El truco: guardamos viejo->nuevo)
    String nombreCombinado = nombreViejo + "->" + nombreNuevo;
    estructuras.Proceso pActualizar = new estructuras.Proceso(nombreCombinado, bloqueInicio, tamano, "UPDATE", "Admin");
    pActualizar.setEstado("En Espera");
    
    colaDisco.encolar(pActualizar);
    historialProcesos.insertar(pActualizar);
    actualizarTabla(); // Mostramos en la tabla que está haciendo fila
    
    javax.swing.JOptionPane.showMessageDialog(this, "Solicitud de actualización enviada a la cola de E/S.");
    }//GEN-LAST:event_btnRenombrarActionPerformed

    private void btnCarpetaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCarpetaActionPerformed
        // TODO add your handling code here:
        // 1. Validamos que solo el Administrador pueda crear carpetas
        if (cbRol.getSelectedItem() != null && cbRol.getSelectedItem().toString().equalsIgnoreCase("Usuario")) {
            javax.swing.JOptionPane.showMessageDialog(this, "Acceso denegado. Solo los administradores pueden crear directorios.");
            return;
        }

        // 2. Pedimos el nombre de la carpeta
        String nombreCarpeta = javax.swing.JOptionPane.showInputDialog(this, "Ingresa el nombre de la nueva carpeta:");
        
        // Si el usuario cancela o deja en blanco, abortamos
        if (nombreCarpeta == null || nombreCarpeta.trim().isEmpty()) {
            return; 
        }

        // 3. Identificamos dónde está parado el usuario en el JTree
        javax.swing.tree.DefaultMutableTreeNode nodoPadre = (javax.swing.tree.DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
        
        // Si no seleccionó nada, la carpeta se crea en la raíz (el inicio del árbol)
        if (nodoPadre == null) {
            nodoPadre = (javax.swing.tree.DefaultMutableTreeNode) jTree1.getModel().getRoot();
        } 
        // Si seleccionó un archivo normal (que no acepta hijos), subimos un nivel para guardarlo en la misma carpeta
        else if (!nodoPadre.getAllowsChildren()) {
            nodoPadre = (javax.swing.tree.DefaultMutableTreeNode) nodoPadre.getParent();
        }

        // 4. Creamos el nodo visual de la carpeta
        javax.swing.tree.DefaultMutableTreeNode nuevaCarpeta = new javax.swing.tree.DefaultMutableTreeNode(nombreCarpeta);
        nuevaCarpeta.setAllowsChildren(true); 

        javax.swing.tree.DefaultTreeModel modeloArbol = (javax.swing.tree.DefaultTreeModel) jTree1.getModel();
        
        // ¡LA LÍNEA MÁGICA! 
        // Obliga al JTree a preguntar si "permite hijos" para decidir el icono, en vez de fijarse si está vacía
        modeloArbol.setAsksAllowsChildren(true); 

        // 5. Agregamos la carpeta al modelo visual del árbol
        modeloArbol.insertNodeInto(nuevaCarpeta, nodoPadre, nodoPadre.getChildCount());
        // 6. Desplegamos la carpeta padre para que el usuario vea su nueva creación inmediatamente
        jTree1.expandPath(new javax.swing.tree.TreePath(nodoPadre.getPath()));

        // Mensaje de éxito
        javax.swing.JOptionPane.showMessageDialog(this, "Carpeta '" + nombreCarpeta + "' creada con éxito.");
    }//GEN-LAST:event_btnCarpetaActionPerformed

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
    private javax.swing.JButton btnCargar;
    private javax.swing.JButton btnCarpeta;
    private javax.swing.JButton btnCrear;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnRenombrar;
    private javax.swing.JButton btnSimularFallo;
    private javax.swing.JComboBox<String> cbPolitica;
    private javax.swing.JComboBox<String> cbRol;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTree jTree1;
    private javax.swing.JPanel panelDisco;
    private javax.swing.JTable tablaProcesos;
    private javax.swing.JTextArea txtJournal;
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
    javax.swing.tree.DefaultTreeModel modeloArbol = (javax.swing.tree.DefaultTreeModel) jTree1.getModel();
    
    // 1. Identificamos qué carpeta tiene seleccionada el usuario ahorita mismo
    javax.swing.tree.DefaultMutableTreeNode nodoDestino = (javax.swing.tree.DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
    
    // Si no seleccionó nada, lo mandamos a la raíz
    if (nodoDestino == null) {
        nodoDestino = (javax.swing.tree.DefaultMutableTreeNode) modeloArbol.getRoot();
    } 
    // Si seleccionó otro archivo por accidente, lo guardamos al lado (en la misma carpeta padre)
    else if (!nodoDestino.getAllowsChildren()) {
        nodoDestino = (javax.swing.tree.DefaultMutableTreeNode) nodoDestino.getParent();
    }

    // 2. Creamos el nodo visual
    javax.swing.tree.DefaultMutableTreeNode nuevoArchivo = new javax.swing.tree.DefaultMutableTreeNode(nombreArchivo);
    
    // ¡LA MAGIA! Le prohibimos tener hijos para que salga con icono de hojita
    nuevoArchivo.setAllowsChildren(false); 

    // 3. Lo agregamos a la carpeta correcta
    nodoDestino.add(nuevoArchivo);
    
    // 4. Refrescamos el árbol visualmente
    modeloArbol.reload();
}

    private void dibujarDisco() {
        java.awt.Graphics g = panelDisco.getGraphics();
        if (g == null) return;

        // Limpiar fondo
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, panelDisco.getWidth(), panelDisco.getHeight());

        int x = 5; // Margen izquierdo más pequeño
        int y = 5; // Margen superior más pequeño

        for (int i = 0; i < miDisco.getTamano(); i++) {
            if (i == posicionCabezal) {
                g.setColor(java.awt.Color.GREEN); // Cabezal
            } else if (miDisco.estaOcupado(i)) {
                g.setColor(java.awt.Color.RED);   // Bloque ocupado
            } else {
                g.setColor(java.awt.Color.LIGHT_GRAY); // Bloque libre
            }

            // Cuadritos de 12x12 píxeles
            g.fillRect(x, y, 12, 12); 
            g.setColor(java.awt.Color.BLACK); 
            g.drawRect(x, y, 12, 12);

            x += 15; // Espacio entre cuadritos
            
            // Cuando llegue a 25 cuadritos en la fila, baja a la siguiente línea
            if ((i + 1) % 25 == 0) {
                x = 5;
                y += 15;
            }
        }
    }
    
    
    private void reiniciarSimulador() {
    miDisco = new estructuras.Disco(250); 
    colaDisco = new estructuras.ColaProcesos(); 
    historialProcesos = new estructuras.Lista();
    posicionCabezal = 0;
    
    // Limpiamos las tablas visualmente
    ((DefaultTableModel) jTable1.getModel()).setRowCount(0);
    ((DefaultTableModel) tablaProcesos.getModel()).setRowCount(0);
    
    // Reiniciamos el árbol
    DefaultTreeModel modeloArbol = (DefaultTreeModel) jTree1.getModel();
    DefaultMutableTreeNode raiz = (DefaultMutableTreeNode) modeloArbol.getRoot();
    raiz.removeAllChildren();
    modeloArbol.reload();
}
    
    
    
    private void cargarSystemFile(String nombre, int inicio, int tamano) {
    for (int i = 0; i < tamano; i++) {
        if (inicio + i < miDisco.getTamano()) {
            estructuras.Bloque b = miDisco.getBloque(inicio + i);
            b.setOcupado(true);
            b.setNombreArchivo(nombre);
        }
    }
    actualizarArbol(nombre);
    
    // Llenar tabla derecha de una vez
    javax.swing.table.DefaultTableModel modeloAsignacion = (javax.swing.table.DefaultTableModel) jTable1.getModel();
    modeloAsignacion.addRow(new Object[] { nombre, tamano, inicio, "Rojo" });
}

    private void encolarSolicitud(String nombre, int inicio, int tamano, String operacion) {
        estructuras.Proceso p = new estructuras.Proceso(nombre, inicio, tamano, operacion, "Admin");
        p.setEstado("En Espera");
        colaDisco.encolar(p);
        historialProcesos.insertar(p);
    }


        private void escribirJournal(String operacion, String archivo, String estado) {
        if (txtJournal != null) {
            // Obtenemos la hora actual para que el log se vea bien pro
            String hora = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
            String registro = "[" + hora + "] OP: " + operacion + " | Archivo: " + archivo + " | Estado: " + estado + "\n";

            txtJournal.append(registro);
            // Hacemos que el cuadro de texto haga scroll automático hacia abajo
            txtJournal.setCaretPosition(txtJournal.getDocument().getLength());
        }
}



    }
