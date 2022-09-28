package com.revature.wordsaway.models;

import com.revature.wordsaway.services.BoardService;

import javax.persistence.*;

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
    @Column(name = "elo", nullable = false)
    private float elo;
    @Column(name = "games_played", nullable = false)
    private int gamesPlayed;
    @Column(name = "games_won", nullable = false)
    private int gamesWon;
    @Column(name = "is_cpu", nullable = false)
    private boolean isCPU;

    protected User(){}
    public User(String username, String password, String salt, String email, float elo, int gamesPlayed, int gamesWon, boolean isCPU) {
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.email = email;
        this.elo = elo;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.isCPU = isCPU;
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

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof User)) return false;
        User user = (User) obj;
        return this.username.equals(user.getUsername());
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", elo=" + elo +
                ", gamesPlayed=" + gamesPlayed +
                ", gamesWon=" + gamesWon +
                ", isCPU=" + isCPU +
                '}';
    }
}
