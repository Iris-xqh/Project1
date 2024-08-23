package com.example;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"rsa.private-key=classpath:keys/testPrivate.pem", "rsa.public-key=classpath:keys/testPublic.pem"})
@SuppressWarnings("rawtypes")
public class CustomerApiTest {

    // TestRestTemplate is a convenience class provided by Spring Boot for testing RESTful services
    @Autowired
    TestRestTemplate template;

    // Test to check if the API works without authentication
    // The test should return an internal server error
    @Test
    void testWithoutAuth() {
        RequestEntity request = RequestEntity
                .get("/api/customers")
                .build();

        ResponseEntity<String> responseEntity = template.exchange(request, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Test to check if the API works with authentication
    // The test should return an OK status
    @Test
    public void testWithAuth() {
        String token = generateToken();

        RequestEntity request = RequestEntity
                .get("/api/customers")
                .header("Authorization", "Bearer " + token)
                .build();

        ResponseEntity<String> responseEntity = template.exchange(request, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // Generate a JWT token
    private String generateToken() {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        String generatedToken = JWT.create()
                .withIssuer("auth0")
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(new Date().toInstant().plusSeconds(7200))) // 2 hour expiration
                .withClaim("name", "authServer")
                .sign(algorithm);

        return generatedToken;
    }
}

