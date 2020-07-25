package cn.com.verificationcode.sms.session;

import cn.com.verificationcode.sms.cache.CacheSmsCodeService;
import cn.com.verificationcode.sms.common.AbstractSmsCodeService;
import cn.com.verificationcode.sms.common.SmsCodeService;
import cn.com.verificationcode.sms.raw.SmsCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

/**
 * @author wyl
 * @create 2020-07-18 17:12
 */
@ConditionalOnMissingBean(CacheSmsCodeService.class)
@Component
public class SessionSmsCodeService extends AbstractSmsCodeService {
    @Autowired
    private HttpServletRequest request;

    @PostConstruct
    public void init() {
        super.setRequest(request);
    }


    @Override
    public SmsCode get() {
        HttpSession session;
        String mobile;
        if ((session = request.getSession()) != null
                && (mobile = this.obtainMobile()) != null) {
            return (SmsCode) session.getAttribute(mobile);
        }
        return null;
    }

    @Override
    public SmsCode get(String key) {
        HttpSession session;
        if ((session = request.getSession()) != null) {
            return (SmsCode) session.getAttribute(key);
        }
        return null;
    }

    @Override
    public void remove() {
        HttpSession session;
        String mobile;
        if ((session = request.getSession()) != null
                && (mobile = this.obtainMobile()) != null) {
            session.removeAttribute(mobile);
        }
    }

    @Override
    public SmsCode createAndSaveCode(String mobile) {
        SmsCode smsCode = super.createAndSendSmsCode(mobile);
        HttpSession session = request.getSession();
        if (session != null)
            session.setAttribute(mobile, smsCode);
        return smsCode;
    }

    @Override
    public String getKey() {
        return this.obtainMobile();
    }


}
