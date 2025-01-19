-- 创建数据库
CREATE DATABASE CloudPile;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

SET FOREIGN_KEY_CHECKS = 1;

USE CloudPile;

-- 创建users表
CREATE TABLE `users`
(
    `id`           int                                                           NOT NULL AUTO_INCREMENT COMMENT '用户的唯一标识，自增主键',
    `email`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '用户邮箱，唯一',
    `password`     varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '用户密码',
    `nick_name`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '用户昵称',
    `amount`       decimal(10, 2)                                                NOT NULL DEFAULT 0.00 COMMENT '用户金额，单位元',
    `country`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '用户所在国家',
    `province`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '用户所在省',
    `language`     varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '用户使用语言',
    `avatar_url`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '用户头像',
    `created_at`   timestamp                                                     NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '用户注册时间',
    `updated_at`   timestamp                                                     NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '用户最后一次更新时间',
    `permission`   ENUM ('admin', 'user', 'banned')                              NOT NULL DEFAULT 'User' COMMENT '用户权限，admin 表示管理员，user 表示普通用户，banned 表示被封禁的用户',
    `bucket_count` int                                                           NOT NULL DEFAULT 1 COMMENT '用户创建的桶数，默认为1',
    `access_key`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          DEFAULT NULL COMMENT '用户的AccessKey',
    `secret_key`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          DEFAULT NULL COMMENT '用户的SecretKey',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `email_UNIQUE` (`email` ASC) USING BTREE,
    UNIQUE INDEX `access_key_UNIQUE` (`access_key` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表'
  ROW_FORMAT = Dynamic;

-- 创建buckets表
CREATE TABLE `buckets`
(
    `id`           int                                                           NOT NULL AUTO_INCREMENT COMMENT 'Bucket的唯一标识，自增主键',
    `name`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'Bucket的名字',
    `status`       tinyint(1)                                                    NOT NULL DEFAULT 1 COMMENT 'Bucket的状态，1表示启用，0表示禁用',
    `created_at`   timestamp                                                     NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'Bucket的创建时间',
    `updated_at`   timestamp                                                     NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Bucket的最后一次更新时间',
    `user_id`      int                                                           NOT NULL COMMENT '与用户表的用户ID关联的外键',
    `capacity`     decimal(20, 2)                                                NOT NULL DEFAULT 20971520.00 COMMENT 'Bucket的容量，默认20GB，单位KB',
    `use_capacity` decimal(20, 2)                                                NOT NULL DEFAULT 0.00 COMMENT 'Bucket已使用的容量，0KB，单位KB',
    `bucket_uid`   varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT '' COMMENT 'Bucket的UUID',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uuid_UNIQUE` (`bucket_uid` ASC) USING BTREE,
    INDEX `idx_user_id` (`user_id` ASC) USING BTREE,
    CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Bucket表'
  ROW_FORMAT = Dynamic;

-- 创建files表
CREATE TABLE `files`
(
    `id`          int                                                           NOT NULL AUTO_INCREMENT COMMENT '文件的唯一标识，自增主键',
    `user_id`     int                                                           NOT NULL COMMENT '与用户表的用户ID关联的外键',
    `bucket_id`   int                                                           NOT NULL COMMENT '与bucket表的bucket ID关联的外键',
    `name`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '文件名',
    `local_path`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '本地文件保存路径',
    `file_type`   enum ('file', 'audio', 'video', 'image')                      NOT NULL DEFAULT 'file' COMMENT '文件类型',
    `created_at`  timestamp                                                     NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '文件的创建时间',
    `updated_at`  timestamp                                                     NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '文件的最后一次更新时间',
    `status`      tinyint(1)                                                    NOT NULL DEFAULT 1 COMMENT '文件状态，1表示启用，0表示禁用',
    `file_uid`    varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT '' COMMENT '文件的UUID',
    `public_url`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '文件的公共访问URL',
    `expire_time` timestamp                                                     NULL     DEFAULT NULL COMMENT '文件的公共访问URL过期时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uuid_UNIQUE` (`file_uid` ASC) USING BTREE,
    INDEX `user_id` (`user_id` ASC) USING BTREE,
    INDEX `bucket_id` (`bucket_id` ASC) USING BTREE,
    CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT `bucket_id` FOREIGN KEY (`bucket_id`) REFERENCES `buckets` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件表'
  ROW_FORMAT = Dynamic;

-- 创建keys表
CREATE TABLE `keys`
(
    `id`         int                                                           NOT NULL AUTO_INCREMENT COMMENT 'Key的唯一标识，自增主键',
    `key_code`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'Key的代码',
    `capacity`   decimal(20, 2)                                                NOT NULL DEFAULT 0 COMMENT 'Key绑定的容量',
    `bucket_uid` varchar(255)                                                  NULL     DEFAULT NULL COMMENT '被使用的桶ID',
    `user_id`    int                                                           NULL     DEFAULT NULL COMMENT '被使用的用户ID',
    `created_at` timestamp                                                     NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'Key的生成时间',
    `updated_at` timestamp                                                     NULL     DEFAULT NULL COMMENT 'Key的使用时间',
    `used`       boolean                                                       NOT NULL DEFAULT false COMMENT 'Key是否已被使用',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `key_code_UNIQUE` (`key_code` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Key表'
  ROW_FORMAT = Dynamic;

-- 用户消费表
CREATE TABLE `transactions`
(
    `id`               int                                                           NOT NULL AUTO_INCREMENT COMMENT '消费记录的唯一标识，自增主键',
    `user_id`          int                                                           NOT NULL COMMENT '与用户表的用户ID关联的外键',
    `amount`           decimal(10, 2)                                                NOT NULL COMMENT '消费金额，单位是元',
    `transaction_time` timestamp                                                     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消费时间',
    `description`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '消费描述，比如购买的服务或产品',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_user_id` (`user_id` ASC) USING BTREE,
    CONSTRAINT `fk_user_id_transaction` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '消费记录表'
  ROW_FORMAT = Dynamic;

-- 用户充值表
CREATE TABLE `recharge_records`
(
    `id`             int                                                           NOT NULL AUTO_INCREMENT COMMENT '充值记录的唯一标识，自增主键',
    `user_id`        int                                                           NOT NULL COMMENT '与用户表的用户ID关联的外键',
    `amount`         decimal(10, 2)                                                NOT NULL COMMENT '充值金额，单位是元',
    `recharge_time`  timestamp                                                     NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '充值时间',
    `payment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '支付方式，如支付宝、微信、信用卡等',
    `status`         tinyint(1)                                                    NOT NULL DEFAULT 1 COMMENT '充值状态，1表示成功，0表示失败',
    `description`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '充值描述，比如充值活动或优惠信息',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_user_id` (`user_id` ASC) USING BTREE,
    CONSTRAINT `fk_user_id_recharge` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户充值记录表'
  ROW_FORMAT = Dynamic;

-- 插入用户数据
INSERT INTO `users` (`email`, `password`, `nick_name`, `permission`, `bucket_count`, `created_at`, `updated_at`)
VALUES ('admin@cloudpile.com', '123456', '管理员', 'admin', '10000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('user@cloudpile.com', '123456', '普通用户', 'user', '1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);