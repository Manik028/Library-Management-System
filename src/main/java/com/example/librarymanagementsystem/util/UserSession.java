package com.example.librarymanagementsystem.util;

public class UserSession {
    private static String currentRole;
    private static String currentUsername;
    private static String loginAttemptRole;
    private static String currentSubject;

    // Fields for Library Card & Profile
    private static String fullName;
    private static String dob;
    private static String studentId;
    private static String photoPath;
    private static String password; // ADDED THIS

    public static String getCurrentRole() { return currentRole; }
    public static void setCurrentRole(String role) { currentRole = role; }

    public static String getCurrentUsername() { return currentUsername; }
    public static void setCurrentUsername(String username) { currentUsername = username; }

    // ADDED THIS to fix the "cannot find symbol" error in ActivityController
    public static String getUsername() { return currentUsername; }

    public static String getLoginAttemptRole() { return loginAttemptRole; }
    public static void setLoginAttemptRole(String role) { loginAttemptRole = role; }

    public static String getCurrentSubject() { return currentSubject; }
    public static void setCurrentSubject(String subject) { currentSubject = subject; }

    public static String getFullName() { return fullName; }
    public static void setFullName(String fullName) { UserSession.fullName = fullName; }

    public static String getDob() { return dob; }
    public static void setDob(String dob) { UserSession.dob = dob; }

    public static String getStudentId() { return studentId; }
    public static void setStudentId(String studentId) { UserSession.studentId = studentId; }

    public static String getPhotoPath() { return photoPath; }
    public static void setPhotoPath(String photoPath) { UserSession.photoPath = photoPath; }

    // ADDED THIS for the ProfileController to check the old password
    public static String getPassword() { return password; }
    public static void setPassword(String password) { UserSession.password = password; }

    public static void cleanUserSession() {
        currentRole = null; currentUsername = null; loginAttemptRole = null; currentSubject = null;
        fullName = null; dob = null; studentId = null; photoPath = null; password = null;
    }
}