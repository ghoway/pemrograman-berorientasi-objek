package com.undira.absenin.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public class QRCodeGenerator {
    
    public static BufferedImage generateQRCodeImage(String text, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }


    public static String generateAndSaveQRCode(String nim, String nama) throws IOException, WriterException {
        // 1. Buat folder 'qrcode' jika belum ada
        Path qrCodeDir = Paths.get("qrcode");
        if (!Files.exists(qrCodeDir)) {
            Files.createDirectories(qrCodeDir);
        }

        // 2. Bersihkan nama dari karakter yang tidak valid untuk nama file
        String sanitizedNama = nama.replaceAll("[^a-zA-Z0-9.-]", "_");

        // 3. Buat nama file
        String fileName = nim + "_" + sanitizedNama + ".png";
        Path filePath = qrCodeDir.resolve(fileName);

        // 4. Generate image dan simpan ke file
        BufferedImage qrImage = generateQRCodeImage(nim, 200, 200);
        File outputFile = filePath.toFile();
        ImageIO.write(qrImage, "png", outputFile);

        return outputFile.getAbsolutePath();
    }
}