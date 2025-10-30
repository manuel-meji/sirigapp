package vista.lotes;

import controlador.Controlador;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class panelRegistroLotes extends JPanel {

    private Controlador controlador;
    private JLabel lblTitulo, lblNombre, lblEtapa, lblDescripcion;
    private JTextField txtNombre;
    private JComboBox<String> cmbEtapa;
    private JTextArea areaDescripcion;
    // Se añade btnEliminar a la declaración
    private JButton btnRegistrar, btnModificar, btnLimpiar, btnEliminar;
    private JTable tablaLotes;
    private DefaultTableModel modeloTablaLotes;
    private JScrollPane scrollPaneTabla, scrollPaneDescripcion;
    private boolean modoEdicion = false;
    private int idLoteSeleccionado = -1;

    public panelRegistroLotes(Controlador controlador) {
        this.controlador = controlador;
        initComponents();
        cargarLotesEnTabla();
    }

    private void initComponents() {
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitulo = new JLabel("Registro y Administración de Lotes de Ganado");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setBounds(50, 20, 600, 30);
        add(lblTitulo);

        lblNombre = new JLabel("Nombre del Lote:");
        lblNombre.setFont(new Font("Arial", Font.PLAIN, 16));
        lblNombre.setBounds(50, 80, 150, 25);
        add(lblNombre);
        txtNombre = new JTextField();
        txtNombre.setBounds(200, 80, 250, 25);
        add(txtNombre);

        lblEtapa = new JLabel("Etapa del Lote:");
        lblEtapa.setFont(new Font("Arial", Font.PLAIN, 16));
        lblEtapa.setBounds(50, 120, 150, 25);
        add(lblEtapa);
        String[] etapas = {"Seleccione una etapa", "Cría", "Levante", "Ceba", "Producción"};
        cmbEtapa = new JComboBox<>(etapas);
        cmbEtapa.setBounds(200, 120, 250, 25);
        add(cmbEtapa);

        lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Arial", Font.PLAIN, 16));
        lblDescripcion.setBounds(50, 160, 150, 25);
        add(lblDescripcion);
        areaDescripcion = new JTextArea();
        scrollPaneDescripcion = new JScrollPane(areaDescripcion);
        scrollPaneDescripcion.setBounds(200, 160, 250, 80);
        add(scrollPaneDescripcion);

        btnRegistrar = new JButton("Registrar Lote");
        btnRegistrar.setBounds(500, 80, 150, 30);
        btnRegistrar.setBackground(new Color(0x2BA76B));
        btnRegistrar.setForeground(Color.WHITE);
        add(btnRegistrar);

        btnModificar = new JButton("Modificar Lote");
        btnModificar.setBounds(500, 120, 150, 30);
        btnModificar.setBackground(new Color(0xFF054FBE));
        btnModificar.setForeground(Color.WHITE);
        add(btnModificar);

        btnLimpiar = new JButton("Limpiar Campos");
        btnLimpiar.setBounds(500, 160, 150, 30);
        add(btnLimpiar);

        // --- NUEVO BOTÓN ELIMINAR ---
        btnEliminar = new JButton("Eliminar Lote");
        btnEliminar.setBounds(500, 200, 150, 30); // Posicionado debajo de Limpiar
        btnEliminar.setBackground(new Color(220, 53, 69)); // Color rojo de peligro
        btnEliminar.setForeground(Color.WHITE);
        add(btnEliminar);

        String[] columnas = {"ID Lote", "Nombre", "Etapa", "Descripción"};
        modeloTablaLotes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaLotes = new JTable(modeloTablaLotes);
        scrollPaneTabla = new JScrollPane(tablaLotes);
        scrollPaneTabla.setBounds(50, 280, 966, 350);
        add(scrollPaneTabla);

        btnRegistrar.addActionListener(e -> {
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
            }else {
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

        // --- ACTION LISTENER PARA EL NUEVO BOTÓN ---
        btnEliminar.addActionListener(e -> {
            int filaSeleccionada = tablaLotes.getSelectedRow();
            if (filaSeleccionada != -1) {
                int idLote = (int) modeloTablaLotes.getValueAt(filaSeleccionada, 0);
                controlador.eliminarLote(idLote);
                cargarLotesEnTabla();
                VistaLotes.panelHistorial.cargarLotesEnCombo(); // Actualiza el otro panel
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un lote de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    public void cargarLotesEnTabla() {
        modeloTablaLotes.setRowCount(0);
        ResultSet rs = controlador.obtenerTodosLosLotes();
        try {
            if (rs != null) {
                while (rs.next()) {
                    Object[] fila = {rs.getInt("id"), rs.getString("nombre"), rs.getString("etapa"), rs.getString("descripcion")};
                    modeloTablaLotes.addRow(fila);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos de los lotes: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void entrarModoEdicion(int fila) {
        modoEdicion = true;
        idLoteSeleccionado = (int) modeloTablaLotes.getValueAt(fila, 0);
        txtNombre.setText(modeloTablaLotes.getValueAt(fila, 1).toString());
        cmbEtapa.setSelectedItem(modeloTablaLotes.getValueAt(fila, 2).toString());
        areaDescripcion.setText(modeloTablaLotes.getValueAt(fila, 3).toString());
        btnRegistrar.setText("Guardar Cambios");
        btnLimpiar.setText("Cancelar Edición");
        btnModificar.setEnabled(false);
        tablaLotes.setEnabled(false);
    }

    private void salirModoEdicion() {
        modoEdicion = false;
        idLoteSeleccionado = -1;
        limpiarCampos();
        btnRegistrar.setText("Registrar Lote");
        btnLimpiar.setText("Limpiar Campos");
        btnModificar.setEnabled(true);
        tablaLotes.setEnabled(true);
    }

    private void registrarLote() {
        String nombre = txtNombre.getText().trim();
        String etapa = cmbEtapa.getSelectedItem().toString();
        String descripcion = areaDescripcion.getText().trim();
        controlador.registrarNuevoLote(nombre, etapa, descripcion);
        cargarLotesEnTabla();
        VistaLotes.panelHistorial.cargarLotesEnCombo();
        limpiarCampos();
    }

    private void guardarCambiosLote() {
        String nombre = txtNombre.getText().trim();
        String etapa = cmbEtapa.getSelectedItem().toString();
        String descripcion = areaDescripcion.getText().trim();
        controlador.modificarLote(idLoteSeleccionado, nombre, etapa, descripcion);
        cargarLotesEnTabla();
        VistaLotes.panelHistorial.cargarLotesEnCombo(); // Actualiza el otro panel también al modificar
        salirModoEdicion();
    }

    public void limpiarCampos() {
        txtNombre.setText("");
        cmbEtapa.setSelectedIndex(0);
        areaDescripcion.setText("");
        tablaLotes.clearSelection();
    }
}
