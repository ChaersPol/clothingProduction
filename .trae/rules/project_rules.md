# 项目规则 · 服装厂生产管理系统

## 技术栈与结构
- 前端：`ruoyi-ui/`（RuoYi-Vue3 + Vue3 + Vite + Element Plus + Pinia）
- 后端：`ruoyi-backend/`（RuoYi SpringBoot3 + Java 17 + MyBatis + MySQL + Redis）
- 数据库：本地 MySQL `garment`（127.0.0.1:3306，root/123456）
- 前端开发端口 8090，代理 `/dev-api` → `http://localhost:8080`

## 前端页面规范（必须与若依原生页面保持一致）
- 页面根节点使用 `<div class="app-container garment-page">`。
- 搜索区：`<el-form class="search-form" :inline="true" v-show="showSearch">`，只放「搜索 / 重置」。
- 操作区：搜索表单下方使用若依标准工具栏，新增、导出等按钮一律放在这里，不得放进搜索表单：
  ```html
  <el-row :gutter="10" class="mb8">
    <el-col :span="1.5">
      <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['xxx:xxx:add']">新增</el-button>
    </el-col>
    <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
  </el-row>
  ```
- 每个列表页脚本中必须有 `const showSearch = ref(true)`，并在查询后调用对应的列表加载函数。
- 操作列：`<el-table-column label="操作" class-name="op-cell" width="...">`，按钮使用 `link` 类型，必须单行展示；`op-cell` 已全局设置 `white-space: nowrap`。列宽按「按钮文字总宽 + 24px 单元格内边距 + 至少 20px 缓冲」给足（链接按钮 2 字≈34px、3 字≈48px、4 字≈62px），避免出现省略号「…」。
- 表格：至少一列使用 `min-width` 让表格铺满容器，避免右侧大面积留白；列宽不足时允许横向滚动。
- 表格内容默认单行展示（全局已设 `white-space: nowrap`）。单号/条码/时间列宽必须按内容给足：单号 ≥150、日期时间 ≥170、扎号 ≥150；剩余宽度交给品名、备注等文本列（`min-width`）吸收，禁止只给编号/时间列设 `min-width`，否则该列会被拉得异常宽。
- 主操作按钮使用若依风格：新增/保存 `type="primary"`，导出/导入 `plain`，删除 `type="danger"`。
- 列表页只要提供行内「删除」操作，表格首列必须是与若依一致的多选框，并在工具栏提供批量「删除」（文案与若依原生一致，按钮 `:disabled="multiple"`）：
  ```html
  <el-table v-loading="loading" :data="list" border @selection-change="handleSelectionChange">
    <el-table-column type="selection" width="50" align="center" />
  ```
  ```html
  <el-col :span="1.5">
    <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
  </el-col>
  ```
  脚本中维护 `const ids = ref([])`、`const multiple = ref(true)` 与 `handleSelectionChange`；行内删除与批量删除复用同一个 `handleDelete(row)`（`row` 为空时取 `ids`）。后端删除接口统一接收 `Long[]`，支持逗号分隔的批量 ID。
- 没有删除功能的列表（如标签打印、纯查询统计）不强制加多选列。
- 所有自定义页面需接入 `v-hasPermi` 权限指令，权限标识与后端 `@PreAuthorize` 保持一致。

## 数据库与 SQL 规则
- **整个项目只保留一份 SQL 文件**：`ruoyi-backend/sql/garment.sql`，禁止再新增 `*.sql`（如升级脚本、临时脚本）。
- 该文件必须与本地 MySQL `garment` 库**完全一致**（结构 + 数据）。
- 当用户说「同步数据库」时：执行 `./scripts/sync-db.sh`，把本地库真实数据全量导出覆盖 `garment.sql`。
- 若发现本地库与 `garment.sql` 不一致（表结构、菜单、数据行数等），必须**先提醒用户同步**，再继续后续改动。
- 数据库变更一律先在本地 MySQL 执行，验证通过后再导出 SQL，不要手写增量 SQL 交付。
- 配置项：MySQL `garment`（root/123456）、Redis 本机、验证码已关闭。

## 数据规范
- 不允许保留若依自带演示数据：demo 账号 `ry`、`common` 角色、若依 demo 部门/岗位/公告/定时任务、登录日志、操作日志、`gen_table` 演示数据、「系统工具」演示菜单。
- 业务数据只保留与服装厂生产相关的合理数据，且必须覆盖需求文档的主要场景：
  选品研发（含未生成 SKU 的款）、商品/SKU、BOM、成本、供应商、采购（待入库/部分入库/已入库）、
  物资库存（含安全库存预警与负库存）、裁货（待分扎/已入库/已完成）、分扎、领片、交后道、检验、包装、
  成品库存（裁片/产线/成品）、出库（正常/作废）、出货、退货、盘点（进行中/已完成）、次品、费用。
- 测试数据要有业务含义（真实款号、颜色、尺码、金额、日期），不要使用 `TEST`、`123` 之类的占位数据。

## 常用命令
- 后端打包：`cd ruoyi-backend && mvn -DskipTests package`
- 后端启动：`java -jar ruoyi-admin/target/ruoyi-admin.jar`（8080）
- 前端启动：`cd ruoyi-ui && npm run dev`（8090）
- 前端构建：`cd ruoyi-ui && npm run build:prod`
- 数据库同步：`./scripts/sync-db.sh`
