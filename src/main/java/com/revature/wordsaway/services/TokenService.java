package com.revature.wordsaway.services;

import com.revature.wordsaway.models.User;
import com.revature.wordsaway.utils.JwtConfig;
import com.revature.wordsaway.utils.customExceptions.AuthenticationException;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class TokenService {
    private static JwtConfig jwtConfig = new JwtConfig();

    public TokenService() {
        super();
    }

    public static String generateToken(String username) {
        long now = System.currentTimeMillis();
        JwtBuilder tokenBuilder = Jwts.builder()
                .setId(username)
                .setIssuer("WordsAway")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtConfig.getExpiration()))
                .setSubject(username)
                .signWith(jwtConfig.getSigAlg(), jwtConfig.getSigningKey());

        return tokenBuilder.compact();
    }

    public static User extractRequesterDetails(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if((token == null) || (token.isEmpty())){
            throw new AuthenticationException("No Authorization token provided.");
        }
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();
            return UserService.getByUsername(claims.getSubject());
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
                 IllegalArgumentException e) {
            throw new AuthenticationException(e.getMessage());
        }
    }
}