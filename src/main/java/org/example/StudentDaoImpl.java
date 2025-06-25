package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.NoResultException;

import java.util.List;

public class StudentDaoImpl implements GenericDao<Student, Long> {

    private final EntityManagerFactory entityManagerFactory;

    public StudentDaoImpl() {
        entityManagerFactory = Persistence.createEntityManagerFactory("hillel-persistence-unit");
    }

    @Override
    public void save(Student student) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            entityManager.persist(student);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Error saving student: " + e.getMessage());
            throw new RuntimeException("Failed to save student", e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Student findById(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            Student student = entityManager.createQuery(
                            "SELECT s FROM Student s LEFT JOIN FETCH s.homeworks WHERE s.id = :id", Student.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return student;
        } catch (NoResultException e) {
            return null;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Student findByEmail(String email) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            Student student = entityManager.createQuery(
                            "SELECT s FROM Student s LEFT JOIN FETCH s.homeworks WHERE s.email = :email", Student.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return student;
        } catch (NoResultException e) {
            return null;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Student> findAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.createQuery("SELECT s FROM Student s LEFT JOIN FETCH s.homeworks", Student.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Student update(Student student) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            Student updatedStudent = entityManager.merge(student);
            entityManager.getTransaction().commit();
            return updatedStudent;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Error updating student: " + e.getMessage());
            throw new RuntimeException("Failed to update student", e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public boolean deleteById(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            Student student = entityManager.find(Student.class, id);
            if (student != null) {
                entityManager.remove(student);
                entityManager.getTransaction().commit();
                return true;
            }
            entityManager.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Error deleting student: " + e.getMessage());
            throw new RuntimeException("Failed to delete student", e);
        } finally {
            entityManager.close();
        }
    }

    public void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    public void clearAllStudents() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            entityManager.createQuery("DELETE FROM Homework").executeUpdate();
            entityManager.createQuery("DELETE FROM Student").executeUpdate();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Error clearing students: " + e.getMessage());
            throw new RuntimeException("Failed to clear students", e);
        } finally {
            entityManager.close();
        }
    }
}