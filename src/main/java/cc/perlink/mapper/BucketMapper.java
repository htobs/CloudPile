package cc.perlink.mapper;

import cc.perlink.pojo.dto.bucket.UpdateBucketRequest;
import cc.perlink.pojo.po.mysql.Bucket;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/15
 */
@Mapper
public interface BucketMapper extends BaseMapper <Bucket> {

    @Select("SELECT\n" +
            "    (SELECT bucket_count FROM users WHERE id = #{userId}) AS max_bucket,\n" +
            "    (SELECT COUNT(id) FROM buckets WHERE user_id = #{userId}) AS bucket_count;")
    Map<String, Object> customBucketCountSql(@Param("userId") Integer userId);
}
