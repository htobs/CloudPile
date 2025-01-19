package cc.perlink.util;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class GenerateCodeUtil {

    public static final String VERIFY_CODES = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static Random random = new Random();

    /**
     * 使用系统默认字符源生成验证码
     *
     * @param verifySize 验证码长度
     * @return 生成的验证码字符串
     */
    public static String generateVerifyCode(int verifySize) {
        return generateVerifyCode(verifySize, VERIFY_CODES);
    }

    /**
     * 使用指定源生成验证码
     *
     * @param verifySize 验证码长度
     * @param sources    验证码字符源
     * @return 生成的验证码字符串
     */
    public static String generateVerifyCode(int verifySize, String sources) {
        if (sources == null || sources.length() == 0) {
            sources = VERIFY_CODES;
        }
        int codesLen = sources.length();
        StringBuilder verifyCode = new StringBuilder(verifySize);
        for (int i = 0; i < verifySize; i++) {
            verifyCode.append(sources.charAt(random.nextInt(codesLen)));
        }
        return verifyCode.toString();
    }

    /**
     * 生成随机验证码图片，并返回包含验证码和Base64编码图片的Map对象
     *
     * @param w          验证码图片宽度
     * @param h          验证码图片高度
     * @param verifySize 验证码长度
     * @return 包含验证码和Base64编码图片的Map对象
     * @throws IOException 文件操作异常
     */
    public static Map<String, String> outputVerifyImageBase64(int w, int h, int verifySize) throws IOException {
        String verifyCode = generateVerifyCode(verifySize);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        outputImage(w, h, baos, verifyCode);
        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("code", verifyCode);
        resultMap.put("base64", "data:image/png;base64," + base64Image);
        return resultMap;
    }

    /**
     * 输出指定验证码图片流
     *
     * @param w    验证码图片宽度
     * @param h    验证码图片高度
     * @param baos 输出流，用于存储图片字节数据
     * @param code 验证码字符串
     * @throws IOException 文件操作异常
     */
    public static void outputImage(int w, int h, ByteArrayOutputStream baos, String code) throws IOException {
        int verifySize = code.length();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color[] colors = new Color[5];
        Color[] colorSpaces = new Color[]{Color.WHITE, Color.CYAN, Color.GRAY, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.YELLOW};
        for (int i = 0; i < colors.length; i++) {
            colors[i] = colorSpaces[random.nextInt(colorSpaces.length)];
        }

        g2.setColor(Color.GRAY);
        g2.fillRect(0, 0, w, h);

        Color c = getRandColor(200, 250);
        g2.setColor(c);
        g2.fillRect(0, 2, w, h - 4);

        g2.setColor(getRandColor(160, 200));
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(w - 1);
            int y = random.nextInt(h - 1);
            int xl = random.nextInt(6) + 1;
            int yl = random.nextInt(12) + 1;
            g2.drawLine(x, y, x + xl + 40, y + yl + 20);
        }

        float yawpRate = 0.05f;
        int area = (int) (yawpRate * w * h);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(w);
            int y = random.nextInt(h);
            int rgb = getRandomIntColor();
            image.setRGB(x, y, rgb);
        }

        shear(g2, w, h, c);
        g2.setColor(getRandColor(100, 160));
        int fontSize = h - 4;
        Font font = new Font("Algerian", Font.ITALIC, fontSize);
        g2.setFont(font);
        char[] chars = code.toCharArray();
        for (int i = 0; i < verifySize; i++) {
            AffineTransform affine = new AffineTransform();
            affine.setToRotation(Math.PI / 4 * random.nextDouble() * (random.nextBoolean() ? 1 : -1), (w / verifySize) * i + fontSize / 2, h / 2);
            g2.setTransform(affine);
            g2.drawChars(chars, i, 1, ((w - 10) / verifySize) * i + 5, h / 2 + fontSize / 2 - 10);
        }

        g2.dispose();
        ImageIO.write(image, "png", baos);
    }

    private static Color getRandColor(int fc, int bc) {
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    private static int getRandomIntColor() {
        int[] rgb = getRandomRgb();
        int color = 0;
        for (int c : rgb) {
            color = color << 8 | c;
        }
        return color;
    }

    private static int[] getRandomRgb() {
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = random.nextInt(255);
        }
        return rgb;
    }

    private static void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }

    private static void shearX(Graphics g, int w1, int h1, Color color) {
        int period = random.nextInt(2) + 1;
        for (int i = 0; i < h1; i++) {
            double d = (period >> 1) * Math.sin((double) i / period);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            if (d > 0) {
                g.setColor(color);
                g.drawLine(0, i, (int) d, i);
                g.drawLine((int) d + w1, i, w1, i);
            }
        }
    }

    private static void shearY(Graphics g, int w1, int h1, Color color) {
        int period = random.nextInt(40) + 10;
        for (int i = 0; i < w1; i++) {
            double d = (period >> 1) * Math.sin((double) i / period);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (d > 0) {
                g.setColor(color);
                g.drawLine(i, 0, i, (int) d);
                g.drawLine(i, (int) d + h1, i, h1);
            }
        }
    }

    @Test
    public void test() throws IOException {
        Map<String, String> result = GenerateCodeUtil.outputVerifyImageBase64(400, 200, 5);
        System.out.println("验证码: " + result.get("code"));
        System.out.println("Base64编码: " + result.get("base64"));
    }
}