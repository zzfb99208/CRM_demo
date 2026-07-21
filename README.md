# CRM Demo — 海外 IVD 业务 PO→PI→PL 订单管理系统

面向 IVD（体外诊断）行业的海外订单场景，演示从客户 PO（采购订单）→ PI（形式发票）→ PL（装箱单）全链路流转的 CRM 系统。以海外经销商采购场景为原型，支持 Excel 批量导入、智能编码映射、在线编辑审批、装箱算法自动生成 PL 等功能。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端 | Spring Boot + MyBatis-Plus | 3.4.x / 3.5.x |
| 前端 | Vue 3 + Element Plus + Vite | 3.5.x / 2.9.x |
| 数据库 | MySQL | 8.0 |
| Excel 解析 | Apache POI | 5.2.x |
| 构建工具 | Maven | 3.9+ |

## 核心功能

```
客户签约 → 导入 PO Excel → 自动生成 PI（编码转换）→ 导出 PI
                                                    ↓
                                              外部修改后重新导入 PI（差异对比）
                                                    ↓
                                             提交审核 → 审批 → 装箱算法生成 PL
```

- **PO Excel 导入**：支持客户 Excel 格式自动解析，智能识别表头与行项目
- **PO → PI 编码映射**：内部产品编码（CAT.#）→ 客户物料编码（Ref No.）自动匹配转换
- **PI 在线编辑**：支持表头与行项目在线修改，实时库存校验
- **PI 审核流程**：提交审核 → 审批人通过/驳回 → 驳回后可修改重新提交
- **PI 重新导入**：导出 PI → 外部修改 → 重新导入 → 按行号自动 diff，变更行高亮标记
- **装箱算法**：试剂 6 盒/标准箱（57×26×37cm），余数 < 4 用小箱（35×38×26cm），仪器 1 台/箱（57×45×55cm），全局箱号递增
- **双角色权限**：业务员（sales）操作 PO/PI，审批人（approver）审核 PI

## 业务流程图

```
客户管理 → 导入 PO Excel → 生成 PI
                              ↓
                        CAT.# → Ref No. 编码映射
                              ↓
                        PI 在线编辑 / 导出
                              ↓
                        提交审核 ← 驳回后修改重新提交
                        ↓       ↗
                      审批人审批
                        ↓
                      生成 Packing List
                        ↓
                      导出装箱单
```

## 项目结构

```
CRM_demo_ws/
├── README.md                              ← 本文件
├── crm-demo-backend/                      ← Spring Boot 后端
│   └── src/main/java/com/crm/demo/
│       ├── controller/                    ← REST API 控制器
│       │   ├── CustomerController.java    ←   客户查询
│       │   ├── PurchaseOrderController.java ← PO 导入/查询
│       │   ├── ProformaInvoiceController.java ← PI 生成/编辑/导出/重新导入
│       │   ├── PackingListController.java ← PL 生成/导出
│       │   ├── ApprovalController.java    ← 审批工作台
│       │   ├── ProductController.java     ← 产品查询
│       │   └── AuthController.java        ← 登录认证
│       ├── service/DemoService.java       ← 核心业务逻辑（~750 行）
│       ├── entity/                        ← 10 个数据实体
│       ├── mapper/                        ← MyBatis-Plus Mapper
│       └── dto/                           ← 数据传输对象
├── crm-demo-web/                          ← Vue 3 前端
│   └── src/
│       ├── views/                         ← 业务页面
│       │   ├── Login.vue                  ←   登录页
│       │   ├── customer/                  ←   客户管理
│       │   ├── product/                   ←   产品总表
│       │   ├── po/                        ←   采购订单（列表/导入/详情）
│       │   ├── pi/                        ←   形式发票（列表/详情/编辑/导出）
│       │   ├── pl/                        ←   装箱单（列表/详情）
│       │   └── approval/                  ←   审批工作台
│       ├── api/                           ← Axios 请求封装
│       ├── router/                        ← 路由配置（含 Token 守卫）
│       └── utils/                         ← 工具函数
├── database/                              ← SQL 脚本
│   ├── schema.sql                         ← 13 张表 DDL
│   ├── seed.sql                           ← 种子数据（客户 + 16 产品 + 库存）
│   ├── seed_v2_comprehensive.sql          ← v2 扩展种子数据
│   └── migration_v2_comprehensive.sql     ← v2 表结构迁移
├── import_products.sql                    ← 106 款产品完整导入
├── docs/                                  ← 文档
│   ├── design/                            ← 设计文档（PRD/数据库/后端/前端）
│   ├── samples/                           ← 示例 PO Excel 文件
│   └── CRM_字段分析.md                    ← 字段分析文档
└── scripts/                               ← 工具脚本
```

## 数据库设计

13 张数据表：

| 表名 | 用途 | 关键内容 |
|------|------|---------|
| `user` | 登录认证 | 用户名/密码/角色（SALES/APPROVER） |
| `customer` | 客户主数据 | 84 列，覆盖 IVD 外贸客户完整信息 |
| `customer_contact` | 客户联系人 | 关联客户 |
| `product` | 产品主数据 | 106 款产品，含内部编码（CAT.#）与客户物料编码 |
| `inventory` | 库存 | 批次/效期/库存量 |
| `purchase_order` | PO 主表 | 合同号/客户/交付条款 |
| `purchase_order_item` | PO 行项目 | 产品/数量/单价 |
| `proforma_invoice` | PI 主表 | 发票号/关联 PO/审批状态 |
| `proforma_invoice_item` | PI 行项目 | Ref No./数量/金额 |
| `packing_list` | PL 主表 | 运输方式/总箱数/总毛重 |
| `packing_list_item` | PL 行项目 | 箱号/尺寸/批号/效期 |
| `price_list` | 价格表主表 | 业务标签/区域/客户类型 |
| `price_list_item` | 价格表明细 | 产品价格/市场价 |

## 本地部署

### 前置环境

| 组件 | 要求 | 验证命令 |
|------|------|---------|
| JDK | 17+ | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| MySQL | 8.0 | `mysql -u root -p -e "SELECT 1"` |
| Node.js | 18+ | `node -v` |

### Step 1：创建数据库并导入数据

```bash
# 连接 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE crm_demo DEFAULT CHARACTER SET utf8mb4;

# 退出后导入 SQL
mysql -u root -p crm_demo < database/schema.sql
mysql -u root -p crm_demo < database/seed.sql
mysql -u root -p crm_demo < database/seed_v2_comprehensive.sql
mysql -u root -p crm_demo < import_products.sql
```

> **macOS 注意**：如果 `database/schema.sql` 导入时报 `Row size too large` 错误，需要在 MySQL 中先执行 `SET GLOBAL innodb_strict_mode = 0;` 再重新导入。

### Step 2：修改数据库连接配置

编辑 `crm-demo-backend/src/main/resources/application.yml`，将密码修改为你的 MySQL 密码：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/crm_demo?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8
    username: root
    password: 你的MySQL密码
```

### Step 3：启动后端

```bash
cd crm-demo-backend
mvn spring-boot:run
```

首次启动会下载 Maven 依赖，约 1~2 分钟。看到以下信息表示启动成功：

```
Started CrmDemoApplication in X.XXX seconds
```

后端运行在 **http://localhost:8080**。

验证：浏览器访问 `http://localhost:8080/api/customers/search?keyword=Demo`，应返回 JSON。

### Step 4：启动前端

打开**新的终端窗口**（不要关闭后端），执行：

```bash
cd crm-demo-web
npm install        # 首次需安装依赖
npm run dev
```

看到以下信息表示启动成功：

```
VITE v5.x.x  ready in xxx ms
➜  Local:   http://localhost:5173/
```

> **macOS 注意**：如果 `npm run dev` 报 `Permission denied`，执行 `chmod +x node_modules/.bin/*` 后重试。

### Step 5：登录使用

浏览器打开 **http://localhost:5173**。

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 业务员 | `sales` | `123456` |
| 审批人 | `approver` | `123456` |

## 完整操作演示

```
1. 以 sales/123456 登录
2. 客户管理 → 搜索 "Demo" → 查看客户详情
3. 点击 "导入 PO Excel" → 上传 docs/samples/ 下的示例 PO 文件
4. PO 详情 → 点击 "生成 PI"
5. PI 详情 → 可在线编辑 / 导出 Invoice / 提交审核
6. 退出 → 以 approver/123456 登录 → 审核工作台 → 通过或驳回 PI
7. （如驳回）重新以 sales 登录 → 修改 PI → 再次提交审核
8. PI 审批通过后 → 点击 "生成 Packing List"
9. PL 详情 → 查看自动装箱结果 → 导出 Packing List
```

## API 概览

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/login` | 用户登录 |
| GET | `/api/customers/search?keyword=` | 搜索客户 |
| GET | `/api/products` | 产品列表 |
| POST | `/api/purchase-orders/import` | 上传 PO Excel |
| GET | `/api/purchase-orders/{id}` | PO 详情 |
| POST | `/api/proforma-invoices/generate/{poId}` | PO → PI 自动转换 |
| GET | `/api/proforma-invoices/{id}` | PI 详情 |
| PUT | `/api/proforma-invoices/{id}` | 更新 PI 表头 |
| PUT | `/api/proforma-invoices/{piId}/items/{itemId}` | 更新 PI 行项目 |
| GET | `/api/proforma-invoices/{id}/export` | 导出 PI（Invoice 格式） |
| POST | `/api/proforma-invoices/{id}/reimport` | 重新导入修改后的 PI（差异对比） |
| POST | `/api/proforma-invoices/{id}/submit` | 提交 PI 审批 |
| GET | `/api/approvals/pending` | 待审批 PI 列表 |
| GET | `/api/approvals/history` | 审批历史 |
| POST | `/api/approvals/{piId}/approve` | 审批通过 |
| POST | `/api/approvals/{piId}/reject` | 驳回 |
| POST | `/api/packing-lists/generate/{piId}` | 生成装箱单 |
| GET | `/api/packing-lists/{id}` | PL 详情 |
| GET | `/api/packing-lists/{id}/export` | 导出 PL |
| GET | `/api/trace/{contractNo}` | 全链路追溯 |

## 技术亮点

- **编码映射引擎**：CAT.# → Ref No. 自动匹配，支持 fallback 到关键字模糊匹配
- **装箱算法**：按产品类型（仪器/试剂/耗材）自动分配不同规格外箱，全局箱号顺序编号
- **PI 重新导入差异检测**：按行号匹配，变更字段级 diff，前端橙色高亮标记
- **双角色权限**：Vue Router 导航守卫 + 后端 API 权限校验
- **库存实时校验**：编辑 PI 时展示当前库存，超出库存弹窗确认
- **单 Service 架构**：核心业务逻辑集中在 `DemoService.java`，便于理解整体流程

## 技术债务与改进方向

- **安全**：当前用户密码明文存储，生产使用需改为 BCrypt 加密
- **并发**：装箱算法未加分布式锁，高并发场景需优化
- **事务**：部分跨表操作需补全 `@Transactional` 注解
- **分页**：列表 API 未加分页，数据量大时需引入 PageHelper
- **测试**：缺少单元测试和集成测试
