package com.revature.wordsaway.models.entities;

import com.revature.wordsaway.models.GameState;
import com.revature.wordsaway.utils.customExceptions.InvalidRequestException;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.UUID;

import static com.revature.wordsaway.utils.Constants.BOARD_SIZE;

@Component
@Entity
@Table(name = "boards")
public class Board implements Cloneable{
    @Id
    private UUID id;
    @OneToOne
    @JoinColumn(name="username", referencedColumnName = "username")
    private User user;
    @Column(name = "tray", nullable = false, length = 7)
    private char[] tray;
    @Column(name = "fireballs", nullable = false)
    private int fireballs;
    @Column(name = "worms", nullable = false, length = BOARD_SIZE*BOARD_SIZE)
    private char[] worms;
    @Column(name = "letters", nullable = false, length = BOARD_SIZE*BOARD_SIZE)
    private char[] letters;
    @Column(name = "game_id", nullable = false)
    private UUID gameID;
    @Column(name = "game_state", nullable = false)
    private GameState gameState;
    @Column(name = "completed")
    private Timestamp completed;
    @Transient
    private char[][] lettersRows;
    @Transient
    private char[][] lettersColumns;

    //Delg v2
    @Column(name = "type")
    private String type;

    protected Board(){}

    public Board(UUID id, User user, char[] tray, int fireballs, char[] worms, char[] letters, UUID gameID, GameState gameState, Timestamp completed, String type) {
        this.id = id;
        this.user = user;
        this.tray = tray;
        this.fireballs = fireballs;
        this.worms = worms;
        this.letters = letters;
        this.gameID = gameID;
        this.gameState = gameState;
        this.completed = completed;
        this.type = type;
    }

    @Override
    public Board clone() {
        try {
            Board clone = (Board) super.clone();
            clone.letters = new char[letters.length];
            System.arraycopy(letters, 0, clone.letters, 0, letters.length);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public char[] getTray() {
        return tray;
    }

    public void setTray(char[] tray) {
        this.tray = tray;
    }

    public int getFireballs() {
        return fireballs;
    }

    public void addFireballs(int fireballs) {
        this.fireballs += fireballs;
    }

    public char[] getWorms() {
        return worms.clone();
    }

    public void setWorms(char[] worms) {
        this.worms = worms;
    }

    public char[] getLetters() {
        return letters.clone();
    }

    public void setLetters(char[] letters) {
        this.letters = letters;
        lettersRows = null;
        lettersColumns = null;
    }

    public UUID getGameID() {
        return gameID;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isActive(){
        return gameState == GameState.YOUR_TURN;
    }

    public Timestamp getCompleted(){
        return completed;
    }

    public char[] getRow(int index){
        if(index >= BOARD_SIZE) throw new IllegalArgumentException("Can not get row outside of board range.");
        return getRows()[index];
    }

    public char[][] getRows(){
        if(lettersRows != null) return lettersRows;
        char[][] lettersRows = new char[BOARD_SIZE][BOARD_SIZE];
        for(int i = 0; i < BOARD_SIZE; i++){
            lettersRows[i] = new char[BOARD_SIZE];
            for(int j = 0; j < BOARD_SIZE; j++){
                lettersRows[i][j] = letters[i * BOARD_SIZE + j];
            }
        }
        return lettersRows;
    }

    public char[] getColumn(int index){
        if(index >= BOARD_SIZE) throw new IllegalArgumentException("Can not get row outside of board range.");
        return getColumns()[index];
    }

    public char[][] getColumns(){
        if(lettersColumns != null) return lettersColumns;
        char[][] lettersColumns = new char[BOARD_SIZE][BOARD_SIZE];
        for(int i = 0; i < BOARD_SIZE; i++){
            lettersColumns[i] = new char[BOARD_SIZE];
            for(int j = 0; j < BOARD_SIZE; j++){
                lettersColumns[i][j] = letters[i + j * BOARD_SIZE];
            }
        }
        return lettersColumns;
    }

    public void toggleActive() {
        if (gameState == GameState.YOUR_TURN) gameState = GameState.OPPONENTS_TURN;
        else if (gameState == GameState.OPPONENTS_TURN) gameState = GameState.YOUR_TURN;
        else throw new RuntimeException("Can't toggle turns of game that has ended.");
    }

    //Delg v2
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", user=" + user +
                ", tray=" + Arrays.toString(tray) +
                ", fireballs=" + fireballs +
                ", worms=" + Arrays.toString(worms) +
                ", letters=" + Arrays.toString(letters) +
                ", gameID=" + gameID +
                ", gameState=" + gameState +
                '}';
    }
}
