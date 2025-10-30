package vista.salud;

import controlador.Controlador;
import controlador.FontLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.util.Date;

public class panelDesparacitaciones extends JPanel {
	private Controlador controlador;
	private JTable table;
	private DefaultTableModel model;
	private JDateChooser dcFecha;

	private final Font FONT_TITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Black.ttf", 32f);
	private final Font FONT_NORMAL = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 18f);

	public panelDesparacitaciones(Controlador controlador) {
		this.controlador = controlador;
		setLayout(new BorderLayout());
	}

	public JPanel createContentPanel() {
		JPanel content = new JPanel(new BorderLayout());
		content.setBackground(new Color(245,246,248));

		JLabel title = new JLabel("Desparasitaciones");
		title.setFont(FONT_TITULO.deriveFont(Font.BOLD, 24f));
		title.setBorder(new EmptyBorder(20,24,8,24));
		content.add(title, BorderLayout.NORTH);

		JPanel card = new JPanel(new BorderLayout());
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(220,223,230),1),
			new EmptyBorder(16,16,16,16)
		));

		// Form minimal para desparasitaciones
		JPanel form = new JPanel(new GridBagLayout()); form.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(6,6,6,6); gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx=0; gbc.gridy=0; form.add(new JLabel("ID Animal:"), gbc);
		JComboBox<String> cbAnimal = new JComboBox<>(); cbAnimal.setFont(FONT_NORMAL);
		java.util.List<String> animales = controlador.buscarAnimales(""); for (String a: animales) cbAnimal.addItem(a);
		gbc.gridx=1; gbc.gridy=0; gbc.weightx=1.0; form.add(cbAnimal, gbc);

		gbc.gridx=0; gbc.gridy=1; form.add(new JLabel("Producto ID:"), gbc);
		JTextField txtProducto = new JTextField(); txtProducto.setFont(FONT_NORMAL);
		gbc.gridx=1; gbc.gridy=1; form.add(txtProducto, gbc);

		gbc.gridx=0; gbc.gridy=2; form.add(new JLabel("Dosis:"), gbc);
		JTextField txtDosis = new JTextField(); txtDosis.setFont(FONT_NORMAL);
		gbc.gridx=1; gbc.gridy=2; form.add(txtDosis, gbc);

		gbc.gridx=0; gbc.gridy=3; form.add(new JLabel("Fecha:"), gbc);
		dcFecha = new JDateChooser(); dcFecha.setDate(new Date()); dcFecha.setDateFormatString("yyyy-MM-dd");
		gbc.gridx=1; gbc.gridy=3; form.add(dcFecha, gbc);

		JButton btnGuardar = new JButton("Registrar"); btnGuardar.setBackground(new Color(67,160,71)); btnGuardar.setForeground(Color.WHITE);
		gbc.gridx=1; gbc.gridy=4; form.add(btnGuardar, gbc);

		card.add(form, BorderLayout.NORTH);

		String[] cols = {"ID","Fecha","Animal","Producto","Dosis","Motivo","Diagnóstico"};
		model = new DefaultTableModel(cols,0) { public boolean isCellEditable(int r,int c){return false;} };
		table = new JTable(model); table.setRowHeight(28);
		card.add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT)); btns.setOpaque(false);
		JButton btnEditar = new JButton("Editar"); JButton btnEliminar = new JButton("Eliminar");
		btnEditar.setBackground(new Color(100,181,246)); btnEditar.setForeground(Color.WHITE);
		btnEliminar.setBackground(new Color(229,57,53)); btnEliminar.setForeground(Color.WHITE);
		btns.add(btnEditar); btns.add(btnEliminar);
		card.add(btns, BorderLayout.SOUTH);

		content.add(card, BorderLayout.CENTER);

		// listeners
		btnGuardar.addActionListener(ae -> {
			try {
				String animal = cbAnimal.getSelectedItem()==null?"":cbAnimal.getSelectedItem().toString();
				Integer producto = txtProducto.getText().trim().isEmpty()?null:Integer.parseInt(txtProducto.getText().trim());
				Float dosis = txtDosis.getText().trim().isEmpty()?null:Float.parseFloat(txtDosis.getText().trim());
				java.sql.Timestamp fecha = new java.sql.Timestamp(dcFecha.getDate().getTime());
				controlador.guardarEventoSanitario(fecha, producto, dosis, "DESPARASITANTE", "", animal, "DESPARASITACION");
				cargar();
				JOptionPane.showMessageDialog(this, "Desparasitacion registrada");
			} catch(Exception ex){ JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
		});

		btnEliminar.addActionListener(ae -> {
			int r = table.getSelectedRow(); if (r==-1){ JOptionPane.showMessageDialog(this, "Seleccione"); return; }
			int id = (int) model.getValueAt(r,0); controlador.eliminarEvento(id); cargar();
		});

		btnEditar.addActionListener(ae -> {
			int r = table.getSelectedRow(); if (r==-1){ JOptionPane.showMessageDialog(this, "Seleccione"); return; }
			int id = (int) model.getValueAt(r,0); controlador.editarEvento(id); cargar();
		});

		cargar();
		return content;
	}

	private void cargar(){
		model.setRowCount(0);
		java.util.List<Object[]> rows = controlador.obtenerEventosSanitariosPorTipo("DESPARASITACION");
		for (Object[] r: rows) model.addRow(r);
	}
}
