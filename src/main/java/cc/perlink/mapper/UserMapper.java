package cc.perlink.mapper;

import cc.perlink.pojo.po.mysql.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/12
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
