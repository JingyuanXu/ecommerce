package com.example.demo.security;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;


public class JWTAuthenticationVerficationFilter extends BasicAuthenticationFilter {

    private Logger log = LoggerFactory.getLogger(JWTAuthenticationVerficationFilter.class);
    public JWTAuthenticationVerficationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {

        String jwtToken = req.getHeader(SecurityConstants.HEADER_STRING);

        if (jwtToken == null || !jwtToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(jwtToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }
    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        String user = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()))
                .build()
                .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                .getSubject();

        if (user != null) {
            log.info("pass authentication");
            return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        }
        return null;

    }
}
