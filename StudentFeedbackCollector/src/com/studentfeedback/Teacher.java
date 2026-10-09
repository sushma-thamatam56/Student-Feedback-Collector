package com.studentfeedback;

public class Teacher extends User {

    public Teacher(int id, String name) {
        super(id, name);
    }

    @Override
    public void showProfile() {
        System.out.println("Teacher ID: " + getId());
        System.out.println("Teacher Name: " + getName());
    }
}