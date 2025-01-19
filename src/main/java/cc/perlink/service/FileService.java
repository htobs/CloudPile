package cc.perlink.service;

import cc.perlink.pojo.vo.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/13
 */
public interface FileService {
    // 上传文件（登录状态下）
    Result<Object> upload(MultipartFile file, String bucketUid) throws IOException;

    // 删除文件（登录状态下）
    Result<Object> delete(String bucketUid, String fileUid);

    // 查看文件
    void preview(String bucketUid, String fileUid, HttpServletResponse response) throws IOException;

    // 查看当前用户的所有文件
    Result<Object> findUserAllFile(String bucketUid, int page, int size) throws IOException;

}
