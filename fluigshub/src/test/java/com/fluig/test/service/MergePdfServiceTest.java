package com.fluig.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Before;
import org.junit.Test;
import org.xhtmlrenderer.css.parser.property.PrimitivePropertyBuilders.Color;

import com.fluig.service.MergePdfService;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class MergePdfServiceTest {
    

    @Before
    public void setUp() {
        // Inicializa a classe que será testada
        MergePdfService mergePdfService = new MergePdfService();
    }

    @Test
    public void testConvertImageToPdf() throws Exception {
        // Criando uma imagem dinâmica
        BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(java.awt.Color.BLUE);
        graphics.fillRect(0, 0, 100, 100);
        graphics.dispose();

        // Convertendo a imagem para um InputStream
        ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", imageOutputStream);
        InputStream imageStream = new ByteArrayInputStream(imageOutputStream.toByteArray());

        // Convertendo a imagem para PDF
        InputStream pdfStream = MergePdfService.convertImageToPdf(imageStream);

        assertNotNull("O PDF gerado a partir da imagem não deve ser nulo.", pdfStream);
        assertTrue("O PDF deve conter dados.", pdfStream.available() > 0);

        // Verifica se o PDF gerado é válido
        byte[] pdfBytes = pdfStream.readAllBytes();
        assertTrue("O PDF gerado deve ser válido.", isValidPdf(pdfBytes));
    }
    
    private InputStream createSamplePdf(String text) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);
        document.open();
        document.add(new Paragraph(text));
        document.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private byte[] createEmptyPdf() throws IOException {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); PDDocument document = new PDDocument()) {
            document.addPage(new PDPage());
            document.save(outputStream);
            return outputStream.toByteArray();
        }   
    }

    private boolean isValidPdf(byte[] pdfBytes) {
        try {
            PdfReader reader = new PdfReader(pdfBytes);
            reader.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    public void testMergePdfs() throws Exception {
        MergePdfService mergePdfService = new MergePdfService();

        // Criando dois PDFs de teste
        InputStream pdf1 = createSamplePdf("PDF 1");
        InputStream pdf2 = createSamplePdf("PDF 2");

        // Mesclar os PDFs
        byte[] mergedPdf = mergePdfService.executeService(List.of(pdf1, pdf2));

        assertNotNull("O PDF mesclado não deve ser nulo.", mergedPdf);
        assertTrue("O PDF mesclado deve conter dados.", mergedPdf.length > 0);
        assertTrue("O PDF mesclado deve ser válido.", isValidPdf(mergedPdf));
    }

   

    @Test
    public void testConvertImageToPdfWithEmptyStream() {
        InputStream emptyStream = new ByteArrayInputStream(new byte[0]);

        Exception exception = assertThrows(IOException.class, () -> {
            MergePdfService.convertImageToPdf(emptyStream);
        });

        assertTrue("não é uma imagem suportada", exception.getMessage().contains("não é uma imagem suportada"));
    }

    @Test
    public void testConvertImageToPdfWithInvalidImage() {
        InputStream invalidStream = new ByteArrayInputStream("Isso não é uma imagem!".getBytes());

        Exception exception = assertThrows(IOException.class, () -> {
            MergePdfService.convertImageToPdf(invalidStream);
        });

        assertEquals("O arquivo fornecido não é uma imagem suportada (JPG, PNG, etc.) ou está corrompido.", exception.getMessage());
    }

    @Test
    public void testMergePdfsWithInvalidFile() throws Exception {
        InputStream pdf1 = createSamplePdf("PDF-TEST");
        byte[] invalidFile = "Isso não é um PDF!".getBytes();

        List<InputStream> pdfList = Arrays.asList(
            pdf1,
            new ByteArrayInputStream(invalidFile)
        );

        MergePdfService mergePdfService = new MergePdfService();

        Exception exception = assertThrows(IOException.class, () -> {
            mergePdfService.executeService(pdfList);
        });

        assertTrue("A exceção deve indicar que o arquivo não é um PDF.", 
            exception.getMessage().contains("O arquivo não é um PDF válido nem uma imagem suportada."));
    }


    @Test
    public void testMergePdfsWithCorruptedPdf() throws Exception {
        byte[] corruptedPdf = {0x25, 0x50, 0x44, 0x46}; // Apenas um cabeçalho PDF incompleto

        List<InputStream> pdfList = Arrays.asList(
            new ByteArrayInputStream(createEmptyPdf()),
            new ByteArrayInputStream(corruptedPdf)
        );

        MergePdfService mergePdfService = new MergePdfService();

        Exception exception = assertThrows(IOException.class, () -> {
            mergePdfService.executeService(pdfList);
        });

        assertTrue("A exceção deve indicar que o arquivo está corrompido.", 
            exception.getMessage().contains("O arquivo não é um PDF válido nem uma imagem suportada."));
    }

}
