package vista.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;

import controlador.FontLoader;

public final class DesignSystem {

    // Colores base
    public static final Color COLOR_FONDO_MENU = new Color(0, 38, 51);
    public static final Color COLOR_BOTON_NORMAL = new Color(106, 255, 193);
    public static final Color COLOR_TEXTO_BOTON = new Color(0, 38, 51);
    public static final Color COLOR_BOTON_CERRAR_SESION = new Color(247, 51, 116);
    public static final Color COLOR_FONDO_APP = new Color(245, 246, 248);
    public static final Color COLOR_CARD_BORDER = new Color(220, 223, 230);

    // Fuentes base (cargadas una sola vez)
    public static final Font FONT_TITULO_MENU = FontLoader.loadFont("/resources/fonts/Montserrat-Black.ttf", 40f);
    public static final Font FONT_SUBTITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Bold.ttf", 18f);
    public static final Font FONT_BOTON_MENU = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 18f);

    //Colores de botones específicos
    public static final Color COLOR_GUARDAR = new Color(0, 136, 255);
    public static final Color COLOR_LIMPIAR = new Color(255, 141, 40);
    public static final Color COLOR_ELIMINAR = new Color(255, 56, 60);
    public static final Color COLOR_MODIFICAR = new Color(52, 199, 89);

    public DesignSystem() {}

    public JButton crearBotonConEstilo(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(FONT_BOTON_MENU);
        button.setBackground(backgroundColor);
        return button;
    
    }

}


