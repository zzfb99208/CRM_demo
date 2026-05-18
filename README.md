# CRM Demo — 海外 IVD 业务订单管理系统

演示从 PO（采购订单）→ PI（形式发票）→ PL（装箱单）全链路流转的 CRM 系统。面向中国体外诊断试剂公司（Coyote Bioscience）的海外分销业务场景。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端 | Spring Boot + MyBatis-Plus | 3.4.x / 3.5.x |
| 前端 | Vue 3 + Element Plus + Vite | 3.5.x / 2.9.x |
| 数据库 | MySQL | 8.0 |
| Excel | Apache POI | 5.2.x |

## 核心功能

```
客户签约 → 导入 PO Excel → 自动生成 PI (编码转换) → 导出 PI
                                                         │
                                                   外部修改 PI 后重新导入
                                                         │
                                                   打包算法 → 生成 PL → 导出 PL
```

- **PO Excel 导入**：自动解析签约客户发来的 Excel，识别表头 + 行项目，校验产品编码
- **PO → PI 转换**：通过 CAT.# 和 Product Name 匹配 Menarini code → Ref No. 编码映射
- **PI 重新导入**：导出 PI → 外部修改 → 重新导入 → 差异对比（按行号匹配，橙色标记变更）
- **打包算法**：6 盒/标准箱，余数 < 4 用小箱，仪器类每台一箱，全局箱号递增
- **PI/PL 导出**：按 Invoice 和 Packing List 原样格式导出 Excel

## 项目结构

```
CRM_demo/
├── README.md                       ← 本文件
├── 启动指南.md                     ← 本地启动步骤
├── crm-demo-backend/               ← Spring Boot 后端
│   └── src/main/java/com/crm/demo/
│       ├── controller/             ← REST API
│       ├── service/DemoService.java ← 核心业务（PO导入/PI转换/PL打包）
│       ├── entity/                 ← 数据实体
│       ├── mapper/                 ← MyBatis-Plus Mapper
│       └── dto/                    ← 数据传输对象
├── crm-demo-web/                   ← Vue 3 前端
│   └── src/
│       ├── views/                  ← 8 个业务页面
│       ├── api/                    ← Axios 请求封装
│       └── router/                 ← 路由配置
├── database/                       ← SQL 脚本
│   ├── schema.sql                  ← 10 张表 DDL
│   └── seed.sql                    ← 种子数据
├── scripts/                        ← 工具脚本
│   └── reset_mysql_password.bat    ← MySQL 密码重置
└── docs/                           ← 文档
    ├── design/                     ← 设计文档 + LLM 提示词
    ├── samples/                    ← 原始 Excel 示例
    └── CRM_字段分析.md             ← 数据字段分析
```

## 快速开始

详细步骤见 **[启动指南.md](启动指南.md)**。

1. 启动 MySQL，执行 `database/schema.sql` 和 `database/seed.sql`
2. 启动后端：`cd crm-demo-backend && mvn spring-boot:run`
3. 启动前端：`cd crm-demo-web && npm install && npm run dev`
4. 打开 `http://localhost:5173`

## API 一览

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/customers/search?keyword=` | 搜索客户 |
| POST | `/api/purchase-orders/import` | 上传 PO Excel 导入 |
| POST | `/api/proforma-invoices/generate/{poId}` | PO → PI 自动转换 |
| GET | `/api/proforma-invoices/{id}/export` | 导出 PI (Invoice 格式) |
| POST | `/api/proforma-invoices/{id}/import` | 重新导入修改后的 PI |
| POST | `/api/packing-lists/generate/{piId}` | 打包算法生成 PL |
| GET | `/api/packing-lists/{id}/export` | 导出 PL (Packing List 格式) |
| GET | `/api/trace/{contractNo}` | 全链路追溯 |

## 设计文档

| 文档 | 说明 |
|------|------|
| [产品需求文档](docs/design/CRM_产品需求文档.md) | Demo 业务需求 + 7 步操作流程 |
| [数据库设计](docs/design/CRM_数据库设计.md) | 10 张表 DDL + ER 图 + LLM 提示词 |
| [Java 后端设计](docs/design/CRM_Java后端设计.md) | API 清单 + 打包算法伪代码 + LLM 提示词 |
| [Vue 前端设计](docs/design/CRM_Vue前端设计.md) | 8 页面布局 + 路由设计 + LLM 提示词 |
| [字段分析](docs/CRM_字段分析.md) | PO/PI/PL 关键字段 + Customer/Product 实体 |

> 每份设计文档末尾均附有**可交付其他大模型生成代码的提示词**，可直接复制使用。
