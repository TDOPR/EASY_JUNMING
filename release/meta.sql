
-- ----------------------------------
-- Table structure for meta_info
-- ----------------------------------
DROP TABLE IF EXISTS `meta_info`;
CREATE TABLE `meta_info`  (
  `id` bigint(0) UNSIGNED NOT NULL,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `increment` bit(1) NOT NULL COMMENT '表的主键是否为自增类型 true(1)=自增 flase(0)=手动设置',
  `menuId` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '关联的菜单Id' ,
  `metaDescription` varchar(255) NOT NULL DEFAULT '' COMMENT '描述信息',
  `metaName` varchar(64) NOT NULL DEFAULT '' COMMENT '表名称',
  `tableCode` varchar(64)  NOT NULL DEFAULT '' COMMENT '数据库表名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='元数据表' ROW_FORMAT = Dynamic;

-- ----------------------------------
-- Table structure for meta_column
-- ----------------------------------
DROP TABLE IF EXISTS `meta_column`;
CREATE TABLE `meta_column`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `lastmodifiedTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `columnCode` varchar(255) NOT NULL DEFAULT '' COMMENT '数据库中字段名称',
  `columnName` varchar(255) NOT NULL DEFAULT '' COMMENT '字段名名称->映射到页面显示的table列名称',
  `dataType` varchar(255) NOT NULL DEFAULT ''  COMMENT '数据类型',
  `metaId` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '所属meta_info的主键Id',
  `search` bit(1) NOT NULL DEFAULT 0 COMMENT '是否作为查询条件',
  `sortIndex` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '页面上字段显示的顺序',
  `viewShow` bit(1) NOT NULL DEFAULT 1 COMMENT '页面上是否展示当前列',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKnd8poxd4jfxgpchufdgxlvwyb`(`metaId`) USING BTREE,
  CONSTRAINT `FKnd8poxd4jfxgpchufdgxlvwyb` FOREIGN KEY (`metaId`) REFERENCES `meta_info` (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4  COLLATE = utf8mb4_unicode_ci COMMENT ='元数据表字段' ROW_FORMAT = Dynamic;

/*插入元数据模型相关信息*/
INSERT INTO meta_info (id, createTime, lastmodifiedTime, increment, metaDescription, metaName, tableCode, menuId) VALUES (1561953433691971584, '2022-08-23 13:48:13', '2022-08-23 13:49:21', true, '对图书数据进行管理', '图书管理', 'books', 37);

INSERT INTO meta_column (id, columnCode, columnName, dataType, metaId, sortIndex, viewShow, search) VALUES (29, 'bookName', '图书名称', 'varchar(255)', 1561953433691971584, 0, true, true);
INSERT INTO meta_column (id, columnCode, columnName, dataType, metaId, sortIndex, viewShow, search) VALUES (30, 'author', '作者', 'varchar(255)', 1561953433691971584, 1, true, true);
INSERT INTO meta_column (id, columnCode, columnName, dataType, metaId, sortIndex, viewShow, search) VALUES (31, 'publishTime', '发版时间', 'datetime', 1561953433691971584, 2, true, true);
INSERT INTO meta_column (id, columnCode, columnName, dataType, metaId, sortIndex, viewShow, search) VALUES (32, 'bookType', '图片类型', 'varchar(255)', 1561953433691971584, 3, true, true);



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