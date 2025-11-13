package vista.informes;

import controlador.Controlador;
import controlador.FontLoader;
import com.toedter.calendar.JDateChooser;

// Imports de Java Swing y AWT
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Imports de iText 5 (asegúrate de que la librería esté en tu proyecto)
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
//import com.itextpdf.text.Font;

// Imports de Java IO y Util
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class panelInformes extends JPanel {
    private Controlador controlador;
    private JRadioButton radioIndividual, radioGeneral;
    private JPanel optionsPanel;
    private CardLayout cardLayout;
    private JComboBox<String> cmbAnimal;
    private JDateChooser dcDesde, dcHasta;

    // --- Fuentes estandarizadas ---
    private final Font FONT_SUBTITULO = FontLoader.loadFont("/resources/fonts/Montserrat-Bold.ttf", 24f);
    private final Font FONT_LABEL = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 16f);
    private final Font FONT_INPUT = FontLoader.loadFont("/resources/fonts/Montserrat-Light.ttf", 16f);
    private final Font FONT_BOTON = FontLoader.loadFont("/resources/fonts/Montserrat-SemiBold.ttf", 14f);

    public panelInformes(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
        add(createContentPanel(), BorderLayout.CENTER);
    }

    public JPanel createContentPanel() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(245, 246, 248));

        JLabel title = new JLabel("Generación de Informes");
        title.setFont(controlador.estilos.FONT_TITULO.deriveFont(Font.BOLD, 26f));
        title.setBorder(new EmptyBorder(20, 24, 8, 24));
        content.add(title, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout(10, 20));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 24, 24, 24));

        JPanel typeSelectionPanel = createTypeSelectionPanel();
        card.add(typeSelectionPanel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        optionsPanel = new JPanel(cardLayout);
        optionsPanel.setOpaque(false);
        optionsPanel.add(createIndividualOptionsPanel(), "INDIVIDUAL");
        optionsPanel.add(createGeneralOptionsPanel(), "GENERAL");
        card.add(optionsPanel, BorderLayout.CENTER);

        JPanel generateButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        generateButtonPanel.setOpaque(false);
        JButton btnGenerar = new JButton("Generar PDF");
        btnGenerar.setIcon(new ImageIcon("src/resources/images/icon-pdf.png"));
        btnGenerar.setFont(FONT_BOTON);
        btnGenerar.setBackground(controlador.estilos.COLOR_GUARDAR);
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setPreferredSize(new Dimension(220, 45));
        generateButtonPanel.add(btnGenerar);
        card.add(generateButtonPanel, BorderLayout.SOUTH);

        content.add(card, BorderLayout.CENTER);

        radioIndividual.addActionListener(e -> cardLayout.show(optionsPanel, "INDIVIDUAL"));
        radioGeneral.addActionListener(e -> cardLayout.show(optionsPanel, "GENERAL"));
        btnGenerar.addActionListener(e -> procesarGeneracionInforme());

        actualizarBusquedaAnimal("");
        return content;
    }

    private JPanel createTypeSelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        panel.setBorder(createTitledPanelBorder(" 1. Seleccione el tipo de informe "));
        radioIndividual = new JRadioButton("Informe Individual por Animal");
        radioIndividual.setFont(FONT_INPUT);
        radioIndividual.setOpaque(false);
        radioIndividual.setSelected(true);
        radioGeneral = new JRadioButton("Informe General del Hato");
        radioGeneral.setFont(FONT_INPUT);
        radioGeneral.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        group.add(radioIndividual);
        group.add(radioGeneral);
        panel.add(radioIndividual);
        panel.add(radioGeneral);
        return panel;
    }

    private JPanel createIndividualOptionsPanel() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setBorder(createTitledPanelBorder(" 2. Configure los parámetros "));
        JLabel lblAnimal = new JLabel("Seleccione el Animal:");
        lblAnimal.setFont(FONT_LABEL);
        lblAnimal.setBounds(20, 40, 200, 30);
        panel.add(lblAnimal);
        cmbAnimal = new JComboBox<>();
        cmbAnimal.setFont(FONT_INPUT);
        cmbAnimal.setEditable(true);
        cmbAnimal.setBounds(220, 40, 300, 30);
        panel.add(cmbAnimal);
        JTextField editor = (JTextField) cmbAnimal.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                actualizarBusquedaAnimal(editor.getText());
            }
        });
        return panel;
    }

    private JPanel createGeneralOptionsPanel() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setBorder(createTitledPanelBorder(" 2. Configure los parámetros "));
        JLabel lblDesde = new JLabel("Fecha de Inicio:");
        lblDesde.setFont(FONT_LABEL);
        lblDesde.setBounds(20, 40, 150, 30);
        panel.add(lblDesde);
        dcDesde = new JDateChooser(new Date());
        dcDesde.setFont(FONT_INPUT);
        dcDesde.setDateFormatString("yyyy-MM-dd");
        dcDesde.setBounds(180, 40, 200, 30);
        panel.add(dcDesde);
        JLabel lblHasta = new JLabel("Fecha de Fin:");
        lblHasta.setFont(FONT_LABEL);
        lblHasta.setBounds(420, 40, 150, 30);
        panel.add(lblHasta);
        dcHasta = new JDateChooser(new Date());
        dcHasta.setFont(FONT_INPUT);
        dcHasta.setDateFormatString("yyyy-MM-dd");
        dcHasta.setBounds(580, 40, 200, 30);
        panel.add(dcHasta);
        return panel;
    }

    private TitledBorder createTitledPanelBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 223, 230)),
                title, TitledBorder.LEFT, TitledBorder.TOP, FONT_LABEL, new Color(50, 50, 50));
    }

    private void procesarGeneracionInforme() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar informe como...");
        fileChooser.setSelectedFile(new File("Informe.pdf"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (radioIndividual.isSelected()) {
                String animalSeleccionado = (String) cmbAnimal.getSelectedItem();
                if (animalSeleccionado == null || animalSeleccionado.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor, seleccione un animal.", "Validación",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                generarInformeIndividual(animalSeleccionado, fileToSave.getAbsolutePath());
            } else {
                Date fechaDesde = dcDesde.getDate();
                Date fechaHasta = dcHasta.getDate();
                if (fechaDesde == null || fechaHasta == null || fechaDesde.after(fechaHasta)) {
                    JOptionPane.showMessageDialog(this, "Por favor, seleccione un rango de fechas válido.",
                            "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                generarInformeGeneral(fechaDesde, fechaHasta, fileToSave.getAbsolutePath());
            }
        }
    }

    private void generarInformeIndividual(String idAnimal, String rutaDestino) {
        try {
            Map<String, Object> datosInforme = controlador.obtenerDatosParaInformeIndividual(idAnimal);
            if (datosInforme == null || datosInforme.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se pudieron obtener los datos para el animal: " + idAnimal,
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Map<String, String> datosBasicos = (Map<String, String>) datosInforme.get("datosBasicos");
            Map<String, Object> datosProduccion = (Map<String, Object>) datosInforme.get("produccion");
            List<Map<String, String>> eventosSalud = (List<Map<String, String>>) datosInforme.get("eventosSalud");
            Map<String, Integer> datosReproductivos = (Map<String, Integer>) datosInforme.get("reproductivo");

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(rutaDestino));
            document.open();

            com.itextpdf.text.Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            com.itextpdf.text.Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14,
                    new BaseColor(40, 40, 40));
            com.itextpdf.text.Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
            com.itextpdf.text.Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);

            Paragraph titulo = new Paragraph("Informe Individual de Animal", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(25);
            document.add(titulo);

            document.add(new Paragraph(
                    "Fecha del Informe: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), fontNormal));
            addEmptyLine(document, 1);

            Paragraph subtituloBasicos = new Paragraph("1. Datos Generales", fontSubtitulo);
            subtituloBasicos.setSpacingAfter(10);
            document.add(subtituloBasicos);
            for (Map.Entry<String, String> entry : datosBasicos.entrySet()) {
                Paragraph p = new Paragraph();
                p.add(new Phrase(entry.getKey() + ": ", fontBold));
                p.add(new Phrase(entry.getValue(), fontNormal));
                document.add(p);
            }
            addEmptyLine(document, 1);

            if (eventosSalud != null && !eventosSalud.isEmpty()) {
                Paragraph subtituloSalud = new Paragraph("2. Historial de Salud", fontSubtitulo);
                subtituloSalud.setSpacingAfter(10);
                document.add(subtituloSalud);
                PdfPTable tablaSalud = new PdfPTable(4);
                tablaSalud.setWidthPercentage(100);
                tablaSalud.setWidths(new float[] { 2f, 3f, 3f, 1.5f });
                tablaSalud.addCell(createHeaderCell("Fecha"));
                tablaSalud.addCell(createHeaderCell("Tipo de Evento"));
                tablaSalud.addCell(createHeaderCell("Producto"));
                tablaSalud.addCell(createHeaderCell("Dosis"));
                for (Map<String, String> evento : eventosSalud) {
                    tablaSalud.addCell(createBodyCell(evento.get("fecha"), Element.ALIGN_LEFT));
                    tablaSalud.addCell(createBodyCell(evento.get("tipo"), Element.ALIGN_LEFT));
                    tablaSalud.addCell(createBodyCell(evento.get("producto"), Element.ALIGN_LEFT));
                    tablaSalud.addCell(createBodyCell(evento.get("dosis"), Element.ALIGN_CENTER));
                }
                document.add(tablaSalud);
                addEmptyLine(document, 1);
            }

            if (datosProduccion != null) {
                Paragraph subtituloProd = new Paragraph("3. Registro de Producción", fontSubtitulo);
                subtituloProd.setSpacingAfter(10);
                document.add(subtituloProd);
                Paragraph pProd = new Paragraph();
                pProd.add(new Phrase("Total de Litros Registrados: ", fontBold));
                pProd.add(new Phrase(datosProduccion.get("totalLitros").toString() + " L", fontNormal));
                document.add(pProd);
                addEmptyLine(document, 1);
            }

            if (datosReproductivos != null) {
                Paragraph subtituloRep = new Paragraph("4. Historial Reproductivo", fontSubtitulo);
                subtituloRep.setSpacingAfter(10);
                document.add(subtituloRep);
                Paragraph pRep = new Paragraph();
                pRep.add(new Phrase("Número de Crías Registradas: ", fontBold));
                pRep.add(new Phrase(datosReproductivos.get("cantidadCrias").toString(), fontNormal));
                document.add(pRep);
            }

            document.close();
            JOptionPane.showMessageDialog(this, "Informe PDF generado exitosamente en:\n" + rutaDestino, "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (FileNotFoundException | DocumentException e) {
            JOptionPane.showMessageDialog(this, "Error al generar el PDF: " + e.getMessage(), "Error iText",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + e.getMessage(), "Error General",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // En panelInformes.java, REEMPLAZA este método completo:

    private void generarInformeGeneral(Date fechaDesde, Date fechaHasta, String rutaDestino) {
        try {
            Map<String, Object> datos = controlador.obtenerDatosParaInformeGeneral(fechaDesde, fechaHasta);
            if (datos == null || datos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se pudieron obtener datos para el informe general.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(rutaDestino));
            document.open();

            com.itextpdf.text.Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            com.itextpdf.text.Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14,
                    new BaseColor(40, 40, 40));
            com.itextpdf.text.Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
            com.itextpdf.text.Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            Paragraph titulo = new Paragraph("Informe General del Hato", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(15);
            document.add(titulo);

            Paragraph rangoFechas = new Paragraph(
                    "Periodo del Informe: " + sdf.format(fechaDesde) + " al " + sdf.format(fechaHasta), fontBold);
            rangoFechas.setAlignment(Element.ALIGN_CENTER);
            rangoFechas.setSpacingAfter(25);
            document.add(rangoFechas);

            // --- 1. Sección de Inventario ---
            Map<String, Integer> inventario = (Map<String, Integer>) datos.get("resumenInventario");
            if (inventario != null) {
                document.add(new Paragraph("1. Resumen de Inventario (a la fecha)", fontSubtitulo));
                addEmptyLine(document, 1);
                PdfPTable tablaInv = new PdfPTable(2);
                tablaInv.setWidthPercentage(60);
                tablaInv.setHorizontalAlignment(Element.ALIGN_LEFT);
                tablaInv.setWidths(new float[] { 4f, 2f });
                tablaInv.addCell(createHeaderCell("Concepto"));
                tablaInv.addCell(createHeaderCell("Cantidad"));
                for (Map.Entry<String, Integer> entry : inventario.entrySet()) {
                    tablaInv.addCell(createBodyCell(entry.getKey(), Element.ALIGN_LEFT));
                    tablaInv.addCell(createBodyCell(entry.getValue().toString(), Element.ALIGN_CENTER));
                }
                document.add(tablaInv);
                addEmptyLine(document, 1);
            }

            // --- 2. Sección de Producción ---
            Map<String, Object> produccion = (Map<String, Object>) datos.get("resumenProduccion");
            if (produccion != null) {
                document.add(new Paragraph("2. Resumen de Producción (en el periodo)", fontSubtitulo));
                addEmptyLine(document, 1);
                Paragraph pProdTotal = new Paragraph();
                pProdTotal.add(new Phrase("Total de Litros Registrados: ", fontBold));
                pProdTotal.add(new Phrase(produccion.get("totalLitrosPeriodo").toString() + " L", fontNormal));
                document.add(pProdTotal);

                // LÍNEA PROBLEMÁTICA ELIMINADA - El promedio no se calculaba en el SP.

                addEmptyLine(document, 1);
            }

            // --- 3. Sección de Salud ---
            Map<String, Integer> salud = (Map<String, Integer>) datos.get("resumenSalud");
            if (salud != null) {
                document.add(new Paragraph("3. Resumen de Eventos Sanitarios (en el periodo)", fontSubtitulo));
                addEmptyLine(document, 1);
                PdfPTable tablaSalud = new PdfPTable(2);
                tablaSalud.setWidthPercentage(60);
                tablaSalud.setHorizontalAlignment(Element.ALIGN_LEFT);
                tablaSalud.setWidths(new float[] { 4f, 2f });
                tablaSalud.addCell(createHeaderCell("Tipo de Evento"));
                tablaSalud.addCell(createHeaderCell("Nº de Registros"));
                for (Map.Entry<String, Integer> entry : salud.entrySet()) {
                    tablaSalud.addCell(createBodyCell(entry.getKey(), Element.ALIGN_LEFT));
                    tablaSalud.addCell(createBodyCell(entry.getValue().toString(), Element.ALIGN_CENTER));
                }
                document.add(tablaSalud);
                addEmptyLine(document, 1);
            }

            // <<<--- SECCIÓN AÑADIDA Y CORREGIDA ---
            // --- 4. Sección de Salidas ---
            Map<String, Integer> salidas = (Map<String, Integer>) datos.get("resumenSalidas");
            if (salidas != null) {
                document.add(new Paragraph("4. Resumen de Salidas (en el periodo)", fontSubtitulo));
                addEmptyLine(document, 1);
                PdfPTable tablaSalidas = new PdfPTable(2);
                tablaSalidas.setWidthPercentage(60);
                tablaSalidas.setHorizontalAlignment(Element.ALIGN_LEFT);
                tablaSalidas.setWidths(new float[] { 4f, 2f });
                tablaSalidas.addCell(createHeaderCell("Motivo de Salida"));
                tablaSalidas.addCell(createHeaderCell("Cantidad"));
                for (Map.Entry<String, Integer> entry : salidas.entrySet()) {
                    tablaSalidas.addCell(createBodyCell(entry.getKey(), Element.ALIGN_LEFT));
                    tablaSalidas.addCell(createBodyCell(entry.getValue().toString(), Element.ALIGN_CENTER));
                }
                document.add(tablaSalidas);
            }

            document.close();
            JOptionPane.showMessageDialog(this, "Informe general generado exitosamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error al generar el informe general: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    // --- Métodos de Ayuda ---

    private PdfPCell createHeaderCell(String text) {
        com.itextpdf.text.Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new BaseColor(80, 80, 80));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        return cell;
    }

    private PdfPCell createBodyCell(String text, int alignment) {
        com.itextpdf.text.Font font = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.DARK_GRAY);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        return cell;
    }

    private void addEmptyLine(Document document, int number) throws DocumentException {
        for (int i = 0; i < number; i++) {
            document.add(new Paragraph(" "));
        }
    }

    private void actualizarBusquedaAnimal(String filtro) {
        List<String> animales = controlador.buscarAnimales(filtro);
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cmbAnimal.getModel();
        String textoActual = ((JTextField) cmbAnimal.getEditor().getEditorComponent()).getText();
        model.removeAllElements();
        animales.forEach(model::addElement);
        ((JTextField) cmbAnimal.getEditor().getEditorComponent()).setText(textoActual);
        if (!animales.isEmpty() && !filtro.isEmpty()) {
            cmbAnimal.showPopup();
        } else {
            cmbAnimal.hidePopup();
        }
    }
}