package com.revature.wordsaway.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.wordsaway.models.entities.Chat;
import com.revature.wordsaway.models.entities.Message;
import com.revature.wordsaway.models.entities.User;
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
        System.out.println("Chat Server on Port " + port + " Initiated.");
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
            UUID id;
            switch (stub.getType()){
                case "LOGIN":
                    try {
                        users.put(UserService.getByUsername(stub.getUser()), webSocket);
                        System.out.println("User " + stub.getUser() + " logged in.");
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"LOGIN_ACK\", \"data\":\"\"}");
                    }catch(Exception e){
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"" + e + "\"}");
                    }
                    break;
                case "START_CHAT":
                    id = UUID.randomUUID();
                    Chat chat = new Chat(id, new ArrayList<>());
                    chats.put(id, new HashSet<>());
                    try {
                        chats.get(id).add(UserService.getByUsername(stub.user));
                        if(!stub.getData().matches("\\s*")) {
                            for (String username : stub.getData().split(",")) {
                                chats.get(id).add(UserService.getByUsername(username));
                            }
                        }
                        //TODO probably save chat to database here
                        System.out.println("Chat Started with id: " + id + " and users: " + chats.get(id));
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"" + id + "\", \"type\":\"START_CHAT_ACK\", \"data\":\"\"}");
                    }catch(Exception e){
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"" + e + "\"}");
                    }
                    break;
                case "ADD_USER":
                    id = UUID.fromString(stub.getID());
                    try {
                        chats.get(id).add(UserService.getByUsername(stub.user));
                        for (String username : stub.getData().split(",")) {
                            chats.get(id).add(UserService.getByUsername(username));
                        }
                        System.out.println("Users Added to Chat " + id + ": " + chats.get(id));
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"" + id + "\", \"type\":\"ADD_USER_ACK\", \"data\":\"" + chats.get(id) + "\"}");
                    }catch(NetworkException e){
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"" + e + "\"}");
                    }
                    //TODO add users to chat
                    break;
                case "MESSAGE":
                    try {
                        id = UUID.fromString(stub.getID());
                        Message message = new Message(
                                UUID.randomUUID(),
                                UserService.getByUsername(stub.getUser()),
                                Timestamp.from(Instant.now()),
                                stub.getData()
                        );
                        //TODO probably save message to database here
                        System.out.println("Message Received: " + message.getMessage() + " from: " + message.getUser().getUsername() + " to: " + chats.get(id));
                        for (User user : chats.get(id)) {
                            if(users.containsKey(user)) users.get(user).send("{\"user\":\"" + stub.getUser() + "\", \"id\":\"" + stub.getID() + "\", \"type\":\"MESSAGE\", \"data\":\""+ stub.getData() + "\"}");
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"" + e + "\"}");
                    }
                    break;
                case "LEAVE_CHAT":
                    try {
                        id = UUID.fromString(stub.getID());
                        chats.get(id).remove(UserService.getByUsername(stub.getUser()));
                        if(chats.get(id).isEmpty()){
                            chats.remove(id);
                            //TODO delete chat from database
                        }
                        System.out.println("Users Left the Chat " + id + ": " + stub.getUser());
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"" + id + "\", \"type\":\"LEAVE_CHAT_ACK\", \"data\":\"\"}");
                    }catch(Exception e) {
                        webSocket.send("{\"user\":\"SERVER\", \"id\":\"\", \"type\":\"ERROR\", \"data\":\"" + e + "\"}");
                    }
                    //TODO remove users from chat
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
