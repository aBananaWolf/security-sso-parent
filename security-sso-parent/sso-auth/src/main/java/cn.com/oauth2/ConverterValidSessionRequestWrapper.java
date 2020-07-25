package cn.com.oauth2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author wyl
 * @create 2020-07-23 17:14
 */
public class ConverterValidSessionRequestWrapper extends HttpServletRequestWrapper {
    public ConverterValidSessionRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return true;
    }
}
