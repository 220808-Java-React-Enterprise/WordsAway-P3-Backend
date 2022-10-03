package com.revature.wordsaway.models.entities;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chats")
public class Chat {
    @Id
    private UUID id;

    @OneToMany
    @JoinColumn(name="chat", referencedColumnName = "id")
    List<Message> messages;


    protected Chat() {}

    public Chat(UUID id, List<Message> messages) {
        this.id = id;
        this.messages = messages;
    }

    public UUID getId() {
        return id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", messages=" + messages +
                '}';
    }
}
