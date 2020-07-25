package cn.com.verificationcode.sms.raw;

import cn.com.verificationcode.sms.common.SmsCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

/**
 * @author wyl
 * @create 2020-07-12 20:44
 */
@RestController
@RequestMapping("/code")
public class SmsCodeController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @Autowired
    private SmsCodeService smsCodeServiceWrapper;


    @GetMapping("/sms")
    public void createCode(String mobile) {
        SmsCode smsCode = smsCodeServiceWrapper.createAndSaveCode(mobile);
        System.out.println("系统生成了一条验证码：" + smsCode.getCode() + " 两分钟内有效，请尽快登录" );
    }


}
