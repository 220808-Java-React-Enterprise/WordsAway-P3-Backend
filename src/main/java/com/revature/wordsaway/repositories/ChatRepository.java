package com.revature.wordsaway.repositories;

import com.revature.wordsaway.models.entities.Chat;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRepository extends CrudRepository<Chat, UUID> {

    @Query(value = "SELECT * FROM chats WHERE id = ?1", nativeQuery = true)
    Chat findByID(UUID id);

    @Query(value = "SELECT chats.* FROM chats INNER JOIN chats_jnc ON chats_jnc.chat=chats.id WHERE username = ?1", nativeQuery = true)
    List<Chat> findByUsername(String username);
}
