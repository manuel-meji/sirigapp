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
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sirigapp?verifyServerCertificate=false&useSSL=true", "root", "1234");
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

    /**
     * Obtiene todos los animales de la base de datos.
     * @return Lista de Object[] con los datos de cada animal.
     */
    public java.util.List<Object[]> obtenerAnimales() {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "SELECT codigo, fecha_nacimiento, sexo, raza, peso_nacimiento, peso, id_madre, id_padre, estado FROM animal";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] fila = new Object[9];
                fila[0] = rs.getString("codigo");
                fila[1] = rs.getTimestamp("fecha_nacimiento");
                fila[2] = rs.getString("sexo");
                fila[3] = rs.getString("raza");
                fila[4] = rs.getString("peso_nacimiento");
                fila[5] = rs.getString("peso");
                fila[6] = rs.getString("id_madre");
                fila[7] = rs.getString("id_padre");
                fila[8] = rs.getString("estado");
                lista.add(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener animales: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Elimina un animal por su código.
     */
    public void eliminarAnimal(Object codigo) {
        String sql = "DELETE FROM animal WHERE codigo = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, codigo.toString());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Animal eliminado correctamente.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar animal: " + e.getMessage());
        }
    }

    /**
     * Edita los datos de un animal. Aquí solo muestra un mensaje, pero puedes abrir un panel de edición.
     */
    public void editarAnimal(Object codigo) {
        // Aquí deberías abrir un panel de edición o retornar los datos del animal para editar.
        JOptionPane.showMessageDialog(null, "Funcionalidad de edición para el animal con código: " + codigo);
    }

    /**
     * Busca animales que coincidan con el filtro proporcionado
     */
    public java.util.List<String> buscarAnimales(String filtro) {
        java.util.List<String> resultado = new java.util.ArrayList<>();
        String sql = "SELECT codigo FROM animal WHERE codigo LIKE ? AND estado = 'ACTIVO'";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "%" + filtro + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultado.add(rs.getString("codigo"));
            }
        } catch (Exception e) {
            System.out.println("Error al buscar animales: " + e.getMessage());
        }
        return resultado;
    }

    /**
     * Guarda un nuevo registro de salida de animal
     */
    public void guardarSalida(String animal, String motivo, java.sql.Date fecha) throws SQLException {
        // Primero actualizamos el estado del animal
        String updateAnimal = "UPDATE animal SET estado = ? WHERE codigo = ?";
        PreparedStatement psAnimal = connection.prepareStatement(updateAnimal);
        psAnimal.setString(1, motivo.equals("MUERTE") ? "MUERTO" : "VENDIDO");
        psAnimal.setString(2, animal);
        psAnimal.executeUpdate();

        // Luego registramos la salida
        String sql = "INSERT INTO salida (fecha, motivo, id_animal) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setDate(1, fecha);
        ps.setString(2, motivo);
        ps.setString(3, animal);
        ps.executeUpdate();
    }

    /**
     * Obtiene todas las salidas registradas
     */
    public java.util.List<Object[]> obtenerSalidas() {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "SELECT s.id, s.id_animal, s.motivo, s.fecha FROM salida s ORDER BY s.fecha DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] fila = new Object[4];
                fila[0] = rs.getInt("id");
                fila[1] = rs.getString("id_animal");
                fila[2] = rs.getString("motivo");
                fila[3] = rs.getDate("fecha");
                lista.add(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener salidas: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Elimina un registro de salida
     */
    public void eliminarSalida(int id) {
        try {
            // Primero obtenemos el id_animal y motivo
            String sqlSelect = "SELECT id_animal, motivo FROM salida WHERE id = ?";
            PreparedStatement psSelect = connection.prepareStatement(sqlSelect);
            psSelect.setInt(1, id);
            ResultSet rs = psSelect.executeQuery();
            
            if (rs.next()) {
                String idAnimal = rs.getString("id_animal");
                
                // Reactivamos el animal
                String updateAnimal = "UPDATE animal SET estado = 'ACTIVO' WHERE codigo = ?";
                PreparedStatement psAnimal = connection.prepareStatement(updateAnimal);
                psAnimal.setString(1, idAnimal);
                psAnimal.executeUpdate();
                
                // Eliminamos la salida
                String sqlDelete = "DELETE FROM salida WHERE id = ?";
                PreparedStatement psDelete = connection.prepareStatement(sqlDelete);
                psDelete.setInt(1, id);
                psDelete.executeUpdate();
                
                JOptionPane.showMessageDialog(null, "Salida eliminada correctamente");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar salida: " + e.getMessage());
        }
    }

    /**
     * Edita un registro de salida
     */
    public void editarSalida(int id) {
        try {
            String sql = "SELECT id_animal, motivo, fecha FROM salida WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String idAnimal = rs.getString("id_animal");
                String motivo = rs.getString("motivo");
                java.sql.Date fecha = rs.getDate("fecha");
                
                // Aquí podrías abrir un diálogo de edición con estos datos
                JOptionPane.showMessageDialog(null, 
                    "Salida ID: " + id + "\n" +
                    "Animal: " + idAnimal + "\n" +
                    "Motivo: " + motivo + "\n" +
                    "Fecha: " + fecha
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al editar salida: " + e.getMessage());
        }
    }

    public void registrarNuevoLote(String nombre, String etapa, String descripcion) {
        
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO lotes (nombre, etapa, descripcion) VALUES (?, ?, ?)";
            ps = connection.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, etapa);
            ps.setString(3, descripcion);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Lote registrado exitosamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar lote: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar PreparedStatement: " + e.getMessage());
            }
        }
    }

    public ResultSet obtenerTodosLosLotes() {
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM lotes";
            PreparedStatement ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener lotes: " + e.getMessage());
        }
        return rs;
    }

    /* ------------------ Productos ------------------ */

    public void guardarProducto(String nombre, String tipo) throws SQLException {
        String sql = "INSERT INTO productos (producto, tipo) VALUES (?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, nombre);
        ps.setString(2, tipo);
        ps.executeUpdate();
    }

    public void editarProducto(int id, String nombre, String tipo) throws SQLException {
        String sql = "UPDATE productos SET producto = ?, tipo = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, nombre);
        ps.setString(2, tipo);
        ps.setInt(3, id);
        ps.executeUpdate();
    }

    public void eliminarProducto(int id) throws SQLException {
        String sql = "DELETE FROM productos WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public java.util.List<Object[]> obtenerProductos() {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "SELECT id, producto, tipo FROM productos ORDER BY id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] fila = new Object[3];
                fila[0] = rs.getInt("id");
                fila[1] = rs.getString("producto");
                fila[2] = rs.getString("tipo");
                lista.add(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener productos: " + e.getMessage());
        }
        return lista;
    }

    public java.util.List<Object[]> obtenerProductosPorTipo(String tipo) {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "SELECT id, producto, tipo FROM productos WHERE tipo = ? ORDER BY producto";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, tipo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] fila = new Object[3];
                fila[0] = rs.getInt("id");
                fila[1] = rs.getString("producto");
                fila[2] = rs.getString("tipo");
                lista.add(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener productos por tipo: " + e.getMessage());
        }
        return lista;
    }

    /* ------------------ Eventos sanitarios ------------------ */

    public void guardarEventoSanitario(java.sql.Timestamp fecha, Integer idProducto, Float dosis, String motivo, String diagnostico, String idAnimal, String tipo) throws SQLException {
        String sql = "INSERT INTO eventos_sanitarios (fecha, id_producto, dosis, motivo, diagnostico, id_animal, tipo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setTimestamp(1, fecha);
        if (idProducto != null) ps.setInt(2, idProducto); else ps.setNull(2, java.sql.Types.INTEGER);
        if (dosis != null) ps.setFloat(3, dosis); else ps.setNull(3, java.sql.Types.FLOAT);
        ps.setString(4, motivo);
        ps.setString(5, diagnostico);
        ps.setString(6, idAnimal);
        ps.setString(7, tipo);
        ps.executeUpdate();
    }

    public java.util.List<Object[]> obtenerEventosSanitarios() {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "SELECT id, fecha, tipo, id_animal, id_producto, dosis, motivo, diagnostico FROM eventos_sanitarios ORDER BY fecha DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] fila = new Object[8];
                fila[0] = rs.getInt("id");
                fila[1] = rs.getTimestamp("fecha");
                fila[2] = rs.getString("tipo");
                fila[3] = rs.getString("id_animal");
                fila[4] = rs.getObject("id_producto");
                fila[5] = rs.getObject("dosis");
                fila[6] = rs.getString("motivo");
                fila[7] = rs.getString("diagnostico");
                lista.add(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener eventos sanitarios: " + e.getMessage());
        }
        return lista;
    }

    public java.util.List<Object[]> obtenerEventosSanitariosPorTipo(String tipo) {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "SELECT id, fecha, tipo, id_animal, id_producto, dosis, motivo, diagnostico FROM eventos_sanitarios WHERE tipo = ? ORDER BY fecha DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, tipo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] fila = new Object[7];
                fila[0] = rs.getInt("id");
                fila[1] = rs.getTimestamp("fecha");
                fila[2] = rs.getString("id_animal");
                fila[3] = rs.getObject("id_producto");
                fila[4] = rs.getObject("dosis");
                fila[5] = rs.getString("motivo");
                fila[6] = rs.getString("diagnostico");
                lista.add(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener eventos por tipo: " + e.getMessage());
        }
        return lista;
    }

    public void eliminarEvento(int id) {
        try {
            String sql = "DELETE FROM eventos_sanitarios WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Evento eliminado correctamente");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar evento: " + e.getMessage());
        }
    }

    public void editarEvento(int id) {
        try {
            String sql = "SELECT id, fecha, id_producto, dosis, motivo, diagnostico, id_animal, tipo FROM eventos_sanitarios WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("Fecha: ").append(rs.getTimestamp("fecha")).append("\n");
                sb.append("Animal: ").append(rs.getString("id_animal")).append("\n");
                sb.append("Tipo: ").append(rs.getString("tipo")).append("\n");
                sb.append("Producto: ").append(rs.getObject("id_producto")).append("\n");
                sb.append("Dosis: ").append(rs.getObject("dosis")).append("\n");
                sb.append("Motivo: ").append(rs.getString("motivo")).append("\n");
                sb.append("Diagnóstico: ").append(rs.getString("diagnostico")).append("\n");
                JOptionPane.showMessageDialog(null, sb.toString());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener evento: " + e.getMessage());
        }
    }

}

