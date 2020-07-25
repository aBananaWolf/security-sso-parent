package cn.com.verificationcode.img;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

/**
 * @author wyl
 * @create 2020-07-12 09:42
 */
public class ImageCode implements Serializable {
    private String code;
    private long expire;
    transient private BufferedImage bufferedImage;

    public ImageCode(String code, BufferedImage bufferedImage , long addExpire, TimeUnit timeUnit) {
        this.code = code;
        this.expire = System.currentTimeMillis() + timeUnit.toMillis(addExpire);
        this.bufferedImage = bufferedImage;
    }

    public ImageCode(String code, BufferedImage bufferedImage, LocalDateTime addDateTime) {
        this.code = code;
        this.expire = System.currentTimeMillis() + addDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        this.bufferedImage = bufferedImage;
    }

    public boolean isExpire(){
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

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }
}
