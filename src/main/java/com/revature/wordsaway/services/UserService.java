package com.revature.wordsaway.services;

import com.revature.wordsaway.dtos.requests.LoginRequest;
import com.revature.wordsaway.dtos.requests.NewUserRequest;
import com.revature.wordsaway.dtos.requests.UpdateUserRequest;
import com.revature.wordsaway.dtos.responses.UserResponse;
import com.revature.wordsaway.dtos.responses.OpponentResponse;
import com.revature.wordsaway.models.entities.Board;
import com.revature.wordsaway.models.entities.User;
import com.revature.wordsaway.repositories.BoardRepository;
import com.revature.wordsaway.repositories.UserRepository;
import com.revature.wordsaway.utils.customExceptions.AuthenticationException;
import com.revature.wordsaway.utils.customExceptions.InvalidRequestException;
import com.revature.wordsaway.utils.customExceptions.NotFoundException;
import com.revature.wordsaway.utils.customExceptions.ResourceConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private static UserRepository userRepository;
    private static BoardRepository boardRepository;

    @Autowired
    public UserService(UserRepository userRepository, BoardRepository boardRepository){
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
    }

    public static User register(NewUserRequest request){
        validateUsername(request.getUsername());
        //validatePassword(request.getPassword());
        checkAvailableUsername(request.getUsername());
        if(request.getEmail() != null && !request.getEmail().equals("")){
            validateEmail(request.getEmail());
            checkAvailableEmail(request.getEmail());
        }
        float sum = 0;
        int total = 0;
        for (User user : userRepository.findAll()) {
            if (!user.isCPU()) {
                sum += user.getELO();
                total++;
            }
        }
        User user = new User(
                request.getUsername(),
                request.getPassword(),
                request.getSalt(),
                request.getEmail(),
                0,
                total != 0 ? sum / total : 1000,
                0,
                0,
                false,
                new HashSet<User>()
        );
        userRepository.save(user);
        return user;
    }

    public static void update(User user){
        userRepository.updateUser(user.getUsername(), user.getPassword(), user.getEmail(), user.getELO(), user.getGamesPlayed(), user.getGamesWon());
    }

    public static void addFriend(String username, String friendName) {
        getByUsername(friendName); //Checks to make sure friend exists
        if(userRepository.findAllFriends(username).contains(friendName))
            throw new InvalidRequestException("You can not add a friend who is already in your friend list.");
        userRepository.addFriend(username, friendName);
    }

    public static void removeFriend(String username, String friendName) {
        getByUsername(friendName); //Checks to make sure friend exists
        if(!userRepository.findAllFriends(username).contains(friendName))
            throw new InvalidRequestException("You can not remove a friend who is not in your friend list.");
        userRepository.removeFriend(username, friendName);
    }

    public static String login(LoginRequest request) throws AuthenticationException {
        User user = userRepository.findUserByUsername(request.getUsername());
        if (user != null && user.getPassword().equals(request.getPassword()))
            return TokenService.generateToken(user.getUsername());
        throw new AuthenticationException("Login unsuccessful. Please check username and password.");
    }

    public static User getByUsername(String username){
        User user = userRepository.findUserByUsername(username);
        if(user == null) throw new InvalidRequestException("No user with username " + username + " found.");
        return user;
    }

    //Delg  created for v2
    public static UserResponse getFriendByUsername(String username) {
        User user = userRepository.findUserByUsername(username);
        if(user == null) throw new InvalidRequestException("No user with username " + username + " found.");

        return new UserResponse(user.getUsername(), user.getELO(), user.getGamesPlayed(), user.getGamesWon(), user.getAvatar());

    }

    public static List<User> getAll() {
        List<User> users = (List<User>) userRepository.findAll();
        if(users.size() == 0) throw new InvalidRequestException("No users found.");
        return users;
    }

    public static List<OpponentResponse> getAllOpponents(String username) {
        return getAllOpponents(username, userRepository.findAllOtherUsers(username));
    }

    public static List<OpponentResponse> getAllOpponents(String username, boolean bots) {
        return getAllOpponents(username, userRepository.findAllOtherUsers(username, bots));
    }

    public static List<OpponentResponse> getAllOpponents(String username, List<User> users) {
        List<OpponentResponse> results = new ArrayList<>();
        for(User opponent : users){
            List<Board> boards = boardRepository.findBoardsByTwoUsernames(username, opponent.getUsername());
            UUID boardID;
            if(boards.size() > 0){
                if(boards.get(0).getUser().getUsername().equals(username)) boardID = boards.get(0).getId();
                else boardID = boards.get(1).getId();
            } else boardID = null;
            results.add(new OpponentResponse(opponent.getUsername(), opponent.getELO(), boardID));
        }
        return results;
    }

    public static void validateUsername(String username) throws InvalidRequestException {
        if(!username.matches("^[A-Za-z\\d]{3,15}$"))
            throw new InvalidRequestException("Username must start with a letter and consist of between 3 and 15 alphanumeric characters.");
    }

    /* Now done client side
    public static void validatePassword(String password) throws InvalidRequestException {
        if(!password.matches("^[A-Za-z\\d@$!%*?&]{5,30}$"))
            throw new InvalidRequestException("Password must be between 5 and 30 alphanumeric or special characters.");
    }
    */

    public static void validateEmail(String email) throws InvalidRequestException {
        if(!email.matches("^|[A-Za-z0-9][A-Za-z0-9!#$%&'*+\\-/=?^_`{}|]{0,63}@[A-Za-z0-9.-]{1,253}\\.[A-Za-z]{2,24}$"))
            throw new InvalidRequestException("Invalid Email Address.");
    }

    public static void checkAvailableUsername(String username) throws ResourceConflictException {
        if (userRepository.findUserByUsername(username) != null){
            throw new ResourceConflictException("Username is already taken, please choose another.");
        }
    }

    public static void checkAvailableEmail(String email) throws ResourceConflictException {
        if (userRepository.findUserByEmail(email) != null){
            throw new ResourceConflictException("Email is already taken, please choose another.");
        }
    }

    //Delg v2
    public static Map<String, List<UserResponse>> getFriendsList(String username) {
        Map<String, List<UserResponse>> friendsList = new HashMap<>();
        friendsList.put("friends",  new ArrayList<>());
        friendsList.put("incomingRequests",  new ArrayList<>());
        friendsList.put("outgoingRequests",  new ArrayList<>());
        List<String> myFriends = userRepository.findAllFriends(username);
        List<String> peopleWhoFriendMe = userRepository.findAllUserWhoFriendUser(username);
        for(String name: myFriends) {
            User friendAccount = getByUsername(name);
            if(peopleWhoFriendMe.contains(name)){
                friendsList.get("friends").add(
                        new UserResponse(
                                friendAccount.getUsername(),
                                friendAccount.getELO(),
                                friendAccount.getGamesPlayed(),
                                friendAccount.getGamesWon(),
                                friendAccount.getAvatar()
                        ));
            }else{
                friendsList.get("outgoingRequests").add(
                        new UserResponse(
                                friendAccount.getUsername(),
                                friendAccount.getELO(),
                                friendAccount.getGamesPlayed(),
                                friendAccount.getGamesWon(),
                                friendAccount.getAvatar()
                        ));
            }
        }
        for(String name : peopleWhoFriendMe){
            if(!myFriends.contains(name)){
                User friendAccount = getByUsername(name);
                friendsList.get("incomingRequests").add(
                        new UserResponse(friendAccount.getUsername(),
                                friendAccount.getELO(),
                                friendAccount.getGamesPlayed(),
                                friendAccount.getGamesWon(),
                                friendAccount.getAvatar()
                        ));
            }
        }
        return friendsList;
    }

    public static List<UserResponse> getTopTenByElo() {
        List<User> userList = userRepository.getTopTenInElo();
        List<UserResponse> topTenList = new ArrayList<>();

        for(User user: userList){
            topTenList.add(new UserResponse(user.getUsername(), user.getELO(), user.getGamesPlayed(), user.getGamesWon(), user.getAvatar()));
        }

        return topTenList;
    }

    public static List<UserResponse> getRankingsByELO() {

        List<User> userList = userRepository.getAllOrderByElo();
        List<UserResponse> rankingsList = new ArrayList<>();

        for(User user: userList){
            rankingsList.add(new UserResponse(user.getUsername(), user.getELO(), user.getGamesPlayed(), user.getGamesWon(), user.getAvatar()));
        }

        return rankingsList;
    }

    public static int getRankByElo(String username, List<UserResponse> rankingList) {

        for(UserResponse user: rankingList){
            //added one so the rank can be displayed as is.
            if(user.getUsername().equals(username)){ return rankingList.indexOf(user) + 1; }
        }

        throw new NotFoundException("Username not found in the rankings list. Please refresh and try again. If problem persists please contact us.");
    }

    public static void settingsUpdateUser(String username, UpdateUserRequest request){

        User user = userRepository.findUserByUsername(username);

        boolean newEmail = request.getEmail() != null && !request.getEmail().equals(""),
                newPassword = request.getNewPassword() != null && !request.getNewPassword().equals(""),
                oldPassword = request.getCurrentPassword().equals(user.getPassword());

        if(oldPassword && (newEmail || newPassword)){
            if (request.getNewPassword() != null && !request.getNewPassword().equals("")) user.setPassword(request.getNewPassword());
            if (request.getEmail() != null && !request.getEmail().equals("")) {
                validateEmail(request.getEmail());
                checkAvailableEmail(request.getEmail());
                user.setEmail(request.getEmail());
            }
        } else if (!oldPassword && (newEmail || newPassword))
            throw new AuthenticationException("Invalid current password.");
        else if (request.getAvatarIdx() != user.getAvatar())
            user.setAvatar(request.getAvatarIdx());
        else
            throw new ResourceConflictException("No change to account");

        userRepository.save(user);
    }
}
