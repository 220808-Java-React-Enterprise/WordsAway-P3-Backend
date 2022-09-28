package com.revature.wordsaway.services;

import com.revature.wordsaway.dtos.requests.LoginRequest;
import com.revature.wordsaway.dtos.requests.NewUserRequest;
import com.revature.wordsaway.dtos.responses.OpponentResponse;
import com.revature.wordsaway.models.Board;
import com.revature.wordsaway.models.User;
import com.revature.wordsaway.repositories.BoardRepository;
import com.revature.wordsaway.repositories.UserRepository;
import com.revature.wordsaway.utils.customExceptions.AuthenticationException;
import com.revature.wordsaway.utils.customExceptions.InvalidRequestException;
import com.revature.wordsaway.utils.customExceptions.ResourceConflictException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class UserServiceTest {
    private UserRepository mockUserRepo;
    private BoardRepository mockBoardRepo;
    private UserService userService;
    private NewUserRequest mockRequest;
    private MockedStatic<TokenService> tokenServiceMockedStatic;
    private User mockUser;

    @BeforeEach
    public void setup() {
        mockUserRepo = mock(UserRepository.class);
        mockBoardRepo = mock(BoardRepository.class);
        userService = new UserService(mockUserRepo, mockBoardRepo);
        mockRequest = mock(NewUserRequest.class);
        when(mockRequest.getUsername()).thenReturn("username");
        when(mockRequest.getPassword()).thenReturn("password");
        when(mockRequest.getEmail()).thenReturn("username@email.com");
        when(mockRequest.getSalt()).thenReturn("00000000000000000000000000000000");
        tokenServiceMockedStatic = mockStatic(TokenService.class);
        tokenServiceMockedStatic.when(() -> TokenService.generateToken(any())).thenReturn("testtoken");
        mockUser = mock(User.class);
    }

    @AfterEach
    public void setdown(){
        mockUserRepo = null;
        mockBoardRepo = null;
        userService = null;
        mockRequest = null;
        tokenServiceMockedStatic.close();
        tokenServiceMockedStatic = null;
        mockUser = null;
    }

    @Test
    public void test_register_WithNullEmail_succeed(){
        when(mockRequest.getEmail()).thenReturn(null);
        when(mockUserRepo.findUserByUsername(any())).thenReturn(null);
        when(mockUserRepo.findAll()).thenReturn(new ArrayList<>());
        User user = userService.register(mockRequest);
        verify(mockUserRepo, times(1)).save(any());
        assertNotNull(user);
        assertEquals(user.getUsername(), "username");
        assertEquals(user.getPassword(), "password");
        assertNull(user.getEmail());
        assertEquals(user.getSalt(), "00000000000000000000000000000000");
    }

    @Test
    public void test_register_WithEmail_succeed(){
        when(mockUserRepo.findUserByUsername(any())).thenReturn(null);
        when(mockUserRepo.findAll()).thenReturn(new ArrayList<>());
        User user = userService.register(mockRequest);
        verify(mockUserRepo, times(1)).save(any());
        assertNotNull(user);
        assertEquals(user.getUsername(), "username");
        assertEquals(user.getPassword(), "password");
        assertEquals(user.getEmail(), "username@email.com");
        assertEquals(user.getSalt(), "00000000000000000000000000000000");
    }

    @Test
    public void test_register_WithShortUsername_fail(){
        when(mockRequest.getUsername()).thenReturn("u");
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockUserRepo, times(0)).save(any());
        Assertions.assertEquals("Username must start with a letter and consist of between 3 and 15 alphanumeric characters.",
                thrown.getMessage());
    }

    @Test
    public void test_register_WithLongUsername_fail(){
        when(mockRequest.getUsername()).thenReturn("usernameusername");
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockUserRepo, times(0)).save(any());
        Assertions.assertEquals("Username must start with a letter and consist of between 3 and 15 alphanumeric characters.",
                thrown.getMessage());
    }

    @Test
    public void test_register_BadCharacterUsername_fail(){
        when(mockRequest.getUsername()).thenReturn("üsername");
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockUserRepo, times(0)).save(any());
        Assertions.assertEquals("Username must start with a letter and consist of between 3 and 15 alphanumeric characters.",
                thrown.getMessage());
    }

    /**
    @Test
    public void test_register_WithShortPassword_fail(){
        when(mockRequest.getPassword()).thenReturn("p");
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockRepo, times(0)).save(any());
        Assertions.assertEquals("Password must be between 5 and 30 alphanumeric or special characters.",
                thrown.getMessage());
    }

    @Test
    public void test_register_WithLongPassword_fail(){
        when(mockRequest.getPassword()).thenReturn("passwordpasswordpasswordpassword");
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockRepo, times(0)).save(any());
        Assertions.assertEquals("Password must be between 5 and 30 alphanumeric or special characters.",
                thrown.getMessage());
    }

    @Test
    public void test_register_BadCharacterPassword_fail(){
        when(mockRequest.getPassword()).thenReturn("passwørd");
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockRepo, times(0)).save(any());
        Assertions.assertEquals("Password must be between 5 and 30 alphanumeric or special characters.",
                thrown.getMessage());
    }
    **/

    @Test
    public void test_register_ShortEmail_fail(){
        when(mockRequest.getEmail()).thenReturn("@email.com");
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockUserRepo, times(0)).save(any());
        Assertions.assertEquals("Invalid Email Address.", thrown.getMessage());
    }

    @Test
    public void test_register_ShortEmailDomainName_fail(){
        when(mockRequest.getEmail()).thenReturn("username@.com");
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockUserRepo, times(0)).save(any());
        Assertions.assertEquals("Invalid Email Address.", thrown.getMessage());
    }

    @Test
    public void test_register_ShortEmailDomainExtension_fail(){
        when(mockRequest.getEmail()).thenReturn("username@email.");
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockUserRepo, times(0)).save(any());
        Assertions.assertEquals("Invalid Email Address.", thrown.getMessage());
    }

    @Test
    public void test_register_BadStartingCharacterEmail_fail(){
        when(mockRequest.getEmail()).thenReturn("#username@email.com");
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockUserRepo, times(0)).save(any());
        Assertions.assertEquals("Invalid Email Address.", thrown.getMessage());
    }

    @Test
    public void test_register_BadCharacterEmail_fail(){
        when(mockRequest.getEmail()).thenReturn("usernamë@email.com");
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockUserRepo, times(0)).save(any());
        Assertions.assertEquals("Invalid Email Address.", thrown.getMessage());
    }

    @Test
    public void test_register_BadCharacterDomainNameEmail_fail(){
        when(mockRequest.getEmail()).thenReturn("username@@email.com");
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockUserRepo, times(0)).save(any());
        Assertions.assertEquals("Invalid Email Address.", thrown.getMessage());
    }

    @Test
    public void test_register_BadCharacterDomainExtensionEmail_fail(){
        when(mockRequest.getEmail()).thenReturn("username@email.cöm");
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockUserRepo, times(0)).save(any());
        Assertions.assertEquals("Invalid Email Address.", thrown.getMessage());
    }

    @Test
    public void test_register_LongEmail_fail(){
        when(mockRequest.getEmail()).thenReturn("usernameusernameusernameusernameusernameusernameusernameusernameusername@email.com");
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockUserRepo, times(0)).save(any());
        Assertions.assertEquals("Invalid Email Address.", thrown.getMessage());
    }

    @Test
    public void test_register_LongDomainNameEmail_fail(){
        when(mockRequest.getEmail()).thenReturn("username@emailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemailemail.com");
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockUserRepo, times(0)).save(any());
        Assertions.assertEquals("Invalid Email Address.", thrown.getMessage());
    }

    @Test
    public void test_register_LongDomainExtensionEmail_fail(){
        when(mockRequest.getEmail()).thenReturn("username@email.comcomcomcomcomcomcomcomcom");
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockUserRepo, times(0)).save(any());
        Assertions.assertEquals("Invalid Email Address.", thrown.getMessage());
    }

    @Test
    public void test_register_WithTakenUsername_fail(){
        when(mockUserRepo.findUserByUsername(any())).thenReturn(mock(User.class));
        ResourceConflictException thrown = Assertions.assertThrows(ResourceConflictException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockUserRepo, times(0)).save(any());
        Assertions.assertEquals("Username is already taken, please choose another.", thrown.getMessage());
    }

    @Test
    public void test_register_WithTakenEmail_fail(){
        when(mockUserRepo.findUserByEmail(any())).thenReturn(mock(User.class));
        ResourceConflictException thrown = Assertions.assertThrows(ResourceConflictException.class, () -> {
            userService.register(mockRequest);
        });
        verify(mockUserRepo, times(0)).save(any());
        Assertions.assertEquals("Email is already taken, please choose another.", thrown.getMessage());
    }

    @Test
    public void test_update_succeed(){
        when(mockUser.getUsername()).thenReturn("username");
        when(mockUser.getPassword()).thenReturn("password");
        when(mockUser.getSalt()).thenReturn("00000000000000000000000000000000");
        when(mockUser.getELO()).thenReturn(1000F);
        when(mockUser.getGamesPlayed()).thenReturn(0);
        when(mockUser.getGamesWon()).thenReturn(0);
        when(mockUser.isCPU()).thenReturn(false);
        userService.update(mockUser);
        //verify(mockUserRepo, times(1)).updateUser(any(), any(), any(), any(), any(), any()); //TODO figure out why this doesn't work
        verify(mockUser, times(1)).getUsername();
        verify(mockUser, times(1)).getPassword();
        verify(mockUser, times(1)).getELO();
        verify(mockUser, times(1)).getGamesPlayed();
        verify(mockUser, times(1)).getGamesWon();
    }

    @Test
    public void test_login_succeed(){
        LoginRequest request = mock(LoginRequest.class);
        when(request.getPassword()).thenReturn("password");
        when(request.getUsername()).thenReturn("username");
        when(mockUser.getPassword()).thenReturn("password");
        when(mockUserRepo.findUserByUsername(any())).thenReturn(mockUser);
        String token = userService.login(request);
        verify(mockUserRepo, times(1)).findUserByUsername(any());
        assertEquals(token, "testtoken");
    }

    @Test
    public void test_login_fail(){
        LoginRequest request = mock(LoginRequest.class);
        when(request.getPassword()).thenReturn("wrong password");
        when(request.getUsername()).thenReturn("username");
        when(mockUser.getPassword()).thenReturn("password");
        when(mockUserRepo.findUserByUsername(any())).thenReturn(mockUser);
        final String[] token = new String[1];
        AuthenticationException thrown = Assertions.assertThrows(AuthenticationException.class, () -> {
            token[0] = userService.login(request);
        });
        verify(mockUserRepo, times(1)).findUserByUsername(any());
        Assertions.assertEquals("Login unsuccessful. Please check username and password.", thrown.getMessage());
        assertNull(token[0]);
    }

    @Test
    public void test_getByUsername_succeed(){
        when(mockUserRepo.findUserByUsername(any())).thenReturn(mockUser);
        User user = userService.getByUsername("username");
        verify(mockUserRepo, times(1)).findUserByUsername(any());
        assertNotNull(user);
    }

    @Test
    public void test_getByUsername_fail(){
        when(mockUserRepo.findUserByUsername(any())).thenReturn(null);
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.getByUsername("username");
        });
        verify(mockUserRepo, times(1)).findUserByUsername(any());
        Assertions.assertEquals("No user with username username found.", thrown.getMessage());
    }

    @Test
    public void test_getAll_succeed(){
        when(mockUserRepo.findAll()).thenReturn(Arrays.asList(mockUser));
        List<User> users = userService.getAll();
        verify(mockUserRepo, times(1)).findAll();
        assertNotNull(users);
    }

    @Test
    public void test_getAll_fail(){
        when(mockUserRepo.findAll()).thenReturn(new ArrayList<>());
        InvalidRequestException thrown = Assertions.assertThrows(InvalidRequestException.class, () -> {
            userService.getAll();
        });
        verify(mockUserRepo, times(1)).findAll();
        Assertions.assertEquals("No users found.", thrown.getMessage());
    }

    @Test void test_getAllOpponents_succeed(){
        when(mockUserRepo.findAllOtherUsers(any())).thenReturn(Arrays.asList(mockUser));
        when(mockUser.getUsername()).thenReturn("username");
        when(mockUser.getELO()).thenReturn(1000F);
        Board mockBoard = mock(Board.class);
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        when(mockBoard.getId()).thenReturn(uuid);
        when(mockBoard.getUser()).thenReturn(mockUser);
        when(mockBoardRepo.findBoardsByTwoUsernames(any(), any())).thenReturn(Arrays.asList(mockBoard));
        List<OpponentResponse> opponents = userService.getAllOpponents("username");
        verify(mockUserRepo, times(1)).findAllOtherUsers(any());
        verify(mockBoardRepo, times(1)).findBoardsByTwoUsernames(any(), any());
        assertNotNull(opponents);
        assertEquals(opponents.size(), 1);
        assertEquals(opponents.get(0).getUsername(), "username");
        assertEquals(opponents.get(0).getElo(), 1000F);
        assertEquals(opponents.get(0).getBoard_id(), uuid);
    }

    @Test void test_getAllOpponents_NoOtherUsers_succeed(){
        when(mockUserRepo.findAllOtherUsers(any())).thenReturn(new ArrayList<>());
        List<OpponentResponse> opponents = userService.getAllOpponents("username");
        verify(mockUserRepo, times(1)).findAllOtherUsers(any());
        verify(mockBoardRepo, times(0)).findBoardsByTwoUsernames(any(), any());
        assertNotNull(opponents);
        assertEquals(opponents.size(), 0);
    }

    @Test void test_getAllOpponents_NoBoards_succeed(){
        when(mockUserRepo.findAllOtherUsers(any())).thenReturn(Arrays.asList(mockUser));
        when(mockUser.getUsername()).thenReturn("username");
        when(mockUser.getELO()).thenReturn(1000F);
        when(mockBoardRepo.findBoardsByTwoUsernames(any(), any())).thenReturn(new ArrayList<>());
        List<OpponentResponse> opponents = userService.getAllOpponents("username");
        verify(mockUserRepo, times(1)).findAllOtherUsers(any());
        verify(mockBoardRepo, times(1)).findBoardsByTwoUsernames(any(), any());
        assertNotNull(opponents);
        assertEquals(opponents.size(), 1);
        assertEquals(opponents.get(0).getUsername(), "username");
        assertEquals(opponents.get(0).getElo(), 1000F);
        assertNull(opponents.get(0).getBoard_id());
    }
}