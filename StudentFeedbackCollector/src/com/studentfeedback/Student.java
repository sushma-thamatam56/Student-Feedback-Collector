package com.studentfeedback;

public class Student extends User {

    private String rollNumber;
    private String comment;
    private int[] ratings;

    public Student(int id, String name, String rollNumber) {
        super(id, name);
        this.rollNumber = rollNumber;
        this.comment = "";
        this.ratings = new int[]{0, 0, 0};
    }

    public Student(int id, String name, String rollNumber, int[] ratings, String comment) {
        super(id, name);
        this.rollNumber = rollNumber;
        this.ratings = ratings.clone();
        this.comment = comment;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public String getComment() {
        return comment;
    }

    public int[] getRatings() {
        return ratings.clone();
    }

    public boolean hasFeedback() {
        return ratings[0] >= 1 && ratings[0] <= 5
                && ratings[1] >= 1 && ratings[1] <= 5
                && ratings[2] >= 1 && ratings[2] <= 5;
    }

    public void submitFeedback(int[] ratings, String comment) {
        this.ratings = ratings.clone();
        this.comment = comment;
    }

    @Override
    public void showProfile() {
        System.out.println("Student ID: " + getId());
        System.out.println("Student Name: " + getName());
        System.out.println("Roll Number: " + rollNumber);
    }
}