package vista.salud;

import controlador.Controlador;
import controlador.FontLoader;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;

public class panelEventosSanitarios extends JPanel {
	private Controlador controlador;
	private JTable table;
	private DefaultTableModel model;
	private JDateChooser dcFecha;
	private JComboBox<String> cbAnimal;
	private JTextField txtMotivo;
	private JTextField txtDiagnostico;
	private JTextField txtDosis;

	private final Font FONT_TITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Black.ttf", 32f);
	private final Font FONT_NORMAL = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 18f);

	public panelEventosSanitarios(Controlador controlador) {
		this.controlador = controlador;
		setLayout(new BorderLayout());
	}

	public JPanel createContentPanel() {
		JPanel content = new JPanel(new BorderLayout());
		content.setBackground(new Color(245,246,248));

		JLabel title = new JLabel("Eventos Sanitarios");
		title.setFont(FONT_TITULO.deriveFont(Font.BOLD, 24f));
		title.setBorder(new EmptyBorder(20,24,8,24));
		content.add(title, BorderLayout.NORTH);

		JPanel card = new JPanel(new BorderLayout());
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(220,223,230),1),
			new EmptyBorder(16,16,16,16)
		));

		// Form
		JPanel form = new JPanel(new GridBagLayout());
		form.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(6,6,6,6);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("ID Animal:"), gbc);
		cbAnimal = new JComboBox<>(); cbAnimal.setFont(FONT_NORMAL);
		gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; form.add(cbAnimal, gbc);

		gbc.gridx = 2; gbc.gridy = 0; form.add(new JLabel("Tipo:"), gbc);
		JComboBox<String> cbTipo = new JComboBox<>(new String[]{"DESPARASITACION","TRATAMIENTO"}); cbTipo.setFont(FONT_NORMAL);
		gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0; form.add(cbTipo, gbc);

		gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Producto:"), gbc);
		JComboBox<String> cbProducto = new JComboBox<>(); cbProducto.setFont(FONT_NORMAL);
		// Cargar productos
		java.util.List<Object[]> productos = controlador.obtenerProductos();
		DefaultComboBoxModel<String> productosModel = new DefaultComboBoxModel<>();
		for(Object[] producto : productos) {
			productosModel.addElement(producto[0] + " - " + producto[1]);
		}
		cbProducto.setModel(productosModel);
		gbc.gridx = 1; gbc.gridy = 1; form.add(cbProducto, gbc);

		gbc.gridx = 2; gbc.gridy = 1; form.add(new JLabel("Dosis:"), gbc);
		txtDosis = new JTextField(); txtDosis.setFont(FONT_NORMAL);
		gbc.gridx = 3; gbc.gridy = 1; form.add(txtDosis, gbc);

		gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Motivo:"), gbc);
		txtMotivo = new JTextField(); txtMotivo.setFont(FONT_NORMAL);
		gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3; form.add(txtMotivo, gbc); gbc.gridwidth = 1;

		gbc.gridx = 0; gbc.gridy = 3; form.add(new JLabel("Diagnóstico:"), gbc);
		txtDiagnostico = new JTextField(); txtDiagnostico.setFont(FONT_NORMAL);
		gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 3; form.add(txtDiagnostico, gbc); gbc.gridwidth = 1;

		gbc.gridx = 0; gbc.gridy = 4; form.add(new JLabel("Fecha:"), gbc);
		dcFecha = new JDateChooser(); dcFecha.setDate(new Date()); dcFecha.setDateFormatString("yyyy-MM-dd");
		gbc.gridx = 1; gbc.gridy = 4; form.add(dcFecha, gbc);

		JButton btnGuardar = new JButton("Registrar evento");
		btnGuardar.setBackground(new Color(67,160,71)); btnGuardar.setForeground(Color.WHITE);
		gbc.gridx = 3; gbc.gridy = 4; form.add(btnGuardar, gbc);

		card.add(form, BorderLayout.NORTH);

		// Tabla
		String[] cols = {"ID","Fecha","Tipo","Animal","Producto","Dosis","Motivo","Diagnóstico"};
		model = new DefaultTableModel(cols,0) { public boolean isCellEditable(int r,int c){return false;} };
		table = new JTable(model);
		table.setRowHeight(28);
		JScrollPane sp = new JScrollPane(table);
		sp.setBorder(new EmptyBorder(8,0,0,0));
		card.add(sp, BorderLayout.CENTER);

		// Botones editar/eliminar
		JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT)); btns.setOpaque(false);
		JButton btnEditar = new JButton("Editar");
		JButton btnEliminar = new JButton("Eliminar");
		btnEditar.setBackground(new Color(100,181,246)); btnEditar.setForeground(Color.WHITE);
		btnEliminar.setBackground(new Color(229,57,53)); btnEliminar.setForeground(Color.WHITE);
		btns.add(btnEditar); btns.add(btnEliminar);
		card.add(btns, BorderLayout.SOUTH);

		content.add(card, BorderLayout.CENTER);

		// Cargar inicial
		cargarEventos();

		// Listeners básicos
		btnGuardar.addActionListener(_ -> {
			try {
				String animal = cbAnimal.getSelectedItem() == null ? "" : cbAnimal.getSelectedItem().toString();
				String tipo = cbTipo.getSelectedItem().toString();
				String productoSeleccionado = cbProducto.getSelectedItem().toString();
				int idProducto = Integer.parseInt(productoSeleccionado.split(" - ")[0]);
				String dosis = txtDosis.getText().trim();
				String motivo = txtMotivo.getText().trim();
				String diag = txtDiagnostico.getText().trim();
				java.sql.Timestamp fecha = new java.sql.Timestamp(dcFecha.getDate().getTime());
				controlador.guardarEventoSanitario(fecha, idProducto, dosis.isEmpty()?null:Float.parseFloat(dosis), motivo, diag, animal, tipo);
				cargarEventos();
				JOptionPane.showMessageDialog(this, "Evento registrado");
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
			}
		});

		btnEliminar.addActionListener(_ -> {
			int r = table.getSelectedRow();
			if (r==-1) { JOptionPane.showMessageDialog(this, "Seleccione un evento"); return; }
			int id = (int) model.getValueAt(r,0);
			controlador.eliminarEvento(id);
			cargarEventos();
		});

		btnEditar.addActionListener(_ -> {
			int r = table.getSelectedRow();
			if (r==-1) { JOptionPane.showMessageDialog(this, "Seleccione un evento"); return; }
			int id = (int) model.getValueAt(r,0);
			controlador.editarEvento(id);
			cargarEventos();
		});

		// rellenar combo de animales
		java.util.List<String> animales = controlador.buscarAnimales("");
		for (String a: animales) cbAnimal.addItem(a);

		return content;
	}

	private void cargarEventos(){
		model.setRowCount(0);
		java.util.List<Object[]> rows = controlador.obtenerEventosSanitarios();
		for (Object[] r: rows) model.addRow(r);
	}
}
