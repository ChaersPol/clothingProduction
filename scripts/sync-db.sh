#!/usr/bin/env bash
# 将本地 MySQL 的 garment 库完整导出为项目唯一的 SQL 脚本。
# 用法：./scripts/sync-db.sh
set -euo pipefail

DB_HOST="${DB_HOST:-127.0.0.1}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASS="${DB_PASS:-123456}"
DB_NAME="${DB_NAME:-garment}"

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
OUT="$ROOT_DIR/ruoyi-backend/sql/garment.sql"

{
  echo "-- ============================================================"
  echo "-- 服装厂生产管理系统 · 唯一数据库脚本"
  echo "-- 说明：本文件由本地 MySQL 库 ${DB_NAME} 完整导出，结构与数据保持一致。"
  echo "-- 同步方式：在本地库调整数据后执行 ./scripts/sync-db.sh 重新导出。"
  echo "-- ============================================================"
  echo
  mysqldump -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" \
    --databases "$DB_NAME" \
    --single-transaction --triggers \
    --default-character-set=utf8mb4 \
    --set-gtid-purged=OFF --no-tablespaces \
    2>/dev/null
} > "$OUT"

echo "已同步：$OUT"
