package vista.lotes;

import controlador.Controlador;
import javax.swing.*;
import java.awt.*;

public class VistaLotes extends JPanel { // <-- CAMBIADO DE JFRAME A JPANEL

    private Controlador controlador;
    private JTabbedPane tabbedPane;
    private panelRegistroLotes panelRegistro;
    private panelHistorialLotes panelHistorial;

    public VistaLotes(Controlador controlador) {
        this.controlador = controlador;
        initComponents();
    }

    private void initComponents() {
        // Establecemos el layout para este panel contenedor
        setLayout(new BorderLayout());
        
        // Inicializamos los paneles de las pestañas
        panelRegistro = new panelRegistroLotes(controlador);
        panelHistorial = new panelHistorialLotes(controlador);

        // Creamos el JTabbedPane
        tabbedPane = new JTabbedPane();
        
        // Añadimos los paneles como pestañas
        tabbedPane.addTab("Registro de Lotes", panelRegistro);
        tabbedPane.addTab("Historial de Lotes", panelHistorial);
        
        // Añadimos el JTabbedPane al panel principal (VistaLotes)
        add(tabbedPane, BorderLayout.CENTER);
    }
}