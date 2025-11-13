package controlador;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import vista.SiriGAppLogin;
import vista.animales.AnimalesFrame;
import vista.ui.DesignSystem;

public class Controlador {

    public DesignSystem estilos;

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

        estilos = new DesignSystem();

        loginFrame = new SiriGAppLogin(this);
        loginFrame.setVisible(true);
        loginFrame.setLocationRelativeTo(null);

    }

    public void IniciarSesion(String usuario, String contraseña) {
        String sql = "{CALL sp_iniciar_sesion(?, ?)}";

        try (CallableStatement callableStatement = connection.prepareCall(sql)) {
            callableStatement.setString(1, usuario);
            callableStatement.setString(2, contraseña);
            ResultSet resultSet = callableStatement.executeQuery();
            if (resultSet.next()) {
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
            java.sql.Date fechaNacimiento,
            String sexo,
            String raza,
            String pesoNacimiento,
            String peso,
            String idMadre,
            String idPadre,
            String estado) throws SQLException {
        // 1. Llamada al nuevo procedimiento almacenado
        String sql = "{CALL sp_guardar_animal(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        // 2. Uso de CallableStatement dentro de un try-with-resources
        try (CallableStatement cs = connection.prepareCall(sql)) {
            // 3. Los parámetros se asignan de la misma manera que con PreparedStatement
            cs.setString(1, codigo);
            cs.setDate(2, fechaNacimiento);
            cs.setString(3, sexo);
            cs.setString(4, raza);
            cs.setString(5, pesoNacimiento);
            cs.setString(6, peso);
            cs.setString(7, idMadre);
            cs.setString(8, idPadre);
            cs.setString(9, estado);
            // 4. Se ejecuta la actualización
            cs.executeUpdate();
        }
    }

    /**
     * Obtiene todos los animales de la base de datos.
     * 
     * @return Lista de Object[] con los datos de cada animal.
     */
    public java.util.List<Object[]> obtenerAnimales() {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "{CALL sp_obtener_animales()}"; // Llamada al SP
        try (CallableStatement cs = connection.prepareCall(sql);
                ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Object[] fila = new Object[10];
                fila[0] = rs.getString("codigo");
                java.sql.Timestamp ts = rs.getTimestamp("fecha_nacimiento");
                if (ts != null) {
                    fila[1] = calcularEdad(new java.sql.Date(ts.getTime()));
                } else {
                    fila[1] = "";
                }
                fila[2] = rs.getString("sexo");
                fila[3] = rs.getString("raza");
                fila[4] = rs.getString("peso_nacimiento");
                fila[5] = rs.getString("peso");
                fila[6] = rs.getString("id_madre");
                fila[7] = rs.getString("id_padre");
                String loteNombre = rs.getString("lote_nombre");
                fila[8] = loteNombre != null ? loteNombre : "--";
                fila[9] = rs.getString("estado");
                lista.add(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener animales: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Busca animales (filas completas) que coincidan con el filtro en el código,
     * raza o nombre del lote. Devuelve la misma estructura que obtenerAnimales()
     * (10 columnas).
     *
     * @param filtro texto a buscar (se usa LIKE %filtro%).
     * @return lista de filas con 10 columnas: Codigo, Edad, Sexo, Raza, Peso Nac.,
     *         Peso, Madre, Padre, Lote, Estado
     */
    public java.util.List<Object[]> buscarAnimalesCompletos(String filtro) {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "{CALL sp_buscar_animales_completos(?)}"; // Llamada al SP
        try (CallableStatement cs = connection.prepareCall(sql)) {
            String like = "%" + filtro + "%";
            cs.setString(1, like); // Solo un parámetro ahora
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[10];
                    fila[0] = rs.getString("codigo");
                    java.sql.Timestamp ts = rs.getTimestamp("fecha_nacimiento");
                    if (ts != null) {
                        fila[1] = calcularEdad(new java.sql.Date(ts.getTime()));
                    } else {
                        fila[1] = "";
                    }
                    fila[2] = rs.getString("sexo");
                    fila[3] = rs.getString("raza");
                    fila[4] = rs.getString("peso_nacimiento");
                    fila[5] = rs.getString("peso");
                    fila[6] = rs.getString("id_madre");
                    fila[7] = rs.getString("id_padre");
                    String loteNombre = rs.getString("lote_nombre");
                    fila[8] = loteNombre != null ? loteNombre : "--";
                    fila[9] = rs.getString("estado");
                    lista.add(fila);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al buscar animales: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Calcula la edad (años/meses/días) a partir de una fecha de nacimiento.
     *
     * @param fechaNacimiento java.sql.Date o null
     * @return String con la edad formateada, por ejemplo "2 años 3 meses" o "5
     *         meses" o "10 días".
     */
    private String calcularEdad(java.sql.Date fechaNacimiento) {
        if (fechaNacimiento == null)
            return "";
        try {
            LocalDate nacimiento = fechaNacimiento.toLocalDate();
            LocalDate hoy = LocalDate.now();
            if (nacimiento.isAfter(hoy)) {
                return "0 días";
            }
            Period p = Period.between(nacimiento, hoy);
            if (p.getYears() > 0) {
                return p.getYears() + " años " + p.getMonths() + " meses";
            } else if (p.getMonths() > 0) {
                return p.getMonths() + " meses";
            } else {
                return p.getDays() + " días";
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Elimina un animal por su código.
     */
    public void eliminarAnimal(Object codigo) {
        String sql = "{CALL sp_eliminar_animal(?)}";
        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, codigo.toString());
            cs.executeUpdate();
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

        // 1. La consulta ahora es una llamada al procedimiento almacenado.
        String sql = "{CALL sp_buscar_animales_activos(?)}";

        // 2. Usamos CallableStatement en lugar de PreparedStatement.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 3. El resto del código es prácticamente idéntico.
            cs.setString(1, "%" + filtro + "%");

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    resultado.add(rs.getString("codigo"));
                }
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
        String sql = "{CALL sp_guardar_salida(?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, animal);
            cs.setString(2, motivo);
            cs.setDate(3, fecha);
            cs.executeUpdate();
        }
    }

    /**
     * Obtiene todas las salidas registradas
     */
    public java.util.List<Object[]> obtenerSalidas() {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "{CALL sp_obtener_salidas()}"; // Llamada al SP
        try (CallableStatement cs = connection.prepareCall(sql);
                ResultSet rs = cs.executeQuery()) {
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
        // 1. La consulta ahora es una única llamada al procedimiento almacenado.
        String sql = "{CALL sp_eliminar_salida(?)}";

        // 2. Usamos CallableStatement. Toda la lógica compleja ha desaparecido de Java.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 3. Asignamos el único parámetro que el procedimiento necesita: el ID de la
            // salida.
            cs.setInt(1, id);

            // 4. Ejecutamos el procedimiento. La base de datos se encarga de la
            // transacción.
            cs.executeUpdate();

            // Si el procedimiento termina sin lanzar un error, significa que el COMMIT fue
            // exitoso.
            JOptionPane.showMessageDialog(null, "Salida eliminada y animal reactivado correctamente.");

        } catch (Exception e) {
            // Si algo falló, el ROLLBACK fue automático en la BD y aquí recibimos el
            // mensaje de error.
            JOptionPane.showMessageDialog(null, "Error al eliminar la salida: " + e.getMessage());
        }
    }

    /**
     * Edita un registro de salida
     */
    public Object[] obtenerDatosSalidaPorId(int id) {
        // 1. La consulta ahora es una llamada al procedimiento almacenado.
        String sql = "{CALL sp_obtener_datos_salida_por_id(?)}";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setInt(1, id);

            try (ResultSet rs = cs.executeQuery()) {
                // 2. Si se encuentra un resultado...
                if (rs.next()) {
                    // 3. Creamos un array para guardar los datos.
                    Object[] datosSalida = new Object[3];
                    datosSalida[0] = rs.getString("id_animal");
                    datosSalida[1] = rs.getString("motivo");
                    datosSalida[2] = rs.getDate("fecha");

                    // 4. Retornamos el array con los datos.
                    return datosSalida;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener los datos de la salida: " + e.getMessage());
        }

        // 5. Si no se encontró nada o hubo un error, retornamos null.
        return null;
    }

    public void registrarNuevoLote(String nombre, String etapa, String descripcion) {
        // 1. La consulta ahora es una llamada al procedimiento almacenado.
        String sql = "{CALL sp_registrar_nuevo_lote(?, ?, ?)}";

        // 2. Usamos try-with-resources, que cierra automáticamente el
        // CallableStatement.
        // Esto elimina la necesidad del bloque 'finally'.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 3. Asignamos los parámetros.
            cs.setString(1, nombre);
            cs.setString(2, etapa);
            cs.setString(3, descripcion);

            // 4. Ejecutamos el procedimiento.
            cs.executeUpdate();

            JOptionPane.showMessageDialog(null, "Lote registrado exitosamente.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar lote: " + e.getMessage());
        }
    }

    public java.util.List<Object[]> obtenerDetallesTodosLotes() {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();

        // 1. La consulta ahora es una llamada al procedimiento almacenado.
        // No lleva paréntesis porque no tiene parámetros.
        String sql = "{CALL sp_obtener_detalles_todos_lotes()}";

        // 2. Usamos CallableStatement en lugar de PreparedStatement.
        try (CallableStatement cs = connection.prepareCall(sql);
                ResultSet rs = cs.executeQuery()) {

            // 3. El resto del código para procesar el resultado es EXACTAMENTE el mismo.
            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("etapa"),
                        rs.getString("descripcion")
                };
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener detalles de lotes: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public java.util.List<String> obtenerLotesParaComboBox() {
        java.util.List<String> lotesFormateados = new java.util.ArrayList<>();

        // 1. La consulta ahora es la llamada al procedimiento almacenado.
        String sql = "{CALL sp_obtener_lotes_para_combobox()}";

        // 2. Usamos CallableStatement.
        try (CallableStatement cs = connection.prepareCall(sql);
                ResultSet rs = cs.executeQuery()) {

            // 3. La lógica para leer los resultados y formatear la cadena
            // sigue siendo exactamente la misma. ¡No cambia nada aquí!
            while (rs.next()) {
                String item = rs.getInt("id") + " - " + rs.getString("nombre");
                lotesFormateados.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener lotes para ComboBox: " + e.getMessage());
            e.printStackTrace();
        }
        return lotesFormateados;
    }

    public void modificarLote(int idLote, String nombre, String etapa, String descripcion) {
        // 1. La consulta ahora es una llamada al procedimiento almacenado.
        String sql = "{CALL sp_modificar_lote(?, ?, ?, ?)}";

        // 2. Usamos try-with-resources, que elimina la necesidad del bloque 'finally'.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 3. Asignamos los parámetros en el orden en que los definimos en el SP.
            cs.setInt(1, idLote);
            cs.setString(2, nombre);
            cs.setString(3, etapa);
            cs.setString(4, descripcion);

            // 4. Ejecutamos la actualización.
            cs.executeUpdate();

            JOptionPane.showMessageDialog(null, "Lote modificado exitosamente.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar lote: " + e.getMessage());
        }
    }

    /**
     * Obtiene solo los codigos de todos los animales para llenar el JComboBox.
     * He cambiado el nombre del método para que sea más claro.
     */
    // public ResultSet obtenerCodigosAnimales() {
    // ResultSet rs = null;
    // try {
    // String sql = "SELECT codigo FROM animal ORDER BY codigo";
    // PreparedStatement ps = connection.prepareStatement(sql);
    // rs = ps.executeQuery();
    // } catch (SQLException e) {
    // JOptionPane.showMessageDialog(null, "Error al obtener códigos de animales: "
    // + e.getMessage(),
    // "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
    // }
    // return rs;
    // }

    /**
     * Obtiene el historial de movimientos simple, sin JOINs.
     */
    public java.util.List<Object[]> obtenerHistorialMovimientos() {
        // 1. Preparamos la lista que vamos a devolver.
        java.util.List<Object[]> historial = new java.util.ArrayList<>();

        // 2. La consulta es la llamada al procedimiento almacenado.
        String sql = "{CALL sp_obtener_historial_movimientos()}";

        // 3. Usamos try-with-resources para garantizar el cierre automático de
        // recursos.
        try (CallableStatement cs = connection.prepareCall(sql);
                ResultSet rs = cs.executeQuery()) {

            // 4. Iteramos sobre los resultados y los empaquetamos en la lista.
            while (rs.next()) {
                // La lógica para construir la fila que antes estaba en la VISTA,
                // ahora está aquí, en el CONTROLADOR, que es donde debe estar.
                Object[] fila = {
                        rs.getInt("id"),
                        rs.getString("id_animal"),
                        // El manejo del NULL se hace aquí.
                        rs.getObject("id_lote_anterior") == null ? "N/A" : rs.getInt("id_lote_anterior"),
                        rs.getInt("id_lote_posterior"),
                        rs.getDate("fecha")
                };
                historial.add(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el historial de movimientos: " + e.getMessage(),
                    "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }

        // 5. Retornamos la lista llena de datos, desconectada de la BD.
        return historial;
    }

    public void registrarMovimientoAnimal(String codigoAnimal, int idLoteDestino, java.sql.Date fecha) {
        String sql = "{CALL sp_registrar_movimiento_animal(?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, codigoAnimal);
            cs.setInt(2, idLoteDestino);
            cs.setDate(3, fecha);
            cs.executeUpdate();
            JOptionPane.showMessageDialog(null, "Movimiento registrado exitosamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            // El mensaje de error vendrá directamente desde la base de datos si la
            // validación falla
            JOptionPane.showMessageDialog(null, "Error al registrar el movimiento: " + e.getMessage(),
                    "Error de Transacción", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void modificarUltimoMovimiento(int idMovimiento, String codigoAnimal, int nuevoIdLoteDestino,
            java.sql.Date nuevaFecha) throws SQLException {

        // 1. La consulta es una única llamada a nuestro procedimiento transaccional.
        String sql = "{CALL sp_modificar_ultimo_movimiento(?, ?, ?, ?)}";

        // 2. Usamos un simple try-with-resources.
        // El 'throws SQLException' en la firma del método se encarga de propagar el
        // error.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 3. Asignamos los parámetros.
            cs.setInt(1, idMovimiento);
            cs.setString(2, codigoAnimal);
            cs.setInt(3, nuevoIdLoteDestino);
            cs.setDate(4, nuevaFecha);

            // 4. Ejecutamos. La base de datos se encarga de TODO:
            // validar, actualizar, y manejar el commit/rollback.
            cs.executeUpdate();

            // Si llegamos aquí, el COMMIT fue exitoso.
            JOptionPane.showMessageDialog(null, "Último movimiento modificado exitosamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            // Si el procedimiento lanzó un error (SIGNAL), lo recibimos aquí.
            // Simplemente lo re-lanzamos para que la vista lo maneje.
            throw e;
        }
    }

    public void modificarRegistroHistorial(int idMovimiento, String nuevoCodigoAnimal, int nuevoIdLoteDestino,
            java.sql.Date nuevaFecha) {

        // 1. La llamada incluye un '?' adicional para el parámetro de salida (OUT).
        String sql = "{CALL sp_modificar_registro_historial(?, ?, ?, ?, ?)}";

        try (CallableStatement cs = connection.prepareCall(sql)) {
            // 2. Asignamos los parámetros de ENTRADA (IN) como siempre.
            cs.setInt(1, idMovimiento);
            cs.setString(2, nuevoCodigoAnimal);
            cs.setInt(3, nuevoIdLoteDestino);
            cs.setDate(4, nuevaFecha);

            // 3. REGISTRAMOS el parámetro de SALIDA (OUT) antes de ejecutar.
            // Le decimos a JDBC que el 5º parámetro será un entero que volverá de la BD.
            cs.registerOutParameter(5, java.sql.Types.INTEGER);

            // 4. Ejecutamos la llamada.
            cs.executeUpdate();

            // 5. OBTENEMOS el valor del parámetro de salida que la BD nos ha devuelto.
            int filasAfectadas = cs.getInt(5);

            // 6. La lógica de decisión es exactamente la misma que antes.
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

    public List<String> buscarCodigosAnimales(String busqueda) {
        // 1. Preparamos la lista para devolver los resultados.
        java.util.List<String> codigosEncontrados = new java.util.ArrayList<>();

        // 2. La consulta es la llamada al procedimiento.
        String sql = "{CALL sp_buscar_codigos_animales_like(?)}";

        // 3. Usamos try-with-resources para garantizar el cierre automático.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 4. Asignamos el parámetro, añadiendo el comodín '%' como antes.
            cs.setString(1, busqueda + "%");

            try (ResultSet rs = cs.executeQuery()) {
                // 5. Llenamos nuestra lista de Strings.
                while (rs.next()) {
                    codigosEncontrados.add(rs.getString("codigo"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar animales: " + e.getMessage(), "Error de Base de Datos",
                    JOptionPane.ERROR_MESSAGE);
        }

        // 6. Retornamos la lista llena y desconectada de la base de datos.
        return codigosEncontrados;
    }

    /* ------------------ Productos ------------------ */

    public void guardarProducto(String nombre, String tipo) throws SQLException {
        String sql = "{CALL sp_guardar_producto(?, ?)}";
        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, nombre);
            cs.setString(2, tipo);
            cs.executeUpdate();
        }
    }

    public void editarProducto(int id, String nombre, String tipo) throws SQLException {
        String sql = "{CALL sp_editar_producto(?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.setString(2, nombre);
            cs.setString(3, tipo);
            cs.executeUpdate();
        }
    }

    public void eliminarProducto(int id) throws SQLException {
        String sql = "{CALL sp_eliminar_producto(?)}";
        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.executeUpdate();
        }
    }

    public java.util.List<Object[]> obtenerProductos() {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "{CALL sp_obtener_productos()}"; // Llamada al SP
        try (CallableStatement cs = connection.prepareCall(sql);
                ResultSet rs = cs.executeQuery()) {
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
        String sql = "{CALL sp_obtener_productos_por_tipo(?)}"; // Llamada al SP
        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, tipo);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[3];
                    fila[0] = rs.getInt("id");
                    fila[1] = rs.getString("producto");
                    fila[2] = rs.getString("tipo");
                    lista.add(fila);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener productos por tipo: " + e.getMessage());
        }
        return lista;
    }
    /* ------------------ Eventos sanitarios ------------------ */

    public void guardarEventoSanitario(java.sql.Timestamp fecha, Integer idProducto, Float dosis, String motivo,
            String diagnostico, String idAnimal, String tipo) throws SQLException {

        // 1. La consulta ahora es la llamada al procedimiento almacenado.
        String sql = "{CALL sp_guardar_evento_sanitario(?, ?, ?, ?, ?, ?, ?)}";

        // 2. Usamos try-with-resources con CallableStatement.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 3. La asignación de parámetros es idéntica a la que ya tenías,
            // incluyendo el manejo correcto de los valores nulos.
            cs.setTimestamp(1, fecha);

            if (idProducto != null) {
                cs.setInt(2, idProducto);
            } else {
                cs.setNull(2, java.sql.Types.INTEGER);
            }

            if (dosis != null) {
                cs.setFloat(3, dosis);
            } else {
                cs.setNull(3, java.sql.Types.FLOAT);
            }

            cs.setString(4, motivo);
            cs.setString(5, diagnostico);
            cs.setString(6, idAnimal);
            cs.setString(7, tipo);

            // 4. Ejecutamos el procedimiento.
            cs.executeUpdate();
        }
        // El 'throws SQLException' en la firma del método se encarga de propagar
        // cualquier error.
    }

    public java.util.List<Object[]> obtenerEventosSanitarios() {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "{CALL sp_obtener_eventos_sanitarios()}"; // Llamada al SP
        try (CallableStatement cs = connection.prepareCall(sql);
                ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Object[] fila = new Object[8];
                fila[0] = rs.getInt("id");
                fila[1] = rs.getTimestamp("fecha");
                fila[2] = rs.getString("tipo");
                fila[3] = rs.getString("id_animal");
                fila[4] = rs.getString("nombre_producto");
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

        // 1. La consulta ahora es la llamada al procedimiento almacenado.
        String sql = "{CALL sp_obtener_eventos_sanitarios_por_tipo(?)}";

        // 2. Usamos try-with-resources para gestionar CallableStatement y ResultSet.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 3. Asignamos el parámetro de entrada.
            cs.setString(1, tipo);

            try (ResultSet rs = cs.executeQuery()) {
                // 4. La lógica de lectura es la misma, pero ahora corregimos el tamaño del
                // array.
                while (rs.next()) {
                    Object[] fila = new Object[8]; // Tamaño corregido a 8
                    fila[0] = rs.getInt("id");
                    fila[1] = rs.getTimestamp("fecha");
                    fila[2] = rs.getString("tipo"); // Campo 'tipo' añadido
                    fila[3] = rs.getString("id_animal");
                    fila[4] = rs.getObject("id_producto");
                    fila[5] = rs.getObject("dosis");
                    fila[6] = rs.getString("motivo");
                    fila[7] = rs.getString("diagnostico");
                    lista.add(fila);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener eventos por tipo: " + e.getMessage());
        }
        return lista;
    }

    public void eliminarEvento(int id) {
        // 1. La consulta ahora es una llamada al procedimiento almacenado.
        String sql = "{CALL sp_eliminar_evento_sanitario(?)}";

        // 2. Usamos try-with-resources para una gestión de recursos más robusta.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 3. Asignamos el parámetro.
            cs.setInt(1, id);

            // 4. Ejecutamos el procedimiento.
            cs.executeUpdate();

            JOptionPane.showMessageDialog(null, "Evento eliminado correctamente");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar evento: " + e.getMessage());
        }
    }

    public Object[] obtenerEventoSanitarioPorId(int id) {
        // 1. La consulta ahora es la llamada al procedimiento almacenado.
        String sql = "{CALL sp_obtener_evento_sanitario_por_id(?)}";

        // 2. Usamos CallableStatement en lugar de PreparedStatement.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            cs.setInt(1, id);

            // 3. El resto del código para ejecutar la consulta y procesar el resultado
            // permanece EXACTAMENTE igual.
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Object[] fila = new Object[8];
                    fila[0] = rs.getInt("id");
                    fila[1] = rs.getTimestamp("fecha");
                    fila[2] = rs.getString("tipo");
                    fila[3] = rs.getString("id_animal");
                    fila[4] = rs.getObject("id_producto");
                    fila[5] = rs.getObject("dosis");
                    fila[6] = rs.getString("motivo");
                    fila[7] = rs.getString("diagnostico");
                    return fila;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener datos del evento: " + e.getMessage());
        }
        return null;
    }

    /**
     * Actualiza un registro de evento sanitario existente en la base de datos.
     */
    public void actualizarEventoSanitario(int idEvento, java.sql.Timestamp fecha, Integer idProducto, Float dosis,
            String idAnimal, String motivo, String diagnostico) throws SQLException {

        // 1. La consulta ahora es la llamada al procedimiento almacenado.
        String sql = "{CALL sp_actualizar_evento_sanitario(?, ?, ?, ?, ?, ?, ?)}";

        // 2. Usamos CallableStatement dentro de un try-with-resources.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 3. Asignamos los parámetros en el orden definido en el SP.
            // La lógica para manejar nulos sigue siendo la misma.
            cs.setInt(1, idEvento);
            cs.setTimestamp(2, fecha);

            if (idProducto != null) {
                cs.setInt(3, idProducto);
            } else {
                cs.setNull(3, java.sql.Types.INTEGER);
            }

            if (dosis != null) {
                cs.setFloat(4, dosis);
            } else {
                cs.setNull(4, java.sql.Types.FLOAT);
            }

            cs.setString(5, idAnimal);
            cs.setString(6, motivo);
            cs.setString(7, diagnostico);

            // 4. Ejecutamos la actualización.
            cs.executeUpdate();
        }
        // El 'throws SQLException' se encarga de propagar cualquier error.
    }

    public void actualizarSalida(int idSalida, String nuevoMotivo, java.sql.Date nuevaFecha) {

        // 1. La consulta es una única llamada a nuestro procedimiento transaccional.
        String sql = "{CALL sp_actualizar_salida(?, ?, ?)}";

        // 2. Usamos un simple try-with-resources.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 3. Asignamos los parámetros de entrada.
            cs.setInt(1, idSalida);
            cs.setString(2, nuevoMotivo);
            cs.setDate(3, nuevaFecha);

            // 4. Ejecutamos. La base de datos se encarga de TODO lo demás.
            cs.executeUpdate();

            // Si no hay excepción, la operación fue un éxito.
            // El mensaje de éxito ya se muestra en el panel, por lo que no se necesita
            // aquí.

        } catch (SQLException e) {
            // Si el procedimiento falló, el ROLLBACK fue automático.
            // Lanzamos una excepción para que el panel la capture y muestre el error.
            throw new RuntimeException("Error al actualizar la salida: ".concat(e.getMessage()), e);
        }
    }

    /**
     * Guarda un nuevo registro de producción de leche en la base de datos.
     */
    public void guardarProduccionLeche(java.sql.Date fecha, int litrosMatutinos, int litrosVispertinos, String idAnimal)
            throws SQLException {

        // 1. La consulta ahora es la llamada al procedimiento almacenado.
        String sql = "{CALL sp_guardar_produccion_leche(?, ?, ?, ?)}";

        // 2. Usamos CallableStatement en lugar de PreparedStatement.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 3. La asignación de parámetros es la misma.
            cs.setDate(1, fecha);
            cs.setInt(2, litrosMatutinos);
            cs.setInt(3, litrosVispertinos);
            cs.setString(4, idAnimal);

            // 4. Ejecutamos el procedimiento.
            cs.executeUpdate();
        }
        // El 'throws SQLException' se encarga de propagar cualquier error.
    }

    public java.util.List<String> buscarAnimalesHembras(String filtro) {
        java.util.List<String> resultado = new java.util.ArrayList<>();
        String sql = "{CALL sp_buscar_animales_hembras(?)}"; // Llamada al SP
        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, "%" + filtro + "%");
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    resultado.add(rs.getString("codigo"));
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar animales hembras: " + e.getMessage());
        }
        return resultado;
    }

    // --- MÉTODOS A AÑADIR EN TU CONTROLADOR ---

    /**
     * Elimina un lote de la base de datos por su ID.
     * ADVERTENCIA: Esto puede fallar si hay animales asignados a este lote y
     * existen
     * restricciones de clave foránea en la base de datos.
     */
    public void eliminarLote(int idLote) {
        // 1. La lógica de confirmación del usuario se mantiene intacta. ¡Esto es
        // correcto!
        int confirmacion = JOptionPane.showConfirmDialog(null,
                "¿Está seguro de que desea eliminar este lote?\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            // 2. La consulta ahora llama al SP con un parámetro IN y uno OUT.
            String sql = "{CALL sp_eliminar_lote(?, ?)}";

            try (CallableStatement cs = connection.prepareCall(sql)) {
                // 3. Asignamos el parámetro de ENTRADA.
                cs.setInt(1, idLote);

                // 4. Registramos el parámetro de SALIDA.
                cs.registerOutParameter(2, java.sql.Types.INTEGER);

                cs.executeUpdate();

                // 5. Obtenemos el resultado que nos devolvió el SP.
                int filasAfectadas = cs.getInt(2);

                // 6. La lógica para mostrar mensajes basada en el resultado es la misma.
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(null, "Lote eliminado exitosamente.", "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró el lote a eliminar.", "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException e) {
                // 7. El manejo de errores específico se mantiene exactamente igual. ¡Excelente!
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
        // No necesitamos el diálogo de confirmación aquí si ya se muestra en la vista
        // (panel).
        // Si no, puedes mantenerlo.
        int confirmacion = JOptionPane.showConfirmDialog(null,
                "¿Está seguro de que desea revertir este movimiento?\nEl animal " + codigoAnimal
                        + " volverá a su lote anterior.",
                "Confirmar Reversión", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return; // El usuario canceló la operación
        }

        String sql = "{CALL sp_revertir_ultimo_movimiento(?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setInt(1, idMovimiento);
            cs.setString(2, codigoAnimal);

            // El procedimiento almacenado espera un INT que puede ser NULL.
            // Debemos manejar el caso en que el lote anterior no existía.
            if (idLoteAnteriorObj instanceof Integer) {
                cs.setInt(3, (Integer) idLoteAnteriorObj);
            } else {
                cs.setNull(3, java.sql.Types.INTEGER);
            }

            cs.executeUpdate();
            JOptionPane.showMessageDialog(null, "Movimiento revertido exitosamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            // El mensaje de error personalizado vendrá de la base de datos
            JOptionPane.showMessageDialog(null, "Error al revertir el movimiento: " + e.getMessage(),
                    "Error de Transacción", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Obtiene todos los registros de producción de leche de la base de datos.
     * 
     * @return Una lista de arrays de objetos, donde cada array representa una fila.
     */
    public List<Object[]> obtenerProduccionLeche() {
        List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "{CALL sp_obtener_produccion_leche()}"; // Llamada al SP
        try (CallableStatement cs = connection.prepareCall(sql);
                ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Object[] fila = new Object[5];
                fila[0] = rs.getInt("id");
                fila[1] = rs.getDate("fecha");
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
    public void actualizarProduccionLeche(int id, Date fecha, int litrosMatutinos, int litrosVispertinos,
            String idAnimal) throws SQLException {

        // 1. La consulta ahora es la llamada al procedimiento almacenado.
        String sql = "{CALL sp_actualizar_produccion_leche(?, ?, ?, ?, ?)}";

        // 2. Usamos CallableStatement.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 3. Asignamos los parámetros en el orden definido en el SP.
            // (id, fecha, matutinos, vispertinos, idAnimal)
            cs.setInt(1, id);
            cs.setDate(2, fecha);
            cs.setInt(3, litrosMatutinos);
            cs.setInt(4, litrosVispertinos);
            cs.setString(5, idAnimal);

            // 4. Ejecutamos el procedimiento.
            cs.executeUpdate();
        }
        // El 'throws SQLException' se encarga de propagar cualquier error.
    }

    /**
     * Elimina un registro de producción de leche de la base de datos.
     */
    public void eliminarProduccionLeche(int id) {
        // 1. La consulta ahora es la llamada al procedimiento almacenado.
        String sql = "{CALL sp_eliminar_produccion_leche(?)}";

        // 2. Usamos CallableStatement en lugar de PreparedStatement.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 3. Asignamos el parámetro.
            cs.setInt(1, id);

            // 4. Ejecutamos el procedimiento.
            cs.executeUpdate();

            JOptionPane.showMessageDialog(null, "Registro eliminado exitosamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el registro: " + e.getMessage());
        }
    }

    public boolean animalExiste(String codigo) {
        // 1. La consulta ahora es la llamada al procedimiento almacenado.
        String sql = "{CALL sp_animal_existe(?)}";

        // 2. Usamos CallableStatement en lugar de PreparedStatement.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            cs.setString(1, codigo);

            // 3. La lógica para ejecutar la consulta y comprobar el resultado
            // con rs.next() sigue siendo la misma. Es la forma más eficiente.
            try (ResultSet rs = cs.executeQuery()) {
                return rs.next(); // Retorna true si el cursor se movió (se encontró una fila), false si no.
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar la existencia del animal: " + e.getMessage());
            // Es una buena práctica de seguridad devolver false en caso de error
            // para prevenir, por ejemplo, la creación de registros duplicados.
            return false;
        }
    }

    public boolean existeProduccionLechePorAnimalYFecha(String idAnimal, java.sql.Date fecha) {
        // 1. La consulta ahora es la llamada al procedimiento almacenado.
        String sql = "{CALL sp_existe_produccion_leche_por_animal_y_fecha(?, ?)}";

        // 2. Usamos CallableStatement.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 3. Asignamos los parámetros de entrada.
            cs.setString(1, idAnimal);
            cs.setDate(2, fecha);

            // 4. La lógica de comprobación con rs.next() sigue siendo la misma.
            try (ResultSet rs = cs.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar producción de leche: " + e.getMessage());
            // Devolver false en caso de error es una práctica segura.
            return false;
        }
    }

    public List<Object[]> buscarEventosSanitarios(String filtro) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "{CALL sp_buscar_eventos_sanitarios(?)}"; // Llamada al SP
        try (CallableStatement cs = connection.prepareCall(sql)) {
            String likeFiltro = "%" + filtro + "%";
            cs.setString(1, likeFiltro);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[8];
                    fila[0] = rs.getInt("es.id");
                    fila[1] = rs.getTimestamp("es.fecha");
                    fila[2] = rs.getString("es.tipo");
                    fila[3] = rs.getString("es.id_animal");
                    fila[4] = rs.getString("p.producto");
                    fila[5] = rs.getObject("es.dosis");
                    fila[6] = rs.getString("es.motivo");
                    fila[7] = rs.getString("es.diagnostico");
                    lista.add(fila);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al buscar eventos sanitarios: " + e.getMessage());
        }
        return lista;
    }

    public List<Object[]> buscarProductosTratamiento(String filtro) {
        List<Object[]> lista = new ArrayList<>();

        // 1. La consulta ahora es la llamada al procedimiento almacenado.
        String sql = "{CALL sp_buscar_productos_tratamiento(?)}";

        // 2. Usamos CallableStatement.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 3. Asignamos el parámetro, añadiendo los comodines como antes.
            cs.setString(1, "%" + filtro + "%");

            // 4. El resto del código para procesar el resultado es exactamente el mismo.
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[2];
                    fila[0] = rs.getInt("id");
                    fila[1] = rs.getString("producto");
                    lista.add(fila);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar productos de tratamiento: " + e.getMessage());
        }
        return lista;
    }

    public boolean esUltimoMovimiento(int idMovimiento, String codigoAnimal) {
        // 1. La consulta ahora es la llamada al procedimiento almacenado.
        String sql = "{CALL sp_obtener_ultimo_movimiento_id(?)}";

        // 2. Usamos CallableStatement.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            cs.setString(1, codigoAnimal);

            // 3. El resto de la lógica es idéntica: ejecutamos, leemos el resultado
            // y comparamos los IDs.
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    int ultimoId = rs.getInt("id");
                    return ultimoId == idMovimiento;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar el último movimiento: " + e.getMessage());
        }

        // 4. El retorno por defecto sigue siendo 'false' por seguridad.
        return false;
    }

    public List<Object[]> buscarProductosDesparasitantes(String filtro) {
        List<Object[]> lista = new ArrayList<>();

        // 1. La consulta ahora es la llamada al procedimiento almacenado.
        String sql = "{CALL sp_buscar_productos_desparasitantes(?)}";

        // 2. Usamos CallableStatement.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 3. Asignamos el parámetro, añadiendo los comodines.
            cs.setString(1, "%" + filtro + "%");

            // 4. El resto del código para procesar el resultado es exactamente el mismo.
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[2];
                    fila[0] = rs.getInt("id");
                    fila[1] = rs.getString("producto");
                    lista.add(fila);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar productos desparasitantes: " + e.getMessage());
        }
        return lista;
    }

    public void actualizarEventoSanitario(int idEvento, java.sql.Timestamp fecha, Integer idProducto, Float dosis,
            String idAnimal) throws SQLException {
        // Reutilizamos el método existente, pasando los valores implícitos
        this.actualizarEventoSanitario(idEvento, fecha, idProducto, dosis, idAnimal, "DESPARASITANTE", "");
    }

    public List<Object[]> buscarLotes(String filtro) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "{CALL sp_buscar_lotes(?, ?)}"; // Llamada al SP

        try (CallableStatement cs = connection.prepareCall(sql)) {
            String likeFiltro = "%" + filtro + "%";
            int idFiltro = -1; // Usamos un valor improbable como -1 para indicar que no es un ID válido.

            try {
                idFiltro = Integer.parseInt(filtro);
            } catch (NumberFormatException e) {
                // No es un número, idFiltro se queda en -1
            }

            cs.setString(1, likeFiltro);
            cs.setInt(2, idFiltro);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = {
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("etapa"),
                            rs.getString("descripcion")
                    };
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar lotes: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al realizar la búsqueda de lotes: " + e.getMessage(),
                    "Error de Búsqueda", JOptionPane.ERROR_MESSAGE);
        }
        return lista;
    }

    public List<Object[]> buscarMovimientosHistorial(String filtro) {
        List<Object[]> lista = new ArrayList<>();

        // 1. La consulta ahora es una única y simple llamada al SP.
        String sql = "{CALL sp_buscar_movimientos_historial(?, ?)}";

        // 2. Preparamos los dos parámetros que el SP necesita.
        String likeFiltro = "%" + filtro + "%";
        int filtroNumerico;
        try {
            filtroNumerico = Integer.parseInt(filtro);
        } catch (NumberFormatException e) {
            // Si no es un número, usamos un valor que no coincidirá con ningún ID.
            filtroNumerico = -1;
        }

        // 3. Ejecutamos la llamada.
        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, likeFiltro);
            cs.setInt(2, filtroNumerico);

            try (ResultSet rs = cs.executeQuery()) {
                // 4. La lógica para leer los resultados y formatear la fila
                // sigue siendo exactamente la misma. ¡No cambia nada aquí!
                while (rs.next()) {
                    Object loteAnterior = rs.getObject("id_lote_anterior") == null
                            ? "N/A"
                            : rs.getInt("id_lote_anterior");

                    Object[] fila = {
                            rs.getInt("id"),
                            rs.getString("id_animal"),
                            loteAnterior,
                            rs.getInt("id_lote_posterior"),
                            rs.getDate("fecha")
                    };
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar en el historial de movimientos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // Otros métodos del controlador...
    public void iniciarEdicionAnimal(String codigo) {
        // 1. Obtener todos los datos del animal desde la base de datos
        // (Necesitarás crear este método que haga un "SELECT * FROM animales WHERE
        // codigo = ?")
        Object[] datosAnimal = obtenerDatosCompletosAnimal(codigo); // Esto es un ejemplo

        if (datosAnimal != null) {

            // 3. Cambiar al panel de registro para que el usuario pueda editar
            animalesFrame.cambiarPanelContenido(animalesFrame.pRegistro.createContentPanel());

            // 2. Cargar los datos en el panel de registro
            // Asegúrate que `animalesFrame.pRegistro` sea la instancia persistente del
            // panel
            animalesFrame.pRegistro.cargarDatosParaEdicion(
                    (String) datosAnimal[0], // codigo
                    (java.sql.Date) datosAnimal[1], // fecha_nacimiento
                    (String) datosAnimal[2], // sexo
                    (String) datosAnimal[3], // raza
                    (String) datosAnimal[4], // peso_nacimiento
                    (String) datosAnimal[5], // peso_actual
                    (String) datosAnimal[6], // id_madre
                    (String) datosAnimal[7], // id_padre
                    (String) datosAnimal[8] // estado
            );

        } else {
            JOptionPane.showMessageDialog(null, "No se encontraron los datos del animal seleccionado.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para obtener todos los datos de un animal por su código.
     * DEBES IMPLEMENTAR ESTO con tu lógica de base de datos.
     */
    public Object[] obtenerDatosCompletosAnimal(String codigo) {

        String sql = "{CALL sp_buscar_animales_completos(?)}"; // Llamada al SP
        Object[] datosAnimal = null;
        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, codigo);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    datosAnimal = new Object[10];
                    datosAnimal[0] = rs.getString("codigo");
                    datosAnimal[1] = rs.getDate("fecha_nacimiento");
                    datosAnimal[2] = rs.getString("sexo");
                    datosAnimal[3] = rs.getString("raza");
                    datosAnimal[4] = rs.getString("peso_nacimiento");
                    datosAnimal[5] = rs.getString("peso");
                    datosAnimal[6] = rs.getString("id_madre");
                    datosAnimal[7] = rs.getString("id_padre");
                    datosAnimal[8] = rs.getString("estado");
                    datosAnimal[9] = rs.getString("lote_nombre");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener datos del animal: " + e.getMessage());
        }

        return datosAnimal; // Devuelve null si no lo encuentras
    }

    /**
     * Actualiza un animal existente en la base de datos.
     * Es llamado desde panelRegistroAnimales cuando está en modo edición.
     * DEBES IMPLEMENTAR ESTO.
     */
    public void actualizarAnimal(String codigoOriginal, java.sql.Timestamp fecha, String sexo, String raza,
            String pesoNac, String peso, String idMadre, String idPadre, String estado) throws SQLException {

        String sql = "{CALL sp_actualizar_animal(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        // 2. Usamos try-with-resources para asegurar que el CallableStatement se cierre
        // solo.
        try (CallableStatement cs = connection.prepareCall(sql)) {

            // 3. Asignamos los parámetros en el orden correcto.
            cs.setString(1, codigoOriginal);
            cs.setTimestamp(2, fecha); // Usamos setTimestamp para el tipo DATETIME
            cs.setString(3, sexo);
            cs.setString(4, raza);

            // Los pesos se pasan como String. Si el campo está vacío, enviamos NULL.
            cs.setString(5, pesoNac.trim().isEmpty() ? null : pesoNac.trim());
            cs.setString(6, peso.trim().isEmpty() ? null : peso.trim());

            // Hacemos lo mismo para los IDs de los padres.
            cs.setString(7, idMadre.trim().isEmpty() ? null : idMadre.trim());
            cs.setString(8, idPadre.trim().isEmpty() ? null : idPadre.trim());

            cs.setString(9, estado);

            // 4. Ejecutamos el procedimiento.
            cs.executeUpdate();
        }
        // El 'throws SQLException' en la firma del método se encargará de notificar al
        // panel si algo sale mal.
    }

    public Map<String, Object> obtenerDatosParaInformeIndividual(String idAnimal) {
        Map<String, Object> informeData = new HashMap<>();

        // 1. Obtener Datos Básicos
        try (CallableStatement cst = connection.prepareCall("{CALL sp_informe_datos_basicos(?)}")) {
            cst.setString(1, idAnimal);
            ResultSet rs = cst.executeQuery();
            if (rs.next()) {
                Map<String, String> datosBasicos = new HashMap<>();
                datosBasicos.put("Código / ID", rs.getString("codigo"));
                String sexo = rs.getString("sexo");
                datosBasicos.put("Sexo", sexo);
                datosBasicos.put("Raza", rs.getString("raza"));
                datosBasicos.put("Fecha de Nacimiento",
                        new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("fecha_nacimiento")));
                datosBasicos.put("Estado Actual", rs.getString("estado"));
                informeData.put("datosBasicos", datosBasicos);

                // Si es hembra, buscar producción y crías
                if ("F".equalsIgnoreCase(sexo)) {
                    // 2. Obtener Resumen de Producción
                    try (CallableStatement cstProd = connection.prepareCall("{CALL sp_informe_produccion_total(?)}")) {
                        cstProd.setString(1, idAnimal);
                        ResultSet rsProd = cstProd.executeQuery();
                        if (rsProd.next()) {
                            Map<String, Object> datosProduccion = new HashMap<>();
                            datosProduccion.put("totalLitros", rsProd.getDouble("total_litros"));
                            datosProduccion.put("diasEnProduccion", rsProd.getInt("dias_en_produccion"));
                            informeData.put("produccion", datosProduccion);
                        }
                    }

                    // 4. Contar Crías
                    try (CallableStatement cstCrias = connection.prepareCall("{CALL sp_informe_conteo_crias(?)}")) {
                        cstCrias.setString(1, idAnimal);
                        ResultSet rsCrias = cstCrias.executeQuery();
                        if (rsCrias.next()) {
                            Map<String, Integer> datosReproductivos = new HashMap<>();
                            datosReproductivos.put("cantidadCrias", rsCrias.getInt("cantidad_crias"));
                            informeData.put("reproductivo", datosReproductivos);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al obtener datos básicos: " + e.getMessage());
            return null;
        }

        // 3. Obtener Historial de Salud
        List<Map<String, String>> eventosSalud = new ArrayList<>();
        try (CallableStatement cst = connection.prepareCall("{CALL sp_informe_historial_salud(?)}")) {
            cst.setString(1, idAnimal);
            ResultSet rs = cst.executeQuery();
            while (rs.next()) {
                Map<String, String> evento = new HashMap<>();
                evento.put("fecha", new SimpleDateFormat("dd/MM/yyyy").format(rs.getTimestamp("fecha")));
                evento.put("tipo", rs.getString("tipo"));
                evento.put("producto", rs.getString("nombre_producto"));
                // Manejar dosis nula
                String dosis = rs.getString("dosis");
                evento.put("dosis", dosis == null ? "N/A" : dosis);
                eventosSalud.add(evento);
            }
            informeData.put("eventosSalud", eventosSalud);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al obtener historial de salud: " + e.getMessage());
        }

        return informeData;
    }

    // public Map<String, Object> obtenerDatosParaInformeIndividual(String idAnimal)
    // {
    // // Cuando tengas tu procedimiento almacenado, esta será la única parte que
    // // cambiarás.
    // // El resto de la lógica en el panel funcionará igual.

    // Map<String, Object> informeData = new HashMap<>();

    // // 1. Datos Básicos del Animal
    // Map<String, String> datosBasicos = new HashMap<>();
    // datosBasicos.put("Código / ID", idAnimal);
    // datosBasicos.put("Sexo", "F"); // Ejemplo
    // datosBasicos.put("Raza", "Holstein");
    // datosBasicos.put("Fecha de Nacimiento", "2022-01-15");
    // datosBasicos.put("Estado Actual", "ACTIVO");
    // informeData.put("datosBasicos", datosBasicos);

    // // 2. Datos de Producción (solo si es hembra)
    // if ("F".equals(datosBasicos.get("Sexo"))) {
    // Map<String, Object> datosProduccion = new HashMap<>();
    // datosProduccion.put("totalLitros", 12540.5); // Ejemplo
    // datosProduccion.put("diasEnProduccion", 305);
    // informeData.put("produccion", datosProduccion);
    // }

    // // 3. Datos de Salud (Historial)
    // List<Map<String, String>> eventosSalud = new ArrayList<>();
    // eventosSalud.add(new HashMap<String, String>() {
    // {
    // put("fecha", "2023-11-20");
    // put("tipo", "VACUNACION");
    // put("producto", "Bovisan");
    // put("dosis", "5 ml");
    // }
    // });
    // eventosSalud.add(new HashMap<String, String>() {
    // {
    // put("fecha", "2024-03-10");
    // put("tipo", "DESPARASITACION");
    // put("producto", "Ivermectina");
    // put("dosis", "10 ml");
    // }
    // });
    // eventosSalud.add(new HashMap<String, String>() {
    // {
    // put("fecha", "2024-05-02");
    // put("tipo", "TRATAMIENTO");
    // put("producto", "Antibiótico X");
    // put("dosis", "20 ml");
    // }
    // });
    // informeData.put("eventosSalud", eventosSalud);

    // // 4. Datos Reproductivos
    // Map<String, Integer> datosReproductivos = new HashMap<>();
    // datosReproductivos.put("cantidadCrias", 2); // Ejemplo
    // informeData.put("reproductivo", datosReproductivos);

    // return informeData;
    // }

    public Map<String, Object> obtenerDatosParaInformeGeneral(java.util.Date fechaDesde, java.util.Date fechaHasta) {
        Map<String, Object> informeData = new HashMap<>();

        try (CallableStatement cst = connection.prepareCall("{CALL sp_informe_general_hato(?, ?)}")) {
            // Convertir java.util.Date a java.sql.Date
            cst.setDate(1, new java.sql.Date(fechaDesde.getTime()));
            cst.setDate(2, new java.sql.Date(fechaHasta.getTime()));

            ResultSet rs = cst.executeQuery();
            if (rs.next()) {
                // Resumen Inventario
                Map<String, Integer> inventario = new HashMap<>();
                inventario.put("Total Animales Activos", rs.getInt("total_animales_activos"));
                inventario.put("Hembras", rs.getInt("total_hembras"));
                inventario.put("Machos", rs.getInt("total_machos"));
                informeData.put("resumenInventario", inventario);

                // Resumen Producción
                Map<String, Object> produccion = new HashMap<>();
                produccion.put("totalLitrosPeriodo", rs.getDouble("total_litros_periodo"));
                informeData.put("resumenProduccion", produccion);

                // Resumen Salud
                Map<String, Integer> salud = new HashMap<>();
                salud.put("Vacunaciones", rs.getInt("total_vacunaciones"));
                salud.put("Desparasitaciones", rs.getInt("total_desparasitaciones"));
                salud.put("Tratamientos", rs.getInt("total_tratamientos"));
                informeData.put("resumenSalud", salud);

                // Resumen Salidas
                Map<String, Integer> salidas = new HashMap<>();
                salidas.put("Ventas", rs.getInt("total_ventas"));
                salidas.put("Muertes", rs.getInt("total_muertes"));
                informeData.put("resumenSalidas", salidas);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar informe general: " + e.getMessage());
            return null;
        }

        return informeData;
    }
    // public Map<String, Object> obtenerDatosParaInformeGeneral(java.util.Date
    // fechaDesde, java.util.Date fechaHasta) {
    // // Cuando tengas tus procedimientos, aquí llamarás a la base de datos
    // // pasando las fechas como parámetros.

    // Map<String, Object> informeData = new HashMap<>();

    // // 1. Resumen del Inventario
    // Map<String, Integer> resumenInventario = new HashMap<>();
    // resumenInventario.put("Total Animales Activos", 152);
    // resumenInventario.put("Hembras", 120);
    // resumenInventario.put("Machos", 32);
    // informeData.put("resumenInventario", resumenInventario);

    // // 2. Resumen de Producción en el Periodo
    // Map<String, Object> resumenProduccion = new HashMap<>();
    // resumenProduccion.put("totalLitrosPeriodo", 45870.5);
    // resumenProduccion.put("promedioDiarioPorVaca", 18.2);
    // informeData.put("resumenProduccion", resumenProduccion);

    // // 3. Resumen de Salud en el Periodo
    // Map<String, Integer> resumenSalud = new HashMap<>();
    // resumenSalud.put("Vacunaciones", 85);
    // resumenSalud.put("Desparasitaciones", 152);
    // resumenSalud.put("Tratamientos", 23);
    // informeData.put("resumenSalud", resumenSalud);

    // // 4. Resumen de Salidas en el Periodo
    // Map<String, Integer> resumenSalidas = new HashMap<>();
    // resumenSalidas.put("Ventas", 12);
    // resumenSalidas.put("Muertes", 3);
    // informeData.put("resumenSalidas", resumenSalidas);

    // return informeData;
    // }
}