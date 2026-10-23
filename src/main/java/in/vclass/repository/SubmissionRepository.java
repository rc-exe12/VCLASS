package in.vclass.repository;
import in.vclass.model.Submission; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface SubmissionRepository extends JpaRepository<Submission,Long>{List<Submission> findAllByOrderBySubmittedAtDesc(); List<Submission> findByStudentEmailOrderBySubmittedAtDesc(String email); Optional<Submission> findByAssignmentIdAndStudentEmail(Long assignmentId,String studentEmail);}
