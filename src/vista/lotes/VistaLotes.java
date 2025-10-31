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
        // System.out.println("1. [DEBUG] Entrando al constructor de VistaLotes...");
        this.controlador = controlador;
        initComponents();
       //  System.out.println("7. [DEBUG] Fin del constructor de VistaLotes. Panel creado con éxito.");
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        //System.out.println("2. [DEBUG] Creando panelRegistroLotes...");
        panelRegistro = new panelRegistroLotes(controlador);
        //System.out.println("4. [DEBUG] panelRegistroLotes CREADO. Creando panelHistorialLotes...");
        panelHistorial = new panelHistorialLotes(controlador);
        //System.out.println("6. [DEBUG] panelHistorialLotes CREADO. Añadiendo pestañas...");

        tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Registro de Lotes", panelRegistro);
        tabbedPane.addTab("Historial de Lotes", panelHistorial);
        
        add(tabbedPane, BorderLayout.CENTER);

    

    }
}