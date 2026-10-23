package in.vclass.model;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity public class Submission {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private Long assignmentId; private String studentEmail; @Column(length=5000) private String answer; private LocalDateTime submittedAt; private Integer marks; @Column(length=1000) private String feedback;
 protected Submission(){}
 public Submission(Long a,String e,String ans){assignmentId=a;studentEmail=e;answer=ans;submittedAt=LocalDateTime.now();}
 public Long getId(){return id;} public Long getAssignmentId(){return assignmentId;} public String getStudentEmail(){return studentEmail;} public String getAnswer(){return answer;} public LocalDateTime getSubmittedAt(){return submittedAt;} public Integer getMarks(){return marks;} public String getFeedback(){return feedback;}
 public void grade(Integer m,String f){marks=m;feedback=f;}
}
