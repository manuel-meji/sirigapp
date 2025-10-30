package vista.produccion;

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
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class panelProduccionLeche extends JPanel {
    private Controlador controlador;
    private JTable tablaProduccion;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> cbAnimal;
    private JDateChooser dcFecha;
    private JTextField txtLitrosMatutinos;
    private JTextField txtLitrosVispertinos;
    private JTextField txtBusquedaTabla;
    private JButton btnGuardar;
    private Integer idProduccionEditando = null;

    // --- Fuentes estandarizadas ---
    private final Font FONT_SUBTITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Bold.ttf", 24f);
    private final Font FONT_LABEL = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 16f);
    private final Font FONT_INPUT = FontLoader.loadFont("/resources/fonts/Montserrat-Light.ttf", 16f);
    private final Font FONT_BOTON = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 14f);

    public panelProduccionLeche(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
    }

    public JPanel createContentPanel() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(245, 246, 248));

        JLabel title = new JLabel("Registro de Producción de Leche");
        title.setFont(FONT_SUBTITULO);
        title.setBorder(new EmptyBorder(20, 24, 8, 24));
        content.add(title, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 223, 230), 1),
                new EmptyBorder(16, 16, 16, 16)));

        // --- Panel de formulario con Layout Absoluto ---
        JPanel formPanel = new JPanel(null);
        formPanel.setOpaque(false);
        formPanel.setPreferredSize(new Dimension(0, 120));

        // --- Fila 1: Animal y Litros Mañana ---
        JLabel lblAnimal = new JLabel("Animal (ID):");
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
                    SwingUtilities.invokeLater(() -> actualizarBusquedaAnimal(editor.getText()));
                }
            }
        });

        JLabel lblMatutinos = new JLabel("Litros (Mañana):");
        lblMatutinos.setFont(FONT_LABEL);
        lblMatutinos.setBounds(460, 10, 150, 30);
        formPanel.add(lblMatutinos);

        txtLitrosMatutinos = new JTextField("0");
        txtLitrosMatutinos.setFont(FONT_INPUT);
        txtLitrosMatutinos.setBounds(620, 10, 200, 30);
        formPanel.add(txtLitrosMatutinos);

        // --- Fila 2: Fecha y Litros Tarde ---
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

        JLabel lblVispertinos = new JLabel("Litros (Tarde):");
        lblVispertinos.setFont(FONT_LABEL);
        lblVispertinos.setBounds(460, 60, 150, 30);
        formPanel.add(lblVispertinos);

        txtLitrosVispertinos = new JTextField("0");
        txtLitrosVispertinos.setFont(FONT_INPUT);
        txtLitrosVispertinos.setBounds(620, 60, 200, 30);
        formPanel.add(txtLitrosVispertinos);

        // --- Botón Guardar ---
        btnGuardar = new JButton("Registrar");
        btnGuardar.setFont(FONT_BOTON);
        btnGuardar.setBackground(new Color(67, 160, 71));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBounds(840, 60, 130, 40);
        formPanel.add(btnGuardar);

        card.add(formPanel, BorderLayout.NORTH);

        // --- Panel contenedor de la tabla y búsqueda ---
        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);

        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        searchBarPanel.setOpaque(false);
        JLabel lblBuscarTabla = new JLabel("Buscar en registros:");
        lblBuscarTabla.setFont(FONT_LABEL.deriveFont(14f));
        txtBusquedaTabla = new JTextField(25);
        txtBusquedaTabla.setFont(FONT_INPUT.deriveFont(14f));
        searchBarPanel.add(lblBuscarTabla);
        searchBarPanel.add(Box.createHorizontalStrut(10));
        searchBarPanel.add(txtBusquedaTabla);
        tableContainer.add(searchBarPanel, BorderLayout.NORTH);

        // --- Tabla ---
        String[] columnas = { "ID", "Fecha", "Animal", "L. Mañana", "L. Tarde", "Total Día" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProduccion = new JTable(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tablaProduccion.setRowSorter(sorter);

        tablaProduccion.setRowHeight(28);
        tablaProduccion.setFont(FONT_INPUT.deriveFont(14f));
        tablaProduccion.getTableHeader().setFont(FONT_LABEL.deriveFont(14f));
        JScrollPane scrollPane = new JScrollPane(tablaProduccion);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        card.add(tableContainer, BorderLayout.CENTER);

        // --- Botones de acción de la tabla ---
        JPanel tableButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableButtonsPanel.setOpaque(false);
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");

        btnEditar.setFont(FONT_BOTON);
        btnEditar.setBackground(new Color(100, 181, 246));
        btnEditar.setForeground(Color.WHITE);

        btnEliminar.setFont(FONT_BOTON);
        btnEliminar.setBackground(new Color(229, 57, 53));
        btnEliminar.setForeground(Color.WHITE);

        tableButtonsPanel.add(btnEditar);
        tableButtonsPanel.add(btnEliminar);
        card.add(tableButtonsPanel, BorderLayout.SOUTH);

        content.add(card, BorderLayout.CENTER);

        // --- Listeners ---
        btnGuardar.addActionListener(e -> guardarOActualizarProduccion());
        btnEditar.addActionListener(e -> prepararEdicion());
        btnEliminar.addActionListener(e -> eliminarProduccion());
        txtBusquedaTabla.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filtrarTabla();
            }
        });

        // --- Carga Inicial ---
        cargarProduccion();
        cargarAnimalesIniciales();

        return content;
    }

    private void guardarOActualizarProduccion() {
        try {
            String idAnimal = cbAnimal.getSelectedItem() != null ? cbAnimal.getSelectedItem().toString() : "";
            if (idAnimal.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un animal.", "Error de Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Timestamp fecha = new Timestamp(dcFecha.getDate().getTime());
            int litrosM = Integer.parseInt(txtLitrosMatutinos.getText());
            int litrosV = Integer.parseInt(txtLitrosVispertinos.getText());

            if (idProduccionEditando == null) {
                // MODO REGISTRO
                controlador.guardarProduccionLeche(fecha, litrosM, litrosV, idAnimal);
                JOptionPane.showMessageDialog(this, "Producción registrada exitosamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                // MODO ACTUALIZACIÓN
                controlador.actualizarProduccionLeche(idProduccionEditando, fecha, litrosM, litrosV, idAnimal);
                JOptionPane.showMessageDialog(this, "Producción actualizada exitosamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            cargarProduccion();
            limpiarFormulario();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Los litros deben ser números enteros válidos.", "Error de Formato",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar los datos: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prepararEdicion() {
        int filaVista = tablaProduccion.getSelectedRow();
        if (filaVista == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro para editar.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = tablaProduccion.convertRowIndexToModel(filaVista);
        idProduccionEditando = (Integer) modeloTabla.getValueAt(filaModelo, 0);
        Date fecha = (Date) modeloTabla.getValueAt(filaModelo, 1);
        String animal = (String) modeloTabla.getValueAt(filaModelo, 2);
        int litrosM = (int) modeloTabla.getValueAt(filaModelo, 3);
        int litrosV = (int) modeloTabla.getValueAt(filaModelo, 4);

        // Cargar datos en el formulario
        dcFecha.setDate(fecha);
        ((JTextField) cbAnimal.getEditor().getEditorComponent()).setText(animal);
        txtLitrosMatutinos.setText(String.valueOf(litrosM));
        txtLitrosVispertinos.setText(String.valueOf(litrosV));

        // Adaptar UI para edición
        btnGuardar.setText("Actualizar");
        cbAnimal.setEnabled(true); // Permitir cambiar el animal si fue un error de registro
    }

    private void eliminarProduccion() {
        int filaVista = tablaProduccion.getSelectedRow();
        if (filaVista == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro para eliminar.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este registro?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            int filaModelo = tablaProduccion.convertRowIndexToModel(filaVista);
            int id = (Integer) modeloTabla.getValueAt(filaModelo, 0);
            controlador.eliminarProduccionLeche(id);
            cargarProduccion();
            limpiarFormulario();
        }
    }

    private void cargarProduccion() {
        modeloTabla.setRowCount(0);
        List<Object[]> produccion = controlador.obtenerProduccionLeche();
        for (Object[] fila : produccion) {
            // fila[0]=id, fila[1]=fecha, fila[2]=id_animal, fila[3]=l_mat, fila[4]=l_vis
            int id = (int) fila[0];
            Date fecha = (Date) fila[1];
            String animal = (String) fila[2];
            int litrosM = (int) fila[3];
            int litrosV = (int) fila[4];
            int total = litrosM + litrosV;
            modeloTabla.addRow(new Object[] { id, fecha, animal, litrosM, litrosV, total });
        }
    }

    private void limpiarFormulario() {
        idProduccionEditando = null;
        ((JTextField) cbAnimal.getEditor().getEditorComponent()).setText("");
        cbAnimal.setSelectedIndex(-1);
        dcFecha.setDate(new Date());
        txtLitrosMatutinos.setText("0");
        txtLitrosVispertinos.setText("0");
        btnGuardar.setText("Registrar");
        cbAnimal.setEnabled(true);
        tablaProduccion.clearSelection();
    }

    private void filtrarTabla() {
        String texto = txtBusquedaTabla.getText();
        sorter.setRowFilter(texto.trim().isEmpty() ? null : RowFilter.regexFilter("(?i)" + texto));
    }

    private void cargarAnimalesIniciales() {
        actualizarBusquedaAnimal("");
    }

    private void actualizarBusquedaAnimal(String filtro) {
        // ANTES:
        // List<String> animales = controlador.buscarAnimales(filtro);

        // DESPUÉS (Llamada al nuevo método):
        List<String> animales = controlador.buscarAnimalesHembras(filtro);

        // El resto del método permanece exactamente igual...
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
}