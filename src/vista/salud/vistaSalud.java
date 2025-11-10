package vista.salud;

import controlador.Controlador;
import controlador.FontLoader;
import java.awt.*;
import javax.swing.*;

public class vistaSalud extends JPanel {
    private final Font FONT_TITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Black.ttf", 28f);
    private Controlador controlador;
    
    // Agregar estas declaraciones como atributos de clase
    private panelEventosSanitarios eventos;
    private panelDesparasitaciones despar;
    private panelProductos productos;

    public vistaSalud(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
    }

    public JPanel createContentPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245,246,248));

        JLabel title = new JLabel("Gestión de Salud");
        title.setFont(FONT_TITULO.deriveFont(Font.BOLD, 26f));
        title.setBorder(BorderFactory.createEmptyBorder(20, 24, 8, 24));
        main.add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(controlador.estilos.FONT_TITLE_TAB.deriveFont(Font.PLAIN, 16f));

        // Inicializar los paneles usando los atributos de clase
       eventos = new panelEventosSanitarios(controlador);
        despar = new panelDesparasitaciones(controlador);
        productos = new panelProductos(controlador);

       tabs.addTab("Eventos sanitarios", eventos.createContentPanel());
        tabs.addTab("Desparasitaciones", despar.createContentPanel());
        tabs.addTab("Productos", productos.createContentPanel());

        main.add(tabs, BorderLayout.CENTER);

        return main;
    }

    public void actualizarPanelesInternos() {
        
        
        if (eventos != null) {
            eventos.cargarProductos();
            eventos.cargarEventos(); 
        }
        if (despar != null) {
            despar.cargarProductos();
            despar.cargarEventos();
        }
    }
}