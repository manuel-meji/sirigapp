package vista.lotes;

import controlador.Controlador;
import controlador.FontLoader;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class panelHistorialLotes extends JPanel {

    private Controlador controlador;
    private JComboBox<String> cmbAnimalesId;
    private JComboBox<String> cmbLotesDestinoId;
    private JDateChooser dateChooser;
    private JButton btnGuardar, btnModificarMovimiento, btnEliminarMovimiento, btnLimpiar;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTablaHistorial;
    private boolean modoEdicion = false;
    private int idMovimientoSeleccionado = -1;
    private String codigoAnimalEditando = null;
    private DefaultComboBoxModel<String> comboModelAnimales;
    private final String PLACEHOLDER_ANIMAL = "Escriba para buscar un ID...";

    // --- NUEVO COMPONENTE PARA LA BÚSQUEDA ---
    private JTextField txtBuscar;

    private final Font FONT_SUBTITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Bold.ttf", 24f);
    private final Font FONT_LABEL = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 16f);
    private final Font FONT_INPUT = FontLoader.loadFont("/resources/fonts/Montserrat-Light.ttf", 16f);
    private final Font FONT_BOTON = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 14f);

    public panelHistorialLotes(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
        add(createContentPanel(), BorderLayout.CENTER);
        cargarDatosIniciales();
    }

    private JPanel createContentPanel() {
        // --- El diseño se mantiene intacto ---
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(245, 246, 248));
        JLabel title = new JLabel("Historial de movimientos de animales");
        title.setFont(FONT_SUBTITULO);
        title.setBorder(new EmptyBorder(20, 24, 8, 24));
        content.add(title, BorderLayout.NORTH);
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 223, 230), 1), new EmptyBorder(16, 16, 16, 16)));
        JPanel formPanel = new JPanel(null);
        formPanel.setOpaque(false);
        formPanel.setPreferredSize(new Dimension(0, 140));
        JLabel lblAnimalId = new JLabel("Buscar Animal (ID):");
        lblAnimalId.setFont(FONT_LABEL);
        lblAnimalId.setBounds(10, 10, 190, 30);
        formPanel.add(lblAnimalId);
        comboModelAnimales = new DefaultComboBoxModel<>();
        cmbAnimalesId = new JComboBox<>(comboModelAnimales);
        cmbAnimalesId.setFont(FONT_INPUT);
        cmbAnimalesId.setEditable(true);
        cmbAnimalesId.setBounds(200, 10, 280, 30);
        formPanel.add(cmbAnimalesId);
        JLabel lblLoteDestinoId = new JLabel("Lote de Destino:");
        lblLoteDestinoId.setFont(FONT_LABEL);
        lblLoteDestinoId.setBounds(10, 50, 190, 30);
        formPanel.add(lblLoteDestinoId);
        cmbLotesDestinoId = new JComboBox<>();
        cmbLotesDestinoId.setFont(FONT_INPUT);
        cmbLotesDestinoId.setBounds(200, 50, 280, 30);
        formPanel.add(cmbLotesDestinoId);
        JLabel lblFecha = new JLabel("Fecha del Movimiento:");
        lblFecha.setFont(FONT_LABEL);
        lblFecha.setBounds(10, 90, 190, 30);
        formPanel.add(lblFecha);
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setFont(FONT_INPUT);
        dateChooser.setBounds(200, 90, 280, 30);
        formPanel.add(dateChooser);
        btnGuardar = new JButton("Registrar Movimiento");
        btnGuardar.setIcon(new ImageIcon("src/resources/images/icon-guardar.png"));
        btnGuardar.setHorizontalTextPosition(SwingConstants.LEFT);

        btnGuardar.setFont(FONT_BOTON);
        btnGuardar.setBackground(controlador.estilos.COLOR_GUARDAR);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBounds(500, 10, 220, 45);
        formPanel.add(btnGuardar);
        btnLimpiar = new JButton("Limpiar Campos");
        btnLimpiar.setIcon(new ImageIcon("src/resources/images/icon-limpiar.png"));
        btnLimpiar.setHorizontalTextPosition(SwingConstants.LEFT);
        btnLimpiar.setFont(FONT_BOTON);
        btnLimpiar.setBackground(controlador.estilos.COLOR_LIMPIAR);
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setBounds(500, 65, 220, 45);
        formPanel.add(btnLimpiar);
        card.add(formPanel, BorderLayout.NORTH);
        
        // --- Panel Intermedio para la Búsqueda y la Tabla ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        // --- Panel de Búsqueda ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        JLabel lblBuscar = new JLabel("Buscar en Historial:");
        lblBuscar.setFont(FONT_LABEL);
        lblBuscar.setBorder(new EmptyBorder(0, 0, 0, 10));
        searchPanel.add(lblBuscar);

        txtBuscar = new JTextField(30);
        txtBuscar.setFont(FONT_INPUT);
        searchPanel.add(txtBuscar);
        
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // --- Tabla de Historial ---
        String[] columnas = {"ID Mov.", "ID Animal", "Lote Anterior", "Lote Posterior", "Fecha"};
        modeloTablaHistorial = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaHistorial = new JTable(modeloTablaHistorial);
        tablaHistorial.setRowHeight(28);
        tablaHistorial.setFont(FONT_INPUT.deriveFont(14f));
        tablaHistorial.getTableHeader().setFont(FONT_LABEL.deriveFont(14f));
        JScrollPane scrollPaneTabla = new JScrollPane(tablaHistorial);
        centerPanel.add(scrollPaneTabla, BorderLayout.CENTER);
        
        card.add(centerPanel, BorderLayout.CENTER);
        
        JPanel tableButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableButtonsPanel.setOpaque(false);
        btnModificarMovimiento = new JButton("Modificar");
        btnModificarMovimiento.setIcon(new ImageIcon("src/resources/images/icon-editar.png"));
        btnModificarMovimiento.setHorizontalTextPosition(SwingConstants.LEFT);
        btnModificarMovimiento.setFont(FONT_BOTON);
        btnModificarMovimiento.setBackground(controlador.estilos.COLOR_MODIFICAR);
        btnModificarMovimiento.setForeground(Color.WHITE);
        btnModificarMovimiento.setPreferredSize(new Dimension(200, 40));
        btnEliminarMovimiento = new JButton("Eliminar");
        btnEliminarMovimiento.setIcon(new ImageIcon("src/resources/images/icon-eliminar.png"));
        btnEliminarMovimiento.setHorizontalTextPosition(SwingConstants.LEFT);
        btnEliminarMovimiento.setFont(FONT_BOTON);
        btnEliminarMovimiento.setBackground(controlador.estilos.COLOR_ELIMINAR);
        btnEliminarMovimiento.setForeground(Color.WHITE);
        btnEliminarMovimiento.setPreferredSize(new Dimension(200, 40));
        tableButtonsPanel.add(btnModificarMovimiento);
        tableButtonsPanel.add(btnEliminarMovimiento);
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

        JTextField editor = (JTextField) cmbAnimalesId.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN && e.getKeyCode() != KeyEvent.VK_ENTER && e.getKeyCode() != KeyEvent.VK_ESCAPE) {
                    filtrarComboAnimales(editor.getText());
                }
            }
        });
        editor.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (editor.getText().equals(PLACEHOLDER_ANIMAL)) {
                    editor.setText("");
                    editor.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (editor.getText().trim().isEmpty()) {
                    restaurarPlaceholderAnimal();
                }
            }
        });

        btnGuardar.addActionListener(e -> {
            if (modoEdicion) {
                guardarCambiosMovimiento();
            } else {
                registrarMovimiento();
            }
        });
        
        btnLimpiar.addActionListener(e -> {
            if (modoEdicion) {
                salirModoEdicion();
            } else {
                limpiarCampos();
            }
        });

        btnModificarMovimiento.addActionListener(e -> {
            int fila = tablaHistorial.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un movimiento de la tabla para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idMovimiento = (int) modeloTablaHistorial.getValueAt(fila, 0);
            String codigoAnimal = (String) modeloTablaHistorial.getValueAt(fila, 1);
            if (controlador.esUltimoMovimiento(idMovimiento, codigoAnimal)) {
                entrarModoEdicion(fila);
            } else {
                JOptionPane.showMessageDialog(this, "Solo se puede modificar el movimiento más reciente de un animal.", "Acción no permitida", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEliminarMovimiento.addActionListener(e -> {
            int filaSeleccionada = tablaHistorial.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione el movimiento que desea revertir.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idMovimiento = (int) modeloTablaHistorial.getValueAt(filaSeleccionada, 0);
            String codigoAnimal = (String) modeloTablaHistorial.getValueAt(filaSeleccionada, 1);
            
            // Reutilizamos la lógica de verificación del controlador
            if(controlador.esUltimoMovimiento(idMovimiento, codigoAnimal)) {
                Object idLoteAnterior = modeloTablaHistorial.getValueAt(filaSeleccionada, 2);
                controlador.revertirMovimientoHistorial(idMovimiento, codigoAnimal, idLoteAnterior);
                cargarHistorialEnTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede revertir este movimiento. No es el más reciente para este animal.", "Acción no permitida", JOptionPane.ERROR_MESSAGE);
            }
        });

        return content;
    }

    // --- NUEVO MÉTODO PARA CENTRALIZAR LA LÓGICA DE FILTRADO ---
    private void filtrarTabla() {
        String textoBusqueda = txtBuscar.getText().trim();
        if (textoBusqueda.isEmpty()) {
            cargarHistorialEnTabla(); // Si no hay texto, muestra todo el historial
        } else {
            // Llama al nuevo método en el controlador para buscar
            List<Object[]> historialFiltrado = controlador.buscarMovimientosHistorial(textoBusqueda);
            actualizarTablaConNuevosDatos(historialFiltrado);
        }
    }
    
    // --- NUEVO MÉTODO PARA ACTUALIZAR LA TABLA ---
    private void actualizarTablaConNuevosDatos(List<Object[]> movimientos) {
        modeloTablaHistorial.setRowCount(0); // Limpia la tabla
        for (Object[] fila : movimientos) {
            modeloTablaHistorial.addRow(fila); // Agrega las nuevas filas
        }
    }

    private void entrarModoEdicion(int fila) {
        modoEdicion = true;
        idMovimientoSeleccionado = (int) modeloTablaHistorial.getValueAt(fila, 0);
        String idAnimal = modeloTablaHistorial.getValueAt(fila, 1).toString();
        this.codigoAnimalEditando = idAnimal;
        int idLotePosterior = (int) modeloTablaHistorial.getValueAt(fila, 3);
        Date fecha = (Date) modeloTablaHistorial.getValueAt(fila, 4);

        JTextField editorAnimal = (JTextField) cmbAnimalesId.getEditor().getEditorComponent();
        editorAnimal.setForeground(Color.BLACK);
        editorAnimal.setText(idAnimal);

        dateChooser.setDate(fecha);

        for (int i = 0; i < cmbLotesDestinoId.getItemCount(); i++) {
            String item = cmbLotesDestinoId.getItemAt(i);
            if (item.startsWith(idLotePosterior + " - ")) {
                cmbLotesDestinoId.setSelectedIndex(i);
                break;
            }
        }

        btnGuardar.setText("Guardar Cambios");
        btnLimpiar.setText("Cancelar Edición");
        btnModificarMovimiento.setEnabled(false);
        btnEliminarMovimiento.setEnabled(false);
        tablaHistorial.setEnabled(false);
        cmbAnimalesId.setEnabled(false);
        txtBuscar.setEnabled(false); // Deshabilitar búsqueda en modo edición
    }

    private void guardarCambiosMovimiento() {
        try {
            int selectedIndex = cmbLotesDestinoId.getSelectedIndex();
            String loteSeleccionado = selectedIndex > 0 ? (String) cmbLotesDestinoId.getSelectedItem() : null;
            if (loteSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un lote de destino válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dateChooser.getDate() == null) {
                JOptionPane.showMessageDialog(this, "La fecha no puede estar vacía.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int idLoteDestino = Integer.parseInt(loteSeleccionado.split(" - ")[0]);
            java.sql.Date fechaSql = new java.sql.Date(dateChooser.getDate().getTime());

            controlador.modificarUltimoMovimiento(idMovimientoSeleccionado, this.codigoAnimalEditando, idLoteDestino, fechaSql);

            cargarHistorialEnTabla();
            salirModoEdicion();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Verificación", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            salirModoEdicion();
        }
    }

    private void salirModoEdicion() {
        modoEdicion = false;
        idMovimientoSeleccionado = -1;
        this.codigoAnimalEditando = null;
        btnGuardar.setText("Registrar Movimiento");
        btnLimpiar.setText("Limpiar Campos");
        btnModificarMovimiento.setEnabled(true);
        btnEliminarMovimiento.setEnabled(true);
        tablaHistorial.setEnabled(true);
        cmbAnimalesId.setEnabled(true);
        txtBuscar.setEnabled(true); // Habilitar búsqueda al salir de edición
        limpiarCampos();
    }

    private void cargarDatosIniciales() {
        cargarLotesEnCombo();
        cargarHistorialEnTabla();
        restaurarPlaceholderAnimal();
        filtrarComboAnimales("");
    }

    public void cargarLotesEnCombo() {
        cmbLotesDestinoId.removeAllItems();
        cmbLotesDestinoId.addItem("Seleccione un lote...");
        List<String> lotes = controlador.obtenerLotesParaComboBox();
        for (String loteFormateado : lotes) {
            cmbLotesDestinoId.addItem(loteFormateado);
        }
    }

    private void registrarMovimiento() {
        String codigoAnimal = ((JTextField) cmbAnimalesId.getEditor().getEditorComponent()).getText();
        int selectedIndex = cmbLotesDestinoId.getSelectedIndex();
        String loteSeleccionado = selectedIndex > 0 ? (String) cmbLotesDestinoId.getSelectedItem() : null;
        if (codigoAnimal.trim().isEmpty() || codigoAnimal.equals(PLACEHOLDER_ANIMAL) || loteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un animal y un lote de destino válidos.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (dateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una fecha.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int idLoteDestino = Integer.parseInt(loteSeleccionado.split(" - ")[0]);
            java.sql.Date fechaSql = new java.sql.Date(dateChooser.getDate().getTime());
            controlador.registrarMovimientoAnimal(codigoAnimal, idLoteDestino, fechaSql);
            cargarHistorialEnTabla();
            limpiarCampos();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(this, "El lote seleccionado no tiene un formato de ID válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

public void cargarHistorialEnTabla() {
    // 1. Limpiamos la tabla como antes.
    modeloTablaHistorial.setRowCount(0);
    
    // 2. Obtenemos la lista de datos ya procesada desde el controlador.
    java.util.List<Object[]> historial = controlador.obtenerHistorialMovimientos();
    
    // 3. Simplemente iteramos sobre la lista y añadimos cada fila al modelo.
    //    ¡No hay ResultSet, no hay try-catch, no hay SQLException!
    for (Object[] fila : historial) {
        modeloTablaHistorial.addRow(fila);
    }
}

private void filtrarComboAnimales(String busqueda) {
    if (busqueda.equals(PLACEHOLDER_ANIMAL)) {
        return;
    }
    
    // 1. Obtenemos la lista de Strings. ¡No hay try-catch ni SQLException aquí!
    java.util.List<String> codigos = controlador.buscarCodigosAnimales(busqueda);
    
    // 2. Guardamos el texto actual que el usuario ha escrito.
    String textoActual = ((JTextField) cmbAnimalesId.getEditor().getEditorComponent()).getText();
    
    // 3. Limpiamos el modelo del ComboBox.
    comboModelAnimales.removeAllElements();
    
    // 4. Llenamos el modelo con los resultados de la lista.
    for (String codigo : codigos) {
        comboModelAnimales.addElement(codigo);
    }
    
    // 5. Restauramos el texto y manejamos la visibilidad del popup.
    //    Esta lógica no cambia.
    ((JTextField) cmbAnimalesId.getEditor().getEditorComponent()).setText(textoActual);
    if (comboModelAnimales.getSize() > 0 && !textoActual.isEmpty()) {
        if (cmbAnimalesId.isShowing()) {
            cmbAnimalesId.showPopup();
        }
    } else {
        cmbAnimalesId.hidePopup();
    }
}

    private void restaurarPlaceholderAnimal() {
        JTextField editor = (JTextField) cmbAnimalesId.getEditor().getEditorComponent();
        editor.setForeground(Color.GRAY);
        editor.setText(PLACEHOLDER_ANIMAL);
    }

    private void limpiarCampos() {
        restaurarPlaceholderAnimal();
        filtrarComboAnimales("");
        cmbLotesDestinoId.setSelectedIndex(0);
        dateChooser.setDate(new Date());
        tablaHistorial.clearSelection();
    }
}