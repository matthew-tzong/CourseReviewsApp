package edu.virginia.sde.reviews;

import java.util.List;

public class Course {
    private String subject;
    private int number;
    private String title;
    private float rating;


    public Course(String subject, int number, String title, float rating) {
        this.subject = subject;
        this.number = number;
        this.title = title;
        this.rating = Math.round(rating * 100.0f) / 100.0f;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        if (rating == 0.0) {
            return subject + "\t\t" + number + "\t\t" + title + "\t\t--";
        }
        return subject + "\t\t" + number + "\t\t" + title + "\t\t" + rating;
    }
}
