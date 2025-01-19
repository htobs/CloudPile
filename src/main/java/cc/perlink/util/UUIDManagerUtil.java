package cc.perlink.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class UUIDManagerUtil {

    /**
     * 生成用户的AccessKey和SecretKey
     * AccessKey 通过用户邮箱加当前时间戳生成的uuid
     * secretKey 通过MD5加密AccessKey生成的
     * @param userEmail 用户邮箱
     * @return 包含AccessKey和SecretKey的Map
     */
    public static Map<String, String> generateUserKeys(String userEmail) {
        Date date = new Date();
        long time = date.getTime();
        String key = userEmail + time;
        String accessKey = UUID.nameUUIDFromBytes(key.getBytes()).toString().replace("-","");
        String secretKey = Md5Util.getMD5String(accessKey);
        return Map.of("accessKey", accessKey, "secretKey", secretKey);
    }

    /**
     * 检查accessKey和secretKey是否有效
     * 通过MD5加密accessKey判断是否和secretKey相同
     * @param accessKey 访问密钥
     * @param secretKey 私密密钥
     * @return true或false
     */
    public static Boolean checkUserKeys(String accessKey, String secretKey) {
        return Md5Util.getMD5String(accessKey).equals(secretKey);
    }




    /**
     * 生成存储桶的UUID，基于当前时间
     * @return 存储桶的UUID
     */
    public static String generateBucketUUID() {
        Date date = new Date();
        long time = date.getTime();
        return UUID.nameUUIDFromBytes(String.valueOf(time).getBytes()).toString().replace("-","");
    }

    /**
     * 生成文件的UUID，基于存储桶UUID + 当前时间
     * 使用加密算法
     * @param bucketUuid 存储桶的UUID
     * @param fileName 文件名
     * @return 文件的UUID
     */
    public static String generateFileUUID(String bucketUuid,String fileName) {
        Date date = new Date();
        long time = date.getTime();
        String key = bucketUuid + fileName + time;
        return UUID.nameUUIDFromBytes(key.getBytes()).toString().replace("-","");
    }

    /**
     * 生成Key的UUID，基于所创建的容量 + 当前时间
     * 使用加密算法
     * @param capacity 容量
     * @return Key的UUID
     */
    public static String generateKeyUUID(BigDecimal capacity) {
        Date date = new Date();
        long time = date.getTime();
        String key = capacity.toString() + time;
        return UUID.nameUUIDFromBytes(key.getBytes()).toString().replace("-","");
    }

    /**
     * 生成验证码的UUID，基于验证码 + 当前时间
     * @param code 验证码
     * @return
     */
    public static String generateCodeUUID(String code){
        Date date = new Date();
        long time = date.getTime();
        String key = code + time;
        return UUID.nameUUIDFromBytes(key.getBytes()).toString().replace("-","");
    }
}