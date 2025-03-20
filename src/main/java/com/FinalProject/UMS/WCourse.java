package com.FinalProject.UMS;

public class WCourse {
    private String courseCode;
    private String courseName;
    private String subjectCode;
    private String sectionNumber;
    private int capacity;
    private String lectureTime;
    private String examDate;
    private String location;
    private String teacherName;

    // Constructor, getters, and setters
    public WCourse(String courseCode, String courseName, String subjectCode, String sectionNumber, int capacity, String lectureTime, String examDate, String location, String teacherName) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.subjectCode = subjectCode;
        this.sectionNumber = sectionNumber;
        this.capacity = capacity;
        this.lectureTime = lectureTime;
        this.examDate = examDate;
        this.location = location;
        this.teacherName = teacherName;
    }

    // Getters and setters for all fields
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSectionNumber() { return sectionNumber; }
    public void setSectionNumber(String sectionNumber) { this.sectionNumber = sectionNumber; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getLectureTime() { return lectureTime; }
    public void setLectureTime(String lectureTime) { this.lectureTime = lectureTime; }

    public String getExamDate() { return examDate; }
    public void setExamDate(String examDate) { this.examDate = examDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
}