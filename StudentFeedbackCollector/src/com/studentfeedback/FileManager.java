package com.studentfeedback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class FileManager {

    private final Path dataFolder = Path.of("data");
    private final Path studentsFile = dataFolder.resolve("students.txt");
    private final Path reportFile = dataFolder.resolve("report.txt");

    public FileManager() {
        try {
            Files.createDirectories(dataFolder);
        } catch (IOException e) {
            System.out.println("Unable to create data folder.");
        }
    }

    public ArrayList<Student> loadStudents() {

        ArrayList<Student> students = new ArrayList<>();

        if (!Files.exists(studentsFile)) {
            return students;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                studentsFile, StandardCharsets.UTF_8)) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {

                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = line.split("\\|", -1);

                if (fields.length != 5) {
                    System.out.println(
                            "Invalid record at students.txt line "
                                    + lineNumber);
                    return new ArrayList<>();
                }

                int id;

                try {
                    id = Integer.parseInt(fields[0]);
                } catch (NumberFormatException e) {
                    System.out.println(
                            "Invalid ID at students.txt line "
                                    + lineNumber);
                    return new ArrayList<>();
                }

                if (id <= 0) {
                    System.out.println(
                            "Invalid ID at students.txt line "
                                    + lineNumber);
                    return new ArrayList<>();
                }

                String name = fields[1];
                String rollNumber = fields[2];
                String ratingText = fields[3];
                String comment = fields[4];

                if (name.trim().isEmpty()
                        || rollNumber.trim().isEmpty()) {

                    System.out.println(
                            "Invalid student record at line "
                                    + lineNumber);

                    return new ArrayList<>();
                }

                String[] ratingValues = ratingText.split(",", -1);

                if (ratingValues.length != 3) {
                    System.out.println(
                            "Invalid ratings at line "
                                    + lineNumber);

                    return new ArrayList<>();
                }

                int[] ratings = new int[3];

                try {
                    for (int i = 0; i < 3; i++) {
                        ratings[i] = Integer.parseInt(ratingValues[i]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(
                            "Invalid ratings at line "
                                    + lineNumber);

                    return new ArrayList<>();
                }

                boolean allZero =
                        ratings[0] == 0
                        && ratings[1] == 0
                        && ratings[2] == 0;

                boolean allValid =
                        ratings[0] >= 1 && ratings[0] <= 5
                        && ratings[1] >= 1 && ratings[1] <= 5
                        && ratings[2] >= 1 && ratings[2] <= 5;

                if (allZero) {

                    if (!comment.isEmpty()) {
                        System.out.println(
                                "Invalid feedback record at line "
                                        + lineNumber);

                        return new ArrayList<>();
                    }

                } else if (allValid) {

                    if (comment.trim().isEmpty()) {
                        System.out.println(
                                "Invalid feedback record at line "
                                        + lineNumber);

                        return new ArrayList<>();
                    }

                } else {

                    System.out.println(
                            "Invalid feedback record at line "
                                    + lineNumber);

                    return new ArrayList<>();
                }

                for (Student existing : students) {

                    if (existing.getId() == id) {
                        System.out.println(
                                "Duplicate student ID at line "
                                        + lineNumber);

                        return new ArrayList<>();
                    }

                    if (existing.getRollNumber()
                            .equalsIgnoreCase(rollNumber.trim())) {

                        System.out.println(
                                "Duplicate roll number at line "
                                        + lineNumber);

                        return new ArrayList<>();
                    }
                }

                students.add(
                        new Student(
                                id,
                                name,
                                rollNumber,
                                ratings,
                                comment
                        )
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Error reading students.txt");

            return new ArrayList<>();
        }

        return students;
    }

    public boolean saveStudents(ArrayList<Student> students) {

        Path tempFile =
                dataFolder.resolve("students_temp.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(
                tempFile,
                StandardCharsets.UTF_8)) {

            for (Student student : students) {

                int[] ratings = student.getRatings();

                writer.write(
                        student.getId()
                                + "|"
                                + student.getName()
                                + "|"
                                + student.getRollNumber()
                                + "|"
                                + ratings[0]
                                + ","
                                + ratings[1]
                                + ","
                                + ratings[2]
                                + "|"
                                + student.getComment()
                );

                writer.newLine();
            }

        } catch (IOException e) {

            System.out.println(
                    "Error saving students.txt");

            return false;
        }

        try {

            Files.move(
                    tempFile,
                    studentsFile,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (IOException e) {

            System.out.println(
                    "Error replacing students.txt");

            return false;
        }

        return true;
    }

    public boolean saveReport(String report) {

        try (BufferedWriter writer = Files.newBufferedWriter(
                reportFile,
                StandardCharsets.UTF_8)) {

            writer.write(report);

        } catch (IOException e) {

            System.out.println(
                    "Error saving report.txt");

            return false;
        }

        return true;
    }
}