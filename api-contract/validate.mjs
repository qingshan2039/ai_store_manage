/**
 * OpenAPI 契约校验测试。
 * 使用 swagger-parser 对 openapi.yaml 做结构校验 + $ref 解析 + OpenAPI 3.0 合规校验。
 * 同时断言关键端点存在，作为契约的“回归测试”。
 *
 * 运行：npm test （或 npm run validate）
 */
import SwaggerParser from '@apidevtools/swagger-parser';

const FILE = new URL('./openapi.yaml', import.meta.url).pathname;

/** 契约必须包含的端点（方法 + 路径 → operationId） */
const REQUIRED_OPERATIONS = [
  ['post', '/api/users', 'createUser'],
  ['get', '/api/users', 'listUsers'],
  ['post', '/api/departments', 'createDepartment'],
  ['get', '/api/departments', 'listDepartments'],
  ['get', '/api/departments/{id}', 'getDepartmentById'],
  ['put', '/api/departments/{id}', 'updateDepartment'],
  ['delete', '/api/departments/{id}', 'deleteDepartment'],
  ['patch', '/api/departments/{id}/status', 'updateDepartmentStatus'],
  ['post', '/api/customers', 'createCustomer'],
  ['get', '/api/customers', 'listCustomers'],
  ['get', '/api/customers/{id}', 'getCustomerById'],
  ['put', '/api/customers/{id}', 'updateCustomer'],
  ['delete', '/api/customers/{id}', 'deleteCustomer'],
  ['patch', '/api/customers/{id}/status', 'updateCustomerStatus'],
  ['post', '/api/suppliers', 'createSupplier'],
  ['get', '/api/suppliers', 'listSuppliers'],
  ['post', '/api/warehouses', 'createWarehouse'],
  ['get', '/api/warehouses', 'listWarehouses'],
  ['post', '/api/zones', 'createZone'],
  ['get', '/api/zones', 'listZones'],
  ['post', '/api/pallet-types', 'createPalletType'],
  ['get', '/api/pallet-types', 'listPalletTypes'],
  ['post', '/api/material-categories', 'createMaterialCategory'],
  ['get', '/api/material-categories', 'listMaterialCategories'],
  ['post', '/api/spus', 'createSpu'],
  ['get', '/api/spus', 'listSpus'],
  ['post', '/api/skus', 'createSku'],
  ['get', '/api/skus', 'listSkus'],
  ['get', '/api/skus/{id}', 'getSkuById'],
  ['put', '/api/skus/{id}', 'updateSku'],
  ['delete', '/api/skus/{id}', 'deleteSku'],
  ['patch', '/api/skus/{id}/status', 'updateSkuStatus'],
];

/** SKU item_type 枚举（原料/半成品/成品） */
const EXPECTED_ITEM_TYPES = ['RAW', 'SEMI', 'FINISHED'];

function fail(msg) {
  console.error('✗ ' + msg);
  process.exit(1);
}

const api = await SwaggerParser.validate(FILE).catch((err) => fail('OpenAPI 校验失败: ' + err.message));

// 1) 基本结构
if (!api.openapi?.startsWith('3.')) fail('openapi 版本应为 3.x，实际: ' + api.openapi);

// 2) 关键端点 + operationId
for (const [method, path, opId] of REQUIRED_OPERATIONS) {
  const op = api.paths?.[path]?.[method];
  if (!op) fail(`缺少端点 ${method.toUpperCase()} ${path}`);
  if (op.operationId !== opId) fail(`${method.toUpperCase()} ${path} 的 operationId 应为 ${opId}，实际: ${op.operationId}`);
}

// 3) 部门类型枚举完整（8 类）
const deptTypeEnum = api.components?.schemas?.DepartmentType?.enum ?? [];
const EXPECTED_TYPES = ['WAREHOUSE', 'TRANSPORT', 'SALES', 'PRODUCTION', 'OFFICE', 'HR', 'FINANCE', 'MANAGEMENT'];
for (const t of EXPECTED_TYPES) {
  if (!deptTypeEnum.includes(t)) fail(`DepartmentType 枚举缺少 ${t}`);
}

// 4) 物料 item_type 枚举完整（3 类）
const itemTypeEnum = api.components?.schemas?.ItemType?.enum ?? [];
for (const t of EXPECTED_ITEM_TYPES) {
  if (!itemTypeEnum.includes(t)) fail(`ItemType 枚举缺少 ${t}`);
}

const paths = Object.keys(api.paths ?? {}).length;
const schemas = Object.keys(api.components?.schemas ?? {}).length;
console.log(`✓ openapi.yaml 校验通过：openapi=${api.openapi}, paths=${paths}, schemas=${schemas}, 关键端点 ${REQUIRED_OPERATIONS.length} 个齐全, 部门类型 ${EXPECTED_TYPES.length} 类齐全`);
