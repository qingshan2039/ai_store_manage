/**
 * 主数据测试数据灌入脚本（通过 REST API 写入开发库 ai_store_manage）。
 * 覆盖：供应商 / 仓库 / 库区 / 托盘类型（ISO 6780 大中小三规格）。
 *
 * 用法：后端在 8080 启动后执行  node scripts/seed-master-data.mjs
 * 幂等：编码重复（409）视为已存在，自动跳过并复用已有记录。
 */
const BASE = process.env.API_BASE || 'http://localhost:8080';

async function post(path, body) {
  const res = await fetch(BASE + path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (res.status === 201) return res.json();
  if (res.status === 409) return { __duplicate: true };
  const text = await res.text();
  throw new Error(`POST ${path} -> ${res.status}: ${text}`);
}

async function findByCode(path, code) {
  const res = await fetch(`${BASE}${path}?keyword=${encodeURIComponent(code)}&pageSize=100`);
  const data = await res.json();
  return (data.items || []).find((it) => it.code === code) || null;
}

/** 创建或复用（按 code 唯一）。返回带 id 的记录。 */
async function ensure(path, body, label) {
  const created = await post(path, body);
  if (!created.__duplicate) {
    console.log(`  + ${label}：${body.code} ${body.name}`);
    return created;
  }
  const existing = await findByCode(path, body.code);
  console.log(`  = ${label}：${body.code}（已存在，复用 id=${existing?.id}）`);
  return existing;
}

/** 创建或复用 SPU（按 spuCode），返回带 id 的记录。 */
async function ensureSpu(spu) {
  const created = await post('/api/spus', spu);
  if (!created.__duplicate) {
    console.log(`  + SPU：${spu.spuCode} ${spu.spuName}`);
    return created;
  }
  const res = await fetch(`${BASE}/api/spus?keyword=${encodeURIComponent(spu.spuCode)}&pageSize=100`);
  const data = await res.json();
  const existing = (data.items || []).find((it) => it.spuCode === spu.spuCode) || null;
  console.log(`  = SPU：${spu.spuCode}（已存在 id=${existing?.id}）`);
  return existing;
}

/** 创建或复用 SKU（按 skuCode）。 */
async function ensureSku(sku) {
  const created = await post('/api/skus', sku);
  console.log(`  ${created.__duplicate ? '=' : '+'} SKU：${sku.skuCode} ${sku.skuName}`);
}

/** POST，遇 409/404 不报错（返回 null）。 */
async function postSoft(path, body) {
  const res = await fetch(BASE + path, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) });
  if (res.status === 201) return res.json();
  if (res.status === 409 || res.status === 404) return null;
  throw new Error(`POST ${path} -> ${res.status}: ${await res.text()}`);
}

/** GET 列表全部 items（keyword 可选）。 */
async function listItems(path, keyword) {
  const q = keyword ? `${path}${path.includes('?') ? '&' : '?'}keyword=${encodeURIComponent(keyword)}&pageSize=100` : `${path}${path.includes('?') ? '&' : '?'}pageSize=100`;
  const data = await (await fetch(BASE + q)).json();
  return data.items || [];
}

/** 按某字段在列表中查一条。 */
async function findByField(path, field, value, keyword) {
  const items = await listItems(path, keyword ?? value);
  return items.find((it) => it[field] === value) || null;
}

async function main() {
  console.log('▶ 供应商');
  const suppliers = [
    { code: 'SUP-001', name: '中石化华东树脂供应商', address: '上海市金山区石化大道 1 号', contact: '王经理', phone: '021-58880001', email: 'sales@sinopec-east.com', remark: 'LLDPE 拉伸膜专用树脂' },
    { code: 'SUP-002', name: '江苏华美包装材料有限公司', address: '江苏省苏州市工业园区包装路 18 号', contact: '李主管', phone: '0512-66660002', email: 'huamei@pack.com', remark: '纸箱 / 纸管供应' },
    { code: 'SUP-003', name: '宁波塑源添加剂有限公司', address: '浙江省宁波市北仑区化工园 9 号', contact: '张工', phone: '0574-99990003', email: 'add@suyuan.com', remark: '色母 / 增粘剂' },
  ];
  for (const s of suppliers) await ensure('/api/suppliers', s, '供应商');

  console.log('▶ 仓库');
  const warehouses = [
    { code: 'WH-RAW', name: '原料仓库', type: 'RAW', remark: '存放 LLDPE 树脂等原材料' },
    { code: 'WH-SEMI', name: '半成品仓库', type: 'SEMI', remark: '存放母卷 / 半成品' },
    { code: 'WH-FIN', name: '成品仓库', type: 'FINISHED', remark: '存放成品缠绕膜，待发货' },
  ];
  const whMap = {};
  for (const w of warehouses) {
    const rec = await ensure('/api/warehouses', w, '仓库');
    whMap[w.code] = rec.id;
  }

  console.log('▶ 库区（关联仓库，编码在仓库内唯一）');
  const zones = [
    { wh: 'WH-RAW', code: 'Z-A', name: '收货暂存区', type: '收货' },
    { wh: 'WH-RAW', code: 'Z-B', name: '树脂存储区', type: '存储' },
    { wh: 'WH-SEMI', code: 'Z-A', name: '母卷存储区', type: '存储' },
    { wh: 'WH-FIN', code: 'Z-A', name: '成品拣货区', type: '拣货' },
    { wh: 'WH-FIN', code: 'Z-B', name: '发货月台区', type: '发货' },
  ];
  for (const z of zones) {
    const warehouseId = whMap[z.wh];
    const created = await post('/api/zones', { warehouseId, code: z.code, name: z.name, type: z.type });
    console.log(`  ${created.__duplicate ? '=' : '+'} 库区：${z.wh}/${z.code} ${z.name}`);
  }

  console.log('▶ 托盘类型（ISO 6780 国际标准 大/中/小）');
  const pallets = [
    { code: 'PLT-L', name: '大托盘 1200×1000', length: 1200, width: 1000, tareWeight: 25, maxLoad: 1500, maxStack: 5, remark: 'ISO6780 标准（北美 / 通用）' },
    { code: 'PLT-M', name: '欧标托盘 1200×800', length: 1200, width: 800, tareWeight: 25, maxLoad: 1500, maxStack: 5, remark: 'ISO6780 / 欧标 EUR1（EPAL）' },
    { code: 'PLT-S', name: '小托盘 800×600', length: 800, width: 600, tareWeight: 12, maxLoad: 500, maxStack: 4, remark: 'ISO6780 半欧托（EUR6）' },
  ];
  for (const p of pallets) await ensure('/api/pallet-types', p, '托盘');

  // ── 物料目录（Phase A）：SPU + SKU（品类由 V5 迁移内置种子）──
  console.log('▶ 物料目录（SPU + SKU，覆盖 5 类原材料）');
  // 每个 SPU 下挂若干 SKU；纸管演示"同尺寸不同规格 = 两个 SKU"（需求1）
  const catalog = [
    {
      spu: { spuCode: 'CORE-3IN', spuName: '3寸纸管', categoryCode: 'CORE', baseUnit: 'PCS' },
      skus: [
        { skuCode: 'PC-340480-A', skuName: '纸管 340x480x5mm 规格A', itemType: 'RAW', lengthMm: 340, widthMm: 480, thicknessMm: 5, spec: { material: '再生纸', grade: 'A' } },
        { skuCode: 'PC-340480-B', skuName: '纸管 340x480x5mm 规格B', itemType: 'RAW', lengthMm: 340, widthMm: 480, thicknessMm: 5, spec: { material: '原浆纸', grade: 'B' } },
      ],
    },
    {
      spu: { spuCode: 'FILM-PE', spuName: 'PE保鲜膜', categoryCode: 'FILM', baseUnit: 'ROLL' },
      skus: [
        { skuCode: 'FILM-300', skuName: '保鲜膜 300mm', itemType: 'FINISHED', widthMm: 300, rollLengthM: 300, spec: { material: 'PE' } },
        { skuCode: 'FILM-450', skuName: '保鲜膜 450mm', itemType: 'FINISHED', widthMm: 450, rollLengthM: 300, spec: { material: 'PE' } },
      ],
    },
    {
      spu: { spuCode: 'FOIL-STD', spuName: '标准铝箔', categoryCode: 'FOIL', baseUnit: 'ROLL' },
      skus: [
        { skuCode: 'FOIL-300', skuName: '铝箔 300mm', itemType: 'RAW', widthMm: 300, rollLengthM: 150, gsm: 40 },
      ],
    },
    {
      spu: { spuCode: 'PAPER-KRAFT', spuName: '牛皮纸皮', categoryCode: 'PAPER', baseUnit: 'PCS' },
      skus: [
        { skuCode: 'PAPER-K-A4', skuName: '牛皮纸皮 A4', itemType: 'RAW', lengthMm: 297, widthMm: 210, gsm: 120 },
      ],
    },
    {
      spu: { spuCode: 'BAKE-STD', spuName: '标准烘焙纸', categoryCode: 'BAKING', baseUnit: 'PCS' },
      skus: [
        { skuCode: 'BAKE-400', skuName: '烘焙纸 400x600', itemType: 'FINISHED', lengthMm: 400, widthMm: 600, gsm: 38 },
      ],
    },
  ];
  for (const c of catalog) {
    const spu = await ensureSpu(c.spu);
    if (!spu) continue;
    for (const s of c.skus) await ensureSku({ ...s, spuId: spu.id });
  }

  // ── 需求②演示（Phase B/C）：纸管 PC-340480-A 的包装链 + 库位 + 两托（500整托 / 480尾托）──
  console.log('▶ 库存统计演示（需求②：500 整托 + 480 尾托）');
  const demoSku = await findByField('/api/skus', 'skuCode', 'PC-340480-A');
  const whRaw = await findByField('/api/warehouses', 'code', 'WH-RAW');
  const pltL = await findByField('/api/pallet-types', 'code', 'PLT-L');
  if (demoSku && whRaw && pltL) {
    // 包装层级 卷/箱/托 + 关系 托→箱 500
    await postSoft('/api/packaging-levels', { skuId: demoSku.id, levelName: '卷', levelSeq: 1, unitCode: 'ROLL', isBaseUnit: 1 });
    await postSoft('/api/packaging-levels', { skuId: demoSku.id, levelName: '箱', levelSeq: 2, unitCode: 'CTN' });
    await postSoft('/api/packaging-levels', { skuId: demoSku.id, levelName: '托', levelSeq: 3, unitCode: 'PLT' });
    const levels = await listItems(`/api/packaging-levels?skuId=${demoSku.id}`);
    const box = levels.find((l) => l.levelName === '箱');
    const plt = levels.find((l) => l.levelName === '托');
    if (box && plt) await postSoft('/api/packaging-relations', { parentLevelId: plt.id, childLevelId: box.id, childQty: 500, isFixedQty: 1 });
    // 库位 + 两个托盘
    await postSoft('/api/locations', { warehouseId: whRaw.id, code: 'A-01-01', locType: '货架' });
    await postSoft('/api/lpns', { lpnCode: 'SSCC-DEMO-1', palletTypeId: pltL.id, warehouseId: whRaw.id });
    await postSoft('/api/lpns', { lpnCode: 'SSCC-DEMO-2', palletTypeId: pltL.id, warehouseId: whRaw.id });
    const lpn1 = await findByField('/api/lpns', 'lpnCode', 'SSCC-DEMO-1');
    const lpn2 = await findByField('/api/lpns', 'lpnCode', 'SSCC-DEMO-2');
    // 库存无唯一约束，已存在则跳过，避免重复
    const existing = await listItems(`/api/inventory?skuId=${demoSku.id}`);
    if (existing.length === 0 && lpn1 && lpn2) {
      await post('/api/inventory', { skuId: demoSku.id, lpnId: lpn1.id, qtyOnHand: 500 });
      await post('/api/inventory', { skuId: demoSku.id, lpnId: lpn2.id, qtyOnHand: 480 });
      console.log('  + 库存：SSCC-DEMO-1=500(整托), SSCC-DEMO-2=480(尾托)');
    } else {
      console.log(`  = 库存已存在(${existing.length} 条)，跳过`);
    }
  } else {
    console.log('  ! 缺少 SKU/仓库/托盘类型，跳过演示');
  }

  console.log('\n✓ 主数据灌入完成');
}

main().catch((e) => {
  console.error('✗ 灌入失败：', e.message);
  process.exit(1);
});
