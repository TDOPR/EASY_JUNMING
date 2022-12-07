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

-- -------------------------------
-- Table structure for address_pool
-- -------------------------------
DROP TABLE IF EXISTS `address_pool`;
CREATE TABLE `address_pool`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `address` varchar(255)  NOT NULL DEFAULT ''  COMMENT '区块链交易地址',
  `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '区块链地址池' ROW_FORMAT = Dynamic;

-- -------------------------------
-- Table structure for app_user_recharge
-- -------------------------------
DROP TABLE IF EXISTS `app_user_recharge`;
CREATE TABLE `app_user_recharge`  (
  `id` bigint UNSIGNED NOT NULL,
  `userId` int(0) UNSIGNED NOT NULL COMMENT '关联的用户ID',
  `txid`varchar(64) NOT NULL DEFAULT '' COMMENT '交易id',
  `coinUnit` tinyint UNSIGNED NOT NULL DEFAULT 0  COMMENT '货币类型 1=法币 2=usdt',
  `amount` decimal(24, 8) NOT NULL DEFAULT 0.00000000 COMMENT '充值金额',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '充值状态 1=成功 5-打币中;6;-待区块确认;7-区块打币失败',
  `address`varchar(256) NOT NULL DEFAULT '' COMMENT '区块链充值地址',
   `exchangeRate` decimal(24, 8) NOT NULL DEFAULT 0.00000000 COMMENT '多少金额等于1$',
   `usdAmount` decimal(24, 8) NOT NULL DEFAULT 0.00000000 COMMENT '充值金额等值的美元',
  `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
   PRIMARY KEY (`id`) USING BTREE,
   CONSTRAINT `FK_app_user_recharge_userId` FOREIGN KEY (`userId`) REFERENCES `app_users` (`id`)
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '充值记录表' ROW_FORMAT = Dynamic;


-- -------------------------------
-- Table structure for app_user_withdraw
-- -------------------------------
DROP TABLE IF EXISTS `app_user_withdraw`;
CREATE TABLE `app_user_withdraw`  (
  `id` bigint UNSIGNED NOT NULL,
  `userId` int(0) UNSIGNED NOT NULL COMMENT '关联的用户ID',
  `txid`varchar(64) NOT NULL DEFAULT '' COMMENT '交易id',
  `coinUnit` tinyint UNSIGNED NOT NULL DEFAULT 0  COMMENT '货币类型 1=法币 2=usdt',
  `amount` decimal(24, 8) NOT NULL DEFAULT 0.00000000 COMMENT '提现金额',
  `fee` decimal(24, 8) NOT NULL DEFAULT 0.00000000 COMMENT '手续费',
  `actualAmount` decimal(24, 8) NOT NULL DEFAULT 0.00000000 COMMENT '实际提现金额',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '提现状态0=未审核  1=成功 5-打币中;6;-待区块确认;7-区块打币失败',
  `address`varchar(256) NOT NULL DEFAULT '' COMMENT '区块链提现地址',
  `chainFee` decimal(24, 8) NOT NULL DEFAULT 0.00000000 COMMENT '链上手续费花费',
  `auditTime`  datetime(0) NULL DEFAULT NULL  COMMENT '审批时间',
  `auditStatus`  tinyint   NOT NULL DEFAULT -1  COMMENT '-1=不需要审核小额提现  0待审核 2=审核通过 3=驳回',
  `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
   PRIMARY KEY (`id`) USING BTREE,
   CONSTRAINT `FK_app_user_withdraw_userId` FOREIGN KEY (`userId`) REFERENCES `app_users` (`id`)
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '提现记录表' ROW_FORMAT = Dynamic;


-- -------------------------------
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
  `platformDesc` varchar(255)  NOT NULL DEFAULT '' COMMENT '平台描述',
  `downloadAddress` varchar(255)  NOT NULL DEFAULT '' COMMENT 'app下载地址',
  `use` tinyint  NOT NULL DEFAULT 0 COMMENT '最新发布版本 1=最新版本 0=旧版本'
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
  `strategyType` varchar(255)  NOT NULL DEFAULT '' COMMENT '策略类型',
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