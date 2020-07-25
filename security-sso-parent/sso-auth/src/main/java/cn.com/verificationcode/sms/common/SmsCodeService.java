package cn.com.verificationcode.sms.common;

import cn.com.verificationcode.sms.raw.SmsCode;
import com.sun.org.apache.bcel.internal.classfile.Code;

/**
 * @author wyl
 * @create 2020-07-18 17:45
 */
public interface SmsCodeService {

    public static final String PARAMETER_CODE_KEY = "smsCode";
    public static final String PARAMETER_MOBILE_KEY = "mobile";

    /**
     * 根据请求获取smsCode
     */
    SmsCode get();

    /**
     * 根据key获取
     * @param key
     * @return
     */
    SmsCode get(String key);

    String obtainSmsCode();

    String obtainMobile();


    /**
     * 根据请求删除
     */
    void remove();

    /**
     * 根据传入的参数创建smsCode
     * @param key
     * @return
     */
    SmsCode createAndSaveCode(String key);

    /**
     * 根据请求获取key
     */
    String getKey();
}
