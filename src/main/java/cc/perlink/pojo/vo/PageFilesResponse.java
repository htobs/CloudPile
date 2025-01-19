package cc.perlink.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PageFilesResponse {
    private String fileType;
    private BigDecimal fileSize;
    private String bucketUid;
    private String fileUid;
    private Boolean status;
    private String createdAt;
}
