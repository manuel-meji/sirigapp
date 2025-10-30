package controlador;

import java.sql.*;
import java.util.List;

import javax.swing.JOptionPane;
import vista.SiriGAppLogin;
import vista.animales.AnimalesFrame;

public class Controlador {

    public Connection connection = null;
    public Statement statement = null;
    public ResultSet resultSet = null;

    public AnimalesFrame animalesFrame;

    public SiriGAppLogin loginFrame;

    public int idUsuario;

    public Controlador() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/sirigapp?verifyServerCertificate=false&useSSL=true", "root", "1234");
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

    public void IniciarSesion(String usuario, String contraseña) {
        String sql = "SELECT * FROM usuarios WHERE id = ? AND contrasena = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usuario);
            preparedStatement.setString(2, contraseña);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                JOptionPane.showMessageDialog(null, "Inicio de sesión exitoso");
                loginFrame.setVisible(false);
                animalesFrame = new AnimalesFrame(this);
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
            String estado) throws SQLException {
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
     * 
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
     * Edita los datos de un animal. Aquí solo muestra un mensaje, pero puedes abrir
     * un panel de edición.
     */
    public void editarAnimal(Object codigo) {
        // Aquí deberías abrir un panel de edición o retornar los datos del animal para
        // editar.
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
                                "Fecha: " + fecha);
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
                if (ps != null)
                    ps.close();
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
                if (ps != null)
                    ps.close();
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
            JOptionPane.showMessageDialog(null, "Error al obtener códigos de animales: " + e.getMessage(),
                    "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "Error al obtener el historial de movimientos: " + e.getMessage(),
                    "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(null, "El animal ya se encuentra en el lote de destino.",
                        "Movimiento Inválido", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "Movimiento registrado exitosamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            // Si ocurre cualquier error, revertimos todos los cambios
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Error al hacer rollback: " + ex.getMessage());
            }
            JOptionPane.showMessageDialog(null, "Error al registrar el movimiento: " + e.getMessage(),
                    "Error de Transacción", JOptionPane.ERROR_MESSAGE);
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
     * ADVERTENCIA: Esta función modifica datos históricos y no ajusta la ubicación
     * actual
     * del animal. Debe usarse para corregir errores de digitación.
     */
    /**
     * Modifica un registro existente en el historial.
     * ADVERTENCIA: Esta función modifica datos históricos y no ajusta la ubicación
     * actual
     * del animal. Debe usarse para corregir errores de digitación.
     */
// REEMPLAZA ESTE MÉTODO EN TU CLASE 'Controlador.java'

/**
 * Modifica el último movimiento de un animal de forma segura.
 * Verifica que sea el último movimiento, actualiza el historial y la ubicación
 * actual del animal dentro de una transacción.
 */
public void modificarUltimoMovimiento(int idMovimiento, String codigoAnimal, int nuevoIdLoteDestino, java.sql.Date nuevaFecha) {
    try {
        // PASO 1: Iniciar la transacción
        connection.setAutoCommit(false);

        // PASO 2: VERIFICACIÓN CRÍTICA. Asegurarnos de que estamos modificando el ÚLTIMO movimiento del animal.
        String sqlCheckLast = "SELECT id FROM historial_lote WHERE id_animal = ? ORDER BY fecha DESC, id DESC LIMIT 1";
        try (PreparedStatement psCheck = connection.prepareStatement(sqlCheckLast)) {
            psCheck.setString(1, codigoAnimal);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next()) {
                    int ultimoMovimientoId = rs.getInt("id");
                    if (ultimoMovimientoId != idMovimiento) {
                        throw new SQLException("Solo se puede modificar el movimiento más reciente de un animal.");
                    }
                } else {
                    // Esto no debería pasar si el idMovimiento es válido, pero es una buena salvaguarda.
                    throw new SQLException("No se encontraron movimientos para el animal especificado.");
                }
            }
        }

        // PASO 3: Actualizar el registro en la tabla de historial
        String sqlUpdateHistorial = "UPDATE historial_lote SET id_lote_posterior = ?, fecha = ? WHERE id = ?";
        try (PreparedStatement psUpdateHistorial = connection.prepareStatement(sqlUpdateHistorial)) {
            psUpdateHistorial.setInt(1, nuevoIdLoteDestino);
            psUpdateHistorial.setDate(2, nuevaFecha);
            psUpdateHistorial.setInt(3, idMovimiento);
            psUpdateHistorial.executeUpdate();
        }

        // PASO 4: Actualizar la ubicación actual del animal en la tabla 'animal'
        String sqlUpdateAnimal = "UPDATE animal SET id_lote_actual = ? WHERE codigo = ?";
        try (PreparedStatement psUpdateAnimal = connection.prepareStatement(sqlUpdateAnimal)) {
            psUpdateAnimal.setInt(1, nuevoIdLoteDestino);
            psUpdateAnimal.setString(2, codigoAnimal);
            psUpdateAnimal.executeUpdate();
        }

        // PASO 5: Confirmar la transacción
        connection.commit();
        JOptionPane.showMessageDialog(null, "Último movimiento modificado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

    } catch (SQLException e) {
        // Si algo falla, revertir todo
        try {
            connection.rollback();
        } catch (SQLException ex) {
            System.out.println("Error al hacer rollback: " + ex.getMessage());
        }
        JOptionPane.showMessageDialog(null, "Error al modificar el movimiento: " + e.getMessage(), "Error de Transacción", JOptionPane.ERROR_MESSAGE);
    } finally {
        // Siempre restaurar el modo auto-commit
        try {
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            System.out.println("Error al restaurar auto-commit: " + ex.getMessage());
<<<<<<< HEAD
=======

        }
    }
}
>>>>>>> d5b33d01bf881afc79593c6b75e85430551d5bd9
    public void modificarRegistroHistorial(int idMovimiento, String nuevoCodigoAnimal, int nuevoIdLoteDestino,
            java.sql.Date nuevaFecha) {
        String sql = "UPDATE historial_lote SET id_animal = ?, id_lote_posterior = ?, fecha = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nuevoCodigoAnimal);
            ps.setInt(2, nuevoIdLoteDestino);
            ps.setDate(3, nuevaFecha); // Se usa setDate
            ps.setInt(4, idMovimiento);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Registro de historial modificado exitosamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el registro de historial a modificar.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar el historial: " + e.getMessage(),
                    "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Busca códigos de animales que coincidan con un texto de búsqueda.
     * 
     * @param busqueda El texto que el usuario está escribiendo.
     * @return Un ResultSet con los códigos que empiezan con el texto de búsqueda.
     */
    public ResultSet buscarCodigosAnimales(String busqueda) {
        ResultSet rs = null;
        // Usamos LIKE con el comodín '%' para buscar códigos que COMIENCEN con el
        // texto.
        String sql = "SELECT codigo FROM animal WHERE codigo LIKE ? ORDER BY codigo";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, busqueda + "%"); // Añade el comodín aquí
            rs = ps.executeQuery();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar animales: " + e.getMessage(), "Error de Base de Datos",
                    JOptionPane.ERROR_MESSAGE);
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

    public void guardarEventoSanitario(java.sql.Timestamp fecha, Integer idProducto, Float dosis, String motivo,
            String diagnostico, String idAnimal, String tipo) throws SQLException {
        String sql = "INSERT INTO eventos_sanitarios (fecha, id_producto, dosis, motivo, diagnostico, id_animal, tipo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setTimestamp(1, fecha);
        if (idProducto != null)
            ps.setInt(2, idProducto);
        else
            ps.setNull(2, java.sql.Types.INTEGER);
        if (dosis != null)
            ps.setFloat(3, dosis);
        else
            ps.setNull(3, java.sql.Types.FLOAT);
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

    public void actualizarSalida(int idSalida, String nuevoMotivo, java.sql.Date nuevaFecha) {
        String idAnimal = null;

        try {
            // --- PASO 1: Obtener el código del animal antes de iniciar la transacción ---
            String sqlSelect = "SELECT id_animal FROM salida WHERE id = ?";
            try (PreparedStatement psSelect = connection.prepareStatement(sqlSelect)) {
                psSelect.setInt(1, idSalida);
                ResultSet rs = psSelect.executeQuery();
                if (rs.next()) {
                    idAnimal = rs.getString("id_animal");
                } else {
                    // Si no se encuentra la salida, no podemos continuar.
                    throw new SQLException("No se encontró el registro de salida con ID: " + idSalida);
                }
            }

            // --- PASO 2: Iniciar la transacción ---
            connection.setAutoCommit(false);

            // --- PASO 3: Actualizar el registro en la tabla 'salida' ---
            String sqlUpdateSalida = "UPDATE salida SET motivo = ?, fecha = ? WHERE id = ?";
            try (PreparedStatement psSalida = connection.prepareStatement(sqlUpdateSalida)) {
                psSalida.setString(1, nuevoMotivo);
                psSalida.setDate(2, nuevaFecha);
                psSalida.setInt(3, idSalida);
                psSalida.executeUpdate();
            }

            // --- PASO 4: Actualizar el estado correspondiente en la tabla 'animal' ---
            String nuevoEstado = nuevoMotivo.equals("MUERTE") ? "MUERTO" : "VENDIDO";
            String sqlUpdateAnimal = "UPDATE animal SET estado = ? WHERE codigo = ?";
            try (PreparedStatement psAnimal = connection.prepareStatement(sqlUpdateAnimal)) {
                psAnimal.setString(1, nuevoEstado);
                psAnimal.setString(2, idAnimal);
                psAnimal.executeUpdate();
            }

            // --- PASO 5: Si todo fue exitoso, confirmar la transacción ---
            connection.commit();
            // JOptionPane.showMessageDialog(null, "Salida actualizada correctamente."); //
            // El panel ya muestra este mensaje

        } catch (SQLException e) {
            // --- ERROR: Si algo falló, revertir todos los cambios ---
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Error al intentar hacer rollback: " + ex.getMessage());
            }
            // Lanzamos una nueva excepción para que el panel la capture y muestre el
            // mensaje de error.
            throw new RuntimeException("Error al actualizar la salida: " + e.getMessage());

        } finally {
            // --- SIEMPRE: Restaurar el modo auto-commit ---
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Error al restaurar auto-commit: " + ex.getMessage());
            }
        }
    }

    /**
     * Guarda un nuevo registro de producción de leche en la base de datos.
     */
    public void guardarProduccionLeche(Timestamp fecha, int litrosMatutinos, int litrosVispertinos, String idAnimal)
            throws SQLException {
        String sql = "INSERT INTO produccion_leche (fecha, litros_matutinos, litros_vispertinos, id_animal) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, fecha);
            ps.setInt(2, litrosMatutinos);
            ps.setInt(3, litrosVispertinos);
            ps.setString(4, idAnimal);
            ps.executeUpdate();
        }
    }

    public java.util.List<String> buscarAnimalesHembras(String filtro) {
        java.util.List<String> resultado = new java.util.ArrayList<>();
        // Añadimos la condición AND sexo = 'F' a la consulta SQL
        String sql = "SELECT codigo FROM animal WHERE codigo LIKE ? AND estado = 'ACTIVO' AND sexo = 'F'";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "%" + filtro + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultado.add(rs.getString("codigo"));
            }
        } catch (Exception e) {
            System.out.println("Error al buscar animales hembras: " + e.getMessage());
        }
        return resultado;
    }
<<<<<<< HEAD
    return resultado;
=======
>>>>>>> d5b33d01bf881afc79593c6b75e85430551d5bd9

    // --- MÉTODOS A AÑADIR EN TU CONTROLADOR ---

    /**
     * Elimina un lote de la base de datos por su ID.
     * ADVERTENCIA: Esto puede fallar si hay animales asignados a este lote y
     * existen
     * restricciones de clave foránea en la base de datos.
     */
    public void eliminarLote(int idLote) {
        // Confirmación antes de una acción destructiva
        int confirmacion = JOptionPane.showConfirmDialog(null,
                "¿Está seguro de que desea eliminar este lote?\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM lotes WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, idLote);
                int filasAfectadas = ps.executeUpdate();
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(null, "Lote eliminado exitosamente.", "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró el lote a eliminar.", "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException e) {
                // Mensaje de error común si hay animales en el lote
                if (e.getMessage().contains("foreign key constraint fails")) {
                    JOptionPane.showMessageDialog(null,
                            "No se puede eliminar el lote porque contiene animales.\nPor favor, mueva los animales a otro lote antes de eliminarlo.",
                            "Error de Integridad", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error al eliminar el lote: " + e.getMessage(),
                            "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Elimina un registro del historial de movimientos.
     * Nota: Esta acción solo elimina el registro histórico, no revierte el estado
     * actual del animal en la tabla 'animal'. Sirve para corregir errores de
     * registro.
     */
    // REEMPLAZA ESTE MÉTODO EN TU CLASE 'Controlador.java'

    /**
     * Revierte un movimiento de historial.
     * Elimina el registro del historial y actualiza la ubicación actual del animal
     * a su lote anterior, siempre y cuando sea el último movimiento registrado para
     * ese animal.
     */
    public void revertirMovimientoHistorial(int idMovimiento, String codigoAnimal, Object idLoteAnteriorObj) {
        int confirmacion = JOptionPane.showConfirmDialog(null,
                "¿Está seguro de que desea revertir este movimiento?\nEl animal " + codigoAnimal
                        + " volverá a su lote anterior.",
                "Confirmar Reversión", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return; // El usuario canceló la operación
        }

        try {
            // PASO 1: Iniciar la transacción
            connection.setAutoCommit(false);

            // PASO 2: VERIFICACIÓN CRÍTICA. Asegurarnos de que estamos revirtiendo el
            // ÚLTIMO movimiento del animal.
            String sqlCheckLast = "SELECT id FROM historial_lote WHERE id_animal = ? ORDER BY fecha DESC, id DESC LIMIT 1";
            try (PreparedStatement psCheck = connection.prepareStatement(sqlCheckLast)) {
                psCheck.setString(1, codigoAnimal);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        int ultimoMovimientoId = rs.getInt("id");
                        if (ultimoMovimientoId != idMovimiento) {
                            throw new SQLException(
                                    "No se puede revertir este movimiento. No es el más reciente para este animal.");
                        }
                    } else {
                        throw new SQLException("No se encontraron movimientos para este animal.");
                    }
                }
            }

            // PASO 3: Actualizar el lote actual del animal en la tabla 'animal'
            String sqlUpdateAnimal = "UPDATE animal SET id_lote_actual = ? WHERE codigo = ?";
            try (PreparedStatement psUpdate = connection.prepareStatement(sqlUpdateAnimal)) {
                // Manejar el caso en que el lote anterior era NULO (primer ingreso del animal)
                if (idLoteAnteriorObj instanceof Integer) {
                    psUpdate.setInt(1, (Integer) idLoteAnteriorObj);
                } else {
                    psUpdate.setNull(1, java.sql.Types.INTEGER);
                }
                psUpdate.setString(2, codigoAnimal);
                psUpdate.executeUpdate();
            }

            // PASO 4: Eliminar el registro del historial
            String sqlDeleteHistorial = "DELETE FROM historial_lote WHERE id = ?";
            try (PreparedStatement psDelete = connection.prepareStatement(sqlDeleteHistorial)) {
                psDelete.setInt(1, idMovimiento);
                psDelete.executeUpdate();
            }

            // PASO 5: Confirmar la transacción
            connection.commit();
            JOptionPane.showMessageDialog(null, "Movimiento revertido exitosamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            // Si algo falla, revertir todo
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Error al hacer rollback: " + ex.getMessage());
            }
            JOptionPane.showMessageDialog(null, "Error al revertir el movimiento: " + e.getMessage(),
                    "Error de Transacción", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Siempre restaurar el modo auto-commit
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Error al restaurar auto-commit: " + ex.getMessage());
            }
        }
    }
<<<<<<< HEAD
}

}

=======
>>>>>>> d5b33d01bf881afc79593c6b75e85430551d5bd9

    /**
     * Obtiene todos los registros de producción de leche de la base de datos.
     * 
     * @return Una lista de arrays de objetos, donde cada array representa una fila.
     */
    public List<Object[]> obtenerProduccionLeche() {
        List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "SELECT id, fecha, id_animal, litros_matutinos, litros_vispertinos FROM produccion_leche ORDER BY fecha DESC, id DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] fila = new Object[5];
                fila[0] = rs.getInt("id");
                fila[1] = rs.getTimestamp("fecha");
                fila[2] = rs.getString("id_animal");
                fila[3] = rs.getInt("litros_matutinos");
                fila[4] = rs.getInt("litros_vispertinos");
                lista.add(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener registros de producción: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza un registro de producción de leche existente.
     */
    public void actualizarProduccionLeche(int id, Timestamp fecha, int litrosMatutinos, int litrosVispertinos,
            String idAnimal) throws SQLException {
        String sql = "UPDATE produccion_leche SET fecha = ?, litros_matutinos = ?, litros_vispertinos = ?, id_animal = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, fecha);
            ps.setInt(2, litrosMatutinos);
            ps.setInt(3, litrosVispertinos);
            ps.setString(4, idAnimal);
            ps.setInt(5, id);
            ps.executeUpdate();
        }
    }

    /**
     * Elimina un registro de producción de leche de la base de datos.
     */
    public void eliminarProduccionLeche(int id) {
        String sql = "DELETE FROM produccion_leche WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Registro eliminado exitosamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el registro: " + e.getMessage());
        }
    }

}
