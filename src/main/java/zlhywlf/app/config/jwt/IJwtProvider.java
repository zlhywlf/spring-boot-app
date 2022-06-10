package zlhywlf.app.config.jwt;


/**
 * @author zlhywlf
 */
public interface IJwtProvider {

    /**
     * 创建token
     *
     * @param username 存入 username
     * @return token
     */
    String createToken(String username);


    /**
     * 获取用户名
     *
     * @param token token
     * @return 认证信息
     */
    String getUsername(String token);

}
