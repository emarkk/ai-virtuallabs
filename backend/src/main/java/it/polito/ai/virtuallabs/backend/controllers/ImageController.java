package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.ImageModelDTO;
import it.polito.ai.virtuallabs.backend.services.ImageElaborationException;
import it.polito.ai.virtuallabs.backend.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping(path = "/api/static/img")
public class ImageController {
    @Autowired
    ImageService imageService;

    @PostMapping(path = "/upload/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadImage(@PathVariable(name = "id") String id, @RequestParam("imageFile") MultipartFile file) throws IOException, NumberFormatException, ImageElaborationException {
        System.out.println("Original Image Byte Size - " + file.getBytes().length);
        if(!id.startsWith("s") && !id.startsWith("d")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id should start with a letter related to the role and must be followed by a numeric id.");
        }
        try {
            long l = Long.parseLong(id.substring(1));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id should start with a letter related to the role and must be followed by a numeric id.");
        }
        try {
            imageService.storeImage(file, id);
        } catch (ImageElaborationException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error occurred.");
        }
    }
}
