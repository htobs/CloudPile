package cc.perlink.service.impl;

import cc.perlink.enums.exception.CodeExceptionEnum;
import cc.perlink.exception.ExceptionMissing;
import cc.perlink.pojo.po.redis.CodeRedisPo;
import cc.perlink.pojo.vo.Result;
import cc.perlink.service.CodeService;
import cc.perlink.util.GenerateCodeUtil;
import cc.perlink.util.UUIDManagerUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/18
 */
@Component
public class CodeServiceImpl implements CodeService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 生成验证码，并将验证码插入Redis，设置有过期时间
     *
     * @return 生成成功消息
     */
    @Override
    public Result<Object> createCode() throws IOException {
        // 生成验证码
        Map<String, String> codeMap = GenerateCodeUtil.outputVerifyImageBase64(600, 300, 4);
        String codeUid = UUIDManagerUtil.generateCodeUUID(codeMap.get("code"));
        codeMap.put("codeUid", codeUid);
        // 缓存redis
        CodeRedisPo codeRedisPo = new CodeRedisPo();
        codeRedisPo.setCode(codeMap.get("code"));
        codeRedisPo.setBase64(codeMap.get("base64"));
        codeRedisPo.setCodeUid(codeUid);
        codeRedisPo.setCreatedAt(String.valueOf(LocalDateTime.now()));
        stringRedisTemplate.opsForValue().set(
                codeRedisPo.getRedisKey(),
                codeRedisPo.getRedisValue(),
                codeRedisPo.getExp(),
                codeRedisPo.getTimeUnit()
        );
        return Result.success(codeMap);
    }


    /**
     * 校验验证码，如果成功则删除Redis当中的验证码，失败则否
     *
     * @param code    验证码
     * @param codeUid 验证码uuid
     * @return 校验结果
     */
    @Override
    public Result<Object> checkCode(String code, String codeUid) {
        CodeRedisPo codeRedisPo = new CodeRedisPo();
        codeRedisPo.setCode(code);
        codeRedisPo.setCodeUid(codeUid);
        String queryCodeRedisString = stringRedisTemplate.opsForValue().get(codeRedisPo.getRedisKey());
        if (queryCodeRedisString == null) {
            throw new ExceptionMissing(CodeExceptionEnum.CAPTCHA_ERROR.getMessage());
        }
        // 根据验证码uuid查找到了验证码
        CodeRedisPo queryCodeRedisData = CodeRedisPo.fromRedis(queryCodeRedisString);
        if (!code.equals(queryCodeRedisData.getCode())) {
            throw new ExceptionMissing(CodeExceptionEnum.CAPTCHA_ERROR.getMessage());
        }
        stringRedisTemplate.delete(codeRedisPo.getRedisKey());
        return Result.success();
    }
}
