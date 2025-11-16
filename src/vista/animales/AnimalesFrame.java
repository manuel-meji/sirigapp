package vista.animales;

import controlador.Controlador;
import vista.SiriGAppLogin;
import vista.informes.panelInformes;
import vista.lotes.VistaLotes;
import vista.produccion.panelProduccionLeche;
import vista.salud.vistaSalud;
import vista.ui.DesignSystem;

import javax.swing.*;
 
 

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AnimalesFrame extends JFrame {

    //Paneles de contenido
    public panelRegistroAnimales pRegistro;
    public panelMostrarAnimales pMostrar;
    public panelSalidaAnimales pSalida;
    public VistaLotes pLotes;
    public vistaSalud pSalud;
    public panelInformes pInformes;
    public panelProduccionLeche pProduccionLeche;
    

    private Controlador controlador;
    JPanel contentPanel;
    // Panel de contenido principal

    // --- Colores (Design System) ---
    private final Color COLOR_FONDO_MENU = DesignSystem.COLOR_FONDO_MENU;
    private final Color COLOR_BOTON_NORMAL = DesignSystem.COLOR_BOTON_NORMAL;
    private final Color COLOR_TEXTO_BOTON = DesignSystem.COLOR_TEXTO_BOTON;
    private final Color COLOR_BOTON_CERRAR_SESION = DesignSystem.COLOR_BOTON_CERRAR_SESION;
    


    // --- Fuentes ---
    private final Font FONT_TITULO_MENU = DesignSystem.FONT_TITULO_MENU;
    private final Font FONT_BOTON_MENU = DesignSystem.FONT_BOTON_MENU;

    public AnimalesFrame(Controlador controlador) {
        this.controlador = controlador;
        initUI();
    }

    private void initUI() {
        setTitle("SiriGApp - Sistema de Identificación y Registro Individual Ganadero");
        setSize(1366, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Image icon = Toolkit.getDefaultToolkit().getImage("src/resources/images/icon-sirigapp.png");
        this.setIconImage(icon);


        //Inicialización de los paneles
        pMostrar = new panelMostrarAnimales();
        pLotes = new VistaLotes(controlador);
        pSalida = new panelSalidaAnimales(controlador);
        pSalud = new vistaSalud(controlador);
        pProduccionLeche = new panelProduccionLeche(controlador);
        pRegistro = new panelRegistroAnimales(controlador);
        pInformes = new panelInformes(controlador);

        // 1. Panel del Menú Lateral
        JPanel menuPanel = createMenuPanel();
        add(menuPanel, BorderLayout.WEST);

        // 2. Panel de Contenido Principal (el área a la derecha del menú)
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);
        
        
        cambiarPanelContenido(pRegistro.createContentPanel());

    }



    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_FONDO_MENU);
        panel.setPreferredSize(new Dimension(300, 0)); // Ancho del menú
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Espaciado superior
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.2; // Espacio en la parte superior
        panel.add(new JLabel(), gbc);
    
        // Título "SiriGApp"
        JLabel menuTitle = new JLabel("SiriGApp");
        menuTitle.setFont(FONT_TITULO_MENU);
        menuTitle.setForeground(Color.WHITE);
        menuTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0); // Reseteamos insets
        panel.add(menuTitle, gbc);
    
        // --- INICIO DE LA MODIFICACIÓN ---
        // Subtítulo debajo de "SiriGApp"
        JLabel menuSubtitle = new JLabel("<html><div style='text-align: center;'>Sistema de Identificación y<br>Registro Individual Ganadero</div></html>");
        menuSubtitle.setFont(controlador.estilos.FONT_INPUT.deriveFont(Font.BOLD, 14f)); // Puedes ajustar la fuente como desees
        menuSubtitle.setForeground(Color.WHITE);
        menuSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
    
        gbc.gridy = 2; // Posición justo debajo del título
        gbc.weighty = 0.1;
        gbc.insets = new Insets(5, 10, 10, 10); // Margen (arriba, izquierda, abajo, derecha)
        panel.add(menuSubtitle, gbc);
        // --- FIN DE LA MODIFICACIÓN ---
        
    
        // Panel para los botones de navegación
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(COLOR_FONDO_MENU);
        buttonsPanel.setLayout(new GridLayout(0, 1, 0, 2)); // 4 filas, 1 columna, 2px de espacio vertical
    
        JButton btnRegistroAnimales = createMenuButton("Registrar Animales", COLOR_BOTON_NORMAL, new ImageIcon("src/resources/images/icon-registro.png"));
        btnRegistroAnimales.addActionListener(e -> {
            panelRegistroAnimales pRegistro = new panelRegistroAnimales(controlador);
            cambiarPanelContenido(pRegistro.createContentPanel());
        });
        
        JButton btnMostrarAnimales = createMenuButton("Mostrar animales", COLOR_BOTON_NORMAL, new ImageIcon("src/resources/images/icon-toro.png"));
        btnMostrarAnimales.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Aquí iría la lógica para mostrar los animales
                cambiarPanelContenido(pMostrar.createContentPanel(controlador, null));
                System.out.println("Mostrar animales");
            }
        });
    
        JButton btnRegistroSanitario = createMenuButton("Registro Sanitario", COLOR_BOTON_NORMAL, new ImageIcon("src/resources/images/icon-salud.png"));
        btnRegistroSanitario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Aquí iría la lógica para mostrar el registro sanitario
                cambiarPanelContenido(pSalud.createContentPanel());
                System.out.println("Registro Sanitario");
            }
        });
        JButton btnProduccionLeche = createMenuButton("Produccion Leche", COLOR_BOTON_NORMAL,new ImageIcon("src/resources/images/icon-leche.png"));
        btnProduccionLeche.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Aquí iría la lógica para mostrar la producción de leche
                cambiarPanelContenido(pProduccionLeche.createContentPanel());
                System.out.println("Produccion Leche");
            }
        });
        JButton btnSalidasAnimales = createMenuButton("Salidas Animales", COLOR_BOTON_NORMAL, new ImageIcon("src/resources/images/icon-salir.png"));
        btnSalidasAnimales.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Aquí iría la lógica para mostrar las salidas de animales
                cambiarPanelContenido(pSalida.createContentPanel());
                System.out.println("Salidas Animales");
            }
        });
    
        JButton btnGeneracionInformes = createMenuButton("Generación de Informes", COLOR_BOTON_NORMAL, new ImageIcon("src/resources/images/icon-informe.png"));
        btnGeneracionInformes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Aquí iría la lógica para mostrar la generación de informes
                cambiarPanelContenido(pInformes.createContentPanel());
                System.out.println("Generación de Informes");
            }
        });
        JButton btnLotes = createMenuButton("Administración de Lotes", COLOR_BOTON_NORMAL, new ImageIcon("src/resources/images/icon-lotes.png"));
        btnLotes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Aquí iría la lógica para mostrar los lotes
                cambiarPanelContenido(pLotes);
                System.out.println("Administración de Lotes");
            }
        });
    
        
        buttonsPanel.add(btnRegistroAnimales);
        buttonsPanel.add(btnMostrarAnimales);   
        buttonsPanel.add(btnRegistroSanitario);
        buttonsPanel.add(btnProduccionLeche);
        buttonsPanel.add(btnSalidasAnimales);
        buttonsPanel.add(btnGeneracionInformes);
        buttonsPanel.add(btnLotes);
        
        
        // Se ajusta la posición de los botones a gridy = 3
        gbc.gridy = 3;
        gbc.weighty = 0.5; // El panel de botones ocupa la mayor parte del espacio
        gbc.insets = new Insets(30, 0, 0, 0); // Margen superior
        panel.add(buttonsPanel, gbc);
        
        // Panel para el botón de cerrar sesión (para empujarlo hacia abajo)
        JPanel logoutPanel = new JPanel(new BorderLayout());
        logoutPanel.setOpaque(false);
        JButton btnCerrarSesion = createMenuButton("Cerrar Sesión", COLOR_BOTON_CERRAR_SESION,  new ImageIcon("src/resources/images/icon-cerrar-sesion.png"));
    
        logoutPanel.add(btnCerrarSesion, BorderLayout.SOUTH); // Coloca el botón en la parte inferior del panel
    
        btnCerrarSesion.addActionListener(e ->{
            this.dispose();
            controlador.loginFrame = new SiriGAppLogin(controlador);
            controlador.loginFrame.setVisible(true);
    
        });
        
        // Se ajusta la posición del botón de logout a gridy = 4
        gbc.gridy = 4;
        gbc.weighty = 0.2; // Espacio que empuja el botón hacia abajo
        gbc.insets = new Insets(0, 0, 20, 0); // Margen inferior
        panel.add(logoutPanel, gbc);
    
        return panel;
    }
    private JButton createMenuButton(String text, Color backgroundColor, ImageIcon icon) {
        JButton button = new JButton(text);
        if (text.equals("Cerrar Sesión")) {
            button.setForeground(Color.WHITE);
        } else {
           //button.setForeground(COLOR_TEXTO_BOTON);
           button.setForeground(Color.WHITE);
           button.setHorizontalAlignment(SwingConstants.LEFT);
        }
        button.setFont(FONT_BOTON_MENU.deriveFont(Font.BOLD, 14f));
        button.setBackground(backgroundColor);
        button.setIcon(icon);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300,50)); // Ancho y alto del botón

        // Efecto hover (cambio de color al pasar el ratón)
        Color hoverColor = backgroundColor.darker();
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    // --- MÉTODO PARA CAMBIAR EL CONTENIDO ---
    public void cambiarPanelContenido(JPanel nuevoPanel) {
        contentPanel.removeAll(); // Limpiamos el panel de contenido
        contentPanel.add(nuevoPanel, BorderLayout.CENTER); // Añadimos el nuevo panel
        contentPanel.revalidate(); // Revalidamos la UI
        contentPanel.repaint(); // Redibujamos la UI
    }

    
}
