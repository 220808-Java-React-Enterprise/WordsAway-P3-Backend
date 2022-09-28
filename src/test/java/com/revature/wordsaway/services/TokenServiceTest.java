package com.revature.wordsaway.services;

import com.revature.wordsaway.models.User;
import com.revature.wordsaway.utils.customExceptions.AuthenticationException;
import com.revature.wordsaway.utils.customExceptions.InvalidRequestException;
import io.jsonwebtoken.JwtBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TokenServiceTest {
    private TokenService tokenService;

    @BeforeEach
    public void setup(){
        tokenService = new TokenService();
    }

    @AfterEach
    public void setdown(){
        tokenService = null;
    }

    @Test
    public void test_generateToken_Succeed(){
        String username = "username";
        JwtBuilder mockTokenBuilder = mock(JwtBuilder.class);
        String token = tokenService.generateToken(username);
        assertNotNull(token);
    }

    @Test
    public void test_extractRequesterDetails_succeed(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(any())).thenReturn(tokenService.generateToken("username")); //This is almost certainly bad, but I don't know how to test it without this.
        UserService userService = mock(UserService.class);
        MockedStatic<UserService> userServiceMockedStatic = mockStatic(UserService.class);
        userServiceMockedStatic.when(() -> UserService.getByUsername(any())).thenReturn(mock(User.class));
        tokenService.extractRequesterDetails(request);
        userServiceMockedStatic.close();
    }

    @Test
    public void test_extractRequesterDetails_fail(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(any())).thenReturn("test.token.");
        UserService userService = mock(UserService.class);
        MockedStatic<UserService> userServiceMockedStatic = mockStatic(UserService.class);
        userServiceMockedStatic.when(() -> UserService.getByUsername(any())).thenReturn(mock(User.class));
        AuthenticationException thrown = Assertions.assertThrows(AuthenticationException.class, () -> {
            tokenService.extractRequesterDetails(request);
        });
        userServiceMockedStatic.close();
    }

    @Test
    public void test_extractRequesterDetails_NoToken_fail(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(any())).thenReturn(null);
        UserService userService = mock(UserService.class);
        MockedStatic<UserService> userServiceMockedStatic = mockStatic(UserService.class);
        userServiceMockedStatic.when(() -> UserService.getByUsername(any())).thenReturn(mock(User.class));
        AuthenticationException thrown = Assertions.assertThrows(AuthenticationException.class, () -> {
            tokenService.extractRequesterDetails(request);
        });
        userServiceMockedStatic.close();
        Assertions.assertEquals("No Authorization token provided.", thrown.getMessage());
    }

}