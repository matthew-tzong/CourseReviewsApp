package edu.virginia.sde.reviews;

import java.sql.Timestamp;

public class Review {
    private int rating;
    private Timestamp timestamp;
    private String comment;
    private String user;
    private String course;
    private String courseSubject;
    private int courseNumber;

    public Review(String user, String course, int rating, String comment, Timestamp timestamp, String courseSubject, int courseNumber) {
        setRating(rating);
        setTimestamp(timestamp);
        setUser(user);
        setComment(comment);
        setCourse(course);
        this.courseSubject = courseSubject;
        this.courseNumber = courseNumber;
    }
    public int getCourseNumber() {
        return courseNumber;
    }
    public String getCourseSubject() {
        return courseSubject;
    }
    public void setRating(int rating) {
        if (rating <=5 && rating >= 1) {
            this.rating = rating;}
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getRating() {
        return rating;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return course + "\t\t" + rating + "\t\t" + timestamp + "\t\t" + comment;
    }

    public String getUser() {
        return user;
    }
    public String getComment() {
        return comment;
    }
    public String getCourse() {
        return course;
    }
}
