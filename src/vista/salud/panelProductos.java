package vista.salud;

import controlador.Controlador;
import controlador.FontLoader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List; // <-- Import necesario para el filtro
import javax.swing.*;
import javax.swing.border.EmptyBorder; // <-- Import necesario para el filtro
import javax.swing.table.DefaultTableModel; // <-- Import necesario para el filtro
import javax.swing.table.TableRowSorter;

public class panelProductos extends JPanel {
	private Controlador controlador;
	private JTextField txtProducto;
	private JComboBox<String> cbTipo;
	private JTable tablaProductos;
	private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter; // <-- Sorter para la tabla
	private JButton btnGuardar;
    // --- NUEVO: btnLimpiar ahora es una variable de clase ---
    private JButton btnLimpiar;
	private Integer editandoId = null;

	private final Font FONT_SUBTITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Bold.ttf", 24f);
	private final Font FONT_LABEL = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 16f);
	private final Font FONT_INPUT = FontLoader.loadFont("/resources/fonts/Montserrat-Light.ttf", 16f);
	private final Font FONT_BOTON = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 14f);

	public panelProductos(Controlador controlador) {
		this.controlador = controlador;
		setLayout(new BorderLayout());
        // El panel se crea y se añade en la clase que lo contiene (vistaSalud)
	}

	public JPanel createContentPanel() {
		JPanel content = new JPanel(new BorderLayout());
		content.setBackground(new Color(245, 246, 248));

		JLabel title = new JLabel("Gestión de Productos");
		title.setFont(FONT_SUBTITULO);
		title.setBorder(new EmptyBorder(20, 24, 8, 24));
		content.add(title, BorderLayout.NORTH);

		JPanel card = new JPanel(new BorderLayout());
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(220, 223, 230), 1),
				new EmptyBorder(16, 16, 16, 16)
		));

		JPanel formPanel = new JPanel(null);
		formPanel.setOpaque(false);
		formPanel.setPreferredSize(new Dimension(0, 120));

		JLabel lblProducto = new JLabel("Producto:");
		lblProducto.setFont(FONT_LABEL);
		lblProducto.setBounds(10, 10, 150, 30);
		formPanel.add(lblProducto);

		txtProducto = new JTextField();
		txtProducto.setFont(FONT_INPUT);
		txtProducto.setBounds(170, 10, 250, 30);
		formPanel.add(txtProducto);

		JLabel lblTipo = new JLabel("Tipo:");
		lblTipo.setFont(FONT_LABEL);
		lblTipo.setBounds(450, 10, 100, 30);
		formPanel.add(lblTipo);

		cbTipo = new JComboBox<>(new String[]{"Medicamento", "Desparasitante", "Vacuna", "Otro"});
		cbTipo.setFont(FONT_INPUT);
		cbTipo.setBounds(560, 10, 250, 30);
		formPanel.add(cbTipo);

		btnGuardar = new JButton("Guardar");
		btnGuardar.setFont(FONT_BOTON);
		btnGuardar.setBackground(controlador.estilos.COLOR_GUARDAR);
		btnGuardar.setForeground(Color.WHITE);
		btnGuardar.setIcon(new ImageIcon("src/resources/images/icon-guardar.png"));
        btnGuardar.setHorizontalTextPosition(SwingConstants.LEFT);
		btnGuardar.setBounds(560, 60, 250, 40);
		formPanel.add(btnGuardar);
        
        // --- NUEVO: Se añade el botón de limpiar al formulario ---
       // btnLimpiar = new JButton("Limpiar");
       // btnLimpiar.setFont(FONT_BOTON);
       // btnLimpiar.setBounds(300, 60, 250, 40);
       // formPanel.add(btnLimpiar);

		card.add(formPanel, BorderLayout.NORTH);
        
        // --- Panel para la tabla y la búsqueda ---
        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);
        tableContainer.setBorder(new EmptyBorder(10,0,0,0));

        // --- NUEVO: Barra de búsqueda ---
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchBarPanel.setOpaque(false);
        JLabel lblBuscar = new JLabel("Buscar en registros:");
        lblBuscar.setFont(FONT_LABEL.deriveFont(14f));
        JTextField txtBusqueda = new JTextField(25);
        txtBusqueda.setFont(FONT_INPUT.deriveFont(14f));
        searchBarPanel.add(lblBuscar);
        searchBarPanel.add(Box.createHorizontalStrut(10));
        searchBarPanel.add(txtBusqueda);
        tableContainer.add(searchBarPanel, BorderLayout.NORTH);

		String[] columnas = {"ID", "Producto", "Tipo"};
		modeloTabla = new DefaultTableModel(columnas, 0) {
			@Override
			public boolean isCellEditable(int row, int column) { return false; }
		};
		tablaProductos = new JTable(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tablaProductos.setRowSorter(sorter);
		tablaProductos.setRowHeight(28);
		tablaProductos.setFont(FONT_INPUT.deriveFont(14f));
		tablaProductos.getTableHeader().setFont(FONT_LABEL.deriveFont(14f));
		JScrollPane scrollPane = new JScrollPane(tablaProductos);
		scrollPane.setBorder(new EmptyBorder(8, 0, 0, 0));
		tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        card.add(tableContainer, BorderLayout.CENTER);
		
		JPanel tableButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		tableButtonsPanel.setOpaque(false);

		

		btnLimpiar = new JButton("Limpiar");
		btnLimpiar.setIcon(new ImageIcon("src/resources/images/icon-limpiar.png"));
        btnLimpiar.setHorizontalTextPosition(SwingConstants.LEFT);
		btnLimpiar.setFont(FONT_BOTON);
		btnLimpiar.setBackground(controlador.estilos.COLOR_LIMPIAR);
		btnLimpiar.setForeground(Color.WHITE);


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
		tableButtonsPanel.add(btnLimpiar);
		tableButtonsPanel.add(btnEditar);
		tableButtonsPanel.add(btnEliminar);
		card.add(tableButtonsPanel, BorderLayout.SOUTH);

		content.add(card, BorderLayout.CENTER);

		// --- LISTENERS CON LÓGICA MEJORADA ---
		btnGuardar.addActionListener(_ -> guardarOActualizar());
        btnLimpiar.addActionListener(_ -> limpiarFormulario());
		btnEditar.addActionListener(_ -> prepararEdicion());
		btnEliminar.addActionListener(_ -> eliminarProducto());
        
        txtBusqueda.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarTabla(txtBusqueda.getText());
            }
        });

		actualizarTabla();
		return content;
	}

    private void guardarOActualizar() {
        String producto = txtProducto.getText().trim();
        String tipo = (String) cbTipo.getSelectedItem();

        if (producto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del producto es requerido.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (editandoId == null) {
                controlador.guardarProducto(producto, tipo);
                JOptionPane.showMessageDialog(this, "Producto guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                controlador.editarProducto(editandoId, producto, tipo);
                JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            limpiarFormulario();
            actualizarTabla();
            
            // Forzar actualización de los otros paneles que usan productos
            controlador.animalesFrame.pSalud.actualizarPanelesInternos();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar el producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
	private void prepararEdicion() {
		int filaSeleccionada = tablaProductos.getSelectedRow();
		if (filaSeleccionada == -1) {
			JOptionPane.showMessageDialog(this, "Por favor, seleccione un producto para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
			return;
		}
        int filaModelo = tablaProductos.convertRowIndexToModel(filaSeleccionada);

		editandoId = (Integer) modeloTabla.getValueAt(filaModelo, 0);
		txtProducto.setText((String) modeloTabla.getValueAt(filaModelo, 1));
		cbTipo.setSelectedItem(modeloTabla.getValueAt(filaModelo, 2).toString());
		
        // --- LÓGICA DE MODO EDICIÓN ---
        btnGuardar.setText("Actualizar");
        btnLimpiar.setText("Cancelar Edición");
		txtProducto.requestFocus();
	}

	private void eliminarProducto() {
		int filaSeleccionada = tablaProductos.getSelectedRow();
		if (filaSeleccionada == -1) {
			JOptionPane.showMessageDialog(this, "Por favor, seleccione un producto para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int confirmacion = JOptionPane.showConfirmDialog(this,
				"¿Está seguro de que desea eliminar este producto?", "Confirmar", JOptionPane.YES_NO_OPTION);

		if (confirmacion == JOptionPane.YES_OPTION) {
			try {
                int filaModelo = tablaProductos.convertRowIndexToModel(filaSeleccionada);
				Integer idProducto = (Integer) modeloTabla.getValueAt(filaModelo, 0);
				controlador.eliminarProducto(idProducto);
				actualizarTabla();
				limpiarFormulario();
				JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Forzar actualización de los otros paneles que usan productos
				controlador.animalesFrame.pSalud.actualizarPanelesInternos();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error al eliminar el producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void limpiarFormulario() {
		txtProducto.setText("");
		cbTipo.setSelectedIndex(0);
		editandoId = null;
		tablaProductos.clearSelection();
        
        // --- LÓGICA DE MODO EDICIÓN ---
        btnGuardar.setText("Guardar");
        btnLimpiar.setText("Limpiar");
	}

	private void actualizarTabla() {
		modeloTabla.setRowCount(0);
		List<Object[]> productos = controlador.obtenerProductos();
		for (Object[] producto : productos) {
			modeloTabla.addRow(producto);
		}
	}
    
    private void filtrarTabla(String texto) {
        if (texto.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            // "(?i)" hace que la búsqueda no distinga mayúsculas de minúsculas
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }
}