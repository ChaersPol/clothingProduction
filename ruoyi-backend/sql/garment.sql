-- ============================================================
-- 服装厂生产管理系统 · 唯一数据库脚本
-- 说明：本文件由本地 MySQL 库 garment 完整导出，结构与数据保持一致。
-- 同步方式：在本地库调整数据后执行 ./scripts/sync-db.sh 重新导出。
-- ============================================================

-- MySQL dump 10.13  Distrib 8.0.46, for macos26.4 (arm64)
--
-- Host: 127.0.0.1    Database: garment
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `garment`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `garment` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `garment`;

--
-- Table structure for table `biz_bom`
--

DROP TABLE IF EXISTS `biz_bom`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_bom` (
  `bom_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'BOM ID',
  `style_no` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '款号',
  `material_type` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '面料' COMMENT '物料类型(面料/里布/辅料/配件)',
  `material_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '物料名称',
  `spec` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '规格',
  `unit` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '米' COMMENT '单位',
  `qty_per_piece` decimal(10,3) DEFAULT '0.000' COMMENT '单件用量',
  `loss_rate` decimal(6,2) DEFAULT '0.00' COMMENT '损耗率(%)',
  `unit_price` decimal(10,2) DEFAULT '0.00' COMMENT '单价',
  `amount` decimal(10,2) DEFAULT '0.00' COMMENT '小计(自动)',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`bom_id`),
  KEY `idx_bom_style` (`style_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1014 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用料清单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_bom`
--

LOCK TABLES `biz_bom` WRITE;
/*!40000 ALTER TABLE `biz_bom` DISABLE KEYS */;
INSERT INTO `biz_bom` VALUES (1001,'JT03','面料','雪纺印花','150D','米',1.600,5.00,18.00,30.24,'','admin','2026-11-06 10:00:00','',NULL),(1002,'JT03','里布','涤纶里布','210T','米',1.200,3.00,6.00,7.42,'','admin','2026-11-06 10:00:00','',NULL),(1003,'JT03','配件','隐形拉链','25cm黑色','条',1.000,0.00,3.50,3.50,'','admin','2026-11-06 10:00:00','',NULL),(1004,'JT03','辅料','树脂纽扣','12mm米白','颗',6.000,2.00,0.30,1.84,'','admin','2026-11-06 10:00:00','',NULL),(1005,'JT04','面料','真丝雪纺','16姆米','米',1.800,4.00,52.00,97.34,'','admin','2026-11-06 10:00:00','',NULL),(1006,'JT04','里布','涤纶里布','210T','米',1.300,3.00,6.00,8.03,'','admin','2026-11-06 10:00:00','',NULL),(1007,'JT04','辅料','松紧带','3cm','米',0.800,2.00,2.20,1.80,'','admin','2026-11-06 10:00:00','',NULL),(1008,'JT07','面料','雪纺印花','150D','米',1.200,5.00,18.00,22.68,'','admin','2026-11-06 10:00:00','',NULL),(1009,'JT07','配件','隐形拉链','25cm黑色','条',1.000,0.00,3.50,3.50,'','admin','2026-11-06 10:00:00','',NULL),(1010,'MQ3001','面料','真丝雪纺','16姆米','米',1.500,4.00,52.00,81.12,'','admin','2026-11-06 10:00:00','',NULL),(1011,'MQ3001','辅料','树脂纽扣','12mm米白','颗',4.000,2.00,0.30,1.22,'','admin','2026-11-06 10:00:00','',NULL),(1012,'MQ3001','辅料','缝纫线','402涤纶线','卷',0.200,1.00,4.50,0.91,'','admin','2026-11-06 10:00:00','',NULL),(1013,'JT05','面料','雪纺印花','150D','米',1.700,5.00,18.00,32.13,'','admin','2026-11-06 10:00:00','',NULL);
/*!40000 ALTER TABLE `biz_bom` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_bundle`
--

DROP TABLE IF EXISTS `biz_bundle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_bundle` (
  `bundle_id` bigint NOT NULL AUTO_INCREMENT COMMENT '扎ID',
  `bundle_no` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '扎号',
  `barcode` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '扎卡条码',
  `cut_id` bigint DEFAULT NULL COMMENT '裁货单ID',
  `cut_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '裁货单号',
  `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID',
  `style_no` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '款号',
  `cn_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '中文品名',
  `color` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '颜色',
  `size` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '尺码',
  `qty` int DEFAULT '0' COMMENT '裁货数量',
  `delivered_qty` int DEFAULT '0' COMMENT '已交后道数量',
  `packed_qty` int DEFAULT '0' COMMENT '已包装入库数',
  `defect_qty` int DEFAULT '0' COMMENT '次品数量',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态(0已开扎 1裁片在库 2产线中 3已完工)',
  `pickup_user` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '领片人',
  `pickup_time` datetime DEFAULT NULL COMMENT '领片时间',
  `pack_code` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '包装袋号',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`bundle_id`),
  UNIQUE KEY `uk_bundle_no` (`bundle_no`),
  UNIQUE KEY `uk_bundle_barcode` (`barcode`),
  KEY `idx_bundle_sku` (`sku_id`),
  KEY `idx_bundle_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1027 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='裁片扎包';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_bundle`
--

LOCK TABLES `biz_bundle` WRITE;
/*!40000 ALTER TABLE `biz_bundle` DISABLE KEYS */;
INSERT INTO `biz_bundle` VALUES (1001,'BN20261113001','JT03-黑色-S-BN20261113001',1001,'CO20261113001',1001,'JT03','卡扣束腰大摆裙','黑色','S',10,6,5,0,'2','赵丽','2026-11-14 09:30:00','PK100101','admin','2026-11-13 08:40:00','',NULL,''),(1002,'BN20261113002','JT03-黑色-S-BN20261113002',1001,'CO20261113001',1001,'JT03','卡扣束腰大摆裙','黑色','S',10,10,5,0,'2','赵丽','2026-11-14 09:30:00','PK100102','admin','2026-11-13 08:40:00','',NULL,''),(1003,'BN20261113003','JT03-白色-M-BN20261113003',1001,'CO20261113001',1006,'JT03','卡扣束腰大摆裙','白色','M',10,6,5,0,'2','赵丽','2026-11-14 09:30:00','PK100101','admin','2026-11-13 08:40:00','',NULL,''),(1004,'BN20261113004','JT03-白色-M-BN20261113004',1001,'CO20261113001',1006,'JT03','卡扣束腰大摆裙','白色','M',10,10,5,0,'2','赵丽','2026-11-14 09:30:00','PK100102','admin','2026-11-13 08:40:00','',NULL,''),(1005,'BN20261116005','JT03-黑色-S-BN20261116005',1002,'CO20261116001',1001,'JT03','卡扣束腰大摆裙','黑色','S',10,10,10,0,'3','钱峰','2026-11-16 08:40:00','PK100201','admin','2026-11-13 08:40:00','',NULL,''),(1006,'BN20261116006','JT03-黑色-S-BN20261116006',1002,'CO20261116001',1001,'JT03','卡扣束腰大摆裙','黑色','S',10,10,10,0,'3','钱峰','2026-11-16 08:40:00','PK100202','admin','2026-11-13 08:40:00','',NULL,''),(1007,'BN20261116007','JT03-黑色-M-BN20261116007',1002,'CO20261116001',1002,'JT03','卡扣束腰大摆裙','黑色','M',10,10,10,0,'3','钱峰','2026-11-16 08:40:00','PK100201','admin','2026-11-13 08:40:00','',NULL,''),(1008,'BN20261116008','JT03-黑色-M-BN20261116008',1002,'CO20261116001',1002,'JT03','卡扣束腰大摆裙','黑色','M',10,10,10,0,'3','钱峰','2026-11-16 08:40:00','PK100202','admin','2026-11-13 08:40:00','',NULL,''),(1009,'BN20261116009','JT03-白色-M-BN20261116009',1002,'CO20261116001',1006,'JT03','卡扣束腰大摆裙','白色','M',10,10,10,0,'3','钱峰','2026-11-16 08:40:00','PK100201','admin','2026-11-13 08:40:00','',NULL,''),(1010,'BN20261116010','JT03-白色-M-BN20261116010',1002,'CO20261116001',1006,'JT03','卡扣束腰大摆裙','白色','M',10,10,10,0,'3','钱峰','2026-11-16 08:40:00','PK100202','admin','2026-11-13 08:40:00','',NULL,''),(1011,'BN20261116011','JT03-白色-L-BN20261116011',1002,'CO20261116001',1007,'JT03','卡扣束腰大摆裙','白色','L',10,10,10,0,'3','钱峰','2026-11-16 08:40:00','PK100201','admin','2026-11-13 08:40:00','',NULL,''),(1012,'BN20261116012','JT03-白色-L-BN20261116012',1002,'CO20261116001',1007,'JT03','卡扣束腰大摆裙','白色','L',10,10,10,0,'3','钱峰','2026-11-16 08:40:00','PK100202','admin','2026-11-13 08:40:00','',NULL,''),(1013,'BN20261116013','JT03-藏青-L-BN20261116013',1002,'CO20261116001',1011,'JT03','卡扣束腰大摆裙','藏青','L',10,10,10,0,'3','钱峰','2026-11-16 08:40:00','PK100201','admin','2026-11-13 08:40:00','',NULL,''),(1014,'BN20261116014','JT03-藏青-L-BN20261116014',1002,'CO20261116001',1011,'JT03','卡扣束腰大摆裙','藏青','L',10,10,10,0,'3','钱峰','2026-11-16 08:40:00','PK100202','admin','2026-11-13 08:40:00','',NULL,''),(1015,'BN20261116015','JT03-酒红-XL-BN20261116015',1002,'CO20261116001',1016,'JT03','卡扣束腰大摆裙','酒红','XL',10,10,10,0,'3','钱峰','2026-11-16 08:40:00','PK100201','admin','2026-11-13 08:40:00','',NULL,''),(1016,'BN20261116016','JT03-酒红-XL-BN20261116016',1002,'CO20261116001',1016,'JT03','卡扣束腰大摆裙','酒红','XL',10,10,10,0,'3','钱峰','2026-11-16 08:40:00','PK100202','admin','2026-11-13 08:40:00','',NULL,''),(1017,'BN20261118017','JT04-米杏-S-BN20261118017',1003,'CO20261118001',1101,'JT04','原布连衣裙','米杏','S',10,0,0,0,'1','',NULL,'','admin','2026-11-13 08:40:00','',NULL,''),(1018,'BN20261118018','JT04-米杏-S-BN20261118018',1003,'CO20261118001',1101,'JT04','原布连衣裙','米杏','S',10,0,0,0,'1','',NULL,'','admin','2026-11-13 08:40:00','',NULL,''),(1019,'BN20261118019','JT04-米杏-M-BN20261118019',1003,'CO20261118001',1102,'JT04','原布连衣裙','米杏','M',10,0,0,0,'1','',NULL,'','admin','2026-11-13 08:40:00','',NULL,''),(1020,'BN20261118020','JT04-米杏-M-BN20261118020',1003,'CO20261118001',1102,'JT04','原布连衣裙','米杏','M',10,0,0,0,'1','',NULL,'','admin','2026-11-13 08:40:00','',NULL,''),(1021,'BN20261118021','JT04-雾霾蓝-M-BN20261118021',1003,'CO20261118001',1105,'JT04','原布连衣裙','雾霾蓝','M',10,0,0,0,'1','',NULL,'','admin','2026-11-13 08:40:00','',NULL,''),(1022,'BN20261118022','JT04-雾霾蓝-M-BN20261118022',1003,'CO20261118001',1105,'JT04','原布连衣裙','雾霾蓝','M',10,0,0,0,'1','',NULL,'','admin','2026-11-13 08:40:00','',NULL,''),(1023,'BN20261121023','JT07-米杏-S-BN20261121023',1005,'CO20261121001',1201,'JT07','原布半身裙','米杏','S',10,0,0,0,'1','',NULL,'','admin','2026-11-13 08:40:00','',NULL,''),(1024,'BN20261121024','JT07-米杏-S-BN20261121024',1005,'CO20261121001',1201,'JT07','原布半身裙','米杏','S',5,0,0,0,'1','',NULL,'','admin','2026-11-13 08:40:00','',NULL,''),(1025,'BN20261121025','JT07-雾霾蓝-L-BN20261121025',1005,'CO20261121001',1206,'JT07','原布半身裙','雾霾蓝','L',10,0,0,0,'1','',NULL,'','admin','2026-11-13 08:40:00','',NULL,''),(1026,'BN20261121026','JT07-雾霾蓝-L-BN20261121026',1005,'CO20261121001',1206,'JT07','原布半身裙','雾霾蓝','L',5,0,0,0,'1','',NULL,'','admin','2026-11-13 08:40:00','',NULL,'');
/*!40000 ALTER TABLE `biz_bundle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_check`
--

DROP TABLE IF EXISTS `biz_check`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_check` (
  `check_id` bigint NOT NULL AUTO_INCREMENT COMMENT '盘点ID',
  `check_no` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '盘点单号',
  `check_date` date DEFAULT NULL COMMENT '盘点日期',
  `checker` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '盘点人',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态(0进行中 1已完成)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`check_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1003 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='盘点单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_check`
--

LOCK TABLES `biz_check` WRITE;
/*!40000 ALTER TABLE `biz_check` DISABLE KEYS */;
INSERT INTO `biz_check` VALUES (1001,'PD20261115001','2026-11-15','陈静','1','admin','2026-11-15 09:00:00','月度成品抽盘'),(1002,'PD20261119001','2026-11-19','陈静','0','admin','2026-11-15 09:00:00','仓库全面盘点进行中');
/*!40000 ALTER TABLE `biz_check` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_check_item`
--

DROP TABLE IF EXISTS `biz_check_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_check_item` (
  `item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `check_id` bigint NOT NULL COMMENT '盘点ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `stock_type` varchar(10) COLLATE utf8mb4_general_ci NOT NULL COMMENT '库存类型',
  `book_qty` int DEFAULT '0' COMMENT '账面数量',
  `actual_qty` int DEFAULT '0' COMMENT '实盘数量',
  `diff_qty` int DEFAULT '0' COMMENT '差异',
  PRIMARY KEY (`item_id`),
  KEY `idx_ci_check` (`check_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1009 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='盘点明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_check_item`
--

LOCK TABLES `biz_check_item` WRITE;
/*!40000 ALTER TABLE `biz_check_item` DISABLE KEYS */;
INSERT INTO `biz_check_item` VALUES (1001,1001,1002,'FINISH',19,18,-1),(1002,1001,1101,'FINISH',4,4,0),(1003,1001,1302,'FINISH',10,11,1),(1004,1001,1201,'CUT',15,15,0),(1005,1002,1006,'FINISH',0,0,0),(1006,1002,1105,'CUT',20,0,-20),(1007,1002,1306,'FINISH',0,0,0),(1008,1002,1206,'CUT',15,0,-15);
/*!40000 ALTER TABLE `biz_check_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_color`
--

DROP TABLE IF EXISTS `biz_color`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_color` (
  `color_id` bigint NOT NULL AUTO_INCREMENT COMMENT '颜色ID',
  `cn_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '中文名',
  `en_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '英文名',
  `color_code` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '#000000' COMMENT '色卡/色值',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`color_id`),
  UNIQUE KEY `uk_color_cn` (`cn_name`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='颜色管理';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_color`
--

LOCK TABLES `biz_color` WRITE;
/*!40000 ALTER TABLE `biz_color` DISABLE KEYS */;
INSERT INTO `biz_color` VALUES (101,'黑色','Black','#000000',1,'0','admin','2026-11-01 09:00:00','',NULL,''),(102,'白色','White','#FFFFFF',2,'0','admin','2026-11-01 09:00:00','',NULL,''),(103,'藏青','Navy','#1F3A5F',3,'0','admin','2026-11-01 09:00:00','',NULL,''),(104,'酒红','Wine','#7B1F2B',4,'0','admin','2026-11-01 09:00:00','',NULL,''),(105,'米杏','Apricot','#E8DCCB',5,'0','admin','2026-11-01 09:00:00','',NULL,''),(106,'雾霾蓝','Haze Blue','#8FA8B8',6,'0','admin','2026-11-01 09:00:00','',NULL,''),(107,'卡其','Khaki','#C3A47B',7,'0','admin','2026-11-01 09:00:00','',NULL,''),(108,'浅灰','Light Grey','#C9CDD4',8,'0','admin','2026-11-01 09:00:00','',NULL,'');
/*!40000 ALTER TABLE `biz_color` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_cost`
--

DROP TABLE IF EXISTS `biz_cost`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_cost` (
  `cost_id` bigint NOT NULL AUTO_INCREMENT COMMENT '成本ID',
  `style_no` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '款号',
  `fabric_cost` decimal(10,2) DEFAULT '0.00' COMMENT '面料成本',
  `lining_cost` decimal(10,2) DEFAULT '0.00' COMMENT '里布成本',
  `accessory_cost` decimal(10,2) DEFAULT '0.00' COMMENT '辅料成本',
  `trim_cost` decimal(10,2) DEFAULT '0.00' COMMENT '配件成本',
  `labor_cost` decimal(10,2) DEFAULT '0.00' COMMENT '加工费',
  `pattern_cost` decimal(10,2) DEFAULT '0.00' COMMENT '打版费',
  `shoot_cost` decimal(10,2) DEFAULT '0.00' COMMENT '拍摄费',
  `operate_cost` decimal(10,2) DEFAULT '0.00' COMMENT '运营费',
  `first_freight` decimal(10,2) DEFAULT '0.00' COMMENT '头程运费',
  `last_freight` decimal(10,2) DEFAULT '0.00' COMMENT '尾程运费',
  `sea_freight` decimal(10,2) DEFAULT '0.00' COMMENT '海运费',
  `other_cost` decimal(10,2) DEFAULT '0.00' COMMENT '其他费用',
  `total_cost` decimal(10,2) DEFAULT '0.00' COMMENT '合计成本',
  `ex_factory_price` decimal(10,2) DEFAULT '0.00' COMMENT '出厂价',
  `suggest_price` decimal(10,2) DEFAULT '0.00' COMMENT '建议售价',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`cost_id`),
  UNIQUE KEY `uk_cost_style` (`style_no`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='成本核算';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_cost`
--

LOCK TABLES `biz_cost` WRITE;
/*!40000 ALTER TABLE `biz_cost` DISABLE KEYS */;
INSERT INTO `biz_cost` VALUES (100,'JT03',35.00,8.00,6.00,4.00,25.00,3.00,2.00,6.00,3.00,4.00,2.00,1.00,99.00,129.00,259.00,'admin','2026-11-06 11:00:00','admin','2026-11-21 13:47:20',''),(101,'JT04',42.00,0.00,5.00,3.00,22.00,3.00,2.00,5.00,2.00,3.00,1.00,1.00,89.00,109.00,219.00,'admin','2026-11-06 11:00:00','',NULL,''),(102,'JT07',24.00,0.00,3.00,2.00,15.00,2.00,1.00,4.00,1.00,2.00,1.00,0.00,55.00,79.00,159.00,'admin','2026-11-06 11:00:00','',NULL,''),(103,'MQ3001',30.00,0.00,4.00,3.00,20.00,3.00,2.00,6.00,2.00,3.00,1.00,1.00,75.00,89.00,189.00,'admin','2026-11-06 11:00:00','',NULL,''),(105,'JT06',22.00,0.00,3.00,2.00,12.00,2.00,1.00,3.00,1.00,2.00,1.00,0.00,49.00,59.00,129.00,'admin','2026-11-06 11:00:00','',NULL,'');
/*!40000 ALTER TABLE `biz_cost` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_cut_order`
--

DROP TABLE IF EXISTS `biz_cut_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_cut_order` (
  `cut_id` bigint NOT NULL AUTO_INCREMENT COMMENT '裁货ID',
  `cut_no` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '裁货单号',
  `style_id` bigint DEFAULT NULL COMMENT '款号ID',
  `style_no` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '货号',
  `cn_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '品名',
  `customer` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '客户',
  `fabric` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '面料',
  `lining` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '里布',
  `total_qty` int DEFAULT '0' COMMENT '总件数',
  `layer_count` int DEFAULT '0' COMMENT '层数',
  `cutter` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '裁工',
  `cut_date` date DEFAULT NULL COMMENT '裁货日期',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态(0待分扎 1已入库 2已完成)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`cut_id`),
  KEY `idx_cut_no` (`cut_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1010 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='裁货开单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_cut_order`
--

LOCK TABLES `biz_cut_order` WRITE;
/*!40000 ALTER TABLE `biz_cut_order` DISABLE KEYS */;
INSERT INTO `biz_cut_order` VALUES (1001,'CO20261113001',100,'JT03','卡扣束腰大摆裙','杭州四季青','雪纺印花','涤纶里布',40,20,'王芳','2026-11-13','2','admin','2026-11-13 08:30:00','',NULL,''),(1002,'CO20261116001',100,'JT03','卡扣束腰大摆裙','杭州四季青','雪纺印花','涤纶里布',120,40,'李强','2026-11-16','2','admin','2026-11-13 08:30:00','',NULL,''),(1003,'CO20261118001',101,'JT04','原布连衣裙','上海衣品','真丝雪纺','涤纶里布',60,20,'王芳','2026-11-18','1','admin','2026-11-13 08:30:00','',NULL,''),(1004,'CO20261120001',103,'MQ3001','法式泡泡袖上衣','北京尚品','真丝雪纺','',80,20,'李强','2026-11-20','0','admin','2026-11-13 08:30:00','',NULL,''),(1005,'CO20261121001',102,'JT07','原布半身裙','上海衣品','雪纺印花','',30,15,'李强','2026-11-21','1','admin','2026-11-13 08:30:00','',NULL,''),(1006,'CO20261121002',100,'JT03','卡扣束腰大摆裙','杭州四季青','雪纺印花','涤纶里布',20,10,'王芳','2026-11-21','0','admin','2026-11-13 08:30:00','',NULL,'');
/*!40000 ALTER TABLE `biz_cut_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_cut_order_item`
--

DROP TABLE IF EXISTS `biz_cut_order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_cut_order_item` (
  `item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `cut_id` bigint NOT NULL COMMENT '裁货ID',
  `layer_no` int DEFAULT '1' COMMENT '层次(铺布层)',
  `color` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '颜色',
  `size` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '尺码',
  `qty` int DEFAULT '0' COMMENT '数量',
  PRIMARY KEY (`item_id`),
  KEY `idx_coi_cut` (`cut_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1023 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='裁货单层次明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_cut_order_item`
--

LOCK TABLES `biz_cut_order_item` WRITE;
/*!40000 ALTER TABLE `biz_cut_order_item` DISABLE KEYS */;
INSERT INTO `biz_cut_order_item` VALUES (1001,1001,1,'黑色','S',20),(1002,1001,1,'白色','M',20),(1003,1002,1,'黑色','S',20),(1004,1002,1,'黑色','M',20),(1005,1002,1,'白色','M',20),(1006,1002,1,'白色','L',20),(1007,1002,1,'藏青','L',20),(1008,1002,1,'酒红','XL',20),(1009,1003,1,'米杏','S',20),(1010,1003,1,'米杏','M',20),(1011,1003,1,'雾霾蓝','M',20),(1012,1004,1,'白色','S',20),(1013,1004,1,'黑色','M',20),(1014,1004,1,'浅灰','L',20),(1015,1004,1,'白色','XL',20),(1016,1005,1,'米杏','S',15),(1017,1005,1,'雾霾蓝','L',15),(1018,1006,1,'藏青','M',10),(1019,1006,1,'酒红','M',10);
/*!40000 ALTER TABLE `biz_cut_order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_defect`
--

DROP TABLE IF EXISTS `biz_defect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_defect` (
  `defect_id` bigint NOT NULL AUTO_INCREMENT COMMENT '次品ID',
  `bundle_id` bigint DEFAULT NULL COMMENT '扎ID',
  `bundle_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '扎号',
  `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID',
  `qty` int DEFAULT '0' COMMENT '数量',
  `defect_type` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '修理' COMMENT '类型(补片/报废/返修)',
  `handler` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '处理人',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`defect_id`),
  KEY `idx_defect_time` (`handle_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1006 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='次品处理';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_defect`
--

LOCK TABLES `biz_defect` WRITE;
/*!40000 ALTER TABLE `biz_defect` DISABLE KEYS */;
INSERT INTO `biz_defect` VALUES (1001,1009,'BN20261116009',1006,1,'报废','王检','2026-11-18 18:20:00','检验判定报废'),(1003,1004,'BN20261113004',1006,2,'补片','王检','2026-11-19 11:00:00','裁片破损补片'),(1004,1006,'BN20261116006',1001,1,'返修','赵丽','2026-11-19 16:30:00','袖口返修');
/*!40000 ALTER TABLE `biz_defect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_delivery`
--

DROP TABLE IF EXISTS `biz_delivery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_delivery` (
  `delivery_id` bigint NOT NULL AUTO_INCREMENT COMMENT '交单ID',
  `bundle_id` bigint NOT NULL COMMENT '扎ID',
  `bundle_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '扎号',
  `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID',
  `qty` int DEFAULT '0' COMMENT '本次完工数',
  `defect_qty` int DEFAULT '0' COMMENT '次品数',
  `employee` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '员工',
  `delivery_time` datetime DEFAULT NULL COMMENT '交货时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`delivery_id`),
  KEY `idx_delivery_bundle` (`bundle_id`),
  KEY `idx_delivery_time` (`delivery_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1017 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='完工交后道';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_delivery`
--

LOCK TABLES `biz_delivery` WRITE;
/*!40000 ALTER TABLE `biz_delivery` DISABLE KEYS */;
INSERT INTO `biz_delivery` VALUES (1001,1001,'BN20261113001',1001,6,0,'赵丽','2026-11-14 18:20:00',''),(1002,1002,'BN20261113002',1001,10,0,'赵丽','2026-11-14 18:20:00',''),(1003,1003,'BN20261113003',1006,6,0,'赵丽','2026-11-14 18:20:00',''),(1004,1004,'BN20261113004',1006,10,0,'赵丽','2026-11-14 18:20:00',''),(1005,1005,'BN20261116005',1001,10,0,'钱峰','2026-11-18 18:20:00',''),(1006,1006,'BN20261116006',1001,10,0,'钱峰','2026-11-18 18:20:00',''),(1007,1007,'BN20261116007',1002,10,0,'钱峰','2026-11-18 18:20:00',''),(1008,1008,'BN20261116008',1002,10,0,'钱峰','2026-11-18 18:20:00',''),(1009,1009,'BN20261116009',1006,10,1,'钱峰','2026-11-18 18:20:00',''),(1010,1010,'BN20261116010',1006,10,0,'钱峰','2026-11-18 18:20:00',''),(1011,1011,'BN20261116011',1007,10,0,'钱峰','2026-11-18 18:20:00',''),(1012,1012,'BN20261116012',1007,10,0,'钱峰','2026-11-18 18:20:00',''),(1013,1013,'BN20261116013',1011,10,0,'钱峰','2026-11-18 18:20:00',''),(1014,1014,'BN20261116014',1011,10,0,'钱峰','2026-11-18 18:20:00',''),(1015,1015,'BN20261116015',1016,10,0,'钱峰','2026-11-18 18:20:00',''),(1016,1016,'BN20261116016',1016,10,0,'钱峰','2026-11-18 18:20:00','');
/*!40000 ALTER TABLE `biz_delivery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_employee`
--

DROP TABLE IF EXISTS `biz_employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_employee` (
  `employee_id` bigint NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
  `position` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '岗位/工种',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '在职状态（0在职 1离职）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='员工档案';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_employee`
--

LOCK TABLES `biz_employee` WRITE;
/*!40000 ALTER TABLE `biz_employee` DISABLE KEYS */;
INSERT INTO `biz_employee` VALUES (1,'钱峰','车间-车工',9,'0','admin','2026-11-21 14:40:52','','0000-00-00 00:00:00',NULL),(2,'赵丽','车间-车工',10,'0','admin','2026-11-21 14:40:52','','0000-00-00 00:00:00',NULL),(3,'殷杰','车间-车工',11,'0','admin','2026-11-21 14:40:52','','0000-00-00 00:00:00',NULL),(4,'孙梅','后道-包装工',13,'0','admin','2026-11-21 14:40:52','','0000-00-00 00:00:00',NULL),(5,'王芳','仓库-仓管',14,'0','admin','2026-11-21 14:40:52','','0000-00-00 00:00:00',NULL),(6,'陈静','仓库-仓管',15,'0','admin','2026-11-21 14:40:52','','0000-00-00 00:00:00',NULL),(7,'老周','裁床-裁工',6,'0','admin','2026-11-21 14:40:52','','0000-00-00 00:00:00',NULL),(8,'李梅','车间-组长',8,'0','admin','2026-11-21 14:40:52','','0000-00-00 00:00:00',NULL),(9,'王检','后道-检验',12,'0','admin','2026-11-21 14:40:52','','0000-00-00 00:00:00',NULL),(10,'小陈','仓库-仓管',16,'0','admin','2026-11-21 14:40:52','','0000-00-00 00:00:00',NULL),(11,'小刘','财务-会计',17,'0','admin','2026-11-21 14:40:52','','0000-00-00 00:00:00',NULL),(12,'张敏','设计部-主设计师',1,'0','sysadmin','2026-11-21 15:06:39','',NULL,'负责整体款式开发'),(13,'李婷','设计部-设计师',2,'0','sysadmin','2026-11-21 15:06:39','',NULL,'负责女装连衣裙'),(14,'王倩','设计部-设计师',3,'0','sysadmin','2026-11-21 15:06:39','',NULL,'负责风衣与针织'),(15,'周敏','采购部-采购主管',4,'0','sysadmin','2026-11-21 15:06:39','',NULL,'负责供应商与对账'),(16,'吴磊','采购部-采购员',5,'0','sysadmin','2026-11-21 15:06:39','',NULL,'负责面料辅料采购'),(17,'李强','裁床-裁工',7,'0','sysadmin','2026-11-21 15:06:39','',NULL,'负责铺布裁剪');
/*!40000 ALTER TABLE `biz_employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_expense`
--

DROP TABLE IF EXISTS `biz_expense`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_expense` (
  `expense_id` bigint NOT NULL AUTO_INCREMENT COMMENT '费用ID',
  `expense_type` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '费用类型(海运费/头程/尾程/拍摄/运营/其他)',
  `style_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '款号',
  `amount` decimal(12,2) DEFAULT '0.00' COMMENT '金额',
  `expense_date` date DEFAULT NULL COMMENT '费用日期',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`expense_id`),
  KEY `idx_expense_date` (`expense_date`)
) ENGINE=InnoDB AUTO_INCREMENT=1008 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='费用登记';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_expense`
--

LOCK TABLES `biz_expense` WRITE;
/*!40000 ALTER TABLE `biz_expense` DISABLE KEYS */;
INSERT INTO `biz_expense` VALUES (1001,'海运费','JT03',2600.00,'2026-11-05','admin','2026-11-05 10:00:00','宁波港至洛杉矶整柜分摊'),(1002,'拍摄费','JT04',1200.00,'2026-11-08','admin','2026-11-05 10:00:00','秋冬款详情页拍摄'),(1003,'运营费','',3000.00,'2026-11-10','admin','2026-11-05 10:00:00','11 月店铺推广'),(1004,'头程运费','MQ3001',1800.00,'2026-11-12','admin','2026-11-05 10:00:00','工厂至宁波港'),(1005,'尾程运费','JT03',900.00,'2026-11-16','admin','2026-11-05 10:00:00','美国仓派送'),(1006,'其他','JT05',350.00,'2026-11-18','admin','2026-11-05 10:00:00','风衣面料打样'),(1007,'海运费','JT07',700.00,'2026-11-20','admin','2026-11-05 10:00:00','拼柜海运费');
/*!40000 ALTER TABLE `biz_expense` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_inventory`
--

DROP TABLE IF EXISTS `biz_inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_inventory` (
  `inventory_id` bigint NOT NULL AUTO_INCREMENT COMMENT '库存ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `stock_type` varchar(10) COLLATE utf8mb4_general_ci NOT NULL COMMENT '库存类型(CUT裁片/LINE产线/FINISH成品)',
  `qty` int DEFAULT '0' COMMENT '数量',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`inventory_id`),
  UNIQUE KEY `uk_inv` (`sku_id`,`stock_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1018 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='三色库存';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_inventory`
--

LOCK TABLES `biz_inventory` WRITE;
/*!40000 ALTER TABLE `biz_inventory` DISABLE KEYS */;
INSERT INTO `biz_inventory` VALUES (1001,1001,'LINE',10,'2026-11-21 08:00:00'),(1002,1001,'FINISH',30,'2026-11-21 10:56:39'),(1003,1002,'FINISH',29,'2026-11-21 11:34:32'),(1004,1006,'LINE',10,'2026-11-21 08:00:00'),(1005,1006,'FINISH',26,'2026-11-21 08:00:00'),(1006,1007,'FINISH',20,'2026-11-21 08:00:00'),(1007,1011,'FINISH',20,'2026-11-21 08:00:00'),(1008,1016,'FINISH',18,'2026-11-21 08:00:00'),(1009,1101,'CUT',20,'2026-11-21 08:00:00'),(1010,1101,'FINISH',-2,'2026-11-21 08:00:00'),(1011,1102,'CUT',20,'2026-11-21 08:00:00'),(1012,1105,'CUT',20,'2026-11-21 08:00:00'),(1013,1201,'CUT',15,'2026-11-21 08:00:00'),(1014,1206,'CUT',15,'2026-11-21 08:00:00'),(1015,1206,'FINISH',-6,'2026-11-21 08:00:00'),(1016,1302,'FINISH',-6,'2026-11-21 08:00:00'),(1017,1312,'FINISH',8,'2026-11-21 08:00:00');
/*!40000 ALTER TABLE `biz_inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_material`
--

DROP TABLE IF EXISTS `biz_material`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_material` (
  `material_id` bigint NOT NULL AUTO_INCREMENT COMMENT '物资ID',
  `material_code` varchar(32) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '物资编码',
  `material_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '物资名称',
  `spec` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '规格',
  `unit` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '单位',
  `material_type` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '辅料' COMMENT '物资类型',
  `stock_qty` decimal(14,2) DEFAULT '0.00' COMMENT '当前库存',
  `min_stock` decimal(14,2) DEFAULT '0.00' COMMENT '安全库存',
  `price` decimal(12,2) DEFAULT '0.00' COMMENT '单价',
  `supplier_id` bigint DEFAULT NULL COMMENT '常用供应商',
  `supplier_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '供应商名称',
  `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`material_id`),
  KEY `idx_name_spec` (`material_name`,`spec`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='物资档案与库存';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_material`
--

LOCK TABLES `biz_material` WRITE;
/*!40000 ALTER TABLE `biz_material` DISABLE KEYS */;
INSERT INTO `biz_material` VALUES (101,'WL101','雪纺印花','150D','米','面料',139.80,100.00,18.00,101,'杭州四季青面料商行','0','admin','2026-10-01 09:00:00','','2026-11-21 15:35:41'),(102,'WL102','真丝雪纺','16姆米','米','面料',12.88,60.00,52.00,101,'杭州四季青面料商行','0','admin','2026-10-01 09:00:00','',NULL),(103,'WL103','涤纶里布','210T','米','里布',57.18,80.00,6.00,101,'杭州四季青面料商行','0','admin','2026-10-01 09:00:00','',NULL),(104,'WL104','隐形拉链','25cm黑色','条','配件',190.00,200.00,3.50,103,'东莞恒信拉链厂','0','admin','2026-10-01 09:00:00','','2026-11-21 15:35:41'),(105,'WL105','树脂纽扣','12mm米白','颗','辅料',372.00,300.00,0.30,102,'义乌华美辅料有限公司','0','admin','2026-10-01 09:00:00','',NULL),(106,'WL106','松紧带','3cm','米','辅料',71.04,30.00,2.20,102,'义乌华美辅料有限公司','0','admin','2026-10-01 09:00:00','',NULL),(107,'WL107','纸箱','60x40x30','个','耗材',120.00,50.00,5.00,104,'苏州锦程包装材料厂','0','admin','2026-10-01 09:00:00','',NULL),(108,'WL108','裁床刀片','10片/盒','盒','耗材',12.00,5.00,12.00,105,'广州华兴制衣设备','0','admin','2026-10-01 09:00:00','',NULL),(109,'WL109','缝纫线','402涤纶线','卷','辅料',70.00,40.00,4.50,102,'义乌华美辅料有限公司','0','admin','2026-10-01 09:00:00','',NULL),(110,'WL110','包装袋','30x40cm','个','耗材',5800.00,200.00,0.15,104,'苏州锦程包装材料厂','0','admin','2026-10-01 09:00:00','',NULL),(111,'WL111','打印纸','A4 500张/包','箱','耗材',15.00,5.00,30.00,104,'苏州锦程包装材料厂','0','admin','2026-10-01 09:00:00','',NULL),(112,'WL112','裁床画粉','白色 20片/盒','盒','耗材',30.00,10.00,4.00,105,'广州华兴制衣设备','0','sysadmin','2026-11-21 15:06:39','',NULL),(113,'WL113','隔色纸','80g 100张/包','包','耗材',40.00,15.00,8.00,105,'广州华兴制衣设备','0','sysadmin','2026-11-21 15:06:39','',NULL),(114,'WL114','平缝机针','DB*1 11# 100支/盒','盒','耗材',30.00,20.00,25.00,105,'广州华兴制衣设备','0','sysadmin','2026-11-21 15:06:39','','2026-11-21 15:30:47'),(115,'WL115','吊牌','主吊牌 5x9cm','张','辅料',0.00,500.00,0.20,104,'苏州锦程包装材料厂','0','sysadmin','2026-11-21 15:06:39','','2026-11-21 15:29:45'),(116,'WL116','洗水标','缎面 3x5cm','张','辅料',0.00,500.00,0.15,104,'苏州锦程包装材料厂','0','sysadmin','2026-11-21 15:06:39','',NULL);
/*!40000 ALTER TABLE `biz_material` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_material_log`
--

DROP TABLE IF EXISTS `biz_material_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_material_log` (
  `log_id` bigint NOT NULL AUTO_INCREMENT,
  `material_id` bigint NOT NULL,
  `material_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '',
  `spec` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '',
  `unit` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '',
  `change_qty` decimal(14,2) DEFAULT '0.00' COMMENT '变动数量，正入负出',
  `before_qty` decimal(14,2) DEFAULT '0.00',
  `after_qty` decimal(14,2) DEFAULT '0.00',
  `biz_type` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '采购入库/生产领料/领料退回/盘点调整',
  `biz_no` varchar(40) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '关联单号',
  `operator` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `operate_time` datetime DEFAULT NULL,
  `remark` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`log_id`),
  KEY `idx_material` (`material_id`),
  KEY `idx_biz_no` (`biz_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1049 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='物资出入库流水';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_material_log`
--

LOCK TABLES `biz_material_log` WRITE;
/*!40000 ALTER TABLE `biz_material_log` DISABLE KEYS */;
INSERT INTO `biz_material_log` VALUES (1001,101,'雪纺印花','150D','米',360.00,0.00,360.00,'采购入库','OPEN20261001','admin','2026-10-01 09:00:00','期初建账'),(1002,101,'雪纺印花','150D','米',-67.20,360.00,292.80,'生产领料','CO20261113001','system','2026-11-13 08:20:00','按BOM扣减 JT03'),(1003,101,'雪纺印花','150D','米',120.00,292.80,412.80,'采购入库','CG20261115001','admin','2026-11-15 10:00:00','供应商：杭州四季青面料商行'),(1004,101,'雪纺印花','150D','米',-201.60,412.80,211.20,'生产领料','CO20261116001','system','2026-11-16 08:20:00','按BOM扣减 JT03'),(1005,101,'雪纺印花','150D','米',-37.80,211.20,173.40,'生产领料','CO20261121001','system','2026-11-21 08:20:00','按BOM扣减 JT07'),(1006,101,'雪纺印花','150D','米',-33.60,173.40,139.80,'生产领料','CO20261121002','system','2026-11-21 08:20:00','按BOM扣减 JT03'),(1007,102,'真丝雪纺','16姆米','米',100.00,0.00,100.00,'采购入库','OPEN20261001','admin','2026-10-01 09:00:00','期初建账'),(1008,102,'真丝雪纺','16姆米','米',150.00,100.00,250.00,'采购入库','CG20261115001','admin','2026-11-15 10:00:00','供应商：杭州四季青面料商行'),(1009,102,'真丝雪纺','16姆米','米',-112.32,250.00,137.68,'生产领料','CO20261118001','system','2026-11-18 08:20:00','按BOM扣减 JT04'),(1010,102,'真丝雪纺','16姆米','米',-124.80,137.68,12.88,'生产领料','CO20261120001','system','2026-11-20 08:20:00','按BOM扣减 MQ3001'),(1011,103,'涤纶里布','210T','米',260.00,0.00,260.00,'采购入库','OPEN20261001','admin','2026-10-01 09:00:00','期初建账'),(1012,103,'涤纶里布','210T','米',-49.44,260.00,210.56,'生产领料','CO20261113001','system','2026-11-13 08:20:00','按BOM扣减 JT03'),(1013,103,'涤纶里布','210T','米',100.00,210.56,310.56,'采购入库','CG20261115001','admin','2026-11-15 10:00:00','供应商：杭州四季青面料商行'),(1014,103,'涤纶里布','210T','米',-148.32,310.56,162.24,'生产领料','CO20261116001','system','2026-11-16 08:20:00','按BOM扣减 JT03'),(1015,103,'涤纶里布','210T','米',-80.34,162.24,81.90,'生产领料','CO20261118001','system','2026-11-18 08:20:00','按BOM扣减 JT04'),(1016,103,'涤纶里布','210T','米',-24.72,81.90,57.18,'生产领料','CO20261121002','system','2026-11-21 08:20:00','按BOM扣减 JT03'),(1017,104,'隐形拉链','25cm黑色','条',400.00,0.00,400.00,'采购入库','OPEN20261001','admin','2026-10-01 09:00:00','期初建账'),(1018,104,'隐形拉链','25cm黑色','条',-40.00,400.00,360.00,'生产领料','CO20261113001','system','2026-11-13 08:20:00','按BOM扣减 JT03'),(1019,104,'隐形拉链','25cm黑色','条',-120.00,360.00,240.00,'生产领料','CO20261116001','system','2026-11-16 08:20:00','按BOM扣减 JT03'),(1020,104,'隐形拉链','25cm黑色','条',-30.00,240.00,210.00,'生产领料','CO20261121001','system','2026-11-21 08:20:00','按BOM扣减 JT07'),(1021,104,'隐形拉链','25cm黑色','条',-20.00,210.00,190.00,'生产领料','CO20261121002','system','2026-11-21 08:20:00','按BOM扣减 JT03'),(1022,105,'树脂纽扣','12mm米白','颗',1800.00,0.00,1800.00,'采购入库','OPEN20261001','admin','2026-10-01 09:00:00','期初建账'),(1023,105,'树脂纽扣','12mm米白','颗',-244.80,1800.00,1555.20,'生产领料','CO20261113001','system','2026-11-13 08:20:00','按BOM扣减 JT03'),(1024,105,'树脂纽扣','12mm米白','颗',-734.40,1555.20,820.80,'生产领料','CO20261116001','system','2026-11-16 08:20:00','按BOM扣减 JT03'),(1025,105,'树脂纽扣','12mm米白','颗',-326.40,820.80,494.40,'生产领料','CO20261120001','system','2026-11-20 08:20:00','按BOM扣减 MQ3001'),(1026,105,'树脂纽扣','12mm米白','颗',-122.40,494.40,372.00,'生产领料','CO20261121002','system','2026-11-21 08:20:00','按BOM扣减 JT03'),(1027,106,'松紧带','3cm','米',120.00,0.00,120.00,'采购入库','OPEN20261001','admin','2026-10-01 09:00:00','期初建账'),(1028,106,'松紧带','3cm','米',-48.96,120.00,71.04,'生产领料','CO20261118001','system','2026-11-18 08:20:00','按BOM扣减 JT04'),(1029,107,'纸箱','60x40x30','个',20.00,0.00,20.00,'采购入库','OPEN20261001','admin','2026-10-01 09:00:00','期初建账'),(1030,107,'纸箱','60x40x30','个',100.00,20.00,120.00,'采购入库','CG20261112001','admin','2026-11-12 10:00:00','供应商：苏州锦程包装材料厂'),(1031,108,'裁床刀片','10片/盒','盒',0.00,0.00,0.00,'采购入库','OPEN20261001','admin','2026-10-01 09:00:00','期初建账'),(1032,108,'裁床刀片','10片/盒','盒',10.00,0.00,10.00,'采购入库','CG20261112001','admin','2026-11-12 10:00:00','供应商：苏州锦程包装材料厂'),(1033,108,'裁床刀片','10片/盒','盒',-12.00,10.00,-2.00,'生产领料','LL20261120001','老周','2026-11-20 08:00:00','裁床日常领用；超领原因：采购在途，紧急插单'),(1034,109,'缝纫线','402涤纶线','卷',80.00,0.00,80.00,'采购入库','OPEN20261001','admin','2026-10-01 09:00:00','期初建账'),(1035,109,'缝纫线','402涤纶线','卷',-16.16,80.00,63.84,'生产领料','CO20261120001','system','2026-11-20 08:20:00','按BOM扣减 MQ3001'),(1036,110,'包装袋','30x40cm','个',800.00,0.00,800.00,'采购入库','OPEN20261001','admin','2026-10-01 09:00:00','期初建账'),(1037,110,'包装袋','30x40cm','个',5000.00,800.00,5800.00,'采购入库','CG20261112001','admin','2026-11-12 10:00:00','供应商：苏州锦程包装材料厂'),(1038,111,'打印纸','A4 500张/包','箱',5.00,0.00,5.00,'采购入库','OPEN20261001','admin','2026-10-01 09:00:00','期初建账'),(1039,111,'打印纸','A4 500张/包','箱',10.00,5.00,15.00,'采购入库','CG20261110001','admin','2026-11-10 10:00:00','供应商：苏州锦程包装材料厂'),(1040,112,'裁床画粉','白色 20片/盒','盒',30.00,0.00,30.00,'采购入库','CG20261106001','吴磊','2026-11-06 10:05:00','裁床画粉入库'),(1041,113,'隔色纸','80g 100张/包','包',40.00,0.00,40.00,'采购入库','CG20261106001','吴磊','2026-11-06 10:08:00','隔色纸入库'),(1042,114,'平缝机针','DB*1 11# 100支/盒','盒',5.00,0.00,5.00,'采购入库','CG20261114001','吴磊','2026-11-16 09:30:00','机针首批到货入库'),(1043,109,'缝纫线','402涤纶线','卷',6.16,63.84,70.00,'领料退回','LLT20261120001','李梅','2026-11-20 17:20:00','车间未用完缝纫线退回仓库'),(1045,114,'平缝机针','DB*1 11# 100支/盒','盒',25.00,5.00,30.00,'手工调整','ADJ20261121153047','warehouse','2026-11-21 15:30:47','线下补货入库'),(1046,108,'裁床刀片','10片/盒','盒',14.00,-2.00,12.00,'采购入库','CG20261121001','admin','2026-11-21 09:30:00','供应商：苏州锦程包装材料厂');
/*!40000 ALTER TABLE `biz_material_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_outbound`
--

DROP TABLE IF EXISTS `biz_outbound`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_outbound` (
  `outbound_id` bigint NOT NULL AUTO_INCREMENT COMMENT '出库ID',
  `outbound_no` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '出库单号',
  `customer` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '客户',
  `outbound_date` date DEFAULT NULL COMMENT '出库日期',
  `total_qty` int DEFAULT '0' COMMENT '总数量',
  `total_amount` decimal(12,2) DEFAULT '0.00' COMMENT '总金额',
  `operator` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作人',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态(0正常 1已作废)',
  `cancel_reason` varchar(200) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '作废原因',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`outbound_id`),
  KEY `idx_outbound_date` (`outbound_date`)
) ENGINE=InnoDB AUTO_INCREMENT=1005 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='成品出库单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_outbound`
--

LOCK TABLES `biz_outbound` WRITE;
/*!40000 ALTER TABLE `biz_outbound` DISABLE KEYS */;
INSERT INTO `biz_outbound` VALUES (1001,'CK20261114001','杭州四季青','2026-11-14',10,2590.00,'王芳','0','','','admin','2026-11-14 09:00:00','',NULL),(1002,'CK20261119001','上海衣品','2026-11-19',10,1830.00,'王芳','0','','','admin','2026-11-14 09:00:00','',NULL),(1003,'CK20261121001','北京尚品','2026-11-21',6,1134.00,'小陈','0','','','admin','2026-11-14 09:00:00','',NULL),(1004,'CK20261117001','杭州四季青','2026-11-17',2,518.00,'王芳','1','客户临时取消订单','','admin','2026-11-14 09:00:00','',NULL);
/*!40000 ALTER TABLE `biz_outbound` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_outbound_item`
--

DROP TABLE IF EXISTS `biz_outbound_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_outbound_item` (
  `item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `outbound_id` bigint NOT NULL COMMENT '出库ID',
  `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID',
  `style_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '款号',
  `color` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '颜色',
  `size` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '尺码',
  `qty` int DEFAULT '0' COMMENT '数量',
  `price` decimal(10,2) DEFAULT '0.00' COMMENT '单价',
  `amount` decimal(12,2) DEFAULT '0.00' COMMENT '金额',
  `box_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '箱号',
  PRIMARY KEY (`item_id`),
  KEY `idx_oi_outbound` (`outbound_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1008 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='出库明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_outbound_item`
--

LOCK TABLES `biz_outbound_item` WRITE;
/*!40000 ALTER TABLE `biz_outbound_item` DISABLE KEYS */;
INSERT INTO `biz_outbound_item` VALUES (1001,1001,1002,'JT03','黑色','M',4,259.00,1036.00,'BOX-01'),(1002,1001,1006,'JT03','白色','M',4,259.00,1036.00,'BOX-01'),(1003,1001,1016,'JT03','酒红','XL',2,259.00,518.00,'BOX-01'),(1004,1002,1101,'JT04','米杏','S',4,219.00,876.00,'BOX-02'),(1005,1002,1206,'JT07','雾霾蓝','L',6,159.00,954.00,'BOX-02'),(1006,1003,1302,'MQ3001','白色','M',6,189.00,1134.00,'BOX-03'),(1007,1004,1011,'JT03','藏青','L',2,259.00,518.00,'BOX-04');
/*!40000 ALTER TABLE `biz_outbound_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_pack`
--

DROP TABLE IF EXISTS `biz_pack`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_pack` (
  `pack_id` bigint NOT NULL AUTO_INCREMENT COMMENT '包装ID',
  `bundle_id` bigint DEFAULT NULL COMMENT '扎ID',
  `bundle_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '扎号',
  `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID',
  `qty` int DEFAULT '0' COMMENT '包装数量',
  `pack_method` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '袋装' COMMENT '包装方式',
  `per_bag` int DEFAULT '1' COMMENT '每袋件数',
  `packer` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '包装人',
  `pack_time` datetime DEFAULT NULL COMMENT '包装时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`pack_id`),
  KEY `idx_pack_time` (`pack_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1017 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='包装登记';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_pack`
--

LOCK TABLES `biz_pack` WRITE;
/*!40000 ALTER TABLE `biz_pack` DISABLE KEYS */;
INSERT INTO `biz_pack` VALUES (1001,1001,'BN20261113001',1001,5,'袋装',10,'孙梅','2026-11-19 10:30:00',''),(1002,1002,'BN20261113002',1001,5,'袋装',10,'孙梅','2026-11-19 10:30:00',''),(1003,1003,'BN20261113003',1006,5,'袋装',10,'孙梅','2026-11-19 10:30:00',''),(1004,1004,'BN20261113004',1006,5,'袋装',10,'孙梅','2026-11-19 10:30:00',''),(1005,1005,'BN20261116005',1001,10,'袋装',10,'孙梅','2026-11-19 15:00:00',''),(1006,1006,'BN20261116006',1001,10,'袋装',10,'孙梅','2026-11-19 15:00:00',''),(1007,1007,'BN20261116007',1002,10,'袋装',10,'孙梅','2026-11-19 15:00:00',''),(1008,1008,'BN20261116008',1002,10,'袋装',10,'孙梅','2026-11-19 15:00:00',''),(1009,1009,'BN20261116009',1006,10,'袋装',10,'孙梅','2026-11-19 15:00:00',''),(1010,1010,'BN20261116010',1006,10,'袋装',10,'孙梅','2026-11-19 15:00:00',''),(1011,1011,'BN20261116011',1007,10,'袋装',10,'孙梅','2026-11-19 15:00:00',''),(1012,1012,'BN20261116012',1007,10,'袋装',10,'孙梅','2026-11-19 15:00:00',''),(1013,1013,'BN20261116013',1011,10,'袋装',10,'孙梅','2026-11-19 15:00:00',''),(1014,1014,'BN20261116014',1011,10,'袋装',10,'孙梅','2026-11-19 15:00:00',''),(1015,1015,'BN20261116015',1016,10,'袋装',10,'孙梅','2026-11-19 15:00:00',''),(1016,1016,'BN20261116016',1016,10,'袋装',10,'孙梅','2026-11-19 15:00:00','');
/*!40000 ALTER TABLE `biz_pack` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_pick`
--

DROP TABLE IF EXISTS `biz_pick`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_pick` (
  `pick_id` bigint NOT NULL AUTO_INCREMENT COMMENT '领片ID',
  `bundle_id` bigint NOT NULL COMMENT '扎ID',
  `bundle_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '扎号',
  `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID',
  `style_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '款号',
  `color` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '颜色',
  `size` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '尺码',
  `qty` int DEFAULT '0' COMMENT '数量',
  `employee` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '员工',
  `pick_time` datetime DEFAULT NULL COMMENT '领片时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`pick_id`),
  KEY `idx_pick_bundle` (`bundle_id`),
  KEY `idx_pick_time` (`pick_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1017 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='车间领片记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_pick`
--

LOCK TABLES `biz_pick` WRITE;
/*!40000 ALTER TABLE `biz_pick` DISABLE KEYS */;
INSERT INTO `biz_pick` VALUES (1001,1001,'BN20261113001',1001,'JT03','黑色','S',10,'赵丽','2026-11-14 09:30:00',''),(1002,1002,'BN20261113002',1001,'JT03','黑色','S',10,'赵丽','2026-11-14 09:30:00',''),(1003,1003,'BN20261113003',1006,'JT03','白色','M',10,'赵丽','2026-11-14 09:30:00',''),(1004,1004,'BN20261113004',1006,'JT03','白色','M',10,'赵丽','2026-11-14 09:30:00',''),(1005,1005,'BN20261116005',1001,'JT03','黑色','S',10,'钱峰','2026-11-16 08:40:00',''),(1006,1006,'BN20261116006',1001,'JT03','黑色','S',10,'钱峰','2026-11-16 08:40:00',''),(1007,1007,'BN20261116007',1002,'JT03','黑色','M',10,'钱峰','2026-11-16 08:40:00',''),(1008,1008,'BN20261116008',1002,'JT03','黑色','M',10,'钱峰','2026-11-16 08:40:00',''),(1009,1009,'BN20261116009',1006,'JT03','白色','M',10,'钱峰','2026-11-16 08:40:00',''),(1010,1010,'BN20261116010',1006,'JT03','白色','M',10,'钱峰','2026-11-16 08:40:00',''),(1011,1011,'BN20261116011',1007,'JT03','白色','L',10,'钱峰','2026-11-16 08:40:00',''),(1012,1012,'BN20261116012',1007,'JT03','白色','L',10,'钱峰','2026-11-16 08:40:00',''),(1013,1013,'BN20261116013',1011,'JT03','藏青','L',10,'钱峰','2026-11-16 08:40:00',''),(1014,1014,'BN20261116014',1011,'JT03','藏青','L',10,'钱峰','2026-11-16 08:40:00',''),(1015,1015,'BN20261116015',1016,'JT03','酒红','XL',10,'钱峰','2026-11-16 08:40:00',''),(1016,1016,'BN20261116016',1016,'JT03','酒红','XL',10,'钱峰','2026-11-16 08:40:00','');
/*!40000 ALTER TABLE `biz_pick` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_purchase`
--

DROP TABLE IF EXISTS `biz_purchase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_purchase` (
  `purchase_id` bigint NOT NULL AUTO_INCREMENT COMMENT '采购ID',
  `purchase_no` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '采购单号',
  `purchase_type` varchar(20) COLLATE utf8mb4_general_ci DEFAULT 'COMMON' COMMENT '类型(COMMON公用耗材/MATERIAL按款物料)',
  `category` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '类别(办公室/裁床/仓库/车间/后道)',
  `style_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '款号',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `supplier_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '供应商名称',
  `purchase_date` date DEFAULT NULL COMMENT '采购日期',
  `total_amount` decimal(12,2) DEFAULT '0.00' COMMENT '采购金额',
  `paid_amount` decimal(12,2) DEFAULT '0.00' COMMENT '已付金额',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态(0待入库 1部分入库 2已入库)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`purchase_id`),
  KEY `idx_purchase_date` (`purchase_date`),
  KEY `idx_purchase_style` (`style_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1008 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='采购单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_purchase`
--

LOCK TABLES `biz_purchase` WRITE;
/*!40000 ALTER TABLE `biz_purchase` DISABLE KEYS */;
INSERT INTO `biz_purchase` VALUES (1001,'CG20261110001','COMMON','办公室','',104,'苏州锦程包装材料厂','2026-11-10',300.00,300.00,'2','admin','2026-11-10 09:00:00','',NULL,''),(1002,'CG20261112001','COMMON','仓库','',104,'苏州锦程包装材料厂','2026-11-12',1370.00,1370.00,'2','admin','2026-11-10 09:00:00','',NULL,''),(1003,'CG20261115001','MATERIAL','','JT03',101,'杭州四季青面料商行','2026-11-15',12000.00,6000.00,'1','admin','2026-11-10 09:00:00','',NULL,''),(1004,'CG20261118001','MATERIAL','','MQ3001',102,'义乌华美辅料有限公司','2026-11-18',1150.00,0.00,'0','admin','2026-11-10 09:00:00','',NULL,''),(1005,'CG20261106001','COMMON','裁床',NULL,105,'广州华兴制衣设备','2026-11-06',440.00,440.00,'2','purchaser01','2026-11-06 09:20:00','',NULL,'裁床用画粉、隔色纸，已全部入库'),(1006,'CG20261114001','COMMON','车间',NULL,105,'广州华兴制衣设备','2026-11-14',500.00,200.00,'1','purchaser01','2026-11-14 10:10:00','',NULL,'车间机针分批到货，余量待入库'),(1007,'CG20261119001','COMMON','后道',NULL,104,'苏州锦程包装材料厂','2026-11-19',275.00,0.00,'0','purchaser01','2026-11-19 15:40:00','',NULL,'后道吊牌、洗水标待收货');
/*!40000 ALTER TABLE `biz_purchase` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_purchase_item`
--

DROP TABLE IF EXISTS `biz_purchase_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_purchase_item` (
  `item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `purchase_id` bigint NOT NULL COMMENT '采购ID',
  `material_type` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '物料类型',
  `material_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `spec` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '规格',
  `unit` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '件' COMMENT '单位',
  `price` decimal(10,2) DEFAULT '0.00' COMMENT '单价',
  `qty` decimal(12,2) DEFAULT '0.00' COMMENT '数量',
  `received_qty` decimal(12,2) DEFAULT '0.00' COMMENT '已入库数量',
  `amount` decimal(12,2) DEFAULT '0.00' COMMENT '金额',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`item_id`),
  KEY `idx_pi_purchase` (`purchase_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1016 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='采购明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_purchase_item`
--

LOCK TABLES `biz_purchase_item` WRITE;
/*!40000 ALTER TABLE `biz_purchase_item` DISABLE KEYS */;
INSERT INTO `biz_purchase_item` VALUES (1001,1001,'耗材','打印纸','A4 500张/包','箱',30.00,10.00,10.00,300.00,''),(1002,1002,'耗材','纸箱','60x40x30','个',5.00,100.00,100.00,500.00,''),(1003,1002,'耗材','包装袋','30x40cm','个',0.15,5000.00,5000.00,750.00,''),(1004,1002,'耗材','裁床刀片','10片/盒','盒',12.00,10.00,10.00,120.00,''),(1005,1003,'面料','雪纺印花','150D','米',18.00,200.00,120.00,3600.00,''),(1006,1003,'里布','涤纶里布','210T','米',6.00,100.00,100.00,600.00,''),(1007,1003,'面料','真丝雪纺','16姆米','米',52.00,150.00,150.00,7800.00,''),(1008,1004,'辅料','树脂纽扣','12mm米白','颗',0.30,1000.00,0.00,300.00,''),(1009,1004,'辅料','松紧带','3cm','米',2.20,100.00,0.00,220.00,''),(1010,1004,'辅料','缝纫线','402涤纶线','卷',4.50,140.00,0.00,630.00,''),(1011,1005,'耗材','裁床画粉','白色 20片/盒','盒',4.00,30.00,30.00,120.00,NULL),(1012,1005,'耗材','隔色纸','80g 100张/包','包',8.00,40.00,40.00,320.00,NULL),(1013,1006,'耗材','平缝机针','DB*1 11# 100支/盒','盒',25.00,20.00,5.00,500.00,'首批到货 5 盒'),(1014,1007,'辅料','吊牌','主吊牌 5x9cm','张',0.20,1000.00,0.00,200.00,NULL),(1015,1007,'辅料','洗水标','缎面 3x5cm','张',0.15,500.00,0.00,75.00,NULL);
/*!40000 ALTER TABLE `biz_purchase_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_qc`
--

DROP TABLE IF EXISTS `biz_qc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_qc` (
  `qc_id` bigint NOT NULL AUTO_INCREMENT COMMENT '检验ID',
  `bundle_id` bigint DEFAULT NULL COMMENT '扎ID',
  `bundle_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '扎号',
  `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID',
  `check_qty` int DEFAULT '0' COMMENT '检验数',
  `qualified_qty` int DEFAULT '0' COMMENT '合格数',
  `defect_qty` int DEFAULT '0' COMMENT '次品数',
  `defect_reason` varchar(200) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '次品原因',
  `inspector` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '检验人',
  `check_time` datetime DEFAULT NULL COMMENT '检验时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`qc_id`),
  KEY `idx_qc_time` (`check_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1017 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='后道检验';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_qc`
--

LOCK TABLES `biz_qc` WRITE;
/*!40000 ALTER TABLE `biz_qc` DISABLE KEYS */;
INSERT INTO `biz_qc` VALUES (1001,1001,'BN20261113001',1001,6,6,0,'','王检','2026-11-14 18:20:00',''),(1002,1002,'BN20261113002',1001,10,10,0,'','王检','2026-11-14 18:20:00',''),(1003,1003,'BN20261113003',1006,6,6,0,'','王检','2026-11-14 18:20:00',''),(1004,1004,'BN20261113004',1006,10,10,0,'','王检','2026-11-14 18:20:00',''),(1005,1005,'BN20261116005',1001,10,10,0,'','王检','2026-11-18 18:20:00',''),(1006,1006,'BN20261116006',1001,10,10,0,'','王检','2026-11-18 18:20:00',''),(1007,1007,'BN20261116007',1002,10,10,0,'','王检','2026-11-18 18:20:00',''),(1008,1008,'BN20261116008',1002,10,10,0,'','王检','2026-11-18 18:20:00',''),(1009,1009,'BN20261116009',1006,10,9,1,'线头/跳线','王检','2026-11-18 18:20:00',''),(1010,1010,'BN20261116010',1006,10,10,0,'','王检','2026-11-18 18:20:00',''),(1011,1011,'BN20261116011',1007,10,10,0,'','王检','2026-11-18 18:20:00',''),(1012,1012,'BN20261116012',1007,10,10,0,'','王检','2026-11-18 18:20:00',''),(1013,1013,'BN20261116013',1011,10,10,0,'','王检','2026-11-18 18:20:00',''),(1014,1014,'BN20261116014',1011,10,10,0,'','王检','2026-11-18 18:20:00',''),(1015,1015,'BN20261116015',1016,10,10,0,'','王检','2026-11-18 18:20:00',''),(1016,1016,'BN20261116016',1016,10,10,0,'','王检','2026-11-18 18:20:00','');
/*!40000 ALTER TABLE `biz_qc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_return`
--

DROP TABLE IF EXISTS `biz_return`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_return` (
  `return_id` bigint NOT NULL AUTO_INCREMENT COMMENT '退货ID',
  `return_no` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '退货单号',
  `customer` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '客户',
  `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID',
  `style_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '款号',
  `color` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '颜色',
  `size` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '尺码',
  `qty` int DEFAULT '0' COMMENT '退货数量',
  `good_qty` int DEFAULT '0' COMMENT '好货数量',
  `bad_qty` int DEFAULT '0' COMMENT '次品数量',
  `reason` varchar(200) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '退货原因',
  `return_date` date DEFAULT NULL COMMENT '退货日期',
  `operator` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作人',
  `create_time` datetime DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`return_id`),
  KEY `idx_return_date` (`return_date`)
) ENGINE=InnoDB AUTO_INCREMENT=1004 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='退货入库';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_return`
--

LOCK TABLES `biz_return` WRITE;
/*!40000 ALTER TABLE `biz_return` DISABLE KEYS */;
INSERT INTO `biz_return` VALUES (1001,'TH20261118001','杭州四季青',1002,'JT03','黑色','M',4,3,1,'尺码偏小','2026-11-18','陈静','2026-11-18 10:00:00',''),(1002,'TH20261120001','上海衣品',1101,'JT04','米杏','S',2,2,0,'客户取消','2026-11-20','陈静','2026-11-18 10:00:00','');
/*!40000 ALTER TABLE `biz_return` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_shipment`
--

DROP TABLE IF EXISTS `biz_shipment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_shipment` (
  `ship_id` bigint NOT NULL AUTO_INCREMENT COMMENT '出货ID',
  `ship_date` date DEFAULT NULL COMMENT '出货日期',
  `customer` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '客户',
  `style_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '款式',
  `color` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '颜色',
  `size` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '尺码',
  `qty` int DEFAULT '0' COMMENT '数量',
  `price` decimal(10,2) DEFAULT '0.00' COMMENT '单价',
  `amount` decimal(12,2) DEFAULT '0.00' COMMENT '金额',
  `sea_freight` decimal(12,2) DEFAULT '0.00' COMMENT '海运费',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  `outbound_id` bigint DEFAULT NULL COMMENT '关联成品出库单ID',
  PRIMARY KEY (`ship_id`),
  KEY `idx_ship_date` (`ship_date`)
) ENGINE=InnoDB AUTO_INCREMENT=1007 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='出货表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_shipment`
--

LOCK TABLES `biz_shipment` WRITE;
/*!40000 ALTER TABLE `biz_shipment` DISABLE KEYS */;
INSERT INTO `biz_shipment` VALUES (1001,'2026-11-14','杭州四季青','JT03','黑色','M',4,259.00,1036.00,300.00,'admin','2026-11-14 09:10:00','',NULL,'',1001),(1002,'2026-11-14','杭州四季青','JT03','白色','M',4,259.00,1036.00,300.00,'admin','2026-11-14 09:10:00','',NULL,'',1001),(1003,'2026-11-14','杭州四季青','JT03','酒红','XL',2,259.00,518.00,200.00,'admin','2026-11-14 09:10:00','',NULL,'',1001),(1004,'2026-11-19','上海衣品','JT04','米杏','S',4,219.00,876.00,200.00,'admin','2026-11-14 09:10:00','',NULL,'',1002),(1005,'2026-11-19','上海衣品','JT07','雾霾蓝','L',6,159.00,954.00,200.00,'admin','2026-11-14 09:10:00','',NULL,'',1002),(1006,'2026-11-21','北京尚品','MQ3001','白色','M',6,189.00,1134.00,150.00,'admin','2026-11-14 09:10:00','',NULL,'',1003);
/*!40000 ALTER TABLE `biz_shipment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_size`
--

DROP TABLE IF EXISTS `biz_size`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_size` (
  `size_id` bigint NOT NULL AUTO_INCREMENT COMMENT '尺码ID',
  `size_name` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '尺码',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`size_id`),
  UNIQUE KEY `uk_size_name` (`size_name`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='尺码表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_size`
--

LOCK TABLES `biz_size` WRITE;
/*!40000 ALTER TABLE `biz_size` DISABLE KEYS */;
INSERT INTO `biz_size` VALUES (101,'XS',1,'0','admin','2026-11-01 09:00:00','',NULL,''),(102,'S',2,'0','admin','2026-11-01 09:00:00','',NULL,''),(103,'M',3,'0','admin','2026-11-01 09:00:00','',NULL,''),(104,'L',4,'0','admin','2026-11-01 09:00:00','',NULL,''),(105,'XL',5,'0','admin','2026-11-01 09:00:00','',NULL,''),(106,'XXL',6,'0','admin','2026-11-01 09:00:00','',NULL,''),(107,'均码',7,'0','admin','2026-11-01 09:00:00','',NULL,'');
/*!40000 ALTER TABLE `biz_size` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_sku`
--

DROP TABLE IF EXISTS `biz_sku`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_sku` (
  `sku_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
  `style_id` bigint DEFAULT NULL COMMENT '款号ID',
  `style_no` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '款号',
  `parent_style_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '父体款号',
  `cn_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '中文品名',
  `color` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '颜色',
  `size` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '尺码',
  `barcode` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品条码(款号-颜色-尺码)',
  `cost_price` decimal(10,2) DEFAULT '0.00' COMMENT '成本价',
  `sale_price` decimal(10,2) DEFAULT '0.00' COMMENT '销售价',
  `min_stock` int DEFAULT '0' COMMENT '最低库存预警',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`sku_id`),
  UNIQUE KEY `uk_sku` (`style_no`,`color`,`size`),
  UNIQUE KEY `uk_barcode` (`barcode`)
) ENGINE=InnoDB AUTO_INCREMENT=1402 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品SKU';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_sku`
--

LOCK TABLES `biz_sku` WRITE;
/*!40000 ALTER TABLE `biz_sku` DISABLE KEYS */;
INSERT INTO `biz_sku` VALUES (1001,100,'JT03','JT03','卡扣束腰大摆裙','黑色','S','JT03-黑色-S',129.00,259.00,0,'0','admin','2026-11-05 10:00:00','','2026-11-21 13:47:20',''),(1002,100,'JT03','JT03','卡扣束腰大摆裙','黑色','M','JT03-黑色-M',129.00,259.00,0,'0','admin','2026-11-05 10:00:00','','2026-11-21 13:47:20',''),(1003,100,'JT03','JT03','卡扣束腰大摆裙','黑色','L','JT03-黑色-L',129.00,259.00,0,'0','admin','2026-11-05 10:00:00','','2026-11-21 13:47:20',''),(1004,100,'JT03','JT03','卡扣束腰大摆裙','黑色','XL','JT03-黑色-XL',129.00,259.00,0,'0','admin','2026-11-05 10:00:00','','2026-11-21 13:47:20',''),(1005,100,'JT03','JT03','卡扣束腰大摆裙','白色','S','JT03-白色-S',129.00,259.00,0,'0','admin','2026-11-05 10:00:00','','2026-11-21 13:47:20',''),(1006,100,'JT03','JT03','卡扣束腰大摆裙','白色','M','JT03-白色-M',129.00,259.00,0,'0','admin','2026-11-05 10:00:00','','2026-11-21 13:47:20',''),(1007,100,'JT03','JT03','卡扣束腰大摆裙','白色','L','JT03-白色-L',129.00,259.00,0,'0','admin','2026-11-05 10:00:00','','2026-11-21 13:47:20',''),(1008,100,'JT03','JT03','卡扣束腰大摆裙','白色','XL','JT03-白色-XL',129.00,259.00,0,'0','admin','2026-11-05 10:00:00','','2026-11-21 13:47:20',''),(1009,100,'JT03','JT03','卡扣束腰大摆裙','藏青','S','JT03-藏青-S',129.00,259.00,0,'0','admin','2026-11-05 10:00:00','','2026-11-21 13:47:20',''),(1010,100,'JT03','JT03','卡扣束腰大摆裙','藏青','M','JT03-藏青-M',129.00,259.00,0,'0','admin','2026-11-05 10:00:00','','2026-11-21 13:47:20',''),(1011,100,'JT03','JT03','卡扣束腰大摆裙','藏青','L','JT03-藏青-L',129.00,259.00,0,'0','admin','2026-11-05 10:00:00','','2026-11-21 13:47:20',''),(1012,100,'JT03','JT03','卡扣束腰大摆裙','藏青','XL','JT03-藏青-XL',129.00,259.00,0,'0','admin','2026-11-05 10:00:00','','2026-11-21 13:47:20',''),(1013,100,'JT03','JT03','卡扣束腰大摆裙','酒红','S','JT03-酒红-S',129.00,259.00,0,'0','admin','2026-11-05 10:00:00','','2026-11-21 13:47:20',''),(1014,100,'JT03','JT03','卡扣束腰大摆裙','酒红','M','JT03-酒红-M',129.00,259.00,0,'0','admin','2026-11-05 10:00:00','','2026-11-21 13:47:20',''),(1015,100,'JT03','JT03','卡扣束腰大摆裙','酒红','L','JT03-酒红-L',129.00,259.00,0,'0','admin','2026-11-05 10:00:00','','2026-11-21 13:47:20',''),(1016,100,'JT03','JT03','卡扣束腰大摆裙','酒红','XL','JT03-酒红-XL',129.00,259.00,25,'0','admin','2026-11-05 10:00:00','','2026-11-21 13:47:20',''),(1101,101,'JT04','SP1001','原布连衣裙','米杏','S','JT04-米杏-S',109.00,219.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1102,101,'JT04','SP1001','原布连衣裙','米杏','M','JT04-米杏-M',109.00,219.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1103,101,'JT04','SP1001','原布连衣裙','米杏','L','JT04-米杏-L',109.00,219.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1104,101,'JT04','SP1001','原布连衣裙','雾霾蓝','S','JT04-雾霾蓝-S',109.00,219.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1105,101,'JT04','SP1001','原布连衣裙','雾霾蓝','M','JT04-雾霾蓝-M',109.00,219.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1106,101,'JT04','SP1001','原布连衣裙','雾霾蓝','L','JT04-雾霾蓝-L',109.00,219.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1201,102,'JT07','SP1001','原布半身裙','米杏','S','JT07-米杏-S',79.00,159.00,10,'0','admin','2026-11-05 10:00:00','',NULL,''),(1202,102,'JT07','SP1001','原布半身裙','米杏','M','JT07-米杏-M',79.00,159.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1203,102,'JT07','SP1001','原布半身裙','米杏','L','JT07-米杏-L',79.00,159.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1204,102,'JT07','SP1001','原布半身裙','雾霾蓝','S','JT07-雾霾蓝-S',79.00,159.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1205,102,'JT07','SP1001','原布半身裙','雾霾蓝','M','JT07-雾霾蓝-M',79.00,159.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1206,102,'JT07','SP1001','原布半身裙','雾霾蓝','L','JT07-雾霾蓝-L',79.00,159.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1301,103,'MQ3001','MQ3001','法式泡泡袖上衣','白色','S','MQ3001-白色-S',89.00,189.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1302,103,'MQ3001','MQ3001','法式泡泡袖上衣','白色','M','MQ3001-白色-M',89.00,189.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1303,103,'MQ3001','MQ3001','法式泡泡袖上衣','白色','L','MQ3001-白色-L',89.00,189.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1304,103,'MQ3001','MQ3001','法式泡泡袖上衣','白色','XL','MQ3001-白色-XL',89.00,189.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1305,103,'MQ3001','MQ3001','法式泡泡袖上衣','黑色','S','MQ3001-黑色-S',89.00,189.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1306,103,'MQ3001','MQ3001','法式泡泡袖上衣','黑色','M','MQ3001-黑色-M',89.00,189.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1307,103,'MQ3001','MQ3001','法式泡泡袖上衣','黑色','L','MQ3001-黑色-L',89.00,189.00,10,'0','admin','2026-11-05 10:00:00','',NULL,''),(1308,103,'MQ3001','MQ3001','法式泡泡袖上衣','黑色','XL','MQ3001-黑色-XL',89.00,189.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1309,103,'MQ3001','MQ3001','法式泡泡袖上衣','浅灰','S','MQ3001-浅灰-S',89.00,189.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1310,103,'MQ3001','MQ3001','法式泡泡袖上衣','浅灰','M','MQ3001-浅灰-M',89.00,189.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1311,103,'MQ3001','MQ3001','法式泡泡袖上衣','浅灰','L','MQ3001-浅灰-L',89.00,189.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1312,103,'MQ3001','MQ3001','法式泡泡袖上衣','浅灰','XL','MQ3001-浅灰-XL',89.00,189.00,0,'0','admin','2026-11-05 10:00:00','',NULL,''),(1401,105,'JT06','JT06','基础针织衫','卡其','均码','JT06-卡其-均码',59.00,129.00,10,'1','admin','2026-11-05 10:00:00','',NULL,'');
/*!40000 ALTER TABLE `biz_sku` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_stock_log`
--

DROP TABLE IF EXISTS `biz_stock_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_stock_log` (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID',
  `style_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '款号',
  `cn_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '中文品名',
  `color` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '颜色',
  `size` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '尺码',
  `stock_type` varchar(10) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '库存类型(CUT/LINE/FINISH)',
  `change_qty` int DEFAULT '0' COMMENT '变动数量(正入负出)',
  `before_qty` int DEFAULT '0' COMMENT '变动前',
  `after_qty` int DEFAULT '0' COMMENT '变动后',
  `biz_type` varchar(30) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '业务类型',
  `biz_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '业务单号',
  `bundle_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '扎号',
  `operator` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作人',
  `operate_time` datetime DEFAULT NULL COMMENT '操作时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`log_id`),
  KEY `idx_log_sku` (`sku_id`),
  KEY `idx_log_time` (`operate_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='库存流水';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_stock_log`
--

LOCK TABLES `biz_stock_log` WRITE;
/*!40000 ALTER TABLE `biz_stock_log` DISABLE KEYS */;
INSERT INTO `biz_stock_log` VALUES (1001,1001,'JT03','卡扣束腰大摆裙','黑色','S','CUT',10,0,10,'裁片入库','CO20261113001','BN20261113001','system','2026-11-13 09:00:00','分扎入库'),(1002,1001,'JT03','卡扣束腰大摆裙','黑色','S','CUT',10,10,20,'裁片入库','CO20261113001','BN20261113002','system','2026-11-13 09:00:00','分扎入库'),(1003,1001,'JT03','卡扣束腰大摆裙','黑色','S','CUT',-10,20,10,'车间领片','CO20261113001','BN20261113001','赵丽','2026-11-14 09:30:00','车间扫码领片'),(1004,1001,'JT03','卡扣束腰大摆裙','黑色','S','LINE',10,0,10,'车间领片','CO20261113001','BN20261113001','赵丽','2026-11-14 09:30:00','车间扫码领片'),(1005,1001,'JT03','卡扣束腰大摆裙','黑色','S','CUT',-10,10,0,'车间领片','CO20261113001','BN20261113002','赵丽','2026-11-14 09:30:00','车间扫码领片'),(1006,1001,'JT03','卡扣束腰大摆裙','黑色','S','LINE',10,10,20,'车间领片','CO20261113001','BN20261113002','赵丽','2026-11-14 09:30:00','车间扫码领片'),(1007,1001,'JT03','卡扣束腰大摆裙','黑色','S','CUT',-10,0,-10,'车间领片','CO20261116001','BN20261116005','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1008,1001,'JT03','卡扣束腰大摆裙','黑色','S','LINE',10,20,30,'车间领片','CO20261116001','BN20261116005','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1009,1001,'JT03','卡扣束腰大摆裙','黑色','S','CUT',-10,-10,-20,'车间领片','CO20261116001','BN20261116006','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1010,1001,'JT03','卡扣束腰大摆裙','黑色','S','LINE',10,30,40,'车间领片','CO20261116001','BN20261116006','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1011,1001,'JT03','卡扣束腰大摆裙','黑色','S','CUT',10,-20,-10,'裁片入库','CO20261116001','BN20261116005','system','2026-11-16 09:00:00','分扎入库'),(1012,1001,'JT03','卡扣束腰大摆裙','黑色','S','CUT',10,-10,0,'裁片入库','CO20261116001','BN20261116006','system','2026-11-16 09:00:00','分扎入库'),(1013,1001,'JT03','卡扣束腰大摆裙','黑色','S','LINE',-5,40,35,'包装出产线','PK100101','BN20261113001','孙梅','2026-11-19 10:30:00','包装入库'),(1014,1001,'JT03','卡扣束腰大摆裙','黑色','S','FINISH',5,0,5,'成品入库','PK100101','BN20261113001','孙梅','2026-11-19 10:30:00','包装入库'),(1015,1001,'JT03','卡扣束腰大摆裙','黑色','S','LINE',-5,35,30,'包装出产线','PK100102','BN20261113002','孙梅','2026-11-19 10:30:00','包装入库'),(1016,1001,'JT03','卡扣束腰大摆裙','黑色','S','FINISH',5,5,10,'成品入库','PK100102','BN20261113002','孙梅','2026-11-19 10:30:00','包装入库'),(1017,1001,'JT03','卡扣束腰大摆裙','黑色','S','LINE',-10,30,20,'包装出产线','PK100201','BN20261116005','孙梅','2026-11-19 15:00:00','包装入库'),(1018,1001,'JT03','卡扣束腰大摆裙','黑色','S','FINISH',10,10,20,'成品入库','PK100201','BN20261116005','孙梅','2026-11-19 15:00:00','包装入库'),(1019,1001,'JT03','卡扣束腰大摆裙','黑色','S','LINE',-10,20,10,'包装出产线','PK100202','BN20261116006','孙梅','2026-11-19 15:00:00','包装入库'),(1020,1001,'JT03','卡扣束腰大摆裙','黑色','S','FINISH',10,20,30,'成品入库','PK100202','BN20261116006','孙梅','2026-11-19 15:00:00','包装入库'),(1021,1002,'JT03','卡扣束腰大摆裙','黑色','M','FINISH',-4,0,-4,'成品出库','CK20261114001','','王芳','2026-11-14 11:00:00','客户：杭州四季青'),(1022,1002,'JT03','卡扣束腰大摆裙','黑色','M','CUT',-10,0,-10,'车间领片','CO20261116001','BN20261116007','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1023,1002,'JT03','卡扣束腰大摆裙','黑色','M','LINE',10,0,10,'车间领片','CO20261116001','BN20261116007','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1024,1002,'JT03','卡扣束腰大摆裙','黑色','M','CUT',-10,-10,-20,'车间领片','CO20261116001','BN20261116008','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1025,1002,'JT03','卡扣束腰大摆裙','黑色','M','LINE',10,10,20,'车间领片','CO20261116001','BN20261116008','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1026,1002,'JT03','卡扣束腰大摆裙','黑色','M','CUT',10,-20,-10,'裁片入库','CO20261116001','BN20261116007','system','2026-11-16 09:00:00','分扎入库'),(1027,1002,'JT03','卡扣束腰大摆裙','黑色','M','CUT',10,-10,0,'裁片入库','CO20261116001','BN20261116008','system','2026-11-16 09:00:00','分扎入库'),(1028,1002,'JT03','卡扣束腰大摆裙','黑色','M','FINISH',3,-4,-1,'退货入库','TH20261118001','','陈静','2026-11-18 14:00:00','退货原因：尺码偏小'),(1029,1002,'JT03','卡扣束腰大摆裙','黑色','M','LINE',-10,20,10,'包装出产线','PK100201','BN20261116007','孙梅','2026-11-19 15:00:00','包装入库'),(1030,1002,'JT03','卡扣束腰大摆裙','黑色','M','FINISH',10,-1,9,'成品入库','PK100201','BN20261116007','孙梅','2026-11-19 15:00:00','包装入库'),(1031,1002,'JT03','卡扣束腰大摆裙','黑色','M','LINE',-10,10,0,'包装出产线','PK100202','BN20261116008','孙梅','2026-11-19 15:00:00','包装入库'),(1032,1002,'JT03','卡扣束腰大摆裙','黑色','M','FINISH',10,9,19,'成品入库','PK100202','BN20261116008','孙梅','2026-11-19 15:00:00','包装入库'),(1033,1006,'JT03','卡扣束腰大摆裙','白色','M','CUT',10,0,10,'裁片入库','CO20261113001','BN20261113003','system','2026-11-13 09:00:00','分扎入库'),(1034,1006,'JT03','卡扣束腰大摆裙','白色','M','CUT',10,10,20,'裁片入库','CO20261113001','BN20261113004','system','2026-11-13 09:00:00','分扎入库'),(1035,1006,'JT03','卡扣束腰大摆裙','白色','M','CUT',-10,20,10,'车间领片','CO20261113001','BN20261113003','赵丽','2026-11-14 09:30:00','车间扫码领片'),(1036,1006,'JT03','卡扣束腰大摆裙','白色','M','LINE',10,0,10,'车间领片','CO20261113001','BN20261113003','赵丽','2026-11-14 09:30:00','车间扫码领片'),(1037,1006,'JT03','卡扣束腰大摆裙','白色','M','CUT',-10,10,0,'车间领片','CO20261113001','BN20261113004','赵丽','2026-11-14 09:30:00','车间扫码领片'),(1038,1006,'JT03','卡扣束腰大摆裙','白色','M','LINE',10,10,20,'车间领片','CO20261113001','BN20261113004','赵丽','2026-11-14 09:30:00','车间扫码领片'),(1039,1006,'JT03','卡扣束腰大摆裙','白色','M','FINISH',-4,0,-4,'成品出库','CK20261114001','','王芳','2026-11-14 11:00:00','客户：杭州四季青'),(1040,1006,'JT03','卡扣束腰大摆裙','白色','M','CUT',-10,0,-10,'车间领片','CO20261116001','BN20261116009','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1041,1006,'JT03','卡扣束腰大摆裙','白色','M','LINE',10,20,30,'车间领片','CO20261116001','BN20261116009','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1042,1006,'JT03','卡扣束腰大摆裙','白色','M','CUT',-10,-10,-20,'车间领片','CO20261116001','BN20261116010','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1043,1006,'JT03','卡扣束腰大摆裙','白色','M','LINE',10,30,40,'车间领片','CO20261116001','BN20261116010','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1044,1006,'JT03','卡扣束腰大摆裙','白色','M','CUT',10,-20,-10,'裁片入库','CO20261116001','BN20261116009','system','2026-11-16 09:00:00','分扎入库'),(1045,1006,'JT03','卡扣束腰大摆裙','白色','M','CUT',10,-10,0,'裁片入库','CO20261116001','BN20261116010','system','2026-11-16 09:00:00','分扎入库'),(1046,1006,'JT03','卡扣束腰大摆裙','白色','M','LINE',-5,40,35,'包装出产线','PK100101','BN20261113003','孙梅','2026-11-19 10:30:00','包装入库'),(1047,1006,'JT03','卡扣束腰大摆裙','白色','M','FINISH',5,-4,1,'成品入库','PK100101','BN20261113003','孙梅','2026-11-19 10:30:00','包装入库'),(1048,1006,'JT03','卡扣束腰大摆裙','白色','M','LINE',-5,35,30,'包装出产线','PK100102','BN20261113004','孙梅','2026-11-19 10:30:00','包装入库'),(1049,1006,'JT03','卡扣束腰大摆裙','白色','M','FINISH',5,1,6,'成品入库','PK100102','BN20261113004','孙梅','2026-11-19 10:30:00','包装入库'),(1050,1006,'JT03','卡扣束腰大摆裙','白色','M','LINE',-10,30,20,'包装出产线','PK100201','BN20261116009','孙梅','2026-11-19 15:00:00','包装入库'),(1051,1006,'JT03','卡扣束腰大摆裙','白色','M','FINISH',10,6,16,'成品入库','PK100201','BN20261116009','孙梅','2026-11-19 15:00:00','包装入库'),(1052,1006,'JT03','卡扣束腰大摆裙','白色','M','LINE',-10,20,10,'包装出产线','PK100202','BN20261116010','孙梅','2026-11-19 15:00:00','包装入库'),(1053,1006,'JT03','卡扣束腰大摆裙','白色','M','FINISH',10,16,26,'成品入库','PK100202','BN20261116010','孙梅','2026-11-19 15:00:00','包装入库'),(1054,1007,'JT03','卡扣束腰大摆裙','白色','L','CUT',-10,0,-10,'车间领片','CO20261116001','BN20261116011','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1055,1007,'JT03','卡扣束腰大摆裙','白色','L','LINE',10,0,10,'车间领片','CO20261116001','BN20261116011','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1056,1007,'JT03','卡扣束腰大摆裙','白色','L','CUT',-10,-10,-20,'车间领片','CO20261116001','BN20261116012','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1057,1007,'JT03','卡扣束腰大摆裙','白色','L','LINE',10,10,20,'车间领片','CO20261116001','BN20261116012','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1058,1007,'JT03','卡扣束腰大摆裙','白色','L','CUT',10,-20,-10,'裁片入库','CO20261116001','BN20261116011','system','2026-11-16 09:00:00','分扎入库'),(1059,1007,'JT03','卡扣束腰大摆裙','白色','L','CUT',10,-10,0,'裁片入库','CO20261116001','BN20261116012','system','2026-11-16 09:00:00','分扎入库'),(1060,1007,'JT03','卡扣束腰大摆裙','白色','L','LINE',-10,20,10,'包装出产线','PK100201','BN20261116011','孙梅','2026-11-19 15:00:00','包装入库'),(1061,1007,'JT03','卡扣束腰大摆裙','白色','L','FINISH',10,0,10,'成品入库','PK100201','BN20261116011','孙梅','2026-11-19 15:00:00','包装入库'),(1062,1007,'JT03','卡扣束腰大摆裙','白色','L','LINE',-10,10,0,'包装出产线','PK100202','BN20261116012','孙梅','2026-11-19 15:00:00','包装入库'),(1063,1007,'JT03','卡扣束腰大摆裙','白色','L','FINISH',10,10,20,'成品入库','PK100202','BN20261116012','孙梅','2026-11-19 15:00:00','包装入库'),(1064,1011,'JT03','卡扣束腰大摆裙','藏青','L','CUT',-10,0,-10,'车间领片','CO20261116001','BN20261116013','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1065,1011,'JT03','卡扣束腰大摆裙','藏青','L','LINE',10,0,10,'车间领片','CO20261116001','BN20261116013','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1066,1011,'JT03','卡扣束腰大摆裙','藏青','L','CUT',-10,-10,-20,'车间领片','CO20261116001','BN20261116014','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1067,1011,'JT03','卡扣束腰大摆裙','藏青','L','LINE',10,10,20,'车间领片','CO20261116001','BN20261116014','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1068,1011,'JT03','卡扣束腰大摆裙','藏青','L','CUT',10,-20,-10,'裁片入库','CO20261116001','BN20261116013','system','2026-11-16 09:00:00','分扎入库'),(1069,1011,'JT03','卡扣束腰大摆裙','藏青','L','CUT',10,-10,0,'裁片入库','CO20261116001','BN20261116014','system','2026-11-16 09:00:00','分扎入库'),(1070,1011,'JT03','卡扣束腰大摆裙','藏青','L','LINE',-10,20,10,'包装出产线','PK100201','BN20261116013','孙梅','2026-11-19 15:00:00','包装入库'),(1071,1011,'JT03','卡扣束腰大摆裙','藏青','L','FINISH',10,0,10,'成品入库','PK100201','BN20261116013','孙梅','2026-11-19 15:00:00','包装入库'),(1072,1011,'JT03','卡扣束腰大摆裙','藏青','L','LINE',-10,10,0,'包装出产线','PK100202','BN20261116014','孙梅','2026-11-19 15:00:00','包装入库'),(1073,1011,'JT03','卡扣束腰大摆裙','藏青','L','FINISH',10,10,20,'成品入库','PK100202','BN20261116014','孙梅','2026-11-19 15:00:00','包装入库'),(1074,1016,'JT03','卡扣束腰大摆裙','酒红','XL','FINISH',-2,0,-2,'成品出库','CK20261114001','','王芳','2026-11-14 11:00:00','客户：杭州四季青'),(1075,1016,'JT03','卡扣束腰大摆裙','酒红','XL','CUT',-10,0,-10,'车间领片','CO20261116001','BN20261116015','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1076,1016,'JT03','卡扣束腰大摆裙','酒红','XL','LINE',10,0,10,'车间领片','CO20261116001','BN20261116015','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1077,1016,'JT03','卡扣束腰大摆裙','酒红','XL','CUT',-10,-10,-20,'车间领片','CO20261116001','BN20261116016','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1078,1016,'JT03','卡扣束腰大摆裙','酒红','XL','LINE',10,10,20,'车间领片','CO20261116001','BN20261116016','钱峰','2026-11-16 08:40:00','车间扫码领片'),(1079,1016,'JT03','卡扣束腰大摆裙','酒红','XL','CUT',10,-20,-10,'裁片入库','CO20261116001','BN20261116015','system','2026-11-16 09:00:00','分扎入库'),(1080,1016,'JT03','卡扣束腰大摆裙','酒红','XL','CUT',10,-10,0,'裁片入库','CO20261116001','BN20261116016','system','2026-11-16 09:00:00','分扎入库'),(1081,1016,'JT03','卡扣束腰大摆裙','酒红','XL','LINE',-10,20,10,'包装出产线','PK100201','BN20261116015','孙梅','2026-11-19 15:00:00','包装入库'),(1082,1016,'JT03','卡扣束腰大摆裙','酒红','XL','FINISH',10,-2,8,'成品入库','PK100201','BN20261116015','孙梅','2026-11-19 15:00:00','包装入库'),(1083,1016,'JT03','卡扣束腰大摆裙','酒红','XL','LINE',-10,10,0,'包装出产线','PK100202','BN20261116016','孙梅','2026-11-19 15:00:00','包装入库'),(1084,1016,'JT03','卡扣束腰大摆裙','酒红','XL','FINISH',10,8,18,'成品入库','PK100202','BN20261116016','孙梅','2026-11-19 15:00:00','包装入库'),(1085,1101,'JT04','原布连衣裙','米杏','S','CUT',10,0,10,'裁片入库','CO20261118001','BN20261118017','system','2026-11-18 09:00:00','分扎入库'),(1086,1101,'JT04','原布连衣裙','米杏','S','CUT',10,10,20,'裁片入库','CO20261118001','BN20261118018','system','2026-11-18 09:00:00','分扎入库'),(1087,1101,'JT04','原布连衣裙','米杏','S','FINISH',-4,0,-4,'成品出库','CK20261119001','','王芳','2026-11-19 11:00:00','客户：上海衣品'),(1088,1101,'JT04','原布连衣裙','米杏','S','FINISH',2,-4,-2,'退货入库','TH20261120001','','陈静','2026-11-20 14:00:00','退货原因：客户取消'),(1089,1102,'JT04','原布连衣裙','米杏','M','CUT',10,0,10,'裁片入库','CO20261118001','BN20261118019','system','2026-11-18 09:00:00','分扎入库'),(1090,1102,'JT04','原布连衣裙','米杏','M','CUT',10,10,20,'裁片入库','CO20261118001','BN20261118020','system','2026-11-18 09:00:00','分扎入库'),(1091,1105,'JT04','原布连衣裙','雾霾蓝','M','CUT',10,0,10,'裁片入库','CO20261118001','BN20261118021','system','2026-11-18 09:00:00','分扎入库'),(1092,1105,'JT04','原布连衣裙','雾霾蓝','M','CUT',10,10,20,'裁片入库','CO20261118001','BN20261118022','system','2026-11-18 09:00:00','分扎入库'),(1093,1201,'JT07','原布半身裙','米杏','S','CUT',10,0,10,'裁片入库','CO20261121001','BN20261121023','system','2026-11-21 09:00:00','分扎入库'),(1094,1201,'JT07','原布半身裙','米杏','S','CUT',5,10,15,'裁片入库','CO20261121001','BN20261121024','system','2026-11-21 09:00:00','分扎入库'),(1095,1206,'JT07','原布半身裙','雾霾蓝','L','FINISH',-6,0,-6,'成品出库','CK20261119001','','王芳','2026-11-19 11:00:00','客户：上海衣品'),(1096,1206,'JT07','原布半身裙','雾霾蓝','L','CUT',10,0,10,'裁片入库','CO20261121001','BN20261121025','system','2026-11-21 09:00:00','分扎入库'),(1097,1206,'JT07','原布半身裙','雾霾蓝','L','CUT',5,10,15,'裁片入库','CO20261121001','BN20261121026','system','2026-11-21 09:00:00','分扎入库'),(1098,1302,'MQ3001','法式泡泡袖上衣','白色','M','FINISH',-6,0,-6,'成品出库','CK20261121001','','小陈','2026-11-21 11:00:00','客户：北京尚品'),(1099,1312,'MQ3001','法式泡泡袖上衣','浅灰','XL','FINISH',8,0,8,'初始库存','INIT','','admin','2026-11-10 09:00:00','Excel导入初始库存'),(1100,1001,'JT03','卡扣束腰大摆裙','黑色','S','FINISH',2,30,32,'退货入库','TH20261121003','','sysadmin','2026-11-21 10:56:24','测试退货'),(1101,1001,'JT03','卡扣束腰大摆裙','黑色','S','FINISH',-2,32,30,'退货作废','TH20261121003','','sysadmin','2026-11-21 10:56:39','删除退货单，回滚库存'),(1102,1002,'JT03','卡扣束腰大摆裙','黑色','M','FINISH',10,19,29,'成品入库','CP20261121002','','王芳','2026-11-21 14:30:00','包装入库');
/*!40000 ALTER TABLE `biz_stock_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_style`
--

DROP TABLE IF EXISTS `biz_style`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_style` (
  `style_id` bigint NOT NULL AUTO_INCREMENT COMMENT '款号ID',
  `style_no` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '款号',
  `parent_style_no` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '父体款号',
  `cn_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '中文品名',
  `style_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '名称',
  `category` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '品类',
  `season` varchar(30) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '季节',
  `image` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '款式图片',
  `designer` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '设计人',
  `size_table` varchar(200) COLLATE utf8mb4_general_ci DEFAULT 'S,M,L,XL,XXL' COMMENT '尺码表(逗号分隔)',
  `ref_image` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参考图片',
  `expect_price` decimal(10,2) DEFAULT '0.00' COMMENT '预计卖价',
  `pattern_file` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '纸样文件',
  `sample_image` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '样衣照片',
  `design_remark` text COLLATE utf8mb4_general_ci COMMENT '设计/修改记录',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志(0存在 2删除)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`style_id`),
  UNIQUE KEY `uk_style_no` (`style_no`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='款号档案';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_style`
--

LOCK TABLES `biz_style` WRITE;
/*!40000 ALTER TABLE `biz_style` DISABLE KEYS */;
INSERT INTO `biz_style` VALUES (100,'JT03','JT03','卡扣束腰大摆裙','Kink Waist Flared Dress','连衣裙','春夏',NULL,'张敏','S,M,L,XL',NULL,259.00,'JT03-纸样-v3.dxf',NULL,'2026-11-02 选品会通过，A字裙型+腰部卡扣；\n2026-11-06 打版第2版，腰头加宽 2cm；\n2026-11-10 样衣评审通过，进入生产。\n纸样文件已归档，样衣 1 件留档。','0','0','admin','2026-11-02 10:00:00','',NULL,''),(101,'JT04','SP1001','原布连衣裙','Linen Dress','连衣裙','春夏',NULL,'李婷','S,M,L',NULL,219.00,'JT04-纸样-v2.dxf',NULL,'2026-11-03 选品通过，原布肌理面料。\n2026-11-07 样衣试穿，肩线调整。','0','0','admin','2026-11-03 10:00:00','',NULL,''),(102,'JT07','SP1001','原布半身裙','Linen Skirt','半身裙','春夏',NULL,'李婷','S,M,L',NULL,159.00,'JT07-纸样-v1.dxf',NULL,'与 JT04 同系列，共用父体款号 SP1001。','0','0','admin','2026-11-03 10:20:00','',NULL,''),(103,'MQ3001','MQ3001','法式泡泡袖上衣','Puff Sleeve Blouse','上衣','秋季',NULL,'张敏','S,M,L,XL',NULL,189.00,'MQ3001-纸样-v2.dxf',NULL,'2026-11-05 选品通过，泡泡袖版型打版中。\n2026-11-12 样衣完成，待大货确认。','0','0','admin','2026-11-05 09:30:00','',NULL,''),(104,'JT05','JT05','秋冬长款风衣','Trench Coat','外套','秋季',NULL,'王倩','S,M,L,XL',NULL,469.00,'JT05-纸样-draft.dxf',NULL,'2026-11-18 新选品，风衣长款，纸样打版中，尚未生成商品。','0','0','admin','2026-11-18 14:00:00','',NULL,''),(105,'JT06','JT06','基础针织衫','Knit Sweater','针织衫','冬季',NULL,'王倩','均码',NULL,129.00,'JT06-纸样-v1.dxf',NULL,'旧款，已停用，仅保留历史资料。','1','0','admin','2026-11-08 11:00:00','admin','2026-11-21 13:46:04','');
/*!40000 ALTER TABLE `biz_style` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_supplier`
--

DROP TABLE IF EXISTS `biz_supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_supplier` (
  `supplier_id` bigint NOT NULL AUTO_INCREMENT COMMENT '供应商ID',
  `supplier_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '供应商名称',
  `contact` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '联系人',
  `phone` varchar(30) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '电话',
  `supply_content` varchar(200) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '供货内容',
  `address` varchar(200) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '地址',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '',
  PRIMARY KEY (`supplier_id`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='供应商';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_supplier`
--

LOCK TABLES `biz_supplier` WRITE;
/*!40000 ALTER TABLE `biz_supplier` DISABLE KEYS */;
INSERT INTO `biz_supplier` VALUES (101,'杭州四季青面料商行','王经理','13805710001','雪纺、真丝、里布等面料','杭州市上城区四季青服装市场 A 区 12 号','0','admin','2026-11-01 09:30:00','',NULL,''),(102,'义乌华美辅料有限公司','陈丽','13905790002','纽扣、松紧带、缝纫线','义乌市国际商贸城二区 18 街','0','admin','2026-11-01 09:30:00','',NULL,''),(103,'东莞恒信拉链厂','李建国','13602990003','隐形拉链、金属拉链','东莞市虎门镇富民布料市场','0','admin','2026-11-01 09:30:00','',NULL,''),(104,'苏州锦程包装材料厂','周涛','13705120004','纸箱、包装袋、打印耗材','苏州市吴江区盛泽镇包装产业园','0','admin','2026-11-01 09:30:00','',NULL,''),(105,'广州华兴制衣设备','黄勇','13802000005','裁床刀片、设备配件','广州市海珠区中大布匹市场','1','admin','2026-11-01 09:30:00','',NULL,'');
/*!40000 ALTER TABLE `biz_supplier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table`
--

DROP TABLE IF EXISTS `gen_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table` (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) COLLATE utf8mb4_general_ci DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成功能作者',
  `form_col_num` int DEFAULT '1' COMMENT '表单布局（单列 双列 三列）',
  `gen_type` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) COLLATE utf8mb4_general_ci DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='代码生成业务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table`
--

LOCK TABLES `gen_table` WRITE;
/*!40000 ALTER TABLE `gen_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `gen_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table_column`
--

DROP TABLE IF EXISTS `gen_table_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table_column` (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) COLLATE utf8mb4_general_ci DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典类型',
  `sort` int DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='代码生成业务表字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table_column`
--

LOCK TABLES `gen_table_column` WRITE;
/*!40000 ALTER TABLE `gen_table_column` DISABLE KEYS */;
/*!40000 ALTER TABLE `gen_table_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_BLOB_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_BLOB_TRIGGERS` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Blob类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_BLOB_TRIGGERS`
--

LOCK TABLES `QRTZ_BLOB_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_BLOB_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_BLOB_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_CALENDARS`
--

DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_CALENDARS` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='日历信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_CALENDARS`
--

LOCK TABLES `QRTZ_CALENDARS` WRITE;
/*!40000 ALTER TABLE `QRTZ_CALENDARS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_CALENDARS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_CRON_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_CRON_TRIGGERS` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Cron类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_CRON_TRIGGERS`
--

LOCK TABLES `QRTZ_CRON_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_CRON_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_CRON_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_FIRED_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_FIRED_TRIGGERS` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint NOT NULL COMMENT '触发的时间',
  `sched_time` bigint NOT NULL COMMENT '定时器制定的时间',
  `priority` int NOT NULL COMMENT '优先级',
  `state` varchar(16) COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态',
  `job_name` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`,`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='已触发的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_FIRED_TRIGGERS`
--

LOCK TABLES `QRTZ_FIRED_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_FIRED_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_FIRED_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_JOB_DETAILS`
--

DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_JOB_DETAILS` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务组名',
  `description` varchar(250) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_JOB_DETAILS`
--

LOCK TABLES `QRTZ_JOB_DETAILS` WRITE;
/*!40000 ALTER TABLE `QRTZ_JOB_DETAILS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_JOB_DETAILS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_LOCKS`
--

DROP TABLE IF EXISTS `QRTZ_LOCKS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_LOCKS` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) COLLATE utf8mb4_general_ci NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='存储的悲观锁信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_LOCKS`
--

LOCK TABLES `QRTZ_LOCKS` WRITE;
/*!40000 ALTER TABLE `QRTZ_LOCKS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_LOCKS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_PAUSED_TRIGGER_GRPS`
--

DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='暂停的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_PAUSED_TRIGGER_GRPS`
--

LOCK TABLES `QRTZ_PAUSED_TRIGGER_GRPS` WRITE;
/*!40000 ALTER TABLE `QRTZ_PAUSED_TRIGGER_GRPS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_PAUSED_TRIGGER_GRPS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_SCHEDULER_STATE`
--

DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_SCHEDULER_STATE` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='调度器状态表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_SCHEDULER_STATE`
--

LOCK TABLES `QRTZ_SCHEDULER_STATE` WRITE;
/*!40000 ALTER TABLE `QRTZ_SCHEDULER_STATE` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_SCHEDULER_STATE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_SIMPLE_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='简单触发器的信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_SIMPLE_TRIGGERS`
--

LOCK TABLES `QRTZ_SIMPLE_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_SIMPLE_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_SIMPLE_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_SIMPROP_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_SIMPROP_TRIGGERS` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='同步机制的行锁表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_SIMPROP_TRIGGERS`
--

LOCK TABLES `QRTZ_SIMPROP_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_SIMPROP_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_SIMPROP_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_TRIGGERS` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器的类型',
  `start_time` bigint NOT NULL COMMENT '开始时间',
  `end_time` bigint DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `sched_name` (`sched_name`,`job_name`,`job_group`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `QRTZ_JOB_DETAILS` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='触发器详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_TRIGGERS`
--

LOCK TABLES `QRTZ_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) COLLATE utf8mb4_general_ci DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue','Y','admin','2026-11-21 08:36:37','',NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),(2,'用户管理-账号初始密码','sys.user.initPassword','123456','Y','admin','2026-11-21 08:36:37','',NULL,'初始化密码 123456'),(3,'主框架页-侧边栏主题','sys.index.sideTheme','theme-dark','Y','admin','2026-11-21 08:36:37','',NULL,'深色主题theme-dark，浅色主题theme-light'),(4,'账号自助-验证码开关','sys.account.captchaEnabled','false','Y','admin','2026-11-21 08:36:37','',NULL,'是否开启验证码功能（true开启，false关闭）'),(5,'账号自助-是否开启用户注册功能','sys.account.registerUser','true','Y','admin','2026-11-21 08:36:37','admin','2026-11-21 16:10:31','是否开启注册用户功能（true开启，false关闭）'),(6,'用户登录-黑名单列表','sys.login.blackIPList','','Y','admin','2026-11-21 08:36:37','',NULL,'设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）'),(7,'用户管理-初始密码修改策略','sys.account.initPasswordModify','1','Y','admin','2026-11-21 08:36:37','',NULL,'0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框'),(8,'用户管理-账号密码更新周期','sys.account.passwordValidateDays','0','Y','admin','2026-11-21 08:36:37','',NULL,'密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框'),(9,'用户管理-密码字符范围','sys.account.chrtype','0','Y','admin','2026-11-21 08:36:37','',NULL,'默认任意字符范围，0任意（密码可以输入任意字符），1数字（密码只能为0-9数字），2英文字母（密码只能为a-z和A-Z字母），3字母和数字（密码必须包含字母，数字）,4字母数字和特殊字符（目前支持的特殊字符包括：~!@#$%^&*()-=_+）'),(100,'账号自助-默认角色','sys.account.defaultRoleId','107','Y','admin','2026-11-21 15:53:49','',NULL,'新注册用户默认授予的角色ID');
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '部门名称',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dept`
--

LOCK TABLES `sys_dept` WRITE;
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` VALUES (100,0,'0','武汉漫百千服饰有限公司',0,'陈宏','15888888888','ry@qq.com','0','0','admin','2026-11-21 08:36:37','admin','2026-11-21 11:29:47'),(101,100,'0,100','生产中心',1,'陈宏','15888888888','ry@qq.com','0','0','admin','2026-11-21 08:36:37','',NULL),(102,100,'0,100','职能中心',2,'刘敏','15888888888','ry@qq.com','0','0','admin','2026-11-21 08:36:37','',NULL),(103,101,'0,100,101','设计部',1,'张敏','15888888888','ry@qq.com','0','0','admin','2026-11-21 08:36:37','',NULL),(104,101,'0,100,101','裁床部',2,'老周','15888888888','ry@qq.com','0','0','admin','2026-11-21 08:36:37','',NULL),(105,101,'0,100,101','车间部',3,'李梅','15888888888','ry@qq.com','0','0','admin','2026-11-21 08:36:37','',NULL),(106,101,'0,100,101','后道部',4,'王检','15888888888','ry@qq.com','0','0','admin','2026-11-21 08:36:37','',NULL),(107,101,'0,100,101','仓储部',5,'小陈','15888888888','ry@qq.com','0','0','admin','2026-11-21 08:36:37','',NULL),(108,102,'0,100,102','采购部',6,'周涛','15888888888','ry@qq.com','0','0','admin','2026-11-21 08:36:37','',NULL),(109,102,'0,100,102','财务部',7,'小刘','15888888888','ry@qq.com','0','0','admin','2026-11-21 08:36:37','',NULL);
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_data`
--

DROP TABLE IF EXISTS `sys_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) COLLATE utf8mb4_general_ci DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`),
  UNIQUE KEY `uk_dict_type_value` (`dict_type`,`dict_value`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_data`
--

LOCK TABLES `sys_dict_data` WRITE;
/*!40000 ALTER TABLE `sys_dict_data` DISABLE KEYS */;
INSERT INTO `sys_dict_data` VALUES (1,1,'男','0','sys_user_sex','','','Y','0','admin','2026-11-21 08:36:37','',NULL,'性别男'),(2,2,'女','1','sys_user_sex','','','N','0','admin','2026-11-21 08:36:37','',NULL,'性别女'),(3,3,'未知','2','sys_user_sex','','','N','0','admin','2026-11-21 08:36:37','',NULL,'性别未知'),(4,1,'显示','0','sys_show_hide','','primary','Y','0','admin','2026-11-21 08:36:37','',NULL,'显示菜单'),(5,2,'隐藏','1','sys_show_hide','','danger','N','0','admin','2026-11-21 08:36:37','',NULL,'隐藏菜单'),(6,1,'正常','0','sys_normal_disable','','primary','Y','0','admin','2026-11-21 08:36:37','',NULL,'正常状态'),(7,2,'停用','1','sys_normal_disable','','danger','N','0','admin','2026-11-21 08:36:37','',NULL,'停用状态'),(8,1,'正常','0','sys_job_status','','primary','Y','0','admin','2026-11-21 08:36:37','',NULL,'正常状态'),(9,2,'暂停','1','sys_job_status','','danger','N','0','admin','2026-11-21 08:36:37','',NULL,'停用状态'),(10,1,'默认','DEFAULT','sys_job_group','','','Y','0','admin','2026-11-21 08:36:37','',NULL,'默认分组'),(11,2,'系统','SYSTEM','sys_job_group','','','N','0','admin','2026-11-21 08:36:37','',NULL,'系统分组'),(12,1,'是','Y','sys_yes_no','','primary','Y','0','admin','2026-11-21 08:36:37','',NULL,'系统默认是'),(13,2,'否','N','sys_yes_no','','danger','N','0','admin','2026-11-21 08:36:37','',NULL,'系统默认否'),(14,1,'通知','1','sys_notice_type','','warning','Y','0','admin','2026-11-21 08:36:37','',NULL,'通知'),(15,2,'公告','2','sys_notice_type','','success','N','0','admin','2026-11-21 08:36:37','',NULL,'公告'),(16,1,'正常','0','sys_notice_status','','primary','Y','0','admin','2026-11-21 08:36:37','',NULL,'正常状态'),(17,2,'关闭','1','sys_notice_status','','danger','N','0','admin','2026-11-21 08:36:37','',NULL,'关闭状态'),(18,99,'其他','0','sys_oper_type','','info','N','0','admin','2026-11-21 08:36:37','',NULL,'其他操作'),(19,1,'新增','1','sys_oper_type','','info','N','0','admin','2026-11-21 08:36:37','',NULL,'新增操作'),(20,2,'修改','2','sys_oper_type','','info','N','0','admin','2026-11-21 08:36:37','',NULL,'修改操作'),(21,3,'删除','3','sys_oper_type','','danger','N','0','admin','2026-11-21 08:36:37','',NULL,'删除操作'),(22,4,'授权','4','sys_oper_type','','primary','N','0','admin','2026-11-21 08:36:37','',NULL,'授权操作'),(23,5,'导出','5','sys_oper_type','','warning','N','0','admin','2026-11-21 08:36:37','',NULL,'导出操作'),(24,6,'导入','6','sys_oper_type','','warning','N','0','admin','2026-11-21 08:36:37','',NULL,'导入操作'),(25,7,'强退','7','sys_oper_type','','danger','N','0','admin','2026-11-21 08:36:37','',NULL,'强退操作'),(26,8,'生成代码','8','sys_oper_type','','warning','N','0','admin','2026-11-21 08:36:37','',NULL,'生成操作'),(27,9,'清空数据','9','sys_oper_type','','danger','N','0','admin','2026-11-21 08:36:37','',NULL,'清空操作'),(28,1,'成功','0','sys_common_status','','primary','N','0','admin','2026-11-21 08:36:37','',NULL,'正常状态'),(29,2,'失败','1','sys_common_status','','danger','N','0','admin','2026-11-21 08:36:37','',NULL,'停用状态');
/*!40000 ALTER TABLE `sys_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_type`
--

DROP TABLE IF EXISTS `sys_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典类型',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_type`
--

LOCK TABLES `sys_dict_type` WRITE;
/*!40000 ALTER TABLE `sys_dict_type` DISABLE KEYS */;
INSERT INTO `sys_dict_type` VALUES (1,'用户性别','sys_user_sex','0','admin','2026-11-21 08:36:37','',NULL,'用户性别列表'),(2,'菜单状态','sys_show_hide','0','admin','2026-11-21 08:36:37','',NULL,'菜单状态列表'),(3,'系统开关','sys_normal_disable','0','admin','2026-11-21 08:36:37','',NULL,'系统开关列表'),(4,'任务状态','sys_job_status','0','admin','2026-11-21 08:36:37','',NULL,'任务状态列表'),(5,'任务分组','sys_job_group','0','admin','2026-11-21 08:36:37','',NULL,'任务分组列表'),(6,'系统是否','sys_yes_no','0','admin','2026-11-21 08:36:37','',NULL,'系统是否列表'),(7,'通知类型','sys_notice_type','0','admin','2026-11-21 08:36:37','',NULL,'通知类型列表'),(8,'通知状态','sys_notice_status','0','admin','2026-11-21 08:36:37','',NULL,'通知状态列表'),(9,'操作类型','sys_oper_type','0','admin','2026-11-21 08:36:37','',NULL,'操作类型列表'),(10,'系统状态','sys_common_status','0','admin','2026-11-21 08:36:37','',NULL,'登录状态列表');
/*!40000 ALTER TABLE `sys_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job`
--

DROP TABLE IF EXISTS `sys_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='定时任务调度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job`
--

LOCK TABLES `sys_job` WRITE;
/*!40000 ALTER TABLE `sys_job` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job_log`
--

DROP TABLE IF EXISTS `sys_job_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job_log` (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日志信息',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '异常信息',
  `start_time` datetime DEFAULT NULL COMMENT '执行开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '执行结束时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='定时任务调度日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job_log`
--

LOCK TABLES `sys_job_log` WRITE;
/*!40000 ALTER TABLE `sys_job_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_job_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_logininfor`
--

DROP TABLE IF EXISTS `sys_logininfor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作系统',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_sys_logininfor_s` (`status`),
  KEY `idx_sys_logininfor_lt` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统访问记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_logininfor`
--

LOCK TABLES `sys_logininfor` WRITE;
/*!40000 ALTER TABLE `sys_logininfor` DISABLE KEYS */;
INSERT INTO `sys_logininfor` VALUES (1,'workshop','127.0.0.1','内网IP','Chrome 158','Mac OS >=10.15.7','1','用户不存在/密码错误','2026-11-21 14:45:54'),(2,'workshop','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 15:08:03'),(3,'workshop','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 15:08:09'),(4,'workshop','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 15:22:21'),(5,'workshop','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 15:22:24'),(6,'workshop','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 15:22:29'),(7,'sysadmin','127.0.0.1','内网IP','Curl 8.7.1','','1','用户不存在/密码错误','2026-11-21 15:22:36'),(8,'ry','127.0.0.1','内网IP','Curl 8.7.1','','1','用户不存在/密码错误','2026-11-21 15:22:36'),(9,'cutting','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 15:22:36'),(10,'cutting','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 15:22:41'),(11,'cutting','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 15:24:22'),(12,'cutting','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 15:24:26'),(13,'cutting','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 15:24:43'),(14,'warehouse','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 15:25:03'),(15,'warehouse','127.0.0.1','内网IP','TraeCN 1.107.1','Mac OS 10.15.7','0','登录成功','2026-11-21 15:25:35'),(16,'warehouse','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 15:29:45'),(17,'warehouse','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 15:30:47'),(18,'cutting','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 15:35:41'),(19,'cutting','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 15:35:48'),(23,'warehouse','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 16:00:49'),(24,'warehouse','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 16:00:57'),(25,'warehouse','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 16:01:25'),(26,'workshop','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 16:01:35'),(27,'sysadmin','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 16:01:39'),(28,'sysadmin','127.0.0.1','内网IP','Python-urllib 3.9','','0','登录成功','2026-11-21 16:04:29'),(29,'sysadmin','127.0.0.1','内网IP','Python-urllib 3.9','','0','登录成功','2026-11-21 16:05:41'),(30,'sysadmin','127.0.0.1','内网IP','Python-urllib 3.9','','0','登录成功','2026-11-21 16:05:54'),(31,'sysadmin','127.0.0.1','内网IP','TraeCN 1.107.1','Mac OS 10.15.7','0','登录成功','2026-11-21 16:07:13'),(32,'sysadmin','127.0.0.1','内网IP','TraeCN 1.107.1','Mac OS 10.15.7','0','登录成功','2026-11-21 16:07:56'),(33,'sysadmin','127.0.0.1','内网IP','TraeCN 1.107.1','Mac OS 10.15.7','0','登录成功','2026-11-21 16:08:32'),(34,'sysadmin','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 16:35:37'),(35,'sysadmin','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 16:35:43'),(36,'sysadmin','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 16:38:52'),(37,'sysadmin','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 17:15:58'),(38,'sysadmin','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 17:16:02'),(39,'sysadmin','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 17:16:07'),(40,'sysadmin','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 17:16:12'),(41,'cutting','127.0.0.1','内网IP','Curl 8.7.1','','0','登录成功','2026-11-21 19:46:29');
/*!40000 ALTER TABLE `sys_logininfor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '路由名称',
  `is_frame` int DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2137 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,'系统管理',0,98,'system',NULL,'','',1,0,'M','0','0','','system','admin','2026-11-21 08:36:37','',NULL,'系统管理目录'),(2,'系统监控',0,99,'monitor',NULL,'','',1,0,'M','0','0','','monitor','admin','2026-11-21 08:36:37','',NULL,'系统监控目录'),(100,'用户管理',1,1,'user','system/user/index','','',1,0,'C','0','0','system:user:list','user','admin','2026-11-21 08:36:37','',NULL,'用户管理菜单'),(101,'角色管理',1,2,'role','system/role/index','','',1,0,'C','0','0','system:role:list','peoples','admin','2026-11-21 08:36:37','',NULL,'角色管理菜单'),(102,'菜单管理',1,3,'menu','system/menu/index','','',1,0,'C','0','0','system:menu:list','tree-table','admin','2026-11-21 08:36:37','',NULL,'菜单管理菜单'),(103,'部门管理',1,4,'dept','system/dept/index','','',1,0,'C','0','0','system:dept:list','tree','admin','2026-11-21 08:36:37','',NULL,'部门管理菜单'),(104,'岗位管理',1,5,'post','system/post/index','','',1,0,'C','0','0','system:post:list','post','admin','2026-11-21 08:36:37','',NULL,'岗位管理菜单'),(105,'字典管理',1,6,'dict','system/dict/index','','',1,0,'C','0','0','system:dict:list','dict','admin','2026-11-21 08:36:37','',NULL,'字典管理菜单'),(106,'参数设置',1,7,'config','system/config/index','','',1,0,'C','0','0','system:config:list','edit','admin','2026-11-21 08:36:37','',NULL,'参数设置菜单'),(107,'通知公告',1,8,'notice','system/notice/index','','',1,0,'C','0','0','system:notice:list','message','admin','2026-11-21 08:36:37','',NULL,'通知公告菜单'),(108,'日志管理',1,9,'log','','','',1,0,'M','0','0','','log','admin','2026-11-21 08:36:37','',NULL,'日志管理菜单'),(109,'在线用户',2,1,'online','monitor/online/index','','',1,0,'C','0','0','monitor:online:list','online','admin','2026-11-21 08:36:37','',NULL,'在线用户菜单'),(110,'定时任务',2,2,'job','monitor/job/index','','',1,0,'C','0','0','monitor:job:list','job','admin','2026-11-21 08:36:37','',NULL,'定时任务菜单'),(111,'数据监控',2,3,'druid','monitor/druid/index','','',1,0,'C','0','0','monitor:druid:list','druid','admin','2026-11-21 08:36:37','',NULL,'数据监控菜单'),(112,'服务监控',2,4,'server','monitor/server/index','','',1,0,'C','0','0','monitor:server:list','server','admin','2026-11-21 08:36:37','',NULL,'服务监控菜单'),(113,'缓存监控',2,5,'cache','monitor/cache/index','','',1,0,'C','0','0','monitor:cache:list','redis','admin','2026-11-21 08:36:37','',NULL,'缓存监控菜单'),(114,'缓存列表',2,6,'cacheList','monitor/cache/list','','',1,0,'C','0','0','monitor:cache:list','redis-list','admin','2026-11-21 08:36:37','',NULL,'缓存列表菜单'),(500,'操作日志',108,1,'operlog','monitor/operlog/index','','',1,0,'C','0','0','monitor:operlog:list','form','admin','2026-11-21 08:36:37','',NULL,'操作日志菜单'),(501,'登录日志',108,2,'logininfor','monitor/logininfor/index','','',1,0,'C','0','0','monitor:logininfor:list','logininfor','admin','2026-11-21 08:36:37','',NULL,'登录日志菜单'),(1000,'用户查询',100,1,'','','','',1,0,'F','0','0','system:user:query','#','admin','2026-11-21 08:36:37','',NULL,''),(1001,'用户新增',100,2,'','','','',1,0,'F','0','0','system:user:add','#','admin','2026-11-21 08:36:37','',NULL,''),(1002,'用户修改',100,3,'','','','',1,0,'F','0','0','system:user:edit','#','admin','2026-11-21 08:36:37','',NULL,''),(1003,'用户删除',100,4,'','','','',1,0,'F','0','0','system:user:remove','#','admin','2026-11-21 08:36:37','',NULL,''),(1004,'用户导出',100,5,'','','','',1,0,'F','0','0','system:user:export','#','admin','2026-11-21 08:36:37','',NULL,''),(1005,'用户导入',100,6,'','','','',1,0,'F','0','0','system:user:import','#','admin','2026-11-21 08:36:37','',NULL,''),(1006,'重置密码',100,7,'','','','',1,0,'F','0','0','system:user:resetPwd','#','admin','2026-11-21 08:36:37','',NULL,''),(1007,'角色查询',101,1,'','','','',1,0,'F','0','0','system:role:query','#','admin','2026-11-21 08:36:37','',NULL,''),(1008,'角色新增',101,2,'','','','',1,0,'F','0','0','system:role:add','#','admin','2026-11-21 08:36:37','',NULL,''),(1009,'角色修改',101,3,'','','','',1,0,'F','0','0','system:role:edit','#','admin','2026-11-21 08:36:37','',NULL,''),(1010,'角色删除',101,4,'','','','',1,0,'F','0','0','system:role:remove','#','admin','2026-11-21 08:36:37','',NULL,''),(1011,'角色导出',101,5,'','','','',1,0,'F','0','0','system:role:export','#','admin','2026-11-21 08:36:37','',NULL,''),(1012,'菜单查询',102,1,'','','','',1,0,'F','0','0','system:menu:query','#','admin','2026-11-21 08:36:37','',NULL,''),(1013,'菜单新增',102,2,'','','','',1,0,'F','0','0','system:menu:add','#','admin','2026-11-21 08:36:37','',NULL,''),(1014,'菜单修改',102,3,'','','','',1,0,'F','0','0','system:menu:edit','#','admin','2026-11-21 08:36:37','',NULL,''),(1015,'菜单删除',102,4,'','','','',1,0,'F','0','0','system:menu:remove','#','admin','2026-11-21 08:36:37','',NULL,''),(1016,'部门查询',103,1,'','','','',1,0,'F','0','0','system:dept:query','#','admin','2026-11-21 08:36:37','',NULL,''),(1017,'部门新增',103,2,'','','','',1,0,'F','0','0','system:dept:add','#','admin','2026-11-21 08:36:37','',NULL,''),(1018,'部门修改',103,3,'','','','',1,0,'F','0','0','system:dept:edit','#','admin','2026-11-21 08:36:37','',NULL,''),(1019,'部门删除',103,4,'','','','',1,0,'F','0','0','system:dept:remove','#','admin','2026-11-21 08:36:37','',NULL,''),(1020,'岗位查询',104,1,'','','','',1,0,'F','0','0','system:post:query','#','admin','2026-11-21 08:36:37','',NULL,''),(1021,'岗位新增',104,2,'','','','',1,0,'F','0','0','system:post:add','#','admin','2026-11-21 08:36:37','',NULL,''),(1022,'岗位修改',104,3,'','','','',1,0,'F','0','0','system:post:edit','#','admin','2026-11-21 08:36:37','',NULL,''),(1023,'岗位删除',104,4,'','','','',1,0,'F','0','0','system:post:remove','#','admin','2026-11-21 08:36:37','',NULL,''),(1024,'岗位导出',104,5,'','','','',1,0,'F','0','0','system:post:export','#','admin','2026-11-21 08:36:37','',NULL,''),(1025,'字典查询',105,1,'#','','','',1,0,'F','0','0','system:dict:query','#','admin','2026-11-21 08:36:37','',NULL,''),(1026,'字典新增',105,2,'#','','','',1,0,'F','0','0','system:dict:add','#','admin','2026-11-21 08:36:37','',NULL,''),(1027,'字典修改',105,3,'#','','','',1,0,'F','0','0','system:dict:edit','#','admin','2026-11-21 08:36:37','',NULL,''),(1028,'字典删除',105,4,'#','','','',1,0,'F','0','0','system:dict:remove','#','admin','2026-11-21 08:36:37','',NULL,''),(1029,'字典导出',105,5,'#','','','',1,0,'F','0','0','system:dict:export','#','admin','2026-11-21 08:36:37','',NULL,''),(1030,'参数查询',106,1,'#','','','',1,0,'F','0','0','system:config:query','#','admin','2026-11-21 08:36:37','',NULL,''),(1031,'参数新增',106,2,'#','','','',1,0,'F','0','0','system:config:add','#','admin','2026-11-21 08:36:37','',NULL,''),(1032,'参数修改',106,3,'#','','','',1,0,'F','0','0','system:config:edit','#','admin','2026-11-21 08:36:37','',NULL,''),(1033,'参数删除',106,4,'#','','','',1,0,'F','0','0','system:config:remove','#','admin','2026-11-21 08:36:37','',NULL,''),(1034,'参数导出',106,5,'#','','','',1,0,'F','0','0','system:config:export','#','admin','2026-11-21 08:36:37','',NULL,''),(1035,'公告查询',107,1,'#','','','',1,0,'F','0','0','system:notice:query','#','admin','2026-11-21 08:36:37','',NULL,''),(1036,'公告新增',107,2,'#','','','',1,0,'F','0','0','system:notice:add','#','admin','2026-11-21 08:36:37','',NULL,''),(1037,'公告修改',107,3,'#','','','',1,0,'F','0','0','system:notice:edit','#','admin','2026-11-21 08:36:37','',NULL,''),(1038,'公告删除',107,4,'#','','','',1,0,'F','0','0','system:notice:remove','#','admin','2026-11-21 08:36:37','',NULL,''),(1039,'操作查询',500,1,'#','','','',1,0,'F','0','0','monitor:operlog:query','#','admin','2026-11-21 08:36:37','',NULL,''),(1040,'操作删除',500,2,'#','','','',1,0,'F','0','0','monitor:operlog:remove','#','admin','2026-11-21 08:36:37','',NULL,''),(1041,'日志导出',500,3,'#','','','',1,0,'F','0','0','monitor:operlog:export','#','admin','2026-11-21 08:36:37','',NULL,''),(1042,'登录查询',501,1,'#','','','',1,0,'F','0','0','monitor:logininfor:query','#','admin','2026-11-21 08:36:37','',NULL,''),(1043,'登录删除',501,2,'#','','','',1,0,'F','0','0','monitor:logininfor:remove','#','admin','2026-11-21 08:36:37','',NULL,''),(1044,'日志导出',501,3,'#','','','',1,0,'F','0','0','monitor:logininfor:export','#','admin','2026-11-21 08:36:37','',NULL,''),(1045,'账户解锁',501,4,'#','','','',1,0,'F','0','0','monitor:logininfor:unlock','#','admin','2026-11-21 08:36:37','',NULL,''),(1046,'在线查询',109,1,'#','','','',1,0,'F','0','0','monitor:online:query','#','admin','2026-11-21 08:36:37','',NULL,''),(1047,'批量强退',109,2,'#','','','',1,0,'F','0','0','monitor:online:batchLogout','#','admin','2026-11-21 08:36:37','',NULL,''),(1048,'单条强退',109,3,'#','','','',1,0,'F','0','0','monitor:online:forceLogout','#','admin','2026-11-21 08:36:37','',NULL,''),(1049,'任务查询',110,1,'#','','','',1,0,'F','0','0','monitor:job:query','#','admin','2026-11-21 08:36:37','',NULL,''),(1050,'任务新增',110,2,'#','','','',1,0,'F','0','0','monitor:job:add','#','admin','2026-11-21 08:36:37','',NULL,''),(1051,'任务修改',110,3,'#','','','',1,0,'F','0','0','monitor:job:edit','#','admin','2026-11-21 08:36:37','',NULL,''),(1052,'任务删除',110,4,'#','','','',1,0,'F','0','0','monitor:job:remove','#','admin','2026-11-21 08:36:37','',NULL,''),(1053,'状态修改',110,5,'#','','','',1,0,'F','0','0','monitor:job:changeStatus','#','admin','2026-11-21 08:36:37','',NULL,''),(1054,'任务导出',110,6,'#','','','',1,0,'F','0','0','monitor:job:export','#','admin','2026-11-21 08:36:37','',NULL,''),(2000,'商品资料',0,1,'goods',NULL,'','',1,0,'M','0','0','','clipboard','admin','2026-11-21 08:36:38','',NULL,'商品资料目录'),(2001,'款号建档',2000,1,'style','garment/style/index','','',1,0,'C','0','0','garment:style:list','form','admin','2026-11-21 08:36:38','',NULL,'款号建档菜单'),(2002,'颜色尺码',2000,2,'basics','garment/basics/index','','',1,0,'C','0','0','garment:basics:list','color','admin','2026-11-21 08:36:38','',NULL,'颜色尺码菜单'),(2003,'商品SKU',2000,3,'sku','garment/sku/index','','',1,0,'C','0','0','garment:sku:list','shopping','admin','2026-11-21 08:36:38','',NULL,'商品管理菜单'),(2004,'标签打印',2000,4,'label','garment/label/index','','',1,0,'C','0','0','garment:label:list','documentation','admin','2026-11-21 08:36:38','',NULL,'标签打印菜单'),(2010,'研发设计',0,2,'design',NULL,'','',1,0,'M','0','0','','education','admin','2026-11-21 08:36:38','',NULL,'研发设计目录'),(2011,'选品研发',2010,1,'research','garment/research/index','','',1,0,'C','0','0','garment:research:list','star','admin','2026-11-21 08:36:38','',NULL,'选品研发菜单'),(2012,'用料清单',2010,2,'bom','garment/bom/index','','',1,0,'C','0','0','garment:bom:list','list','admin','2026-11-21 08:36:38','',NULL,'用料清单菜单'),(2013,'成本核算',2010,3,'cost','garment/cost/index','','',1,0,'C','0','0','garment:cost:list','money','admin','2026-11-21 08:36:38','',NULL,'成本核算菜单'),(2020,'采购管理',0,3,'purchaseM',NULL,'','',1,0,'M','0','0','','shopping','admin','2026-11-21 08:36:38','',NULL,'采购管理目录'),(2021,'采购登记',2020,1,'purchase','garment/purchase/index','','',1,0,'C','0','0','garment:purchase:list','edit','admin','2026-11-21 08:36:38','',NULL,'采购登记菜单'),(2022,'供应商管理',2020,2,'supplier','garment/supplier/index','','',1,0,'C','0','0','garment:supplier:list','peoples','admin','2026-11-21 08:36:38','',NULL,'供应商菜单'),(2023,'采购查询',2020,3,'purchaseQuery','garment/purchaseQuery/index','','',1,0,'C','0','0','garment:purchase:list','search','admin','2026-11-21 08:36:38','',NULL,'采购查询菜单'),(2030,'裁床管理',0,4,'cut',NULL,'','',1,0,'M','0','0','','tool','admin','2026-11-21 08:36:38','',NULL,'裁床管理目录'),(2031,'裁货开单',2030,1,'cutorder','garment/cutorder/index','','',1,0,'C','0','0','garment:cutorder:list','documentation','admin','2026-11-21 08:36:38','',NULL,'裁货开单菜单'),(2032,'分扎与入库',2030,2,'bundle','garment/bundle/index','','',1,0,'C','0','0','garment:bundle:list','druid','admin','2026-11-21 08:36:38','',NULL,'分扎与入库菜单'),(2033,'裁床产量',2030,3,'cutstat','garment/cutstat/index','','',1,0,'C','0','0','garment:cutstat:list','chart','admin','2026-11-21 08:36:38','',NULL,'裁床产量菜单'),(2040,'车间管理',0,5,'workshop',NULL,'','',1,0,'M','0','0','','user','admin','2026-11-21 08:36:38','',NULL,'车间管理目录'),(2041,'扫码领片',2040,1,'pick','garment/pick/index','','',1,0,'C','0','0','garment:pick:list','guide','admin','2026-11-21 08:36:38','',NULL,'扫码领片菜单'),(2042,'产线库存',2040,2,'linestock','garment/linestock/index','','',1,0,'C','0','0','garment:linestock:list','table','admin','2026-11-21 08:36:38','',NULL,'产线库存菜单'),(2043,'完工交后道',2040,3,'delivery','garment/delivery/index','','',1,0,'C','0','0','garment:delivery:list','checkbox','admin','2026-11-21 08:36:38','',NULL,'完工交后道菜单'),(2050,'后道管理',0,6,'finishing',NULL,'','',1,0,'M','0','0','','validCode','admin','2026-11-21 08:36:38','',NULL,'后道管理目录'),(2051,'检验与次品',2050,1,'qc','garment/qc/index','','',1,0,'C','0','0','garment:qc:list','eye-open','admin','2026-11-21 08:36:38','',NULL,'检验登记菜单'),(2052,'包装入库',2050,2,'packing','garment/packing/index','','',1,0,'C','0','0','garment:packing:list','zip','admin','2026-11-21 08:36:38','',NULL,'包装入库菜单'),(2053,'后道产量',2050,3,'finishingstat','garment/finishingstat/index','','',1,0,'C','0','0','garment:finishingstat:list','chart','admin','2026-11-21 08:36:38','',NULL,'后道产量菜单'),(2060,'仓库管理',0,7,'warehouse',NULL,'','',1,0,'M','0','0','','build','admin','2026-11-21 08:36:38','',NULL,'仓库管理目录'),(2061,'实时库存看板',2060,1,'inventory','garment/inventory/index','','',1,0,'C','0','0','garment:inventory:list','dashboard','admin','2026-11-21 08:36:38','',NULL,'库存看板菜单'),(2062,'成品出库',2060,2,'outbound','garment/outbound/index','','',1,0,'C','0','0','garment:outbound:list','upload','admin','2026-11-21 08:36:38','',NULL,'成品出库菜单'),(2063,'出货表',2060,3,'shipment','garment/shipment/index','','',1,0,'C','0','0','garment:shipment:list','table','admin','2026-11-21 08:36:38','',NULL,'出货表菜单'),(2064,'库存流水',2060,4,'stocklog','garment/stocklog/index','','',1,0,'C','0','0','garment:stocklog:list','log','admin','2026-11-21 08:36:38','',NULL,'库存流水菜单'),(2065,'库存预警',2060,5,'warning','garment/warning/index','','',1,0,'C','0','0','garment:warning:list','bell','admin','2026-11-21 08:36:38','',NULL,'库存预警菜单'),(2066,'盘点管理',2060,6,'checkstock','garment/check/index','','',1,0,'C','0','0','garment:check:list','checkbox','admin','2026-11-21 08:36:38','',NULL,'盘点管理菜单'),(2067,'退货入库',2060,7,'returngood','garment/return/index','','',1,0,'C','0','0','garment:return:list','download','admin','2026-11-21 08:36:38','',NULL,'退货入库菜单'),(2068,'物资库存',2060,8,'material','garment/material/index','','',1,0,'C','0','0','garment:material:list','component','admin','2026-11-21 09:21:25','',NULL,'物资库存台账'),(2070,'财务看板',0,8,'financeBoard',NULL,'','',1,0,'M','0','0','','money','admin','2026-11-21 08:36:38','',NULL,'财务看板目录'),(2071,'经营分析',2070,1,'analysis','garment/finance/index','','',1,0,'C','0','0','garment:finance:list','chart','admin','2026-11-21 08:36:38','',NULL,'经营分析菜单'),(2072,'费用登记',2070,2,'expense','garment/expense/index','','',1,0,'C','0','0','garment:expense:list','edit','admin','2026-11-21 08:36:38','',NULL,'费用登记菜单'),(2080,'消息提醒',0,9,'msg','garment/message/index','','',1,0,'C','0','0','garment:message:list','message','admin','2026-11-21 08:36:38','',NULL,'消息提醒菜单'),(2101,'款号新增',2001,1,'','','','',1,0,'F','0','0','garment:style:add','#','admin','2026-11-21 08:36:38','',NULL,''),(2102,'款号修改',2001,2,'','','','',1,0,'F','0','0','garment:style:edit','#','admin','2026-11-21 08:36:38','',NULL,''),(2103,'款号删除',2001,3,'','','','',1,0,'F','0','0','garment:style:remove','#','admin','2026-11-21 08:36:38','',NULL,''),(2111,'SKU新增',2003,1,'','','','',1,0,'F','0','0','garment:sku:add','#','admin','2026-11-21 08:36:38','',NULL,''),(2112,'SKU修改',2003,2,'','','','',1,0,'F','0','0','garment:sku:edit','#','admin','2026-11-21 08:36:38','',NULL,''),(2113,'SKU删除',2003,3,'','','','',1,0,'F','0','0','garment:sku:remove','#','admin','2026-11-21 08:36:38','',NULL,''),(2121,'采购新增',2021,1,'','','','',1,0,'F','0','0','garment:purchase:add','#','admin','2026-11-21 08:36:38','',NULL,''),(2122,'采购修改',2021,2,'','','','',1,0,'F','0','0','garment:purchase:edit','#','admin','2026-11-21 08:36:38','',NULL,''),(2123,'采购删除',2021,3,'','','','',1,0,'F','0','0','garment:purchase:remove','#','admin','2026-11-21 08:36:38','',NULL,''),(2131,'裁货开单',2031,1,'','','','',1,0,'F','0','0','garment:cutorder:add','#','admin','2026-11-21 08:36:38','',NULL,''),(2132,'裁货修改',2031,2,'','','','',1,0,'F','0','0','garment:cutorder:edit','#','admin','2026-11-21 08:36:38','',NULL,''),(2133,'员工档案',1,10,'employee','garment/employee/index','','',1,0,'C','0','0','garment:employee:list','peoples','admin','2026-11-21 14:40:52','',NULL,'员工档案菜单'),(2134,'员工新增',2133,1,'','','','',1,0,'F','0','0','garment:employee:add','#','admin','2026-11-21 14:40:52','',NULL,''),(2135,'员工修改',2133,2,'','','','',1,0,'F','0','0','garment:employee:edit','#','admin','2026-11-21 14:40:52','',NULL,''),(2136,'员工删除',2133,3,'','','','',1,0,'F','0','0','garment:employee:remove','#','admin','2026-11-21 14:40:52','',NULL,'');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
INSERT INTO `sys_notice` VALUES (10,'2026 年 11 月生产计划通知','1',_binary '本月主推 JT03、JT04、MQ3001 三个款号，请裁床与车间按裁货单排期执行，仓库提前备料。','0','admin','2026-11-01 09:00:00','',NULL,NULL),(11,'仓库月度盘点通知','1',_binary '定于 11 月 15 日、11 月 19 日进行成品与裁片盘点，请仓库与后道提前整理货位。','0','admin','2026-11-12 09:00:00','',NULL,NULL);
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice_read`
--

DROP TABLE IF EXISTS `sys_notice_read`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice_read` (
  `read_id` bigint NOT NULL AUTO_INCREMENT COMMENT '已读主键',
  `notice_id` int NOT NULL COMMENT '公告id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `read_time` datetime NOT NULL COMMENT '阅读时间',
  PRIMARY KEY (`read_id`),
  UNIQUE KEY `uk_user_notice` (`user_id`,`notice_id`) COMMENT '同一用户同一公告只记录一次'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公告已读记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice_read`
--

LOCK TABLES `sys_notice_read` WRITE;
/*!40000 ALTER TABLE `sys_notice_read` DISABLE KEYS */;
INSERT INTO `sys_notice_read` VALUES (1,11,1,'2026-11-21 15:19:31');
/*!40000 ALTER TABLE `sys_notice_read` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_oper_log`
--

DROP TABLE IF EXISTS `sys_oper_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '模块标题',
  `business_type` int DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求方式',
  `operator_type` int DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '返回参数',
  `status` int DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_sys_oper_log_bt` (`business_type`),
  KEY `idx_sys_oper_log_s` (`status`),
  KEY `idx_sys_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_oper_log`
--

LOCK TABLES `sys_oper_log` WRITE;
/*!40000 ALTER TABLE `sys_oper_log` DISABLE KEYS */;
INSERT INTO `sys_oper_log` VALUES (1,'裁货开单',1,'com.ruoyi.web.controller.garment.BizCutOrderController.add()','POST',1,'cutting','裁床部','/garment/cutorder','127.0.0.1','内网IP','{\"cnName\":\"原布半身裙\",\"createBy\":\"cutting\",\"cutDate\":\"2026-11-21\",\"cutId\":1007,\"cutNo\":\"CH20261121003\",\"items\":[{\"color\":\"米杏\",\"cutId\":1007,\"layerNo\":1,\"qty\":200,\"size\":\"S\"}],\"layerCount\":1,\"params\":{},\"status\":\"0\",\"styleNo\":\"JT07\",\"totalQty\":200} ',NULL,1,'物资库存不足：隐形拉链 需 200条，库存 190条；雪纺印花 需 252米，库存 139.8米。如确需先领后补，请填写超领原因后重新保存','2026-11-21 15:35:41',39),(2,'裁货开单',1,'com.ruoyi.web.controller.garment.BizCutOrderController.add()','POST',1,'cutting','裁床部','/garment/cutorder','127.0.0.1','内网IP','{\"allowOverdraft\":true,\"cnName\":\"原布半身裙\",\"createBy\":\"cutting\",\"cutDate\":\"2026-11-21\",\"cutId\":1008,\"cutNo\":\"CH20261121003\",\"items\":[{\"color\":\"米杏\",\"cutId\":1008,\"layerNo\":1,\"qty\":200,\"size\":\"S\"}],\"layerCount\":1,\"overdraftReason\":\"客户催货，采购在途\",\"params\":{},\"status\":\"0\",\"styleNo\":\"JT07\",\"totalQty\":200} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-21 15:35:41',25),(3,'裁货开单',1,'com.ruoyi.web.controller.garment.BizCutOrderController.add()','POST',1,'warehouse','仓储部','/garment/cutorder','127.0.0.1','内网IP','{\"cnName\":\"原布半身裙\",\"createBy\":\"warehouse\",\"customer\":\"\",\"cutDate\":\"2026-11-21\",\"cutId\":1009,\"cutNo\":\"CH20261121003\",\"cutter\":\"\",\"fabric\":\"\",\"items\":[{\"color\":\"米杏\",\"cutId\":1009,\"layerNo\":1,\"qty\":200,\"size\":\"S\"}],\"layerCount\":1,\"lining\":\"\",\"params\":{},\"status\":\"0\",\"styleNo\":\"JT07\",\"totalQty\":200} ',NULL,1,'物资库存不足：隐形拉链 需 200条，库存 190条；雪纺印花 需 252米，库存 139.8米。如确需先领后补，请填写超领原因后重新保存','2026-11-21 15:48:20',7),(4,'参数管理',2,'com.ruoyi.web.controller.system.SysConfigController.edit()','PUT',1,'sysadmin','武汉漫百千服饰有限公司','/system/config','127.0.0.1','内网IP','{\"configId\":5,\"configKey\":\"sys.account.registerUser\",\"configName\":\"账号自助-是否开启用户注册功能\",\"configType\":\"Y\",\"configValue\":\"false\",\"params\":{},\"remark\":\"是否开启注册用户功能（true开启，false关闭）\",\"updateBy\":\"sysadmin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-21 16:08:06',15),(5,'参数管理',2,'com.ruoyi.web.controller.system.SysConfigController.edit()','PUT',1,'sysadmin','武汉漫百千服饰有限公司','/system/config','127.0.0.1','内网IP','{\"configId\":5,\"configKey\":\"sys.account.registerUser\",\"configName\":\"账号自助-是否开启用户注册功能\",\"configType\":\"Y\",\"configValue\":\"true\",\"params\":{},\"remark\":\"是否开启注册用户功能（true开启，false关闭）\",\"updateBy\":\"sysadmin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-21 16:08:41',5);
/*!40000 ALTER TABLE `sys_oper_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_post`
--

DROP TABLE IF EXISTS `sys_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='岗位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_post`
--

LOCK TABLES `sys_post` WRITE;
/*!40000 ALTER TABLE `sys_post` DISABLE KEYS */;
INSERT INTO `sys_post` VALUES (1,'factoryManager','厂长',1,'0','admin','2026-11-21 08:36:37','',NULL,''),(2,'prodManager','生产主管',2,'0','admin','2026-11-21 08:36:37','',NULL,''),(3,'hrAdmin','人事行政',3,'0','admin','2026-11-21 08:36:37','',NULL,''),(4,'staff','普通员工',4,'0','admin','2026-11-21 08:36:37','',NULL,'');
/*!40000 ALTER TABLE `sys_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'超级管理员','admin',1,'1',1,1,'0','0','admin','2026-11-21 08:36:37','',NULL,'超级管理员'),(100,'裁床','cutting',10,'1',1,1,'0','0','admin','2026-11-21 08:36:38','',NULL,'裁床岗位'),(101,'车间','workshop',11,'1',1,1,'0','0','admin','2026-11-21 08:36:38','',NULL,'车间岗位'),(102,'后道','finishing',12,'1',1,1,'0','0','admin','2026-11-21 08:36:38','',NULL,'后道岗位'),(103,'仓库','warehouse',13,'1',1,1,'0','0','admin','2026-11-21 08:36:38','',NULL,'仓库岗位'),(104,'财务','finance',14,'1',1,1,'0','0','admin','2026-11-21 08:36:38','',NULL,'财务岗位'),(105,'采购','purchaser',15,'1',1,1,'0','0','admin','2026-11-21 08:36:38','',NULL,'采购岗位'),(106,'设计','designer',16,'1',1,1,'0','0','admin','2026-11-21 08:36:38','',NULL,'设计岗位'),(107,'普通员工','staff',5,'5',1,1,'0','0','admin','2026-11-21 15:53:49','',NULL,'自助注册用户默认角色');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_dept`
--

DROP TABLE IF EXISTS `sys_role_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色和部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_dept`
--

LOCK TABLES `sys_role_dept` WRITE;
/*!40000 ALTER TABLE `sys_role_dept` DISABLE KEYS */;
INSERT INTO `sys_role_dept` VALUES (2,100),(2,101),(2,105);
/*!40000 ALTER TABLE `sys_role_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (1,2068),(100,2000),(100,2001),(100,2030),(100,2031),(100,2032),(100,2033),(100,2080),(100,2101),(100,2102),(100,2103),(100,2131),(100,2132),(101,2000),(101,2001),(101,2040),(101,2041),(101,2042),(101,2043),(101,2080),(102,2000),(102,2001),(102,2050),(102,2051),(102,2052),(102,2053),(102,2080),(103,2000),(103,2003),(103,2004),(103,2030),(103,2031),(103,2060),(103,2061),(103,2062),(103,2063),(103,2064),(103,2065),(103,2066),(103,2067),(103,2068),(103,2080),(103,2131),(104,2061),(104,2070),(104,2071),(104,2072),(104,2080),(105,2000),(105,2003),(105,2020),(105,2021),(105,2022),(105,2023),(105,2080),(106,2000),(106,2001),(106,2002),(106,2003),(106,2004),(106,2010),(106,2011),(106,2012),(106,2013),(106,2080),(107,2080);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) COLLATE utf8mb4_general_ci DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '手机号码',
  `sex` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '密码',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,100,'admin','管理员','00','admin@hongzuan.com','15888888888','1','','$2b$10$JagxTZ1ZvOMKVMUVdCHMSOyM4h75a9hyDJV2bIsdehTI7LXyUnaWe','0','0','127.0.0.1','2026-11-21 19:17:39','2026-11-21 08:36:37','admin','2026-11-21 08:36:37','','2026-11-21 10:02:18','系统运维保留账号（超级管理员），不对业务开放'),(100,104,'cutting','裁床-老周','00','','','0','','$2b$10$skjCk/78SlHENipkfGrl6u27QDDdw/HxBWgoX4.P2HKakurAMlWK6','0','0','127.0.0.1','2026-11-21 19:46:29','2026-11-21 19:46:29','admin','2026-11-21 08:36:38','',NULL,'裁床演示账号'),(101,105,'workshop','车间-李梅','00','','','1','','$2b$10$skjCk/78SlHENipkfGrl6u27QDDdw/HxBWgoX4.P2HKakurAMlWK6','0','0','127.0.0.1','2026-11-21 16:01:35','2026-11-21 19:46:29','admin','2026-11-21 08:36:38','',NULL,'车间演示账号'),(102,106,'finishing','后道-王检','00','','','0','','$2b$10$skjCk/78SlHENipkfGrl6u27QDDdw/HxBWgoX4.P2HKakurAMlWK6','0','0','',NULL,'2026-11-21 19:46:29','admin','2026-11-21 08:36:38','',NULL,'后道演示账号'),(103,107,'warehouse','仓库-小陈','00','','','0','','$2b$10$skjCk/78SlHENipkfGrl6u27QDDdw/HxBWgoX4.P2HKakurAMlWK6','0','0','127.0.0.1','2026-11-21 16:01:25','2026-11-21 19:46:29','admin','2026-11-21 08:36:38','',NULL,'仓库演示账号'),(104,109,'finance','财务-小刘','00','','','1','','$2b$10$skjCk/78SlHENipkfGrl6u27QDDdw/HxBWgoX4.P2HKakurAMlWK6','0','0','',NULL,'2026-11-21 19:46:29','admin','2026-11-21 08:36:38','',NULL,'财务演示账号'),(105,100,'sysadmin','系统管理员','00','','13900000000','0','','$2b$10$zBkOEa6m1Wj5RSRnQf5EZu.79lKMsOqTLJZmMCIzDzWaQWSiIqBbO','0','0','127.0.0.1','2026-11-21 17:16:13','2026-11-21 10:02:18','','2026-11-21 10:02:18','',NULL,'系统管理员账号'),(106,103,'design01','设计部-张敏','00','design01@garment.local','13800000006','1','','$2b$10$skjCk/78SlHENipkfGrl6u27QDDdw/HxBWgoX4.P2HKakurAMlWK6','0','0','',NULL,'2026-11-21 19:46:29','sysadmin','2026-11-21 15:06:39','',NULL,'主设计师'),(107,103,'design02','设计部-李婷','00','design02@garment.local','13800000007','1','','$2b$10$skjCk/78SlHENipkfGrl6u27QDDdw/HxBWgoX4.P2HKakurAMlWK6','0','0','',NULL,'2026-11-21 19:46:29','sysadmin','2026-11-21 15:06:39','',NULL,'设计师'),(108,103,'design03','设计部-王倩','00','design03@garment.local','13800000008','1','','$2b$10$skjCk/78SlHENipkfGrl6u27QDDdw/HxBWgoX4.P2HKakurAMlWK6','0','0','',NULL,'2026-11-21 19:46:29','sysadmin','2026-11-21 15:06:39','',NULL,'设计师'),(109,108,'purchaser01','采购部-吴磊','00','purchaser01@garment.local','13800000009','0','','$2b$10$skjCk/78SlHENipkfGrl6u27QDDdw/HxBWgoX4.P2HKakurAMlWK6','0','0','',NULL,'2026-11-21 19:46:29','sysadmin','2026-11-21 15:06:39','',NULL,'采购员');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_post`
--

DROP TABLE IF EXISTS `sys_user_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户与岗位关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_post`
--

LOCK TABLES `sys_user_post` WRITE;
/*!40000 ALTER TABLE `sys_user_post` DISABLE KEYS */;
INSERT INTO `sys_user_post` VALUES (1,1);
/*!40000 ALTER TABLE `sys_user_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户和角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1),(100,100),(101,101),(102,102),(103,103),(104,104),(105,1),(106,106),(107,106),(108,106),(109,105);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-21 19:46:33
