
import javax.swing.UIManager;

import com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.mysql.jdbc.log.Log;

import controlador.Controlador;
import vista.LoginFrame;

public class App {
    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (Exception e) {
            System.err.println("Error al iniciar la apariencia: " + e.getMessage());
        }
       

        Controlador  controlador = new Controlador();
         
    }
}
