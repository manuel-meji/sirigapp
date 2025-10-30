package vista.lotes;

import controlador.Controlador;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

public class panelHistorialLotes extends JPanel {

    private Controlador controlador;
    private JLabel lblTitulo, lblAnimalId, lblLoteDestinoId, lblFecha;
    private JComboBox<String> cmbAnimalesId;
    private JComboBox<String> cmbLotesDestinoId;
    private JDateChooser dateChooser;
    private JButton btnRegistrarMovimiento, btnModificarMovimiento, btnLimpiar, btnEliminarMovimiento;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTablaHistorial;
    private JScrollPane scrollPaneTabla;
    private boolean modoEdicion = false;
    private int idMovimientoSeleccionado = -1;

    // --- CAMBIO CLAVE: Se declara un modelo persistente para el ComboBox ---
    private DefaultComboBoxModel<String> comboModelAnimales;
    private final String PLACEHOLDER_ANIMAL = "Escriba para buscar un ID...";

    public panelHistorialLotes(Controlador controlador) {
        this.controlador = controlador;
        initComponents();
        cargarDatosIniciales();
    }

    private void initComponents() {
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitulo = new JLabel("Registro y Consulta de Movimientos de Animales");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setBounds(50, 20, 700, 30);
        add(lblTitulo);

        lblAnimalId = new JLabel("Buscar Animal (ID):");
        lblAnimalId.setFont(new Font("Arial", Font.PLAIN, 16));
        lblAnimalId.setBounds(50, 80, 150, 25);
        add(lblAnimalId);

        // --- CAMBIO CLAVE: Se inicializa el modelo y se asigna UNA SOLA VEZ ---
        comboModelAnimales = new DefaultComboBoxModel<>();
        cmbAnimalesId = new JComboBox<>(comboModelAnimales);
        cmbAnimalesId.setEditable(true);
        cmbAnimalesId.setBounds(220, 80, 250, 25);
        add(cmbAnimalesId);

        JTextField editor = (JTextField) cmbAnimalesId.getEditor().getEditorComponent();

        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN
                        && e.getKeyCode() != KeyEvent.VK_ENTER && e.getKeyCode() != KeyEvent.VK_ESCAPE) {
                    filtrarComboAnimales(editor.getText());
                }
            }
        });

        editor.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (editor.getText().equals(PLACEHOLDER_ANIMAL)) {
                    editor.setText("");
                    editor.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (editor.getText().trim().isEmpty()) {
                    restaurarPlaceholderAnimal();
                }
            }
        });

        // El resto de los componentes se mantienen igual
        lblLoteDestinoId = new JLabel("ID Lote Destino:");
        lblLoteDestinoId.setFont(new Font("Arial", Font.PLAIN, 16));
        lblLoteDestinoId.setBounds(50, 120, 150, 25);
        add(lblLoteDestinoId);
        cmbLotesDestinoId = new JComboBox<>();
        cmbLotesDestinoId.setBounds(220, 120, 250, 25);
        add(cmbLotesDestinoId);
        lblFecha = new JLabel("Fecha del Movimiento:");
        lblFecha.setFont(new Font("Arial", Font.PLAIN, 16));
        lblFecha.setBounds(50, 160, 180, 25);
        add(lblFecha);
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setBounds(220, 160, 250, 25);
        add(dateChooser);
        btnRegistrarMovimiento = new JButton("Registrar Movimiento");
        btnRegistrarMovimiento.setBounds(500, 80, 200, 30);
        btnRegistrarMovimiento.setBackground(new Color(0x2BA76B));
        btnRegistrarMovimiento.setForeground(Color.WHITE);
        add(btnRegistrarMovimiento);
        btnModificarMovimiento = new JButton("Modificar Movimiento");
        btnModificarMovimiento.setBounds(500, 120, 200, 30);
        btnModificarMovimiento.setBackground(new Color(0xFF054FBE));
        btnModificarMovimiento.setForeground(Color.WHITE);
        add(btnModificarMovimiento);
        btnLimpiar = new JButton("Limpiar Campos");
        btnLimpiar.setBounds(710, 80, 200, 30);
        add(btnLimpiar);
        btnEliminarMovimiento = new JButton("Eliminar Movimiento");
        btnEliminarMovimiento.setBounds(710, 120, 200, 30);
        btnEliminarMovimiento.setBackground(new Color(220, 53, 69));
        btnEliminarMovimiento.setForeground(Color.WHITE);
        add(btnEliminarMovimiento);
        String[] columnas = {"ID Movimiento", "ID Animal", "ID Lote Anterior", "ID Lote Posterior", "Fecha"};
        modeloTablaHistorial = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaHistorial = new JTable(modeloTablaHistorial);
        scrollPaneTabla = new JScrollPane(tablaHistorial);
        scrollPaneTabla.setBounds(50, 220, 966, 410);
        add(scrollPaneTabla);
        btnRegistrarMovimiento.addActionListener(e -> {
            if (modoEdicion) {
                guardarCambiosMovimiento();
            } else {
                registrarMovimiento();
        
            }});
        btnModificarMovimiento.addActionListener(e -> {
            int fila = tablaHistorial.getSelectedRow();
            if (fila != -1) {
                entrarModoEdicion(fila); 
            }else {
                JOptionPane.showMessageDialog(this, "Seleccione un movimiento de la tabla para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnLimpiar.addActionListener(e -> {
            if (modoEdicion) {
                salirModoEdicion();
            } else {
                limpiarCampos();
        
            }});
        btnEliminarMovimiento.addActionListener(e -> {
            int filaSeleccionada = tablaHistorial.getSelectedRow();
            if (filaSeleccionada != -1) {
                // Obtenemos todos los datos necesarios de la fila seleccionada
                int idMovimiento = (int) modeloTablaHistorial.getValueAt(filaSeleccionada, 0);
                String codigoAnimal = (String) modeloTablaHistorial.getValueAt(filaSeleccionada, 1);
                // Ojo: idLoteAnterior puede ser el String "N/A" o un Integer
                Object idLoteAnterior = modeloTablaHistorial.getValueAt(filaSeleccionada, 2);

                // Llamamos al nuevo método del controlador con todos los datos
                controlador.revertirMovimientoHistorial(idMovimiento, codigoAnimal, idLoteAnterior);
                
                // Recargamos la tabla para ver el resultado
                cargarHistorialEnTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione el movimiento que desea revertir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void cargarDatosIniciales() {
        cargarLotesEnCombo();
        cargarHistorialEnTabla();
        // Carga el placeholder al final
        restaurarPlaceholderAnimal();
    }

    // --- CAMBIO CLAVE: Lógica de placeholder y carga inicial simplificada ---
    private void restaurarPlaceholderAnimal() {
        comboModelAnimales.removeAllElements();
        JTextField editor = (JTextField) cmbAnimalesId.getEditor().getEditorComponent();
        editor.setForeground(Color.GRAY);
        editor.setText(PLACEHOLDER_ANIMAL);
    }

    // --- CAMBIO CLAVE: El método de filtrado AHORA MODIFICA el modelo existente ---
    private void filtrarComboAnimales(String busqueda) {
        if (busqueda.equals(PLACEHOLDER_ANIMAL)) {
            return;
        }

        try {
            ResultSet rs = controlador.buscarCodigosAnimales(busqueda);

            // Se limpia el modelo existente, NO se crea uno nuevo
            comboModelAnimales.removeAllElements();

            if (rs != null) {
                while (rs.next()) {
                    comboModelAnimales.addElement(rs.getString("codigo"));
                }
            }

            // Se restaura el texto y se muestra el popup
            ((JTextField) cmbAnimalesId.getEditor().getEditorComponent()).setText(busqueda);
            cmbAnimalesId.setSelectedItem(busqueda);

            if (comboModelAnimales.getSize() > 0) {
                cmbAnimalesId.showPopup();
            } else {
                cmbAnimalesId.hidePopup();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al filtrar animales: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        restaurarPlaceholderAnimal();
        cmbLotesDestinoId.setSelectedIndex(0);
        dateChooser.setDate(null);
        tablaHistorial.clearSelection();
    }

    private void registrarMovimiento() {
        String codigoAnimal = ((JTextField) cmbAnimalesId.getEditor().getEditorComponent()).getText();

        if (codigoAnimal.trim().isEmpty() || codigoAnimal.equals(PLACEHOLDER_ANIMAL) || cmbLotesDestinoId.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un ID de animal y un ID de lote válidos.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (dateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una fecha para el movimiento.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idLoteDestino = Integer.parseInt(cmbLotesDestinoId.getSelectedItem().toString());
        Date fechaUtil = dateChooser.getDate();
        java.sql.Date fechaSql = new java.sql.Date(fechaUtil.getTime());
        controlador.registrarMovimientoAnimal(codigoAnimal, idLoteDestino, fechaSql);

        cargarHistorialEnTabla();
        limpiarCampos();
    }

    // El resto de los métodos (`cargarLotesEnCombo`, `cargarHistorial`, `guardarCambios`, `entrarModoEdicion`, etc.)
    // se mantienen correctos.
    public void cargarLotesEnCombo() {
        cmbLotesDestinoId.removeAllItems();
        cmbLotesDestinoId.addItem("Seleccione un ID...");
        ResultSet rs = controlador.obtenerTodosLosLotes();
        try {
            if (rs != null) {
                while (rs.next()) {
                    cmbLotesDestinoId.addItem(String.valueOf(rs.getInt("id")));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar IDs de lotes: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarHistorialEnTabla() {
        modeloTablaHistorial.setRowCount(0);
        ResultSet rs = controlador.obtenerHistorialMovimientos();
        try {
            if (rs != null) {
                while (rs.next()) {
                    Object[] fila = {rs.getInt("id"), rs.getString("id_animal"), rs.getObject("id_lote_anterior") == null ? "N/A" : rs.getInt("id_lote_anterior"), rs.getInt("id_lote_posterior"), rs.getDate("fecha")};
                    modeloTablaHistorial.addRow(fila);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar el historial: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

// REEMPLAZA ESTE MÉTODO EN TU CLASE 'panelHistorialLotes.java'

private void guardarCambiosMovimiento() {
    // La obtención de datos es la misma
    String codigoAnimal = ((JTextField) cmbAnimalesId.getEditor().getEditorComponent()).getText();
    if (codigoAnimal.trim().isEmpty() || codigoAnimal.equals(PLACEHOLDER_ANIMAL) || cmbLotesDestinoId.getSelectedIndex() <= 0) {
        JOptionPane.showMessageDialog(this, "Debe seleccionar un ID de animal y un ID de lote válidos.", "Error de validación", JOptionPane.ERROR_MESSAGE);
        return;
    }
    int idLoteDestino = Integer.parseInt(cmbLotesDestinoId.getSelectedItem().toString());
    Date fechaUtil = dateChooser.getDate();
    java.sql.Date fechaSql = new java.sql.Date(fechaUtil.getTime());

    // --- CAMBIO CLAVE: Se llama al nuevo método del controlador ---
    // Nota: Estamos asumiendo que el código del animal no se puede cambiar en el modo de edición.
    // El 'idAnimal' original se obtiene de la tabla cuando se entra en modo edición.
    String idAnimalOriginal = (String) modeloTablaHistorial.getValueAt(tablaHistorial.getSelectedRow(), 1);
    controlador.modificarUltimoMovimiento(idMovimientoSeleccionado, idAnimalOriginal, idLoteDestino, fechaSql);

    cargarHistorialEnTabla();
    salirModoEdicion();
}

    private void entrarModoEdicion(int fila) {
        modoEdicion = true;
        idMovimientoSeleccionado = (int) modeloTablaHistorial.getValueAt(fila, 0);
        String idAnimal = modeloTablaHistorial.getValueAt(fila, 1).toString();
        String idLotePosterior = modeloTablaHistorial.getValueAt(fila, 3).toString();
        java.sql.Date fecha = (java.sql.Date) modeloTablaHistorial.getValueAt(fila, 4);
        ((JTextField) cmbAnimalesId.getEditor().getEditorComponent()).setForeground(Color.BLACK);
        cmbAnimalesId.setSelectedItem(idAnimal);
        cmbLotesDestinoId.setSelectedItem(idLotePosterior);
        dateChooser.setDate(fecha);
        btnRegistrarMovimiento.setText("Guardar Cambios");
        btnLimpiar.setText("Cancelar Edición");
        btnModificarMovimiento.setEnabled(false);
        tablaHistorial.setEnabled(false);
    }

    private void salirModoEdicion() {
        modoEdicion = false;
        idMovimientoSeleccionado = -1;
        limpiarCampos();
        btnRegistrarMovimiento.setText("Registrar Movimiento");
        btnLimpiar.setText("Limpiar Campos");
        btnModificarMovimiento.setEnabled(true);
        tablaHistorial.setEnabled(true);
    }
}
