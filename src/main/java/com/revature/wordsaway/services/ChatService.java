package com.revature.wordsaway.services;

import com.revature.wordsaway.models.entities.Chat;
import com.revature.wordsaway.models.entities.Message;
import com.revature.wordsaway.models.entities.User;
import com.revature.wordsaway.repositories.ChatRepository;
import com.revature.wordsaway.repositories.MessageRepository;
import com.revature.wordsaway.utils.customExceptions.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private static ChatRepository chatRepository;
    private static MessageRepository messageRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository, MessageRepository messageRepository){
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
    }

    public static void register(Chat chat){
        chatRepository.save(chat);
    }

    public static void addMessage(Chat chat, Message message){
        chat.getMessages().add(message);
        message.setChat(chat);
        chatRepository.save(chat);
        messageRepository.save(message);
    }

    public static void addMessage(Message message){
        messageRepository.save(message);
    }

    public static void update(Chat chat){
        chatRepository.save(chat);
    }

//    public static void addUser(User user, Chat chat){
//        chatRepository.addUser(user.getUsername(), chat.getId().toString());
//    }
//
//    public static void removeUser(User user, Chat chat){
//        chatRepository.addUser(user.getUsername(), chat.getId().toString());
//    }

    public static void delete(Chat chat){
        for(Message message : chat.getMessages()){
            messageRepository.delete(message);
        }
        chatRepository.delete(chat);
    }

    public static Chat getByID(UUID id){
        Chat chat = chatRepository.findByID(id);
        if(chat == null) throw new InvalidRequestException("No chat with id " + id + " found.");
        return chat;
    }

    public static List<Chat> getChatsByUsername(String username){
        return chatRepository.findByUsername(username);
    }
}
