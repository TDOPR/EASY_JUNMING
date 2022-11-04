-- --------------------------------
-- Table structure for users 用户表
-- -------------------------------
DROP TABLE IF EXISTS `app_users`;
CREATE TABLE `app_users`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `inviteCode` varchar(20) NOT NULL DEFAULT '' COMMENT '邀请码',
  `deleted` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除状态：0-未删除 1-已删除',
  `level` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '代理商等级',
  `parentId` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '邀请人Id',
  `loginCount` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '登录次数',
  `nickName` varchar(255) UNSIGNED NOT NULL DEFAULT '' COMMENT '用户昵称',
  `createTime` timestamp(0) NULL DEFAULT NULL,
  `lastmodifiedTime` timestamp(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'eht 用户表' ROW_FORMAT = Dynamic;


-- ---------------------------
-- Table structure for wallets
-- ---------------------------
DROP TABLE IF EXISTS `wallets`;
CREATE TABLE `wallets`  (
  `id` bigint UNSIGNED NOT NULL,
  `userId` int(0) UNSIGNED  NOT NULL,
  `robotAmount` decimal(12, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '机器人购买金额',
  `robotLevel` int UNSIGNED NOT NULL COMMENT '机器人等级',
  `principalAmount` decimal(24, 8) NOT NULL DEFAULT 0.00000000 COMMENT '托管本金',
  `withdrawAmount` decimal(24, 8) UNSIGNED NOT NULL DEFAULT 0.00000000 COMMENT '累计提现金额',
  `staticRewardAmount` decimal(24, 8) UNSIGNED NOT NULL DEFAULT 0.00000000 COMMENT '静态收益',
  `performanceAmount` decimal(24, 8) UNSIGNED NOT NULL DEFAULT 0.00000000 COMMENT '业绩（充值金额+复投金额+购买机器人）',
  `totalStaticRreward` decimal(24, 8) NOT NULL DEFAULT 0.00000000 COMMENT '总静态收益',
  `totalAmount` decimal(24, 8) UNSIGNED NOT NULL DEFAULT 0.00000000 COMMENT '历史总金额',
  `createTime` timestamp(0) NULL DEFAULT NULL,
  `lastmodifiedTime` timestamp(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `wallets_userId`(`userId`) USING BTREE,
  INDEX `idx_amount`(`principalAmount`) USING BTREE,
  CONSTRAINT `FK_wallet_userId` FOREIGN KEY (`userId`) REFERENCES `users` (`id`)
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户钱包表' ROW_FORMAT = Dynamic;

-- -------------------------------
-- Table structure for wallet_logs
-- -------------------------------
DROP TABLE IF EXISTS `wallet_logs`;
CREATE TABLE `wallet_logs`  (
  `id` bigint UNSIGNED NOT NULL,
  `userId` int(0) UNSIGNED NOT NULL COMMENT '关联的用户ID',
  `targetUserId` int UNSIGNED  NOT NULL DEFAULT 0  COMMENT '目标用户ID',
  `amount` decimal(12, 4) UNSIGNED NOT NULL COMMENT '本次变动金额',
  `action` tinyint UNSIGNED NOT NULL COMMENT '收支动作:1-收入2-支出',
  `type` tinyint UNSIGNED NOT NULL COMMENT '流水类型 1-ai奖励 2-推广奖励 3-团队奖励 4-特级分红奖励 5-提现 6=周结算收益',
  `createTime` timestamp(0) NULL DEFAULT NULL,
   PRIMARY KEY (`id`) USING BTREE,
   INDEX `wallet_logs_userId_index`(`userId`) USING BTREE,
   INDEX `type`(`type`) USING BTREE,
   CONSTRAINT `FK_wallet_logs_userId` FOREIGN KEY (`userId`) REFERENCES `users` (`id`)
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '钱包日志表' ROW_FORMAT = Dynamic;

-- -------------------------------
-- Table structure for profit_logs
-- -------------------------------
DROP TABLE IF EXISTS `profit_logs`;
CREATE TABLE `profit_logs`  (
  `id` bigint UNSIGNED NOT NULL,
  `userId` int(0) UNSIGNED NOT NULL COMMENT '关联的用户ID',
  `principal` decimal(24, 8) NOT NULL DEFAULT 0.00000000 COMMENT '托管本金',
  `profitRate` decimal(4, 3) NOT NULL DEFAULT 0.00 COMMENT '收益率 0.01=1%',
  `generatedAmount` decimal(24, 8) NOT NULL DEFAULT 0.00000000 COMMENT '托管本金',
  `grantToUser` tinyint UNSIGNED NOT NULL DEFAULT 0  COMMENT '结算类型 1=已结算 0=未结算',
  `createTime` timestamp(0) NULL DEFAULT NULL,
  `lastmodifiedTime` timestamp(0) NULL DEFAULT NULL,
   PRIMARY KEY (`id`) USING BTREE,
   INDEX `profit_logs_userId_index`(`userId`) USING BTREE,
   CONSTRAINT `FK_profit_logs_userId` FOREIGN KEY (`userId`) REFERENCES `users` (`id`)
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收益日结记录表' ROW_FORMAT = Dynamic;
