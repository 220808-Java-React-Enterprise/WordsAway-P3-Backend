package com.revature.wordsaway.models.entities;

import jdk.nashorn.internal.objects.annotations.Getter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "chats")
public class Chat {
    @Id
    private UUID id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="chat", referencedColumnName = "id")
    List<Message> messages;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "chats_jnc", joinColumns = @JoinColumn(name = "chat"), inverseJoinColumns = @JoinColumn(name = "username"))
    Set<User> users;

    protected Chat() {}

    public Chat(UUID id, List<Message> messages, Set<User> users) {
        this.id = id;
        this.messages = messages;
        this.users = users;
    }

    public UUID getId() {
        return id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    @Getter
    public Set<User> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", messages=" + messages +
                '}';
    }
}
