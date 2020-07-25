package cn.com.verificationcode.img;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * @author wyl
 * @create 2020-07-12 10:07
 */
@RestController
@RequestMapping("/code")
public class ValidateCodeController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    public static final String SESSION_KEY_IMAGE_CODE = "SESSION_KEY_IMAGE_CODE";

    public final String[] RANDOM_CODE = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","0","1","2","3","4","5","6","7","8","9"};


    @GetMapping("/image")
    public void createCode() {
        ImageCode imageCode = this.createImageCode();
        HttpSession session = request.getSession();
        if (session != null)
            session.setAttribute(SESSION_KEY_IMAGE_CODE,imageCode);
        try {
            ImageIO.write(imageCode.getBufferedImage(),"jpeg",response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ImageCode createImageCode() {
        int width = 100; // 验证码图片宽度
        int height = 36; // 验证码图片长度
        int length = 4; // 验证码位数
        int expireIn = 120; // 验证码有效时间 120s

        // 随机数
        Random random = new Random();

        // 创建图片对象
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 2d 画笔
        Graphics2D graphics = (Graphics2D)image.getGraphics();
        // 调整画笔rgb颜色
        graphics.setColor(this.getColor(200,255, random));
        // 填充正方形
        graphics.fillRect(0,0,width,height);
        graphics.setFont(new Font("微软雅黑", Font.TRUETYPE_FONT, 100));
        // 开始绘画一些横线，注意调整画笔rgb颜色
        // 要求1：不要和背景颜色重合，没有意义
        // 要求2：需要和验证码颜色部分重合，混淆视觉
        int strokesCount = 150;
        int Scribing = (int) (strokesCount * 0.3);
        for (int i = 0; i < strokesCount; i++) {
            graphics.setColor(this.getColor(90,200, random));
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(50);
            int yl = random.nextInt(height);
            if (i % Scribing == 0) {
                // 横线
                graphics.setColor(this.getColor(40,200, random));
                graphics.drawLine(0, y, 100, yl);
            }
            else
                // 随机画笔
                graphics.drawLine(x, y, x + xl, y + yl);
        }
        // 切换字体，开始绘画验证码
        graphics.setFont(new Font("微软雅黑", Font.TRUETYPE_FONT, 30));

        StringBuilder sRand = new StringBuilder();
        for (int i = 0; i < length; i++) {
            // 随机验证码
            String rand = RANDOM_CODE[random.nextInt(RANDOM_CODE.length)];

            sRand.append(rand);
            // 注意颜色和划线部分重合即可
            graphics.setColor(
                    new Color(20 + random.nextInt(110),
                            20 + random.nextInt(110),
                            20 + random.nextInt(110)));
            int degree = random.nextInt() % 30;

            // 以弧度为参数的 旋转角度
            graphics.rotate(degree * Math.PI / 180,20 * i + 10, 25 );

            graphics.drawString(rand, 20 * i + 10, 25);

            // 旋转回去
            graphics.rotate(-degree * Math.PI / 180,20 * i + 10, 25);
        }
        // 处理并释放资源
        graphics.dispose();

        return new ImageCode(sRand.toString(),image,expireIn, TimeUnit.SECONDS);
    }

    private Color getColor(int floor, int ceiling, Random random) {
        if (floor > 255) {
            floor = 255;
        }
        if (ceiling > 255) {
            ceiling = 255;
        }
        return new Color(
                floor + random.nextInt(ceiling - floor),
                floor + random.nextInt(ceiling - floor),
                floor + random.nextInt(ceiling - floor)
        );
    }

    /**
     * 打印验证码我们的 码表，使用 ascii 码表生成字母
     */
    public static String printImageCode() {
        List<Integer> list1 = IntStream.range(65, 65 + 26).boxed().map(t -> new Integer(t.toString())).collect(toList());
        List<Integer> list2 = IntStream.range(65 + 32 , 65 + 32 + 26).boxed().map(t -> new Integer(t.toString())).collect(toList());
        list1.addAll(list2);
        List<Character> collect = list1.stream().map(num -> new Character((char) num.intValue())).collect(toList());
        collect.addAll(Arrays.asList('0','1','2','3','4','5','6','7','8','9'));

        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (Character character : collect) {
            builder.append("\"");
            builder.append(String.valueOf(character));
            builder.append("\"");
            builder.append(",");
        }
        builder.delete(builder.length() - 1, builder.length());
        builder.append("]");
        System.out.println(builder.toString());
        return builder.toString();
    }
}
