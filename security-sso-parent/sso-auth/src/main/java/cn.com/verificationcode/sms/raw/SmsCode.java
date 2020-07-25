package cn.com.verificationcode.sms.raw;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

/**
 * @author wyl
 * @create 2020-07-12 09:42
 */
public class SmsCode implements Serializable {
    private static final long serialVersionUID = 6112883028593203486L;
    private String code;
    private long expire;

    /**
     * 序列化使用
     */
    public SmsCode() {
    }

    public SmsCode(String code, long addExpire, TimeUnit timeUnit) {
        this.code = code;
        this.expire = System.currentTimeMillis() + timeUnit.toMillis(addExpire);
    }

    public SmsCode(String code, LocalDateTime addDateTime) {
        this.code = code;
        this.expire = System.currentTimeMillis() + addDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public boolean isExpire() {
        return System.currentTimeMillis() >= this.expire;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }
}
