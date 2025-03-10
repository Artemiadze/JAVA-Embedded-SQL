package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Objects;

public class StudentGUI extends JFrame {
    private final JTextField nameField;
    private final JTextField emailField;
    private final JTextField groupField;
    private final JTextField searchField;
    private final JTextArea outputArea;
    private final StudentManager dbManager;

    public StudentGUI() {
        // Устанавливаем параметры окна
        setTitle("Управление");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 2));

        dbManager = new StudentManager();

        // Создание элементов интерфейса
        nameField = new JTextField();
        emailField = new JTextField();
        groupField = new JTextField();
        searchField = new JTextField();
        outputArea = new JTextArea(5, 20);
        outputArea.setEditable(false);

        // Кнопки управления
        JButton addButton = new JButton("Добавить");
        JButton searchButton = new JButton("Найти");
        JButton updateButton = new JButton("Обновить");
        JButton deleteButton = new JButton("Удалить");
        JButton clearTableButton = new JButton("Очистить таблицу");
        JButton createDBButton = new JButton("Создать БД");
        JButton dropDBButton = new JButton("Удалить БД");

        // Добавление обработчиков кнопок
        addButton.addActionListener(e -> addStudent());
        searchButton.addActionListener(e -> searchStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        clearTableButton.addActionListener(e -> dbManager.truncateStudentTable());
        createDBButton.addActionListener(e -> dbManager.createDatabase());
        dropDBButton.addActionListener(e -> dbManager.dropDatabase());

        // Добавляем элементы в окно
        add(new JLabel("Имя:"));
        add(nameField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Группа:"));
        add(groupField);
        add(new JLabel("Поиск/Удаление (Имя/Почта):"));
        add(searchField);
        add(addButton);
        add(searchButton);
        add(updateButton);
        add(deleteButton);
        add(clearTableButton);
        add(createDBButton);
        add(dropDBButton);
        add(new JScrollPane(outputArea));

        setVisible(true);
    }

    // Методы обработки событий
    private void addStudent() throws SQLException {
        String name = nameField.getText();
        String email = emailField.getText();
        String group = groupField.getText();
        dbManager.addStudent(name, email, group);
        outputArea.setText("Студент добавлен: " + name);
    }

    private void searchStudent() throws SQLException {
        String name = searchField.getText();
        String result = dbManager.searchStudentByName(name);
        outputArea.setText(result);
        if (Objects.equals(result, "")){
            //Нет пользователя с таким именем
            outputArea.setText("Nothing");
        }
    }

    private void updateStudent() throws SQLException {
        String name = nameField.getText();
        String email = emailField.getText();
        String group = groupField.getText();
        dbManager.updateStudent(name, email, group);
        outputArea.setText("Студент обновлён: " + name);
    }

    private void deleteStudent() throws SQLException {
        String name = searchField.getText();
        dbManager.deleteStudentByEmail(name);
        outputArea.setText("Студент удалён: " + name);
    }

    // запуск GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentGUI::new);
    }
}