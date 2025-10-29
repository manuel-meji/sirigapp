package controlador;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenuFrame extends JFrame {

    // --- Colores ---
    private final Color COLOR_FONDO_MENU = new Color(0, 38, 51);
    private final Color COLOR_BOTON_NORMAL = new Color(121, 255, 194);
    private final Color COLOR_TEXTO_BOTON = new Color(0, 38, 51);
    private final Color COLOR_BOTON_CERRAR_SESION = new Color(255, 102, 102);
    private final Color COLOR_CONTENIDO_PRINCIPAL = new Color(45, 45, 45);

    // --- Fuentes ---
    private final Font FONT_TITULO_MENU = FontLoader.loadFont("/resources/fonts/Montserrat-Black.ttf", 48f);
    private final Font FONT_BOTON_MENU = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 18f);

    public MainMenuFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("SiriGApp - Menú Principal");
        setSize(1366, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // 1. Panel del Menú Lateral
        JPanel menuPanel = createMenuPanel();
        add(menuPanel, BorderLayout.WEST);

        // 2. Panel de Contenido Principal (el área a la derecha del menú)
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(COLOR_CONTENIDO_PRINCIPAL);
        add(contentPanel, BorderLayout.CENTER);
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

        buttonsPanel.add(createMenuButton("Mostrar animales", COLOR_BOTON_NORMAL));
        buttonsPanel.add(createMenuButton("Registro Sanitario", COLOR_BOTON_NORMAL));
        buttonsPanel.add(createMenuButton("Produccion Leche", COLOR_BOTON_NORMAL));
        buttonsPanel.add(createMenuButton("Generación de Informes", COLOR_BOTON_NORMAL));
        buttonsPanel.add(createMenuButton("Salidas Animales", COLOR_BOTON_NORMAL));
        
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
        button.setFont(FONT_BOTON_MENU);
        button.setForeground(COLOR_TEXTO_BOTON);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 60)); // Ancho y alto del botón

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

    public static void main(String[] args) {
        // Establecer el Look and Feel de FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to initialize LaF");
        }

        EventQueue.invokeLater(() -> {
            MainMenuFrame ex = new MainMenuFrame();
            ex.setVisible(true);
        });
    }
}
