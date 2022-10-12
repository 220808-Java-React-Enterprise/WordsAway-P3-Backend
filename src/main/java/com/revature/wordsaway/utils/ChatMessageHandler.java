package com.revature.wordsaway.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.wordsaway.models.entities.Chat;
import com.revature.wordsaway.models.entities.Message;
import com.revature.wordsaway.models.entities.User;
import com.revature.wordsaway.models.enums.MessageType;
import com.revature.wordsaway.services.ChatService;
import com.revature.wordsaway.services.UserService;
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
import java.util.stream.Collectors;

@Component
public class ChatMessageHandler extends TextWebSocketHandler {
    private final HashMap<User, WebSocketSession> users = new HashMap<>();
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
        for (Map.Entry<User, WebSocketSession> entry : users.entrySet()) {
            if (Objects.equals(session, entry.getValue())) {
                users.remove(entry.getKey());
            }
        }
        System.out.println("Closed connection to " + session.getRemoteAddress());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> webSocketMessage) throws Exception {
        super.handleMessage(session, webSocketMessage);
        try {
            MessageStub stub = new ObjectMapper().readValue((String) webSocketMessage.getPayload(), MessageStub.class);
            switch (stub.getType()){
                case "LOGIN": {
                    if(stub.getUser() == null || stub.getUser().equals("")) {
                        session.close();
                        return;
                    }
                    User user = UserService.getByUsername(stub.getUser());
                    users.remove(user);
                    users.put(user, session);
                    sendChatsToUser(ChatService.getChatsByUsername(user.getUsername()), user);
                    System.out.println("User " + stub.getUser() + " logged in.");
                    break;
                }case "START_CHAT": {
                    Chat chat = new Chat(UUID.randomUUID(), new ArrayList<>(), new HashSet<>());
                    ChatService.register(chat);
                    User user = UserService.getByUsername(stub.user);
                    addUserToChat(chat, user);
                    if (!stub.getData().matches("\\s*")) addUsersToChat(chat, stub.getData().split(","));
                    String message = "Chat started with users: " + chat.getUsernameList();
                    addMessageToChat(chat, user, message);
                    sendMessageToChat(chat, "SERVER", message);
                    System.out.println("Chat Started with id: " + chat.getId() + " and users: " + chat.getUsernameList());
                    break;
                }case "ADD_USER": {
                    Chat chat = ChatService.getByID(UUID.fromString(stub.getID()));
                    User user = UserService.getByUsername(stub.user);
                    User userToAdd = UserService.getByUsername(stub.data);
                    String message = user.getUsername() + " requested " + userToAdd.getUsername() + " join the chat.";
                    addMessageToChat(chat, user, message);
                    sendMessageToOthersInChat(chat, user,"SERVER", message);
                    addUserToChat(chat, userToAdd);
                    chat = ChatService.getByID(UUID.fromString(stub.getID())); //TODO hacky
                    message = userToAdd.getUsername() + " joined the chat.";
                    addMessageToChat(chat, user, message);
                    sendMessageToChat(chat, "SERVER", message);
                    System.out.println("Users Added to Chat " + chat.getId() + ": " + userToAdd.getUsername());
                    break;
                }case "MESSAGE": {
                    Chat chat = ChatService.getByID(UUID.fromString(stub.getID()));
                    User user = UserService.getByUsername(stub.getUser());
                    addMessageToChat(chat, user, stub.getData());
                    sendMessageToOthersInChat(chat, user, user.getUsername(), stub.getData());
                    System.out.println("Message received: \"" + stub.getData() + "\" from " + user.getUsername());
                    break;
                }case "LEAVE_CHAT": {
                    Chat chat = ChatService.getByID(UUID.fromString(stub.getID()));
                    User user = UserService.getByUsername(stub.getUser());
                    String message = user.getUsername() + " left the chat.";
                    addMessageToChat(chat, user, message);
                    sendMessageToOthersInChat(chat, user, "SERVER", message);
                    removeUserFromChat(chat, user);
                    System.out.println("Users Left the Chat " + stub.getID() + ": " + stub.getUser());
                    break;
                }default: {
                    System.out.println("Invalid message type: " + stub.getType());
                    session.sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"Invalid message type: " + stub.getType() + "\"}"));
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            session.sendMessage(new TextMessage("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"Incoming message is in incorrect format: " + webSocketMessage.getPayload() + "\"}"));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        exception.printStackTrace();
        System.out.println(exception.getMessage());
    }

    private void addUserToChat(Chat chat, User user) throws IOException{
        addUsersToChat(chat, Collections.singletonList(user));
    }

    private void addUsersToChat (Chat chat, String[] usernames) throws IOException{
        List<User> users = new ArrayList<>();
        for(String username : usernames){
            users.add(UserService.getByUsername(username));
        }
        addUsersToChat(chat, users);
    }

    private void addUsersToChat (Chat chat, List<User> users) throws IOException{
        for(User user : users){
            chat.addUser(user);
            //ChatService.addUser(user, chat);
            sendChatToUser(chat, user);
        }
        ChatService.update(chat);
    }

    private void removeUserFromChat(Chat chat, User user){
        chat.getUsers().remove(user);
        //ChatService.addUser(user, chat);
        if(chat.getUsers().isEmpty()) ChatService.delete(chat);
        else ChatService.update(chat);
    }

    private void sendMessageToUser(User user, String from, UUID id, MessageType type, String text) throws IOException{
        if(users.containsKey(user)) users.get(user).sendMessage(new TextMessage("{\"user\":\"" + from + "\", \"id\":\"" + id.toString() + "\", \"type\":\"" + type + "\", \"data\":\"" + text + "\"}"));
    }

    private void sendMessageToChat(Chat chat, String from, String text) throws IOException{
        for(User user : chat.getUsers()){
            sendMessageToUser(user, from, chat.getId(), MessageType.MESSAGE, text);
        }
    }

    private void sendMessageToOthersInChat(Chat chat, User excluded, String from, String text) throws IOException{
        for(User user : chat.getUsers()){
            if(!user.equals(excluded)) sendMessageToUser(user, from, chat.getId(), MessageType.MESSAGE, text);
        }
    }

    private void sendChatsToUser(List<Chat> chats, User user) throws IOException{
        for(Chat chat : chats){
            sendChatToUser(chat, user);
        }
    }

    private void sendChatToUser(Chat chat, User user) throws IOException{
        sendMessageToUser(user, "SERVER", chat.getId(), MessageType.START_CHAT_ACK, "");
        for(Message message : chat.getMessages().stream().sorted(Comparator.comparing(Message::getCreated)).collect(Collectors.toList())){
            sendMessageToUser(user, message.getUser().getUsername(), chat.getId(), MessageType.MESSAGE, message.getMessage());
        }
    }

    private void addMessageToChat(Chat chat, User from, String text) throws IOException{
        Message message = new Message(UUID.randomUUID(), from, Timestamp.from(Instant.now()), text, chat);
        chat.addMessage(message);
        ChatService.addMessage(message);
    }

    public void sendNotification(User user, String message) throws IOException{
        sendMessageToUser(user, "SERVER", new UUID(0L, 0L), MessageType.NOTIFICATION, message);
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
