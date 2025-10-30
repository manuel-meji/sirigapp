package vista.animales;

import controlador.Controlador;
import vista.lotes.VistaLotes;
import vista.ui.DesignSystem;

import javax.swing.*;
 
 

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AnimalesFrame extends JFrame {

    //Paneles de contenido
    panelRegistroAnimales pRegistro;
    panelMostrarAnimales pMostrar;
    VistaLotes pLotes;
    

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
        setTitle("SiriGApp - Menú Principal");
        setSize(1366, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        //Inicialización de los paneles
        pMostrar = new panelMostrarAnimales();
        pLotes = new VistaLotes(controlador);

        // 1. Panel del Menú Lateral
        JPanel menuPanel = createMenuPanel();
        add(menuPanel, BorderLayout.WEST);

        // 2. Panel de Contenido Principal (el área a la derecha del menú)
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);
        
        pRegistro = new panelRegistroAnimales(controlador);
        cambiarPanelContenido(pRegistro.createContentPanel());

        //  contentPanel.add(contentPanel, BorderLayout.CENTER);
        //  contentPanel.revalidate();
        //  contentPanel.repaint();


        //createContentPanel();

    }

    // private void createContentPanel() {
    //     contentPanel.removeAll();
    //     contentPanel.setLayout(new BorderLayout());
    //     contentPanel.setBackground(DesignSystem.COLOR_FONDO_APP);

    //     JLabel title = new JLabel("Registro de Animales");
    //     title.setHorizontalAlignment(SwingConstants.LEFT);
    //     title.setBorder(new EmptyBorder(24, 32, 8, 32));
    //     title.setFont(FONT_BOTON_MENU.deriveFont(Font.BOLD, 26f));
    //     contentPanel.add(title, BorderLayout.NORTH);

    //     JPanel wrapper = new JPanel(null);
    //     wrapper.setOpaque(false);
    //     contentPanel.add(wrapper, BorderLayout.CENTER);

    //     JPanel card = new JPanel(null);
    //     card.setBackground(Color.WHITE);
    //     card.setBorder(BorderFactory.createCompoundBorder(
    //         BorderFactory.createLineBorder(DesignSystem.COLOR_CARD_BORDER, 1),
    //         new EmptyBorder(20, 24, 24, 24)
    //     ));
    //     wrapper.add(card);

    //     wrapper.addComponentListener(new java.awt.event.ComponentAdapter() {
    //         public void componentResized(java.awt.event.ComponentEvent e) {
    //             int w = wrapper.getWidth();
    //             int x = Math.max(32, (w - 980) / 2);
    //             card.setBounds(x, 20, 980, 500);
    //         }
    //     });

    //     Font labelFont = FONT_BOTON_MENU.deriveFont(Font.PLAIN, 16f);
    //     int col1X = 30;  int col1W = 380;
    //     int col2X = 500; int col2W = 380;
    //     int labelW = 200; int fieldH = 34; int rowY = 20; int rowGap = 46;

    //     JLabel lCodigo = new JLabel("Código:"); lCodigo.setFont(labelFont);
    //     lCodigo.setBounds(col1X, rowY, labelW, 24); card.add(lCodigo);
    //     txtCodigo = new JTextField(); txtCodigo.setBounds(col1X + labelW, rowY, col1W - labelW, fieldH); card.add(txtCodigo);

    //     JLabel lFecha = new JLabel("Fecha de nacimiento:"); lFecha.setFont(labelFont);
    //     lFecha.setBounds(col2X, rowY, labelW, 24); card.add(lFecha);
    //     dcFechaNacimiento = new JDateChooser(); dcFechaNacimiento.setDateFormatString("yyyy-MM-dd");
    //     dcFechaNacimiento.setBounds(col2X + labelW, rowY, col2W - labelW, fieldH); card.add(dcFechaNacimiento);

    //     rowY += rowGap;
    //     JLabel lSexo = new JLabel("Sexo:"); lSexo.setFont(labelFont);
    //     lSexo.setBounds(col1X, rowY, labelW, 24); card.add(lSexo);
    //     cbSexo = new JComboBox<>(new String[] {"M", "F"}); cbSexo.setBounds(col1X + labelW, rowY, col1W - labelW, fieldH); card.add(cbSexo);

    //     JLabel lRaza = new JLabel("Raza:"); lRaza.setFont(labelFont);
    //     lRaza.setBounds(col2X, rowY, labelW, 24); card.add(lRaza);
    //     txtRaza = new JTextField(); txtRaza.setBounds(col2X + labelW, rowY, col2W - labelW, fieldH); card.add(txtRaza);

    //     rowY += rowGap;
    //     JLabel lPesoNac = new JLabel("Peso nacimiento (kg):"); lPesoNac.setFont(labelFont);
    //     lPesoNac.setBounds(col1X, rowY, labelW, 24); card.add(lPesoNac);
    //     txtPesoNacimiento = new JTextField(); txtPesoNacimiento.setBounds(col1X + labelW, rowY, col1W - labelW, fieldH); card.add(txtPesoNacimiento);

    //     JLabel lPeso = new JLabel("Peso actual (kg):"); lPeso.setFont(labelFont);
    //     lPeso.setBounds(col2X, rowY, labelW, 24); card.add(lPeso);
    //     txtPeso = new JTextField(); txtPeso.setBounds(col2X + labelW, rowY, col2W - labelW, fieldH); card.add(txtPeso);

    //     rowY += rowGap;
    //     JLabel lMadre = new JLabel("ID madre:"); lMadre.setFont(labelFont);
    //     lMadre.setBounds(col1X, rowY, labelW, 24); card.add(lMadre);
    //     txtIdMadre = new JTextField(); txtIdMadre.setBounds(col1X + labelW, rowY, col1W - labelW, fieldH); card.add(txtIdMadre);

    //     JLabel lPadre = new JLabel("ID padre:"); lPadre.setFont(labelFont);
    //     lPadre.setBounds(col2X, rowY, labelW, 24); card.add(lPadre);
    //     txtIdPadre = new JTextField(); txtIdPadre.setBounds(col2X + labelW, rowY, col2W - labelW, fieldH); card.add(txtIdPadre);

    //     rowY += rowGap;
    //     JLabel lEstado = new JLabel("Estado:"); lEstado.setFont(labelFont);
    //     lEstado.setBounds(col1X, rowY, labelW, 24); card.add(lEstado);
    //     cbEstado = new JComboBox<>(new String[] {"ACTIVO", "VENDIDO", "MUERTO"});
    //     cbEstado.setBounds(col1X + labelW, rowY, col1W - labelW, fieldH); card.add(cbEstado);

    //     JButton btnGuardar = new JButton("Guardar");
    //     JButton btnLimpiar = new JButton("Limpiar");
    //     int btnW = 120; int btnH = 36; int btnGap = 12;
    //     final int baseY = rowY + rowGap + 10;
    //     card.add(btnLimpiar); card.add(btnGuardar);

    //     card.addComponentListener(new java.awt.event.ComponentAdapter() {
    //         public void componentResized(java.awt.event.ComponentEvent e) {
    //             int cw = card.getWidth();
    //             btnGuardar.setBounds(cw - 24 - btnW, baseY, btnW, btnH);
    //             btnLimpiar.setBounds(cw - 24 - btnW - btnGap - btnW, baseY, btnW, btnH);
    //         }
    //     });

    //     // btnGuardar.addActionListener(e -> guardarAnimal());
    //     // btnLimpiar.addActionListener(e -> limpiarFormulario());

    //     contentPanel.revalidate();
    //     contentPanel.repaint();
    // }

    

    

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

        // Título "Menú"
        JLabel menuTitle = new JLabel("Menú");
        menuTitle.setFont(FONT_TITULO_MENU);
        menuTitle.setForeground(Color.WHITE);
        menuTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(menuTitle, gbc);

        // Panel para los botones de navegación
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(COLOR_FONDO_MENU);
        buttonsPanel.setLayout(new GridLayout(0, 1, 0, 2)); // 4 filas, 1 columna, 2px de espacio vertical

        JButton btnRegistroAnimales = createMenuButton("Registrar Animales", COLOR_BOTON_NORMAL);
        btnRegistroAnimales.addActionListener(e -> {
            panelRegistroAnimales pRegistro = new panelRegistroAnimales(controlador);
            cambiarPanelContenido(pRegistro.createContentPanel());
        });
        
        JButton btnMostrarAnimales = createMenuButton("Mostrar animales", COLOR_BOTON_NORMAL);
        btnMostrarAnimales.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Aquí iría la lógica para mostrar los animales
                cambiarPanelContenido(pMostrar.createContentPanel(controlador, null));
                System.out.println("Mostrar animales");
            }
        });

        JButton btnRegistroSanitario = createMenuButton("Registro Sanitario", COLOR_BOTON_NORMAL);
        JButton btnProduccionLeche = createMenuButton("Produccion Leche", COLOR_BOTON_NORMAL);
        JButton btnSalidasAnimales = createMenuButton("Salidas Animales", COLOR_BOTON_NORMAL);
        JButton btnGeneracionInformes = createMenuButton("Generación de Informes", COLOR_BOTON_NORMAL);
        JButton btnLotes = createMenuButton("Administración de Lotes", COLOR_BOTON_NORMAL);
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
        
        
        gbc.gridy = 2;
        gbc.weighty = 0.5; // El panel de botones ocupa la mayor parte del espacio
        gbc.insets = new Insets(30, 0, 0, 0); // Margen superior
        panel.add(buttonsPanel, gbc);
        
        // Panel para el botón de cerrar sesión (para empujarlo hacia abajo)
        JPanel logoutPanel = new JPanel(new BorderLayout());
        logoutPanel.setOpaque(false);
        JButton btnCerrarSesion = createMenuButton("Cerrar Sesión", COLOR_BOTON_CERRAR_SESION);
        logoutPanel.add(btnCerrarSesion, BorderLayout.SOUTH); // Coloca el botón en la parte inferior del panel
        
        gbc.gridy = 3;
        gbc.weighty = 0.2; // Espacio que empuja el botón hacia abajo
        gbc.insets = new Insets(0, 0, 20, 0); // Margen inferior
        panel.add(logoutPanel, gbc);

        return panel;
    }

    private JButton createMenuButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        if (text.equals("Cerrar Sesión")) {
            button.setForeground(Color.WHITE);
        } else {
           button.setForeground(COLOR_TEXTO_BOTON);
        }
        button.setFont(FONT_BOTON_MENU);
        button.setBackground(backgroundColor);
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

    // --- MÉTODO NUEVO PARA CAMBIAR EL CONTENIDO ---
    private void cambiarPanelContenido(JPanel nuevoPanel) {
        contentPanel.removeAll(); // Limpiamos el panel de contenido
        contentPanel.add(nuevoPanel, BorderLayout.CENTER); // Añadimos el nuevo panel
        contentPanel.revalidate(); // Revalidamos la UI
        contentPanel.repaint(); // Redibujamos la UI
    }

    
}
