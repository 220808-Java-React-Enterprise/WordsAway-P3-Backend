package com.revature.wordsaway.repositories;

import com.revature.wordsaway.models.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {

    @Query(value = "SELECT * FROM chats WHERE id = ?1", nativeQuery = true)
    Chat findByID(UUID id);

    @Query(value = "SELECT chats.* FROM chats INNER JOIN chats_jnc ON chats_jnc.chat=chats.id WHERE username = ?1", nativeQuery = true)
    List<Chat> findByUsername(String username);

//    @Transactional
//    @Modifying
//    @Query(value = "INSERT INTO users_jnc (username, chat) VALUES (?1, ?2)", nativeQuery = true)
//    void addUser(String username, String chatID);
//
//    @Transactional
//    @Modifying
//    @Query(value = "REMOVE FROM users_jnc WHERE username = ?1 AND chat = ?2", nativeQuery = true)
//    void removeUser(String username, String chatID);
}
