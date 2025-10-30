package controlador;

import java.sql.*;

import javax.swing.JOptionPane;


import vista.SiriGAppLogin;
import vista.animales.AnimalesFrame;

public class Controlador {
    public Connection connection = null;
    public Statement statement = null;
    public ResultSet resultSet = null; 

    public SiriGAppLogin loginFrame;

    
    public int idUsuario;

    public Controlador (){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sirigapp?verifyServerCertificate=false&useSSL=true", "root", "Manuel2004");
            statement = connection.createStatement();
            connection.setAutoCommit(true);
            JOptionPane.showMessageDialog(null, "Conexión exitosa a la base de datos");
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error de conexión a la base de datos: " + e.getMessage());

        }

       
        loginFrame = new SiriGAppLogin(this);
        loginFrame.setVisible(true);
        loginFrame.setLocationRelativeTo(null);


    }

    public void IniciarSesion  (String usuario, String contraseña){
        String sql = "SELECT * FROM usuarios WHERE id = ? AND contrasena = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usuario);
            preparedStatement.setString(2, contraseña);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                JOptionPane.showMessageDialog(null, "Inicio de sesión exitoso");
                loginFrame.setVisible(false);
                AnimalesFrame animalesFrame = new AnimalesFrame(this);
                animalesFrame.setVisible(true);
                animalesFrame.setLocationRelativeTo(null);
            } else {
                JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos");
            }
        } catch (Exception e) {
            System.out.println("Error al iniciar sesión: " + e.getMessage());
        }


    }


    public void guardarAnimal(
        String codigo,
        java.sql.Timestamp fechaNacimiento,
        String sexo,
        String raza,
        String pesoNacimiento,
        String peso,
        String idMadre,
        String idPadre,
        String estado
    ) throws SQLException {
        String sql = "INSERT INTO animal (codigo, fecha_nacimiento, sexo, raza, peso_nacimiento, peso, id_madre, id_padre, estado) VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, codigo);
        ps.setTimestamp(2, fechaNacimiento);
        ps.setString(3, sexo);
        ps.setString(4, raza);
        ps.setString(5, pesoNacimiento);
        ps.setString(6, peso);
        ps.setString(7, idMadre);
        ps.setString(8, idPadre);
        ps.setString(9, estado);
        ps.executeUpdate();
    }

}
