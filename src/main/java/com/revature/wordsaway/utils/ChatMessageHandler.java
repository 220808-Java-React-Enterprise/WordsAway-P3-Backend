package com.revature.wordsaway.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.wordsaway.models.entities.Chat;
import com.revature.wordsaway.models.entities.Message;
import com.revature.wordsaway.models.entities.User;
import com.revature.wordsaway.services.ChatService;
import com.revature.wordsaway.services.UserService;
import com.revature.wordsaway.utils.customExceptions.NetworkException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Component
public class ChatMessageHandler extends TextWebSocketHandler {
    private final HashMap<User, WebSocketSession> users = new HashMap<>();
    private final HashMap<UUID, Set<User>> chats = new HashMap<>();
    private final List<WebSocketSession> webSocketSessions = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        webSocketSessions.add(session);
        System.out.println("New connection from " + session.getRemoteAddress());
        System.out.println("Connections: " + webSocketSessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        webSocketSessions.remove(session);
        System.out.println("Closed connection to " + session.getRemoteAddress());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> webSocketMessage) throws Exception {
        super.handleMessage(session, webSocketMessage);
        try {
            MessageStub stub = new ObjectMapper().readValue((String) webSocketMessage.getPayload(), MessageStub.class);
            switch (stub.getType()){
                case "LOGIN": {
                    try {
                        User user = UserService.getByUsername(stub.getUser());
                        users.remove(user);
                        users.put(user, session);
                        System.out.println("User " + stub.getUser() + " logged in.");
                        session.sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"LOGIN_ACK\", \"data\":\"\"}"));
                        for (Chat c : ChatService.getChatsByUsername(user.getUsername())) {
                            chats.put(c.getId(), c.getUsers());
                            session.sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"" + c.getId() + "\", \"type\":\"START_CHAT\", \"data\":\"\"}"));
                            for (Message m : c.getMessages()) {
                                session.sendMessage(new TextMessage("{\"user\":\"" + m.getUser().getUsername() + "\", \"id\":\"" + c.getId() + "\", \"type\":\"MESSAGE\", \"data\":\"" + m.getMessage() + "\"}"));
                            }
                        }
                    } catch (Exception e) {
                        session.sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"" + e + "\"}"));
                    }
                    break;
                }case "START_CHAT": {
                    UUID id = UUID.randomUUID();
                    Chat chat = new Chat(id, new ArrayList<>(), new HashSet<>());
                    chats.put(id, new HashSet<>());
                    try {
                        User user = UserService.getByUsername(stub.user);
                        chats.get(id).add(user);
                        chat.getUsers().add(user);
                        if (users.containsKey(user)) users.get(user).sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"" + id + "\", \"type\":\"START_CHAT_ACK\", \"data\":\"\"}"));
                        if (!stub.getData().matches("\\s*")) {
                            for (String username : stub.getData().split(",")) {
                                user = UserService.getByUsername(username);
                                chats.get(id).add(user);
                                if (users.containsKey(user)) users.get(user).sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"" + id + "\", \"type\":\"START_CHAT_ACK\", \"data\":\"\"}"));
                                chat.getUsers().add(user);
                            }
                        }
                        ChatService.register(chat);
                        System.out.println("Chat Started with id: " + id + " and users: " + chats.get(id));
                        session.sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"" + id + "\", \"type\":\"START_CHAT_ACK\", \"data\":\"\"}"));
                    } catch (Exception e) {
                        session.sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"" + e + "\"}"));
                    }
                    break;
                }case "ADD_USER": {
                    UUID id = UUID.fromString(stub.getID());
                    Chat chat = ChatService.getByID(id);
                    try {
                        User user = UserService.getByUsername(stub.user);
                        chats.get(id).add(user);
                        users.get(user).sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"" + id + "\", \"type\":\"START_CHAT_ACK\", \"data\":\"\"}"));
                        chat.getUsers().add(user);
                        for (String username : stub.getData().split(",")) {
                            user = UserService.getByUsername(username);
                            chats.get(id).add(user);
                            chat.getUsers().add(user);
                            if (users.containsKey(user)) users.get(user).sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"" + id + "\", \"type\":\"START_CHAT_ACK\", \"data\":\"\"}"));
                            for (Message m : chat.getMessages()) {
                                if (users.containsKey(user))
                                    users.get(user).sendMessage(new TextMessage("{\"user\":\"" + m.getUser().getUsername() + "\", \"id\":\"" + chat.getId() + "\", \"type\":\"MESSAGE\", \"data\":\"" + m.getMessage() + "\"}"));
                            }
                        }
                        ChatService.update(chat);
                        System.out.println("Users Added to Chat " + id + ": " + chats.get(id));
                        session.sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"" + id + "\", \"type\":\"ADD_USER_ACK\", \"data\":\"" + chats.get(id) + "\"}"));
                    } catch (NetworkException e) {
                        session.sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"" + e + "\"}"));
                    }
                    break;
                }case "MESSAGE": {
                    try {
                        UUID id = UUID.fromString(stub.getID());
                        Chat chat = ChatService.getByID(id);
                        Message message = new Message(
                                UUID.randomUUID(),
                                UserService.getByUsername(stub.getUser()),
                                Timestamp.from(Instant.now()),
                                stub.getData(),
                                chat
                        );
                        ChatService.addMessage(chat, message);
                        System.out.println("Message Received: " + message.getMessage() + " from: " + message.getUser().getUsername() + " to: " + chats.get(id));
                        for (User u : chats.get(id)) {
                            if (users.containsKey(u))
                                users.get(u).sendMessage(new TextMessage("{\"user\":\"" + stub.getUser() + "\", \"id\":\"" + stub.getID() + "\", \"type\":\"MESSAGE\", \"data\":\"" + stub.getData() + "\"}"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        session.sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"" + e + "\"}"));
                    }
                    break;
                }case "LEAVE_CHAT": {
                    try {
                        UUID id = UUID.fromString(stub.getID());
                        Chat chat = ChatService.getByID(id);
                        User user = UserService.getByUsername(stub.getUser());
                        chats.get(id).remove(user);
                        chat.getUsers().remove(user);
                        if (chats.get(id).isEmpty()) {
                            chats.remove(id);
                            ChatService.delete(chat); //TODO make sure this bit works when loading is done.
                        } else {
                            ChatService.update(chat);
                        }
                        System.out.println("Users Left the Chat " + id + ": " + stub.getUser());
                        session.sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"" + id + "\", \"type\":\"LEAVE_CHAT_ACK\", \"data\":\"\"}"));
                    } catch (Exception e) {
                        session.sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"" + e + "\"}"));
                    }
                    break;
                }default: {
                    System.out.println("Invalid message type: " + stub.getType());
                    session.sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"Invalid message type: " + stub.getType() + "\"}"));
                }
            }
        }catch (IOException e){
            System.out.println("Incoming message is in incorrect format: " + webSocketMessage.getPayload());
            session.sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"Incoming message is in incorrect format: " + webSocketMessage.getPayload() + "\"}"));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        //session.sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"" + exception + "\"}"));
        exception.printStackTrace();
        System.out.println(exception.getMessage());
    }

    public void sendNotification(User user, String message){
        if(users.containsKey(user)){
            try {
                users.get(user).sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"NOTIFICATION\", \"data\":\"" + message + "\"}"));
            }catch (IOException e){
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }

    public HashMap<User, WebSocketSession> getUsers(){
        return users;
    }

    private static class MessageStub{
        private String user;
        private String id;
        private String type;
        private String data;
        public String getUser() {
            return user;
        }
        public String getID() {
            return id;
        }
        public String getType(){
            return type;
        }
        public String getData() {
            return data;
        }

        @Override
        public String toString() {
            return "MessageStub{" +
                    "user='" + user + '\'' +
                    ", id='" + id + '\'' +
                    ", type='" + type + '\'' +
                    ", data='" + data + '\'' +
                    '}';
        }
    }
}
