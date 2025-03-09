package org.example;

import java.sql.*;
import sqlj.runtime.ref.DefaultContext;

public class StudentManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgre";
    private DefaultContext ctx;

    public StudentManager() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            ctx = new DefaultContext(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Добавление процедур на сервер
    public void AddNecessaryFunction() throws SQLException {
        #sql [ctx] {
            CREATE OR REPLACE FUNCTION truncate_student() RETURNS VOID AS $$
            BEGIN
            TRUNCATE TABLE students RESTART IDENTITY CASCADE;
            END;
            $$ LANGUAGE plpgsql;
        };

        #sql [ctx] {
            CREATE OR REPLACE FUNCTION insert_student(insert_name VARCHAR, insert_email VARCHAR, insert_group VARCHAR)
            RETURNS VOID AS $$
            BEGIN
            INSERT INTO students (name, email, group_name) VALUES (insert_name, insert_email, insert_group);
            END;
            $$ LANGUAGE plpgsql;
        };

        #sql [ctx] {
            CREATE OR REPLACE FUNCTION search_student_by_name(search_name VARCHAR)
            RETURNS TABLE(id INT, name VARCHAR, email VARCHAR, group_name VARCHAR) AS $$
            BEGIN
            RETURN QUERY
            SELECT students.id, students.name, students.email, students.group_name
            FROM students
            WHERE students.name ILIKE '%' || search_name || '%';
            END;
            $$ LANGUAGE plpgsql;
        };

        #sql [ctx] {
            CREATE OR REPLACE FUNCTION update_student(update_id INT, update_name VARCHAR, update_email VARCHAR, update_group VARCHAR)
            RETURNS VOID AS $$
            BEGIN
            UPDATE students SET name = update_name, email = update_email, group_name = update_group WHERE id = update_id;
            END;
            $$ LANGUAGE plpgsql;
        };

        #sql [ctx] {
            CREATE OR REPLACE FUNCTION delete_student_by_email(delete_email VARCHAR)
            RETURNS VOID AS $$
            BEGIN
            DELETE FROM students WHERE students.email = delete_email;
            END;
            $$ LANGUAGE plpgsql;
        };

        System.out.println("Процедуры успешно добавлены в student");
    }

    // Добавление студента
    public void addStudent(String name, String email, String group) throws SQLException {
        #sql [ctx] {
            SELECT insert_student(:name, :email, :group);
        };
        System.out.println("Студент добавлен.");
    }

    // Поиск студента по имени
    public String searchStudentByName(String name) throws SQLException {
        #sql iterator StudentIter (int id, String name, String email, String group_name);
        StudentIter iter;
        #sql [ctx] iter = {
                SELECT * FROM search_student_by_name(:name);
        };
        StringBuilder result = new StringBuilder();
        while (iter.next()) {
            result.append("ID: ").append(iter.id())
                    .append(", Name: ").append(iter.name())
                    .append(", Email: ").append(iter.email())
                    .append(", Group: ").append(iter.group_name())
                    .append("\n");
        }
        iter.close();
        return result.toString();
    }

    // Обновление студента
    public void updateStudent(int id, String name, String email, String group) throws SQLException {
        #sql [ctx] {
            SELECT update_student(:id, :name, :email, :group);
        };
        System.out.println("Данные студента обновлены.");
    }

    // Удаление студента по email
    public void deleteStudentByEmail(String email) throws SQLException {
        #sql [ctx] {
            SELECT delete_student_by_email(:email);
        };
        System.out.println("Студент удалён.");
    }

    // Очистка таблицы
    public void truncateStudentTable() throws SQLException {
        #sql [ctx] {
            SELECT truncate_student();
        };
        System.out.println("Таблица очищена.");
    }
}

