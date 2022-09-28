package com.revature.wordsaway.services;

import com.revature.wordsaway.dtos.requests.BoardRequest;
import com.revature.wordsaway.dtos.responses.GameResponse;
import com.revature.wordsaway.models.Board;
import com.revature.wordsaway.models.User;
import com.revature.wordsaway.repositories.BoardRepository;
import com.revature.wordsaway.utils.customExceptions.InvalidRequestException;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import static com.revature.wordsaway.utils.Constants.BOARD_SIZE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BoardServiceTest {
    private BoardRepository mockRepo;
    private BoardService boardService;
    private MockedStatic<AnagramService> anagramServiceMockedStatic;
    private Board mockBoard;
    private BoardRequest request;
    private char[] move = new char[BOARD_SIZE*BOARD_SIZE];

    private static final char[] BLANK_BOARD = new char[]{
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
    };

    @BeforeEach
    public void setup(){
        mockRepo = mock(BoardRepository.class);
        boardService = new BoardService(mockRepo);
        anagramServiceMockedStatic = mockStatic(AnagramService.class);
        anagramServiceMockedStatic.when(() -> AnagramService.isWord(any())).thenReturn(true);
        mockBoard = mock(Board.class);
        when(mockRepo.findBoardByID(any())).thenReturn(mockBoard);
        when(mockBoard.getTray()).thenReturn("ATTEESSTT".toCharArray());
        request = mock(BoardRequest.class);
        when(request.getBoardID()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        Arrays.fill(move, '.');
    }

    @AfterEach
    public void setdown(){
        mockRepo = null;
        boardService = null;
        anagramServiceMockedStatic.close();
        anagramServiceMockedStatic = null;
        mockBoard = null;
        request = null;
        Arrays.fill(move, '.');
    }

    private void setupBlankBoard(){
        when(mockBoard.getLetters()).thenReturn(new char[]{
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

    private void setupBoardWithOneLetter(){
        when(mockBoard.getLetters()).thenReturn(new char[]{
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'T', '.', '.', '.', '.', '.', '.', '.', '.',
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

    private void setupBoardWithOneAsterisk(){
        when(mockBoard.getLetters()).thenReturn(new char[]{
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '*', '.', '.', '.', '.', '.', '.', '.', '.',
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

    private void setupBoardWithThreeLetters(){
        when(mockBoard.getLetters()).thenReturn(new char[]{
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'T', '.', '.', '.', 'S', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'S', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'
        });
    }

    private void setupBoardWithThreeAsterisks(){
        when(mockBoard.getLetters()).thenReturn(new char[]{
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '*', '.', '.', '.', '*', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '*', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'
        });
    }

    @Test
    public void test_register_succeed(){
        User mockUser = mock(User.class);
        UUID uuid = UUID.randomUUID();
        Board board = boardService.register(mockUser, uuid, true);
        verify(mockRepo, times(1)).save(any());
        assertNotNull(board);
        assertNotNull(board.getId());
        assertEquals(board.getUser(), mockUser);
        assertEquals(board.getTray().length, 7);
        assertEquals(board.getFireballs(), 0);
        assertArrayEquals(board.getLetters(), BLANK_BOARD);
        assertEquals(board.getGameID(), uuid);
        assertTrue(board.isActive());
    }

    @Test
    public void test_update_succeed(){
        when(mockBoard.getId()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        when(mockBoard.getFireballs()).thenReturn(0);
        when(mockBoard.isActive()).thenReturn(false);
        when(mockBoard.getTray()).thenReturn("ATTEESSTT".toCharArray());
        when(mockBoard.getLetters()).thenReturn(BLANK_BOARD);
        when(mockBoard.getWorms()).thenReturn(BLANK_BOARD);
        boardService.update(mockBoard);
        //verify(mockRepo, times(1)).updateBoard(any(), any(), any(), any(), any(), any()); //TODO figure out why this doesn't work
        verify(mockBoard, times(1)).getId();
        verify(mockBoard, times(1)).getFireballs();
        verify(mockBoard, times(1)).isActive();
        verify(mockBoard, times(1)).getTray();
        verify(mockBoard, times(1)).getLetters();
        verify(mockBoard, times(1)).getWorms();
    }

    @Test
    public void test_getByID_succeed(){
        when(mockRepo.findBoardByID(any())).thenReturn(mockBoard);
        Board board = boardService.getByID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        assertNotNull(board);
        verify(mockRepo, times(1)).findBoardByID(any());
    }

    @Test
    public void test_getByID_fail(){
        when(mockRepo.findBoardByID(any())).thenReturn(null);
        final Board[] board = new Board[1];
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            board[0] = boardService.getByID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        });
        assertNull(board[0]);
        verify(mockRepo, times(1)).findBoardByID(any());
        Assertions.assertEquals("No board with ID 00000000-0000-0000-0000-000000000000 found.", thrown.getMessage());
    }

    @Test
    public void test_getByGameID_succeed(){
        List<Board> mockBoards = new ArrayList<>();
        mockBoards.add(mockBoard);
        mockBoards.add(mockBoard);
        when(mockRepo.findBoardByGameID(any())).thenReturn(mockBoards);
        List<Board> board = boardService.getByGameID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        assertNotNull(board);
        verify(mockRepo, times(1)).findBoardByGameID(any());
    }

    @Test
    public void test_getByGameID_fail(){
        when(mockRepo.findBoardByGameID(any())).thenReturn(null);
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            boardService.getByGameID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        });
        verify(mockRepo, times(1)).findBoardByGameID(any());
        Assertions.assertEquals("No boards with gameID 00000000-0000-0000-0000-000000000000 found.", thrown.getMessage());
    }

    @Test
    public void test_getOpposingBoard_succeed(){
        when(mockRepo.findOpposingBoardByIDAndGameID(any(), any())).thenReturn(mockBoard);
        when(mockBoard.getId()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        when(mockBoard.getGameID()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        Board board = boardService.getOpposingBoard(mockBoard);
        assertNotNull(board);
        verify(mockRepo, times(1)).findOpposingBoardByIDAndGameID(any(), any());
    }

    @Test
    public void test_getOpposingBoard_fail(){
        when(mockRepo.findOpposingBoardByIDAndGameID(any(), any())).thenReturn(null);
        when(mockBoard.getId()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        when(mockBoard.getGameID()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        final Board[] board = new Board[1];
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            board[0] = boardService.getOpposingBoard(mockBoard);
        });
        assertNull(board[0]);
        verify(mockRepo, times(1)).findOpposingBoardByIDAndGameID(any(), any());
        Assertions.assertEquals("No boards opposing 00000000-0000-0000-0000-000000000000 found.", thrown.getMessage());
    }

    @Test
    public void test_getGame_success(){
        when(mockRepo.findBoardByID(any())).thenReturn(mockBoard);
        when(mockRepo.findOpposingBoardByIDAndGameID(any(), any())).thenReturn(mockBoard);
        when(mockBoard.getLetters()).thenReturn(BLANK_BOARD);
        when(mockBoard.getWorms()).thenReturn(BLANK_BOARD);
        when(mockBoard.getUser()).thenReturn(mock(User.class));
        GameResponse game = BoardService.getGame(request.getBoardID());
        //TODO maybe check more
        assertNotNull(game);
    }

    @RepeatedTest(100)
    public void test_setWorms(){
        when(mockBoard.getWorms()).thenReturn(move);
        boardService.setWorms(move);

        // Aircraft Carrier - 5
        // Battleship - 4
        // Cruiser - 3
        // Submarine - 3
        // Destroyer - 2
        // total - 17
        int countShipLength = 0, counter = 0;
        for (char letter : move){
            if (String.valueOf(letter).matches("[0-5]"))
                countShipLength++;

//            if (counter % BOARD_SIZE < BOARD_SIZE - 1) System.out.print(letter + ", ");
//            else System.out.println(letter);
//            counter++;
        }
        //System.out.println();

        assertEquals(17, countShipLength);
    }

    @RepeatedTest(100)
    public void test_getRandomChar_succeed(){
        char[] tray = new char[7];
        BoardService.getNewTray(tray);

        assertTrue(String.valueOf(tray).matches("[A-Z]+"));
    }

    @Test
    public void test_makeMove_user(){
        Board mockOpposingBoard = mock(Board.class);
        try(MockedStatic<BoardService> mockedStatic = mockStatic(BoardService.class, CALLS_REAL_METHODS)){
            when(request.isReplacedTray()).thenReturn(false);
            mockedStatic.when(() -> BoardService.validateMove(any())).thenReturn(mockBoard);
            mockedStatic.when(() -> BoardService.getOpposingBoard(any())).thenReturn(mockOpposingBoard);
            doNothing().when(mockBoard).setLetters(any());
            doNothing().when(mockBoard).toggleActive();
            doNothing().when(mockOpposingBoard).toggleActive();
            mockedStatic.when(() -> BoardService.update(any())).then(invocationOnMock -> null);

            mockedStatic.when(() -> BoardService.makeMove(request, mockBoard)).thenCallRealMethod();
        }
    }

    @RepeatedTest(BOARD_SIZE * BOARD_SIZE)
    public void test_validateMove_LongHorizontalMoveOnBlankBoard_succeed(RepetitionInfo repetitionInfo){
        setupBlankBoard();
        int r = repetitionInfo.getCurrentRepetition() - 1;
        //Skips tests where word would be off board
        if(r % BOARD_SIZE >= BOARD_SIZE - 3) return;
        move[r] = 'T';
        move[r + 1] = 'E';
        move[r + 2] = 'S';
        move[r + 3] = 'T';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("test"), times(1));
    }

    @RepeatedTest(BOARD_SIZE * BOARD_SIZE)
    public void test_validateMove_LongVerticalMoveOnBlankBoard_succeed(RepetitionInfo repetitionInfo){
        setupBlankBoard();
        int r = repetitionInfo.getCurrentRepetition() - 1;
        //Skips tests where word would be off board
        if(r / BOARD_SIZE >= BOARD_SIZE - 3) return;
        move[r] = 'T';
        move[r + BOARD_SIZE] = 'E';
        move[r + BOARD_SIZE * 2] = 'S';
        move[r + BOARD_SIZE * 3] = 'T';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("test"), times(1));
    }

    @RepeatedTest(BOARD_SIZE * BOARD_SIZE)
    public void test_validateMove_ShortMoveOnBlankBoard_succeed(RepetitionInfo repetitionInfo){
        setupBlankBoard();
        int r = repetitionInfo.getCurrentRepetition() - 1;
        move[r] = 'A';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("a"), times(1));
    }

    @Test
    public void test_validateMove_NoMoveOnBlankBoard_fail(){
        setupBlankBoard();
        when(request.getLayout()).thenReturn(move);
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            boardService.validateMove(request);
        });
        verify(mockRepo, times(1)).findBoardByID(any());
        Assertions.assertEquals("Invalid Move. Must be some change in boards.", thrown.getMessage());
    }

    @RepeatedTest(BOARD_SIZE * BOARD_SIZE)
    public void test_validateMove_UnconnectedLongHorizontalMoveOnBoardWithOneLetter_succeed(RepetitionInfo repetitionInfo){
        setupBoardWithOneLetter();
        int r = repetitionInfo.getCurrentRepetition() - 1;
        //Skips tests where word would be off board
        if(r % BOARD_SIZE >= BOARD_SIZE - 3) return;
        //Skips Connected Tests
        if((r / BOARD_SIZE == 6 || r / BOARD_SIZE == 8) && r % BOARD_SIZE >= 4 && r % BOARD_SIZE <= 8) return;
        if(r / BOARD_SIZE == 7 && r % BOARD_SIZE >= 3 && r % BOARD_SIZE <= 8) return;
        move[r] = 'T';
        move[r + 1] = 'E';
        move[r + 2] = 'S';
        move[r + 3] = 'T';
        move[7 * BOARD_SIZE + 7] = 'T';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("test"), times(1));
    }

    @RepeatedTest(BOARD_SIZE * BOARD_SIZE)
    public void test_validateMove_UnconnectedLongVerticalMoveOnBoardWithOneLetter_succeed(RepetitionInfo repetitionInfo){
        setupBoardWithOneLetter();
        int r = repetitionInfo.getCurrentRepetition() - 1;
        //Skips tests where word would be off board
        if(r / BOARD_SIZE >= BOARD_SIZE - 3) return;
        //Skips Connected Tests
        if((r % BOARD_SIZE == 6 || r % BOARD_SIZE == 8) && r / BOARD_SIZE >= 4 && r / BOARD_SIZE <= 8) return;
        if(r % BOARD_SIZE == 7 && r / BOARD_SIZE >= 3 && r / BOARD_SIZE <= 8) return;
        move[r] = 'T';
        move[r + BOARD_SIZE] = 'E';
        move[r + BOARD_SIZE * 2] = 'S';
        move[r + BOARD_SIZE * 3] = 'T';
        move[7 * BOARD_SIZE + 7] = 'T';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("test"), times(1));
    }

    @RepeatedTest(BOARD_SIZE * BOARD_SIZE)
    public void test_validateMove_UnconnectedShortMoveOnBoardWithOneLetter_succeed(RepetitionInfo repetitionInfo){
        setupBoardWithOneLetter();
        int r = repetitionInfo.getCurrentRepetition() - 1;
        //Skips Connected Tests
        if(r % BOARD_SIZE == 7 && r / BOARD_SIZE >= 6 && r / BOARD_SIZE <= 8) return;
        if(r / BOARD_SIZE == 7 && r % BOARD_SIZE >= 6 && r % BOARD_SIZE <= 8) return;
        move[0] = 'A';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("a"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedLongHorizontalFrontMoveOnBoardWithOneLetter_succeed(){
        setupBoardWithOneLetter();
        move[7 * BOARD_SIZE + 4] = 'T';
        move[7 * BOARD_SIZE + 5] = 'E';
        move[7 * BOARD_SIZE + 6] = 'S';
        move[7 * BOARD_SIZE + 7] = 'T';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("test"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedLongHorizontalFrontMoveOnBoardWithOneAsterisk_succeed(){
        setupBoardWithOneAsterisk();
        move[7 * BOARD_SIZE + 3] = 'T';
        move[7 * BOARD_SIZE + 4] = 'E';
        move[7 * BOARD_SIZE + 5] = 'S';
        move[7 * BOARD_SIZE + 6] = 'T';
        move[7 * BOARD_SIZE + 7] = '*';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("test"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedShortHorizontalFrontMoveOnBoardWithOneLetter_succeed(){
        setupBoardWithOneLetter();
        move[7 * BOARD_SIZE + 6] = 'A';
        move[7 * BOARD_SIZE + 7] = 'T';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("at"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedShortHorizontalFrontMoveOnBoardWithOneAsterisk_succeed(){
        setupBoardWithOneAsterisk();
        move[7 * BOARD_SIZE + 6] = '*';
        move[7 * BOARD_SIZE + 7] = '*';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord(any()), times(0));
    }

    @Test
    public void test_validateMove_ConnectedLongVerticalFrontMoveOnBoardWithOneLetter_succeed(){
        setupBoardWithOneLetter();
        move[4 * BOARD_SIZE + 7] = 'T';
        move[5 * BOARD_SIZE + 7] = 'E';
        move[6 * BOARD_SIZE + 7] = 'S';
        move[7 * BOARD_SIZE + 7] = 'T';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("test"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedLongVerticalFrontMoveOnBoardWithOneAsterisk_succeed(){
        setupBoardWithOneAsterisk();
        move[3 * BOARD_SIZE + 7] = 'T';
        move[4 * BOARD_SIZE + 7] = 'E';
        move[5 * BOARD_SIZE + 7] = 'S';
        move[6 * BOARD_SIZE + 7] = 'T';
        move[7 * BOARD_SIZE + 7] = '*';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("test"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedShortVerticalFrontMoveOnBoardWithOneLetter_succeed(){
        setupBoardWithOneLetter();
        move[6 * BOARD_SIZE + 7] = 'A';
        move[7 * BOARD_SIZE + 7] = 'T';
        when(request.getLayout()).thenReturn(move);
        anagramServiceMockedStatic.when(() -> AnagramService.isWord("a")).thenReturn(false);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("a"), times(1));
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("at"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedShortVerticalFrontMoveOnBoardWithOneAsterisk_succeed(){
        setupBoardWithOneAsterisk();
        move[6 * BOARD_SIZE + 7] = '*';
        move[7 * BOARD_SIZE + 7] = '*';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord(any()), times(0));
    }

    @Test
    public void test_validateMove_ConnectedLongHorizontalBackMoveOnBoardWithOneLetter_succeed(){
        setupBoardWithOneLetter();
        move[7 * BOARD_SIZE + 7] = 'T';
        move[7 * BOARD_SIZE + 8] = 'E';
        move[7 * BOARD_SIZE + 9] = 'S';
        move[7 * BOARD_SIZE + 10] = 'T';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("test"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedLongHorizontalBackMoveOnBoardWithOneAsterisk_succeed(){
        setupBoardWithOneAsterisk();
        move[7 * BOARD_SIZE + 7] = '*';
        move[7 * BOARD_SIZE + 8] = 'T';
        move[7 * BOARD_SIZE + 9] = 'E';
        move[7 * BOARD_SIZE + 10] = 'S';
        move[7 * BOARD_SIZE + 11] = 'T';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("test"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedShortHorizontalBackMoveOnBoardWithOneLetter_succeed(){
        setupBoardWithOneLetter();
        move[7 * BOARD_SIZE + 7] = 'T';
        move[7 * BOARD_SIZE + 8] = 'A';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("ta"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedShortHorizontalBackMoveOnBoardWithOneAsterisk_succeed(){
        setupBoardWithOneAsterisk();
        move[7 * BOARD_SIZE + 7] = '*';
        move[7 * BOARD_SIZE + 8] = '*';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord(any()), times(0));
    }

    @Test
    public void test_validateMove_ConnectedLongVerticalBackMoveOnBoardWithOneLetter_succeed(){
        setupBoardWithOneLetter();
        move[7 * BOARD_SIZE + 7] = 'T';
        move[8 * BOARD_SIZE + 7] = 'E';
        move[9 * BOARD_SIZE + 7] = 'S';
        move[10 * BOARD_SIZE + 7] = 'T';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("test"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedLongVerticalBackMoveOnBoardWithOneAsterisk_succeed(){
        setupBoardWithOneAsterisk();
        move[7 * BOARD_SIZE + 7] = '*';
        move[8 * BOARD_SIZE + 7] = 'T';
        move[9 * BOARD_SIZE + 7] = 'E';
        move[10 * BOARD_SIZE + 7] = 'S';
        move[11 * BOARD_SIZE + 7] = 'T';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("test"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedShortVerticalBackMoveOnBoardWithOneLetter_succeed(){
        setupBoardWithOneLetter();
        move[7 * BOARD_SIZE + 7] = 'T';
        move[8 * BOARD_SIZE + 7] = 'A';
        when(request.getLayout()).thenReturn(move);
        anagramServiceMockedStatic.when(() -> AnagramService.isWord("a")).thenReturn(false);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("a"), times(1));
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("ta"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedShortVerticalBackMoveOnBoardWithOneAsterisk_succeed(){
        setupBoardWithOneAsterisk();
        move[7 * BOARD_SIZE + 7] = '*';
        move[8 * BOARD_SIZE + 7] = '*';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord(any()), times(0));
    }

    @Test
    public void test_validateMove_ConnectedLongHorizontalAroundMoveOnBoardWithOneLetter_succeed(){
        setupBoardWithOneLetter();
        move[7 * BOARD_SIZE + 4] = 'T';
        move[7 * BOARD_SIZE + 5] = 'E';
        move[7 * BOARD_SIZE + 6] = 'S';
        move[7 * BOARD_SIZE + 7] = 'T';
        move[7 * BOARD_SIZE + 8] = 'S';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("tests"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedLongVerticalAroundMoveOnBoardWithOneLetter_succeed(){
        setupBoardWithOneLetter();
        move[4 * BOARD_SIZE + 7] = 'T';
        move[5 * BOARD_SIZE + 7] = 'E';
        move[6 * BOARD_SIZE + 7] = 'S';
        move[7 * BOARD_SIZE + 7] = 'T';
        move[8 * BOARD_SIZE + 7] = 'S';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("tests"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedLongHorizontalBetweenMoveOnBoardWithThreeLetters_succeed(){
        setupBoardWithThreeLetters();
        move[7 * BOARD_SIZE + 7] = 'T';
        move[7 * BOARD_SIZE + 8] = 'E';
        move[7 * BOARD_SIZE + 9] = 'S';
        move[7 * BOARD_SIZE + 10] = 'T';
        move[7 * BOARD_SIZE + 11] = 'S';
        move[11 * BOARD_SIZE + 7] = 'S';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("tests"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedLongHorizontalBetweenMoveOnBoardWithThreeAsterisks_succeed(){
        setupBoardWithThreeAsterisks();
        move[7 * BOARD_SIZE + 7] = '*';
        move[7 * BOARD_SIZE + 8] = 'T';
        move[7 * BOARD_SIZE + 9] = 'E';
        move[7 * BOARD_SIZE + 10] = 'S';
        move[7 * BOARD_SIZE + 11] = 'T';
        move[11 * BOARD_SIZE + 7] = '*';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("test"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedLongVerticalBetweenMoveOnBoardWithThreeLetters_succeed(){
        setupBoardWithThreeLetters();
        move[7 * BOARD_SIZE + 7] = 'T';
        move[8 * BOARD_SIZE + 7] = 'E';
        move[9 * BOARD_SIZE + 7] = 'S';
        move[10 * BOARD_SIZE + 7] = 'T';
        move[11 * BOARD_SIZE + 7] = 'S';
        move[7 * BOARD_SIZE + 11] = 'S';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("tests"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedLongVerticalBetweenMoveOnBoardWithThreeAsterisks_succeed(){
        setupBoardWithThreeAsterisks();
        move[7 * BOARD_SIZE + 7] = '*';
        move[8 * BOARD_SIZE + 7] = 'T';
        move[9 * BOARD_SIZE + 7] = 'E';
        move[10 * BOARD_SIZE + 7] = 'S';
        move[11 * BOARD_SIZE + 7] = 'T';
        move[7 * BOARD_SIZE + 11] = '*';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord("test"), times(1));
    }

    @Test
    public void test_validateMove_ConnectedAsteriskHorizontalFrontMoveOnBoardWithOneLetter_succeed(){
        setupBoardWithOneLetter();
        move[7 * BOARD_SIZE + 6] = '*';
        move[7 * BOARD_SIZE + 7] = 'T';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord(any()), times(0));
    }

    @Test
    public void test_validateMove_ConnectedAsteriskHorizontalBackMoveOnBoardWithOneLetter_succeed(){
        setupBoardWithOneLetter();
        move[7 * BOARD_SIZE + 7] = 'T';
        move[7 * BOARD_SIZE + 8] = '*';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord(any()), times(0));
    }

    @Test
    public void test_validateMove_ConnectedAsteriskVerticalFrontMoveOnBoardWithOneLetter_succeed(){
        setupBoardWithOneLetter();
        move[6 * BOARD_SIZE + 7] = '*';
        move[7 * BOARD_SIZE + 7] = 'T';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord(any()), times(0));
    }

    @Test
    public void test_validateMove_ConnectedAsteriskVerticalBackMoveOnBoardWithOneLetter_succeed(){
        setupBoardWithOneLetter();
        move[7 * BOARD_SIZE + 7] = 'T';
        move[8 * BOARD_SIZE + 7] = '*';
        when(request.getLayout()).thenReturn(move);
        boardService.validateMove(request);
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord(any()), times(0));
    }

    @RepeatedTest(BOARD_SIZE * BOARD_SIZE)
    public void test_validateMove_TwoAsteriskSameRowMoveOnBlankBoard_fail(RepetitionInfo repetitionInfo){
        setupBlankBoard();
        int r = repetitionInfo.getCurrentRepetition() - 1;
        //Skips tests where word would be off board
        if(r % BOARD_SIZE >= BOARD_SIZE - 1) return;
        move[r] = '*';
        move[r+1] = '*';
        when(request.getLayout()).thenReturn(move);
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            boardService.validateMove(request);
        });
        verify(mockRepo, times(1)).findBoardByID(any());
        Assertions.assertEquals("Invalid Move. Fireball may only be placed alone.",
                thrown.getMessage());
    }

    @RepeatedTest(BOARD_SIZE * BOARD_SIZE)
    public void test_validateMove_LetterAndAsteriskSameRowMoveOnBlankBoard_fail(RepetitionInfo repetitionInfo){
        setupBlankBoard();
        int r = repetitionInfo.getCurrentRepetition() - 1;
        //Skips tests where word would be off board
        if(r % BOARD_SIZE >= BOARD_SIZE - 1) return;
        move[r] = 'N';
        move[r+1] = '*';
        when(request.getLayout()).thenReturn(move);
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            boardService.validateMove(request);
        });
        verify(mockRepo, times(1)).findBoardByID(any());
        Assertions.assertEquals("Invalid Move. Fireball may only be placed alone.",
                thrown.getMessage());
    }

    @RepeatedTest(BOARD_SIZE * BOARD_SIZE)
    public void test_validateMove_TwoAsteriskSameColumnMoveOnBlankBoard_fail(RepetitionInfo repetitionInfo){
        setupBlankBoard();
        int r = repetitionInfo.getCurrentRepetition() - 1;
        //Skips tests where word would be off board
        if(r / BOARD_SIZE >= BOARD_SIZE - 1) return;
        move[r] = '*';
        move[r+ BOARD_SIZE] = '*';
        when(request.getLayout()).thenReturn(move);
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            boardService.validateMove(request);
        });
        verify(mockRepo, times(1)).findBoardByID(any());
        Assertions.assertEquals("Invalid Move. Fireball may only be placed alone.",
                thrown.getMessage());
    }

    @RepeatedTest(BOARD_SIZE * BOARD_SIZE)
    public void test_validateMove_LetterAndAsteriskSameColumnMoveOnBlankBoard_fail(RepetitionInfo repetitionInfo){
        setupBlankBoard();
        int r = repetitionInfo.getCurrentRepetition() - 1;
        //Skips tests where word would be off board
        if(r / BOARD_SIZE >= BOARD_SIZE - 1) return;
        move[r] = 'N';
        move[r+ BOARD_SIZE] = '*';
        when(request.getLayout()).thenReturn(move);
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            boardService.validateMove(request);
        });
        verify(mockRepo, times(1)).findBoardByID(any());
        Assertions.assertEquals("Invalid Move. Fireball may only be placed alone.",
                thrown.getMessage());
    }

    @RepeatedTest(BOARD_SIZE * BOARD_SIZE)
    public void test_validateMove_TwoDifferentRowAndColumnAsteriskMoveOnBlankBoard_fail(RepetitionInfo repetitionInfo){
        setupBlankBoard();
        int r = repetitionInfo.getCurrentRepetition() - 1;
        //Skips Connected Tests
        if(r / BOARD_SIZE == 0 || r % BOARD_SIZE == 0) return;
        move[0] = '*';
        move[r] = '*';
        when(request.getLayout()).thenReturn(move);
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            boardService.validateMove(request);
        });
        verify(mockRepo, times(1)).findBoardByID(any());
        Assertions.assertEquals("Invalid Move. Fireball may only be placed alone.",
                thrown.getMessage());
    }

    @RepeatedTest(BOARD_SIZE * BOARD_SIZE)
    public void test_validateMove_TwoDifferentRowAndColumnLetterAndAsteriskMoveOnBlankBoard_fail(RepetitionInfo repetitionInfo){
        setupBlankBoard();
        int r = repetitionInfo.getCurrentRepetition() - 1;
        //Skips Connected Tests
        if(r / BOARD_SIZE == 0 || r % BOARD_SIZE == 0) return;
        move[0] = 'N';
        move[r] = '*';
        when(request.getLayout()).thenReturn(move);
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            boardService.validateMove(request);
        });
        verify(mockRepo, times(1)).findBoardByID(any());
        Assertions.assertEquals("Invalid Move. Fireball may only be placed alone.",
                thrown.getMessage());
    }

    @RepeatedTest(BOARD_SIZE * BOARD_SIZE)
    public void test_validateMove_TwoUnconnectedLetterMoveOnBlankBoard_fail(RepetitionInfo repetitionInfo){
        setupBlankBoard();
        int r = repetitionInfo.getCurrentRepetition() - 1;
        //Skips Connected Tests
        if(r / BOARD_SIZE == 0 || r % BOARD_SIZE == 0) return;
        move[0] = 'N';
        move[r] = 'O';
        when(request.getLayout()).thenReturn(move);
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            boardService.validateMove(request);
        });
        verify(mockRepo, times(1)).findBoardByID(any());
        Assertions.assertEquals("Invalid Move. All tiles must be placed in either the same row or same column.",
                thrown.getMessage());
    }

    @Test
    public void test_validateMove_TwoLongSameRowMoveOnBlankBoard_succeed(){
        setupBlankBoard();
        move[7 * BOARD_SIZE] = 'T';
        move[7 * BOARD_SIZE + 1] = 'E';
        move[7 * BOARD_SIZE + 2] = 'S';
        move[7 * BOARD_SIZE + 3] = 'T';
        move[7 * BOARD_SIZE + 4] = '.';
        move[7 * BOARD_SIZE + 5] = 'T';
        move[7 * BOARD_SIZE + 6] = 'E';
        move[7 * BOARD_SIZE + 7] = 'S';
        move[7 * BOARD_SIZE + 8] = 'T';
        when(request.getLayout()).thenReturn(move);
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            boardService.validateMove(request);
        });
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord(any()), times(0));
        Assertions.assertEquals("Invalid Move. Only one word may be placed at a time.",
                thrown.getMessage());
    }

    @Test
    public void test_validateMove_TwoLongSameColumnMoveOnBlankBoard_succeed(){
        setupBlankBoard();
        move[3 * BOARD_SIZE + 7] = 'T';
        move[4 * BOARD_SIZE + 7] = 'E';
        move[5 * BOARD_SIZE + 7] = 'S';
        move[6 * BOARD_SIZE + 7] = 'T';
        move[7 * BOARD_SIZE + 7] = '.';
        move[8 * BOARD_SIZE + 7] = 'T';
        move[9 * BOARD_SIZE + 7] = 'E';
        move[10 * BOARD_SIZE + 7] = 'S';
        move[11 * BOARD_SIZE + 7] = 'T';
        when(request.getLayout()).thenReturn(move);
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            boardService.validateMove(request);
        });
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord(any()), times(0));
        Assertions.assertEquals("Invalid Move. Only one word may be placed at a time.",
                thrown.getMessage());
    }

    @Test
    public void test_validateMove_TwoLongSameRowWithAsteriskMoveOnBlankBoard_succeed(){
        setupBlankBoard();
        move[7 * BOARD_SIZE] = 'T';
        move[7 * BOARD_SIZE + 1] = 'E';
        move[7 * BOARD_SIZE + 2] = 'S';
        move[7 * BOARD_SIZE + 3] = 'T';
        move[7 * BOARD_SIZE + 4] = '*';
        move[7 * BOARD_SIZE + 5] = 'T';
        move[7 * BOARD_SIZE + 6] = 'E';
        move[7 * BOARD_SIZE + 7] = 'S';
        move[7 * BOARD_SIZE + 8] = 'T';
        when(request.getLayout()).thenReturn(move);
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            boardService.validateMove(request);
        });
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord(any()), times(0));
        Assertions.assertEquals("Invalid Move. Fireball may only be placed alone.",
                thrown.getMessage());
    }

    @Test
    public void test_validateMove_TwoLongSameColumnWithAsteriskMoveOnBlankBoard_succeed(){
        setupBlankBoard();
        move[3 * BOARD_SIZE + 7] = 'T';
        move[4 * BOARD_SIZE + 7] = 'E';
        move[5 * BOARD_SIZE + 7] = 'S';
        move[6 * BOARD_SIZE + 7] = 'T';
        move[7 * BOARD_SIZE + 7] = '*';
        move[8 * BOARD_SIZE + 7] = 'T';
        move[9 * BOARD_SIZE + 7] = 'E';
        move[10 * BOARD_SIZE + 7] = 'S';
        move[11 * BOARD_SIZE + 7] = 'T';
        when(request.getLayout()).thenReturn(move);
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            boardService.validateMove(request);
        });
        verify(mockRepo, times(1)).findBoardByID(any());
        anagramServiceMockedStatic.verify(() -> AnagramService.isWord(any()), times(0));
        Assertions.assertEquals("Invalid Move. Fireball may only be placed alone.",
                thrown.getMessage());
    }

    @Test
    public void test_getCheckedOnBlankBoard_succeed(){
        setupBlankBoard();
        boolean[] hits = boardService.getChecked(mockBoard.getLetters());
        boolean[] blankHits = new boolean[BOARD_SIZE*BOARD_SIZE];
        Arrays.fill(blankHits, false);
        assertArrayEquals(hits, blankHits);
    }

    @Test
    public void test_getCheckedOnBoardWithOneLetter_succeed(){
        setupBoardWithOneLetter();
        boolean[] hits = boardService.getChecked(mockBoard.getLetters());
        boolean[] blankHits = new boolean[BOARD_SIZE*BOARD_SIZE];
        Arrays.fill(blankHits, false);
        blankHits[7 * BOARD_SIZE + 7] = true;
        assertArrayEquals(hits, blankHits);
    }

    @Test
    public void test_getCheckedOnBoardWithOneAsterisk_succeed(){
        setupBoardWithOneAsterisk();
        boolean[] hits = boardService.getChecked(mockBoard.getLetters());
        boolean[] blankHits = new boolean[BOARD_SIZE*BOARD_SIZE];
        Arrays.fill(blankHits, false);
        blankHits[7 * BOARD_SIZE + 7] = true;
        assertArrayEquals(hits, blankHits);
    }

    @Test
    public void test_getChecked_HorizontalMoveOnBoardWith1PointLetters_succeed(){
        when(mockBoard.getLetters()).thenReturn(new char[]{
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'E', 'E', 'E', 'E', 'E', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'
        });
        boolean[] hits = boardService.getChecked(mockBoard.getLetters());
        boolean[] blankHits = new boolean[BOARD_SIZE*BOARD_SIZE];
        Arrays.fill(blankHits, false);
        blankHits[7 * BOARD_SIZE + 7] = true;
        blankHits[7 * BOARD_SIZE + 8] = true;
        blankHits[7 * BOARD_SIZE + 9] = true;
        blankHits[7 * BOARD_SIZE + 10] = true;
        blankHits[7 * BOARD_SIZE + 11] = true;
        assertArrayEquals(hits, blankHits);
    }

    @Test
    public void test_getChecked_HorizontalMoveOnBoardWithSome2PointLetters_succeed(){
        when(mockBoard.getLetters()).thenReturn(new char[]{
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'E', 'F', 'E', 'F', 'E', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'
        });
        boolean[] hits = boardService.getChecked(mockBoard.getLetters());
        boolean[] blankHits = new boolean[BOARD_SIZE*BOARD_SIZE];
        Arrays.fill(blankHits, false);
        blankHits[7 * BOARD_SIZE + 7] = true;
        blankHits[6 * BOARD_SIZE + 8] = true;
        blankHits[7 * BOARD_SIZE + 8] = true;
        blankHits[8 * BOARD_SIZE + 8] = true;
        blankHits[7 * BOARD_SIZE + 9] = true;
        blankHits[6 * BOARD_SIZE + 10] = true;
        blankHits[7 * BOARD_SIZE + 10] = true;
        blankHits[8 * BOARD_SIZE + 10] = true;
        blankHits[7 * BOARD_SIZE + 11] = true;
        assertArrayEquals(hits, blankHits);
    }

    @Test
    public void test_getCheckedOn_HorizontalMoveBoardWithA3PointLetter_succeed(){
        when(mockBoard.getLetters()).thenReturn(new char[]{
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'E', 'F', 'E', 'F', 'Z', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'
        });
        boolean[] hits = boardService.getChecked(mockBoard.getLetters());
        boolean[] blankHits = new boolean[BOARD_SIZE*BOARD_SIZE];
        Arrays.fill(blankHits, false);
        blankHits[7 * BOARD_SIZE + 6] = true;
        blankHits[6 * BOARD_SIZE + 7] = true;
        blankHits[7 * BOARD_SIZE + 7] = true;
        blankHits[8 * BOARD_SIZE + 7] = true;
        blankHits[6 * BOARD_SIZE + 8] = true;
        blankHits[7 * BOARD_SIZE + 8] = true;
        blankHits[8 * BOARD_SIZE + 8] = true;
        blankHits[6 * BOARD_SIZE + 9] = true;
        blankHits[7 * BOARD_SIZE + 9] = true;
        blankHits[8 * BOARD_SIZE + 9] = true;
        blankHits[6 * BOARD_SIZE + 10] = true;
        blankHits[7 * BOARD_SIZE + 10] = true;
        blankHits[8 * BOARD_SIZE + 10] = true;
        blankHits[6 * BOARD_SIZE + 11] = true;
        blankHits[7 * BOARD_SIZE + 11] = true;
        blankHits[8 * BOARD_SIZE + 11] = true;
        blankHits[7 * BOARD_SIZE + 12] = true;
        assertArrayEquals(blankHits, hits);
    }

    @Test
    public void test_getChecked_VerticalMoveOnBoardWith1PointLetters_succeed(){
        when(mockBoard.getLetters()).thenReturn(new char[]{
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'E', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'E', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'E', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'E', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'E', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'
        });
        boolean[] hits = boardService.getChecked(mockBoard.getLetters());
        boolean[] blankHits = new boolean[BOARD_SIZE*BOARD_SIZE];
        Arrays.fill(blankHits, false);
        blankHits[7 * BOARD_SIZE + 7] = true;
        blankHits[8 * BOARD_SIZE + 7] = true;
        blankHits[9 * BOARD_SIZE + 7] = true;
        blankHits[10 * BOARD_SIZE + 7] = true;
        blankHits[11 * BOARD_SIZE + 7] = true;
        assertArrayEquals(hits, blankHits);
    }

    @Test
    public void test_getChecked_VerticalMoveOnBoardWithSome2PointLetters_succeed(){
        when(mockBoard.getLetters()).thenReturn(new char[]{
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'E', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'F', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'E', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'F', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'E', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'
        });
        boolean[] hits = boardService.getChecked(mockBoard.getLetters());
        boolean[] blankHits = new boolean[BOARD_SIZE*BOARD_SIZE];
        Arrays.fill(blankHits, false);
        blankHits[7 * BOARD_SIZE + 7] = true;
        blankHits[8 * BOARD_SIZE + 6] = true;
        blankHits[8 * BOARD_SIZE + 7] = true;
        blankHits[8 * BOARD_SIZE + 8] = true;
        blankHits[9 * BOARD_SIZE + 7] = true;
        blankHits[10 * BOARD_SIZE + 6] = true;
        blankHits[10 * BOARD_SIZE + 7] = true;
        blankHits[10 * BOARD_SIZE + 8] = true;
        blankHits[11 * BOARD_SIZE + 7] = true;
        assertArrayEquals(hits, blankHits);
    }

    @Test
    public void test_getCheckedOn_VerticalMoveBoardWithA3PointLetter_succeed(){
        when(mockBoard.getLetters()).thenReturn(new char[]{
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'E', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'F', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'E', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'F', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', 'Z', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'
        });
        boolean[] hits = boardService.getChecked(mockBoard.getLetters());
        boolean[] blankHits = new boolean[BOARD_SIZE*BOARD_SIZE];
        Arrays.fill(blankHits, false);
        blankHits[7 + BOARD_SIZE * 6] = true;
        blankHits[6 + BOARD_SIZE * 7] = true;
        blankHits[7 + BOARD_SIZE * 7] = true;
        blankHits[8 + BOARD_SIZE * 7] = true;
        blankHits[6 + BOARD_SIZE * 8] = true;
        blankHits[7 + BOARD_SIZE * 8] = true;
        blankHits[8 + BOARD_SIZE * 8] = true;
        blankHits[6 + BOARD_SIZE * 9] = true;
        blankHits[7 + BOARD_SIZE * 9] = true;
        blankHits[8 + BOARD_SIZE * 9] = true;
        blankHits[6 + BOARD_SIZE * 10] = true;
        blankHits[7 + BOARD_SIZE * 10] = true;
        blankHits[8 + BOARD_SIZE * 10] = true;
        blankHits[6 + BOARD_SIZE * 11] = true;
        blankHits[7 + BOARD_SIZE * 11] = true;
        blankHits[8 + BOARD_SIZE * 11] = true;
        blankHits[7 + BOARD_SIZE * 12] = true;
        assertArrayEquals(blankHits, hits);
    }

    @Test
    public void test_gameOver_duringGame(){
        char[] worms = new char[]{
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', 'S', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', 'S', '.', '.',
                '.', '.', '.', 'B', '.', '.', '.', '.', '.', '.', '.', '.', '.', 'S', '.', '.',
                '.', '.', '.', 'B', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', 'B', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', 'B', '.', '.', '.', 'A', 'A', 'A', 'A', 'A', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', 'D', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', 'D', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', 'C', 'C', 'C', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'
        };

        boolean[] checked = new boolean[BOARD_SIZE*BOARD_SIZE];
        Arrays.fill(checked, false);
        checked[4 * BOARD_SIZE + 3] = true;
        checked[4 * BOARD_SIZE + 4] = true;
        checked[4 * BOARD_SIZE + 5] = true;
        checked[4 * BOARD_SIZE + 6] = true;
        checked[6 * BOARD_SIZE] = true;
        checked[6 * BOARD_SIZE + 1] = true;
        checked[6 * BOARD_SIZE + 2] = true;
        checked[6 * BOARD_SIZE + 3] = true;
        checked[6 * BOARD_SIZE + 4] = true;
        checked[6 * BOARD_SIZE + 5] = true;
        checked[7 * BOARD_SIZE + 7] = true;
        checked[6 * BOARD_SIZE + 7] = true;
        checked[5 * BOARD_SIZE + 7] = true;
        checked[4 * BOARD_SIZE + 7] = true;

        try(MockedStatic<BoardService> mockedStatic = mockStatic(BoardService.class, CALLS_REAL_METHODS)) {
            mockedStatic.when(() -> BoardService.getByID(any())).thenReturn(mockBoard);
            mockedStatic.when(() -> BoardService.getOpposingBoard(any())).thenReturn(mockBoard);
            when(mockBoard.getWorms()).thenReturn(worms);
            mockedStatic.when(() -> BoardService.getChecked(any())).thenReturn(checked);
            mockedStatic.when(() -> BoardService.gameOver(any())).thenCallRealMethod();
        }
    }

    @Test
    public void test_gameOver_endOfGame(){

        char[] worms = new char[]{
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', 'S', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', 'S', '.', '.',
                '.', '.', '.', 'B', '.', '.', '.', '.', '.', '.', '.', '.', '.', 'S', '.', '.',
                '.', '.', '.', 'B', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', 'B', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', 'B', '.', '.', '.', 'A', 'A', 'A', 'A', 'A', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', 'D', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', 'D', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', 'C', 'C', 'C', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'
        };
        boolean[] checked = new boolean[BOARD_SIZE*BOARD_SIZE];
        Arrays.fill(checked, false);
        checked[4 * BOARD_SIZE + 3] = true;
        checked[4 * BOARD_SIZE + 4] = true;
        checked[4 * BOARD_SIZE + 5] = true;
        checked[4 * BOARD_SIZE + 6] = true;
        checked[6 * BOARD_SIZE] = true;
        checked[6 * BOARD_SIZE + 1] = true;
        checked[6 * BOARD_SIZE + 2] = true;
        checked[6 * BOARD_SIZE + 3] = true;
        checked[6 * BOARD_SIZE + 4] = true;
        checked[6 * BOARD_SIZE + 5] = true;
        checked[7 * BOARD_SIZE + 7] = true;
        checked[6 * BOARD_SIZE + 7] = true;
        checked[5 * BOARD_SIZE + 7] = true;
        checked[4 * BOARD_SIZE + 7] = true;

        try(MockedStatic<BoardService> mockedStatic = mockStatic(BoardService.class, CALLS_REAL_METHODS)) {
            mockedStatic.when(() -> BoardService.getByID(any())).thenReturn(mockBoard);
            mockedStatic.when(() -> BoardService.getOpposingBoard(any())).thenReturn(mockBoard);
            when(mockBoard.getWorms()).thenReturn(worms);
            mockedStatic.when(() -> BoardService.getChecked(any())).thenReturn(checked);
            mockedStatic.when(() -> BoardService.gameOver(any())).thenCallRealMethod();
        }
    }

    @Test
    public void test_calculateELO_WinWithEqualELO_succeed(){
        float elo = BoardService.calculateELO(1000, 1000, true);
        assertEquals(elo, 1016, 0.01);
    }

    @Test
    public void test_calculateELO_LossWithEqualELO_succeed(){
        float elo = BoardService.calculateELO(1000, 1000, false);
        assertEquals(elo, 984, 0.01);
    }

    @Test
    public void test_calculateELO_WinWithLessELO_succeed(){
        float elo = BoardService.calculateELO(1000, 1500, true);
        assertEquals(elo, 1030.29F, 0.01);
    }

    @Test
    public void test_calculateELO_LossWithLessELO_succeed(){
        float elo = BoardService.calculateELO(1000, 1500, false);
        assertEquals(elo, 998.30F, 0.01);
    }

    @Test
    public void test_calculateELO_WinWithMoreELO_succeed(){
        float elo = BoardService.calculateELO(1500, 1000, true);
        assertEquals(elo, 1501.70F, 0.01);
    }

    @Test
    public void test_calculateELO_LossWithMoreELO_succeed(){
        float elo = BoardService.calculateELO(1500, 1000, false);
        assertEquals(elo, 1469.70F, 0.01);
    }

    @Test
    public void test_endGame_succeed(){
        boardService.endGame(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        verify(mockRepo, times(1)).findBoardByGameID(any());
        verify(mockRepo, times(1)).deleteAll(any());
    }
}