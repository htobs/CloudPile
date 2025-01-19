package cc.perlink.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Component
public class FileUploadConfig {
    @Value("${file.upload-path}")
    private String uploadPath;

}