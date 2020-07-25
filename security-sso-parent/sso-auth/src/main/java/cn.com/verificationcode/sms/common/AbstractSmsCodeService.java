package cn.com.verificationcode.sms.common;

import cn.com.verificationcode.sms.raw.SmsCode;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * @author wyl
 * @create 2020-07-18 20:45
 */
public abstract class AbstractSmsCodeService implements SmsCodeService{
    private HttpServletRequest request;

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String obtainSmsCode() {
        return request.getParameter(PARAMETER_CODE_KEY);
    }

    @Override
    public String obtainMobile() {
        return request.getParameter(PARAMETER_MOBILE_KEY);
    }

    public SmsCode createAndSendSmsCode(String mobile) {
        // create
        String code = String.valueOf((int) ((Math.random() + 1) * 100000));
        // smsInterface.send(mobile); // 非常简单，只需要集成阿里云短信
        return new SmsCode(code, 5, TimeUnit.MINUTES);
    }
}
