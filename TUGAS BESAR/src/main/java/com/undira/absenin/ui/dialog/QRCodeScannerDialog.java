package com.undira.absenin.ui.dialog;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class QRCodeScannerDialog extends JDialog {

    private String scannedResult = null;
    private Webcam webcam = null;
    private WebcamPanel webcamPanel = null;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public QRCodeScannerDialog(JFrame parent) {
        super(parent, "Scan QR Code", true);
        initializeComponents();
        startScanning();
        pack();
        setLocationRelativeTo(parent);
        
        // Pastikan webcam ditutup saat dialog ditutup
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                stopWebcam();
            }
        });
    }

    private void initializeComponents() {
        setTitle("Scanning QR Code...");
        setLayout(new BorderLayout());
        
        JLabel instructionLabel = new JLabel("Arahkan QR Code ke kamera...", JLabel.CENTER);
        add(instructionLabel, BorderLayout.NORTH);
    }

    private void startScanning() {
        executor.execute(() -> {
            webcam = Webcam.getDefault();
            if (webcam == null) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Tidak ada kamera yang terdeteksi!", "Error", JOptionPane.ERROR_MESSAGE);
                    dispose();
                });
                return;
            }

            webcam.setViewSize(WebcamResolution.VGA.getSize());
            webcam.open();

            SwingUtilities.invokeLater(() -> {
                webcamPanel = new WebcamPanel(webcam);
                webcamPanel.setImageSizeDisplayed(true);
                webcamPanel.setFPSDisplayed(true);
                add(webcamPanel, BorderLayout.CENTER);
                revalidate();
                repaint();
            });

            // Loop untuk terus memindai
            while (webcam.isOpen()) {
                try {
                    Thread.sleep(100); // Jangan scan terlalu cepat
                } catch (InterruptedException e) {
                    // do nothing
                }

                BufferedImage image = webcam.getImage();
                if (image != null) {
                    Result result = scanImage(image);
                    if (result != null) {
                        this.scannedResult = result.getText();
                        stopWebcam();
                        SwingUtilities.invokeLater(() -> dispose());
                        break;
                    }
                }
            }
        });
    }
    
    private void stopWebcam() {
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
    }

    private Result scanImage(BufferedImage image) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            return new MultiFormatReader().decode(bitmap);
        } catch (NotFoundException e) {
            // QR Code tidak ditemukan di frame ini, lanjut ke frame berikutnya
            return null;
        }
    }

    public String getScannedResult() {
        return scannedResult;
    }
}