package org.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentDaoImplTest {

    private static StudentDaoImpl studentDao;

    @BeforeAll
    static void setup() {
        studentDao = new StudentDaoImpl();
        studentDao.clearAllStudents();
    }

    @AfterAll
    static void tearDown() {
        if (studentDao != null) {
            studentDao.close();
        }
    }

    @Test
    @Order(1)
    void testSaveStudent() {
        Student student = new Student("Rodion", "Plakhotniuk", "raidertwk@gmail.com");
        studentDao.save(student);
        assertNotNull(student.getId());
        System.out.println("Saved student: " + student);
    }

    @Test
    @Order(2)
    void testFindStudentById() {
        Student student = studentDao.findByEmail("raidertwk@gmail.com");
        assertNotNull(student);
        Student foundStudent = studentDao.findById(student.getId());
        assertNotNull(foundStudent);
        assertEquals("Rodion", foundStudent.getFirstName());
        System.out.println("Found student by ID: " + foundStudent);
    }

    @Test
    @Order(3)
    void testFindStudentByEmail() {
        Student foundStudent = studentDao.findByEmail("raidertwk@gmail.com");
        assertNotNull(foundStudent);
        assertEquals("Rodion", foundStudent.getFirstName());
        System.out.println("Found student by email: " + foundStudent);
    }

    @Test
    @Order(4)
    void testAddHomeworkToStudent() {
        Student student = studentDao.findByEmail("raidertwk@gmail.com");
        assertNotNull(student);

        Homework homework1 = new Homework("Java Core Basics", LocalDate.of(2025, 7, 1), 0);
        Homework homework2 = new Homework("Hibernate Introduction", LocalDate.of(2025, 7, 5), 0);

        student.addHomework(homework1);
        student.addHomework(homework2);

        studentDao.update(student);

        Student updatedStudent = studentDao.findById(student.getId());
        assertNotNull(updatedStudent);
        assertEquals(2, updatedStudent.getHomeworks().size());
        System.out.println("Student with homeworks: " + updatedStudent);
        updatedStudent.getHomeworks().forEach(hw -> System.out.println("  Homework: " + hw));
    }


    @Test
    @Order(5)
    void testUpdateStudent() {
        Student student = studentDao.findByEmail("raidertwk@gmail.com");
        assertNotNull(student);
        student.setFirstName("Rodion (updated)");
        student.setLastName("Plakhotniuk (updated)");
        Student updatedStudent = studentDao.update(student);
        assertEquals("Rodion (updated)", updatedStudent.getFirstName());
        assertEquals("Plakhotniuk (updated)", updatedStudent.getLastName());
        System.out.println("Updated student: " + updatedStudent);
    }

    @Test
    @Order(6)
    void testFindAllStudents() {
        Student student2 = new Student("Maria", "Sydorenko", "maria.sydorenko@example.com");
        studentDao.save(student2);

        List<Student> students = studentDao.findAll();
        assertFalse(students.isEmpty());
        assertTrue(students.size() >= 2);
        System.out.println("All students:");
        students.forEach(s -> System.out.println(" - " + s));
    }

    @Test
    @Order(7)
    void testRemoveHomeworkFromStudent() {
        Student student = studentDao.findByEmail("raidertwk@gmail.com");
        assertNotNull(student);

        student = studentDao.findById(student.getId());
        assertNotNull(student);
        assertEquals(2, student.getHomeworks().size());

        Homework homeworkToRemove = student.getHomeworks().stream()
                .filter(hw -> "Java Core Basics".equals(hw.getDescription()))
                .findFirst()
                .orElse(null);
        assertNotNull(homeworkToRemove);

        student.removeHomework(homeworkToRemove);
        studentDao.update(student);

        Student updatedStudent = studentDao.findById(student.getId());
        assertNotNull(updatedStudent);
        assertEquals(1, updatedStudent.getHomeworks().size());
        System.out.println("Student after removing homework: " + updatedStudent);
        updatedStudent.getHomeworks().forEach(hw -> System.out.println("  Remaining Homework: " + hw));
    }

    @Test
    @Order(8)
    void testDeleteStudentById() {
        Student student = studentDao.findByEmail("raidertwk@gmail.com");
        assertNotNull(student);
        boolean deleted = studentDao.deleteById(student.getId());
        assertTrue(deleted);
        assertNull(studentDao.findByEmail("raidertwk@gmail.com"));
        System.out.println("Student deleted successfully: " + student.getId());
    }
}