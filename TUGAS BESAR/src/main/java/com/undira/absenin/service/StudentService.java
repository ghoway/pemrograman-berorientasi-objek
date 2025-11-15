package com.undira.absenin.service;

import com.undira.absenin.dao.StudentDAO;
import com.undira.absenin.model.Student;
import java.sql.SQLException;
import java.util.List;

public class StudentService {
    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
    }

    // Metode ini sekarang mengembalikan objek Student yang sudah ada ID-nya
    public Student addStudent(Student student) throws SQLException {
        int newId = studentDAO.add(student);
        student.setId(newId); // Set ID yang baru dibuat ke objek student
        return student;
    }

    public void updateStudent(Student student) throws SQLException {
        studentDAO.update(student);
    }

    public void deleteStudent(int id) throws SQLException {
        studentDAO.delete(id);
    }

    public Student getStudentByNim(String nim) throws SQLException {
        return studentDAO.findByNim(nim);
    }
    
    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.findAll();
    }
}