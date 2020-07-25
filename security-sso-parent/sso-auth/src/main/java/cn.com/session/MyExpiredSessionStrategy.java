package cn.com.session;

import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author wyl
 * @create 2020-07-13 15:11
 */
@Component
public class MyExpiredSessionStrategy implements SessionInformationExpiredStrategy {
    private HttpSessionRequestCache httpSessionRequestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
        HttpServletRequest request = event.getRequest();
        Map<String, String[]> parameterMap = request.getParameterMap();

        HttpServletResponse response = event.getResponse();
        SavedRequest savedRequest = httpSessionRequestCache.getRequest(request, response);

        String info;

        if (savedRequest == null) {
            info = "保留会话为空";
        } else {
            info = savedRequest.getRedirectUrl();
        }


        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        response.getWriter().write(parameterMap + "您的账号已经在别的地方登录，当前登录已失效。如果密码遭到泄露，请立即修改密码！");

    }
}
