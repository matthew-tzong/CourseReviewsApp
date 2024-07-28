package edu.virginia.sde.reviews;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DatabaseDriver {
    private Connection connection;
    private static final String database = "jdbc:sqlite:CourseReviews.sqlite";

    public void connect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            throw new IllegalStateException("The connection is already opened");
        }
        connection = DriverManager.getConnection(database);
    }

    public void disconnect() throws SQLException {
        connection.close();
    }

    public void createTables() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            Statement statement = connection.createStatement();
            String users = "CREATE TABLE IF NOT EXISTS Users (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Username TEXT UNIQUE NOT NULL, Password TEXT NOT NULL)";
            statement.executeUpdate(users);
            String courses = "CREATE TABLE IF NOT EXISTS Courses (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Title TEXT NOT NULL, Subject TEXT NOT NULL, Number INTEGER NOT NULL)";
            statement.executeUpdate(courses);
            String reviews = "CREATE TABLE IF NOT EXISTS Reviews (ID INTEGER PRIMARY KEY AUTOINCREMENT, Username TEXT NOT NULL," +
                    "Course TEXT NOT NULL, Course_Number INTEGER NOT NULL, Course_Subject TEXT NOT NULL, Rating INTEGER NOT NULL, Comment TEXT, TimeStamp TEXT NOT NULL," +
                    " FOREIGN KEY(Username) REFERENCES Users(Username), FOREIGN KEY (Course) REFERENCES Courses(Title), FOREIGN KEY (Course_Number) REFERENCES Courses(Number), FOREIGN KEY (Course_Subject) REFERENCES Courses(Subject))";
            statement.executeUpdate(reviews);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }
        public float getAverageRating(String course, String subject, int number) throws SQLException{
            float averageRating = 0;
            if (connection == null || connection.isClosed()) {
                connect();
            }
            String getAverageRating = "SELECT AVG(Rating) FROM Reviews WHERE Course = ? AND Course_Number = ? AND Course_Subject = ?";
            PreparedStatement statement = connection.prepareStatement(getAverageRating);
            statement.setString(1,course);
            statement.setInt(2,number);
            statement.setString(3,subject);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                averageRating = rs.getFloat(1);
            }
            return averageRating;
        }

        public String getSubject(String course) throws SQLException {
            String subject = "";
            if (connection == null || connection.isClosed()) {
                connect();
            }
            String getSubject = "SELECT Subject FROM Courses WHERE Title = ?";
            PreparedStatement statement = connection.prepareStatement(getSubject);
            statement.setString(1,course);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                subject = rs.getString(1);
            }
            return subject;
        }
        public boolean validateUserReview(String username, String timestamp) throws SQLException {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            String checkUserReview = "SELECT Username FROM Reviews WHERE TimeStamp = (?) AND Username = (?)";
            PreparedStatement statement = connection.prepareStatement(checkUserReview);
            statement.setString(1,timestamp);
            statement.setString(2,username);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        }

        public int getCourseNumber(String course) throws SQLException {
            int courseNumber = 0;
            if (connection == null || connection.isClosed()) {
                connect();
            }
            String getCourseNumber = "SELECT Number FROM Courses WHERE Title = ?";
            PreparedStatement statement = connection.prepareStatement(getCourseNumber);
            statement.setString(1,course);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                courseNumber = rs.getInt(1);
            }
            return courseNumber;
        }
        public boolean addUser(String username, String password)  {
            try {
                if (connection == null || connection.isClosed()) {
                    connect();
                }
                String checkUsers = "SELECT * FROM Users";
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(checkUsers);
                while (rs.next()) {
                    String user = rs.getString("Username");
                    if (user.equals(username)) {
                        return false;
                    }
                }
                String addUser = "INSERT INTO Users(Username, Password) VALUES (?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(addUser);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                int updatedRows = preparedStatement.executeUpdate();
                return updatedRows > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        public boolean userLogin(String username, String password) {
            try {
                if (connection == null || connection.isClosed()) {
                    connect();
                }
                String userLogin = "SELECT * FROM Users WHERE Username = ? AND Password = ?";
                PreparedStatement statement = connection.prepareStatement(userLogin);
                statement.setString(1, username);
                statement.setString(2, password);
                ResultSet rs = statement.executeQuery();
                return rs.next();
            } catch (SQLException e){
                e.printStackTrace();
                return false;
            }
        }
        public List<Course> searchCourses(String subject, int number, String title) {
            List <Course> courses = new ArrayList<>();
            try {
                if (connection == null || connection.isClosed()) {
                    connect();
                }
                String searchCourses = "SELECT * FROM Courses WHERE LOWER(Title) LIKE LOWER(?) AND LOWER(Subject) = LOWER(?) AND Number = ?";
                PreparedStatement statement = connection.prepareStatement(searchCourses);
                statement.setString(1, "%" + title.toLowerCase() + "%");
                statement.setString(2, subject);
                statement.setInt(3, number);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    String courseSubject = rs.getString("Subject");
                    int courseNumber = rs.getInt("Number");
                    String courseTitle = rs.getString("Title");
                    float rating = getAverageRating(courseTitle, courseSubject, courseNumber);
                    Course course = new Course(courseSubject, courseNumber, courseTitle, rating);
                    courses.add(course);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return courses;
        }
    public List<Course> searchCoursesBySubject(String subject) {
        List <Course> courses = new ArrayList<>();
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            String searchCourses = "SELECT * FROM Courses WHERE LOWER(Subject) = LOWER(?)";
            PreparedStatement statement = connection.prepareStatement(searchCourses);
            statement.setString(1, subject);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String courseSubject = rs.getString("Subject");
                int courseNumber = rs.getInt("Number");
                String courseTitle = rs.getString("Title");
                float rating = getAverageRating(courseTitle, courseSubject, courseNumber);
                Course course = new Course(courseSubject, courseNumber, courseTitle, rating);
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
    public List<Course> searchCoursesByNumber(int number) {
        List <Course> courses = new ArrayList<>();
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            String searchCourses = "SELECT * FROM Courses WHERE Number = ?";
            PreparedStatement statement = connection.prepareStatement(searchCourses);
            statement.setInt(1, number);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String courseSubject = rs.getString("Subject");
                int courseNumber = rs.getInt("Number");
                String courseTitle = rs.getString("Title");
                float rating = getAverageRating(courseTitle, courseSubject, courseNumber);
                Course course = new Course(courseSubject, courseNumber, courseTitle, rating);
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
    public boolean validateUserAddReview(String username, String course, int number, String subject) throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            String getUsers = "SELECT * FROM Reviews WHERE Username = ? AND Course = ? AND Course_Number = ? AND Course_Subject = ?";
            PreparedStatement statement = connection.prepareStatement(getUsers);
            statement.setString(1, username);
            statement.setString(2,course);
            statement.setInt(3,number);
            statement.setString(4,subject);
            ResultSet rs = statement.executeQuery();
            return !rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Course> searchCoursesByTitle(String title) {
        List <Course> courses = new ArrayList<>();
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            String searchCourses = "SELECT * FROM Courses WHERE LOWER(Title) LIKE LOWER(?)";
            PreparedStatement statement = connection.prepareStatement(searchCourses);
            statement.setString(1, "%" + title.toLowerCase() + "%");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String courseSubject = rs.getString("Subject");
                int courseNumber = rs.getInt("Number");
                String courseTitle = rs.getString("Title");
                float rating = getAverageRating(courseTitle, courseSubject, courseNumber);
                Course course = new Course(courseSubject, courseNumber, courseTitle, rating);
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
        public void addCourse(String subject, int number, String title) {
            try {
                if (connection == null || connection.isClosed()) {
                    connect();
                }
                String addCourse = "INSERT INTO Courses (Title, Subject, Number) VALUES (?,?,?)";
                PreparedStatement statement = connection.prepareStatement(addCourse);
                statement.setString(1,title);
                statement.setString(2,subject.toUpperCase());
                statement.setInt(3,number);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        public void addReview(Review review) {
            try{
                if (connection == null || connection.isClosed()) {
                    connect();
                }
                String username = review.getUser();
                String course = review.getCourse();
                int rating = review.getRating();
                String timestamp = review.getTimestamp().toString();
                String comment = review.getComment();
                int courseNumber = review.getCourseNumber();
                String courseSubject = review.getCourseSubject();
                String query = "INSERT INTO Reviews(Username, Course, Rating, Comment, TimeStamp, Course_Number, Course_Subject) VALUES (?,?,?,?,?,?,?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1,username);
                statement.setString(2,course);
                statement.setInt(3, rating);
                statement.setString(4,comment);
                statement.setString(5,timestamp);
                statement.setInt(6,courseNumber);
                statement.setString(7,courseSubject);
                statement.executeUpdate();
             } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        public boolean validateNonDuplicateCourse(String subject, int number, String title) {
            try {
                if (connection == null || connection.isClosed()) {
                    connect();
                }
                String getExistingCourses = "SELECT * FROM Courses WHERE Number = ? AND LOWER(Subject) = LOWER(?) AND LOWER(Title) = LOWER(?)";
                PreparedStatement statement = connection.prepareStatement(getExistingCourses);
                statement.setInt(1,number);
                statement.setString(2,subject.toLowerCase());
                statement.setString(3,title.toLowerCase());
                ResultSet rs = statement.executeQuery();
                return !rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        public void deleteReview(Review review) {
            try {
                if (connection == null || connection.isClosed()) {
                    connect();
                }
                String timestamp = String.valueOf(review.getTimestamp());
                String deleteQuery = "DELETE FROM Reviews WHERE Username = ? AND Course = ? AND TimeStamp = ?";
                PreparedStatement statement = connection.prepareStatement(deleteQuery);
                statement.setString(1, review.getUser());
                statement.setString(2, review.getCourse());
                statement.setString(3, timestamp);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
    public void editReview(Review review) {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            String timestamp = review.getTimestamp().toString();
            String updateQuery = "UPDATE Reviews SET Rating = ?, Comment = ?, TimeStamp = ? WHERE Username = ? AND Course = ?";
            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setInt(1, review.getRating());
            statement.setString(2, review.getComment());
            statement.setString(3, timestamp);
            statement.setString(4, review.getUser());
            statement.setString(5, review.getCourse());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Course> getCourses() {
            List<Course> courses = new ArrayList<>();
            try {
                if (connection == null || connection.isClosed()) {
                    connect();
                }
                String getCourses = "SELECT * FROM Courses";
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(getCourses);
                while (rs.next()) {
                    String courseSubject = rs.getString("Subject");
                    int courseNumber = rs.getInt("Number");
                    String courseTitle = rs.getString("Title");
                    float rating = getAverageRating(courseTitle, courseSubject, courseNumber);
                    Course course = new Course(courseSubject, courseNumber, courseTitle, rating);
                    courses.add(course);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return courses;
        }

    public void clearTables() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            Statement statement = connection.createStatement();
            String users = ("DELETE FROM Users");
            String courses = ("DELETE FROM Courses");
            String reviews = ("DELETE FROM Reviews");
            statement.executeUpdate(users);
            statement.executeUpdate(courses);
            statement.executeUpdate(reviews);
        } catch (SQLException e) {
            throw e;
        }
    }
    public List<Review> getCourseReviews(String course, String subject, int number) {
        List<Review> reviews = new ArrayList<>();
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            String query = "SELECT Rating, Comment, TimeStamp, Username FROM Reviews WHERE Course = (?) AND Course_Number = (?) AND Course_Subject = (?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,course);
            statement.setInt(2,number);
            statement.setString(3,subject);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int rating = rs.getInt("Rating");
                String comment = rs.getString("Comment");
                String timestampStr = rs.getString("TimeStamp");
                String user = rs.getString("Username");
                Timestamp timestamp = Timestamp.valueOf(timestampStr);
                if (comment == null || comment.isEmpty()) {
                    comment = "";
                    reviews.add(new Review(user, course, rating, comment, timestamp, subject, number));
                } else {
                    reviews.add(new Review(user, course, rating, comment, timestamp, subject, number));
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public List<Review> getUserReviews(String username) {
        List<Review> reviews = new ArrayList<>();
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            String query = "SELECT Course, Rating, Comment, TimeStamp, Course_Subject, Course_Number FROM Reviews WHERE Username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String course = rs.getString("Course");
                int rating = rs.getInt("Rating");
                String comment = rs.getString("Comment");
                String timestampStr = rs.getString("TimeStamp");
                Timestamp timestamp = Timestamp.valueOf(timestampStr);
                String subject = rs.getString("Course_Subject");
                int number = rs.getInt("Course_Number");
                reviews.add(new Review(username, course, rating, comment, timestamp, subject, number));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

}
