package in.vclass.model;
import jakarta.persistence.*; import java.time.LocalDate;
@Entity public class Assignment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String title; @Column(length=3000) private String description; private Long courseId; private LocalDate dueDate; private Integer maxMarks;
 protected Assignment(){}
 public Assignment(String t,String d,Long c,LocalDate due,Integer m){title=t;description=d;courseId=c;dueDate=due;maxMarks=m;}
 public Long getId(){return id;} public String getTitle(){return title;} public String getDescription(){return description;} public Long getCourseId(){return courseId;} public LocalDate getDueDate(){return dueDate;} public Integer getMaxMarks(){return maxMarks;}
}
