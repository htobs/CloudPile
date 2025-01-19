package cc.perlink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @Description: Spring Boot启动入口
 * @Author: htobs
 * @Date: 2024/11/3
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        final Logger log = LoggerFactory.getLogger(Application.class);
        SpringApplication.run(Application.class, args);
        log.info("CloudPile服务已启动");
    }
}