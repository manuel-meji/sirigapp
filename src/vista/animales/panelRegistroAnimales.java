package vista.animales;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
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

public class panelRegistroAnimales  extends JPanel{

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

    
    // --- Fuentes ---
    private final Font FONT_TITULO_MENU = FontLoader.loadFont("/resources/fonts/Montserrat-Black.ttf", 48f);
    private final Font FONT_BOTON_MENU = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 18f);

    public panelRegistroAnimales(Controlador controlador){
        this.controlador = controlador;
        setLayout(new BorderLayout());
    }

    public JPanel createContentPanel() {
        
        JPanel contentPanel = new JPanel();
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(new Color(245, 246, 248));

        JLabel title = new JLabel("Registro de Animales");
        title.setHorizontalAlignment(SwingConstants.LEFT);
        title.setBorder(new EmptyBorder(24, 32, 8, 32));
        title.setFont(FONT_BOTON_MENU.deriveFont(Font.BOLD, 26f));
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
                card.setBounds(x, 20, 980, 500);
            }
        });

        Font labelFont = FONT_BOTON_MENU.deriveFont(Font.PLAIN, 16f);
        int col1X = 30;  int col1W = 380;
        int col2X = 500; int col2W = 380;
        int labelW = 200; int fieldH = 34; int rowY = 20; int rowGap = 46;

        JLabel lCodigo = new JLabel("Código:"); lCodigo.setFont(labelFont);
        lCodigo.setBounds(col1X, rowY, labelW, 24); card.add(lCodigo);
        txtCodigo = new JTextField(); txtCodigo.setBounds(col1X + labelW, rowY, col1W - labelW, fieldH); card.add(txtCodigo);

        JLabel lFecha = new JLabel("Fecha de nacimiento:"); lFecha.setFont(labelFont);
        lFecha.setBounds(col2X, rowY, labelW, 24); card.add(lFecha);
        dcFechaNacimiento = new JDateChooser(); dcFechaNacimiento.setDateFormatString("yyyy-MM-dd");
        dcFechaNacimiento.setBounds(col2X + labelW, rowY, col2W - labelW, fieldH); card.add(dcFechaNacimiento);

        rowY += rowGap;
        JLabel lSexo = new JLabel("Sexo:"); lSexo.setFont(labelFont);
        lSexo.setBounds(col1X, rowY, labelW, 24); card.add(lSexo);
        cbSexo = new JComboBox<>(new String[] {"M", "F"}); cbSexo.setBounds(col1X + labelW, rowY, col1W - labelW, fieldH); card.add(cbSexo);

        JLabel lRaza = new JLabel("Raza:"); lRaza.setFont(labelFont);
        lRaza.setBounds(col2X, rowY, labelW, 24); card.add(lRaza);
        txtRaza = new JTextField(); txtRaza.setBounds(col2X + labelW, rowY, col2W - labelW, fieldH); card.add(txtRaza);

        rowY += rowGap;
        JLabel lPesoNac = new JLabel("Peso nacimiento (kg):"); lPesoNac.setFont(labelFont);
        lPesoNac.setBounds(col1X, rowY, labelW, 24); card.add(lPesoNac);
        txtPesoNacimiento = new JTextField(); txtPesoNacimiento.setBounds(col1X + labelW, rowY, col1W - labelW, fieldH); card.add(txtPesoNacimiento);

        JLabel lPeso = new JLabel("Peso actual (kg):"); lPeso.setFont(labelFont);
        lPeso.setBounds(col2X, rowY, labelW, 24); card.add(lPeso);
        txtPeso = new JTextField(); txtPeso.setBounds(col2X + labelW, rowY, col2W - labelW, fieldH); card.add(txtPeso);

        rowY += rowGap;
        JLabel lMadre = new JLabel("ID madre:"); lMadre.setFont(labelFont);
        lMadre.setBounds(col1X, rowY, labelW, 24); card.add(lMadre);
        txtIdMadre = new JTextField(); txtIdMadre.setBounds(col1X + labelW, rowY, col1W - labelW, fieldH); card.add(txtIdMadre);

        JLabel lPadre = new JLabel("ID padre:"); lPadre.setFont(labelFont);
        lPadre.setBounds(col2X, rowY, labelW, 24); card.add(lPadre);
        txtIdPadre = new JTextField(); txtIdPadre.setBounds(col2X + labelW, rowY, col2W - labelW, fieldH); card.add(txtIdPadre);

        rowY += rowGap;
        JLabel lEstado = new JLabel("Estado:"); lEstado.setFont(labelFont);
        lEstado.setBounds(col1X, rowY, labelW, 24); card.add(lEstado);
        cbEstado = new JComboBox<>(new String[] {"ACTIVO", "VENDIDO", "MUERTO"});
        cbEstado.setBounds(col1X + labelW, rowY, col1W - labelW, fieldH); card.add(cbEstado);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnLimpiar = new JButton("Limpiar");
        int btnW = 120; int btnH = 36; int btnGap = 12;
        final int baseY = rowY + rowGap + 10;
        card.add(btnLimpiar); card.add(btnGuardar);

        card.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                int cw = card.getWidth();
                btnGuardar.setBounds(cw - 24 - btnW, baseY, btnW, btnH);
                btnLimpiar.setBounds(cw - 24 - btnW - btnGap - btnW, baseY, btnW, btnH);
            }
        });

        btnGuardar.addActionListener(e -> guardarAnimal());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        contentPanel.revalidate();
        contentPanel.repaint();
        return contentPanel;
    }

    private void limpiarFormulario() {
        txtCodigo.setText("");
        dcFechaNacimiento.setDate(new java.util.Date());
        cbSexo.setSelectedIndex(0);
        txtRaza.setText("");
        txtPesoNacimiento.setText("");
        txtPeso.setText("");
        txtIdMadre.setText("");
        txtIdPadre.setText("");
        cbEstado.setSelectedIndex(0);
    }

    private void guardarAnimal() {
        String codigo = txtCodigo.getText().trim();
        java.util.Date fecha = dcFechaNacimiento.getDate();
        String sexo = (String) cbSexo.getSelectedItem();
        String raza = txtRaza.getText().trim();
        String pesoNacimiento = txtPesoNacimiento.getText().trim();
        String peso = txtPeso.getText().trim();
        String idMadre = txtIdMadre.getText().trim();
        String idPadre = txtIdPadre.getText().trim();
        String estado = (String) cbEstado.getSelectedItem();

        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el código del animal");
            return;
        }

        try {
            controlador.guardarAnimal(
                codigo,
                new java.sql.Timestamp(fecha.getTime()),
                sexo,
                raza,
                pesoNacimiento,
                peso,
                idMadre,
                idPadre,
                estado
            );
            JOptionPane.showMessageDialog(this, "Animal registrado correctamente");
            limpiarFormulario();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }

        
    }

}
