package cc.perlink.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.Map;


public class JwtUtil {

    private static final String KEY = "CloudPile";
    private static final Integer expiration = 1000 * 60 * 60 * 24; // 数据有效期24小时

    // 接收业务数据,生成token并返回
    public static String genToken(Map<String, Object> claims) {
        try {
            return JWT.create()
                    .withClaim("claims", claims)
                    .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                    .sign(Algorithm.HMAC256(KEY));
        } catch (Exception e) {
            throw new RuntimeException("生成token失败", e);
        }
    }

    // 接收token,验证token,并返回业务数据
    public static Map<String, Object> parseToken(String token) {
        try {
            Map<String, Object> claims = JWT.require(Algorithm.HMAC256(KEY))
                    .build()
                    .verify(token)
                    .getClaim("claims")
                    .asMap();
            return claims;
        } catch (Exception e) {
            throw new RuntimeException("解析token失败", e);
        }
    }

}