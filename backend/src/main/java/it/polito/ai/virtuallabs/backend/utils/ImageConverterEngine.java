package it.polito.ai.virtuallabs.backend.utils;

import it.polito.ai.virtuallabs.backend.services.FileHandlingException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

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

    public static void compressAndSave(BufferedImage bufferedImage, String path) throws IOException {
        File compressedImageFile = new File(path);
        OutputStream outputStream = new FileOutputStream(compressedImageFile);
        float imageQuality = 0.1f;

        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName("jpg");
        if (!imageWriters.hasNext())
            throw new IOException();

        ImageWriter imageWriter = (ImageWriter) imageWriters.next();
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
        imageWriter.setOutput(imageOutputStream);
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(imageQuality);

        imageWriter.write(null, new IIOImage(bufferedImage, null, null), imageWriteParam);

        outputStream.close();
        imageOutputStream.close();
        imageWriter.dispose();
    }
}
