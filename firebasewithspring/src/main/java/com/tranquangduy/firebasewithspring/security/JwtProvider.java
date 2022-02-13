package com.tranquangduy.firebasewithspring.security;

import com.tranquangduy.firebasewithspring.authority.MyUserDetail;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
public class JwtProvider implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);
    private final String JWT_KEYSECRET = "hahaha";
    private static final long JWT_EXPIRATION = 30*24*60*60*1000L;

    public String generateToken(MyUserDetail userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
        return Jwts.builder()
                .setSubject(userDetails.getUser().getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, JWT_KEYSECRET)
                .compact();
    }

    public String getUserNameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_KEYSECRET)
                .parseClaimsJws(token)
                .getBody();
        return String.valueOf(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            if(!authToken.isEmpty()){
                Jwts.parser().setSigningKey(JWT_KEYSECRET).parseClaimsJws(authToken);
                return true;
            }
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

}
