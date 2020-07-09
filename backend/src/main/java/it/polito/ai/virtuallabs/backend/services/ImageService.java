package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.ImageModelDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    void storeImage (MultipartFile file, String id);
    ImageModelDTO getImage(String role, String id);
}
