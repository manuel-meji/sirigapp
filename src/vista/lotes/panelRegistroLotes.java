package vista.lotes;

import controlador.Controlador;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class panelRegistroLotes extends JPanel {

    private Controlador controlador;

    // --- Componentes de la UI ---
    private JLabel lblTitulo, lblNombre, lblEtapa, lblDescripcion;
    private JTextField txtNombre;
    private JComboBox<String> cmbEtapa;
    private JTextArea areaDescripcion;
    private JButton btnRegistrar, btnModificar, btnLimpiar;
    private JTable tablaLotes;
    private DefaultTableModel modeloTablaLotes;
    private JScrollPane scrollPaneTabla, scrollPaneDescripcion;


    public panelRegistroLotes(Controlador controlador) {
        this.controlador = controlador;
        initComponents();
        // Aquí podrías llamar a un método para cargar los datos iniciales en la tabla
        // cargarLotesEnTabla();
    }

    private void initComponents() {
        // Usamos un layout nulo para posicionar los componentes manualmente, como en tu ejemplo
        setLayout(null);
        setBackground(Color.WHITE);

        // Título del panel
        lblTitulo = new JLabel("Registro y Administración de Lotes de Ganado");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setBounds(50, 20, 600, 30);
        add(lblTitulo);

        // Formulario de entrada
        lblNombre = new JLabel("Nombre del Lote:");
        lblNombre.setFont(new Font("Arial", Font.PLAIN, 16));
        lblNombre.setBounds(50, 80, 150, 25);
        add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(200, 80, 250, 25);
        add(txtNombre);

        lblEtapa = new JLabel("Etapa del Lote:");
        lblEtapa.setFont(new Font("Arial", Font.PLAIN, 16));
        lblEtapa.setBounds(50, 120, 150, 25);
        add(lblEtapa);

        // Combo box para las etapas
        String[] etapas = {"Seleccione una etapa", "Cría", "Levante", "Ceba", "Producción"};
        cmbEtapa = new JComboBox<>(etapas);
        cmbEtapa.setBounds(200, 120, 250, 25);
        add(cmbEtapa);

        lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Arial", Font.PLAIN, 16));
        lblDescripcion.setBounds(50, 160, 150, 25);
        add(lblDescripcion);

        areaDescripcion = new JTextArea();
        scrollPaneDescripcion = new JScrollPane(areaDescripcion);
        scrollPaneDescripcion.setBounds(200, 160, 250, 80);
        add(scrollPaneDescripcion);

        // Botones de acción
        btnRegistrar = new JButton("Registrar Lote");
        btnRegistrar.setBounds(500, 80, 150, 30);
        btnRegistrar.setBackground(new Color(0x2BA76B)); // Verde
        btnRegistrar.setForeground(Color.WHITE);
        add(btnRegistrar);
        
        btnModificar = new JButton("Modificar Lote");
        btnModificar.setBounds(500, 120, 150, 30);
        btnModificar.setBackground(new Color(0xFF054FBE)); // Azul
        btnModificar.setForeground(Color.WHITE);
        add(btnModificar);

        btnLimpiar = new JButton("Limpiar Campos");
        btnLimpiar.setBounds(500, 160, 150, 30);
        add(btnLimpiar);


        // Tabla para mostrar los lotes registrados
        String[] columnas = {"ID Lote", "Nombre", "Etapa", "Descripción"};
        modeloTablaLotes = new DefaultTableModel(columnas, 0);
        tablaLotes = new JTable(modeloTablaLotes);
        scrollPaneTabla = new JScrollPane(tablaLotes);
        
        // El tamaño de la tabla debe ajustarse al espacio disponible
        // El ancho es 1366 (frame) - 300 (menu) = 1066. Le damos un margen.
        scrollPaneTabla.setBounds(50, 280, 966, 350); 
        add(scrollPaneTabla);
        
        // AQUÍ AGREGARÍAS LOS ACTION LISTENERS PARA LOS BOTONES
        // btnRegistrar.addActionListener(e -> registrarLote());
    }
    
    // Aquí irían los métodos para interactuar con el controlador y la base de datos
    /*
    private void registrarLote() {
        String nombre = txtNombre.getText();
        String etapa = cmbEtapa.getSelectedItem().toString();
        String descripcion = areaDescripcion.getText();
        
        // Validaciones...
        
        // Llamar al controlador para que guarde los datos
        // controlador.registrarNuevoLote(nombre, etapa, descripcion);
        
        // Actualizar la tabla
        // cargarLotesEnTabla();
    }
    
    public void cargarLotesEnTabla() {
        // Limpiar tabla
        modeloTablaLotes.setRowCount(0);
        
        // Pedir datos al controlador
        // ResultSet rs = controlador.obtenerTodosLosLotes();
        // try {
        //     while(rs.next()) {
        //         Object[] fila = {
        //             rs.getInt("id_lote"),
        //             rs.getString("nombre"),
        //             rs.getString("etapa"),
        //             rs.getString("descripcion")
        //         };
        //         modeloTablaLotes.addRow(fila);
        //     }
        // } catch (SQLException e) {
        //     // Manejar error
        // }
    }
    */
}