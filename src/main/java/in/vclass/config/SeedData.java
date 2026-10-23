
package in.vclass.config;

import in.vclass.model.User;
import in.vclass.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedData {

    @Bean
    CommandLineRunner seed(
            UserRepository repository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            User teacher = repository
                    .findByEmail("teacher@demo.com")
                    .orElse(null);

            if (teacher == null) {
                repository.save(new User(
                        "Demo Teacher",
                        "teacher@demo.com",
                        passwordEncoder.encode("teacher123"),
                        "TEACHER"
                ));
            } else if (!passwordEncoder.matches(
                    "teacher123", teacher.getPassword())) {

                teacher.updateStudentDetails(
                        teacher.getRollNumber(),
                        teacher.getDepartment(),
                        teacher.getAcademicYear(),
                        teacher.getSection()
                );

                // Update the existing teacher password to BCrypt.
                // The repository save updates the existing record.
                teacher.setPassword(
                        passwordEncoder.encode("teacher123")
                );
                repository.save(teacher);
            }

            User student = repository
                    .findByEmail("student@demo.com")
                    .orElse(null);

            if (student == null) {
                repository.save(new User(
                        "Demo Student",
                        "student@demo.com",
                        passwordEncoder.encode("student123"),
                        "STUDENT"
                ));
            } else if (!passwordEncoder.matches(
                    "student123", student.getPassword())) {

                student.setPassword(
                        passwordEncoder.encode("student123")
                );
                repository.save(student);
            }
        };
    }
}
