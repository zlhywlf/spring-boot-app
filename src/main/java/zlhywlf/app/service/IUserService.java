package zlhywlf.app.service;

import zlhywlf.app.model.User;

/**
 * @author zlhywlf
 */
public interface IUserService {

    User loadUserByUsername(String username);
}
