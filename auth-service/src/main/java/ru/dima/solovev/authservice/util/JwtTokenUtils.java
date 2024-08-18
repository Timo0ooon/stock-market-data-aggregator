package ru.dima.solovev.authservice.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JwtTokenUtils {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private int lifetime;

    public String generateToken(String name) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(lifetime).toInstant());

        return JWT.create()
                .withSubject("User details")
                .withClaim("Name", name)
                .withIssuedAt(new Date())
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
    }

    public String verifyToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).withSubject("User details").build();

        DecodedJWT jwt = verifier.verify(token);

        return jwt.getClaim("name").asString();
    }
}
