package com.revature.wordsaway.services;

import com.revature.wordsaway.dtos.requests.BoardRequest;
import com.revature.wordsaway.models.Board;
import com.revature.wordsaway.models.User;
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
        newBoard = new Board(UUID.fromString("00000000-0000-0000-0000-000000000000"), mock(User.class), tray, 0, worms, letters, UUID.randomUUID(), true);

        mockRepo = mock(BoardRepository.class);
        boardService = new BoardService(mockRepo);

        mockBoard = mock(Board.class);
        when(mockBoard.getLetters()).thenReturn(letters);
        when(mockBoard.getWorms()).thenReturn(worms);

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
    public void test_easyBot_emptyBoard(){
        Board blank = mock(Board.class);
        mockAnagram = mockStatic(AnagramService.class);
        mockAnagram.when(() -> AnagramService.isWord(any())).thenReturn(true);

        tray = "TESTING".toCharArray();
        newBoard.setTray(tray);
        aiService.setRandomSeed(0);

        mockAnagram.when(() -> AnagramService.getAllList(anyString(), anyString(), anyInt())).thenReturn(Arrays.asList("TESTING"));
        newBoard = AIService.start(System.currentTimeMillis(), newBoard);

        when(request.getLayout()).thenReturn(newBoard.getLetters());
        when(mockRepo.findBoardByID(any())).thenReturn(blank);
        when(blank.getLetters()).thenReturn(setupBlankBoard());
        when(blank.getTray()).thenReturn(newBoard.getTray());

        boardService.validateMove(request);
        mockAnagram.close();
    }

    @Test
    public void test_easyBot_twentyMovesIn(){
        Board twentyMoveBoard = mock(Board.class);
        mockAnagram = mockStatic(AnagramService.class);
        mockAnagram.when(() -> AnagramService.isWord(any())).thenReturn(true);

        tray = "SHADOWI".toCharArray();
        newBoard.setTray(tray);
        newBoard.setLetters(setupBoardTwentyMovesIn());
        aiService.setRandomSeed(2);

        mockAnagram.when(() -> AnagramService.getAllList(anyString(), anyString(), anyInt())).thenReturn(Arrays.asList("SHADOW"));
        newBoard = AIService.start(System.currentTimeMillis(), newBoard);

        when(request.getLayout()).thenReturn(newBoard.getLetters());
        when(mockRepo.findBoardByID(any())).thenReturn(twentyMoveBoard);
        when(twentyMoveBoard.getLetters()).thenReturn(setupBoardTwentyMovesIn());
        when(twentyMoveBoard.getTray()).thenReturn(newBoard.getTray());

        boardService.validateMove(request);
        mockAnagram.close();
    }

    @Test
    void test_easyBot_fireball(){
        Board twentyMoveBoard = mock(Board.class);
        mockAnagram = mockStatic(AnagramService.class);
        mockAnagram.when(() -> AnagramService.isWord(any())).thenReturn(true);

        tray = "TESTING".toCharArray();
        newBoard.setTray(tray);
        newBoard.setLetters(setupBoardTwentyMovesIn());
        newBoard.addFireballs(3);
        aiService.setRandomSeed(3);

        mockAnagram.when(() -> AnagramService.getAllList(anyString(), anyString(), anyInt())).thenReturn(Arrays.asList("TESTING"));
        newBoard = AIService.start(System.currentTimeMillis(), newBoard);


        when(request.getLayout()).thenReturn(newBoard.getLetters());
        when(mockRepo.findBoardByID(any())).thenReturn(twentyMoveBoard);
        when(twentyMoveBoard.getLetters()).thenReturn(setupBoardTwentyMovesIn());

        boardService.validateMove(request);
        mockAnagram.close();
    }

    @Test
    void test_easyBot_forceNoMove(){
        Board twentyMoveBoard = mock(Board.class);
        mockAnagram = mockStatic(AnagramService.class);
        mockAnagram.when(() -> AnagramService.isWord(any())).thenReturn(true);

        tray = "GGGGGGG".toCharArray();
        newBoard.setTray(tray);
        newBoard.setLetters(setupBoardTwentyMovesIn());
        aiService.setRandomSeed(3);

        mockAnagram.when(() -> AnagramService.getAllList(anyString(), anyString(), anyInt())).thenReturn(Arrays.asList(""));
        newBoard = AIService.start(System.currentTimeMillis(), newBoard);


        when(request.getLayout()).thenReturn(newBoard.getLetters());
        when(mockRepo.findBoardByID(any())).thenReturn(twentyMoveBoard);
        when(twentyMoveBoard.getLetters()).thenReturn(setupBoardTwentyMovesIn());
        when(twentyMoveBoard.getTray()).thenReturn(newBoard.getTray());

        //boardService.validateMove(request);
        mockAnagram.close();
    }
}