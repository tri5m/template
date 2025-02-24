/*
 Navicat Premium Data Transfer

 Source Server         : 本机docker
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34)
 Source Host           : 127.0.0.1:3306
 Source Schema         : template

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34)
 File Encoding         : 65001

 Date: 19/01/2024 17:40:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_admin
-- ----------------------------
DROP TABLE IF EXISTS `t_admin`;
CREATE TABLE `t_admin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(50) DEFAULT NULL COMMENT 'name',
  `user_name` varchar(50) DEFAULT NULL COMMENT 'userName',
  `password` varchar(50) DEFAULT NULL COMMENT 'password',
  `role_codes` json NOT NULL COMMENT 'roleCodes',
  `create_by` bigint DEFAULT NULL COMMENT 'createBy',
  `create_time` datetime DEFAULT NULL COMMENT 'createTime',
  `update_by` bigint DEFAULT NULL COMMENT 'updateBy',
  `update_time` datetime DEFAULT NULL COMMENT 'updateTime',
  `is_banned` tinyint(1) NOT NULL COMMENT 'isBanned',
  `is_super` tinyint(1) NOT NULL COMMENT 'isSuper',
  `is_del` tinyint(1) NOT NULL COMMENT 'isDel',
  `ext` json DEFAULT NULL COMMENT 'ext',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='mb_admin';

-- ----------------------------
-- Records of t_admin
-- ----------------------------
BEGIN;
INSERT INTO `t_admin` (`id`, `name`, `user_name`, `password`, `role_codes`, `create_by`, `create_time`, `update_by`, `update_time`, `is_banned`, `is_super`, `is_del`, `ext`) VALUES (1, '超级管理员', 'admin', '96e79218965eb72c92a549dd5a330112', '[]', 0, '2023-09-07 15:39:01', 0, '2023-09-07 15:39:06', 0, 1, 0, NULL);
INSERT INTO `t_admin` (`id`, `name`, `user_name`, `password`, `role_codes`, `create_by`, `create_time`, `update_by`, `update_time`, `is_banned`, `is_super`, `is_del`, `ext`) VALUES (2, '管理员2', 'admin2', '96e79218965eb72c92a549dd5a330112', '[\"admin\"]', 1, '2024-01-19 15:00:50', 1, '2024-01-19 15:00:54', 0, 0, 0, NULL);
COMMIT;

-- ----------------------------
-- Table structure for t_admin_role
-- ----------------------------
DROP TABLE IF EXISTS `t_admin_role`;
CREATE TABLE `t_admin_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(50) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `is_banned` tinyint(1) DEFAULT NULL,
  `is_sys_role` tinyint(1) DEFAULT NULL,
  `is_del` tinyint(1) DEFAULT NULL COMMENT 'isDel',
  `description` text COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='mb_admin_role';

-- ----------------------------
-- Records of t_admin_role
-- ----------------------------
BEGIN;
INSERT INTO `t_admin_role` (`id`, `code`, `name`, `type`, `create_by`, `create_time`, `is_banned`, `is_sys_role`, `is_del`, `description`) VALUES (1, 'admin', '管理员角色', NULL, 1, '2024-01-19 14:59:20', 0, 1, 0, '系统管理员角色，拥有所有权限');
COMMIT;

-- ----------------------------
-- Table structure for t_auth_role_idx
-- ----------------------------
DROP TABLE IF EXISTS `t_auth_role_idx`;
CREATE TABLE `t_auth_role_idx` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `role_code` varchar(50) DEFAULT NULL COMMENT 'roleCode',
  `auth_code` varchar(50) DEFAULT NULL COMMENT 'authCode',
  `create_by` bigint DEFAULT NULL COMMENT 'createBy',
  `create_time` datetime DEFAULT NULL COMMENT 'createTime',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='mb_auth_role_idx';

-- ----------------------------
-- Records of t_auth_role_idx
-- ----------------------------
BEGIN;
INSERT INTO `t_auth_role_idx` (`id`, `role_code`, `auth_code`, `create_by`, `create_time`) VALUES (1, 'admin', 'account_admin', 1, '2024-01-19 15:06:00');
INSERT INTO `t_auth_role_idx` (`id`, `role_code`, `auth_code`, `create_by`, `create_time`) VALUES (2, 'admin', 'test', 1, '2024-01-19 15:06:10');
COMMIT;

-- ----------------------------
-- Table structure for t_authorization
-- ----------------------------
DROP TABLE IF EXISTS `t_authorization`;
CREATE TABLE `t_authorization` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  `authority` varchar(50) DEFAULT NULL,
  `type` int DEFAULT NULL,
  `parent_code` varchar(50) DEFAULT NULL,
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `is_del` tinyint(1) DEFAULT NULL COMMENT 'isDel',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='mb_authorization';

-- ----------------------------
-- Records of t_authorization
-- ----------------------------
BEGIN;
INSERT INTO `t_authorization` (`id`, `code`, `name`, `description`, `authority`, `type`, `parent_code`, `create_by`, `create_time`, `is_del`) VALUES (1, 'account_admin', '管理员权限', '账号管理', 'account_admin', NULL, NULL, 1, '2024-01-19 15:02:49', 0);
INSERT INTO `t_authorization` (`id`, `code`, `name`, `description`, `authority`, `type`, `parent_code`, `create_by`, `create_time`, `is_del`) VALUES (2, 'test', '测试权限', '拥有测试接口权限', 'test', NULL, NULL, 1, '2024-01-19 15:04:23', 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
