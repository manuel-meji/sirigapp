package vista;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

/**
 * Clase que crea una ventana de Login visualmente atractiva y responsive
 * utilizando Java Swing con GridBagLayout y el Look and Feel Nimbus.
 */
public class LoginFrame extends JFrame {

    private JTextField campoUsuario;
    private JPasswordField campoContrasena;
    private JButton botonLogin;

    public LoginFrame() {
        // 1. Configuración del Look and Feel (L&F)
        // Intentar usar Nimbus para un diseño más moderno
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Si Nimbus no está disponible, usar el L&F del sistema
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // En caso de fallo total, no hacer nada (usará el L&F por defecto)
            }
        }

        setTitle("Iniciar Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(600, 500)); // Tamaño mínimo
        
        // 2. Panel principal usando BorderLayout para centrar el formulario
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        
        // 3. Panel del formulario usando GridBagLayout (para ser responsive)
        JPanel panelFormulario = crearPanelFormulario();
        
        // Añadir el panel del formulario al centro del panel principal
        // y darle un padding (borde vacío)
        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);
        
        // Establecer el contenido de la ventana
        setContentPane(panelPrincipal);
        
        // Centrar la ventana en la pantalla
        setLocationRelativeTo(null);
        
        // Ajustar el tamaño a los componentes y mostrar
        pack();
        setVisible(true);
    }

    /**
     * Crea y configura el panel del formulario con GridBagLayout.
     * @return JPanel configurado.
     */
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        
        // Añadir un borde vacío para dar espacio (padding interno)
        panel.setBorder(new EmptyBorder(40, 40, 40, 40)); 

        GridBagConstraints gbc = new GridBagConstraints();
        
        // Configuración para que los campos de texto se estiren horizontalmente (responsive)
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Un poco de padding entre componentes
        gbc.insets = new Insets(8, 8, 8, 8); 

        // --- 1. Título ---
        JLabel tituloLabel = new JLabel("ACCESO AL SISTEMA", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 24));
        // Ocupa 2 columnas
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.gridwidth = 2; 
        gbc.weightx = 1.0; // Asegura que el título también use el espacio
        panel.add(tituloLabel, gbc);

        // Separador visual
        gbc.gridy = 1;
        panel.add(new JSeparator(), gbc);
        
        // --- 2. Etiqueta de Usuario ---
        JLabel usuarioLabel = new JLabel("Usuario:");
        usuarioLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1; // Vuelve a 1 columna
        gbc.weightx = 0.0; // No se estira
        gbc.anchor = GridBagConstraints.WEST; // Alineación a la izquierda
        panel.add(usuarioLabel, gbc);

        // --- 3. Campo de Usuario ---
        campoUsuario = new JTextField(15);
        campoUsuario.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.weightx = 1.0; // Se estira
        panel.add(campoUsuario, gbc);

        // --- 4. Etiqueta de Contraseña ---
        JLabel contrasenaLabel = new JLabel("Contraseña:");
        contrasenaLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0; // No se estira
        panel.add(contrasenaLabel, gbc);

        // --- 5. Campo de Contraseña ---
        campoContrasena = new JPasswordField(15);
        campoContrasena.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.weightx = 1.0; // Se estira
        panel.add(campoContrasena, gbc);
        
        // --- 6. Botón de Login ---
        botonLogin = new JButton("Iniciar Sesión");
        botonLogin.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Estilización simple del botón (solo Nimbus o Metal lo respetan bien)
        botonLogin.setBackground(new Color(52, 152, 219)); // Azul atractivo
        botonLogin.setForeground(Color.WHITE); 
        botonLogin.setFocusPainted(false); // Quita el borde de enfoque feo
        
        // Configuración para el botón: ocupa 2 columnas, abajo
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; 
        gbc.weightx = 1.0;
        gbc.insets = new Insets(20, 8, 8, 8); // Más espacio encima del botón
        panel.add(botonLogin, gbc);

        // --- Añadir acción al botón ---
        botonLogin.addActionListener(e -> {
            String usuario = campoUsuario.getText();
            String contrasena = new String(campoContrasena.getPassword());
            
            // Simulación de verificación de credenciales
            if ("admin".equals(usuario) && "1234".equals(contrasena)) {
                JOptionPane.showMessageDialog(this, "Login Exitoso!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Aquí podrías cerrar la ventana de login y abrir la principal
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o Contraseña incorrectos.", "Error de Login", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    public static void main(String[] args) {
        // Ejecutar la interfaz de usuario en el Event Dispatch Thread (recomendado por Swing)
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}