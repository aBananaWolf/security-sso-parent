package cn.com.verificationcode.img;

import org.springframework.security.core.AuthenticationException;

/**
 * @author wyl
 * @create 2020-07-12 13:09
 */
public class ValidateCodeException extends AuthenticationException {

    public ValidateCodeException(String msg) {
        super(msg);
    }
}
