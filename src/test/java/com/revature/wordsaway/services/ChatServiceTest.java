package com.revature.wordsaway.services;

import com.revature.wordsaway.dtos.requests.NewUserRequest;
import com.revature.wordsaway.models.entities.Chat;
import com.revature.wordsaway.models.entities.Message;
import com.revature.wordsaway.models.entities.User;
import com.revature.wordsaway.repositories.BoardRepository;
import com.revature.wordsaway.repositories.ChatRepository;
import com.revature.wordsaway.repositories.MessageRepository;
import com.revature.wordsaway.repositories.UserRepository;
import com.revature.wordsaway.utils.customExceptions.InvalidRequestException;
import org.apache.commons.net.ntp.TimeStamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

class ChatServiceTest {

    private ChatRepository mockChatRepository;
    private MessageRepository mockMessageRepository;
    private ChatService chatService;

    private Chat chat;

    private Message message;

    private User user;

    private Timestamp timeStamp;

    @BeforeEach
    public void setup() {

        mockChatRepository = mock(ChatRepository.class);
        mockMessageRepository = mock(MessageRepository.class);
        chatService = new ChatService(mockChatRepository, mockMessageRepository);

        user = new User("chuong@gmail.com", "password", "salt", "chuong@gmail.com", 3, 3.2f, 10, 10, true, new HashSet<>());
        this.timeStamp = new Timestamp(System.currentTimeMillis());
        message = new Message(UUID.randomUUID(), user,timeStamp, "Hello World!", chat);

        List<Message> messageList = new ArrayList<>();
        messageList.add(message);
        chat = new Chat(UUID.randomUUID(), messageList, new HashSet<>());

    }

    @Test
    void test_register_and_verify_mockChatRepository() {
        when(mockChatRepository.save(chat)).thenReturn(chat);
        chatService.register(chat);
        verify(mockChatRepository, times(1)).save(chat);
    }

    @Test
    void test_addMessage_and_verify_messageRepository() {
        when(mockChatRepository.save(chat)).thenReturn(chat);
        when(mockMessageRepository.save(message)).thenReturn(message);
        chatService.addMessage(chat, message);
        verify(mockMessageRepository, times(1)).save(message);
    }

    @Test
    void test_addMessage_and_verify_chatRepository() {
        when(mockChatRepository.save(chat)).thenReturn(chat);
        when(mockMessageRepository.save(message)).thenReturn(message);
        chatService.addMessage(chat, message);
        verify(mockChatRepository, times(1)).save(chat);
    }

    @Test
    void test_update_and_verify_mockChatRepository() {
        when(mockChatRepository.save(chat)).thenReturn(chat);
        chatService.update(chat);
        verify(mockChatRepository, times(1)).save(chat);
    }

    @Test
    void test_delete_and_verify_mockChatRepository() {
        doNothing().when(mockChatRepository).delete(chat);
        chatService.delete(chat);
        verify(mockChatRepository, times(1)).delete(chat);
    }

    @Test
    void test_getByID() {
        UUID uid = UUID.randomUUID();
        when(mockChatRepository.findByID(uid)).thenReturn(chat);
        Chat c = chatService.getByID(uid);
        assertEquals(1, c.getMessages().size());
    }

    @Test
    void test_getChatsByUsername() {
        List<Chat> chatList = new ArrayList<>();
        chatList.add(chat);
        when(mockChatRepository.findByUsername("chuong@gmail.com")).thenReturn(chatList);
        List<Chat> cl = chatService.getChatsByUsername("chuong@gmail.com");
        assertEquals(1, cl.size());
    }
}