package com.revature.wordsaway.services;

import com.revature.wordsaway.dtos.requests.LoginRequest;
import com.revature.wordsaway.dtos.requests.NewUserRequest;
import com.revature.wordsaway.dtos.responses.FindUserResponse;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

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

    public static void addFriend(User user, String friendName) {
        User friend = getByUsername(friendName);
        //TODO check if you are already friends.
        userRepository.addFriend(user.getUsername(), friendName);
    }

    public static void removeFriend(User user, String friendName) {
        User friend = getByUsername(friendName);
        //TODO check if you are already friends.
        userRepository.removeFriend(user.getUsername(), friendName);
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
    public static FindUserResponse getFriendByUsername(String username) {
        User user = userRepository.findUserByUsername(username);
        if(user == null) throw new InvalidRequestException("No user with username " + username + " found.");

        return new FindUserResponse(user.getUsername(), user.getELO(), user.getGamesPlayed(), user.getGamesWon());

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
    public static List<FindUserResponse> getFriendsList(String username) {
        List<String> friendsNames = userRepository.getAllFriends(username);
        List<FindUserResponse> friendsList = new ArrayList<>();

        for(String name: friendsNames) {
            User friendAccount = getByUsername(name);
            friendsList.add( new FindUserResponse(friendAccount.getUsername(), friendAccount.getELO(), friendAccount.getGamesPlayed(), friendAccount.getGamesWon()));

        }

        return friendsList;

    }

    public static List<FindUserResponse> getTopTenByElo() {
        List<User> userList = userRepository.getTopTenInElo();
        List<FindUserResponse> topTenList = new ArrayList<>();

        for(User user: userList){
            topTenList.add(new FindUserResponse(user.getUsername(), user.getELO(), user.getGamesPlayed(), user.getGamesWon()));
        }

        return topTenList;
    }

    public static List<FindUserResponse> getRankingsByELO() {

        List<User> userList = userRepository.getAllOrderByElo();
        List<FindUserResponse> rankingsList = new ArrayList<>();

        for(User user: userList){
            rankingsList.add(new FindUserResponse(user.getUsername(), user.getELO(), user.getGamesPlayed(), user.getGamesWon()));
        }

        return rankingsList;
    }

    public static int getRankByElo(String username, List<FindUserResponse> rankingList) {

        for(FindUserResponse user: rankingList){
            if(user.getUsername().equals(username)){ return rankingList.indexOf(user); }
        }

        throw new NotFoundException("Username not found in the rankings list. Please refresh and try again. If problem persists please contact us.");
    }

}
