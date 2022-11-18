CREATE DATABASE easytrade;
use easytrade;

-- -------------------------------
-- Table structure for sys_role
-- -------------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `roleCode` varchar(255) NOT NULL DEFAULT ''  COMMENT '角色编码',
  `roleName` varchar(255) NOT NULL DEFAULT ''  COMMENT '角色名称',
  `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '角色状态 1=可用 0=删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- --------------------------------
-- Table structure for sys_channel
-- --------------------------------
DROP TABLE IF EXISTS `sys_channel`;
CREATE TABLE `sys_channel`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `channelCode` varchar(255) NOT NULL DEFAULT ''  COMMENT '机构编码',
  `channelName` varchar(255) NOT NULL DEFAULT ''  COMMENT '机构名称',
  `sortIndex` tinyint UNSIGNED NOT NULL DEFAULT 0  COMMENT '排序下标',
  `parentId` int(0) UNSIGNED NOT NULL DEFAULT 0  COMMENT '父机构编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '机构表' ROW_FORMAT = Dynamic;

-- -----------------------------
-- Table structure for sys_user
-- -----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `channelId` int(0) UNSIGNED  NOT NULL DEFAULT 1   COMMENT '渠道Id',
  `deleted` tinyint  NOT NULL DEFAULT 0 COMMENT '逻辑删除 1=删除 0=未删除',
  `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '用户状态 1=可用 0=删除',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '用户昵称',
  `password` varchar(255)  NOT NULL DEFAULT '' COMMENT '密码',
  `roleId` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '角色Id',
  `salt` varchar(32) NOT NULL DEFAULT '' COMMENT '密码加密的盐',
  `username` varchar(255)  NOT NULL DEFAULT '' COMMENT '用户名',
  `email` varchar(36)  NOT NULL DEFAULT '' COMMENT '邮箱号',
  `mobile` varchar(18)  NOT NULL DEFAULT '' COMMENT '手机号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK51bvuyvihefoh4kp5syh2jpi4`(`username`) USING BTREE,
  INDEX `FKtlnkwkosadnhdypumotecrebo`(`channelId`) USING BTREE,
  INDEX `FK9s2sqg6p1req126agyn1sfeiy`(`roleId`) USING BTREE,
  CONSTRAINT `FK9s2sqg6p1req126agyn1sfeiy` FOREIGN KEY (`roleId`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKtlnkwkosadnhdypumotecrebo` FOREIGN KEY (`channelId`) REFERENCES `sys_channel` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='用户表'  ROW_FORMAT = Dynamic;

-- -----------------------------
-- Table structure for sys_menu
-- -----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `icon` varchar(255)  NOT NULL DEFAULT '' COMMENT '菜单图标样式',
  `importStr` varchar(255) NOT NULL DEFAULT '' COMMENT '组件路径',
  `parentId` int(0) NOT NULL DEFAULT 0 COMMENT '父菜单编号',
  `path` varchar(255)  NOT NULL DEFAULT '' COMMENT '访问路径',
  `sortIndex` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '排序下标',
  `title` varchar(255)  NOT NULL DEFAULT '' COMMENT '菜单名称',
   `type` tinyint  NOT NULL DEFAULT 0 COMMENT '菜单类型  1=目录,2=菜单,3=权限',
  `outlink` tinyint  NOT NULL DEFAULT 0 COMMENT '是否外链 1=外链,0=内部菜单',
  `display` tinyint  NOT NULL DEFAULT 1 COMMENT '显示状态 1=显示,0=隐藏',
  `authorityStr` varchar(64)  NOT NULL DEFAULT 1 COMMENT '权限字符',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='菜单表' ROW_FORMAT = Dynamic;


-- ----------------------------------
-- Table structure for sys_role_menu
-- ----------------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `menuId` int(0) UNSIGNED NOT NULL  COMMENT '菜单Id',
  `roleId` int(0) UNSIGNED NOT NULL  COMMENT '角色Id',
  `createTime` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`menuId`, `roleId`) USING BTREE,
  FOREIGN KEY (`menuId`) REFERENCES `sys_menu` (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='角色菜单表' ROW_FORMAT = Dynamic;

-- ---------------------------------
-- Table structure for sys_role_menu
-- ----------------------------------
DROP TABLE IF EXISTS `sys_operationlog`;
CREATE TABLE `sys_operationlog`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL  COMMENT '创建时间',
  `content` text  NULL  COMMENT '详细信息',
  `description` varchar(255) NOT NULL DEFAULT '' COMMENT '操作类型',
  `ipAddr` varchar(255)  NOT NULL DEFAULT '' COMMENT 'ip地址',
  `module` varchar(255)  NOT NULL DEFAULT '' COMMENT '模块',
  `username` varchar(255)  NOT NULL DEFAULT '' COMMENT '操作员',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='操作日志表' ROW_FORMAT = Dynamic;


-- ----------------------------------
-- Table structure for sys_loginlog
-- ----------------------------------
DROP TABLE IF EXISTS `sys_loginlog`;
CREATE TABLE `sys_loginlog`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL  COMMENT '创建时间',
  `ipAddr` varchar(255)  NULL DEFAULT NULL COMMENT '登录Ip地址',
  `username` varchar(255) NOT NULL DEFAULT '' COMMENT '用户名',
  `userType` varchar(255)  NOT NULL DEFAULT 1 COMMENT '用户类型 1=系统用户 2=app用户',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------------
-- Table structure for sys_errorlog
-- ----------------------------------
DROP TABLE IF EXISTS `sys_errorlog`;
CREATE TABLE `sys_errorlog`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `errorMsg` varchar(255)  NOT NULL DEFAULT '' COMMENT '错误信息',
  `errorType` varchar(255)  NOT NULL DEFAULT '' COMMENT '错误类型',
  `ipAddr` varchar(255)  NOT NULL DEFAULT '' COMMENT '机器ip地址',
  `position` text   NULL COMMENT'异常发生的位置',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='错误日志表' ROW_FORMAT = Dynamic;


-- ----------------------------------
-- Table structure for sys_dictionary
-- ----------------------------------
DROP TABLE IF EXISTS `sys_dictionary`;
CREATE TABLE `sys_dictionary`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `dicKey` varchar(255)  NOT NULL DEFAULT '' COMMENT '字典的key',
  `dicValue` varchar(255)  NOT NULL DEFAULT '' COMMENT '字典的value',
  `parentId` int(0) UNSIGNED DEFAULT 0 COMMENT '父级id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='字典表' ROW_FORMAT = Dynamic;


-- ----------------------------------
-- Table structure for sys_message
-- ----------------------------------
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `keyName` varchar(255)  NOT NULL DEFAULT '' COMMENT '字典的key',
  `zhCn` text   NULL  COMMENT '中文',
  `zhTw` text   NULL  COMMENT '繁体中文',
   `enUs` text  NULL  COMMENT '英文',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='国际化信息表' ROW_FORMAT = Dynamic;

-- ----------------------------------
-- Table structure for sys_notice
-- ----------------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `sortIndex` int(0)  NOT NULL DEFAULT 0 COMMENT '排序',
  `display` tinyint  NOT NULL DEFAULT 1 COMMENT '显示状态 1=可见,0=隐藏',
  `cnTitle` varchar(255)  NOT NULL DEFAULT '' COMMENT '中文标题',
  `cnDescription` varchar(255)  NOT NULL DEFAULT '' COMMENT '中文简介',
  `cnText` text  NULL COMMENT '中文内容',
  `enTitle` varchar(255)  NOT NULL DEFAULT '' COMMENT '英文标题',
  `enDescription` varchar(255)  NOT NULL DEFAULT '' COMMENT '英文简介',
  `enText` text   NULL COMMENT '英文内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='系统公告' ROW_FORMAT = Dynamic;

INSERT INTO sys_dictionary (id, createTime, lastmodifiedTime, dicKey, parentId, dicValue) VALUES (1, '2020-11-03 10:46:11', '2020-11-03 10:46:11', 'loginLogSaveDay', 0, '-1');
INSERT INTO sys_dictionary (id, createTime, lastmodifiedTime, dicKey, parentId, dicValue) VALUES (2, '2020-11-03 10:46:11', '2020-11-03 10:46:11', 'loginLogSaveDay', 1, '-1');
INSERT INTO sys_dictionary (id, createTime, lastmodifiedTime, dicKey, parentId, dicValue) VALUES (3, '2020-11-03 10:46:11', '2020-11-03 10:46:11', 'operationLogSaveDay', 1, '-1');
INSERT INTO sys_dictionary (id, createTime, lastmodifiedTime, dicKey, parentId, dicValue) VALUES (4, '2020-11-03 10:46:11', '2020-11-03 10:46:11', 'errorLogSaveDay', 1, '-1');
INSERT INTO sys_dictionary (id, createTime, lastmodifiedTime, dicKey, parentId, dicValue) VALUES (5, '2020-11-03 10:46:11', '2020-11-03 10:46:11', 'enableSso', 1, 'false');
INSERT INTO sys_dictionary (id, createTime, lastmodifiedTime, dicKey, parentId, dicValue) VALUES (6, '2020-11-03 10:46:11', '2020-11-03 10:46:11', 'thresholdSize', 1, '80');

INSERT INTO sys_channel (id, createTime, lastmodifiedTime, channelCode, channelName, sortIndex, parentId) VALUES (1, '2021-05-18 18:51:56', '2021-05-18 18:51:56', '1000', '总渠道', 1, 0);

INSERT INTO sys_role (id, createTime, lastmodifiedTime, roleCode, roleName) VALUES (1, '2019-05-14 16:26:12', '2019-05-14 16:26:12', 'user', '普通用户');
INSERT INTO sys_role (id, createTime, lastmodifiedTime, roleCode, roleName) VALUES (2, '2019-05-14 16:26:12', '2019-05-14 16:26:12', 'manager', '渠道负责人');
INSERT INTO sys_role (id, createTime, lastmodifiedTime, roleCode, roleName) VALUES (3, '2019-05-14 16:26:12', '2019-05-14 16:26:12', 'admin', '管理员');

INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (2, '2021-05-14 16:26:00', '2022-08-23 10:37:26', 'iconfont icon-xitongshezhi icon-menu', 'common/Through.vue', 0, '/index/systems', 1, '系统管理',1,0,1,'');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (3, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 'iconfont icon-yonghuguanli', 'admin/systemManager/user/Index.vue', 2, '/index/user', 1, '用户管理',2,0,1,'sys:user:list');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (4, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 'iconfont icon-yonghuguanli1', 'admin/systemManager/role/Index.vue', 2, '/index/role', 2, '角色管理',2,0,1,'sys:role:list');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (5, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 'iconfont icon-qudaoguanli', 'admin/systemManager/channel/Index.vue', 2, '/index/channel', 3, '机构管理',2,0,1,'sys:channel:list');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (6, '2021-05-14 16:26:00', '2022-08-23 10:37:26', 'iconfont icon-rizhiguanli icon-menu', 'common/Through.vue', 0, '/index/log', 2, '日志管理',1,0,1,'');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (7, '2021-05-13 17:20:00', '2021-09-10 12:01:28', 'iconfont icon-denglurizhi', 'admin/log/loginlog.vue', 6, '/index/loginLog', 1, '登录日志',2,0,1,'sys:loginlog:list');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (8, '2021-05-13 17:20:00', '2021-09-10 12:01:28', 'iconfont icon-caozuorizhi', 'admin/log/operationlog.vue', 6, '/index/operationlog', 2, '操作日志',2,0,1,'sys:operationlog:list');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (10, '2021-05-13 17:24:00', '2022-10-19 17:59:39', 'iconfont icon-xingneng icon-menu', 'admin/monitor/index.vue', 0, '/index/monitor', 5, '资源监控',2,0,1,'sys:monitor:list');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (13, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 'iconfont icon-xitongguanli-caidanguanli', 'admin/systemManager/menus/Index.vue', 2, '/index/menus', 4, '菜单管理',2,0,1,'sys:menu:list');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (15, '2021-05-13 17:20:00', '2021-09-10 12:01:28', 'iconfont icon-caozuorizhi', 'admin/log/errorlog.vue', 6, '/index/errorlog', 3, '错误日志',2,0,1,'sys:errorlog:list');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (16, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 'iconfont icon-xitongshezhi icon-menu', 'admin/systemManager/setting/Index.vue', 2, '/index/setting', 5, '系统设置',2,0,1,'sys:system:list');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (17, '2022-05-24 21:29:45', '2022-08-23 10:37:26', 'iconfont icon-yonghuguanli1', 'metadata/Index.vue', 0, '/index/metadata', 0, '元数据管理',2,0,1,'sys:meta:list');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (37, '2022-08-23 13:48:13', '2022-08-23 13:48:13', 'iconfont icon-rizhiguanli icon-menu', 'universal/Index.vue', 0, '/index/universal/1561953433691971584', 4, '图书管理',2,0,1,'');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (39, '2022-10-19 15:16:52', '2022-10-21 15:06:35', 'iconfont icon-xingneng icon-menu', 'admin/iframeView/index.vue', 0, 'http://localhost:9091/', 1, '服务监控',2,1,1,'');


/*添加用户管理菜单权限*/
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (40, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 3, 1,'新增', 3, 'sys:user:add');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (41, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 3, 2,'修改', 3, 'sys:user:edit');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (42, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 3, 3,'删除', 3, 'sys:user:remove');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (43, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 3, 4,'导出', 3, 'sys:user:export');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (62, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 3, 4,'重置密码', 3, 'sys:user:resetPwd');


/*添加角色管理菜单权限*/
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (44, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 4, 1,'新增', 3, 'sys:role:add');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (45, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 4, 2,'修改', 3, 'sys:role:edit');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (46, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 4, 3,'删除', 3, 'sys:role:remove');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (47, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 4, 4,'导出', 3, 'sys:role:export');

/*添加菜单管理菜单权限*/
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (48, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 13, 1,'新增', 3, 'sys:menu:add');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (49, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 13, 2,'修改', 3, 'sys:menu:edit');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (50, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 13, 3,'删除', 3, 'sys:menu:remove');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (51, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 13, 4,'导出', 3, 'sys:menu:export');

/*添加登录日志菜单权限*/
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (52, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 7, 1,'新增', 3, 'sys:loginlog:remove');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (53, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 7, 2,'修改', 3, 'sys:loginlog:export');

/*添加操作日志菜单权限*/
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (54, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 8, 3,'删除', 3, 'sys:operationlog:remove');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (55, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 8, 4,'导出', 3, 'sys:operationlog:export');

/*添加错误日志菜单权限*/
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (56, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 15, 3,'删除', 3, 'sys:errorlog:remove');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (57, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 15, 4,'导出', 3, 'sys:errorlog:export');

/*添加机构管理菜单权限*/
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (58, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 5, 1,'新增', 3, 'sys:channel:add');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (59, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 5, 2,'修改', 3, 'sys:channel:edit');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (60, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 5, 3,'删除', 3, 'sys:channel:remove');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (61, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 5, 4,'导出', 3, 'sys:channel:export');

/*添加国际化菜单权限*/
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (64, '2022-10-19 15:16:52', '2022-10-21 15:06:35', 'iconfont icon-xingneng icon-menu', 'admin/iframeView/index.vue', 0, 'http://localhost:9091/', 1, '国际化',2,1,1,'sys:msg:list');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (65, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 64, 1,'新增', 3, 'sys:msg:add');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (66, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 64, 2,'修改', 3, 'sys:msg:edit');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (67, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 64, 3,'删除', 3, 'sys:msg:remove');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (68, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 64, 4,'导出', 3, 'sys:msg:export');

/*添加系统公告菜单权限*/
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (69, '2022-10-19 15:16:52', '2022-10-21 15:06:35', 'iconfont icon-xingneng icon-menu', 'admin/iframeView/index.vue', 0, 'http://localhost:9091/', 1, '公告管理',2,1,1,'sys:notice:list');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (70, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 69, 1,'新增', 3, 'sys:notice:add');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (71, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 69, 2,'修改', 3, 'sys:notice:edit');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, parentId, sortIndex, title,type,authorityStr) VALUES (72, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 69, 3,'删除', 3, 'sys:notice:remove');


INSERT INTO sys_user (id, createTime, lastmodifiedTime, channelId, deleted, enabled, name, password, roleId, salt, username) VALUES (1, null, null, 1, 0, 1, '管理员', 'E81C5B99F11123006C1F58DA7488281D', 3, '962012d09b8170d912f0669f6d7d933', 'admin');

