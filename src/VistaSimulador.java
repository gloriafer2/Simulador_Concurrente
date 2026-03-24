

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

   
    private estructuras.Disco miDisco = new estructuras.Disco(250); 
    private estructuras.ColaProcesos colaDisco = new estructuras.ColaProcesos(); 
    private estructuras.Planificador planificador = new estructuras.Planificador();
    private estructuras.Lista historialProcesos = new estructuras.Lista();
    int posicionCabezal = 0; 
    int distanciaTotal = 0; 
    volatile boolean falloActivo = false;
    volatile boolean cargandoJson = false; 
    

    
    public VistaSimulador() {
        initComponents(); 
        
        // ==========================================
        // APLICAR RENDERIZADOR MODERNO AL JTREE
        // ==========================================
        if (jTree1 != null) {
            jTree1.setCellRenderer(new ModernTreeRenderer());
        }
        
    
        if (lblMetricas != null) {
            lblMetricas.setOpaque(true); 
            lblMetricas.setBackground(new java.awt.Color(34, 40, 49)); 
            lblMetricas.setForeground(new java.awt.Color(0, 255, 127)); 
            lblMetricas.setFont(new java.awt.Font("Consolas", java.awt.Font.BOLD, 14)); 
            
            lblMetricas.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(70, 75, 85), 1), 
                javax.swing.BorderFactory.createEmptyBorder(8, 15, 8, 15) 
            ));
        }
        
        
        panelDisco.addHierarchyListener(e -> {
            if (panelDisco.isShowing()) {
                java.awt.EventQueue.invokeLater(() -> dibujarDisco());
            }
        });
        
        java.awt.Color fondoPaneles = new java.awt.Color(49, 54, 63);
        java.awt.Color textoBlanco = new java.awt.Color(238, 238, 238);
        java.awt.Color headerTabla = new java.awt.Color(34, 40, 49); // Cabecera más oscura para que resalte

        // 1. Árbol (Este te quedó perfecto)
        if (jTree1 != null) {
            jTree1.setBackground(fondoPaneles);
        }

        // 2. Tabla de Asignación (Izquierda)
        if (jTable1 != null) {
            jTable1.setBackground(fondoPaneles);
            jTable1.setForeground(textoBlanco);
            jTable1.setGridColor(new java.awt.Color(70, 75, 85)); // Líneas de división sutiles
            jTable1.getTableHeader().setBackground(headerTabla);
            jTable1.getTableHeader().setForeground(textoBlanco);
            if (jTable1.getParent() instanceof javax.swing.JViewport) {
                ((javax.swing.JViewport) jTable1.getParent()).setBackground(fondoPaneles);
            }
        }

        // 3. Tabla de Procesos (Derecha)
        if (tablaProcesos != null) {
            tablaProcesos.setBackground(fondoPaneles);
            tablaProcesos.setForeground(textoBlanco);
            tablaProcesos.setGridColor(new java.awt.Color(70, 75, 85));
            tablaProcesos.getTableHeader().setBackground(headerTabla);
            tablaProcesos.getTableHeader().setForeground(textoBlanco);
            // EL TRUCO MAGICO PARA EL FONDO VACÍO:
            if (tablaProcesos.getParent() instanceof javax.swing.JViewport) {
                ((javax.swing.JViewport) tablaProcesos.getParent()).setBackground(fondoPaneles);
            }
        }
        
        
        // 4. Pintar el Cuadro de Texto del Journal (Abajo)
        // Nota: Asegúrate de que tu variable se llame jTextArea1 o cámbiala por el nombre real
        if (jTable1 != null) {
            jTable1.setBackground(fondoPaneles);
            jTable1.setForeground(textoBlanco);
        }
   
        javax.swing.table.DefaultTableCellRenderer headerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        headerRenderer.setBackground(new java.awt.Color(34, 40, 49)); // Fondo oscuro para cabecera
        headerRenderer.setForeground(new java.awt.Color(238, 238, 238)); // Letra blanca
        headerRenderer.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12)); // Letra en negrita
        headerRenderer.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        // Aplicamos el arreglo a la tabla de la izquierda
        if (jTable1 != null) {
            for (int i = 0; i < jTable1.getColumnModel().getColumnCount(); i++) {
                jTable1.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
            }
        }

        // Aplicamos el arreglo a la tabla de la derecha
        if (tablaProcesos != null) {
            for (int i = 0; i < tablaProcesos.getColumnModel().getColumnCount(); i++) {
                tablaProcesos.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
            }
        }
        if (jTable1 != null) {
            jTable1.setBackground(new java.awt.Color(34, 40, 49)); // Fondo bien oscuro
            jTable1.setForeground(new java.awt.Color(0, 255, 127)); // Letra verde terminal
            jTable1.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 12));
        }
        
        
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

                    estructuras.Proceso p = planificador.obtenerSiguiente(colaDisco, posicionCabezal);
                    
                    if (p != null) {
                        
                        boolean candadoObtenido = false;
                        estructuras.Bloque primerBloque = null;
                        
                        if (p.getOperacion().equalsIgnoreCase("CREATE")) {
                            candadoObtenido = true; 
                        } else {
                            primerBloque = miDisco.getBloque(p.getBloque());
                            if (primerBloque != null) {
                                if (p.getOperacion().equalsIgnoreCase("READ")) {
                                    if (primerBloque.getTipoLock().equals("LIBRE") || primerBloque.getTipoLock().equals("LECTURA")) {
                                        primerBloque.setTipoLock("LECTURA");
                                        primerBloque.agregarLector();
                                        candadoObtenido = true;
                                    }
                                } else if (p.getOperacion().equalsIgnoreCase("UPDATE")) {
                                    if (primerBloque.getTipoLock().equals("LIBRE")) {
                                        primerBloque.setTipoLock("ACTUALIZANDO");
                                        candadoObtenido = true;
                                    }
                                } else if (p.getOperacion().equalsIgnoreCase("DELETE")) {
                                    if (primerBloque.getTipoLock().equals("LIBRE")) {
                                        primerBloque.setTipoLock("ELIMINANDO");
                                        candadoObtenido = true;
                                    }
                                }
                            }
                        }

                        if (!candadoObtenido) {
                            p.setEstado("Bloqueado");
                            actualizarTabla();
                            colaDisco.encolar(p); 
                            Thread.sleep(800); 
                            continue; 
                        }

                        p.setEstado("Ejecutando");
                        actualizarTabla(); 
                        actualizarTablaLocks(); 

                        if (p.getOperacion().equalsIgnoreCase("CREATE") || p.getOperacion().equalsIgnoreCase("DELETE") || p.getOperacion().equalsIgnoreCase("UPDATE")) {
                            escribirJournal(p.getOperacion(), p.getNombre(), "PENDIENTE");
                        }

                        int destino = p.getBloque();
                        while (posicionCabezal != destino) {
                            if (falloActivo) break; 
                            if (posicionCabezal < destino) posicionCabezal++;
                            else posicionCabezal--;
                            distanciaTotal++; 
                            
                            final int cabezalActual = posicionCabezal;
                            final int distActual = distanciaTotal;
                            javax.swing.SwingUtilities.invokeLater(() -> {
                                if (lblMetricas != null) {
                                    lblMetricas.setText("Cabezal: " + cabezalActual + " | Distancia Total: " + distActual);
                                }
                            });
                            dibujarDisco(); 
                            Thread.sleep(300); 
                        }
                        
                        if (falloActivo) {
                            falloActivo = false; 
                            p.setEstado("ABORTADO"); 
                            if (primerBloque != null) {
                                if (p.getOperacion().equalsIgnoreCase("READ")) primerBloque.quitarLector();
                                else primerBloque.setTipoLock("LIBRE");
                            }
                            actualizarTabla();
                            actualizarTablaLocks();
                            continue; 
                        }

                        if (p.getOperacion() != null && p.getOperacion().equalsIgnoreCase("CREATE")) {
                            int bloquesEscritos = 0;
                            for (int i = 0; i < miDisco.getTamano() && bloquesEscritos < p.getTamano(); i++) {
                                if (falloActivo) break; 
                                estructuras.Bloque b = miDisco.getBloque(i);
                                if (!b.isOcupado()) {
                                    while (posicionCabezal != i) {
                                        if (falloActivo) break;
                                        if (posicionCabezal < i) posicionCabezal++;
                                        else posicionCabezal--;
                                        distanciaTotal++; 
                            
                                        final int cabezalActual = posicionCabezal;
                                        final int distActual = distanciaTotal;
                                        javax.swing.SwingUtilities.invokeLater(() -> {
                                            if (lblMetricas != null) {
                                                lblMetricas.setText("Cabezal: " + cabezalActual + " | Distancia Total: " + distActual);
                                            }
                                        });
                                        dibujarDisco();
                                        Thread.sleep(50); 
                                    }
                                    if (falloActivo) break;

                                    b.setOcupado(true);
                                    b.setNombreArchivo(p.getNombre());
                                    
                                    if (bloquesEscritos == 0) {
                                        b.setTipoLock("CREANDO"); 
                                        primerBloque = b; 
                                    }
                                    
                                    dibujarDisco();
                                    bloquesEscritos++;
                                    Thread.sleep(400); 
                                }
                            }

                            if (falloActivo) {
                                for (int i = 0; i < miDisco.getTamano(); i++) {
                                    estructuras.Bloque b = miDisco.getBloque(i);
                                    if (b.isOcupado() && p.getNombre().equals(b.getNombreArchivo())) {
                                        b.setOcupado(false);
                                        b.setNombreArchivo(null);
                                        b.setTipoLock("LIBRE");
                                    }
                                }
                            } else {
                                actualizarArbol(p.getNombre());
                                javax.swing.table.DefaultTableModel modeloAsignacion = (javax.swing.table.DefaultTableModel) jTable1.getModel();
                                modeloAsignacion.addRow(new Object[] { p.getNombre(), p.getTamano(), p.getBloque(), "Creando 🔴" });
                            }
                        } 
                        else if (p.getOperacion() != null && p.getOperacion().equalsIgnoreCase("READ")) {
                            for (int i = 0; i < miDisco.getTamano(); i++) {
                                if (falloActivo) break; 
                                estructuras.Bloque b = miDisco.getBloque(i);
                                if (b.isOcupado() && b.getNombreArchivo() != null && b.getNombreArchivo().equals(p.getNombre())) {
                                    while (posicionCabezal != i) {
                                        if (falloActivo) break; 
                                        if (posicionCabezal < i) posicionCabezal++;
                                        else posicionCabezal--;
                                        distanciaTotal++; 

                                        final int cabezalActual = posicionCabezal;
                                        final int distActual = distanciaTotal;
                                        javax.swing.SwingUtilities.invokeLater(() -> {
                                            if (lblMetricas != null) {
                                                lblMetricas.setText("Cabezal: " + cabezalActual + " | Distancia Total: " + distActual);
                                            }
                                        });
                                        
                                        dibujarDisco();
                                        Thread.sleep(50); 
                                    }
                                    if (falloActivo) break; 
                                    Thread.sleep(200); 
                                }
                            }
                        }
                        else if (p.getOperacion() != null && p.getOperacion().equalsIgnoreCase("DELETE")) {
                            for (int i = 0; i < miDisco.getTamano(); i++) {
                                if (falloActivo) break; 
                                estructuras.Bloque b = miDisco.getBloque(i);
                                if (b.isOcupado() && b.getNombreArchivo() != null && b.getNombreArchivo().equals(p.getNombre())) {
                                    while (posicionCabezal != i) {
                                        if (falloActivo) break; 
                                        if (posicionCabezal < i) posicionCabezal++;
                                        else posicionCabezal--;
                                        distanciaTotal++; 
                            
                                        final int cabezalActual = posicionCabezal;
                                        final int distActual = distanciaTotal;
                                        javax.swing.SwingUtilities.invokeLater(() -> {
                                            if (lblMetricas != null) {
                                                lblMetricas.setText("Cabezal: " + cabezalActual + " | Distancia Total: " + distActual);
                                            }
                                        });
                                        
                                        dibujarDisco();
                                        Thread.sleep(50); 
                                    }
                                    if (falloActivo) break; 
                                    
                                    b.setOcupado(false); 
                                    dibujarDisco(); 
                                    Thread.sleep(400); 
                                }
                            }
                            
                            if (falloActivo) {
                                for (int i = 0; i < miDisco.getTamano(); i++) {
                                    estructuras.Bloque b = miDisco.getBloque(i);
                                    if (!b.isOcupado() && p.getNombre().equals(b.getNombreArchivo())) {
                                        b.setOcupado(true);
                                    }
                                }
                            } else {
                                for (int i = 0; i < miDisco.getTamano(); i++) {
                                    estructuras.Bloque b = miDisco.getBloque(i);
                                    if (p.getNombre().equals(b.getNombreArchivo())) {
                                        b.setNombreArchivo(null);
                                        b.setTipoLock("LIBRE");
                                    }
                                }
                                try {
                                    javax.swing.table.DefaultTableModel modeloAsignacion = (javax.swing.table.DefaultTableModel) jTable1.getModel();
                                    for (int i = 0; i < modeloAsignacion.getRowCount(); i++) {
                                        Object valorFila = modeloAsignacion.getValueAt(i, 0); 
                                        if (valorFila != null && valorFila.toString().equals(p.getNombre())) {
                                            modeloAsignacion.removeRow(i);
                                            break; 
                                        }
                                    }
                                } catch (Exception e) { }
                                
                                try {
                                    javax.swing.tree.DefaultTreeModel modeloArbol = (javax.swing.tree.DefaultTreeModel) jTree1.getModel();
                                    javax.swing.tree.DefaultMutableTreeNode raiz = (javax.swing.tree.DefaultMutableTreeNode) modeloArbol.getRoot();
                                    if (raiz != null) {
                                        javax.swing.tree.DefaultMutableTreeNode nodoAEditar = buscarNodoRecursivo(raiz, p.getNombre());
                                        if (nodoAEditar != null) {
                                            modeloArbol.removeNodeFromParent(nodoAEditar);
                                        }
                                    }
                                } catch (Exception e) { }
                            }
                        }
                        else if (p.getOperacion() != null && p.getOperacion().equalsIgnoreCase("UPDATE")) {
                            String[] nombres = p.getNombre().split("->");
                            String nombreViejo = nombres[0];
                            String nombreNuevo = (nombres.length > 1) ? nombres[1] : nombreViejo;

                            for (int i = 0; i < miDisco.getTamano(); i++) {
                                if (falloActivo) break; 
                                estructuras.Bloque b = miDisco.getBloque(i);
                                if (b.isOcupado() && b.getNombreArchivo() != null && b.getNombreArchivo().equals(nombreViejo)) {
                                    b.setNombreArchivo(nombreNuevo);
                                }
                            }
                            
                            if (falloActivo) {
                                for (int i = 0; i < miDisco.getTamano(); i++) {
                                    estructuras.Bloque b = miDisco.getBloque(i);
                                    if (b.isOcupado() && b.getNombreArchivo() != null && b.getNombreArchivo().equals(nombreNuevo)) {
                                        b.setNombreArchivo(nombreViejo);
                                    }
                                }
                            } else {
                                Thread.sleep(500); 
                                try {
                                    javax.swing.table.DefaultTableModel modeloAsignacion = (javax.swing.table.DefaultTableModel) jTable1.getModel();
                                    for (int i = 0; i < modeloAsignacion.getRowCount(); i++) {
                                        Object valorFila = modeloAsignacion.getValueAt(i, 0);
                                        if (valorFila != null && valorFila.toString().equals(nombreViejo)) {
                                            modeloAsignacion.setValueAt(nombreNuevo, i, 0); 
                                        }
                                    }
                                } catch (Exception e) { }

                                try {
                                    javax.swing.tree.DefaultTreeModel modeloArbol = (javax.swing.tree.DefaultTreeModel) jTree1.getModel();
                                    javax.swing.tree.DefaultMutableTreeNode raiz = (javax.swing.tree.DefaultMutableTreeNode) modeloArbol.getRoot();
                                    if (raiz != null) {
                                        javax.swing.tree.DefaultMutableTreeNode nodoAEditar = buscarNodoRecursivo(raiz, nombreViejo);
                                        if (nodoAEditar != null) {
                                            nodoAEditar.setUserObject(nombreNuevo);
                                            modeloArbol.nodeChanged(nodoAEditar);
                                        }
                                    }
                                } catch (Exception e) {}
                            }
                        }

                       
                        if (falloActivo) {
                            falloActivo = false; 
                            p.setEstado("ABORTADO"); 
                            if (primerBloque != null) {
                                if (p.getOperacion().equalsIgnoreCase("READ")) primerBloque.quitarLector();
                                else primerBloque.setTipoLock("LIBRE");
                            }
                            actualizarTabla();
                            actualizarTablaLocks();
                            dibujarDisco();
                            continue; 
                        }
                        
                        if (p.getOperacion().equalsIgnoreCase("READ")) {
                            if (primerBloque != null) primerBloque.quitarLector();
                        } else {
                            if (primerBloque != null) primerBloque.setTipoLock("LIBRE");
                        }

                        if (p.getOperacion().equalsIgnoreCase("CREATE") || p.getOperacion().equalsIgnoreCase("DELETE") || p.getOperacion().equalsIgnoreCase("UPDATE")) {
                            escribirJournal(p.getOperacion(), p.getNombre(), "CONFIRMADA (Commit)");
                        }

                        p.setEstado("Terminado");
                        actualizarTabla();              
                        dibujarDisco();                 
                        actualizarTablaLocks(); 
                        
                        try {
                            jTable1.revalidate();
                            jTable1.repaint();
                        } catch (Exception e) {}
                        
                        Thread.sleep(2000); 
                    } else {
                        Thread.sleep(500); 
                    }
                } catch (Exception e) {
                }
            }
        });
        hilo.start();
    }
    
    private javax.swing.tree.DefaultMutableTreeNode buscarNodoRecursivo(javax.swing.tree.DefaultMutableTreeNode nodoActual, String nombreBuscado) {
        if (nodoActual.getUserObject().toString().equals(nombreBuscado)) {
            return nodoActual;
        }
        for (int i = 0; i < nodoActual.getChildCount(); i++) {
            javax.swing.tree.DefaultMutableTreeNode hijo = (javax.swing.tree.DefaultMutableTreeNode) nodoActual.getChildAt(i);
            javax.swing.tree.DefaultMutableTreeNode resultado = buscarNodoRecursivo(hijo, nombreBuscado);
            if (resultado != null) {
                return resultado;
            }
        }
        return null;
    }
    
    private void escribirJournal(String operacion, String archivo, String estado) {
        if (txtJournal != null) {
            String hora = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            String registro = "[" + hora + "] OP: " + operacion + " | Archivo: " + archivo + " | Estado: " + estado + "\n";
            txtJournal.append(registro);
            txtJournal.setCaretPosition(txtJournal.getDocument().getLength());
        }
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
        btnLeer = new javax.swing.JButton();
        lblMetricas = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTree1.setBackground(new java.awt.Color(0, 0, 0));
        jScrollPane1.setViewportView(jTree1);

        panelDisco.setBackground(new java.awt.Color(153, 153, 153));
        panelDisco.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelDiscoLayout = new javax.swing.GroupLayout(panelDisco);
        panelDisco.setLayout(panelDiscoLayout);
        panelDiscoLayout.setHorizontalGroup(
            panelDiscoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 519, Short.MAX_VALUE)
        );
        panelDiscoLayout.setVerticalGroup(
            panelDiscoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 597, Short.MAX_VALUE)
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Archivo", "Bloques", "Inicio", "Estado Lock"
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

        txtJournal.setBackground(new java.awt.Color(204, 204, 204));
        txtJournal.setColumns(20);
        txtJournal.setForeground(new java.awt.Color(0, 153, 153));
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

        btnLeer.setText("Leer");
        btnLeer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeerActionPerformed(evt);
            }
        });

        lblMetricas.setBackground(new java.awt.Color(0, 0, 0));
        lblMetricas.setForeground(new java.awt.Color(255, 255, 255));
        lblMetricas.setText("Cabezal: 0 | Distancia Total: 0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(588, 588, 588)
                                .addComponent(btnRenombrar)
                                .addGap(18, 18, 18)
                                .addComponent(btnLeer)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCargar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSimularFallo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCarpeta)
                                .addGap(18, 18, 18)
                                .addComponent(btnEliminar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCrear))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblMetricas, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(panelDisco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(109, 109, 109)
                                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 522, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                        .addContainerGap(1236, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbRol, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(115, 115, 115)
                        .addComponent(cbPolitica, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(273, 273, 273))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 466, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cbPolitica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cbRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 12, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(lblMetricas)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(63, 63, 63)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnRenombrar)
                                    .addComponent(btnLeer)
                                    .addComponent(btnCargar)
                                    .addComponent(btnSimularFallo)
                                    .addComponent(btnCarpeta)
                                    .addComponent(btnEliminar)
                                    .addComponent(btnCrear))
                                .addGap(35, 35, 35)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(72, 72, 72))
                            .addComponent(panelDisco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbRolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRolActionPerformed
          String rol = cbRol.getSelectedItem().toString();
        
        if (rol.equalsIgnoreCase("Usuario")) {
            // Modo Usuario: Solo lectura. Bloqueamos TODOS los botones críticos.
            btnCrear.setEnabled(false);
            btnEliminar.setEnabled(false);
            btnCargar.setEnabled(false);
            btnSimularFallo.setEnabled(false); 
            btnRenombrar.setEnabled(false);    
            btnCarpeta.setEnabled(false); 
            btnLeer.setEnabled(true);
            
            javax.swing.JOptionPane.showMessageDialog(this, "Modo Usuario activado: Permisos restringidos a solo lectura.");
        } else {
            // Modo Administrador: Desbloqueamos todo.
            btnCrear.setEnabled(true);
            btnEliminar.setEnabled(true);
            btnCargar.setEnabled(true);
            btnSimularFallo.setEnabled(true); 
            btnRenombrar.setEnabled(true);    
            btnCarpeta.setEnabled(true);   
            btnLeer.setEnabled(true);
        }
        
        // Obligamos a Java a redibujar los cuadritos
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

        String nombreElemento = nodoSeleccionado.getUserObject().toString();

        // Averiguamos si es un archivo físico buscando si tiene bloques en el disco
        boolean esArchivo = false;
        for (int i = 0; i < miDisco.getTamano(); i++) {
            estructuras.Bloque b = miDisco.getBloque(i);
            if (b.isOcupado() && b.getNombreArchivo() != null && b.getNombreArchivo().equals(nombreElemento)) {
                esArchivo = true;
                break;
            }
        }

        // 1. Encolamos la eliminación 
        encolarEliminacionRecursiva(nodoSeleccionado);

        // 2. Control visual en el JTree según si es archivo o carpeta
        if (esArchivo) {
            // Si es archivo, NO lo borramos del JTree todavía. ¡El Planificador lo borrará en vivo!
            javax.swing.JOptionPane.showMessageDialog(this, "Archivo enviado a la cola de E/S para ser eliminado.");
        } else {
            // Si es carpeta, la desaparecemos visualmente de inmediato para limpiar la pantalla
            javax.swing.tree.DefaultTreeModel modeloArbol = (javax.swing.tree.DefaultTreeModel) jTree1.getModel();
            modeloArbol.removeNodeFromParent(nodoSeleccionado);
            javax.swing.JOptionPane.showMessageDialog(this, "Carpeta y su contenido encolados para eliminación.");
        }

        actualizarTabla();

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

    if (bloqueInicio == -1) {
        nodoSeleccionado.setUserObject(nombreNuevo);
        ((javax.swing.tree.DefaultTreeModel) jTree1.getModel()).nodeChanged(nodoSeleccionado);
        return;
    }

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
        
        modeloArbol.setAsksAllowsChildren(true); 

        modeloArbol.insertNodeInto(nuevaCarpeta, nodoPadre, nodoPadre.getChildCount());
        jTree1.expandPath(new javax.swing.tree.TreePath(nodoPadre.getPath()));

        javax.swing.JOptionPane.showMessageDialog(this, "Carpeta '" + nombreCarpeta + "' creada con éxito.");
    }//GEN-LAST:event_btnCarpetaActionPerformed

    private void btnLeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeerActionPerformed
        // TODO add your handling code here:
        // en el JTree
        javax.swing.tree.DefaultMutableTreeNode nodoSeleccionado = (javax.swing.tree.DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
        if (nodoSeleccionado == null || nodoSeleccionado.isRoot()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Por favor, selecciona un archivo en el árbol para leer.");
            return;
        }

        String nombreArchivo = nodoSeleccionado.getUserObject().toString();

        // 2. Buscamos el archivo en el disco para ver dónde empieza y cuánto pesa
        int bloqueInicio = -1;
        int tamano = 0;
        for (int i = 0; i < miDisco.getTamano(); i++) {
            estructuras.Bloque b = miDisco.getBloque(i);
            if (b.isOcupado() && nombreArchivo.equals(b.getNombreArchivo())) {
                if (bloqueInicio == -1) bloqueInicio = i;
                tamano++;
            }
        }

        // 3. Validamos si es una carpeta (no tiene bloques físicos)
        if (bloqueInicio == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Has seleccionado una carpeta. Por favor, selecciona un archivo para leer.");
            return;
        }

        // 4. Encolamos la operación READ
        String duenoActual = cbRol.getSelectedItem().toString(); // Sabemos quién lo manda
        estructuras.Proceso pLeer = new estructuras.Proceso(nombreArchivo, bloqueInicio, tamano, "READ", duenoActual);
        pLeer.setEstado("En Espera");
        
        colaDisco.encolar(pLeer);
        historialProcesos.insertar(pLeer);
        actualizarTabla(); // Mostramos que entró a la fila
    }//GEN-LAST:event_btnLeerActionPerformed

    /**
     * @param args the command line arguments
     */
        public static void main(String args[]) {
        /* Configuración del Tema Oscuro (Dark Mode) */
        try {
            // Colores base del Dark Mode
            java.awt.Color fondoPrincipal = new java.awt.Color(34, 40, 49);
            java.awt.Color fondoPaneles = new java.awt.Color(49, 54, 63);
            java.awt.Color textoBlanco = new java.awt.Color(238, 238, 238);
            java.awt.Color seleccion = new java.awt.Color(57, 62, 70); // Color para selección del árbol

            javax.swing.UIManager.put("Panel.background", fondoPrincipal);
            javax.swing.UIManager.put("OptionPane.background", fondoPrincipal);
            javax.swing.UIManager.put("OptionPane.messageForeground", textoBlanco);
            
            // Botones
            javax.swing.UIManager.put("Button.background", new java.awt.Color(118, 171, 174));
            javax.swing.UIManager.put("Button.foreground", java.awt.Color.BLACK);
            
            // Tablas
            javax.swing.UIManager.put("Table.background", fondoPaneles);
            javax.swing.UIManager.put("Table.foreground", textoBlanco);
            javax.swing.UIManager.put("TableHeader.background", new java.awt.Color(20, 25, 30));
            javax.swing.UIManager.put("TableHeader.foreground", textoBlanco);
            javax.swing.UIManager.put("ScrollPane.background", fondoPrincipal);
            
            // Textos generales
            javax.swing.UIManager.put("TextArea.background", fondoPaneles);
            javax.swing.UIManager.put("TextArea.foreground", textoBlanco);
            javax.swing.UIManager.put("Label.foreground", textoBlanco);

            // ==========================================
            // CONFIGURACIÓN AVANZADA DEL JTREE (ÁRBOL)
            // ==========================================
            javax.swing.UIManager.put("Tree.background", fondoPaneles);
            javax.swing.UIManager.put("Tree.textForeground", textoBlanco);
            javax.swing.UIManager.put("Tree.selectionBackground", seleccion);
            javax.swing.UIManager.put("Tree.selectionForeground", java.awt.Color.WHITE);
            javax.swing.UIManager.put("Tree.selectionBorderColor", seleccion);
            javax.swing.UIManager.put("Tree.paintLines", false); // Quita las líneas punteadas viejas
            javax.swing.UIManager.put("Tree.rowHeight", 25); // Más espacio entre carpetas
            javax.swing.UIManager.put("Tree.leftChildIndent", 10);
            javax.swing.UIManager.put("Tree.rightChildIndent", 10);
            
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

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
    private javax.swing.JButton btnLeer;
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
    private javax.swing.JLabel lblMetricas;
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
   private void actualizarTablaLocks() {
        try {
            javax.swing.table.DefaultTableModel modeloAsignacion = (javax.swing.table.DefaultTableModel) jTable1.getModel();
            for (int i = 0; i < modeloAsignacion.getRowCount(); i++) {
                Object nombreObj = modeloAsignacion.getValueAt(i, 0);
                if (nombreObj != null) {
                    String nombreArchivo = nombreObj.toString();
                    
                    for (int j = 0; j < miDisco.getTamano(); j++) {
                        estructuras.Bloque b = miDisco.getBloque(j);
                        if (b.isOcupado() && nombreArchivo.equals(b.getNombreArchivo())) {
                            
                            String estadoLock = "Libre 🟢";
                            String tipo = b.getTipoLock();
                            
                            if (tipo.equals("LECTURA")) estadoLock = "Leyendo (" + b.getLectoresActivos() + ") 📖";
                            else if (tipo.equals("CREANDO") || tipo.equals("ESCRITURA")) estadoLock = "Creando 🔴";
                            else if (tipo.equals("ACTUALIZANDO")) estadoLock = "Actualizando 🟠";
                            else if (tipo.equals("ELIMINANDO")) estadoLock = "Eliminando ⚫";
                            
                            modeloAsignacion.setValueAt(estadoLock, i, 3); 
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {}
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
    
    nuevoArchivo.setAllowsChildren(false); 

    nodoDestino.add(nuevoArchivo);
    
    modeloArbol.reload();
}

    private void dibujarDisco() {
        if (panelDisco.getWidth() <= 0 || panelDisco.getHeight() <= 0) return;

        
        java.awt.image.BufferedImage lienzoInvisible = new java.awt.image.BufferedImage(
            panelDisco.getWidth(), panelDisco.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB);
        
        java.awt.Graphics2D g = lienzoInvisible.createGraphics();

        // Suavizado para bordes HD
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo oscuro
        g.setColor(new java.awt.Color(49, 54, 63));
        g.fillRect(0, 0, panelDisco.getWidth(), panelDisco.getHeight());

        int x = 10; 
        int y = 10; 
        int tamanoCuadro = 28; 

        // Dibujamos todos los cuadritos en el lienzo invisible
        for (int i = 0; i < miDisco.getTamano(); i++) {
            estructuras.Bloque bloque = miDisco.getBloque(i);

            if (i == posicionCabezal) {
                g.setColor(new java.awt.Color(0, 255, 127)); // Cabezal verde
            } else if (bloque.isOcupado()) {
                int hash = Math.abs(bloque.getNombreArchivo().hashCode());
                int r = (hash & 0xFF0000) >> 16;
                int gr = (hash & 0x00FF00) >> 8;
                int b = hash & 0x0000FF;
                g.setColor(new java.awt.Color(Math.max(r, 100), Math.max(gr, 100), Math.max(b, 100))); 
            } else {
                g.setColor(new java.awt.Color(70, 75, 85)); // Libre
            }

            g.fillRoundRect(x, y, tamanoCuadro, tamanoCuadro, 8, 8); 

            g.setColor(java.awt.Color.WHITE);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 10));
            String numero = String.format("%02d", i);
            g.drawString(numero, x + 5, y + 18);

            x += tamanoCuadro + 5; 
            
            if ((i + 1) % 15 == 0) {
                x = 10;
                y += tamanoCuadro + 5;
            }
        }
        
        g.dispose(); // Soltamos el pincel del lienzo invisible

       
        // ==========================================
        java.awt.Graphics gPantalla = panelDisco.getGraphics();
        if (gPantalla != null) {
            gPantalla.drawImage(lienzoInvisible, 0, 0, null);
            gPantalla.dispose();
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


        
}
        
        
        // ==========================================
// RENDERIZADOR PERSONALIZADO PARA ICONOS MODERNOS
// ==========================================
class ModernTreeRenderer extends javax.swing.tree.DefaultTreeCellRenderer {
    
    // Definimos los colores neón que ya usamos
    private final java.awt.Color colorCarpeta = new java.awt.Color(118, 171, 174); // Azul grisáceo
    private final java.awt.Color colorArchivo = new java.awt.Color(0, 255, 127); // Verde neón
    
    public ModernTreeRenderer() {
        // Hacemos el fondo del renderizador transparente para que use el del árbol
        setOpaque(false);
    }

    @Override
    public java.awt.Component getTreeCellRendererComponent(javax.swing.JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        javax.swing.tree.DefaultMutableTreeNode node = (javax.swing.tree.DefaultMutableTreeNode) value;
        setForeground(java.awt.Color.WHITE);

        if (expanded) {
            
            setIcon(createIcon(colorCarpeta, true));
        } else if (!leaf) {
            // Icono Carpeta Cerrada (Un círculo vacío)
            setIcon(createIcon(colorCarpeta, false));
        } else {
            setIcon(createLeafIcon(colorArchivo));
        }
        
        return this;
    }
    
    private javax.swing.Icon createIcon(java.awt.Color color, boolean filled) {
        return new javax.swing.Icon() {
            @Override
            public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                int size = 10;
                int drawX = x + (getIconWidth() - size) / 2;
                int drawY = y + (getIconHeight() - size) / 2;
                if (filled) {
                    g2.fillOval(drawX, drawY, size, size);
                } else {
                    g2.setStroke(new java.awt.BasicStroke(1.5f));
                    g2.drawOval(drawX, drawY, size, size);
                }
                g2.dispose();
            }
            @Override public int getIconWidth() { return 18; }
            @Override public int getIconHeight() { return 18; }
        };
    }
    
    // Método auxiliar para crear iconos de archivos (líneas)
    private javax.swing.Icon createLeafIcon(java.awt.Color color) {
        return new javax.swing.Icon() {
            @Override
            public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setColor(color);
                g2.setStroke(new java.awt.BasicStroke(2.0f));
                int height = 12;
                int drawX = x + getIconWidth() / 2;
                int drawY = y + (getIconHeight() - height) / 2;
                g2.drawLine(drawX, drawY, drawX, drawY + height);
                g2.dispose();
            }
            @Override public int getIconWidth() { return 18; }
            @Override public int getIconHeight() { return 18; }
        };
    }
}



   
