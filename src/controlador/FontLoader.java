package controlador;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

public class FontLoader {
     public static Font loadFont(String path, float size) {
        try {
            InputStream is = FontLoader.class.getResourceAsStream(path);
            if (is == null) {
                System.err.println("No se pudo encontrar la fuente en: " + path);
                return new Font("SansSerif", Font.PLAIN, (int) size);
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            return font.deriveFont(size);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            return new Font("SansSerif", Font.PLAIN, (int) size);
        }
    }
}
