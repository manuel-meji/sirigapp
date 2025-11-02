package vista.lotes;

import controlador.Controlador;
import controlador.FontLoader;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        JLabel title = new JLabel("Historial de Movimientos de Animales");
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
        btnGuardar.setFont(FONT_BOTON);
        btnGuardar.setBackground(controlador.estilos.COLOR_GUARDAR);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBounds(500, 10, 220, 45);
        formPanel.add(btnGuardar);
        btnLimpiar = new JButton("Limpiar Campos");
        btnLimpiar.setFont(FONT_BOTON);
        btnLimpiar.setBackground(controlador.estilos.COLOR_LIMPIAR);
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setBounds(500, 65, 220, 45);
        formPanel.add(btnLimpiar);
        card.add(formPanel, BorderLayout.NORTH);
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
        scrollPaneTabla.setBorder(new EmptyBorder(8, 0, 0, 0));
        card.add(scrollPaneTabla, BorderLayout.CENTER);
        JPanel tableButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableButtonsPanel.setOpaque(false);
        btnModificarMovimiento = new JButton("Modificar Movimiento");
        btnModificarMovimiento.setFont(FONT_BOTON);
        btnModificarMovimiento.setBackground(controlador.estilos.COLOR_MODIFICAR);
        btnModificarMovimiento.setForeground(Color.WHITE);
        btnModificarMovimiento.setPreferredSize(new Dimension(200, 40));
        btnEliminarMovimiento = new JButton("Eliminar Movimiento");
        btnEliminarMovimiento.setFont(FONT_BOTON);
        btnEliminarMovimiento.setBackground(controlador.estilos.COLOR_ELIMINAR);
        btnEliminarMovimiento.setForeground(Color.WHITE);
        btnEliminarMovimiento.setPreferredSize(new Dimension(200, 40));
        tableButtonsPanel.add(btnModificarMovimiento);
        tableButtonsPanel.add(btnEliminarMovimiento);
        card.add(tableButtonsPanel, BorderLayout.SOUTH);
        content.add(card, BorderLayout.CENTER);

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
        
            }});
        btnLimpiar.addActionListener(e -> {
            if (modoEdicion) {
                salirModoEdicion();
            } else {
                limpiarCampos();
        
            }});

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
            if (controlador.esUltimoMovimiento(idMovimiento, codigoAnimal)) {
                if (JOptionPane.showConfirmDialog(this, "¿Está seguro de revertir este movimiento?\nEl animal " + codigoAnimal + " volverá a su lote anterior.", "Confirmar Reversión", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    Object idLoteAnterior = modeloTablaHistorial.getValueAt(filaSeleccionada, 2);
                    controlador.revertirMovimientoHistorial(idMovimiento, codigoAnimal, idLoteAnterior);
                    cargarHistorialEnTabla();
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se puede revertir este movimiento. No es el más reciente para este animal.", "Acción no permitida", JOptionPane.ERROR_MESSAGE);
            }
        });

        return content;
    }

    private void entrarModoEdicion(int fila) {
        modoEdicion = true;
        idMovimientoSeleccionado = (int) modeloTablaHistorial.getValueAt(fila, 0);
        String idAnimal = modeloTablaHistorial.getValueAt(fila, 1).toString();
        this.codigoAnimalEditando = idAnimal;
        int idLotePosterior = (int) modeloTablaHistorial.getValueAt(fila, 3);
        Date fecha = (Date) modeloTablaHistorial.getValueAt(fila, 4);

        // --- CORRECCIÓN DEFINITIVA PARA CARGAR EL ANIMAL ---
        JTextField editorAnimal = (JTextField) cmbAnimalesId.getEditor().getEditorComponent();
        editorAnimal.setForeground(Color.BLACK);
        // Se establece el texto directamente en el editor, que es lo que el usuario ve.
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
            // No salimos del modo edición si hay un error de verificación, para que el usuario pueda corregir.
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            salirModoEdicion();
        }
    }

    // --- El resto de los métodos se mantienen igual ---
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
        modeloTablaHistorial.setRowCount(0);
        try (ResultSet rs = controlador.obtenerHistorialMovimientos()) {
            if (rs != null) {
                while (rs.next()) {
                    Object[] fila = {rs.getInt("id"), rs.getString("id_animal"), rs.getObject("id_lote_anterior") == null ? "N/A" : rs.getInt("id_lote_anterior"), rs.getInt("id_lote_posterior"), rs.getDate("fecha")};
                    modeloTablaHistorial.addRow(fila);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar el historial: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrarComboAnimales(String busqueda) {
        if (busqueda.equals(PLACEHOLDER_ANIMAL)) {
            return;
        
        }try {
            ResultSet rs = controlador.buscarCodigosAnimales(busqueda);
            String textoActual = ((JTextField) cmbAnimalesId.getEditor().getEditorComponent()).getText();
            comboModelAnimales.removeAllElements();
            if (rs != null) {
                while (rs.next()) {
                    comboModelAnimales.addElement(rs.getString("codigo"));
            
                }
            }((JTextField) cmbAnimalesId.getEditor().getEditorComponent()).setText(textoActual);
            if (comboModelAnimales.getSize() > 0 && !textoActual.isEmpty()) {
                if (cmbAnimalesId.isShowing()) {
                    cmbAnimalesId.showPopup();
                }
            } else {
                cmbAnimalesId.hidePopup();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al filtrar animales: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
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
