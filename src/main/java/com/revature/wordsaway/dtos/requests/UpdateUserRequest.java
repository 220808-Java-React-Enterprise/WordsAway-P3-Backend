package com.revature.wordsaway.dtos.requests;

public class UpdateUserRequest {
    private String currentPassword;
    private String newPassword;
    private String email;
    private int avatarIdx;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getEmail() {
        return email;
    }

    public int getAvatarIdx() {return avatarIdx;}

    @Override
    public String toString() {
        return "UpdateUserRequest{" +
                "currentPassword='" + currentPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", email='" + email + '\'' +
                ", avatarIdx='" + avatarIdx + '\'' +
                '}';
    }
}
