package zlhywlf.app.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author zlhywlf
 */
@Data
public class User {
    private final long id;

    private final String email;

    @JsonIgnore
    private final String password;

}
