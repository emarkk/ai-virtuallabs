package it.polito.ai.virtuallabs.backend.utils;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import it.polito.ai.virtuallabs.backend.services.VmConnectionException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;

public class VmConnectionUtility {

    private static final Path _root = Paths.get("vms");
    private static final String _vmsFileName = "Windows10.jpg";

    public static byte[] retrieveVm() {
        try {
            Path file = _root.resolve(_vmsFileName);

            ImagePlus image = IJ.openImage(file.toString());
            ImageProcessor ip = image.getProcessor();
            ip.setColor(Color.white);
            ip.setFont(new Font("Arial", Font.BOLD, 30));
            String date = new Timestamp(System.currentTimeMillis()).toLocalDateTime()
                    .toString()
                    .replace('T', ' ');
            ip.drawString(date, 10, 30);
            BufferedImage bufferedImage = ip.getBufferedImage();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpeg", os);
            return os.toByteArray();

        } catch (IOException | RuntimeException e) {
            throw new VmConnectionException();
        }
    }
}
