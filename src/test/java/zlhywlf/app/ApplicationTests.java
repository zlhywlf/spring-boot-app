package zlhywlf.app;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zlhywlf.app.config.jwt.JwtProvider;

/**
 * @author zlhywlf
 */
@SpringBootTest
@Slf4j
public class ApplicationTests {
    @Autowired
    JwtProvider jwt;

    @Test
    public void jwtTest() {
        jwt.setTokenValidity(1L);
        String token = jwt.createToken("username");
        log.info(token);
        String username = jwt.getUsername(token);
        log.info(username);
    }
}
