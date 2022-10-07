package com.revature.wordsaway.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.wordsaway.models.entities.Chat;
import com.revature.wordsaway.models.entities.Message;
import com.revature.wordsaway.models.entities.User;
import com.revature.wordsaway.services.ChatService;
import com.revature.wordsaway.services.UserService;
import com.revature.wordsaway.utils.customExceptions.NetworkException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.net.InetSocketAddress;

public class ChatServer extends WebSocketServer {
    private HashMap<User, WebSocket> users;

    private HashMap<UUID, Set<User>> chats;

    private Set<WebSocket> conns;

    public ChatServer(int port) {
        super(new InetSocketAddress(port));
        conns = new HashSet<>();
        users = new HashMap<>();
        chats = new HashMap<>();
        System.out.println("Chat Server on Port " + port + " Initiated at address " + this.getAddress() + ".");
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        conns.add(webSocket);
        System.out.println("New connection from " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress());
        System.out.println("Connections: " + conns.size());
    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
        conns.remove(webSocket);
        System.out.println("Closed connection to " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onMessage(WebSocket webSocket, String webSocketText){
        try {
            MessageStub stub = new ObjectMapper().readValue(webSocketText, MessageStub.class);
            UUID id; Chat chat; User user;
            switch (stub.getType()){
                case "LOGIN":
                    try {
                        user = UserService.getByUsername(stub.getUser());
                        users.put(user, webSocket);
                        System.out.println("User " + stub.getUser() + " logged in.");
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"LOGIN_ACK\", \"data\":\"\"}");
                        for(Chat c : ChatService.getChatsByUsername(user.getUsername())){
                            chats.put(c.getId(), c.getUsers());
                            webSocket.send("{\"user\":\"SERVER\", \"id\":\"" + c.getId() + "\", \"type\":\"START_CHAT\", \"data\":\"\"}");
                            for(Message m : c.getMessages()){
                                webSocket.send("{\"user\":\"SERVER\", \"id\":\"" + c.getId() + "\", \"type\":\"MESSAGE\", \"data\":\"" + m.getMessage() + "\"}");
                            }
                        }
                    }catch(Exception e){
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"" + e + "\"}");
                    }
                    break;
                case "START_CHAT":
                    id = UUID.randomUUID();
                    chat = new Chat(id, new ArrayList<>(), new HashSet<>());
                    chats.put(id, new HashSet<>());
                    try {
                        user = UserService.getByUsername(stub.user);
                        chats.get(id).add(user);
                        chat.getUsers().add(user);
                        if(!stub.getData().matches("\\s*")) {
                            for (String username : stub.getData().split(",")) {
                                user = UserService.getByUsername(username);
                                chats.get(id).add(user);
                                chat.getUsers().add(user);
                            }
                        }
                        ChatService.register(chat);
                        System.out.println("Chat Started with id: " + id + " and users: " + chats.get(id));
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"" + id + "\", \"type\":\"START_CHAT_ACK\", \"data\":\"\"}");
                    }catch(Exception e){
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"" + e + "\"}");
                    }
                    break;
                case "ADD_USER":
                    id = UUID.fromString(stub.getID());
                    chat = ChatService.getByID(id);
                    try {
                        user = UserService.getByUsername(stub.user);
                        chats.get(id).add(user);
                        chat.getUsers().add(user);
                        for (String username : stub.getData().split(",")) {
                            user = UserService.getByUsername(username);
                            chats.get(id).add(user);
                            chat.getUsers().add(user);
                        }
                        ChatService.update(chat);
                        System.out.println("Users Added to Chat " + id + ": " + chats.get(id));
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"" + id + "\", \"type\":\"ADD_USER_ACK\", \"data\":\"" + chats.get(id) + "\"}");
                    }catch(NetworkException e){
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"" + e + "\"}");
                    }
                    break;
                case "MESSAGE":
                    try {
                        id = UUID.fromString(stub.getID());
                        chat = ChatService.getByID(id);
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
                            if(users.containsKey(u)) users.get(u).send("{\"user\":\"" + stub.getUser() + "\", \"id\":\"" + stub.getID() + "\", \"type\":\"MESSAGE\", \"data\":\""+ stub.getData() + "\"}");
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"" + e + "\"}");
                    }
                    break;
                case "LEAVE_CHAT":
                    try {
                        id = UUID.fromString(stub.getID());
                        chat = ChatService.getByID(id);
                        user = UserService.getByUsername(stub.getUser());
                        chats.get(id).remove(user);
                        chat.getUsers().remove(user);
                        if(chats.get(id).isEmpty()){
                            chats.remove(id);
                            ChatService.delete(chat); //TODO make sure this bit works when loading is done.
                        }else{
                            ChatService.update(chat);
                        }
                        System.out.println("Users Left the Chat " + id + ": " + stub.getUser());
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"" + id + "\", \"type\":\"LEAVE_CHAT_ACK\", \"data\":\"\"}");
                    }catch(Exception e) {
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"" + e + "\"}");
                    }
                    break;
                default:
                    System.out.println("Invalid message type: " + stub.getType());
                    webSocket.send("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"Invalid message type: " + stub.getType() + "\"}");
            }
        }catch (IOException e){
            System.out.println("Incoming message is in incorrect format: " + webSocketText);
            webSocket.send("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"Incoming message is in incorrect format: " + webSocketText + "\"}");
        }
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

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println(e);
        webSocket.send("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"" + e + "\"}");
    }
}
