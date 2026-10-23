package in.vclass.model;
import jakarta.persistence.*; import java.time.LocalDate;
@Entity public class Attendance {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private Long courseId; private String studentEmail; private LocalDate attendanceDate; private String status;
 protected Attendance(){}
 public Attendance(Long c,String e,LocalDate d,String s){courseId=c;studentEmail=e;attendanceDate=d;status=s;}
 public Long getId(){return id;} public Long getCourseId(){return courseId;} public String getStudentEmail(){return studentEmail;} public LocalDate getAttendanceDate(){return attendanceDate;} public String getStatus(){return status;}
}
