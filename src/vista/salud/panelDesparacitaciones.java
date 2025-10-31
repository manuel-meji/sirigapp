package vista.salud;

import controlador.Controlador;
import controlador.FontLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class panelDesparacitaciones extends JPanel {
	private Controlador controlador;
	private JTable table;
	private DefaultTableModel model;
	private JDateChooser dcFecha;
	private JComboBox<ProductoItem> cbProducto; // Cambiado a JComboBox

	// --- Fuentes optimizadas para la nueva interfaz ---
	private final Font FONT_TITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Black.ttf", 32f);
	private final Font FONT_SUBTITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Bold.ttf", 24f);
	private final Font FONT_LABEL = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 16f); // Para etiquetas (labels)
	private final Font FONT_INPUT = FontLoader.loadFont("/resources/fonts/Montserrat-Light.ttf", 16f);   // Para campos de texto y combos
	private final Font FONT_BOTON = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 14f);

	/**
	 * Clase interna para almacenar el ID y el Nombre del producto en el JComboBox.
	 * El JComboBox mostrará el 'nombre' gracias al método toString().
	 */
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
			return nombre; // Esto es lo que se mostrará en el JComboBox
		}
	}

	public panelDesparacitaciones(Controlador controlador) {
		this.controlador = controlador;
		setLayout(new BorderLayout());
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
				new EmptyBorder(16, 16, 16, 16)
		));

		// --- Panel de formulario con Layout Absoluto ---
		JPanel form = new JPanel(null);
		form.setOpaque(false);
		form.setPreferredSize(new Dimension(0, 180));

		// --- Fila 1: ID Animal ---
		JLabel lblAnimal = new JLabel("ID Animal:");
		lblAnimal.setFont(FONT_LABEL);
		lblAnimal.setBounds(10, 10, 150, 30);
		form.add(lblAnimal);

		JComboBox<String> cbAnimal = new JComboBox<>();
		cbAnimal.setFont(FONT_INPUT);
		List<String> animales = controlador.buscarAnimales("");
		for (String a : animales) {
			cbAnimal.addItem(a);
		}
		cbAnimal.setBounds(170, 10, 250, 30);
		form.add(cbAnimal);

		// --- Fila 2: Producto (Ahora un JComboBox) ---
		JLabel lblProducto = new JLabel("Producto:");
		lblProducto.setFont(FONT_LABEL);
		lblProducto.setBounds(10, 50, 150, 30);
		form.add(lblProducto);

		cbProducto = new JComboBox<>();
		cbProducto.setFont(FONT_INPUT);
		cbProducto.setBounds(170, 50, 250, 30);
		form.add(cbProducto);
		cargarProductos(); // Llamada al método que llena el combo

		// --- Fila 3: Dosis ---
		JLabel lblDosis = new JLabel("Dosis:");
		lblDosis.setFont(FONT_LABEL);
		lblDosis.setBounds(450, 10, 100, 30);
		form.add(lblDosis);

		JTextField txtDosis = new JTextField();
		txtDosis.setFont(FONT_INPUT);
		txtDosis.setBounds(560, 10, 250, 30);
		form.add(txtDosis);

		// --- Fila 4: Fecha ---
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

		// --- Botón Guardar ---
		JButton btnGuardar = new JButton("Registrar");
		btnGuardar.setBackground(new Color(67, 160, 71));
		btnGuardar.setForeground(Color.WHITE);
		btnGuardar.setFont(FONT_BOTON);
		btnGuardar.setBounds(560, 110, 250, 40);
		form.add(btnGuardar);

		card.add(form, BorderLayout.NORTH);

		String[] cols = {"ID", "Fecha", "Animal", "Producto ID", "Dosis", "Motivo"};
		model = new DefaultTableModel(cols, 0) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		table = new JTable(model);
		table.setRowHeight(28);
		table.setFont(FONT_INPUT.deriveFont(14f));
		table.getTableHeader().setFont(FONT_LABEL.deriveFont(14f));
		card.add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btns.setOpaque(false);
		JButton btnEditar = new JButton("Editar");
		JButton btnEliminar = new JButton("Eliminar");

		btnEditar.setBackground(new Color(100, 181, 246));
		btnEditar.setForeground(Color.WHITE);
		btnEditar.setFont(FONT_BOTON);
		btnEliminar.setBackground(new Color(229, 57, 53));
		btnEliminar.setForeground(Color.WHITE);
		btnEliminar.setFont(FONT_BOTON);

		btns.add(btnEditar);
		btns.add(btnEliminar);
		card.add(btns, BorderLayout.SOUTH);

		content.add(card, BorderLayout.CENTER);

		// --- Listeners (Lógica de guardar actualizada) ---
		btnGuardar.addActionListener(ae -> {
			try {
				String animal = cbAnimal.getSelectedItem() == null ? "" : cbAnimal.getSelectedItem().toString();
				
				// Obtener el producto seleccionado del JComboBox
				ProductoItem productoSeleccionado = (ProductoItem) cbProducto.getSelectedItem();
				if (productoSeleccionado == null) {
					JOptionPane.showMessageDialog(this, "Debe seleccionar un producto.", "Error de validación", JOptionPane.WARNING_MESSAGE);
					return;
				}
				Integer productoId = productoSeleccionado.getId();

				Float dosis = Float.parseFloat(txtDosis.getText());

				if (dosis <= 0 ) {
					JOptionPane.showMessageDialog(this, "La dosis debe ser un número positivo.", "Error de validación", JOptionPane.WARNING_MESSAGE);
					return;
				}
				java.sql.Timestamp fecha = new java.sql.Timestamp(dcFecha.getDate().getTime());

				controlador.guardarEventoSanitario(fecha, productoId, dosis, "DESPARASITANTE", "", animal, "DESPARASITACION");
				
				cargar(); // Recargar la tabla
				JOptionPane.showMessageDialog(this, "Desparasitación registrada exitosamente");
			} catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Debe colocar la cantidad de Dosis " , "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error al guardar el registro: " + ex.getMessage(), "Error General", JOptionPane.ERROR_MESSAGE);
			}
		});

		btnEliminar.addActionListener(ae -> {
			int r = table.getSelectedRow();
			if (r == -1) {
				JOptionPane.showMessageDialog(this, "Seleccione una fila para eliminar.");
				return;
			}
			int id = (int) model.getValueAt(r, 0);
			controlador.eliminarEvento(id);
			cargar();
		});

		btnEditar.addActionListener(ae -> {
			int r = table.getSelectedRow();
			if (r == -1) {
				JOptionPane.showMessageDialog(this, "Seleccione una fila para editar.");
				return;
			}
			int id = (int) model.getValueAt(r, 0);
			controlador.editarEvento(id);
			cargar();
		});

		cargar(); // Carga inicial de la tabla de eventos
		return content;
	}

	/**
	 * Carga los eventos sanitarios de tipo "DESPARASITACION" en la tabla.
	 */
	private void cargar() {
		model.setRowCount(0);
		List<Object[]> rows = controlador.obtenerEventosSanitariosPorTipo("DESPARASITACION");
		for (Object[] r : rows) {
			model.addRow(r);
		}
	}

	/**
	 * Carga los productos de tipo "DESPARASITANTE" en el JComboBox.
	 */
	private void cargarProductos() {
		cbProducto.removeAllItems(); // Limpiar combo por si acaso
		List<Object[]> productos = controlador.obtenerProductosPorTipo("Desparasitante");
		for (Object[] p : productos) {
			// p[0] es el id (Integer), p[1] es el producto (String)
			int id = (int) p[0];
			String nombre = (String) p[1];
			cbProducto.addItem(new ProductoItem(id, nombre));
		}
	}
}