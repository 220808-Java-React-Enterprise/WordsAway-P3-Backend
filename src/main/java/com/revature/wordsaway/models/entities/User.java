package com.revature.wordsaway.models.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "salt", nullable = false)
    private String salt;
    @Column(name = "email")
    private String email;

    @Column(name = "avatar")
    private int avatar;

    @Column(name = "elo", nullable = false)
    private float elo;
    @Column(name = "games_played", nullable = false)
    private int gamesPlayed;
    @Column(name = "games_won", nullable = false)
    private int gamesWon;
    @Column(name = "is_cpu", nullable = false)
    private boolean isCPU;

    @ManyToMany
    @JoinTable(name = "friends", joinColumns = @JoinColumn(name = "username"), inverseJoinColumns = @JoinColumn(name = "friend_name"))
    private Set<User> friends = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "chats_jnc", joinColumns = @JoinColumn(name = "username"), inverseJoinColumns = @JoinColumn(name = "chat"))
    private Set<Chat> chats = new HashSet<>();

    protected User(){}
    public User(String username, String password, String salt, String email, int avatar, float elo, int gamesPlayed, int gamesWon, boolean isCPU, Set<User> friends) {
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.email = email;
        this.avatar = avatar;
        this.elo = elo;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.isCPU = isCPU;
        this.friends = friends;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public String getEmail() {
        return email;
    }

    public int getAvatar(){
        return avatar;
    }

    public float getELO() {
        return elo;
    }

    public void setELO(float elo) {
        this.elo = elo;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public boolean isCPU() {
        return isCPU;
    }

    public Set<User> getFriends(){
        return friends;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof User)) return false;
        User user = (User) obj;
        return this.username.equals(user.getUsername());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", email='" + email + '\'' +
                ", elo=" + elo +
                ", gamesPlayed=" + gamesPlayed +
                ", gamesWon=" + gamesWon +
                ", isCPU=" + isCPU +
                ", friends=" + friends +
                '}';
    }
}
