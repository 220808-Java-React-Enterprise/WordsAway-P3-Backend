package com.revature.wordsaway.services;

import com.revature.wordsaway.dtos.requests.BoardRequest;
import com.revature.wordsaway.entities.Board;
import com.revature.wordsaway.entities.User;
import com.revature.wordsaway.repositories.BoardRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.UUID;

import static com.revature.wordsaway.utils.Constants.BOARD_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AIServiceTest {
    private BoardService boardService;
    private BoardRepository mockRepo;
    private Board mockBoard;
    private Board newBoard;
    private AIService aiService;
    private MockedStatic<AnagramService> mockAnagram;
    private BoardRequest request;
    private final char[] letters = new char[BOARD_SIZE * BOARD_SIZE];
    private final char[] worms = new char[BOARD_SIZE * BOARD_SIZE];
    private char[] tray = new char[7];

    @BeforeEach
    public void setupTest(){
        Arrays.fill(letters, '.');
        Arrays.fill(worms, '.');
        tray[0] = 'A';
        tray[1] = 'U';
        tray[2] = 'P';
        tray[3] = 'E';
        tray[4] = 'R';
        tray[5] = 'H';
        tray[6] = 'N';

        newBoard = new Board(UUID.fromString("00000000-0000-0000-0000-000000000000"), mock(User.class), tray, 0, worms, letters, UUID.randomUUID(), true);

        mockRepo = mock(BoardRepository.class);
        boardService = new BoardService(mockRepo);

        mockBoard = mock(Board.class);
        when(mockBoard.getTray()).thenReturn(tray);
        request = mock(BoardRequest.class);

        aiService = new AIService();

        when(request.getBoardID()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    @AfterEach
    public void tearDown(){
        boardService = null;
        mockRepo = null;
        mockBoard = null;
        newBoard = null;
        aiService = null;
        mockAnagram = null;
        request = null;
    }

    private char[] setupBlankBoard(){
        return (new char[]{
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'
        });
    }

    private char[] setupBoardTwentyMovesIn(){
        return (new char[]{
                '.', '.', '.', '.', '.', '.', '.', '.', 'V', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '*', '.', '.', '.', 'I', '.', 'I', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', 'W', '.', 'R', '.', 'N', '.', 'A', 'C', 'T', '.', '.', '.',
                '.', 'T', '.', '.', 'A', '.', 'E', 'M', '.', '.', '.', '.', '.', 'E', '.', '.',
                '.', 'A', '.', '.', 'P', '.', '.', 'E', '.', '.', '*', '.', '.', 'C', '.', '.',
                '.', 'N', '.', '.', '.', 'C', 'I', 'T', 'R', 'O', 'N', '.', '.', 'O', '.', '.',
                '.', '.', '.', '.', '.', 'O', '.', 'A', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', 'N', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', 'J', 'O', 'E', '.', '.', 'G', 'A', 'D', 'I', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', 'E', '.', '.', '.', '.', '.', 'P', 'E', 'E', '.', '.',
                '.', '.', '.', '.', '.', 'E', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', 'W', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', 'W', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', 'F', '.', 'S', '.', 'E', '.', '.', '.', '.', 'G', 'L', 'E', 'D', '.',
                '.', '.', 'I', '.', 'A', 'X', 'E', '.', 'I', 'F', 'F', '.', '.', '.', '.', '.',
                '.', '.', 'L', '.', 'G', '.', 'S', '.', '.', '.', '.', '.', '.', '.', '.', '.'
        });
    }

    @Test
    public void testStart_firstMove(){
        MockedStatic<BoardService> staticMock = mockStatic(BoardService.class, CALLS_REAL_METHODS);

        aiService.setRandomSeed(0);
        aiService.start(System.currentTimeMillis(), newBoard);

        when(request.getLayout()).thenReturn(newBoard.getLetters());
        when(request.getBoardID()).thenReturn(newBoard.getId());
        when(mockBoard.getLetters()).thenReturn(setupBlankBoard());
        staticMock.when(() -> BoardService.getByID(request.getBoardID())).thenReturn(mockBoard);
        staticMock.when(() -> BoardService.validateMove(request)).thenCallRealMethod();

        boardService.validateMove(request);

        staticMock.close();
    }

    @Test
    public void testStart_twentyMovesIn(){
        MockedStatic<BoardService> staticMock = mockStatic(BoardService.class, CALLS_REAL_METHODS);
        newBoard.setLetters(setupBoardTwentyMovesIn());

        aiService.setRandomSeed(0);
        aiService.start(System.currentTimeMillis(), newBoard);

        when(request.getLayout()).thenReturn(newBoard.getLetters());
        when(request.getBoardID()).thenReturn(newBoard.getId());
        when(mockBoard.getLetters()).thenReturn(setupBoardTwentyMovesIn());
        staticMock.when(() -> BoardService.getByID(request.getBoardID())).thenReturn(mockBoard);
        staticMock.when(() -> BoardService.validateMove(request)).thenCallRealMethod();

        boardService.validateMove(request);

        staticMock.close();
    }

    @Test
    public void testStart_forceFailToFindWord(){
        newBoard.setTray(new char[7]);
        newBoard.setLetters(setupBoardTwentyMovesIn());

        aiService.setRandomSeed(0);
        aiService.start(System.currentTimeMillis(), newBoard);
    }

    @Test
    public void testStart_forceFailToFindWord_shootFireBall(){
        MockedStatic<BoardService> staticMock = mockStatic(BoardService.class, CALLS_REAL_METHODS);

        newBoard.setLetters(setupBoardTwentyMovesIn());
        newBoard.addFireballs(3);

        aiService.setRandomSeed(0);
        aiService.start(System.currentTimeMillis(), newBoard);

        when(request.getLayout()).thenReturn(newBoard.getLetters());
        when(request.getBoardID()).thenReturn(newBoard.getId());
        when(mockBoard.getLetters()).thenReturn(setupBoardTwentyMovesIn());
        staticMock.when(() -> BoardService.getByID(request.getBoardID())).thenReturn(mockBoard);
        staticMock.when(() -> BoardService.validateMove(request)).thenCallRealMethod();

        boardService.validateMove(request);

        staticMock.close();
    }

    @Test
    public void testStart_forceFireBall() {
        MockedStatic<BoardService> staticMock = mockStatic(BoardService.class, CALLS_REAL_METHODS);

        newBoard.setLetters(setupBlankBoard());
        newBoard.addFireballs(3);

        aiService.setRandomSeed(3);
        aiService.start(System.currentTimeMillis(), newBoard);

        when(request.getLayout()).thenReturn(newBoard.getLetters());
        when(request.getBoardID()).thenReturn(newBoard.getId());
        when(mockBoard.getLetters()).thenReturn(setupBlankBoard());
        staticMock.when(() -> BoardService.getByID(request.getBoardID())).thenReturn(mockBoard);
        staticMock.when(() -> BoardService.validateMove(request)).thenCallRealMethod();

        boardService.validateMove(request);

        staticMock.close();
    }
}