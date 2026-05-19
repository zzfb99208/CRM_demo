# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CRM Demo for an IVD (in-vitro diagnostics) company — simulates PO → PI → PL document flow for overseas trade. Two roles: sales (`sales/123456`) and approver (`approver/123456`).

## Build & Run

**Backend** (Spring Boot 3.4 + MyBatis-Plus 3.5, port 8080):
```bash
cd crm-demo-backend
set PATH=C:\Users\张富渤\maven\apache-maven-3.9.9\bin;%PATH%
mvn spring-boot:run
```

**Frontend** (Vue 3 + Element Plus + Vite, port 5173):
```bash
cd crm-demo-web
npm run dev
```

**Database**: MySQL 8.0 `crm_demo`, user `root`/`root`, service `MYSQL80`. Schema in `database/schema.sql`, seed in `database/seed.sql`.

**Full startup guide**: `启动指南.md` (Chinese).

## Architecture

### Backend (single-service pattern)

All core business logic lives in **`DemoService.java`** (~700 lines), a single `@Service` with injected mappers:

| Method group | Purpose |
|-------------|---------|
| `importPoExcel()` | Parse uploaded PO Excel (Apache POI) → create PO + items |
| `generatePI()` | PO → PI conversion: CAT.# → Menarini code mapping via `product` table |
| `updatePIItem()` / `updatePIHeader()` | PI online edit (items / header fields) |
| `reimportPI()` | Re-import modified PI Excel → diff by line number |
| `submitPIForApproval()` / `approvePI()` | PI submission → approver review |
| `generatePL()` | Packing algorithm: 6 kits/carton, <4 remaining→small box, carton #s sequential |
| `exportPI()` / `exportPL()` | Generate `.xlsx` in Invoice / Packing List format |

10 entity classes (no Lombok — JDK 24 incompatible), 10 MyBatis-Plus mappers, 7 controllers.

### Frontend (8 pages)

- `Login.vue` → token stored in `localStorage`
- Router guard: no token → redirect `/login`
- Role-based sidebar in `App.vue`: SALES sees PO/PI modules, APPROVER sees approval workbench
- PI detail page (`ProformaInvoiceDetail.vue`) supports edit mode (inline header + line items), ERP schedule panel, submit/export/reimport buttons
- `ApprovalWorkbench.vue`: pending tab + history tab with status filter and rejection reason column

### Database (10 tables)

`customer` → `customer_contact`  
`product` (106 rows, 5 categories)  
`inventory` (lot_no + expiry_date)  
`purchase_order` + `purchase_order_item`  
`proforma_invoice` + `proforma_invoice_item`  
`packing_list` + `packing_list_item`  
`user` (username/password/role)  
`production_schedule` (ERP mock data)

### Key business rules

- **PO→PI mapping**: `CAT.#` (coyote_code) → matches `product.coyote_code` → returns `menarini_code` as `Ref No.`; falls back to `product_name` keyword match
- **Packing algorithm**: instrument (`unit`) = 1 carton/unit, `57*45*55cm`. assay/consumable (`kit`) = 6 kits/standard carton `57*26*37cm`, remaining <4→small carton `35*38*26cm`, ≥4→standard
- **Document numbering**: `IT-{CustomerCode}-{yyyyMMdd}`, auto-suffix `-01` for duplicates
- **Status flow**: PO: `IMPORTED→APPROVED→PI_GENERATED→COMPLETED`. PI: `DRAFT→APPROVED→SUBMITTED→APPROVED/REJECTED→PACKING_GENERATED`. PL: `DRAFT→APPROVED`

## Critical Gotchas

- **JDK 24 + Lombok**: Lombok is completely incompatible — all entities have manual getters/setters. Never add Lombok back.
- **`Map.of()` rejects null values**: Use `new LinkedHashMap<>()` + `.put()` instead. A `Map.of("rejectReason", null)` threw NPE in `approvePI()`.
- **Excel scientific notation**: Numeric cells like `2004009001` → `toString()` gives `2.004009001E9`. Use `DataFormatter.formatCellValue()` + BigDecimal fallback.
- **`@Transactional` requires public method**: Keep service methods `public`.
- **MySQL encoding**: JDBC URL must use `characterEncoding=UTF-8`, never `utf8mb4` (JDK 24 doesn't recognize it).
- **PI edit stock check**: `saveAll()` in `ProformaInvoiceDetail.vue` checks `stockMap` before saving and shows `ElMessageBox.confirm` for over-stock items.

## Design Docs

All in `docs/design/`: PRD, DB design, Java design, Vue design. Each ends with an LLM prompt for regenerating code. Update these sync with code changes.

## Standardized Dev Workflow

`docs/标准化开发流程.md` defines a 4-phase process: PRD update → design doc sync → code implementation → review. Use this for all new features.

## Git

- Repo: `https://github.com/zzfb99208/CRM_demo`
- Stable backup branch: `stable-v1`
- Standard commit: `git add -A && git commit -m "message" && git push`
