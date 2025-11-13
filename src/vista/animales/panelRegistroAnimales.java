package vista.animales;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import com.toedter.calendar.JDateChooser;
import controlador.Controlador;
import controlador.FontLoader;
import java.sql.Date;

public class panelRegistroAnimales extends JPanel {

    Controlador controlador;

    private JTextField txtCodigo;
    private JDateChooser dcFechaNacimiento;
    private JComboBox<String> cbSexo;
    private JTextField txtRaza;
    private JTextField txtPesoNacimiento;
    private JTextField txtPeso;
    private JTextField txtIdMadre;
    private JTextField txtIdPadre;
    private JComboBox<String> cbEstado;

     // Para saber si estamos editando o guardando uno nuevo
    private boolean enModoEdicion = false;
    // Para guardar el código original del animal que se está editando
    private String codigoOriginal;


    // --- Fuentes estandarizadas ---
    private final Font FONT_SUBTITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Bold.ttf", 24f);
    private final Font FONT_LABEL = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 16f);
    private final Font FONT_INPUT = FontLoader.loadFont("/resources/fonts/Montserrat-Light.ttf", 16f);
    private final Font FONT_BOTON = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 14f);

    public panelRegistroAnimales(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
    }

    public JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 246, 248));

        // CAMBIO: Se aplica la fuente estándar para el subtítulo
        JLabel title = new JLabel("Registro de Animales");
        title.setHorizontalAlignment(SwingConstants.LEFT);
        title.setBorder(new EmptyBorder(24, 32, 8, 32));
        title.setFont(controlador.estilos.FONT_TITULO_MENU.deriveFont(Font.BOLD, 26f));
        contentPanel.add(title, BorderLayout.NORTH);

        JPanel wrapper = new JPanel(null);
        wrapper.setOpaque(false);
        contentPanel.add(wrapper, BorderLayout.CENTER);

        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 223, 230), 1),
            new EmptyBorder(20, 24, 24, 24)
        ));
        wrapper.add(card);

        wrapper.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = wrapper.getWidth();
                int x = Math.max(32, (w - 980) / 2);
                card.setBounds(x, 20, 980, 400); // Ajustada la altura para que encaje mejor
            }
        });

        // CAMBIO: Se usa FONT_LABEL directamente
        int col1X = 30;  int col1W = 400;
        int col2X = 500; int col2W = 400;
        int labelW = 185; int fieldH = 34; int rowY = 20; int rowGap = 46;

        JLabel lCodigo = new JLabel("Código:");
        lCodigo.setFont(FONT_LABEL);
        lCodigo.setBounds(col1X, rowY, labelW, 24);
        card.add(lCodigo);
        txtCodigo = new JTextField();
        txtCodigo.setFont(FONT_INPUT); // CAMBIO: Aplicada la fuente de input
        txtCodigo.setBounds(col1X + labelW, rowY, col1W - labelW, fieldH);
        card.add(txtCodigo);

        JLabel lFecha = new JLabel("Fecha de nacimiento:");
        lFecha.setFont(FONT_LABEL);
        lFecha.setBounds(col2X, rowY, labelW, 24);
        card.add(lFecha);
        dcFechaNacimiento = new JDateChooser();
        dcFechaNacimiento.setDateFormatString("yyyy-MM-dd");
        dcFechaNacimiento.setFont(FONT_INPUT); // CAMBIO: Aplicada la fuente de input
        dcFechaNacimiento.setBounds(col2X + labelW, rowY, col2W - labelW, fieldH);
        card.add(dcFechaNacimiento);

        rowY += rowGap;
        JLabel lSexo = new JLabel("Sexo:");
        lSexo.setFont(FONT_LABEL);
        lSexo.setBounds(col1X, rowY, labelW, 24);
        card.add(lSexo);
        cbSexo = new JComboBox<>(new String[]{"M", "F"});
        cbSexo.setFont(FONT_INPUT); // CAMBIO: Aplicada la fuente de input
        cbSexo.setBounds(col1X + labelW, rowY, col1W - labelW, fieldH);
        card.add(cbSexo);

        JLabel lRaza = new JLabel("Raza:");
        lRaza.setFont(FONT_LABEL);
        lRaza.setBounds(col2X, rowY, labelW, 24);
        card.add(lRaza);
        txtRaza = new JTextField();
        txtRaza.setFont(FONT_INPUT); // CAMBIO: Aplicada la fuente de input
        txtRaza.setBounds(col2X + labelW, rowY, col2W - labelW, fieldH);
        card.add(txtRaza);

        rowY += rowGap;
        JLabel lPesoNac = new JLabel("Peso nacimiento (kg):");
        lPesoNac.setFont(FONT_LABEL);
        lPesoNac.setBounds(col1X, rowY, labelW, 24);
        card.add(lPesoNac);
        txtPesoNacimiento = new JTextField();
        txtPesoNacimiento.setFont(FONT_INPUT); // CAMBIO: Aplicada la fuente de input
        txtPesoNacimiento.setBounds(col1X + labelW, rowY, col1W - labelW, fieldH);
        card.add(txtPesoNacimiento);

        JLabel lPeso = new JLabel("Peso actual (kg):");
        lPeso.setFont(FONT_LABEL);
        lPeso.setBounds(col2X, rowY, labelW, 24);
        card.add(lPeso);
        txtPeso = new JTextField();
        txtPeso.setFont(FONT_INPUT); // CAMBIO: Aplicada la fuente de input
        txtPeso.setBounds(col2X + labelW, rowY, col2W - labelW, fieldH);
        card.add(txtPeso);

        rowY += rowGap;
        JLabel lMadre = new JLabel("ID madre:");
        lMadre.setFont(FONT_LABEL);
        lMadre.setBounds(col1X, rowY, labelW, 24);
        card.add(lMadre);
        txtIdMadre = new JTextField();
        txtIdMadre.setFont(FONT_INPUT); // CAMBIO: Aplicada la fuente de input
        txtIdMadre.setBounds(col1X + labelW, rowY, col1W - labelW, fieldH);
        card.add(txtIdMadre);

        JLabel lPadre = new JLabel("ID padre:");
        lPadre.setFont(FONT_LABEL);
        lPadre.setBounds(col2X, rowY, labelW, 24);
        card.add(lPadre);
        txtIdPadre = new JTextField();
        txtIdPadre.setFont(FONT_INPUT); // CAMBIO: Aplicada la fuente de input
        txtIdPadre.setBounds(col2X + labelW, rowY, col2W - labelW, fieldH);
        card.add(txtIdPadre);

        rowY += rowGap;
        JLabel lEstado = new JLabel("Estado:");
        lEstado.setFont(FONT_LABEL);
        lEstado.setBounds(col1X, rowY, labelW, 24);
        card.add(lEstado);
        cbEstado = new JComboBox<>(new String[]{"ACTIVO", "VENDIDO", "MUERTO"});
        cbEstado.setFont(FONT_INPUT); // CAMBIO: Aplicada la fuente de input
        cbEstado.setBounds(col1X + labelW, rowY, col1W - labelW, fieldH);
        card.add(cbEstado);

        JButton btnGuardar = new JButton("Guardar animal");
        btnGuardar.setBackground(controlador.estilos.COLOR_GUARDAR); // CAMBIO: Color estandarizado
        btnGuardar.setFont(FONT_BOTON);
        btnGuardar.setIcon(new ImageIcon("src/resources/images/icon-guardar.png"));
        btnGuardar.setHorizontalTextPosition(SwingConstants.LEFT);
        btnGuardar.setForeground(Color.WHITE);
        
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setIcon(new ImageIcon("src/resources/images/icon-limpiar.png"));
        btnLimpiar.setHorizontalTextPosition(SwingConstants.LEFT);

        btnLimpiar.setBackground(controlador.estilos.COLOR_LIMPIAR); // CAMBIO: Color estandarizado
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFont(FONT_BOTON);

        int btnW = 180; int btnH = 45; int btnGap = 12;
        final int baseY = rowY + rowGap + 20;
        card.add(btnLimpiar);
        card.add(btnGuardar);

        card.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                int cw = card.getWidth();
                btnGuardar.setBounds(cw - 24 - btnW, baseY, btnW, btnH);
                btnLimpiar.setBounds(cw - 24 - btnW - btnGap - btnW, baseY, btnW, btnH);
            }
        });

        btnGuardar.addActionListener(e -> guardarAnimal());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        return contentPanel;
    }

    public void cargarDatosParaEdicion(String codigo, java.sql.Date fechaNacimiento, String sexo, String raza, 
                                     String pesoNacimiento, String pesoActual, String idMadre, String idPadre, String estado) {
        
        txtCodigo.setText(codigo);
        txtCodigo.setEditable(false); // No se debe poder editar el código (PK)

        dcFechaNacimiento.setDate(new java.util.Date(fechaNacimiento.getTime()));
        cbSexo.setSelectedItem(sexo);
        txtRaza.setText(raza);
        txtPesoNacimiento.setText(pesoNacimiento);
        txtPeso.setText(pesoActual);
        txtIdMadre.setText(idMadre);
        txtIdPadre.setText(idPadre);
        cbEstado.setSelectedItem(estado);

        // Activamos el modo edición
        this.enModoEdicion = true;
        this.codigoOriginal = codigo; // Guardamos el código original
    }

    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtCodigo.setEditable(true); // Se puede editar el código al crear uno nuevo
        dcFechaNacimiento.setDate(new java.util.Date());
        cbSexo.setSelectedIndex(0);
        txtRaza.setText("");
        txtPesoNacimiento.setText("");
        txtPeso.setText("");
        txtIdMadre.setText("");
        txtIdPadre.setText("");
        cbEstado.setSelectedIndex(0);

        // ---- AÑADIDO ----
        // Desactivamos el modo edición al limpiar
        this.enModoEdicion = false;
        this.codigoOriginal = null;
    }

    private void guardarAnimal() {
        // Obtenemos los datos del formulario (sin cambios)
        String codigo = txtCodigo.getText().trim();
        java.util.Date fecha = dcFechaNacimiento.getDate();
        String sexo = (String) cbSexo.getSelectedItem();
        String raza = txtRaza.getText().trim();
        String pesoNacimiento = txtPesoNacimiento.getText().trim();
        String peso = txtPeso.getText().trim();
        String idMadre = txtIdMadre.getText().trim();
        String idPadre = txtIdPadre.getText().trim();
        String estado = (String) cbEstado.getSelectedItem();

        if (codigo.isEmpty() || fecha == null) {
            JOptionPane.showMessageDialog(this, "El código y la fecha de nacimiento son obligatorios.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // ---- LÓGICA MODIFICADA ----
            if (enModoEdicion) {
                // Si estamos editando, llamamos al método para actualizar
                controlador.actualizarAnimal(
                    codigoOriginal, // Usamos el código original para la cláusula WHERE de SQL
                    new java.sql.Timestamp(fecha.getTime()),
                    sexo,
                    raza,
                    pesoNacimiento,
                    peso,
                    idMadre,
                    idPadre,
                    estado
                );
                JOptionPane.showMessageDialog(this, "Animal actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Si no, llamamos al método original para guardar un nuevo animal
                controlador.guardarAnimal(
                   codigo,
                   new Date(fecha.getTime()),
                   sexo,
                   raza,
                   pesoNacimiento,
                   peso,
                   idMadre,
                   idPadre,
                   estado
                );
                JOptionPane.showMessageDialog(this, "Animal registrado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            limpiarFormulario();
            // Opcional: Después de guardar, puedes volver a la tabla
            // controlador.animalesFrame.cambiarPanelContenido(controlador.animalesFrame.pMostrar.createContentPanel(...));
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar los datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}