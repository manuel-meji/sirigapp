package vista.lotes;

import controlador.Controlador;
import controlador.FontLoader; // Asegúrate de tener la importación del FontLoader

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class panelRegistroLotes extends JPanel {
    private Controlador controlador;
    private JTextField txtNombre;
    private JComboBox<String> cmbEtapa;
    private JTextArea areaDescripcion;
    private JButton btnGuardar, btnModificar, btnLimpiar, btnEliminar;
    private JTable tablaLotes;
    private DefaultTableModel modeloTablaLotes;
    private boolean modoEdicion = false;
    private int idLoteSeleccionado = -1;

    // --- NUEVOS COMPONENTES PARA LA BÚSQUEDA ---
    private JTextField txtBuscar;

    // --- Fuentes estandarizadas para el diseño ---
    private final Font FONT_SUBTITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Bold.ttf", 24f);
    private final Font FONT_LABEL = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 16f);
    private final Font FONT_INPUT = FontLoader.loadFont("/resources/fonts/Montserrat-Light.ttf", 16f);
    private final Font FONT_BOTON = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 14f);

    public panelRegistroLotes(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
        add(createContentPanel(), BorderLayout.CENTER);
        cargarLotesEnTabla();
    }

    private JPanel createContentPanel() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(245, 246, 248));

        JLabel title = new JLabel("Registro y administración de lotes");
        title.setFont(FONT_SUBTITULO);
        title.setBorder(new EmptyBorder(20, 24, 8, 24));
        content.add(title, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 223, 230), 1),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // --- Panel de formulario con Layout Absoluto ---
        JPanel formPanel = new JPanel(null);
        formPanel.setOpaque(false);
        formPanel.setPreferredSize(new Dimension(0, 190));

        // --- Columna 1: Labels y Campos ---
        JLabel lblNombre = new JLabel("Nombre del Lote:");
        lblNombre.setFont(FONT_LABEL);
        lblNombre.setBounds(10, 10, 150, 30);
        formPanel.add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setFont(FONT_INPUT);
        txtNombre.setBounds(170, 10, 300, 30);
        formPanel.add(txtNombre);

        JLabel lblEtapa = new JLabel("Etapa del Lote:");
        lblEtapa.setFont(FONT_LABEL);
        lblEtapa.setBounds(10, 50, 150, 30);
        formPanel.add(lblEtapa);

        String[] etapas = {"Seleccione una etapa","Terneros Lactantes", "Destete","Vaquilla", "Torete","Novilla", "Vaca", "Toro", "Producción"};
        cmbEtapa = new JComboBox<>(etapas);
        cmbEtapa.setFont(FONT_INPUT);
        cmbEtapa.setBounds(170, 50, 300, 30);
        formPanel.add(cmbEtapa);

        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(FONT_LABEL);
        lblDescripcion.setBounds(10, 90, 150, 30);
        formPanel.add(lblDescripcion);

        areaDescripcion = new JTextArea();
        areaDescripcion.setFont(FONT_INPUT);
        JScrollPane scrollPaneDescripcion = new JScrollPane(areaDescripcion);
        scrollPaneDescripcion.setBounds(170, 90, 300, 80);
        formPanel.add(scrollPaneDescripcion);

        // --- Columna 2: Botones del Formulario ---
        btnGuardar = new JButton("Registrar Lote");
        btnGuardar.setIcon(new ImageIcon("src/resources/images/icon-guardar.png"));
        btnGuardar.setHorizontalTextPosition(SwingConstants.LEFT);

        btnGuardar.setFont(FONT_BOTON);
        btnGuardar.setBackground(controlador.estilos.COLOR_GUARDAR);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBounds(500, 10, 200, 45);
        formPanel.add(btnGuardar);

        btnLimpiar = new JButton("Limpiar Campos");
        btnLimpiar.setIcon(new ImageIcon("src/resources/images/icon-limpiar.png"));
        btnLimpiar.setHorizontalTextPosition(SwingConstants.LEFT);
        btnLimpiar.setBackground(controlador.estilos.COLOR_LIMPIAR);
        btnLimpiar.setFont(FONT_BOTON);
        btnLimpiar.setBackground(controlador.estilos.COLOR_LIMPIAR); // Gris neutro
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setBounds(500, 65, 200, 45);
        formPanel.add(btnLimpiar);

        card.add(formPanel, BorderLayout.NORTH);

        // --- Panel Intermedio para la Búsqueda ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        JLabel lblBuscar = new JLabel("Buscar Lote:");
        lblBuscar.setFont(FONT_LABEL);
        lblBuscar.setBorder(new EmptyBorder(0, 0, 0, 10)); // Margen a la derecha
        searchPanel.add(lblBuscar);

        txtBuscar = new JTextField(30); // Ancho del campo de búsqueda
        txtBuscar.setFont(FONT_INPUT);
        searchPanel.add(txtBuscar);
        
        // --- Panel Central que contendrá la búsqueda y la tabla ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // --- Tabla de Lotes ---
        String[] columnas = {"ID Lote", "Nombre", "Etapa", "Descripción"};
        modeloTablaLotes = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaLotes = new JTable(modeloTablaLotes);
        tablaLotes.setRowHeight(28);
        tablaLotes.setFont(FONT_INPUT.deriveFont(14f));
        tablaLotes.getTableHeader().setFont(FONT_LABEL.deriveFont(14f));
        JScrollPane scrollPaneTabla = new JScrollPane(tablaLotes);
        centerPanel.add(scrollPaneTabla, BorderLayout.CENTER);

        card.add(centerPanel, BorderLayout.CENTER);


        // --- Botones de acción de la tabla ---
        JPanel tableButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableButtonsPanel.setOpaque(false);
        
        btnModificar = new JButton("Modificar Lote");
        btnModificar.setIcon(new ImageIcon("src/resources/images/icon-editar.png"));
        btnModificar.setHorizontalTextPosition(SwingConstants.LEFT);
        btnModificar.setFont(FONT_BOTON);
        btnModificar.setBackground(controlador.estilos.COLOR_MODIFICAR);
        btnModificar.setForeground(Color.WHITE);
        btnModificar.setPreferredSize(new Dimension(180, 40));

        btnEliminar = new JButton("Eliminar Lote");
        btnEliminar.setIcon(new ImageIcon("src/resources/images/icon-eliminar.png"));
        btnEliminar.setHorizontalTextPosition(SwingConstants.LEFT);

        btnEliminar.setFont(FONT_BOTON);
        btnEliminar.setBackground(controlador.estilos.COLOR_ELIMINAR);
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setPreferredSize(new Dimension(180, 40));

        tableButtonsPanel.add(btnModificar);
        tableButtonsPanel.add(btnEliminar);
        card.add(tableButtonsPanel, BorderLayout.SOUTH);

        content.add(card, BorderLayout.CENTER);

        // --- LISTENERS ---

        // >>> INICIO: LÓGICA DE BÚSQUEDA DINÁMICA <<<
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarTabla();
            }
        });
        // >>> FIN: LÓGICA DE BÚSQUEDA DINÁMICA <<<


        btnGuardar.addActionListener(e -> {
            if (cmbEtapa.getSelectedIndex() == 0 || txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una etapa y un nombre para el lote.", "Error de validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (modoEdicion) {
                guardarCambiosLote();
            } else {
                registrarLote();
            }
        });

        btnModificar.addActionListener(e -> {
            int filaSeleccionada = tablaLotes.getSelectedRow();
            if (filaSeleccionada != -1) {
                entrarModoEdicion(filaSeleccionada);
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un lote de la tabla para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnLimpiar.addActionListener(e -> {
            if (modoEdicion) {
                salirModoEdicion();
            } else {
                limpiarCampos();
            }
        });

        btnEliminar.addActionListener(e -> {
            int filaSeleccionada = tablaLotes.getSelectedRow();
            if (filaSeleccionada != -1) {
                if (JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el lote seleccionado?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    int idLote = (int) modeloTablaLotes.getValueAt(filaSeleccionada, 0);
                    controlador.eliminarLote(idLote); 
                    cargarLotesEnTabla();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un lote de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        return content;
    }
    
    // --- NUEVO MÉTODO PARA CENTRALIZAR LA LÓGICA DE FILTRADO ---
    private void filtrarTabla() {
        String textoBusqueda = txtBuscar.getText().trim();
        if (textoBusqueda.isEmpty()) {
            cargarLotesEnTabla(); // Si no hay texto, muestra todos los lotes
        } else {
            // Llama al nuevo método en el controlador para buscar
            List<Object[]> lotesFiltrados = controlador.buscarLotes(textoBusqueda);
            actualizarTablaConNuevosDatos(lotesFiltrados);
        }
    }

    // --- NUEVO MÉTODO PARA ACTUALIZAR LA TABLA ---
    private void actualizarTablaConNuevosDatos(List<Object[]> lotes) {
        modeloTablaLotes.setRowCount(0); // Limpia la tabla
        for (Object[] fila : lotes) {
            modeloTablaLotes.addRow(fila); // Agrega las nuevas filas
        }
    }
    
    public void cargarLotesEnTabla() {
        modeloTablaLotes.setRowCount(0);
        java.util.List<Object[]> lotes = controlador.obtenerDetallesTodosLotes();
        for (Object[] fila : lotes) {
            modeloTablaLotes.addRow(fila);
        }
    }

    private void entrarModoEdicion(int fila) {
        modoEdicion = true;
        idLoteSeleccionado = (int) modeloTablaLotes.getValueAt(fila, 0);
        txtNombre.setText(modeloTablaLotes.getValueAt(fila, 1).toString());
        cmbEtapa.setSelectedItem(modeloTablaLotes.getValueAt(fila, 2).toString());
        areaDescripcion.setText(modeloTablaLotes.getValueAt(fila, 3).toString());
        btnGuardar.setText("Guardar Cambios");
        btnLimpiar.setText("Cancelar Edición");
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        tablaLotes.setEnabled(false);
        txtBuscar.setEnabled(false); // Deshabilitar búsqueda en modo edición
    }

    private void salirModoEdicion() {
        modoEdicion = false;
        idLoteSeleccionado = -1;
        limpiarCampos();
        btnGuardar.setText("Registrar Lote");
        btnLimpiar.setText("Limpiar Campos");
        btnModificar.setEnabled(true);
        btnEliminar.setEnabled(true);
        tablaLotes.setEnabled(true);
        txtBuscar.setEnabled(true); // Habilitar búsqueda al salir de edición
    }

    private void registrarLote() {
        String nombre = txtNombre.getText().trim();
        String etapa = cmbEtapa.getSelectedItem().toString();
        String descripcion = areaDescripcion.getText().trim();
        controlador.registrarNuevoLote(nombre, etapa, descripcion);
        cargarLotesEnTabla();
        limpiarCampos();
    }

    private void guardarCambiosLote() {
        String nombre = txtNombre.getText().trim();
        String etapa = cmbEtapa.getSelectedItem().toString();
        String descripcion = areaDescripcion.getText().trim();
        controlador.modificarLote(idLoteSeleccionado, nombre, etapa, descripcion);
        cargarLotesEnTabla();
        salirModoEdicion();
    }

    public void limpiarCampos() {
        txtNombre.setText("");
        cmbEtapa.setSelectedIndex(0);
        areaDescripcion.setText("");
        tablaLotes.clearSelection();
    }
}