package com.example;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.domain.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    // Test get by ID with authentication
    @Test
    public void testGetWithAuth() {
        String token = generateToken();

        RequestEntity request = RequestEntity
                .get("/api/customers/{id}", 1)
                .header("Authorization", "Bearer " + token)
                .build();

        ResponseEntity<String> responseEntity = template.exchange(request, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).contains("id").contains("name").contains("email").contains("password");
    }

    // Test post with authentication
    @Test
    public void testPostWithAuth() {
        String token = generateToken();

        Customer customer = new Customer();
        customer.setName("Mungo");
        customer.setEmail("mungo@qq.com");
        customer.setPassword("123456");

        HttpEntity<Customer> request = generateRequestEntity(token, customer);

        URI location = template.postForLocation("/api/customers", request, Customer.class);
        assertNotNull(location);

        HttpEntity<String> requestForGet = generateRequestEntity(token, null);

        ResponseEntity<Customer> response = template.exchange(location, HttpMethod.GET, requestForGet, Customer.class);

        Customer customerGet = response.getBody();

        assertNotNull(customerGet);
        assertNotNull(customerGet.getId());
        assertEquals("Mungo", customerGet.getName());
        assertEquals("mungo@qq.com", customerGet.getEmail());
    }

    // generate a request entity with a JWT token
    private <T> HttpEntity<T> generateRequestEntity(String token, T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return new HttpEntity<>(body, headers);
    }

    // Test put with authentication
    @Test
    public void testPutWithAuth() {
        String token = generateToken();

        Customer customer = new Customer();
        customer.setId(2);
        customer.setName("Mungo"); // change the name
        customer.setEmail("paul@gmail.com");
        customer.setPassword("222password");

        HttpEntity<Customer> request = generateRequestEntity(token, customer);
        ResponseEntity<Customer> response = template.exchange("/api/customers/{id}", HttpMethod.PUT, request, Customer.class, 2);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<String> requestForGet = generateRequestEntity(token, null);
        ResponseEntity<Customer> responseGet = template.exchange("/api/customers/{id}", HttpMethod.GET, requestForGet, Customer.class, 2);
        Customer cus = responseGet.getBody();
        assertThat(cus.getName()).isEqualTo("Mungo");

    }

    // Test delete with authentication
    @Test
    public void testDeleteWithAuth() {
        String token = generateToken();

        HttpEntity<String> request = generateRequestEntity(token, null);
        ResponseEntity<String> response = template.exchange("/api/customers/{id}", HttpMethod.DELETE, request, String.class, 2);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<String> requestForGet = generateRequestEntity(token, null);
        ResponseEntity<String> responseGet = template.exchange("/api/customers/{id}", HttpMethod.GET, requestForGet, String.class, 2);
        assertThat(responseGet.getBody()).isEqualTo("null");
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

