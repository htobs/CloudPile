package cc.perlink.controller;

import cc.perlink.pojo.vo.Result;
import cc.perlink.service.FileService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Description: 文件接口控制类
 * @Author: htobs
 * @Date: 2024/11/17
 */
@Validated
@RestController
@RequestMapping("/api/file")
public class FileController {
    @Resource
    private FileService fileService;

    // 上传文件（登录状态下）
    @PostMapping("upload")
    public Result<Object> upload(@RequestParam("file") MultipartFile file,
                                 @RequestParam
                                 @NotNull(message = "存储桶uuid不能为空")
                                 @NotEmpty(message = "存储桶uuid不能为空") String bucketUid) throws IOException {
        return fileService.upload(file, bucketUid);
    }

    // 删除文件（登录状态下）
    @DeleteMapping("delete")
    public Result<Object> delete(@RequestParam
                                 @NotNull(message = "存储桶uuid不能为空")
                                 @NotEmpty(message = "存储桶uuid不能为空") String bucketUid,
                                 @RequestParam
                                 @NotNull(message = "文件名不能为空")
                                 @NotEmpty(message = "文件名不能为空") String fileUid) throws IOException {
        return fileService.delete(bucketUid, fileUid);
    }


    // 查看文件
    @GetMapping("preview")
    public void preview(@Validated @RequestParam String bucketUid,
                        @RequestParam
                        @NotNull(message = "文件uuid不能为空")
                        @NotEmpty(message = "文件uuid不能为空") String fileUid,
                        HttpServletResponse response) throws IOException {
        fileService.preview(bucketUid, fileUid, response);
    }


    // 获取某个桶的所有文件
    @SneakyThrows
    @GetMapping("/findBucketFiles")
    public Result<Object> findBucketFiles(@RequestParam
                                          @NotNull(message = "存储桶uuid不能为空")
                                          @NotEmpty(message = "存储桶uuid不能为空") String bucketUid,
                                          @RequestParam
                                          @NotNull(message = "页码不能为空") Integer page,
                                          @RequestParam
                                          @NotNull(message = "每页大小不能为空") Integer size
    ) {
        return fileService.findUserAllFile(bucketUid, page, size);

    }


}
