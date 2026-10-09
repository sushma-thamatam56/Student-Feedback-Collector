package com.studentfeedback;

import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static FeedbackService service = new FeedbackService();
    private static Teacher teacher = new Teacher(0, "Teacher");

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("     STUDENT FEEDBACK COLLECTOR");
        System.out.println("=================================");

        showMenu();

        scanner.close();
    }

    public static void showMenu() {

        while (true) {

            System.out.println("\nMAIN MENU");
            System.out.println("1. Teacher menu");
            System.out.println("2. Student menu");
            System.out.println("0. Exit");

            int choice = readInt("Enter your choice: ");

            switch (choice) {

                case 1:
                    teacherMenu();
                    break;

                case 2:
                    studentMenu();
                    break;

                case 0:
                    System.out.println(
                            "Thank you for using Student Feedback Collector."
                    );
                    return;

                default:
                    System.out.println(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    private static void teacherMenu() {

        while (true) {

            System.out.println("\nTEACHER MENU");
            System.out.println("1. Register student");
            System.out.println("2. View students");
            System.out.println("3. View submitted feedback");
            System.out.println("4. View and save course summary");
            System.out.println("0. Back");

            int choice = readInt("Enter your choice: ");

            switch (choice) {

                case 1:
                    registerStudent();
                    break;

                case 2:
                    service.viewStudents();
                    break;

                case 3:
                    service.viewSubmittedFeedback();
                    break;

                case 4:
                    showTeacherReport();
                    break;

                case 0:
                    return;

                default:
                    System.out.println(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    private static void registerStudent() {

        String name = readText("Enter student name: ");

        String rollNumber =
                readText("Enter roll number: ");

        service.registerStudent(
                name,
                rollNumber
        );
    }

    private static void showTeacherReport() {

        service.showReport();

        String choice =
                readText("Save report to report.txt? (Y/N): ");

        if (choice.equalsIgnoreCase("Y")) {
            service.saveReport();
        }
    }

    private static void studentMenu() {

        int studentId =
                readInt("Enter student ID: ");

        Student student =
                service.getStudent(studentId);

        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        while (true) {

            System.out.println("\nSTUDENT MENU");
            System.out.println("Student ID: " + student.getId());
            System.out.println("1. View my profile");
            System.out.println("2. Submit or replace my feedback");
            System.out.println("3. View my feedback");
            System.out.println("0. Back");

            int choice =
                    readInt("Enter your choice: ");

            switch (choice) {

                case 1:
                    student.showProfile();
                    break;

                case 2:
                    submitFeedback(student);
                    break;

                case 3:
                    service.viewStudentFeedback(
                            student.getId()
                    );
                    break;

                case 0:
                    return;

                default:
                    System.out.println(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    private static void submitFeedback(Student student) {

        System.out.println("\nRATE FROM 1 TO 5");

        int clarity =
                readRating("Clarity: ");

        int examples =
                readRating("Examples: ");

        int pace =
                readRating("Pace: ");

        String comment =
                readText("Comment: ");

        service.submitFeedback(
                student.getId(),
                clarity,
                examples,
                pace,
                comment
        );
    }

    private static int readRating(String message) {

        while (true) {

            int value = readInt(message);

            if (value >= 1 && value <= 5) {
                return value;
            }

            System.out.println(
                    "Rating must be between 1 and 5."
            );
        }
    }

    private static int readInt(String message) {

        while (true) {

            System.out.print(message);

            String input =
                    scanner.nextLine().trim();

            try {

                return Integer.parseInt(input);

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid number."
                );
            }
        }
    }

    private static String readText(String message) {

        System.out.print(message);

        return scanner.nextLine();
    }
}