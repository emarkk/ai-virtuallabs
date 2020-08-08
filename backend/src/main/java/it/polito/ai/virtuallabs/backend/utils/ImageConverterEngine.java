package it.polito.ai.virtuallabs.backend.utils;

import it.polito.ai.virtuallabs.backend.services.FileHandlingException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageConverterEngine {

    public static BufferedImage convert(MultipartFile file) throws IOException {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(image == null)
                throw new IOException();
            BufferedImage result = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            result.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
            return result;
    }
}
