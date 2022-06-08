package zlhywlf.app.mapper;

import zlhywlf.app.model.User;

/**
 * @author zlhywlf
 */
public interface UserMapper {
    /**
     * loadUserByUsername
     *
     * @param username username
     * @return User
     */
    User loadUserByUsername(String username);
}
