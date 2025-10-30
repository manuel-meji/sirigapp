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

    public void modificarLote(int idLote, String nombre, String etapa, String descripcion) {
        PreparedStatement ps = null;
        try {
            String sql = "UPDATE lotes SET nombre = ?, etapa = ?, descripcion = ? WHERE id = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, etapa);
            ps.setString(3, descripcion);
            ps.setInt(4, idLote);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Lote modificado exitosamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar lote: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar PreparedStatement: " + e.getMessage());
            }
        }
    }

/**
     * Obtiene solo los codigos de todos los animales para llenar el JComboBox.
     * He cambiado el nombre del método para que sea más claro.
     */
    public ResultSet obtenerCodigosAnimales() {
        ResultSet rs = null;
        try {
            String sql = "SELECT codigo FROM animal ORDER BY codigo";
            PreparedStatement ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener códigos de animales: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
        return rs;
    }

    /**
     * Obtiene el historial de movimientos simple, sin JOINs.
     */
    public ResultSet obtenerHistorialMovimientos() {
        ResultSet rs = null;
        try {
            String sql = "SELECT id, id_animal, id_lote_anterior, id_lote_posterior, fecha FROM historial_lote ORDER BY fecha DESC";
            PreparedStatement ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el historial de movimientos: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
        return rs;
    }

    /**
     * Realiza la lógica de negocio para registrar un nuevo movimiento de animal.
     * Esto incluye insertar en el historial y actualizar la tabla de animales.
     * Se usa una transacción para garantizar la integridad de los datos.
     */
/**
     * Realiza la lógica de negocio para registrar un nuevo movimiento de animal.
     * Esto incluye insertar en el historial y actualizar la tabla de animales.
     * Se usa una transacción para garantizar la integridad de los datos.
     */
    public void registrarMovimientoAnimal(String codigoAnimal, int idLoteDestino, java.sql.Date fecha) {
        Integer idLoteAnterior = null; 
        
        try {
            // PASO 1: Iniciar la transacción
            connection.setAutoCommit(false);

            // PASO 2: Obtener el lote actual del animal (será nuestro lote anterior)
            String sqlGetLote = "SELECT id_lote_actual FROM animal WHERE codigo = ?";
            try (PreparedStatement psGetLote = connection.prepareStatement(sqlGetLote)) {
                psGetLote.setString(1, codigoAnimal);
                try (ResultSet rs = psGetLote.executeQuery()) {
                    if (rs.next()) {
                        idLoteAnterior = (Integer) rs.getObject("id_lote_actual");
                    } else {
                        throw new SQLException("El animal con código " + codigoAnimal + " no fue encontrado.");
                    }
                }
            }

            // Validación: No mover un animal al lote en el que ya está.
            if (idLoteAnterior != null && idLoteAnterior == idLoteDestino) {
                JOptionPane.showMessageDialog(null, "El animal ya se encuentra en el lote de destino.", "Movimiento Inválido", JOptionPane.WARNING_MESSAGE);
                connection.rollback(); // Cancelamos la transacción
                return;
            }

            // PASO 3: Insertar el nuevo registro en la tabla de historial
            String sqlInsertHistorial = "INSERT INTO historial_lote (id_animal, id_lote_anterior, id_lote_posterior, fecha) VALUES (?, ?, ?, ?)";
            try (PreparedStatement psInsert = connection.prepareStatement(sqlInsertHistorial)) {
                psInsert.setString(1, codigoAnimal);
                if (idLoteAnterior != null) {
                    psInsert.setInt(2, idLoteAnterior);
                } else {
                    psInsert.setNull(2, java.sql.Types.INTEGER);
                }
                psInsert.setInt(3, idLoteDestino);
                psInsert.setDate(4, fecha); // Se usa setDate para guardar solo la fecha
                psInsert.executeUpdate();
            }

            // PASO 4: Actualizar la ubicación actual del animal en la tabla 'animal'
            String sqlUpdateAnimal = "UPDATE animal SET id_lote_actual = ? WHERE codigo = ?";
            try (PreparedStatement psUpdate = connection.prepareStatement(sqlUpdateAnimal)) {
                psUpdate.setInt(1, idLoteDestino);
                psUpdate.setString(2, codigoAnimal);
                psUpdate.executeUpdate();
            }

            // PASO 5: Si todo fue exitoso, confirmar la transacción
            connection.commit();
            JOptionPane.showMessageDialog(null, "Movimiento registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            // Si ocurre cualquier error, revertimos todos los cambios
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Error al hacer rollback: " + ex.getMessage());
            }
            JOptionPane.showMessageDialog(null, "Error al registrar el movimiento: " + e.getMessage(), "Error de Transacción", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Siempre restauramos el modo auto-commit
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Error al restaurar auto-commit: " + ex.getMessage());
            }
        }
    }

    /**
     * Modifica un registro existente en el historial.
     * ADVERTENCIA: Esta función modifica datos históricos y no ajusta la ubicación actual
     * del animal. Debe usarse para corregir errores de digitación.
     */
/**
     * Modifica un registro existente en el historial.
     * ADVERTENCIA: Esta función modifica datos históricos y no ajusta la ubicación actual
     * del animal. Debe usarse para corregir errores de digitación.
     */
    public void modificarRegistroHistorial(int idMovimiento, String nuevoCodigoAnimal, int nuevoIdLoteDestino, java.sql.Date nuevaFecha) {
        String sql = "UPDATE historial_lote SET id_animal = ?, id_lote_posterior = ?, fecha = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nuevoCodigoAnimal);
            ps.setInt(2, nuevoIdLoteDestino);
            ps.setDate(3, nuevaFecha); // Se usa setDate
            ps.setInt(4, idMovimiento);
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Registro de historial modificado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el registro de historial a modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar el historial: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Busca códigos de animales que coincidan con un texto de búsqueda.
     * @param busqueda El texto que el usuario está escribiendo.
     * @return Un ResultSet con los códigos que empiezan con el texto de búsqueda.
     */
    public ResultSet buscarCodigosAnimales(String busqueda) {
        ResultSet rs = null;
        // Usamos LIKE con el comodín '%' para buscar códigos que COMIENCEN con el texto.
        String sql = "SELECT codigo FROM animal WHERE codigo LIKE ? ORDER BY codigo";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, busqueda + "%"); // Añade el comodín aquí
            rs = ps.executeQuery();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar animales: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
        return rs;
    }

}

