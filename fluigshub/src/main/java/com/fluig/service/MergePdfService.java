package com.fluig.service;


import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;


public class MergePdfService {

    public static InputStream convertImageToPdf(InputStream imageStream) throws IOException {

        BufferedImage bufferedImage = ImageIO.read(imageStream);
        if (bufferedImage == null) {
            throw new IOException("O arquivo fornecido não é uma imagem suportada (JPG, PNG, etc.) ou está corrompido.");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                PDImageXObject pdImage = JPEGFactory.createFromImage(document, bufferedImage);

                contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());
            }
            document.save(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

    public byte[] executeService(List<InputStream> files) throws IOException {
        System.out.println("EXECUTANDO O MERGE...");
        return mergePdfs(files);
    }

    private byte[] mergePdfs(List<InputStream> files) throws IOException {
        byte[] result;
        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDocumentMergeMode(PDFMergerUtility.DocumentMergeMode.OPTIMIZE_RESOURCES_MODE);
        Path tempFile = Files.createTempFile(Path.of(System.getProperty("java.io.tmpdir")), "temp", ".pdf");

        for (InputStream file : files) {
            byte[] fileBytes = file.readAllBytes();
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes)) {

                if (isPdf(byteArrayInputStream)) {
                    byteArrayInputStream.reset();
                    merger.addSource(new RandomAccessReadBuffer(byteArrayInputStream));
                } else if(isImage(byteArrayInputStream)) {
                    byteArrayInputStream.reset();
                    merger.addSource(new RandomAccessReadBuffer(convertImageToPdf(byteArrayInputStream)));
                } else {
                    throw new IOException("O arquivo não é um PDF válido nem uma imagem suportada.");
                }
            }
        }

        try (OutputStream out = Files.newOutputStream(tempFile)) {
            merger.setDestinationStream(out);
            merger.mergeDocuments(null);
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); InputStream in = Files.newInputStream(tempFile)) {
            in.transferTo(baos);
            result = baos.toByteArray();
            System.out.println("ARQUIVOS MERGEADOS COM SUCESSO!");
        }
        return result;
    }

    private boolean isPdf(InputStream inputStream) throws IOException {
        byte[] header = readFirstNBytes(inputStream, 5);

        return header.length == 5 && header[0] == '%' && header[1] == 'P' && header[2] == 'D' && header[3] == 'F' && header[4] == '-';
    }

    private boolean isImage(InputStream inputStream) throws IOException {
        inputStream.mark(0);
        BufferedImage image = ImageIO.read(inputStream);
        inputStream.reset();
        return image != null;
    }

    private byte[] readFirstNBytes(InputStream inputStream, int n) throws IOException {
        byte[] buffer = new byte[n];
        int bytesRead = 0;

        while (bytesRead < n) {
            int result = inputStream.read(buffer, bytesRead, n - bytesRead);
            if (result == -1) {
                break;
            }
            bytesRead += result;
        }


        if (bytesRead < n) {
            buffer = Arrays.copyOf(buffer, bytesRead);
        }

        return buffer;
    }
}



