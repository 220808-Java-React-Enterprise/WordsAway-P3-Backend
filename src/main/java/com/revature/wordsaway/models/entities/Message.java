package com.revature.wordsaway.models.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    private UUID id;
    @OneToOne
    @JoinColumn(name="username", referencedColumnName = "username")
    private User user;
    @Column(name = "created", nullable = false)
    private Timestamp created;
    @Column(name = "message", nullable = false)
    private String message;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="chat", referencedColumnName = "id")
    Chat chat;

    protected Message(){}

    public Message(UUID id, User user, Timestamp created, String message, Chat chat) {
        this.id = id;
        this.user = user;
        this.created = created;
        this.message = message;
        this.chat = chat;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Timestamp getCreated() {
        return created;
    }

    public String getMessage() {
        return message;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", user=" + user +
                ", created=" + created +
                ", message='" + message + '\'' +
                '}';
    }
}
