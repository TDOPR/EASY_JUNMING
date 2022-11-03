CREATE DATABASE easytrade;
use easytrade;

/**
 * 角色表
 */
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `roleCode` varchar(255)  NULL DEFAULT NULL  COMMENT '角色编码',
  `roleName` varchar(255)  NULL DEFAULT NULL  COMMENT '角色名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

/**
 * 机构表
 */
DROP TABLE IF EXISTS `sys_channel`;
CREATE TABLE `sys_channel`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `channelCode` varchar(255)  NULL DEFAULT NULL  COMMENT '机构编码',
  `channelName` varchar(255)  NULL DEFAULT NULL  COMMENT '机构名称',
  `sortIndex` tinyint UNSIGNED NULL DEFAULT NULL  COMMENT '排序下标',
  `parentId` int(0) UNSIGNED NULL DEFAULT NULL  COMMENT '父机构编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '机构表' ROW_FORMAT = Dynamic;

/**
 * 用户表
 */
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `channelId` int(0) UNSIGNED  NULL DEFAULT NULL COMMENT '渠道Id',
  `deleted` tinyint  NULL DEFAULT NULL COMMENT '逻辑删除 1=删除 0=未删除',
  `enabled` tinyint NULL DEFAULT NULL COMMENT '用户状态 1=可用 0=删除',
  `name` varchar(255) NULL DEFAULT NULL COMMENT '用户昵称',
  `password` varchar(255)  NULL DEFAULT NULL COMMENT '密码',
  `roleId` int(0) UNSIGNED NULL DEFAULT NULL COMMENT '角色Id',
  `salt` varchar(255)  NULL DEFAULT NULL COMMENT '密码加密的盐',
  `username` varchar(255)  NULL DEFAULT NULL COMMENT '登录名',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK51bvuyvihefoh4kp5syh2jpi4`(`username`) USING BTREE,
  INDEX `FKtlnkwkosadnhdypumotecrebo`(`channelId`) USING BTREE,
  INDEX `FK9s2sqg6p1req126agyn1sfeiy`(`roleId`) USING BTREE,
  CONSTRAINT `FK9s2sqg6p1req126agyn1sfeiy` FOREIGN KEY (`roleId`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKtlnkwkosadnhdypumotecrebo` FOREIGN KEY (`channelId`) REFERENCES `sys_channel` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='用户表'  ROW_FORMAT = Dynamic;

/**
 * 菜单表
 */
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `icon` varchar(255)  NULL DEFAULT NULL COMMENT '菜单图标样式',
  `importStr` varchar(255) NULL DEFAULT NULL COMMENT '组件路径',
  `parentId` int(0) NULL DEFAULT NULL COMMENT '父菜单编号',
  `path` varchar(255)  NULL DEFAULT NULL COMMENT '访问路径',
  `sortIndex` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '排序下标',
  `title` varchar(255)  NULL DEFAULT NULL COMMENT '菜单名称',
   `type` tinyint  NOT NULL DEFAULT 0 COMMENT '菜单类型  1=目录,2=菜单,3=权限',
  `outlink` tinyint  NOT NULL DEFAULT 0 COMMENT '是否外链 1=外链,0=内部菜单',
  `display` tinyint  NOT NULL DEFAULT 1 COMMENT '显示状态 1=显示,0=隐藏',
  `authorityStr` varchar(64)  NOT NULL DEFAULT 1 COMMENT '权限字符',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='菜单表' ROW_FORMAT = Dynamic;


/**
 * 角色菜单表
 */
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `menuId` int(0) UNSIGNED NOT NULL  COMMENT '菜单Id',
  `roleId` int(0) UNSIGNED NOT NULL  COMMENT '角色Id',
  `createTime` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`menuId`, `roleId`) USING BTREE,
  FOREIGN KEY (`menuId`) REFERENCES `sys_menu` (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='角色菜单表' ROW_FORMAT = Dynamic;


/**
 * 操作日志表
 */
DROP TABLE IF EXISTS `sys_operationlog`;
CREATE TABLE `sys_operationlog`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL  COMMENT '创建时间',
  `content` text  NULL COMMENT '详细信息',
  `description` varchar(255) NULL DEFAULT NULL COMMENT '操作类型',
  `ipAddr` varchar(255)  NULL DEFAULT NULL COMMENT 'ip地址',
  `module` varchar(255)  NULL DEFAULT NULL COMMENT '模块',
  `username` varchar(255)  NULL DEFAULT NULL COMMENT '操作员',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='操作日志表' ROW_FORMAT = Dynamic;

/**
 * 登录日志表
 */
DROP TABLE IF EXISTS `sys_loginlog`;
CREATE TABLE `sys_loginlog`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL  COMMENT '创建时间',
  `ipAddr` varchar(255)  NULL DEFAULT NULL COMMENT '登录Ip地址',
  `username` varchar(255)  NULL DEFAULT NULL COMMENT '用户名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='操作日志表' ROW_FORMAT = Dynamic;

/**
 * 错误日志表
 */
DROP TABLE IF EXISTS `sys_errorlog`;
CREATE TABLE `sys_errorlog`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `errorMsg` varchar(255)  NULL DEFAULT NULL COMMENT '错误信息',
  `errorType` varchar(255)  NULL DEFAULT NULL COMMENT '错误类型',
  `ipAddr` varchar(255)  NULL DEFAULT NULL COMMENT '机器ip地址',
  `position` text  NULL COMMENT'异常发生的位置',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='错误日志表' ROW_FORMAT = Dynamic;

/**
 * 字典表
 */
DROP TABLE IF EXISTS `sys_dictionary`;
CREATE TABLE `sys_dictionary`  (
  `id` bigint(0) UNSIGNED NOT NULL,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `dicKey` varchar(255)  NULL DEFAULT NULL COMMENT '字典的key',
  `dicName` varchar(255)  NULL DEFAULT NULL COMMENT '字典类型名称',
  `dicValue` varchar(255)  NULL DEFAULT NULL COMMENT '字典的value',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='字典表' ROW_FORMAT = Dynamic;

/**
 * 元数据表信息
 */
DROP TABLE IF EXISTS `meta_info`;
CREATE TABLE `meta_info`  (
  `id` bigint(0) UNSIGNED NOT NULL,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `increment` bit(1) NOT NULL COMMENT '表的主键是否为自增类型 true(1)=自增 flase(0)=手动设置',
  `menuId` int(0) UNSIGNED NULL DEFAULT NULL COMMENT '关联的菜单Id' ,
  `metaDescription` varchar(255) NULL DEFAULT NULL COMMENT '描述信息',
  `metaName` varchar(255)  NULL DEFAULT NULL COMMENT '表名称',
  `tableCode` varchar(255)  NULL DEFAULT NULL COMMENT '数据库表名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='元数据表' ROW_FORMAT = Dynamic;

/**
 * 元数据表字段信息
 */
DROP TABLE IF EXISTS `meta_column`;
CREATE TABLE `meta_column`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `columnCode` varchar(255) NULL DEFAULT NULL COMMENT '数据库中字段名称',
  `columnName` varchar(255)  NULL DEFAULT NULL COMMENT '字段名名称->映射到页面显示的table列名称',
  `dataType` varchar(255) NULL DEFAULT NULL COMMENT '数据类型',
  `metaId` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '所属meta_info的主键Id',
  `search` bit(1) NOT NULL COMMENT '是否作为查询条件',
  `sortIndex` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '页面上字段显示的顺序',
  `viewShow` bit(1) NOT NULL COMMENT '页面上是否展示当前列',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKnd8poxd4jfxgpchufdgxlvwyb`(`metaId`) USING BTREE,
  CONSTRAINT `FKnd8poxd4jfxgpchufdgxlvwyb` FOREIGN KEY (`metaId`) REFERENCES `meta_info` (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4  COLLATE = utf8mb4_unicode_ci COMMENT ='元数据表字段' ROW_FORMAT = Dynamic;


INSERT INTO sys_dictionary (id, createTime, lastmodifiedTime, dicKey, dicName, dicValue) VALUES (1, '2020-11-03 10:46:11', '2020-11-03 10:46:11', 'loginLogSaveDay', '基础字典', '-1');
INSERT INTO sys_dictionary (id, createTime, lastmodifiedTime, dicKey, dicName, dicValue) VALUES (2, '2020-11-03 10:46:11', '2020-11-03 10:46:11', 'operationLogSaveDay', '基础字典', '-1');
INSERT INTO sys_dictionary (id, createTime, lastmodifiedTime, dicKey, dicName, dicValue) VALUES (3, '2020-11-03 10:46:11', '2020-11-03 10:46:11', 'errorLogSaveDay', '基础字典', '-1');
INSERT INTO sys_dictionary (id, createTime, lastmodifiedTime, dicKey, dicName, dicValue) VALUES (4, '2020-11-03 10:46:11', '2020-11-03 10:46:11', 'enableSso', '基础字典', 'false');
INSERT INTO sys_dictionary (id, createTime, lastmodifiedTime, dicKey, dicName, dicValue) VALUES (5, '2020-11-03 10:46:11', '2020-11-03 10:46:11', 'thresholdSize', '基础字典', '80');

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
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (16, '2021-05-14 16:26:00', '2021-09-10 12:01:28', 'iconfont icon-xitongshezhi icon-menu', 'admin/systemManager/setting/Index.vue', 2, '/index/setting', 5, '系统设置',2,0,1,'sys:system:all');
INSERT INTO sys_menu (id, createTime, lastmodifiedTime, icon, importStr, parentId, path, sortIndex, title,type,outlink,display,authorityStr) VALUES (17, '2022-05-24 21:29:45', '2022-08-23 10:37:26', 'iconfont icon-yonghuguanli1', 'metadata/Index.vue', 0, '/index/metadata', 0, '元数据管理',2,0,1,'sys:meta:all');
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



INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (2, 3, '2022-08-23 15:09:19');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (3, 3, '2022-08-23 15:09:19');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (4, 3, '2022-08-23 15:09:19');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (5, 3, '2022-08-23 15:09:19');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (6, 3, '2022-08-23 15:09:19');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (7, 3, '2022-08-23 15:09:19');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (8, 3, '2022-08-23 15:09:19');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (10, 3, '2021-11-19 11:23:55');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (13, 3, '2022-08-23 15:09:19');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (15, 3, '2022-08-23 15:09:19');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (16, 3, '2022-08-23 15:09:19');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (17, 1, '2022-08-23 15:22:07');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (17, 2, '2022-08-23 15:22:01');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (17, 3, '2022-08-23 15:09:19');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (37, 3, '2022-08-23 15:09:19');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (38, 3, '2022-08-23 15:09:19');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (39, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (40, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (41, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (42, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (43, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (44, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (45, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (46, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (47, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (48, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (49, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (50, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (51, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (52, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (53, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (54, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (55, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (56, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (57, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (58, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (59, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (60, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (61, 3, '2022-10-19 15:17:36');
INSERT INTO sys_role_menu (menuId, roleId, createTime) VALUES (62, 3, '2022-10-19 15:17:36');

INSERT INTO sys_user (id, createTime, lastmodifiedTime, channelId, deleted, enabled, name, password, roleId, salt, username) VALUES (1, null, null, 1, 0, 1, null, 'E81C5B99F11123006C1F58DA7488281D', 3, '962012d09b8170d912f0669f6d7d933', 'admin');

/*插入元数据模型相关信息*/
INSERT INTO meta_info (id, createTime, lastmodifiedTime, increment, metaDescription, metaName, tableCode, menuId) VALUES (1561953433691971584, '2022-08-23 13:48:13', '2022-08-23 13:49:21', true, '对图书数据进行管理', '图书管理', 'books', 37);
INSERT INTO meta_info (id, createTime, lastmodifiedTime, increment, metaDescription, metaName, tableCode, menuId) VALUES (1561962038331793408, '2022-08-23 14:22:24', '2022-08-23 15:59:50', false, '管理图书馆用户信息', '读者管理', 'readers', 38);

INSERT INTO meta_column (id, columnCode, columnName, dataType, metaId, sortIndex, viewShow, search) VALUES (29, 'bookName', '图书名称', 'varchar(255)', 1561953433691971584, 0, true, true);
INSERT INTO meta_column (id, columnCode, columnName, dataType, metaId, sortIndex, viewShow, search) VALUES (30, 'author', '作者', 'varchar(255)', 1561953433691971584, 1, true, true);
INSERT INTO meta_column (id, columnCode, columnName, dataType, metaId, sortIndex, viewShow, search) VALUES (31, 'publishTime', '发版时间', 'datetime', 1561953433691971584, 2, true, true);
INSERT INTO meta_column (id, columnCode, columnName, dataType, metaId, sortIndex, viewShow, search) VALUES (32, 'bookType', '图片类型', 'varchar(255)', 1561953433691971584, 3, true, true);
INSERT INTO meta_column (id, columnCode, columnName, dataType, metaId, sortIndex, viewShow, search) VALUES (33, 'name', '姓名', 'varchar(255)', 1561962038331793408, 0, true, true);
INSERT INTO meta_column (id, columnCode, columnName, dataType, metaId, sortIndex, viewShow, search) VALUES (34, 'age', '年龄', 'int', 1561962038331793408, 1, true, true);
INSERT INTO meta_column (id, columnCode, columnName, dataType, metaId, sortIndex, viewShow, search) VALUES (35, 'sex', '性别', 'text', 1561962038331793408, 2, true, false);
INSERT INTO meta_column (id, columnCode, columnName, dataType, metaId, sortIndex, viewShow, search) VALUES (36, 'address', '地址', 'varchar(255)', 1561962038331793408, 3, true, true);
INSERT INTO meta_column (id, columnCode, columnName, dataType, metaId, sortIndex, viewShow, search) VALUES (37, 'birthday', '生日', 'datetime', 1561962038331793408, 4, true, true);
INSERT INTO meta_column (id, columnCode, columnName, dataType, metaId, sortIndex, viewShow, search) VALUES (38, 'constellation', '星座', 'varchar(255)', 1561962038331793408, 5, true, true);


DROP TABLE IF EXISTS `books`;
CREATE TABLE `books`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `bookName` varchar(255)  NULL DEFAULT NULL,
  `author` varchar(255)  NULL DEFAULT NULL,
  `publishTime` datetime(0) NULL DEFAULT NULL,
  `bookType` varchar(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

INSERT INTO `books` VALUES (1, '白夜行', '东野圭吾', '2022-08-01 00:00:00', '推理');
INSERT INTO `books` VALUES (2, '嫌疑人X的献身', '东野圭吾', '2022-08-11 00:00:00', '推理');
INSERT INTO `books` VALUES (9, '圣女的救赎', '东野圭吾', '2022-08-11 00:00:00', '推理');
INSERT INTO `books` VALUES (4, '放学后', '东野圭吾', '2022-08-11 00:00:00', '推理');
INSERT INTO `books` VALUES (5, '恶意', '东野圭吾', '2022-08-11 00:00:00', '推理');
INSERT INTO `books` VALUES (6, '假面山庄', '东野圭吾', '2022-08-11 00:00:00', '推理');
INSERT INTO `books` VALUES (7, '我杀了他', '东野圭吾', '2022-08-11 00:00:00', '推理');
INSERT INTO `books` VALUES (8, '谁杀了她', '东野圭吾', '2022-08-11 00:00:00', '推理');
INSERT INTO `books` VALUES (3, '无人生还', '阿加莎·克里斯蒂', '2022-08-10 00:00:00', '推理');