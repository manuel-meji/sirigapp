package vista.lotes;

import controlador.Controlador;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class panelRegistroLotes extends JPanel {

    private Controlador controlador;

    // --- Componentes de la UI ---
    private JLabel lblTitulo, lblNombre, lblEtapa, lblDescripcion;
    private JTextField txtNombre;
    private JComboBox<String> cmbEtapa;
    private JTextArea areaDescripcion;
    private JButton btnRegistrar, btnModificar, btnLimpiar;
    private JTable tablaLotes;
    private DefaultTableModel modeloTablaLotes;
    private JScrollPane scrollPaneTabla, scrollPaneDescripcion;

    // --- Variables de estado para el modo de edición ---
    private boolean modoEdicion = false;
    private int idLoteSeleccionado = -1; // -1 indica que no hay ningún lote seleccionado para editar

    public panelRegistroLotes(Controlador controlador) {
        this.controlador = controlador;
        initComponents();
        cargarLotesEnTabla(); // Llamamos a cargar los datos al iniciar
    }

    private void initComponents() {
        // ... (Tu código de inicialización de componentes se mantiene igual hasta los botones)
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

            if (cmbEtapa.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una etapa para el lote.", "Error de validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre del lote no puede estar vacío.", "Error de validación", JOptionPane.ERROR_MESSAGE);
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

        // El botón 'Limpiar' también cancela el modo de edición
        btnLimpiar.addActionListener(e -> {
            if (modoEdicion) {
                salirModoEdicion(); // Si estamos editando, cancela la operación
            } else {
                limpiarCampos(); // Si no, solo limpia los campos
            }
        });
    }
    
    private void entrarModoEdicion(int fila) {
        modoEdicion = true;
        
        // Guardamos el ID del lote que se va a editar
        idLoteSeleccionado = (int) modeloTablaLotes.getValueAt(fila, 0);
        
        // Cargamos los datos de la tabla en los campos del formulario
        txtNombre.setText(modeloTablaLotes.getValueAt(fila, 1).toString());
        cmbEtapa.setSelectedItem(modeloTablaLotes.getValueAt(fila, 2).toString());
        areaDescripcion.setText(modeloTablaLotes.getValueAt(fila, 3).toString());
        
        // Actualizamos la UI para reflejar el modo de edición
        btnRegistrar.setText("Guardar Cambios");
        btnLimpiar.setText("Cancelar Edición");
        btnModificar.setEnabled(false); // Deshabilitamos el botón de modificar mientras editamos
        tablaLotes.setEnabled(false); // Opcional: Deshabilitar la tabla para evitar que seleccionen otro item
    }

    private void salirModoEdicion() {
        modoEdicion = false;
        idLoteSeleccionado = -1;
        
        limpiarCampos();
        
        // Restauramos la UI al estado normal
        btnRegistrar.setText("Registrar Lote");
        btnLimpiar.setText("Limpiar Campos");
        btnModificar.setEnabled(true);
        tablaLotes.setEnabled(true);
    }
    
    private void registrarLote() {
        // Obtenemos los datos del formulario
        String nombre = txtNombre.getText().trim();
        String etapa = cmbEtapa.getSelectedItem().toString();
        String descripcion = areaDescripcion.getText().trim();
        
        // Validaciones básicas
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del lote no puede estar vacío.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (cmbEtapa.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una etapa para el lote.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Llamar al controlador para que guarde los datos
        controlador.registrarNuevoLote(nombre, etapa, descripcion);
        
        cargarLotesEnTabla();
        limpiarCampos();
    }
    
    private void guardarCambiosLote() {
        // Obtenemos los datos actualizados del formulario
        String nombre = txtNombre.getText().trim();
        String etapa = cmbEtapa.getSelectedItem().toString();
        String descripcion = areaDescripcion.getText().trim();

        // Validaciones
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del lote no puede estar vacío.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Llamar al controlador para modificar el lote usando el ID guardado
        controlador.modificarLote(idLoteSeleccionado, nombre, etapa, descripcion);
        
        cargarLotesEnTabla();
        salirModoEdicion(); // Salimos del modo edición después de guardar
    }
    
    public void cargarLotesEnTabla() {
        modeloTablaLotes.setRowCount(0);
        ResultSet rs = controlador.obtenerTodosLosLotes();
        try {
            if (rs != null) {
                while (rs.next()) {
                    Object[] fila = {
                       rs.getInt("id"),
                       rs.getString("nombre"),
                       rs.getString("etapa"),
                       rs.getString("descripcion")
                    };
                    modeloTablaLotes.addRow(fila);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos de los lotes: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void limpiarCampos() {
        txtNombre.setText("");
        cmbEtapa.setSelectedIndex(0);
        areaDescripcion.setText("");
        tablaLotes.clearSelection();
    }
}