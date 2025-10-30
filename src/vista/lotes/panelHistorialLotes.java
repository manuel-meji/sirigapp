package vista.lotes;

import controlador.Controlador;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class panelHistorialLotes extends JPanel {

    private Controlador controlador;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTablaHistorial;
    private JScrollPane scrollPaneTabla;

    public panelHistorialLotes(Controlador controlador) {
        this.controlador = controlador;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10)); // Usamos BorderLayout para que la tabla ocupe todo el espacio
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Añadimos márgenes
        setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Historial de Movimientos de Lotes");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitulo, BorderLayout.NORTH);

        // Tabla para mostrar el historial
        String[] columnas = {"ID Movimiento", "ID Lote", "Fecha", "Tipo de Movimiento", "Descripción"};
        modeloTablaHistorial = new DefaultTableModel(columnas, 0);
        tablaHistorial = new JTable(modeloTablaHistorial);
        scrollPaneTabla = new JScrollPane(tablaHistorial);

        add(scrollPaneTabla, BorderLayout.CENTER);

        // Aquí llamarías a un método para cargar los datos del historial
        // cargarHistorial();
    }
}