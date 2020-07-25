package cn.com.verificationcode.sms.cache;

import cn.com.verificationcode.sms.common.AbstractSmsCodeService;
import cn.com.verificationcode.sms.common.SmsCodeService;
import cn.com.verificationcode.sms.raw.SmsCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.TIMEOUT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * @author wyl
 * @create 2020-07-18 18:05
 */
@Component
public class CacheSmsCodeService extends AbstractSmsCodeService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private HttpServletRequest request;

    private final String SMS_CODE_PREFIX = "SMS_CODE:";

    @PostConstruct
    public void init() {
        super.setRequest(request);
    }


    @Override
    public SmsCode get() {
        String value = redisTemplate.opsForValue().get(this.getKey());
        return extractCacheValue(value);
    }


    @Override
    public SmsCode get(String key) {
        String value = redisTemplate.opsForValue().get(this.getKey(key));
        return extractCacheValue(value);
    }

    private SmsCode extractCacheValue(String value) {
        if (StringUtils.isNotBlank(value)) {
            try {
                return objectMapper.readValue(value,SmsCode.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    @Override
    public void remove() {
        String key = this.getKey();
        if (StringUtils.isNotBlank(key))
            redisTemplate.delete(key);
    }

    @Override
    public SmsCode createAndSaveCode(String mobile) {
        SmsCode smsCode = super.createAndSendSmsCode(mobile);
        try {
            redisTemplate.opsForValue().set(
                    this.getKey(mobile),
                    objectMapper.writeValueAsString(smsCode),
                    5, TimeUnit.MINUTES);
            return smsCode;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getKey() {
        return this.getKey( super.obtainMobile());
    }

    public String getKey(String detailKey) {
        return SMS_CODE_PREFIX + detailKey;
    }

}
