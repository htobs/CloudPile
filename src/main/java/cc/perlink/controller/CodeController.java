package cc.perlink.controller;

import cc.perlink.pojo.vo.Result;
import cc.perlink.service.CodeService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @Description: 验证码接口控制类
 * @Author: htobs
 * @Date: 2024/11/18
 */
@Validated
@RestController
@RequestMapping("/api/code")
public class CodeController {
    @Resource
    private CodeService codeService;

    // 生成验证码
    @GetMapping("create")
    public Result<Object> createCode() throws IOException {
        return codeService.createCode();
    }

    // 校验验证码
    @PostMapping("check")
    public Result<Object> checkCode(@RequestParam
                                    @NotNull(message = "验证码未填写")
                                    @NotEmpty(message = "验证码未填写") String code,
                                    @RequestParam
                                    @NotNull(message = "验证码uuid不能为空")
                                    @NotEmpty(message = "验证码uuid不能为空") String codeUid) throws IOException {
        return codeService.checkCode(code, codeUid);
    }
}
