package vista.animales;

import controlador.Controlador;
import controlador.FontLoader;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class panelMostrarAnimales {

	// --- Fuentes ---
	private final Font FONT_TITULO_MENU = FontLoader.loadFont("/resources/fonts/Montserrat-Black.ttf", 32f);
	private final Font FONT_BOTON_MENU = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 16f);

	/**
	 * Retorna un JPanel con la tabla de animales y botones de acción.
	 * @param controlador El controlador para obtener y modificar datos.
	 * @param onAgregar Acción para redirigir al panel de registro.
	 */
	public JPanel createContentPanel(Controlador controlador, Runnable onAgregar) {
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBackground(new Color(245, 246, 248));

		JLabel title = new JLabel("Listado de Animales");
		title.setFont(FONT_TITULO_MENU.deriveFont(Font.BOLD, 26f));
		title.setBorder(new EmptyBorder(24, 32, 8, 32));
		title.setHorizontalAlignment(SwingConstants.LEFT);
		contentPanel.add(title, BorderLayout.NORTH);

		// Panel central con la tabla
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setOpaque(false);
		centerPanel.setBorder(new EmptyBorder(16, 32, 32, 32));

		// Modelo de la tabla
		String[] columnas = {"Código", "Fecha Nac.", "Sexo", "Raza", "Peso Nac.", "Peso", "Madre", "Padre", "Estado"};
		DefaultTableModel model = new DefaultTableModel(columnas, 0) {
			public boolean isCellEditable(int row, int column) { return false; }
		};

		// Obtener datos de animales del controlador
		java.util.List<Object[]> animales = controlador.obtenerAnimales(); // Debe retornar List<Object[]>
		for (Object[] animal : animales) {
			model.addRow(animal);
		}

		JTable table = new JTable(model);
		table.setFont(FONT_BOTON_MENU);
		table.setRowHeight(28);
		table.getTableHeader().setFont(FONT_BOTON_MENU.deriveFont(Font.BOLD, 16f));
		table.getTableHeader().setBackground(new Color(220, 223, 230));
		table.setSelectionBackground(new Color(232, 240, 254));
		table.setSelectionForeground(Color.BLACK);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 223, 230), 1));
		centerPanel.add(scrollPane, BorderLayout.CENTER);

		// Panel de botones de acción
		JPanel actionsPanel = new JPanel();
		actionsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 16, 16));
		actionsPanel.setOpaque(false);

		JButton btnEditar = new JButton("Editar");
		JButton btnEliminar = new JButton("Eliminar");
		JButton btnAgregar = new JButton("Agregar animal");

		btnEditar.setFont(FONT_BOTON_MENU);
		btnEliminar.setFont(FONT_BOTON_MENU);
		btnAgregar.setFont(FONT_BOTON_MENU);

		btnEditar.setBackground(new Color(100, 181, 246));
		btnEditar.setForeground(Color.WHITE);
		btnEliminar.setBackground(new Color(229, 57, 53));
		btnEliminar.setForeground(Color.WHITE);
		btnAgregar.setBackground(new Color(0xFF6AFFC1));
		btnAgregar.setForeground(Color.WHITE);

		btnEditar.setFocusPainted(false);
		btnEliminar.setFocusPainted(false);
		btnAgregar.setFocusPainted(false);

		actionsPanel.add(btnEditar);
		actionsPanel.add(btnEliminar);
		actionsPanel.add(btnAgregar);

		// Acción para agregar animal
		btnAgregar.addActionListener(e -> {
			if (onAgregar != null) onAgregar.run();
		});

		// Acción para editar animal seleccionado
		btnEditar.addActionListener(e -> {
			int row = table.getSelectedRow();
			if (row == -1) {
				JOptionPane.showMessageDialog(contentPanel, "Seleccione un animal para editar.");
				return;
			}
			// Aquí puedes abrir un panel de edición, por ejemplo:
			Object codigo = table.getValueAt(row, 0);
			controlador.editarAnimal(codigo); // Implementa este método en el controlador
		});

		// Acción para eliminar animal seleccionado
		btnEliminar.addActionListener(e -> {
			int row = table.getSelectedRow();
			if (row == -1) {
				JOptionPane.showMessageDialog(contentPanel, "Seleccione un animal para eliminar.");
				return;
			}
			Object codigo = table.getValueAt(row, 0);
			int confirm = JOptionPane.showConfirmDialog(contentPanel, "¿Está seguro de eliminar el animal?", "Confirmar", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				controlador.eliminarAnimal(codigo); // Implementa este método en el controlador
				model.removeRow(row);
			}
		});

		centerPanel.add(actionsPanel, BorderLayout.SOUTH);
		contentPanel.add(centerPanel, BorderLayout.CENTER);

		return contentPanel;
	}
}