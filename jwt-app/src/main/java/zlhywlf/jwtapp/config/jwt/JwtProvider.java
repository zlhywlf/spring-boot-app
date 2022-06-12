package zlhywlf.jwtapp.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * @author zlhywlf
 */
@Component
@Getter
@Setter
@Slf4j
public class JwtProvider implements IJwtProvider {
    private final static String AUTH_KEY = "AUTH";
    @Value("${jwt.base64-secret}")
    private String base64Secret;
    @Value("${jwt.token-validity}")
    private long tokenValidity;
    private Key secretKey;

    @PostConstruct
    public void init() {
        byte[] decode = Decoders.BASE64.decode(base64Secret);
        this.secretKey = Keys.hmacShaKeyFor(decode);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }

    @Override
    public String createToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidity))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String getUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            log.warn(e.getMessage());
            return null;
        }
    }


}
