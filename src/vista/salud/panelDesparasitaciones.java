package vista.salud;

import controlador.Controlador;
import controlador.FontLoader;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class panelDesparasitaciones extends JPanel {

    private Controlador controlador;
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JDateChooser dcFecha;
    private JComboBox<String> cbAnimal;
    private JComboBox<ProductoItem> cbProducto;
    private JTextField txtDosis;
    private JTextField txtBusqueda;
    private JButton btnGuardar;
    private JButton btnLimpiar; // Se hace variable de clase para el modo edición
    private Integer idEventoEditando = null;

    private final Font FONT_SUBTITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Bold.ttf", 24f);
    private final Font FONT_LABEL = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 16f);
    private final Font FONT_INPUT = FontLoader.loadFont("/resources/fonts/Montserrat-Light.ttf", 16f);
    private final Font FONT_BOTON = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 14f);
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private final String PLACEHOLDER_ANIMAL = "Escriba para buscar un animal";
    private final String PLACEHOLDER_PRODUCTO = "Escriba para buscar un producto";

    private static class ProductoItem {

        private final int id;
        private final String nombre;

        public ProductoItem(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    public panelDesparasitaciones(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
        add(createContentPanel(), BorderLayout.CENTER);
    }

    public JPanel createContentPanel() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(245, 246, 248));
        JLabel title = new JLabel("Desparasitaciones");
        title.setFont(FONT_SUBTITULO);
        title.setBorder(new EmptyBorder(20, 24, 8, 24));
        content.add(title, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 223, 230), 1),
                new EmptyBorder(16, 16, 16, 16)));

        JPanel form = new JPanel(null);
        form.setOpaque(false);
        form.setPreferredSize(new Dimension(0, 180));

        // --- ID Animal ---
        JLabel lblAnimal = new JLabel("ID Animal:");
        lblAnimal.setFont(FONT_LABEL);
        lblAnimal.setBounds(10, 10, 150, 30);
        form.add(lblAnimal);
        cbAnimal = new JComboBox<>();
        cbAnimal.setEditable(true);
        cbAnimal.setFont(FONT_INPUT);
        cbAnimal.setBounds(170, 10, 250, 30);
        form.add(cbAnimal);

        // --- Producto ---
        JLabel lblProducto = new JLabel("Producto:");
        lblProducto.setFont(FONT_LABEL);
        lblProducto.setBounds(10, 50, 150, 30);
        form.add(lblProducto);
        cbProducto = new JComboBox<>();
        cbProducto.setEditable(true);
        cbProducto.setFont(FONT_INPUT);
        cbProducto.setBounds(170, 50, 250, 30);
        form.add(cbProducto);

        // --- Dosis y Fecha ---
        JLabel lblDosis = new JLabel("Dosis:");
        lblDosis.setFont(FONT_LABEL);
        lblDosis.setBounds(450, 10, 100, 30);
        form.add(lblDosis);
        txtDosis = new JTextField();
        txtDosis.setFont(FONT_INPUT);
        txtDosis.setBounds(560, 10, 250, 30);
        form.add(txtDosis);
        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setFont(FONT_LABEL);
        lblFecha.setBounds(450, 50, 100, 30);
        form.add(lblFecha);
        dcFecha = new JDateChooser();
        dcFecha.setDate(new Date());
        dcFecha.setDateFormatString("yyyy-MM-dd");
        dcFecha.setFont(FONT_INPUT);
        dcFecha.setBounds(560, 50, 250, 30);
        form.add(dcFecha);

        // --- CORRECCIÓN DE LAYOUT ---
        JLabel lblBusqueda = new JLabel("Buscar:");
        lblBusqueda.setFont(FONT_LABEL);
        lblBusqueda.setBounds(10, 110, 80, 30);
        form.add(lblBusqueda);

        txtBusqueda = new JTextField();
        txtBusqueda.setFont(FONT_INPUT);
        txtBusqueda.setBounds(100, 110, 450, 30); // Posición y ancho ajustados
        form.add(txtBusqueda);

        btnGuardar = new JButton("Registrar");
        btnGuardar.setBackground(controlador.estilos.COLOR_GUARDAR);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(FONT_BOTON);
        btnGuardar.setBounds(560, 110, 250, 40);
        form.add(btnGuardar);

        card.add(form, BorderLayout.NORTH);

        String[] cols = {"ID", "Fecha", "Animal", "Producto", "Dosis"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        table.setRowHeight(28);
        table.setFont(FONT_INPUT.deriveFont(14f));
        table.getTableHeader().setFont(FONT_LABEL.deriveFont(14f));
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setOpaque(false);
        // --- SE AÑADE EL BOTÓN LIMPIAR ---
        btnLimpiar = new JButton("Limpiar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");

        btnLimpiar.setFont(FONT_BOTON);
        btnEditar.setBackground(controlador.estilos.COLOR_MODIFICAR);
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setFont(FONT_BOTON);
        btnEliminar.setBackground(controlador.estilos.COLOR_ELIMINAR);
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFont(FONT_BOTON);

        btns.add(btnLimpiar);
        btns.add(btnEditar);
        btns.add(btnEliminar);
        card.add(btns, BorderLayout.SOUTH);
        content.add(card, BorderLayout.CENTER);

        // --- LISTENERS ---
        setupComboBoxListeners();
        btnGuardar.addActionListener(_ -> guardarOActualizarEvento());
        btnEliminar.addActionListener(_ -> eliminarEvento());
        btnEditar.addActionListener(_ -> prepararEdicion());
        btnLimpiar.addActionListener(_ -> limpiarFormulario());
        txtBusqueda.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filtrarTabla();
            }
        });

        cargarEventos();

        return content;
    }

    private void setupComboBoxListeners() {
        // --- Lógica explícita para Animales ---
        JTextField editorAnimal = (JTextField) cbAnimal.getEditor().getEditorComponent();
        editorAnimal.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN) {
                    filtrarAnimales(editorAnimal.getText());
                }
            }
        });
        editorAnimal.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (editorAnimal.getText().equals(PLACEHOLDER_ANIMAL)) {
                    editorAnimal.setText("");
                    editorAnimal.setForeground(Color.BLACK);
                    if (cbAnimal.getItemCount() == 0) {
                        filtrarAnimales("");
                    }
                }
            }

            public void focusLost(FocusEvent e) {
                if (editorAnimal.getText().trim().isEmpty()) {
                    setPlaceholder(cbAnimal, PLACEHOLDER_ANIMAL);
                }
            }
        });

        // --- Lógica explícita para Productos ---
        JTextField editorProducto = (JTextField) cbProducto.getEditor().getEditorComponent();
        editorProducto.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN) {
                    filtrarProductos(editorProducto.getText());
                }
            }
        });
        editorProducto.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (editorProducto.getText().equals(PLACEHOLDER_PRODUCTO)) {
                    editorProducto.setText("");
                    editorProducto.setForeground(Color.BLACK);
                    if (cbProducto.getItemCount() == 0) {
                        filtrarProductos("");
                    }
                }
            }

            public void focusLost(FocusEvent e) {
                if (editorProducto.getText().trim().isEmpty()) {
                    setPlaceholder(cbProducto, PLACEHOLDER_PRODUCTO);
                }
            }
        });

        setPlaceholder(cbAnimal, PLACEHOLDER_ANIMAL);
        setPlaceholder(cbProducto, PLACEHOLDER_PRODUCTO);
    }

    private void guardarOActualizarEvento() {
        try {
            String animal = ((JTextField) cbAnimal.getEditor().getEditorComponent()).getText();
            if (animal.trim().isEmpty() || animal.equals(PLACEHOLDER_ANIMAL)) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un animal válido.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Object productoItem = cbProducto.getSelectedItem();
            if (productoItem == null || !(productoItem instanceof ProductoItem)) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un producto válido.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idProducto = ((ProductoItem) productoItem).getId();

            String dosisStr = txtDosis.getText().trim();
            if (dosisStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo Dosis no puede estar vacío.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Float dosis = Float.parseFloat(dosisStr);
            if (dosis <= 0) {
                JOptionPane.showMessageDialog(this, "La dosis debe ser un número mayor que cero.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            java.sql.Timestamp fecha = new java.sql.Timestamp(dcFecha.getDate().getTime());

            if (idEventoEditando == null) {
                controlador.guardarEventoSanitario(fecha, idProducto, dosis, "DESPARASITANTE", "", animal, "DESPARASITACION");
                JOptionPane.showMessageDialog(this, "Desparasitación registrada exitosamente.");
            } else {
                controlador.actualizarEventoSanitario(idEventoEditando, fecha, idProducto, dosis, animal);
                JOptionPane.showMessageDialog(this, "Desparasitación actualizada exitosamente.");
            }
            cargarEventos();
            limpiarFormulario();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error en el formato del número de Dosis.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar el registro: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prepararEdicion() {
        int filaVista = table.getSelectedRow();
        if (filaVista == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = table.convertRowIndexToModel(filaVista);
        int id = (int) model.getValueAt(filaModelo, 0);

        Object[] datosEvento = controlador.obtenerEventoSanitarioPorId(id);
        if (datosEvento == null) {
            JOptionPane.showMessageDialog(this, "No se encontraron los datos del evento.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        idEventoEditando = id;
        Date fecha = (Date) datosEvento[1];
        String animal = (String) datosEvento[3];
        Integer idProducto = (Integer) datosEvento[4];
        Float dosis = (datosEvento[5] != null) ? ((Number) datosEvento[5]).floatValue() : null;

        dcFecha.setDate(fecha);
        txtDosis.setText((dosis != null) ? String.valueOf(dosis) : "");

        ((JTextField) cbAnimal.getEditor().getEditorComponent()).setForeground(Color.BLACK);
        ((JTextField) cbAnimal.getEditor().getEditorComponent()).setText(animal);

        filtrarProductos("");
        if (idProducto != null) {
            for (int i = 0; i < cbProducto.getItemCount(); i++) {
                ProductoItem item = cbProducto.getItemAt(i);
                if (item.getId() == idProducto) {
                    cbProducto.setSelectedIndex(i);
                    break;
                }
            }
        }

        btnGuardar.setText("Actualizar");
        btnLimpiar.setText("Cancelar");
        cbAnimal.setEnabled(false); // No se puede cambiar el animal de un registro existente
    }

    private void eliminarEvento() {
        int filaVista = table.getSelectedRow();
        if (filaVista == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este evento?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            int filaModelo = table.convertRowIndexToModel(filaVista);
            int id = (int) model.getValueAt(filaModelo, 0);
            controlador.eliminarEvento(id);
            cargarEventos();
        }
    }

    private void limpiarFormulario() {
        idEventoEditando = null;
        setPlaceholder(cbAnimal, PLACEHOLDER_ANIMAL);
        setPlaceholder(cbProducto, PLACEHOLDER_PRODUCTO);
        txtDosis.setText("");
        dcFecha.setDate(new Date());
        btnGuardar.setText("Registrar");
        btnLimpiar.setText("Limpiar");
        cbAnimal.setEnabled(true);
        table.clearSelection();
    }

    private void filtrarTabla() {
        String texto = txtBusqueda.getText();
        sorter.setRowFilter(texto.trim().isEmpty() ? null : RowFilter.regexFilter("(?i)"
                + texto));
    }

    public void cargarEventos() {
        model.setRowCount(0);
        List<Object[]> rows = controlador.obtenerEventosSanitariosPorTipo("DESPARASITACION");
        for (Object[] r : rows) {
            Date fecha = (Date) r[1];
            String fechaFormateada = (fecha != null) ? sdf.format(fecha) : "";

            String nombreProducto = obtenerNombreProductoPorId((Integer) r[3]);

            model.addRow(new Object[]{r[0], fechaFormateada, r[2], nombreProducto, r[4]});
        }
    }

    private void filtrarAnimales(String filtro) {
        List<String> animales = controlador.buscarAnimales(filtro);
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cbAnimal.getModel();
        model.removeAllElements();
        animales.forEach(model::addElement);
        ((JTextField) cbAnimal.getEditor().getEditorComponent()).setText(filtro);
        if (!animales.isEmpty() && !filtro.isEmpty()) {
            cbAnimal.showPopup();
        } else {
            cbAnimal.hidePopup();
        }
    }

    private void filtrarProductos(String filtro) {
        List<Object[]> productos = controlador.buscarProductosDesparasitantes(filtro);
        DefaultComboBoxModel<ProductoItem> model = (DefaultComboBoxModel<ProductoItem>) cbProducto.getModel();
        model.removeAllElements();
        for (Object[] p : productos) {
            model.addElement(new ProductoItem((int) p[0], (String) p[1]));
        }
        ((JTextField) cbProducto.getEditor().getEditorComponent()).setText(filtro);
        if (!productos.isEmpty() && !filtro.isEmpty()) {
            cbProducto.showPopup();
        } else {
            cbProducto.hidePopup();
        }
    }

    public void cargarProductos() {
        filtrarProductos("");
    }

    private <T> void setPlaceholder(JComboBox<T> comboBox, String text) {
        JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();
        comboBox.removeAllItems();
        editor.setForeground(Color.GRAY);
        editor.setText(text);
    }

    private String obtenerNombreProductoPorId(Integer id) {
        if (id == null) {
            return "N/A";
        }
        List<Object[]> productos = controlador.obtenerProductos();
        for (Object[] p : productos) {
            if (id.equals(p[0])) {
                return (String) p[1];
            }
        }
        return "Desconocido";
    }
}
