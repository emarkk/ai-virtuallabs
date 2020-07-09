package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.ImageModelDTO;
import it.polito.ai.virtuallabs.backend.entities.ImageModel;
import it.polito.ai.virtuallabs.backend.entities.Professor;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.repositories.ImageRepository;
import it.polito.ai.virtuallabs.backend.repositories.ProfessorRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.Deflater;

@Service
@Transactional
public class ImageServiceImpl implements ImageService{
    @Autowired
    ImageRepository imageRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ProfessorRepository professorRepository;

    @Override
    public void storeImage(MultipartFile file, String id) {
        try {
            ImageModel img = ImageModel.builder()
                    .name(id)
                    .type(file.getContentType())
                    .picByte(compressBytes(file.getBytes()))
                    .build();
            imageRepository.save(img);
            long idNum = Long.parseLong(id.substring(1));
            if(id.startsWith("s")){
                Optional<Student> s = studentRepository.findById(idNum);
                if(s.isEmpty()){
                    throw new StudentNotFoundException();
                }
                Student stud = s.get();
                stud.setHasPicture(true);
                studentRepository.save(stud);
            } else {
                Optional<Professor> p = professorRepository.findById(idNum);
                if(p.isEmpty()){
                    throw new ProfessorNotFoundException();
                }
                Professor prof = p.get();
                prof.setHasPicture(true);
                professorRepository.save(prof);
            }
        } catch (IOException e){
            throw new ImageElaborationException();
        }
    }

    @Override
    public ImageModelDTO getImage(String role, String id) {
        return null;
    }

    public static byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            throw new ImageElaborationException();
        }
        System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }
}
