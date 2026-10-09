package com.studentfeedback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class FeedbackService {

    private ArrayList<Student> students;
    private HashSet<String> rollNumbers;
    private FileManager fileManager;
    private int nextId;

    private final String[] criteria = {
        "Clarity",
        "Examples",
        "Pace"
    };

    public FeedbackService() {
        fileManager = new FileManager();
        students = fileManager.loadStudents();
        rollNumbers = new HashSet<>();
        nextId = 1;

        for (Student student : students) {
            rollNumbers.add(normalize(student.getRollNumber()));

            if (student.getId() >= nextId) {
                nextId = student.getId() + 1;
            }
        }
    }

    private String normalize(String rollNumber) {
        return rollNumber.trim().toUpperCase(Locale.ROOT);
    }

    public boolean registerStudent(String name, String rollNumber) {

        String studentName = name.trim();
        String studentRoll = rollNumber.trim();

        if (studentName.isEmpty()) {
            System.out.println("Student name cannot be empty.");
            return false;
        }

        if (studentRoll.isEmpty()) {
            System.out.println("Roll number cannot be empty.");
            return false;
        }

        if (studentName.contains("|")
                || studentName.contains("\n")
                || studentName.contains("\r")) {

            System.out.println("Invalid characters in student name.");
            return false;
        }

        if (studentRoll.contains("|")
                || studentRoll.contains("\n")
                || studentRoll.contains("\r")) {

            System.out.println("Invalid characters in roll number.");
            return false;
        }

        String key = normalize(studentRoll);

        if (rollNumbers.contains(key)) {
            System.out.println("Roll number already exists.");
            return false;
        }

        Student student = new Student(
                nextId,
                studentName,
                studentRoll
        );

        students.add(student);

        if (!fileManager.saveStudents(students)) {
            students.remove(students.size() - 1);
            return false;
        }

        rollNumbers.add(key);

        System.out.println(
                "Student " + nextId + " saved. Feedback not submitted."
        );

        nextId++;

        return true;
    }

    public void viewStudents() {

        if (students.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        System.out.println("\nSTUDENT LIST");

        for (Student student : students) {

            System.out.println(
                    "ID: " + student.getId()
                    + " | Name: " + student.getName()
                    + " | Roll Number: " + student.getRollNumber()
            );
        }
    }

    private Student findStudent(int id) {

        for (Student student : students) {

            if (student.getId() == id) {
                return student;
            }
        }

        return null;
    }

    public Student getStudent(int id) {
        return findStudent(id);
    }

    public boolean submitFeedback(
            int studentId,
            int clarity,
            int examples,
            int pace,
            String comment) {

        Student student = findStudent(studentId);

        if (student == null) {
            System.out.println("Student not found.");
            return false;
        }

        if (clarity < 1 || clarity > 5
                || examples < 1 || examples > 5
                || pace < 1 || pace > 5) {

            System.out.println(
                    "Ratings must be between 1 and 5."
            );

            return false;
        }

        String cleanComment = comment.trim();

        if (cleanComment.isEmpty()) {
            System.out.println("Comment cannot be empty.");
            return false;
        }

        if (cleanComment.contains("|")
                || cleanComment.contains("\n")
                || cleanComment.contains("\r")) {

            System.out.println("Invalid characters in comment.");
            return false;
        }

        int[] ratings = {
            clarity,
            examples,
            pace
        };

        int[] oldRatings = student.getRatings();
        String oldComment = student.getComment();

        student.submitFeedback(ratings, cleanComment);

        if (!fileManager.saveStudents(students)) {
            student.submitFeedback(oldRatings, oldComment);
            return false;
        }

        System.out.println("Feedback saved.");

        return true;
    }

    public void viewStudentFeedback(int studentId) {

        Student student = findStudent(studentId);

        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.println("\nMY FEEDBACK");

        System.out.println("Student ID: " + student.getId());
        System.out.println("Name: " + student.getName());
        System.out.println("Roll Number: " + student.getRollNumber());

        if (!student.hasFeedback()) {
            System.out.println("Feedback not submitted.");
            return;
        }

        int[] ratings = student.getRatings();

        System.out.println("Clarity: " + ratings[0]);
        System.out.println("Examples: " + ratings[1]);
        System.out.println("Pace: " + ratings[2]);
        System.out.println("Comment: " + student.getComment());
    }

    public void viewSubmittedFeedback() {

        if (students.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        boolean found = false;

        System.out.println("\nSUBMITTED FEEDBACK");

        for (Student student : students) {

            if (student.hasFeedback()) {

                found = true;

                int[] ratings = student.getRatings();

                System.out.println(
                        "\nStudent ID: " + student.getId()
                );

                System.out.println(
                        "Name: " + student.getName()
                );

                System.out.println(
                        "Roll Number: " + student.getRollNumber()
                );

                System.out.println(
                        "Clarity: " + ratings[0]
                );

                System.out.println(
                        "Examples: " + ratings[1]
                );

                System.out.println(
                        "Pace: " + ratings[2]
                );

                System.out.println(
                        "Comment: " + student.getComment()
                );
            }
        }

        if (!found) {
            System.out.println("No feedback submitted.");
        }
    }

    public String createReport() {

        int[] totals = {
            0,
            0,
            0
        };

        int submitted = 0;

        for (Student student : students) {

            if (student.hasFeedback()) {

                submitted++;

                int[] ratings = student.getRatings();

                for (int i = 0; i < 3; i++) {
                    totals[i] += ratings[i];
                }
            }
        }

        int pending = students.size() - submitted;

        StringBuilder report = new StringBuilder();

        report.append("STUDENT FEEDBACK COLLECTOR\n");
        report.append("==========================\n\n");

        report.append(
                "Total students: "
                        + students.size()
                        + "\n"
        );

        report.append(
                "Submitted: "
                        + submitted
                        + "\n"
        );

        report.append(
                "Pending: "
                        + pending
                        + "\n\n"
        );

        if (submitted == 0) {

            report.append("No feedback yet.\n");

        } else {

            report.append(
                    String.format(
                            Locale.ROOT,
                            "Clarity: %.2f%n",
                            totals[0] / (double) submitted
                    )
            );

            report.append(
                    String.format(
                            Locale.ROOT,
                            "Examples: %.2f%n",
                            totals[1] / (double) submitted
                    )
            );

            report.append(
                    String.format(
                            Locale.ROOT,
                            "Pace: %.2f%n",
                            totals[2] / (double) submitted
                    )
            );
        }

        return report.toString();
    }

    public void showReport() {

        String report = createReport();

        System.out.println("\n" + report);
    }

    public boolean saveReport() {

        String report = createReport();

        if (fileManager.saveReport(report)) {
            System.out.println("Report saved successfully.");
            return true;
        }

        return false;
    }

    public boolean hasStudents() {
        return !students.isEmpty();
    }
}