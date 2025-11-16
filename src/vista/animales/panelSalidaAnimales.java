package vista.animales;

import controlador.Controlador;
import controlador.FontLoader;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.List;

public class panelSalidaAnimales extends JPanel {
    private Controlador controlador;
    private JComboBox<String> cbAnimal;
    private JComboBox<String> cbMotivo;
    private JDateChooser dcFecha;
    private JTable tablaSalidas;
    private DefaultTableModel modeloTabla;
    private JTextField txtBusquedaTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JButton btnGuardar; // Hecho miembro de la clase para cambiar su texto

    private Integer idSalidaEditando = null;

    // --- Fuentes estandarizadas ---
    private final Font FONT_SUBTITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Bold.ttf", 24f);
    private final Font FONT_LABEL = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 16f);
    private final Font FONT_INPUT = FontLoader.loadFont("/resources/fonts/Montserrat-Light.ttf", 16f);
    private final Font FONT_BOTON = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 14f);

    public panelSalidaAnimales(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
    }

    public JPanel createContentPanel() {
        // --- Estructura Principal ---
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(245, 246, 248));

        JLabel title = new JLabel("Registro de Salidas de Animales");
        title.setFont(controlador.estilos.FONT_TITULO_MENU.deriveFont(Font.BOLD, 26f));
        title.setBorder(new EmptyBorder(20, 24, 8, 24));
        content.add(title, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 223, 230), 1),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // --- Panel de formulario ---
        JPanel formPanel = new JPanel(null);
        formPanel.setOpaque(false);
        formPanel.setPreferredSize(new Dimension(0, 120));

        JLabel lblAnimal = new JLabel("Buscar Animal (ID):");
        lblAnimal.setFont(FONT_LABEL);
        lblAnimal.setBounds(10, 10, 160, 30);
        formPanel.add(lblAnimal);

        cbAnimal = new JComboBox<>();
        cbAnimal.setFont(FONT_INPUT);
        cbAnimal.setEditable(true);
        cbAnimal.setBounds(190, 10, 250, 30);
        formPanel.add(cbAnimal);

        JTextField editor = (JTextField) cbAnimal.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (cbAnimal.isEnabled()) {
                    String texto = editor.getText();
                    SwingUtilities.invokeLater(() -> actualizarBusquedaAnimal(texto));
                }
            }
        });

        JLabel lblMotivo = new JLabel("Motivo:");
        lblMotivo.setFont(FONT_LABEL);
        lblMotivo.setBounds(460, 10, 100, 30);
        formPanel.add(lblMotivo);

        cbMotivo = new JComboBox<>(new String[]{"VENTA", "MUERTE"});
        cbMotivo.setFont(FONT_INPUT);
        cbMotivo.setBounds(570, 10, 250, 30);
        formPanel.add(cbMotivo);

        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setFont(FONT_LABEL);
        lblFecha.setBounds(10, 60, 150, 30);
        formPanel.add(lblFecha);

        dcFecha = new JDateChooser();
        dcFecha.setDate(new Date());
        dcFecha.setDateFormatString("yyyy-MM-dd");
        dcFecha.setFont(FONT_INPUT);
        dcFecha.setBounds(190, 60, 250, 30);
        formPanel.add(dcFecha);

        btnGuardar = new JButton("Registrar Salida"); // Asignado a la variable de clase
        btnGuardar.setIcon(new ImageIcon("src/resources/images/icon-guardar.png"));
        btnGuardar.setHorizontalTextPosition(SwingConstants.LEFT);

        btnGuardar.setFont(FONT_BOTON);
        btnGuardar.setBackground(controlador.estilos.COLOR_GUARDAR);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBounds(570, 60, 250, 40);
        formPanel.add(btnGuardar);

        card.add(formPanel, BorderLayout.NORTH);

        // --- Panel de tabla ---
        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);

        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchBarPanel.setOpaque(false);
        JLabel lblBuscarTabla = new JLabel("Buscar en la tabla:");
        lblBuscarTabla.setFont(FONT_LABEL.deriveFont(14f));
        txtBusquedaTabla = new JTextField(25);
        txtBusquedaTabla.setFont(FONT_INPUT.deriveFont(14f));
        searchBarPanel.add(lblBuscarTabla);
        searchBarPanel.add(Box.createHorizontalStrut(10));
        searchBarPanel.add(txtBusquedaTabla);
        tableContainer.add(searchBarPanel, BorderLayout.NORTH);

        String[] columnas = {"ID", "Animal", "Motivo", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaSalidas = new JTable(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tablaSalidas.setRowSorter(sorter);

        tablaSalidas.setRowHeight(28);
        tablaSalidas.setFont(FONT_INPUT.deriveFont(14f));
        tablaSalidas.getTableHeader().setFont(FONT_LABEL.deriveFont(14f));
        JScrollPane scrollPane = new JScrollPane(tablaSalidas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        card.add(tableContainer, BorderLayout.CENTER);

        // --- Botones de acción de la tabla ---
        JPanel tableButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableButtonsPanel.setOpaque(false);
        JButton btnEditar = new JButton("Editar");
        btnEditar.setIcon(new ImageIcon("src/resources/images/icon-editar.png"));
        btnEditar.setHorizontalTextPosition(SwingConstants.LEFT);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setIcon(new ImageIcon("src/resources/images/icon-eliminar.png"));
        btnEliminar.setHorizontalTextPosition(SwingConstants.LEFT);

        btnEditar.setFont(FONT_BOTON);
        btnEditar.setBackground(controlador.estilos.COLOR_MODIFICAR);
        btnEditar.setForeground(Color.WHITE);

        btnEliminar.setFont(FONT_BOTON);
        btnEliminar.setBackground(controlador.estilos.COLOR_ELIMINAR);
        btnEliminar.setForeground(Color.WHITE);

        tableButtonsPanel.add(btnEditar);
        tableButtonsPanel.add(btnEliminar);
        card.add(tableButtonsPanel, BorderLayout.SOUTH);

        content.add(card, BorderLayout.CENTER);

        // --- Listeners ---
        btnGuardar.addActionListener(e -> guardarOActualizarSalida()); // El botón ahora tiene doble propósito
        btnEditar.addActionListener(e -> prepararEdicion()); // Nuevo método para la lógica de edición
        btnEliminar.addActionListener(e -> eliminarSalida());
        
        txtBusquedaTabla.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarTabla();
            }
        });

        // --- Carga Inicial ---
        cargarSalidas();
        cargarAnimalesIniciales();

        return content;
    }

    private void filtrarTabla() {
        String texto = txtBusquedaTabla.getText();
        if (texto.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }

    private void actualizarBusquedaAnimal(String filtro) {
        List<String> animales = controlador.buscarAnimales(filtro);
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cbAnimal.getModel();
        model.removeAllElements();
        animales.forEach(model::addElement);
        
        JTextField editor = (JTextField) cbAnimal.getEditor().getEditorComponent();
        editor.setText(filtro);
        
        if (!animales.isEmpty() && !filtro.isEmpty()) {
            cbAnimal.showPopup();
        } else {
            cbAnimal.hidePopup();
        }
    }


private void guardarOActualizarSalida() {
    if (idSalidaEditando == null) {
        // --- MODO REGISTRO ---
        try {
            // Obtenemos el texto directamente del editor, es la fuente más fiable.
            String animal = ((JTextField) cbAnimal.getEditor().getEditorComponent()).getText().trim();
            
            if (animal.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe introducir un ID de animal.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!controlador.animalExiste(animal)) {
                JOptionPane.showMessageDialog(this, "El animal con el ID '" + animal + "' no existe. Por favor, verifique el código.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return; // Detenemos la operación aquí
            }
        

            String motivo = cbMotivo.getSelectedItem().toString();
            java.sql.Date fecha = new java.sql.Date(dcFecha.getDate().getTime());

            controlador.guardarSalida(animal, motivo, fecha);
            cargarSalidas();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Salida registrada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar la salida: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        
        try {
            String motivo = cbMotivo.getSelectedItem().toString();
            java.sql.Date fecha = new java.sql.Date(dcFecha.getDate().getTime());

            controlador.actualizarSalida(idSalidaEditando, motivo, fecha);
            
            cargarSalidas();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Salida actualizada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar la salida: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
    private void prepararEdicion() {
        int filaSeleccionadaEnVista = tablaSalidas.getSelectedRow();
        if (filaSeleccionadaEnVista == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una salida de la tabla para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaEnModelo = tablaSalidas.convertRowIndexToModel(filaSeleccionadaEnVista);
        
        // Obtener datos de la fila
        Integer id = (Integer) modeloTabla.getValueAt(filaEnModelo, 0);
        String animal = (String) modeloTabla.getValueAt(filaEnModelo, 1);
        String motivo = (String) modeloTabla.getValueAt(filaEnModelo, 2);
        Date fecha = (Date) modeloTabla.getValueAt(filaEnModelo, 3);

        // Entrar en modo edición
        this.idSalidaEditando = id;

        // Cargar datos en el formulario
        ((JTextField) cbAnimal.getEditor().getEditorComponent()).setText(animal);
        cbMotivo.setSelectedItem(motivo);
        dcFecha.setDate(fecha);
        btnGuardar.setText("Actualizar Salida");
        cbAnimal.setEnabled(false); // No se puede cambiar el animal de un registro
    }

    private void eliminarSalida() {
        int filaSeleccionadaEnVista = tablaSalidas.getSelectedRow();
        if (filaSeleccionadaEnVista == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una salida para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar este registro?", "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmar == JOptionPane.YES_OPTION) {
            int filaEnModelo = tablaSalidas.convertRowIndexToModel(filaSeleccionadaEnVista);
            int id = (int) modeloTabla.getValueAt(filaEnModelo, 0);
            controlador.eliminarSalida(id);
            cargarSalidas();
            cargarAnimalesIniciales();
            limpiarFormulario(); // Limpiar por si se estaba editando el registro eliminado
        }
    }

    private void cargarSalidas() {
        modeloTabla.setRowCount(0);
        List<Object[]> salidas = controlador.obtenerSalidas();
        for (Object[] salida : salidas) {
            modeloTabla.addRow(salida);
        }
    }

    private void cargarAnimalesIniciales() {
        actualizarBusquedaAnimal("");
    }
    
   
    private void limpiarFormulario() {
        // Salir del modo edición
        idSalidaEditando = null;

        // Limpiar campos
        ((JTextField) cbAnimal.getEditor().getEditorComponent()).setText("");
        cbAnimal.setSelectedIndex(-1);
        cbMotivo.setSelectedIndex(0);
        dcFecha.setDate(new Date());
        txtBusquedaTabla.setText("");
        
        // Restaurar UI al modo registro
        btnGuardar.setText("Registrar Salida");
        cbAnimal.setEnabled(true);
        tablaSalidas.clearSelection();
    }
}