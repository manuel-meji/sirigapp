package vista.salud;

import controlador.Controlador;
import controlador.FontLoader;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class panelEventosSanitarios extends JPanel {
	private Controlador controlador;
	private JTable table;
	private DefaultTableModel model;
	private JDateChooser dcFecha;
	private JComboBox<String> cbAnimal;
	private JComboBox<ProductoItem> cbProducto; // Usamos ProductoItem para manejar ID y Nombre
	private JTextField txtMotivo;
	private JTextField txtDiagnostico;
	private JTextField txtDosis;

	// --- Fuentes para una interfaz moderna ---
	private final Font FONT_SUBTITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Bold.ttf", 24f);
	private final Font FONT_LABEL = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 16f);
	private final Font FONT_INPUT = FontLoader.loadFont("/resources/fonts/Montserrat-Light.ttf", 16f);
	private final Font FONT_BOTON = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 14f);

	/**
	 * Clase interna para almacenar el ID y el Nombre del producto en el JComboBox.
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
			return nombre; // Esto es lo que se mostrará en la lista del JComboBox
		}
	}

	public panelEventosSanitarios(Controlador controlador) {
		this.controlador = controlador;
		setLayout(new BorderLayout());
	}

	public JPanel createContentPanel() {
		JPanel content = new JPanel(new BorderLayout());
		content.setBackground(new Color(245, 246, 248));

		JLabel title = new JLabel("Eventos Sanitarios");
		title.setFont(FONT_SUBTITULO);
		title.setBorder(new EmptyBorder(20, 24, 8, 24));
		content.add(title, BorderLayout.NORTH);

		JPanel card = new JPanel(new BorderLayout());
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(220, 223, 230), 1),
				new EmptyBorder(16, 16, 16, 16)
		));

		// --- Formulario con Layout Absoluto ---
		JPanel form = new JPanel(null); // Layout absoluto
		form.setOpaque(false);
		form.setPreferredSize(new Dimension(0, 230)); // Altura suficiente para los componentes

		// --- Columna 1 ---
		JLabel lblAnimal = new JLabel("ID Animal:");
		lblAnimal.setFont(FONT_LABEL);
		lblAnimal.setBounds(10, 10, 150, 30);
		form.add(lblAnimal);

		cbAnimal = new JComboBox<>();
		cbAnimal.setFont(FONT_INPUT);
		cbAnimal.setBounds(170, 10, 250, 30);
		form.add(cbAnimal);

		JLabel lblProducto = new JLabel("Producto:");
		lblProducto.setFont(FONT_LABEL);
		lblProducto.setBounds(10, 50, 150, 30);
		form.add(lblProducto);

		cbProducto = new JComboBox<>();
		cbProducto.setFont(FONT_INPUT);
		cbProducto.setBounds(170, 50, 250, 30);
		form.add(cbProducto);

		// --- Columna 2 ---
		JLabel lblDosis = new JLabel("Dosis:");
		lblDosis.setFont(FONT_LABEL);
		lblDosis.setBounds(450, 10, 100, 30);
		form.add(lblDosis);

		txtDosis = new JTextField();
		txtDosis.setFont(FONT_INPUT);
		txtDosis.setBounds(560, 10, 250, 30);
		//txtDosis.setBorder(null); // Sin borde
		txtDosis.setBackground(new Color(238, 238, 238)); // Fondo para visibilidad
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

		// --- Campos anchos ---
		JLabel lblMotivo = new JLabel("Motivo:");
		lblMotivo.setFont(FONT_LABEL);
		lblMotivo.setBounds(10, 90, 150, 30);
		form.add(lblMotivo);

		txtMotivo = new JTextField();
		txtMotivo.setFont(FONT_INPUT);
		txtMotivo.setBounds(170, 90, 640, 30);
		//txtMotivo.setBorder(null); // Sin borde
		txtMotivo.setBackground(new Color(238, 238, 238));
		form.add(txtMotivo);

		JLabel lblDiagnostico = new JLabel("Diagnóstico:");
		lblDiagnostico.setFont(FONT_LABEL);
		lblDiagnostico.setBounds(10, 130, 150, 30);
		form.add(lblDiagnostico);

		txtDiagnostico = new JTextField();
		txtDiagnostico.setFont(FONT_INPUT);
		txtDiagnostico.setBounds(170, 130, 640, 30);
		//txtDiagnostico.setBorder(null); // Sin borde
		txtDiagnostico.setBackground(new Color(238, 238, 238));
		form.add(txtDiagnostico);

		// --- Botón ---
		JButton btnGuardar = new JButton("Registrar Evento");
		btnGuardar.setBackground(new Color(67, 160, 71));
		btnGuardar.setForeground(Color.WHITE);
		btnGuardar.setFont(FONT_BOTON);
		btnGuardar.setBounds(560, 175, 250, 40);
		form.add(btnGuardar);

		card.add(form, BorderLayout.NORTH);

		// --- Tabla ---
		String[] cols = {"ID", "Fecha", "Tipo", "Animal", "Producto", "Dosis", "Motivo", "Diagnóstico"};
		model = new DefaultTableModel(cols, 0) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		table = new JTable(model);
		table.setRowHeight(28);
		table.setFont(FONT_INPUT.deriveFont(14f));
		table.getTableHeader().setFont(FONT_LABEL.deriveFont(14f));
		JScrollPane sp = new JScrollPane(table);
		sp.setBorder(new EmptyBorder(8, 0, 0, 0));
		card.add(sp, BorderLayout.CENTER);

		// --- Botones de acción de la tabla ---
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

		// --- Listeners ---
		btnGuardar.addActionListener(_ -> {
			try {
				String animal = cbAnimal.getSelectedItem() == null ? "" : cbAnimal.getSelectedItem().toString();
				
				ProductoItem productoSeleccionado = (ProductoItem) cbProducto.getSelectedItem();
				if (productoSeleccionado == null) {
					JOptionPane.showMessageDialog(this, "Debe seleccionar un producto.", "Error", JOptionPane.WARNING_MESSAGE);
					return;
				}
				int idProducto = productoSeleccionado.getId();

				String dosisStr = txtDosis.getText().trim();
				Float dosis = dosisStr.isEmpty() ? null : Float.parseFloat(dosisStr);
				
				String motivo = txtMotivo.getText().trim();
				String diag = txtDiagnostico.getText().trim();
				java.sql.Timestamp fecha = new java.sql.Timestamp(dcFecha.getDate().getTime());

				// El tipo es "TRATAMIENTO" por defecto, ya que este panel excluye desparasitaciones
				controlador.guardarEventoSanitario(fecha, idProducto, dosis, motivo, diag, animal, "TRATAMIENTO");
				
				cargarEventos();
				JOptionPane.showMessageDialog(this, "Evento sanitario registrado exitosamente.");
			} catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error en el formato del número de Dosis.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error al guardar el evento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});

		btnEliminar.addActionListener(_ -> {
			int r = table.getSelectedRow();
			if (r == -1) {
				JOptionPane.showMessageDialog(this, "Seleccione un evento para eliminar.");
				return;
			}
			int id = (int) model.getValueAt(r, 0);
			controlador.eliminarEvento(id);
			cargarEventos();
		});

		btnEditar.addActionListener(_ -> {
			int r = table.getSelectedRow();
			if (r == -1) {
				JOptionPane.showMessageDialog(this, "Seleccione un evento para editar.");
				return;
			}
			int id = (int) model.getValueAt(r, 0);
			controlador.editarEvento(id);
			cargarEventos();
		});

		// Carga inicial de datos
		cargarAnimales();
		cargarProductos();
		cargarEventos();

		return content;
	}

	/**
	 * Carga en la tabla todos los eventos sanitarios EXCEPTO las desparasitaciones.
	 */
	private void cargarEventos() {
		model.setRowCount(0);
		List<Object[]> rows = controlador.obtenerEventosSanitarios();
		for (Object[] r : rows) {
			// El índice 2 corresponde a la columna "tipo" en la consulta SQL del controlador
			if (r[2] != null && !r[2].toString().equalsIgnoreCase("DESPARASITACION")) {
				model.addRow(r);
			}
		}
	}

	/**
	 * Carga el JComboBox con todos los animales activos.
	 */
	private void cargarAnimales() {
		cbAnimal.removeAllItems();
		List<String> animales = controlador.buscarAnimales("");
		for (String a : animales) {
			cbAnimal.addItem(a);
		}
	}

	/**
	 * Carga el JComboBox con todos los productos EXCEPTO los de tipo "DESPARASITANTE".
	 */
	private void cargarProductos() {
		cbProducto.removeAllItems();
		List<Object[]> productos = controlador.obtenerProductos();
		for (Object[] p : productos) {
			// p[0]=id, p[1]=producto, p[2]=tipo
			if (p[2] != null && !p[2].toString().equalsIgnoreCase("Desparasitante")) {
				int id = (int) p[0];
				String nombre = (String) p[1];
				cbProducto.addItem(new ProductoItem(id, nombre));
			}
		}
	}
}