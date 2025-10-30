package vista.animales;

import controlador.Controlador;
import controlador.FontLoader;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.Vector;

public class panelSalidaAnimales extends javax.swing.JPanel {
    private Controlador controlador;
    private JComboBox<String> cbAnimal;
    private JTextField txtBusquedaAnimal;
    private JComboBox<String> cbMotivo;
    private JDateChooser dcFecha;
    private JTable tablaSalidas;
    private DefaultTableModel modeloTabla;
    
    // --- Fuentes ---
    private final Font FONT_TITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Black.ttf", 48f);
    private final Font FONT_NORMAL = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 18f);

    public panelSalidaAnimales(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
    }

    public JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(new Color(245, 246, 248));

        // Título
        JLabel title = new JLabel("Registro de Salidas");
        title.setFont(FONT_TITULO.deriveFont(Font.BOLD, 26f));
        title.setBorder(new EmptyBorder(24, 32, 8, 32));
        title.setHorizontalAlignment(SwingConstants.LEFT);
        contentPanel.add(title, BorderLayout.NORTH);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(16, 32, 32, 32));

        // Panel del formulario
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.NORTH);

        // Panel de la tabla
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        contentPanel.add(mainPanel, BorderLayout.CENTER);
        return contentPanel;
    }

    private JPanel createFormPanel() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 223, 230), 1),
            new EmptyBorder(20, 24, 24, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID Animal con búsqueda
        JLabel lblAnimal = new JLabel("ID Animal:");
        lblAnimal.setFont(FONT_NORMAL.deriveFont(Font.PLAIN, 16f));
        gbc.gridx = 0; gbc.gridy = 0;
        card.add(lblAnimal, gbc);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setOpaque(false);
        
        txtBusquedaAnimal = new JTextField();
        txtBusquedaAnimal.setFont(FONT_NORMAL.deriveFont(Font.PLAIN, 14f));
        cbAnimal = new JComboBox<>();
        cbAnimal.setFont(FONT_NORMAL.deriveFont(Font.PLAIN, 14f));
        
        searchPanel.add(txtBusquedaAnimal, BorderLayout.CENTER);
        searchPanel.add(cbAnimal, BorderLayout.EAST);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        card.add(searchPanel, gbc);

        // Configurar búsqueda dinámica
        txtBusquedaAnimal.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { actualizarBusqueda(); }
            public void removeUpdate(DocumentEvent e) { actualizarBusqueda(); }
            public void changedUpdate(DocumentEvent e) { actualizarBusqueda(); }
            
            private void actualizarBusqueda() {
                String texto = txtBusquedaAnimal.getText();
                actualizarComboAnimales(texto);
            }
        });

        // Motivo
        JLabel lblMotivo = new JLabel("Motivo:");
        lblMotivo.setFont(FONT_NORMAL.deriveFont(Font.PLAIN, 16f));
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.0;
        card.add(lblMotivo, gbc);

        cbMotivo = new JComboBox<>(new String[]{"VENTA", "MUERTE"});
        cbMotivo.setFont(FONT_NORMAL.deriveFont(Font.PLAIN, 14f));
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0;
        card.add(cbMotivo, gbc);

        // Fecha
        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setFont(FONT_NORMAL.deriveFont(Font.PLAIN, 16f));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        card.add(lblFecha, gbc);

        dcFecha = new JDateChooser();
        dcFecha.setDate(new Date());
        dcFecha.setDateFormatString("yyyy-MM-dd");
        dcFecha.setFont(FONT_NORMAL.deriveFont(Font.PLAIN, 14f));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        card.add(dcFecha, gbc);

        // Botón Guardar
        JButton btnGuardar = new JButton("Registrar Salida");
        btnGuardar.setFont(FONT_NORMAL.deriveFont(Font.PLAIN, 14f));
        btnGuardar.setBackground(new Color(67, 160, 71));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        gbc.gridx = 3; gbc.gridy = 1; 
        card.add(btnGuardar, gbc);

        btnGuardar.addActionListener(e -> guardarSalida());

        return card;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Crear modelo de tabla
        String[] columnas = {"ID", "Animal", "Motivo", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaSalidas = new JTable(modeloTabla);
        tablaSalidas.setFont(FONT_NORMAL.deriveFont(Font.PLAIN, 14f));
        tablaSalidas.setRowHeight(30);
        tablaSalidas.getTableHeader().setFont(FONT_NORMAL.deriveFont(Font.BOLD, 14f));
        tablaSalidas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tablaSalidas);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setOpaque(false);

        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");

        btnEditar.setFont(FONT_NORMAL.deriveFont(Font.PLAIN, 14f));
        btnEliminar.setFont(FONT_NORMAL.deriveFont(Font.PLAIN, 14f));

        btnEditar.setBackground(new Color(100, 181, 246));
        btnEliminar.setBackground(new Color(229, 57, 53));
        btnEditar.setForeground(Color.WHITE);
        btnEliminar.setForeground(Color.WHITE);

        btnEditar.addActionListener(e -> editarSalida());
        btnEliminar.addActionListener(e -> eliminarSalida());

        buttonsPanel.add(btnEditar);
        buttonsPanel.add(btnEliminar);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        // Cargar datos iniciales
        cargarSalidas();

        return panel;
    }

    private void actualizarComboAnimales(String filtro) {
        cbAnimal.removeAllItems();
        // Obtener los animales que coincidan con el filtro
        java.util.List<String> animales = controlador.buscarAnimales(filtro);
        for (String animal : animales) {
            cbAnimal.addItem(animal);
        }
        cbAnimal.setPopupVisible(!filtro.isEmpty());
    }

    private void guardarSalida() {
        try {
            String animal = cbAnimal.getSelectedItem().toString();
            String motivo = cbMotivo.getSelectedItem().toString();
            java.sql.Date fecha = new java.sql.Date(dcFecha.getDate().getTime());
            
            controlador.guardarSalida(animal, motivo, fecha);
            cargarSalidas();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Salida registrada correctamente");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }

    private void editarSalida() {
        int fila = tablaSalidas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una salida para editar");
            return;
        }
        
        int id = (int) tablaSalidas.getValueAt(fila, 0);
        controlador.editarSalida(id);
        cargarSalidas();
    }

    private void eliminarSalida() {
        int fila = tablaSalidas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una salida para eliminar");
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar esta salida?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirmar == JOptionPane.YES_OPTION) {
            int id = (int) tablaSalidas.getValueAt(fila, 0);
            controlador.eliminarSalida(id);
            cargarSalidas();
        }
    }

    private void cargarSalidas() {
        modeloTabla.setRowCount(0);
        java.util.List<Object[]> salidas = controlador.obtenerSalidas();
        for (Object[] salida : salidas) {
            modeloTabla.addRow(salida);
        }
    }

    private void limpiarFormulario() {
        txtBusquedaAnimal.setText("");
        cbAnimal.setSelectedIndex(-1);
        cbMotivo.setSelectedIndex(0);
        dcFecha.setDate(new Date());
    }
}
