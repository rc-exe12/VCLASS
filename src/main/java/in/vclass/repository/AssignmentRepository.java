package in.vclass.repository;
import in.vclass.model.Assignment; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface AssignmentRepository extends JpaRepository<Assignment,Long>{List<Assignment> findAllByOrderByIdDesc();}
