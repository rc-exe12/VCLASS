package in.vclass.model;
import jakarta.persistence.*;
@Entity public class Course {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String title; private String code; @Column(length=2000) private String description; private String teacherEmail;
 protected Course(){}
 public Course(String t,String c,String d,String e){title=t;code=c;description=d;teacherEmail=e;}
 public Long getId(){return id;} public String getTitle(){return title;} public String getCode(){return code;} public String getDescription(){return description;}
}
