package it.polito.ai.virtuallabs.backend.utils;

import it.polito.ai.virtuallabs.backend.services.FileHandlingException;
import it.polito.ai.virtuallabs.backend.services.ProfilePictureNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class ProfilePicturesUtility {

    public enum ProfileType {
        STUDENT,
        PROFESSOR
    }

    //userId qui è id studente o id docente
    public Resource getProfilePicture(Long userId, ProfileType profileType) {
        String path = profileType == ProfileType.STUDENT ? "student/" : "professor/";
        Path file = Paths.get("uploads/profile_pictures/" + path + userId + ".jpg");
        try{
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new ProfilePictureNotFoundException();
        }
    }

    public void postProfilePicture(Long userId, ProfileType profileType, MultipartFile file) {
        String path = profileType == ProfileType.STUDENT ? "student/" : "professor/";
        Path filePath = Paths.get("uploads/profile_pictures/" + path + userId + ".jpg");
        if(!Objects.equals(file.getContentType(), "image/jpeg"))
            throw new FileHandlingException();
        try {
            Files.deleteIfExists(filePath);
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            throw new FileHandlingException();
        }
    }

}
