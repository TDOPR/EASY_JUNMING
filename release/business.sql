-- --------------------------------
-- Table structure for appUsers 用户表
-- -------------------------------
DROP TABLE IF EXISTS `app_users`;
CREATE TABLE `app_users`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `email` varchar(20) NOT NULL DEFAULT '' COMMENT '邮箱账号',
  `password` varchar(56) NOT NULL DEFAULT '' COMMENT '密码',
  `salt` varchar(32) NOT NULL DEFAULT '' COMMENT '密码加密的盐',
  `inviteCode` varchar(20) NOT NULL DEFAULT '' COMMENT '邀请码',
  `headImage` varchar(255) NOT NULL DEFAULT '' COMMENT '头像',
  `nickName` varchar(255)  NOT NULL DEFAULT '' COMMENT '用户昵称',
  `autograph` varchar(255)  NOT NULL DEFAULT '' COMMENT '个性签名',
  `enabled` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '用户状态： 1-正常 0=禁用',
  `inviteId` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '邀请人Id',
  `loginCount` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '登录次数',
  `level` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '代理商等级',
  `minTeamAmount` decimal(24, 8) UNSIGNED NOT NULL DEFAULT 0.00000000 COMMENT '小团队业绩',
  `teamTotalAmount` decimal(24, 8) UNSIGNED NOT NULL DEFAULT 0.00000000 COMMENT '团队总业绩',
  `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  UNIQUE INDEX `UK_app_users_email`(`email`) USING BTREE,
  UNIQUE INDEX `UK_app_users_inviteCode`(`inviteCode`) USING BTREE,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'app用户表' ROW_FORMAT = Dynamic;

-- ---------------------------
-- Table structure for wallets
-- ---------------------------
DROP TABLE IF EXISTS `wallets`;
CREATE TABLE `wallets`  (
  `id` bigint UNSIGNED NOT NULL,
  `userId` int(0) UNSIGNED  NOT NULL,
  `robotAmount` decimal(6, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '机器人购买金额',
  `robotLevel` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '机器人等级',
  `blockAddress` varchar(255)  NOT NULL DEFAULT '' COMMENT '区块链地址',
  `legalCurrencyAccount` varchar(255)  NOT NULL DEFAULT '' COMMENT '提现用的法币账号',
  `walletAmount` decimal(24, 8) NOT NULL DEFAULT 0.00000000 COMMENT '钱包余额',
  `principalAmount` decimal(24, 8) NOT NULL DEFAULT 0.00000000 COMMENT '托管本金',
  `frozenAmount` decimal(24, 8) UNSIGNED NOT NULL DEFAULT 0.00000000 COMMENT '冻结金额',
  `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `wallets_userId`(`userId`) USING BTREE,
  INDEX `idx_amount`(`principalAmount`) USING BTREE,
  CONSTRAINT `FK_wallet_userId` FOREIGN KEY (`userId`) REFERENCES `app_users` (`id`)
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户钱包表' ROW_FORMAT = Dynamic;

-- -------------------------------
-- Table structure for wallet_logs
-- -------------------------------
DROP TABLE IF EXISTS `wallet_logs`;
CREATE TABLE `wallet_logs`  (
  `id` bigint UNSIGNED NOT NULL,
  `userId` int(0) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '用户ID',
  `amount` decimal(12, 4) UNSIGNED NOT NULL COMMENT '本次变动金额',
  `action` tinyint UNSIGNED NOT NULL COMMENT '收支动作:1-收入 2-支出',
  `type` tinyint UNSIGNED NOT NULL COMMENT '流水类型 1-代数奖 2-推广奖 3-团队奖 4-特别奖 5-充值 6-提现  7=量化收益 8=委托量化 9=提现到钱包 10=首次购买机器人 11=升级机器人',
  `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`) USING BTREE,
   INDEX `type`(`type`) USING BTREE,
   CONSTRAINT `FK_wallet_logs_userId` FOREIGN KEY (`userId`) REFERENCES `app_users` (`id`)
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '钱包日志表' ROW_FORMAT = Dynamic;

-- -------------------------------
-- Table structure for profit_logs
-- -------------------------------
DROP TABLE IF EXISTS `profit_logs`;
CREATE TABLE `profit_logs`  (
  `id` bigint UNSIGNED NOT NULL,
  `userId` int(0) UNSIGNED NOT NULL COMMENT '关联的用户ID',
  `principal` decimal(24, 8) NOT NULL DEFAULT 0.00000000 COMMENT '托管本金',
  `generatedAmount` decimal(24, 8) NOT NULL DEFAULT 0.00000000 COMMENT '托管本金',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 0  COMMENT '结算状态 1=已结算 0=未结算',
  `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
   PRIMARY KEY (`id`) USING BTREE,
   INDEX `profit_logs_userId_index`(`userId`) USING BTREE,
   CONSTRAINT `FK_profit_logs_userId` FOREIGN KEY (`userId`) REFERENCES `app_users` (`id`)
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收益日结记录表' ROW_FORMAT = Dynamic;


-- ------------------------------
-- Table structure for tree_paths
-- -------------------------------
DROP TABLE IF EXISTS `tree_paths`;
CREATE TABLE `tree_paths`  (
  `ancestor` int(0) UNSIGNED NOT NULL DEFAULT  0  COMMENT '父Id',
  `descendant` int(0) UNSIGNED NOT NULL DEFAULT  0 COMMENT '子Id',
  `level` int(0) UNSIGNED NOT NULL DEFAULT  0 COMMENT '子是父的第几代',
  `createTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `largeTeam`  tinyint   NOT NULL DEFAULT 0 COMMENT '只对直邀请客户有效 1=大团队 0=小团队',
  PRIMARY KEY (`ancestor`, `descendant`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户邀请关系表' ROW_FORMAT = Dynamic;

-- -------------------------------
-- Table structure for tree_paths
-- -------------------------------
DROP TABLE IF EXISTS `app_user_rebot_ref`;
CREATE TABLE `app_user_rebot_ref`(
  `userId` int(0) UNSIGNED NOT NULL DEFAULT  0 COMMENT '用户Id',
  `inviteUserId` int(0) UNSIGNED NOT NULL DEFAULT  0 COMMENT '邀请的用户Id',
  `level` int(0) UNSIGNED NOT NULL DEFAULT  0 COMMENT '邀请的用户是用户的第几位购买机器',
  `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`userId`, `inviteId`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户机器人邀请关系' ROW_FORMAT = Dynamic;

-- -------------------------------
-- Table structure for dayrate
-- -------------------------------
DROP TABLE IF EXISTS `dayrate`;
CREATE TABLE `dayrate`  (
  `createDate` datetime(0)  NOT NULL DEFAULT  CURRENT_TIMESTAMP COMMENT '邀请的用户Id',
  `level0` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT '未购买机器人收益率',
  `level1` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT '一级机器人收益率',
  `level2` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT '二级机器人收益率',
  `level3` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT '三级机器人收益率',
  `level4` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT '四级机器人收益率',
  `level5` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT '五级机器人收益率',
  UNIQUE INDEX `UK_dayrate_createDate`(`createDate`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '日收益表' ROW_FORMAT = Dynamic;

-- ----------------------------------
-- Table structure for app_versions
-- ----------------------------------
DROP TABLE IF EXISTS `app_versions`;
CREATE TABLE `app_versions`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `systemName` varchar(255)  NOT NULL DEFAULT '' COMMENT '系统名称 ios 、android',
  `version` varchar(255)  NOT NULL DEFAULT '' COMMENT '版本号',
  `updateDesc` varchar(255)  NOT NULL DEFAULT '' COMMENT '功能更新说明',
  `enUpdateDesc` varchar(255)  NOT NULL DEFAULT '' COMMENT '英语 功能更新说明',
  `ptUpdateDesc` varchar(255)  NOT NULL DEFAULT '' COMMENT '葡萄牙 功能更新说明',
  `esUpdateDesc` varchar(255)  NOT NULL DEFAULT '' COMMENT '西班牙 功能更新说明',
  `downloadAddress` varchar(255)  NOT NULL DEFAULT '' COMMENT 'app下载地址',
  `active` tinyint  NOT NULL DEFAULT 0 COMMENT '最新版本标识 1=是 0=否',
  `forceUpdate` tinyint  NOT NULL DEFAULT 0 COMMENT '强制更新标识 1=是 0=否',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='系统版本表' ROW_FORMAT = Dynamic;


-- ----------------------------------
-- Table structure for  update_user_level_job
-- ----------------------------------
DROP TABLE IF EXISTS `update_user_level_job`;
CREATE TABLE `update_user_level_job`  (
  `userId` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `delayTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务延迟处理时间',
  PRIMARY KEY (`userId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='更新用户的代理商等级' ROW_FORMAT = Dynamic;


-- ----------------------------------
-- Table structure for strategy
-- ---------------------------------
DROP TABLE IF EXISTS `strategy`;
CREATE TABLE `strategy`  (
  `createDate` datetime(0)  NOT NULL DEFAULT  CURRENT_TIMESTAMP COMMENT '创建日期',
  `sortIndex` tinyint   NOT NULL DEFAULT 0  COMMENT '排序下标',
  `strategyType` tinyint  NOT NULL DEFAULT 1 COMMENT '策略类型 1=马丁格尔AI;2=迈凯伦指数;3=期现套利;4=波段追踪;5=频响定投;6=集中频响;7=逆周期跟单;8=低阻抗追踪;9=波段平衡;10=阻抗均衡;',
  `qc` varchar(255)  NOT NULL DEFAULT '' COMMENT '基准货币',
  `bc` varchar(255)  NOT NULL DEFAULT '' COMMENT '计价货币',
  `cro` varchar(255)  NOT NULL DEFAULT '' COMMENT '交叉货币对',
  `dern` varchar(255)  NOT NULL DEFAULT '' COMMENT '派生交易量',
  `ti` varchar(255)  NOT NULL DEFAULT '' COMMENT '量化指标TI',
  `eipM` varchar(255)  NOT NULL DEFAULT '' COMMENT '生态基数EIPM',
  `eipN` varchar(255)  NOT NULL DEFAULT '' COMMENT '生态基数EIPN',
  `eip` varchar(255)  NOT NULL DEFAULT '' COMMENT '生态基数EIP',
   PRIMARY KEY (`createDate`, `sortIndex`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='机器人做单策略表' ROW_FORMAT = Dynamic

-- ----------------------------------
-- Table structure for business_job
-- ---------------------------------
DROP TABLE IF EXISTS `business_job`;
CREATE TABLE `business_job`  (
  `createDate` datetime(0)  NOT NULL DEFAULT  CURRENT_TIMESTAMP COMMENT '创建日期',
  `staticTask` tinyint  NOT NULL DEFAULT 1 COMMENT '发放静态收益任务是否执行 1=已执行 0=未执行',
  `algebraTask` tinyint  NOT NULL DEFAULT 0 COMMENT '发放代数奖任务是否执行 1=已执行 0=未执行',
  `teamTask` tinyint  NOT NULL DEFAULT 0 COMMENT '发放团队奖任务是否执行 1=已执行 0=未执行',
  `specialTask` tinyint  NOT NULL DEFAULT 0 COMMENT '发放分红将任务是否执行 1=已执行 0=未执行',
   UNIQUE INDEX `UK_job_createDate`(`createDate`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='收益发放任务表' ROW_FORMAT = Dynamic


-- ----------------------------
-- Table structure for evm_token_address_pool
-- ----------------------------
DROP TABLE IF EXISTS `evm_token_address_pool`;
CREATE TABLE `evm_token_address_pool`  (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `coin_id` bigint(18) NOT NULL COMMENT '币种ID',
  `address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '地址',
  `keystore` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'keystore',
  `pwd` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '密码',
  `coin_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '地址类型',
  `status` int(10) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `unq_address`(`address`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3036 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户的地址池' ROW_FORMAT = Dynamic;
-- ----------------------------
-- Table structure for evm_token_recharge
-- ----------------------------
DROP TABLE IF EXISTS `evm_token_recharge`;
CREATE TABLE `evm_token_recharge`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` bigint(18) NULL DEFAULT NULL COMMENT '用户UID',
  `coin_id` bigint(18) NOT NULL DEFAULT 0 COMMENT '币种id',
  `coin_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '币种名称',
  `coin_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '币种类型',
  `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '钱包地址',
  `confirm` int(1) NULL DEFAULT NULL COMMENT '充值确认数',
  `status` int(4) NULL DEFAULT 0 COMMENT '状态：0-待入帐；1-充值成功，2到账失败，3到账成功；',
  `txid` varchar(80) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '交易id',
  `num` decimal(20, 8) NULL DEFAULT NULL COMMENT '充值量',
  `fee` decimal(20, 8) NULL DEFAULT NULL COMMENT '手续费',
  `mum` decimal(20, 8) NULL DEFAULT NULL COMMENT '实际到账',
  `block_number` int(10) NULL DEFAULT NULL COMMENT '交易的区块高度',
  `last_update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `type` tinyint(1) NULL DEFAULT 0 COMMENT '充值',
  `remark` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_txid`(`txid`) USING BTREE,
  INDEX `uid_coinId_status`(`uid`, `coin_id`, `status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户充值,当前用户充值成功之后添加数据到这个表,充值一般无手续费.当status为0和confirm=1的时候表示充值成功' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for evm_token_withdraw
-- ----------------------------
DROP TABLE IF EXISTS `evm_token_withdraw`;
CREATE TABLE `evm_token_withdraw`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `uid` bigint(18) UNSIGNED NOT NULL COMMENT '用户id',
  `coin_id` bigint(18) NOT NULL COMMENT '币种id',
  `coin_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '币种名称',
  `coin_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '币种类型',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '钱包地址',
  `txid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '交易id',
  `num` decimal(22, 8) NOT NULL COMMENT '提现量(包含手续费)',
  `fee` decimal(20, 8) NOT NULL COMMENT '手续费',
  `mum` decimal(22, 8) NOT NULL COMMENT '实际提现量',
  `chain_fee` decimal(20, 8) NULL DEFAULT NULL COMMENT '链上手续费花费',
  `block_num` int(11) UNSIGNED NULL DEFAULT 0 COMMENT '区块高度',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `step` tinyint(4) NULL DEFAULT NULL COMMENT '当前审核级数',
  `status` tinyint(1) NOT NULL COMMENT '状态：0-审核中;1-成功;2-拒绝;3-撤销;4-审核通过;5-打币中;6;-待区块确认;7-区块打币失败',
  `audit_time` datetime(0) NULL DEFAULT NULL COMMENT '审核时间',
  `last_update_time` datetime(0) NOT NULL COMMENT '修改时间',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `out_id` bigint(18) NULL DEFAULT NULL,
  `auditStatus` tinyint(255) NULL DEFAULT NULL COMMENT '审核状态: 1=不需要审核的任务  0=待审核 4=审核通过   2=拒绝',
  `monet_log_id` int(11) NOT NULL DEFAULT 0 COMMENT '日志id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid`(`uid`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `coinid`(`coin_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1600407200940695566 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '当用户发起提币的时候,把数据插入到该表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for evm_user_wallet
-- ----------------------------
DROP TABLE IF EXISTS `evm_user_wallet`;
CREATE TABLE `evm_user_wallet`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID[钱包名]',
  `uid` bigint(18) NOT NULL COMMENT '用户ID',
  `coin_id` bigint(20) NOT NULL COMMENT '币种ID',
  `address` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '钱包地址',
  `lower_address` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '小写地址',
  `password` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '钱包密码',
  `keystore` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '秘钥文件',
  `valid` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否可用：E可用，D不可用',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `coin_type` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '网络类型名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1600700293720780802 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户数字货币钱包' ROW_FORMAT = Dynamic;