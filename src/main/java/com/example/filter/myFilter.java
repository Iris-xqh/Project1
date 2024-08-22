package com.example.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class myFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("MyFilter");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        Algorithm algorithm = Algorithm.HMAC256("secret");

        String authServerName = "authServer";
        String clientName = "client";

        try {
            String token = httpRequest.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // Remove Bearer prefix
                // Verify token
                JWTVerifier verifier = JWT.require(algorithm)
                        .withIssuer("auth0")
                        .build();
                DecodedJWT jwt = verifier.verify(token);
                String name = jwt.getClaim("name").asString();
                if(name != null && (name.equals(authServerName) || name.equals(clientName))) {
                    System.out.println("Token is valid. User: " + name);
                    filterChain.doFilter(request, response); // Token is valid, continue
                    return;
                }
            } else {
                throw new JWTVerificationException("Missing or invalid Authorization header");
            }
        } catch (JWTVerificationException exception) {
            // Token verification failed
            throw new ServletException("Unauthorized: Token verification failed", exception);
        }
    }
}
