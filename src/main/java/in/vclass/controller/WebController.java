
package in.vclass.controller;

import in.vclass.model.*;
import in.vclass.repository.*;

import jakarta.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class WebController {

    private final UserRepository users;
    private final CourseRepository courses;
    private final AssignmentRepository assignments;
    private final SubmissionRepository submissions;
    private final AttendanceRepository attendance;
    private final PasswordEncoder passwordEncoder;

    public WebController(
            UserRepository users,
            CourseRepository courses,
            AssignmentRepository assignments,
            SubmissionRepository submissions,
            AttendanceRepository attendance,
            PasswordEncoder passwordEncoder) {

        this.users = users;
        this.courses = courses;
        this.assignments = assignments;
        this.submissions = submissions;
        this.attendance = attendance;
        this.passwordEncoder = passwordEncoder;
    }

    // Current logged-in user
    private User user(HttpSession session) {
        Object email = session.getAttribute("email");

        return email == null
                ? null
                : users.findByEmail(email.toString()).orElse(null);
    }

    // Check authentication
    private String auth(HttpSession session) {
        return user(session) == null
                ? "redirect:/login"
                : null;
    }

    // Common page attributes
    private void base(Model model, HttpSession session, String page) {
        model.addAttribute("user", user(session));
        model.addAttribute("currentPage", page);
    }

    // Home
    @GetMapping("/")
    public String home(HttpSession session) {
        return user(session) == null
                ? "redirect:/login"
                : "redirect:/dashboard";
    }

    // Login page
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        return user(session) == null
                ? "login"
                : "redirect:/dashboard";
    }

    // Login using BCrypt
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirect) {

        User existingUser = users
                .findByEmail(email.trim().toLowerCase())
                .orElse(null);

        if (existingUser == null
                || !passwordEncoder.matches(
                        password,
                        existingUser.getPassword())) {

            redirect.addFlashAttribute(
                    "error",
                    "Email or password is incorrect."
            );

            return "redirect:/login";
        }

        session.setAttribute("email", existingUser.getEmail());

        return "redirect:/dashboard";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String redirect = auth(session);

        if (redirect != null) {
            return redirect;
        }

        base(model, session, "dashboard");

        List<Course> courseList = courses.findAllByOrderByIdDesc();
        List<Assignment> assignmentList =
                assignments.findAllByOrderByIdDesc();
        List<Submission> submissionList =
                submissions.findAllByOrderBySubmittedAtDesc();

        model.addAttribute("courses", courseList);
        model.addAttribute("assignments", assignmentList);
        model.addAttribute("submissions", submissionList);

        model.addAttribute("courseCount", courseList.size());
        model.addAttribute("assignmentCount", assignmentList.size());
        model.addAttribute("submissionCount", submissionList.size());

        return "dashboard";
    }

    // Courses page
    @GetMapping("/courses")
    public String coursesPage(HttpSession session, Model model) {
        String redirect = auth(session);

        if (redirect != null) {
            return redirect;
        }

        base(model, session, "courses");
        model.addAttribute(
                "courses",
                courses.findAllByOrderByIdDesc()
        );

        return "courses";
    }

    // Create course
    @PostMapping("/courses")
    public String addCourse(
            HttpSession session,
            @RequestParam String title,
            @RequestParam String code,
            @RequestParam(defaultValue = "") String description,
            RedirectAttributes redirect) {

        String authRedirect = auth(session);

        if (authRedirect != null) {
            return authRedirect;
        }

        User currentUser = user(session);

        if (!"TEACHER".equals(currentUser.getRole())) {
            return "redirect:/courses";
        }

        if (title.isBlank() || code.isBlank()) {
            redirect.addFlashAttribute(
                    "error",
                    "Course title and code are required."
            );

            return "redirect:/courses";
        }

        courses.save(new Course(
                title.trim(),
                code.trim().toUpperCase(),
                description.trim(),
                currentUser.getEmail()
        ));

        redirect.addFlashAttribute("success", "Course created.");

        return "redirect:/courses";
    }

    // Assignments page
    @GetMapping("/assignments")
    public String assignmentsPage(
            HttpSession session,
            Model model) {

        String redirect = auth(session);

        if (redirect != null) {
            return redirect;
        }

        base(model, session, "assignments");

        User currentUser = user(session);

        model.addAttribute(
                "courses",
                courses.findAllByOrderByIdDesc()
        );

        model.addAttribute(
                "assignments",
                assignments.findAllByOrderByIdDesc()
        );

        model.addAttribute(
                "submissions",
                "TEACHER".equals(currentUser.getRole())
                        ? submissions.findAllByOrderBySubmittedAtDesc()
                        : submissions.findByStudentEmailOrderBySubmittedAtDesc(
                                currentUser.getEmail())
        );

        return "assignments";
    }

    // Create assignment
    @PostMapping("/assignments")
    public String addAssignment(
            HttpSession session,
            @RequestParam String title,
            @RequestParam(defaultValue = "") String description,
            @RequestParam Long courseId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dueDate,
            @RequestParam(defaultValue = "100") Integer maxMarks,
            RedirectAttributes redirect) {

        String authRedirect = auth(session);

        if (authRedirect != null) {
            return authRedirect;
        }

        if (!"TEACHER".equals(user(session).getRole())) {
            return "redirect:/assignments";
        }

        if (title.isBlank() || !courses.existsById(courseId)) {
            redirect.addFlashAttribute(
                    "error",
                    "Enter a title and select a valid course."
            );

            return "redirect:/assignments";
        }

        assignments.save(new Assignment(
                title.trim(),
                description.trim(),
                courseId,
                dueDate,
                Math.max(1, maxMarks)
        ));

        redirect.addFlashAttribute(
                "success",
                "Assignment created."
        );

        return "redirect:/assignments";
    }

    // Student submits assignment
    @PostMapping("/assignments/{id}/submit")
    public String submitAssignment(
            HttpSession session,
            @PathVariable Long id,
            @RequestParam String answer,
            RedirectAttributes redirect) {

        String authRedirect = auth(session);

        if (authRedirect != null) {
            return authRedirect;
        }

        User currentUser = user(session);

        if (!"STUDENT".equals(currentUser.getRole())) {
            return "redirect:/assignments";
        }

        if (assignments.findById(id).isEmpty()
                || answer.isBlank()) {

            redirect.addFlashAttribute(
                    "error",
                    "Choose an assignment and enter your answer."
            );

            return "redirect:/assignments";
        }

        if (submissions
                .findByAssignmentIdAndStudentEmail(
                        id,
                        currentUser.getEmail())
                .isEmpty()) {

            submissions.save(new Submission(
                    id,
                    currentUser.getEmail(),
                    answer.trim()
            ));

            redirect.addFlashAttribute(
                    "success",
                    "Submission recorded."
            );
        } else {
            redirect.addFlashAttribute(
                    "error",
                    "You have already submitted this assignment."
            );
        }

        return "redirect:/assignments";
    }

    // Grade submission
    @PostMapping("/submissions/{id}/grade")
    public String gradeSubmission(
            HttpSession session,
            @PathVariable Long id,
            @RequestParam Integer marks,
            @RequestParam(defaultValue = "") String feedback,
            RedirectAttributes redirect) {

        String authRedirect = auth(session);

        if (authRedirect != null) {
            return authRedirect;
        }

        if (!"TEACHER".equals(user(session).getRole())) {
            return "redirect:/assignments";
        }

        Submission submission = submissions.findById(id).orElse(null);

        if (submission == null || marks < 0) {
            redirect.addFlashAttribute(
                    "error",
                    "Invalid submission or marks."
            );

            return "redirect:/assignments";
        }

        Assignment assignment = assignments
                .findById(submission.getAssignmentId())
                .orElse(null);

        if (assignment == null || marks > assignment.getMaxMarks()) {
            redirect.addFlashAttribute(
                    "error",
                    "Marks cannot exceed the assignment maximum."
            );

            return "redirect:/assignments";
        }

        submission.grade(marks, feedback.trim());
        submissions.save(submission);

        redirect.addFlashAttribute("success", "Submission graded.");

        return "redirect:/assignments";
    }

    // Attendance page
    @GetMapping("/attendance")
    public String attendancePage(
            HttpSession session,
            Model model) {

        String redirect = auth(session);

        if (redirect != null) {
            return redirect;
        }

        base(model, session, "attendance");

        model.addAttribute(
                "courses",
                courses.findAllByOrderByIdDesc()
        );

        model.addAttribute(
                "records",
                attendance.findAllByOrderByAttendanceDateDesc()
        );

        model.addAttribute("today", LocalDate.now());

        return "attendance";
    }

    // Mark attendance
    @PostMapping("/attendance")
    public String markAttendance(
            HttpSession session,
            @RequestParam Long courseId,
            @RequestParam String studentEmail,
            @RequestParam String status,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate attendanceDate,
            RedirectAttributes redirect) {

        String authRedirect = auth(session);

        if (authRedirect != null) {
            return authRedirect;
        }

        if (!"TEACHER".equals(user(session).getRole())) {
            return "redirect:/attendance";
        }

        if (!courses.existsById(courseId)
                || users.findByEmail(
                        studentEmail.trim().toLowerCase())
                        .filter(u -> "STUDENT".equals(u.getRole()))
                        .isEmpty()
                || (!"PRESENT".equals(status)
                    && !"ABSENT".equals(status))) {

            redirect.addFlashAttribute(
                    "error",
                    "Select a valid course, student and status."
            );

            return "redirect:/attendance";
        }

        attendance.save(new Attendance(
                courseId,
                studentEmail.trim().toLowerCase(),
                attendanceDate,
                status
        ));

        redirect.addFlashAttribute(
                "success",
                "Attendance saved."
        );

        return "redirect:/attendance";
    }

    // Students directory
    @GetMapping("/students")
    public String studentsPage(
            HttpSession session,
            Model model) {

        String redirect = auth(session);

        if (redirect != null) {
            return redirect;
        }

        User currentUser = user(session);

        if (!"TEACHER".equals(currentUser.getRole())) {
            return "redirect:/dashboard";
        }

        base(model, session, "students");

        model.addAttribute(
                "students",
                users.findByRoleOrderByNameAsc("STUDENT")
        );

        return "students";
    }

    // Register student with a BCrypt-hashed password
    @PostMapping("/students")
    public String addStudent(
            HttpSession session,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String rollNumber,
            @RequestParam(defaultValue = "") String department,
            @RequestParam(defaultValue = "") String academicYear,
            @RequestParam(defaultValue = "") String section,
            RedirectAttributes redirect) {

        String authRedirect = auth(session);

        if (authRedirect != null) {
            return authRedirect;
        }

        if (!"TEACHER".equals(user(session).getRole())) {
            return "redirect:/dashboard";
        }

        String cleanName = name.trim();
        String cleanEmail = email.trim().toLowerCase();
        String cleanRoll = rollNumber.trim();

        if (cleanName.isBlank()
                || cleanEmail.isBlank()
                || password.isBlank()
                || cleanRoll.isBlank()) {

            redirect.addFlashAttribute(
                    "error",
                    "Name, email, password and roll number are required."
            );

            return "redirect:/students";
        }

        if (password.length() < 8) {
            redirect.addFlashAttribute(
                    "error",
                    "Password must contain at least 8 characters."
            );

            return "redirect:/students";
        }

        if (users.findByEmail(cleanEmail).isPresent()) {
            redirect.addFlashAttribute(
                    "error",
                    "An account with this email already exists."
            );

            return "redirect:/students";
        }

        if (users.existsByRollNumber(cleanRoll)) {
            redirect.addFlashAttribute(
                    "error",
                    "This roll number is already registered."
            );

            return "redirect:/students";
        }

        User student = new User(
                cleanName,
                cleanEmail,
                passwordEncoder.encode(password),
                "STUDENT"
        );

        student.updateStudentDetails(
                cleanRoll,
                department.trim(),
                academicYear.trim(),
                section.trim()
        );

        users.save(student);

        redirect.addFlashAttribute(
                "success",
                "Student registered successfully."
        );

        return "redirect:/students";
    }

    // Delete student
    @PostMapping("/students/{id}/delete")
    public String deleteStudent(
            HttpSession session,
            @PathVariable Long id,
            RedirectAttributes redirect) {

        String authRedirect = auth(session);

        if (authRedirect != null) {
            return authRedirect;
        }

        User currentUser = user(session);

        if (!"TEACHER".equals(currentUser.getRole())) {
            return "redirect:/dashboard";
        }

        User student = users.findById(id).orElse(null);

        if (student == null
                || !"STUDENT".equals(student.getRole())) {

            redirect.addFlashAttribute(
                    "error",
                    "Student record not found."
            );

            return "redirect:/students";
        }

        if (student.getEmail().equals(currentUser.getEmail())) {
            redirect.addFlashAttribute(
                    "error",
                    "You cannot delete your own account."
            );

            return "redirect:/students";
        }

        users.delete(student);

        redirect.addFlashAttribute(
                "success",
                "Student record deleted."
        );

        return "redirect:/students";
    }


    // Show the edit student form (teachers only)
    @GetMapping("/students/{id}/edit")
    public String editStudentPage(
            HttpSession session,
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirect) {

        String authRedirect = auth(session);
        if (authRedirect != null) {
            return authRedirect;
        }

        User currentUser = user(session);
        if (!"TEACHER".equals(currentUser.getRole())) {
            return "redirect:/dashboard";
        }

        User student = users.findById(id).orElse(null);
        if (student == null || !"STUDENT".equals(student.getRole())) {
            redirect.addFlashAttribute("error", "Student record not found.");
            return "redirect:/students";
        }

        base(model, session, "students");
        model.addAttribute("student", student);
        return "edit-student";
    }

    // Save edited student details (password is not changed here)
    @PostMapping("/students/{id}/edit")
    public String updateStudent(
            HttpSession session,
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String rollNumber,
            @RequestParam(defaultValue = "") String department,
            @RequestParam(defaultValue = "") String academicYear,
            @RequestParam(defaultValue = "") String section,
            RedirectAttributes redirect) {

        String authRedirect = auth(session);
        if (authRedirect != null) {
            return authRedirect;
        }

        User currentUser = user(session);
        if (!"TEACHER".equals(currentUser.getRole())) {
            return "redirect:/dashboard";
        }

        User student = users.findById(id).orElse(null);
        if (student == null || !"STUDENT".equals(student.getRole())) {
            redirect.addFlashAttribute("error", "Student record not found.");
            return "redirect:/students";
        }

        String cleanName = name.trim();
        String cleanEmail = email.trim().toLowerCase();
        String cleanRoll = rollNumber.trim();

        if (cleanName.isBlank() || cleanEmail.isBlank() || cleanRoll.isBlank()) {
            redirect.addFlashAttribute(
                    "error",
                    "Name, email and roll number are required."
            );
            return "redirect:/students/" + id + "/edit";
        }

        boolean duplicateEmail = users.findByEmail(cleanEmail)
                .filter(existing -> !existing.getId().equals(id))
                .isPresent();

        boolean duplicateRoll = users.existsByRollNumber(cleanRoll)
                && (student.getRollNumber() == null
                    || !cleanRoll.equalsIgnoreCase(student.getRollNumber()));

        if (duplicateEmail || duplicateRoll) {
            redirect.addFlashAttribute(
                    "error",
                    duplicateEmail
                            ? "An account with this email already exists."
                            : "This roll number is already registered."
            );
            return "redirect:/students/" + id + "/edit";
        }

        student.setName(cleanName);
        student.setEmail(cleanEmail);
        student.updateStudentDetails(
                cleanRoll,
                department.trim(),
                academicYear.trim(),
                section.trim()
        );

        users.save(student);

        redirect.addFlashAttribute(
                "success",
                "Student details updated successfully."
        );
        return "redirect:/students";
    }

}
