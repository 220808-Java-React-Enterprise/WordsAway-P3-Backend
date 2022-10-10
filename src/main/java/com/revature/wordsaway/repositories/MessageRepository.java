package com.revature.wordsaway.repositories;

import com.revature.wordsaway.models.entities.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepository extends CrudRepository<Message, UUID> {
}
