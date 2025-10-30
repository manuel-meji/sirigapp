package vista.salud;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import controlador.Controlador;
public class panelProductos extends JPanel {
    private static final Font DEFAULT_FONT = new Font("Montserrat", Font.PLAIN, 12);
    private static final Font BOLD_FONT = new Font("Montserrat", Font.BOLD, 12);
    private Controlador controlador;
    private JTextField txtProducto;
    private JComboBox<String> cbTipo;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JButton btnGuardar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private Integer editandoId = null;

    public panelProductos(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Nombre del Producto
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblProducto = new JLabel("Producto:");
        lblProducto.setFont(DEFAULT_FONT);
        formPanel.add(lblProducto, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        txtProducto = new JTextField(20);
        txtProducto.setFont(DEFAULT_FONT);
        formPanel.add(txtProducto, gbc);

        // Tipo de Producto
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblTipo = new JLabel("Tipo:");
        lblTipo.setFont(DEFAULT_FONT);
        formPanel.add(lblTipo, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        cbTipo = new JComboBox<>(new String[]{"Medicamento", "Suplemento", "Vacuna"});
        cbTipo.setFont(DEFAULT_FONT);
        formPanel.add(cbTipo, gbc);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnGuardar = new JButton("Guardar");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");

        btnGuardar.setFont(DEFAULT_FONT);
        btnEditar.setFont(DEFAULT_FONT);
        btnEliminar.setFont(DEFAULT_FONT);

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Tabla de productos
        String[] columnas = {"ID", "Producto", "Tipo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setFont(DEFAULT_FONT);
        tablaProductos.getTableHeader().setFont(BOLD_FONT);
        JScrollPane scrollPane = new JScrollPane(tablaProductos);

        // Agregar componentes al panel principal
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Configurar listeners
        btnGuardar.addActionListener(_ -> {
            try {
                String producto = txtProducto.getText().trim();
                String tipo = (String) cbTipo.getSelectedItem();

                if (producto.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre del producto es requerido", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (editandoId == null) {
                    controlador.guardarProducto(producto, tipo);
                } else {
                    controlador.editarProducto(editandoId, producto, tipo);
                    editandoId = null;
                    btnGuardar.setText("Guardar");
                }

                limpiarCampos();
                actualizarTabla();
                JOptionPane.showMessageDialog(this, "Producto guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEditar.addActionListener(_ -> {
            int filaSeleccionada = tablaProductos.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un producto para editar", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            editandoId = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
            txtProducto.setText((String) modeloTabla.getValueAt(filaSeleccionada, 1));
            cbTipo.setSelectedItem((String) modeloTabla.getValueAt(filaSeleccionada, 2));
            btnGuardar.setText("Actualizar");
        });

        btnEliminar.addActionListener(_ -> {
            int filaSeleccionada = tablaProductos.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un producto para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirmacion = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de que desea eliminar este producto?", 
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    Integer idProducto = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
                    controlador.eliminarProducto(idProducto);
                    actualizarTabla();
                    JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Cargar datos iniciales
        actualizarTabla();
    }

    private void limpiarCampos() {
        txtProducto.setText("");
        cbTipo.setSelectedIndex(0);
        editandoId = null;
        btnGuardar.setText("Guardar");
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        java.util.List<Object[]> productos = controlador.obtenerProductos();
        for (Object[] producto : productos) {
            modeloTabla.addRow(producto);
        }
    }

    public JPanel createContentPanel() {
        return this;
    }
}
