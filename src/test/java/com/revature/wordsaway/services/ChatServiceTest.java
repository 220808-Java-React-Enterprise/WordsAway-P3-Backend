package com.revature.wordsaway.services;

import com.revature.wordsaway.dtos.requests.NewUserRequest;
import com.revature.wordsaway.models.entities.Chat;
import com.revature.wordsaway.models.entities.Message;
import com.revature.wordsaway.models.entities.User;
import com.revature.wordsaway.repositories.BoardRepository;
import com.revature.wordsaway.repositories.ChatRepository;
import com.revature.wordsaway.repositories.MessageRepository;
import com.revature.wordsaway.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

class ChatServiceTest {

    private ChatRepository mockChatRepository;
    private MessageRepository mockMessageRepository;

    private ChatService chatService;



    @BeforeEach
    public void setup() {
        mockChatRepository = mock(ChatRepository.class);
        mockMessageRepository = mock(MessageRepository.class);
        chatService = new ChatService(mockChatRepository, mockMessageRepository);

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
    void register() {
        Chat c = new Chat(UUID.randomUUID(), new ArrayList<Message>(), new HashSet<>());
        when(mockChatRepository.save(c)).thenReturn(c);
        chatService.register(c);
        verify(mockChatRepository, times(1)).save(c);
    }

    @Test
    void addMessage() {
        Chat c = new Chat(UUID.randomUUID(), new ArrayList<Message>(), new HashSet<>());
        Message message = new Message();
        when(mockChatRepository.save(c)).thenReturn(c);
        when(mockMessageRepository.save(c)).thenReturn(c);
    }

    @Test
    void update() {
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