package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Student _getStudent(Long studentId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);

        if(studentOptional.isEmpty())
            throw new StudentNotFoundException();

        return studentOptional.get();
    }

    @Override
    public Optional<StudentDTO> getStudent(Long studentId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        return studentOptional.map(s -> modelMapper.map(s, StudentDTO.class));
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDTO> getCoursesForStudent(Long studentId) {
        return this._getStudent(studentId).getCourses()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDTO> getTeamsForStudent(Long studentId) {
        return this._getStudent(studentId).getTeams()
                .stream()
                .map(c -> modelMapper.map(c, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<StudentDTO> getOrderedSearchResult(String q) {
        Comparator<Student> distance = new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                int d1 = StringUtils.getLevenshteinDistance(o1.getResumedInfos(), q);
                int d2 = StringUtils.getLevenshteinDistance(o2.getResumedInfos(), q);
                if (d1 == d2) return 0;
                return d1>d2 ? 1 : -1;
            }
        };
        return studentRepository.getByResumedInfosContaining(q).stream()
                .limit(3)
                .sorted(distance)
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }



    static void count(String str1, String str2)
    {
        int c = 0, j = 0;

        // Traverse the string 1 char by char
        for (int i = 0; i < str1.length(); i++)
        {

            // This will check if str1[i]
            // is present in str2 or not
            // str2.find(str1[i]) returns -1 if not found
            // otherwise it returns the starting occurrence
            // index of that character in str2
            if (str2. indexOf(str1.charAt(i)) >= 0)
            {
                c += 1;
            }
        }
        System.out.println("No. of matching characters are: " + c);
    }


}
