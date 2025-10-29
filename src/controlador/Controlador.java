package controlador;

import java.sql.*;

import javax.swing.JOptionPane;

import vista.LoginFrame;
import vista.SiriGAppLogin;

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
            } else {
                JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos");
            }
        } catch (Exception e) {
            System.out.println("Error al iniciar sesión: " + e.getMessage());
        }


    }



}
