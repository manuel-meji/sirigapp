package vista.lotes;

import controlador.Controlador;
import java.awt.*;
import javax.swing.*;

public class VistaLotes extends JPanel {

    private Controlador controlador;
    private JTabbedPane tabbedPane;
    private panelRegistroLotes panelRegistro;
    public static  panelHistorialLotes panelHistorial;

    public VistaLotes(Controlador controlador) {
        this.controlador = controlador;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Administración de Lotes");
        title.setFont(controlador.estilos.FONT_TITULO.deriveFont(Font.BOLD, 28f));
        title.setBorder(BorderFactory.createEmptyBorder(20, 24, 8, 24));
        add(title, BorderLayout.NORTH);
        panelRegistro = new panelRegistroLotes(controlador);
        panelHistorial = new panelHistorialLotes(controlador);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(controlador.estilos.FONT_TITLE_TAB.deriveFont(Font.PLAIN, 16f));
        
        tabbedPane.addTab("Registro de Lotes", panelRegistro);
        tabbedPane.addTab("Historial de Lotes", panelHistorial);
        
        add(tabbedPane, BorderLayout.CENTER);

    

    }
}