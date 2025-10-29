package vista;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URI;

public class SiriGAppLogin extends JFrame {

    // Cargamos las variantes de la fuente Montserrat que vamos a utilizar
    private final Font montserratBold = FontLoader.loadFont("/fonts/Montserrat-Bold.ttf", 28f);
    private final Font montserratRegular = FontLoader.loadFont("/fonts/Montserrat-Regular.ttf", 14f);
    private final Font montserratSemiBold = FontLoader.loadFont("/fonts/Montserrat-SemiBold.ttf", 14f);

    public SiriGAppLogin() {
        initUI();
    }

    private void initUI() {
        setTitle("SiriGApp");
        setSize(1366, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con el color de fondo oscuro
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(0, 38, 51));
        mainPanel.setLayout(new GridBagLayout());
        add(mainPanel);

        // Panel blanco redondeado para el formulario de login
        JPanel loginPanel = new RoundedPanel(50); // 50 es el radio de las esquinas
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setPreferredSize(new Dimension(600, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 50, 10, 50);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título "SiriGApp"
        JLabel titleLabel = new JLabel("SiriGApp");
        titleLabel.setFont(montserratBold);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.insets = new Insets(40, 50, 5, 50);
        loginPanel.add(titleLabel, gbc);

        // Subtítulo
        JLabel subtitleLabel = new JLabel("Introduzca su nombre de usuario y contraseña para poder utilizar el sistema");
        subtitleLabel.setFont(montserratRegular);
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.insets = new Insets(0, 50, 30, 50);
        loginPanel.add(subtitleLabel, gbc);

        // Etiqueta "Nombre de usuario:"
        JLabel userLabel = new JLabel("Nombre de usuario:");
        userLabel.setFont(montserratSemiBold);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 50, 0, 50);
        loginPanel.add(userLabel, gbc);

        // Campo de texto para el usuario
        JTextField userTextField = new JTextField(20);
        userTextField.setFont(montserratRegular);
        userTextField.setBackground(new Color(230, 230, 230));
        userTextField.setBorder(new EmptyBorder(10, 15, 10, 15));
        gbc.insets = new Insets(5, 50, 10, 50);
        loginPanel.add(userTextField, gbc);

        // Etiqueta "Contraseña:"
        JLabel passLabel = new JLabel("Contraseña:");
        passLabel.setFont(montserratSemiBold);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 50, 0, 50);
        loginPanel.add(passLabel, gbc);

        // Campo de texto para la contraseña
        JPasswordField passTextField = new JPasswordField(20);
        passTextField.setFont(montserratRegular);
        passTextField.setBackground(new Color(230, 230, 230));
        passTextField.setBorder(new EmptyBorder(10, 15, 10, 15));
        gbc.insets = new Insets(5, 50, 20, 50);
        loginPanel.add(passTextField, gbc);

        // Botón "Iniciar Sesión"
        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.setFont(montserratSemiBold);
        loginButton.setBackground(new Color(255, 20, 147)); // Color rosa
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(new EmptyBorder(15, 0, 15, 0));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Para bordes redondeados con FlatLaf
        loginButton.putClientProperty("JButton.buttonType", "roundRect");
        gbc.insets = new Insets(10, 50, 10, 50);
        loginPanel.add(loginButton, gbc);

        // Enlace "¿Ha olvidado su contraseña?"
        JLabel forgotPasswordLabel = new JLabel("¿Ha olvidado su contraseña?");
        forgotPasswordLabel.setFont(montserratRegular);
        forgotPasswordLabel.setForeground(Color.GRAY);
        forgotPasswordLabel.setHorizontalAlignment(JLabel.CENTER);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Acción al hacer clic en el enlace
                try {
                    Desktop.getDesktop().browse(new URI("https://www.ejemplo.com/recuperar-contrasena"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        gbc.insets = new Insets(10, 50, 30, 50);
        loginPanel.add(forgotPasswordLabel, gbc);


        mainPanel.add(loginPanel);
    }

    // Clase interna para crear un panel con bordes redondeados
    class RoundedPanel extends JPanel {
        private int cornerRadius;

        public RoundedPanel(int radius) {
            super();
            this.cornerRadius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Dibuja el panel redondeado
            graphics.setColor(getBackground());
            graphics.fill(new RoundRectangle2D.Float(0, 0, width - 1, height - 1, arcs.width, arcs.height));
        }
    }


    public static void main(String[] args) {
        // Establecer el Look and Feel de FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            SiriGAppLogin ex = new SiriGAppLogin();
            ex.setVisible(true);
        });
    }
}