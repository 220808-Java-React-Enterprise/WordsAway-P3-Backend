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
}