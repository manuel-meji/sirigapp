package vista.salud;

import controlador.Controlador;
import controlador.FontLoader;

import javax.swing.*;
import java.awt.*;

/**
 * Panel contenedor de salud que muestra pestañas para eventos sanitarios y desparasitaciones.
 */
public class vistaSalud extends JPanel {
	private final Font FONT_TITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Black.ttf", 28f);

	private Controlador controlador;

	public vistaSalud(Controlador controlador) {
		this.controlador = controlador;
		setLayout(new BorderLayout());
	}

	/**
	 * Crea el panel principal con un JTabbedPane que contiene los dos subpanes.
	 */
	public JPanel createContentPanel() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(new Color(245,246,248));

		JLabel title = new JLabel("Gestión de Salud");
		title.setFont(FONT_TITULO.deriveFont(Font.BOLD, 24f));
		title.setBorder(BorderFactory.createEmptyBorder(20, 24, 8, 24));
		main.add(title, BorderLayout.NORTH);

		JTabbedPane tabs = new JTabbedPane();

		panelEventosSanitarios eventos = new panelEventosSanitarios(controlador);
		panelDesparacitaciones despar = new panelDesparacitaciones(controlador);
        panelProductos productos = new panelProductos(controlador);

		tabs.addTab("Eventos sanitarios", eventos.createContentPanel());
		tabs.addTab("Desparasitaciones", despar.createContentPanel());
        tabs.addTab("Productos", productos.createContentPanel());

		main.add(tabs, BorderLayout.CENTER);

		return main;
	}
}
