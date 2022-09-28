package com.revature.wordsaway.repositories;

import com.revature.wordsaway.dtos.responses.OpponentResponse;
import com.revature.wordsaway.models.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    @Query(value = "SELECT * FROM users WHERE username = ?1", nativeQuery = true)
    User findUserByUsername(String username);

    @Query(value = "SELECT * FROM users WHERE email = ?1", nativeQuery = true)
    User findUserByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE username = ?1 AND password = ?2", nativeQuery = true)
    User findUserByUsernameAndPassword(String username, String password);

    @Query(value = "SELECT * FROM users WHERE username != ?1", nativeQuery = true)
    List<User> findAllOtherUsers(String username);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET password = ?2 , email = ?3, elo = ?4, games_played = ?5, games_won = ?6 WHERE username = ?1", nativeQuery = true)
    void updateUser(String username, String password, String email, float elo, int gamesPlayed, int gamesWon);
}