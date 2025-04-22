package com.fluig.test.service;

import com.fluig.model.HtmlToPdfModel;
import com.fluig.service.HtmlToPdfService;
import com.itextpdf.text.pdf.PdfReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class HtmlToPdfServiceTest {

    public HtmlToPdfService htmlToPdfService;

    @Before
    public void setUp() {
        htmlToPdfService = new HtmlToPdfService();
    }

    @Test
    public void testConvertHtmlToPdf() throws IOException {
        String html = "<html><body><h1>Test PDF</h1></body></html>";
        byte[] pdfBytes = htmlToPdfService.convertHtmlToPdf(html);

        assertNotNull(pdfBytes);
        assertTrue("O PDF gerado deve conter dados.", pdfBytes.length > 0);
    }

    @Test
    public void testExecuteServiceToReturnBytes() {
        String html = "<html><body><h1>Test PDF</h1></body></html>";
        String encodedHtml = Base64.getEncoder().encodeToString(html.getBytes());

        HtmlToPdfModel model = new HtmlToPdfModel();
        model.setParams(encodedHtml);

        byte[] pdfBytes = htmlToPdfService.executeServiceToReturnBytes(model);

        assertNotNull(pdfBytes);
        assertTrue( "O PDF gerado deve conter dados.", pdfBytes.length > 0);
    }

    
    @Test
    public void testConvertHtmlToPdfHandlesException() {
        String invalidHtml = "<html><body><h1>Test PDF</h1>"; // HTML inválido (falta fechamento)

        Exception exception = assertThrows(RuntimeException.class, () -> {
            htmlToPdfService.convertHtmlToPdf(invalidHtml);
        });

        assertTrue(exception.getMessage().contains("RuntimeException"));
    }


    @Test
    public void testGeneratedPdfIsValid() throws IOException {
        String html = "<html><body><h1>Test PDF</h1></body></html>";
        byte[] pdfBytes = htmlToPdfService.convertHtmlToPdf(html);

        assertNotNull("O PDF gerado não pode ser nulo.", pdfBytes);
        assertTrue("O PDF gerado deve conter dados.", pdfBytes.length > 0);

        // Verifica se o PDF pode ser aberto e não está corrompido
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfBytes)) {
            PdfReader reader = new PdfReader(inputStream);
            assertTrue("O PDF deve conter pelo menos uma página.", reader.getNumberOfPages() > 0);
            reader.close();
        } catch (IOException e) {
            fail("O PDF gerado está corrompido ou inválido.");
        }
    }

    @Test
    public void testDetectCorruptedPdf() {
        // Criando um array de bytes inválido (PDF corrompido)
        byte[] corruptedPdf = "PDF_CORROMPIDO".getBytes();

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(corruptedPdf)) {
            PdfReader reader = new PdfReader(inputStream);
            reader.close();
            fail("O teste deveria falhar, pois o PDF está corrompido.");
        } catch (IOException e) {
            assertTrue("Exceção esperada ao tentar abrir um PDF inválido.", e.getMessage().contains("PDF"));
        }
    }

}
