package edu.ada.grupo5.movies_api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import edu.ada.grupo5.movies_api.Repositories.TokenRepository;
import edu.ada.grupo5.movies_api.dto.ResponseDTO;
import edu.ada.grupo5.movies_api.model.Token;
import edu.ada.grupo5.movies_api.model.TokenType;
import edu.ada.grupo5.movies_api.model.User;
import edu.ada.grupo5.movies_api.service.exception.ResourceNotFoundException;
import edu.ada.grupo5.movies_api.service.exception.ValidationErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


//TODO : revisar excecao personalizada
@Service
public class TokenService {

    @Autowired
    private final TokenRepository tokenRepository;

    @Value("${tmdb.api.secret}")
    private String secret;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Token generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            var token = JWT.create()
                    .withIssuer("movies-api")
                    .withSubject(user.getLogin())
                    .withExpiresAt(getExpirationDate())
                    .sign(algorithm);
            return Token.builder()
                    .user(user)
                    .token(token)
                    .tokenType(TokenType.BEARER)
                    .revoked(false)
                    .expired(false)
                    .build();
        } catch (JWTCreationException e) {
            throw new ValidationErrorException("Token creation failed");
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("movies-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            throw new ValidationErrorException("Token validation failed");
        }
    }

    public void saveUserToken(Token token) {
        tokenRepository.save(token);
    }

    private Instant getExpirationDate() {
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
    }

    public ResponseDTO<String> generateResponse(String token) {
        if (token.isEmpty()) {
            throw new ResourceNotFoundException("Token not found");
        }
        return ResponseDTO.<String>builder()
                .message("Token generated sucessfully")
                .timestamp(Instant.now())
                .data(token)
                .build();
    }
}
