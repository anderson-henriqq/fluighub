package com.fluig.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


public class QRCodeGenerator {


    public static String stringToBase64(String text) {
        BitMatrix bitMatrix = null;

        try {
            bitMatrix = generateQRCode(text);
        } catch (WriterException e) {
            e.printStackTrace();
            System.out.println("ERROR - K31 " + e.getMessage());
        }

        String base64 = null;
        try {
            base64 = matrixToBase64(bitMatrix);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR - K31 " + e.getMessage());

        }

        return base64;
    }


    private static BitMatrix generateQRCode(String text) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        return qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200, getEncoderConfig());
    }


    private static String matrixToBase64(BitMatrix bitMatrix) throws IOException {
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private static Map<EncodeHintType, ErrorCorrectionLevel> getEncoderConfig() {
        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        return hints;
    }
}
