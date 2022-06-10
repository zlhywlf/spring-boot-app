package zlhywlf.app.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zlhywlf
 */
@RestController
public class Tmp {

    @GetMapping("/index")
    public String hello(){
        return "hello";
    }
}
