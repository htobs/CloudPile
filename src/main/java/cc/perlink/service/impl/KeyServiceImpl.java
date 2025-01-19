package cc.perlink.service.impl;

import cc.perlink.enums.UserPermission;
import cc.perlink.enums.exception.BucketExceptionEnum;
import cc.perlink.enums.exception.CommonExceptionEnum;
import cc.perlink.enums.exception.KeyExceptionEnum;
import cc.perlink.exception.ExceptionMissing;
import cc.perlink.mapper.BucketMapper;
import cc.perlink.mapper.KeyMapper;
import cc.perlink.pojo.po.mysql.Bucket;
import cc.perlink.pojo.po.mysql.Key;
import cc.perlink.pojo.vo.Result;
import cc.perlink.service.KeyService;
import cc.perlink.util.ThreadLocalUtil;
import cc.perlink.util.UUIDManagerUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/13
 */
@Component
public class KeyServiceImpl implements KeyService {
    @Resource
    private KeyMapper keyMapper;
    @Autowired
    private BucketMapper bucketMapper;

    /**
     * 创建key
     * @param capacity key容量 单位GB
     * @return 创建成功
     */
    @Override
    public Result<Object> createKey(Integer capacity) {
        // 检查当前用户权限
        Map<String, Object> claims = ThreadLocalUtil.get();
        String permission = claims.get("permission").toString();
        if (!permission.equals(UserPermission.ADMIN.getPermission())){
            throw new ExceptionMissing(CommonExceptionEnum.NO_PERMISSION.getMessage());
        }
        if (capacity <= 0){
            throw new ExceptionMissing(KeyExceptionEnum.CAPACITY_CANNOT_BE_ZERO_OR_LESS.getMessage());
        }
        // 将传入的容量转换为KB
        BigDecimal mb = new BigDecimal("1024").multiply(new BigDecimal("1024")); // 生成的1024Mb，也就是1Gb
        BigDecimal gb = new BigDecimal(capacity).multiply(mb); // 20Gb
        Key key = new Key();
        key.setCapacity(gb);
        key.setKeyCode(UUIDManagerUtil.generateKeyUUID(gb));
        key.setCreatedAt(LocalDateTime.now());
        keyMapper.insert(key);
        return Result.success();
    }



    /**
     * 使用key
     * @param key key
     * @param bucketUid 存储桶uuid
     * @return 使用成功
     */
    @Override
    public Result<Object> useKey(String key,String bucketUid) {
        // 检查存储桶是否存在
        QueryWrapper<Bucket> queryBucketWrapper = new QueryWrapper<Bucket>().eq("bucket_uid", bucketUid);
        Bucket queryBucketData = bucketMapper.selectOne(queryBucketWrapper);
        if (queryBucketData == null){
            throw new ExceptionMissing(BucketExceptionEnum.BUCKET_NOT_EXIST.getMessage());
        }
        // 查询key是否已使用
        QueryWrapper<Key> queryKeyWrapper = new QueryWrapper<Key>().eq("key_code", key);
        Key queryKeyData = keyMapper.selectOne(queryKeyWrapper);
        if (queryKeyData.getUsed().equals(true)){
            throw new ExceptionMissing(KeyExceptionEnum.KEY_ALREADY_IN_USE.getMessage());
        }
        // 获取用户id
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("id");
        Key tmpKey = new Key();
        tmpKey.setKeyCode(key);
        tmpKey.setUserId(userId);
        tmpKey.setBucketUid(bucketUid);
        tmpKey.setUsed(true);
        tmpKey.setUpdatedAt(LocalDateTime.now());
        UpdateWrapper<Key> update = new UpdateWrapper<Key>();
        update.eq("key_code", key);
        int updateCount = keyMapper.update(tmpKey, update);
        if (updateCount < 0) {  // 如果没有更新，说明没有根据key不存在，抛出无效key异常
            throw new ExceptionMissing(KeyExceptionEnum.INVALID_KEY.getMessage());
        }
        return Result.success();
    }

    /**
     * 删除key
     * @param key key
     * @return
     */
    @Override
    public Result<Object> deleteKey(String key) {
        QueryWrapper<Key> query = new QueryWrapper<Key>().eq("key_code", key);
        int delete = keyMapper.delete(query);
        if (delete < 0) {
            throw new ExceptionMissing(KeyExceptionEnum.INVALID_KEY.getMessage());
        }
        return Result.success();
    }

    /**
     * 查询key是否使用
     * @param key key
     * @return
     */
    @Override
    public Result<Object> checkKey(String key) {
        QueryWrapper<Key> query = new QueryWrapper<Key>().eq("key_code", key);
        Key queryData = keyMapper.selectOne(query);
        if (queryData == null) {
            throw new ExceptionMissing(KeyExceptionEnum.INVALID_KEY.getMessage());
        }
        if (queryData.getBucketUid() == null) {
            return Result.success("Key未使用",true);
        }
        return Result.success("Key已使用",false);
    }
}
