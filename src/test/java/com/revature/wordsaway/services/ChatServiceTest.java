package com.revature.wordsaway.services;

import com.revature.wordsaway.dtos.requests.NewUserRequest;
import com.revature.wordsaway.models.entities.Chat;
import com.revature.wordsaway.models.entities.Message;
import com.revature.wordsaway.models.entities.User;
import com.revature.wordsaway.repositories.BoardRepository;
import com.revature.wordsaway.repositories.ChatRepository;
import com.revature.wordsaway.repositories.MessageRepository;
import com.revature.wordsaway.repositories.UserRepository;
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
        chat = new Chat(UUID.randomUUID(), new ArrayList<Message>(), new HashSet<>());
        this.timeStamp = new Timestamp(System.currentTimeMillis());
        message = new Message(UUID.randomUUID(), user,timeStamp, "Hello World!", chat);

//        mockUserRepo = mock(UserRepository.class);
//        mockBoardRepo = mock(BoardRepository.class);
//        userService = new UserService(mockUserRepo, mockBoardRepo);
//        mockRequest = mock(NewUserRequest.class);
//        when(mockRequest.getUsername()).thenReturn("username");
//        when(mockRequest.getPassword()).thenReturn("password");
//        when(mockRequest.getEmail()).thenReturn("username@email.com");
//        when(mockRequest.getSalt()).thenReturn("00000000000000000000000000000000");
//        tokenServiceMockedStatic = mockStatic(TokenService.class);
//        tokenServiceMockedStatic.when(() -> TokenService.generateToken(any())).thenReturn("testtoken");
//        mockUser = mock(User.class);

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
    void delete() {
    }

    @Test
    void getByID() {
    }

    @Test
    void getChatsByUsername() {
    }
}