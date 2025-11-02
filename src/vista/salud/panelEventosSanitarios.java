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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class panelEventosSanitarios extends JPanel {

	private Controlador controlador;
	private JTable table;
	private DefaultTableModel model;
	private TableRowSorter<DefaultTableModel> sorter;
	private JDateChooser dcFecha;
	private JComboBox<String> cbAnimal;
	private JComboBox<ProductoItem> cbProducto;
	private JTextField txtMotivo;
	private JTextField txtDiagnostico;
	private JTextField txtDosis;
	private JTextField txtBusqueda;

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

	public panelEventosSanitarios(Controlador controlador) {
		this.controlador = controlador;
		setLayout(new BorderLayout());
		add(createContentPanel(), BorderLayout.CENTER);
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
				new EmptyBorder(16, 16, 16, 16)));

		JPanel form = new JPanel(null);
		form.setOpaque(false);
		form.setPreferredSize(new Dimension(0, 230));

		// --- ComboBox de Animal con su propia lógica ---
		JLabel lblAnimal = new JLabel("ID Animal:");
		lblAnimal.setFont(FONT_LABEL);
		lblAnimal.setBounds(10, 10, 150, 30);
		form.add(lblAnimal);
		cbAnimal = new JComboBox<>();
		cbAnimal.setEditable(true);
		cbAnimal.setFont(FONT_INPUT);
		cbAnimal.setBounds(170, 10, 250, 30);
		form.add(cbAnimal);

		JTextField editorAnimal = (JTextField) cbAnimal.getEditor().getEditorComponent();
		editorAnimal.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN) {
					filtrarAnimales(editorAnimal.getText());
				}
			}
		});
		editorAnimal.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (editorAnimal.getText().equals(PLACEHOLDER_ANIMAL)) {
					editorAnimal.setText("");
					editorAnimal.setForeground(Color.BLACK);
					if (cbAnimal.getItemCount() == 0) {
						filtrarAnimales(""); // Carga inicial
					}
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (editorAnimal.getText().trim().isEmpty()) {
					setPlaceholder(cbAnimal, PLACEHOLDER_ANIMAL);
				}
			}
		});

		// --- ComboBox de Producto con su propia lógica ---
		JLabel lblProducto = new JLabel("Producto:");
		lblProducto.setFont(FONT_LABEL);
		lblProducto.setBounds(10, 50, 150, 30);
		form.add(lblProducto);
		cbProducto = new JComboBox<>();
		cbProducto.setEditable(true);
		cbProducto.setFont(FONT_INPUT);
		cbProducto.setBounds(170, 50, 250, 30);
		form.add(cbProducto);

		JTextField editorProducto = (JTextField) cbProducto.getEditor().getEditorComponent();
		editorProducto.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN) {
					filtrarProductos(editorProducto.getText());
				}
			}
		});
		editorProducto.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (editorProducto.getText().equals(PLACEHOLDER_PRODUCTO)) {
					editorProducto.setText("");
					editorProducto.setForeground(Color.BLACK);
					if (cbProducto.getItemCount() == 0) {
						filtrarProductos(""); // Carga inicial
					}
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (editorProducto.getText().trim().isEmpty()) {
					setPlaceholder(cbProducto, PLACEHOLDER_PRODUCTO);
				}
			}
		});

		// --- Resto del formulario (sin cambios) ---
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
		JLabel lblMotivo = new JLabel("Motivo:");
		lblMotivo.setFont(FONT_LABEL);
		lblMotivo.setBounds(10, 90, 150, 30);
		form.add(lblMotivo);
		txtMotivo = new JTextField();
		txtMotivo.setFont(FONT_INPUT);
		txtMotivo.setBounds(170, 90, 640, 30);
		form.add(txtMotivo);
		JLabel lblDiagnostico = new JLabel("Diagnóstico:");
		lblDiagnostico.setFont(FONT_LABEL);
		lblDiagnostico.setBounds(10, 130, 150, 30);
		form.add(lblDiagnostico);
		txtDiagnostico = new JTextField();
		txtDiagnostico.setFont(FONT_INPUT);
		txtDiagnostico.setBounds(170, 130, 640, 30);
		form.add(txtDiagnostico);
		JLabel lblBusqueda = new JLabel("Buscar en tabla:");
		lblBusqueda.setFont(FONT_LABEL);
		lblBusqueda.setBounds(10, 175, 150, 30);
		form.add(lblBusqueda);
		txtBusqueda = new JTextField();
		txtBusqueda.setFont(FONT_INPUT);
		txtBusqueda.setBounds(170, 175, 380, 30);
		form.add(txtBusqueda);
		JButton btnGuardar = new JButton("Registrar Evento");
		btnGuardar.setBackground(new Color(67, 160, 71));
		btnGuardar.setForeground(Color.WHITE);
		btnGuardar.setFont(FONT_BOTON);
		btnGuardar.setBounds(560, 175, 250, 40);
		form.add(btnGuardar);
		card.add(form, BorderLayout.NORTH);

		String[] cols = { "ID", "Fecha", "Tipo", "Animal", "Producto", "Dosis", "Motivo", "Diagnóstico" };
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
		JScrollPane sp = new JScrollPane(table);
		sp.setBorder(new EmptyBorder(8, 0, 0, 0));
		card.add(sp, BorderLayout.CENTER);
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

		btnGuardar.addActionListener(_ -> guardarEvento());
		btnEliminar.addActionListener(_ -> eliminarEvento());
		btnEditar.addActionListener(_ -> editarEvento());

		
		txtBusqueda.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				filtrarTabla();
			}
		});

		// --- Botón ---
		btnGuardar = new JButton("Registrar Evento");
		btnGuardar.setBackground(controlador.estilos.COLOR_GUARDAR);
		btnGuardar.setForeground(Color.WHITE);
		btnGuardar.setFont(FONT_BOTON);
		btnGuardar.setBounds(560, 175, 250, 40);
		form.add(btnGuardar);
		cargarEventos();
		// Carga inicial de los placeholders
		setPlaceholder(cbAnimal, PLACEHOLDER_ANIMAL);
		setPlaceholder(cbProducto, PLACEHOLDER_PRODUCTO);

		return content;
	}

	private <T> void setPlaceholder(JComboBox<T> comboBox, String text) {
		JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();
		comboBox.removeAllItems();
		editor.setForeground(Color.GRAY);
		editor.setText(text);
	}

	// --- Botones de acción de la tabla ---
	
	private void filtrarAnimales(String filtro) {
        List<String> animales = controlador.buscarAnimales(filtro);
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cbAnimal.getModel();
        model.removeAllElements();
        animales.forEach(model::addElement);

        ((JTextField) cbAnimal.getEditor().getEditorComponent()).setText(filtro);
        if (!animales.isEmpty()) {
            cbAnimal.showPopup();
        } else {
            cbAnimal.hidePopup();
        }
    }

	private void filtrarProductos(String filtro) {
        List<Object[]> productos = controlador.buscarProductosTratamiento(filtro);
        DefaultComboBoxModel<ProductoItem> model = (DefaultComboBoxModel<ProductoItem>) cbProducto.getModel();
        model.removeAllElements();
        for (Object[] p : productos) {
            model.addElement(new ProductoItem((int) p[0], (String) p[1]));
        }

        ((JTextField) cbProducto.getEditor().getEditorComponent()).setText(filtro);
        if (!productos.isEmpty()) {
            cbProducto.showPopup();
        } else {
            cbProducto.hidePopup();
        }
    }

	private void limpiarFormulario() {
        setPlaceholder(cbAnimal, PLACEHOLDER_ANIMAL);
        setPlaceholder(cbProducto, PLACEHOLDER_PRODUCTO);
        txtDosis.setText("");
        txtMotivo.setText("");
        txtDiagnostico.setText("");
        dcFecha.setDate(new Date());
    }

	private void guardarEvento() {
        try {
            Object animalItem = cbAnimal.getSelectedItem();
            String animal = (animalItem == null) ? ((JTextField) cbAnimal.getEditor().getEditorComponent()).getText() : animalItem.toString();
            if (animal.trim().isEmpty() || animal.equals(PLACEHOLDER_ANIMAL)) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un animal válido.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Object productoItem = cbProducto.getSelectedItem();
            if (productoItem == null || !(productoItem instanceof ProductoItem)) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un producto válido.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idProducto = ((ProductoItem) productoItem).getId();

            String dosisStr = txtDosis.getText().trim();
            Float dosis = dosisStr.isEmpty() ? null : Float.parseFloat(dosisStr);
            if (dosis == null || dosis <= 0) {
                JOptionPane.showMessageDialog(this, "La dosis debe ser un número positivo.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String motivo = txtMotivo.getText().trim();
            String diag = txtDiagnostico.getText().trim();
            java.sql.Timestamp fecha = new java.sql.Timestamp(dcFecha.getDate().getTime());

            controlador.guardarEventoSanitario(fecha, idProducto, dosis, motivo, diag, animal, "TRATAMIENTO");

            cargarEventos();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Evento sanitario registrado exitosamente.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error en el formato del número de Dosis.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar el evento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

	private void eliminarEvento() {
        int filaVista = table.getSelectedRow();
        if (filaVista == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento para eliminar.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar el evento seleccionado?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        int filaModelo = table.convertRowIndexToModel(filaVista);
        int id = (int) model.getValueAt(filaModelo, 0);
        controlador.eliminarEvento(id);
        cargarEventos();
    }

	private void editarEvento() {
        int filaVista = table.getSelectedRow();
        if (filaVista == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento para editar.");
            return;
        }
        int filaModelo = table.convertRowIndexToModel(filaVista);
        int id = (int) model.getValueAt(filaModelo, 0);
        controlador.editarEvento(id);
        cargarEventos();
    }

	private void filtrarTabla() {
        String texto = txtBusqueda.getText();
        if (texto.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }

	private void cargarEventos() {
        model.setRowCount(0);
        List<Object[]> rows = controlador.obtenerEventosSanitarios();
        for (Object[] r : rows) {
            if (r[2] != null && !r[2].toString().equalsIgnoreCase("DESPARASITACION")) {
                Date fecha = (Date) r[1];
                String fechaFormateada = (fecha != null) ? sdf.format(fecha) : "";

                Object[] filaFormateada = {r[0], fechaFormateada, r[2], r[3], r[4], r[5], r[6], r[7]};
                model.addRow(filaFormateada);
            }
        }
    }
}
