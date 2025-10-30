package vista.lotes;

import controlador.Controlador;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
// Se elimina import de Timestamp, ya no se usa
import java.util.Date;
import java.util.Vector;

public class panelHistorialLotes extends JPanel {
    // ... (las declaraciones de variables de clase son las mismas) ...
    private Controlador controlador;
    private JLabel lblTitulo, lblAnimalId, lblLoteDestinoId, lblFecha;
    private JComboBox<String> cmbAnimalesId;
    private JComboBox<String> cmbLotesDestinoId;
    private JDateChooser dateChooser;
    private JButton btnRegistrarMovimiento, btnModificarMovimiento, btnLimpiar;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTablaHistorial;
    private JScrollPane scrollPaneTabla;
    private boolean modoEdicion = false;
    private int idMovimientoSeleccionado = -1;

    public panelHistorialLotes(Controlador controlador) {
        this.controlador = controlador;
        initComponents();
        cargarDatosIniciales();
    }

    private void initComponents() {
        setLayout(null);
        setBackground(Color.WHITE);

        // ... (componentes hasta el ComboBox de animales) ...
        lblTitulo = new JLabel("Registro y Consulta de Movimientos de Animales");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setBounds(50, 20, 700, 30);
        add(lblTitulo);
        lblAnimalId = new JLabel("Buscar Animal (ID):"); // <-- CAMBIO: Texto más claro
        lblAnimalId.setFont(new Font("Arial", Font.PLAIN, 16));
        lblAnimalId.setBounds(50, 80, 150, 25);
        add(lblAnimalId);
        
        // --- IMPLEMENTACIÓN DE BÚSQUEDA DINÁMICA ---
        cmbAnimalesId = new JComboBox<>();
        cmbAnimalesId.setEditable(true); // Hacemos el ComboBox editable
        cmbAnimalesId.setBounds(220, 80, 250, 25);
        add(cmbAnimalesId);

        // Obtenemos el editor (que es un JTextField) para añadirle el listener
        JTextField editor = (JTextField) cmbAnimalesId.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Cada vez que el usuario suelta una tecla, filtramos
                actualizarComboAnimales(editor.getText());
            }
        });
        
        // ... (componentes hasta el JDateChooser) ...
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
        // <-- CAMBIO: Formato de fecha sin hora
        dateChooser.setDateFormatString("yyyy-MM-dd"); 
        dateChooser.setBounds(220, 160, 250, 25);
        add(dateChooser);
        
        // ... (el resto de initComponents es igual) ...
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
        btnLimpiar.setBounds(500, 160, 200, 30);
        add(btnLimpiar);
        String[] columnas = {"ID Movimiento", "ID Animal", "ID Lote Anterior", "ID Lote Posterior", "Fecha"};
        modeloTablaHistorial = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaHistorial = new JTable(modeloTablaHistorial);
        scrollPaneTabla = new JScrollPane(tablaHistorial);
        scrollPaneTabla.setBounds(50, 220, 966, 410);
        add(scrollPaneTabla);
        btnRegistrarMovimiento.addActionListener(e -> { if (modoEdicion) guardarCambiosMovimiento(); else registrarMovimiento(); });
        btnModificarMovimiento.addActionListener(e -> { int fila = tablaHistorial.getSelectedRow(); if (fila != -1) entrarModoEdicion(fila); else JOptionPane.showMessageDialog(this, "Seleccione un movimiento.", "Aviso", JOptionPane.WARNING_MESSAGE); });
        btnLimpiar.addActionListener(e -> { if (modoEdicion) salirModoEdicion(); else limpiarCampos(); });
    }

    private void cargarDatosIniciales() {
        actualizarComboAnimales(""); // Carga inicial con todos los animales
        cargarLotesEnCombo();
        cargarHistorialEnTabla();
    }

    private void registrarMovimiento() {
        // ... (validaciones iguales) ...
        String codigoAnimal = (String) cmbAnimalesId.getSelectedItem(); // <-- CAMBIO: Se obtiene el item seleccionado
        if (codigoAnimal == null || codigoAnimal.equals("Seleccione o busque un ID...")) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un ID de animal válido.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idLoteDestino = Integer.parseInt(cmbLotesDestinoId.getSelectedItem().toString());
        
        // <-- CAMBIO: Se convierte a java.sql.Date
        Date fechaUtil = dateChooser.getDate();
        java.sql.Date fechaSql = new java.sql.Date(fechaUtil.getTime());

        controlador.registrarMovimientoAnimal(codigoAnimal, idLoteDestino, fechaSql);

        cargarHistorialEnTabla();
        limpiarCampos();
    }
    
    private void guardarCambiosMovimiento(){
        String codigoAnimal = (String) cmbAnimalesId.getSelectedItem();
        int idLoteDestino = Integer.parseInt(cmbLotesDestinoId.getSelectedItem().toString());

        // <-- CAMBIO: Se convierte a java.sql.Date
        Date fechaUtil = dateChooser.getDate();
        java.sql.Date fechaSql = new java.sql.Date(fechaUtil.getTime());

        controlador.modificarRegistroHistorial(idMovimientoSeleccionado, codigoAnimal, idLoteDestino, fechaSql);

        cargarHistorialEnTabla();
        salirModoEdicion();
    }

    private void entrarModoEdicion(int fila) {
        modoEdicion = true;
        idMovimientoSeleccionado = (int) modeloTablaHistorial.getValueAt(fila, 0);

        String idAnimal = modeloTablaHistorial.getValueAt(fila, 1).toString();
        String idLotePosterior = modeloTablaHistorial.getValueAt(fila, 3).toString();
        // <-- CAMBIO: Se lee un java.sql.Date de la tabla
        java.sql.Date fecha = (java.sql.Date) modeloTablaHistorial.getValueAt(fila, 4);

        cmbAnimalesId.setSelectedItem(idAnimal);
        cmbLotesDestinoId.setSelectedItem(idLotePosterior);
        dateChooser.setDate(fecha); // No necesita conversión

        btnRegistrarMovimiento.setText("Guardar Cambios");
        btnLimpiar.setText("Cancelar Edición");
        btnModificarMovimiento.setEnabled(false);
        tablaHistorial.setEnabled(false);
    }
    
    // ... (salirModoEdicion y limpiarCampos se mantienen prácticamente igual) ...
    private void salirModoEdicion() {
        modoEdicion = false;
        idMovimientoSeleccionado = -1;
        limpiarCampos();
        btnRegistrarMovimiento.setText("Registrar Movimiento");
        btnLimpiar.setText("Limpiar Campos");
        btnModificarMovimiento.setEnabled(true);
        tablaHistorial.setEnabled(true);
    }
    private void limpiarCampos() {
        cmbAnimalesId.setSelectedIndex(0);
        cmbLotesDestinoId.setSelectedIndex(0);
        dateChooser.setDate(null);
        tablaHistorial.clearSelection();
    }


    // --- MÉTODO ACTUALIZADO PARA BÚSQUEDA DINÁMICA ---
    private void actualizarComboAnimales(String busqueda) {
        try {
            // Llama al nuevo método del controlador
            ResultSet rs = controlador.buscarCodigosAnimales(busqueda);
            Vector<String> model = new Vector<>();
            if (busqueda.isEmpty()) { // Si no hay búsqueda, mostramos una instrucción
                 model.addElement("Seleccione o busque un ID...");
            }
            while (rs.next()) {
                model.addElement(rs.getString("codigo"));
            }
            
            // Actualizamos el modelo del ComboBox
            cmbAnimalesId.setModel(new DefaultComboBoxModel<>(model));
            
            // Restauramos el texto del editor y mostramos el popup
            ((JTextField) cmbAnimalesId.getEditor().getEditorComponent()).setText(busqueda);
            if (model.size() > 1 || (model.size() == 1 && !model.get(0).equals("Seleccione o busque un ID..."))) {
                cmbAnimalesId.showPopup();
            } else {
                cmbAnimalesId.hidePopup();
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al filtrar animales: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // El método cargarAnimalesEnCombo() ya no es necesario, lo reemplaza actualizarComboAnimales
    
    public void cargarLotesEnCombo() {
        // ... (este método se mantiene igual) ...
    }

    public void cargarHistorialEnTabla() {
        modeloTablaHistorial.setRowCount(0);
        ResultSet rs = controlador.obtenerHistorialMovimientos();
        try {
            if (rs != null) {
                while (rs.next()) {
                    Object[] fila = {
                        rs.getInt("id"),
                        rs.getString("id_animal"),
                        rs.getObject("id_lote_anterior") == null ? "N/A" : rs.getInt("id_lote_anterior"),
                        rs.getInt("id_lote_posterior"),
                        rs.getDate("fecha") // <-- CAMBIO: Se lee como java.sql.Date
                    };
                    modeloTablaHistorial.addRow(fila);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el historial: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}