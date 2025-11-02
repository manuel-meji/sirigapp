package vista.salud;

import controlador.Controlador;
import controlador.FontLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class panelProductos extends JPanel {
	private Controlador controlador;
	private JTextField txtProducto;
	private JComboBox<String> cbTipo;
	private JTable tablaProductos;
	private DefaultTableModel modeloTabla;
	private JButton btnGuardar;
	private Integer editandoId = null;

	// --- Fuentes para una interfaz moderna y coherente ---
	private final Font FONT_SUBTITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Bold.ttf", 24f);
	private final Font FONT_LABEL = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 16f);
	private final Font FONT_INPUT = FontLoader.loadFont("/resources/fonts/Montserrat-Light.ttf", 16f);
	private final Font FONT_BOTON = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 14f);

	public panelProductos(Controlador controlador) {
		this.controlador = controlador;
		setLayout(new BorderLayout());
		// El createContentPanel se encargará de construir la UI
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

		// --- Panel de formulario con Layout Absoluto ---
		JPanel formPanel = new JPanel(null); // Layout absoluto
		formPanel.setOpaque(false);
		formPanel.setPreferredSize(new Dimension(0, 120)); // Altura para el formulario

		// --- Fila 1: Nombre del Producto y Tipo ---
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

		// --- Botón Guardar / Actualizar ---
		btnGuardar = new JButton("Guardar");
		btnGuardar.setFont(FONT_BOTON);
		btnGuardar.setBackground(controlador.estilos.COLOR_GUARDAR);
		btnGuardar.setForeground(Color.WHITE);
		btnGuardar.setBounds(560, 60, 250, 40);
		formPanel.add(btnGuardar);

		card.add(formPanel, BorderLayout.NORTH);

		// --- Tabla de productos ---
		String[] columnas = {"ID", "Producto", "Tipo"};
		modeloTabla = new DefaultTableModel(columnas, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tablaProductos = new JTable(modeloTabla);
		tablaProductos.setRowHeight(28);
		tablaProductos.setFont(FONT_INPUT.deriveFont(14f));
		tablaProductos.getTableHeader().setFont(FONT_LABEL.deriveFont(14f));
		JScrollPane scrollPane = new JScrollPane(tablaProductos);
		scrollPane.setBorder(new EmptyBorder(8, 0, 0, 0));
		card.add(scrollPane, BorderLayout.CENTER);
		
		// --- Panel de botones de la tabla (Editar/Eliminar) ---
		JPanel tableButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		tableButtonsPanel.setOpaque(false);
		JButton btnEditar = new JButton("Editar");
		JButton btnEliminar = new JButton("Eliminar");

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

		// --- Listeners (lógica sin cambios) ---
		btnGuardar.addActionListener(_ -> {
			try {
				String producto = txtProducto.getText().trim();
				String tipo = (String) cbTipo.getSelectedItem();

				if (producto.isEmpty()) {
					JOptionPane.showMessageDialog(this, "El nombre del producto es requerido.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
					return;
				}

				if (editandoId == null) {
					controlador.guardarProducto(producto, tipo);
				} else {
					controlador.editarProducto(editandoId, producto, tipo);
				}

				limpiarCampos();
				actualizarTabla();
				JOptionPane.showMessageDialog(this, "Producto guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
				controlador.animalesFrame.cambiarPanelContenido(controlador.animalesFrame.pSalud.createContentPanel());
				
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error al guardar el producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});

		btnEditar.addActionListener(_ -> {
			int filaSeleccionada = tablaProductos.getSelectedRow();
			if (filaSeleccionada == -1) {
				JOptionPane.showMessageDialog(this, "Por favor, seleccione un producto para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
				return;
			}

			editandoId = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
			txtProducto.setText((String) modeloTabla.getValueAt(filaSeleccionada, 1));
			cbTipo.setSelectedItem(modeloTabla.getValueAt(filaSeleccionada, 2).toString());
			btnGuardar.setText("Actualizar");
			txtProducto.requestFocus();
		});

		btnEliminar.addActionListener(_ -> {
			int filaSeleccionada = tablaProductos.getSelectedRow();
			if (filaSeleccionada == -1) {
				JOptionPane.showMessageDialog(this, "Por favor, seleccione un producto para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
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
					limpiarCampos(); // Limpiar por si estaba en modo edición
					JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, "Error al eliminar el producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// Cargar datos iniciales
		actualizarTabla();
		
		return content;
	}

	private void limpiarCampos() {
		txtProducto.setText("");
		cbTipo.setSelectedIndex(0);
		editandoId = null;
		btnGuardar.setText("Guardar");
		tablaProductos.clearSelection();
	}

	private void actualizarTabla() {
		modeloTabla.setRowCount(0);
		List<Object[]> productos = controlador.obtenerProductos();
		for (Object[] producto : productos) {
			modeloTabla.addRow(producto);
		}
	}
}