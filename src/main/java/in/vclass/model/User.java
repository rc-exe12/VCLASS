
package in.vclass.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private String role;

    @Column(unique = true)
    private String rollNumber;

    private String department;
    private String academicYear;
    private String section;

    protected User() {}

    public User(String n, String e, String p, String r) {
        name = n;
        email = e;
        password = p;
        role = r;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getRollNumber() { return rollNumber; }
    public String getDepartment() { return department; }
    public String getAcademicYear() { return academicYear; }
    public String getSection() { return section; }

    public void updateStudentDetails(String rollNumber, String department,
                                     String academicYear, String section) {
        this.rollNumber = rollNumber;
        this.department = department;
        this.academicYear = academicYear;
        this.section = section;
    }

    public void setPassword(String password) {
    this.password = password;
}
public void setName(String name) {
    this.name = name;
}

public void setEmail(String email) {
    this.email = email;
}
}
