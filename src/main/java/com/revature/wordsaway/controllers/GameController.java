package com.revature.wordsaway.controllers;

import com.revature.wordsaway.dtos.requests.BoardRequest;
import com.revature.wordsaway.dtos.requests.GameRequest;
import com.revature.wordsaway.dtos.responses.GameResponse;
import com.revature.wordsaway.dtos.responses.OpponentResponse;
import com.revature.wordsaway.models.Board;
import com.revature.wordsaway.models.User;
import com.revature.wordsaway.services.AIService;
import com.revature.wordsaway.services.BoardService;
import com.revature.wordsaway.services.TokenService;
import com.revature.wordsaway.services.UserService;
import com.revature.wordsaway.utils.customExceptions.ForbiddenException;
import com.revature.wordsaway.utils.customExceptions.InvalidRequestException;
import com.revature.wordsaway.utils.customExceptions.NetworkException;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping
public class GameController {
    private final ConcurrentHashMap<UUID, SseEmitter> subscribedBoards = new ConcurrentHashMap<>(); 
    
    @CrossOrigin
    @PostMapping(value = "/makeGame", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody String makeGame(@RequestBody GameRequest request, HttpServletResponse resp, HttpServletRequest req) {
        try {
            User user = TokenService.extractRequesterDetails(req);
            User opponent = UserService.getByUsername(request.getUsername());
            if(user.getUsername().equals(opponent.getUsername())) throw new InvalidRequestException("You can not challenge yourself to a game. Nice try though.");
            for(OpponentResponse o : UserService.getAllOpponents(user.getUsername())){
                if(o.getUsername().equals(opponent.getUsername()) && o.getBoard_id() != null)
                    throw new InvalidRequestException("Can not start another match with "+ opponent.getUsername() + ". Finish existing game first.");
            }
            UUID uuid = UUID.randomUUID();
            BoardService.register(opponent, uuid, !opponent.isCPU());
            Board board = BoardService.register(user, uuid, opponent.isCPU());
            return board.getId().toString();
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            return e.getMessage();
        }
    }

    @CrossOrigin
    @GetMapping(value = "/getGame", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody GameResponse getGame(@Param("id") String id, HttpServletResponse resp, HttpServletRequest req) {
        try {
            User user = TokenService.extractRequesterDetails(req);
            UUID uuid = UUID.fromString(id);
            Board board = BoardService.getByID(uuid);
            Board opposingBoard = BoardService.getOpposingBoard(board);
            if(opposingBoard.getUser().isCPU() && opposingBoard.isActive()) cpuMakeMove(board, opposingBoard);
            return BoardService.getGame(uuid);
        }catch (NetworkException e) {
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return null;
        }catch (NullPointerException | IllegalArgumentException e) {
            resp.setStatus(400);
            System.out.println(e.getMessage());
            return null;
        }
    }

    // TODO give users the option to place their own worms
    /*
    @CrossOrigin
    @PostMapping(value = "/placeWorms", consumes = "application/json")
    public String placeWorms(@RequestBody BoardRequest request, HttpServletRequest httpServletRequest, HttpServletResponse resp) {
        try {
            User user = TokenService.extractRequesterDetails(httpServletRequest);
            Board board = BoardService.getByID(request.getBoardID());
            Board opposingBoard = BoardService.getOpposingBoard(board);
            User opponent = opposingBoard.getUser();
            //if (opponent.isCPU()) new AIService(opposingBoard).setWorms();
            //else board.setWorms(request.getLayout());
            board.setWorms(request.getLayout());
            BoardService.update(board);
            return "Worms placed.";
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            return e.getMessage();
        }
    }
     */

    @CrossOrigin
    @PostMapping(value = "/checkMove", consumes = "application/json")
    public boolean checkMove(@RequestBody BoardRequest request, HttpServletRequest req, HttpServletResponse resp) {
        try {
            TokenService.extractRequesterDetails(req);
            BoardService.validateMove(request);
            return true;
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return false;
        }
    }

    @CrossOrigin
    @PostMapping(value = "/makeMove", consumes = "application/json")
    public String makeMove(@RequestBody BoardRequest request, HttpServletRequest req, HttpServletResponse resp) {
        try {
            User user = TokenService.extractRequesterDetails(req);
            Board board = BoardService.getByID(request.getBoardID());
            if(!board.isActive()) throw new ForbiddenException("Can not make move on board when it is not your turn.");
            BoardService.makeMove(request, board);
            Board opposingBoard = BoardService.getOpposingBoard(board);
            User opponent = opposingBoard.getUser();
            if (BoardService.gameOver(request.getBoardID())){
                user.setELO(BoardService.calculateELO(user.getELO(), opponent.getELO(), true));
                user.setGamesPlayed(user.getGamesPlayed() + 1);
                user.setGamesWon(user.getGamesWon() + 1);
                UserService.update(user);
                if(!opponent.isCPU()) opponent.setELO(BoardService.calculateELO(opponent.getELO(), user.getELO(), false));
                opponent.setGamesPlayed(opponent.getGamesPlayed() + 1);
                UserService.update(opponent);
            }else if (opponent.isCPU()) {
                cpuMakeMove(board, opposingBoard);
                opposingBoard = board;
            }
            SseEmitter emitter = subscribedBoards.get(opposingBoard.getId());
            if (emitter != null) {
                emitter.send(SseEmitter.event().name("active").data("active"));
                emitter.complete();
                subscribedBoards.remove(opposingBoard.getId());
            }
            return "Move made.";
        }catch (NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return e.getMessage();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    private static void cpuMakeMove(Board board, Board opposingBoard){
        try {
            User user = board.getUser();
            User opponent = opposingBoard.getUser();
            Board botBoard = AIService.start(System.currentTimeMillis(), opposingBoard.clone());
            BoardRequest request = new BoardRequest();
            request.setBoardID(opposingBoard.getId());
            request.setReplacedTray(Arrays.equals(opposingBoard.getLetters(), botBoard.getLetters()));
            request.setLayout(botBoard.getLetters());
            BoardService.makeMove(request, opposingBoard);
            if (BoardService.gameOver(opposingBoard.getId())) {
                user.setELO(BoardService.calculateELO(user.getELO(), opponent.getELO(), false));
                user.setGamesPlayed(user.getGamesPlayed() + 1);
                UserService.update(user);
                opponent.setGamesPlayed(opponent.getGamesPlayed() + 1);
                opponent.setGamesWon(opponent.getGamesWon() + 1);
                UserService.update(opponent);
            }
        }catch (NetworkException e){
            board.toggleActive();
            opposingBoard.toggleActive();
            BoardService.update(board);
            BoardService.update(opposingBoard);
        }
    }

    @CrossOrigin
    @GetMapping(value = "/getOpponents", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<OpponentResponse> getOpponents(HttpServletRequest req, HttpServletResponse resp) {
        try {
            User user = TokenService.extractRequesterDetails(req);
            return UserService.getAllOpponents(user.getUsername());
        }catch(NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    @CrossOrigin
    @GetMapping(value = "/active")
    public SseEmitter isActive(@RequestParam("board_id") String board_id) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        subscribedBoards.put(UUID.fromString(board_id), emitter);
        return emitter;
    }

    @CrossOrigin
    @PostMapping(value = "/endGame", consumes = "application/json")
    public String endGame(@RequestBody BoardRequest request, HttpServletRequest req, HttpServletResponse resp) {
        try {
            User user = TokenService.extractRequesterDetails(req);
            Board board = BoardService.getByID(request.getBoardID());
            Board opposingBoard = BoardService.getOpposingBoard(board);
            if(!user.equals(board.getUser())) throw new InvalidRequestException("You can not end someone else's game.");
            if(!board.isActive() && !opposingBoard.getUser().isCPU())
                throw new InvalidRequestException("Only the losing player can end the game.");
            if(!BoardService.gameOver(opposingBoard.getId()) && !BoardService.gameOver(board.getId()))
                throw new InvalidRequestException("You can not end a game that is still in progress.");
            //TODO possibly allow for surrendering.
            BoardService.endGame(board.getGameID());
            return "Game Ended";
        }catch(NetworkException e){
            resp.setStatus(e.getStatusCode());
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }
}