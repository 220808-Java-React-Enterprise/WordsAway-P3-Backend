package com.revature.wordsaway.dtos.requests;

public class UpdateUserRequest {
    private String currentPassword;
    private String newPassword;
    private String email;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "UpdateUserRequest{" +
                "currentPassword='" + currentPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
