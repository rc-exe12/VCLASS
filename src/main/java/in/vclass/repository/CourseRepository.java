package in.vclass.repository;
import in.vclass.model.Course; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface CourseRepository extends JpaRepository<Course,Long>{List<Course> findAllByOrderByIdDesc();}
