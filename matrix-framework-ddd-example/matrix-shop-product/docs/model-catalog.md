# 模型清单与属性示例

模型的可直接阅读样例位于 `docs/examples/`：
- `attribute-material.json`、`attribute-color.json`：Attribute 与 AttributeOption。
- `product-type-clothing.json`：ProductType、ProductTypeVersion、ProductTypeAttribute。
- `brand-nike.json`：Brand。
- `category-apparel.json`：Category 与 CategoryProductTypePolicy。
- `spu-nike-tshirt.json`：Spu、SpuCategoryAssignment、VariantConfiguration、VariantDimension、MediaReference、SPU AttributeValue。
- `sku-nike-tshirt-red-m.json`：Sku、VariantKey、Barcode、PackageSpecification、Measure、SKU AttributeValue。

## 主要模型字段摘要

| 模型 | 关键属性 | 业务示例 |
|---|---|---|
| Attribute | id, code, name, dataType, valueMode, cardinality, unitCode, options, status | `color / ENUM / RED, BLACK, WHITE` |
| AttributeOption | code, name, swatch, active | `RED / 红色 / #e53935` |
| ProductType | id, code, name, status, draftAttributes, publishedVersions | `CLOTHING / 服装` |
| ProductTypeVersion | productTypeId, version, publishedAt, attributes | `CLOTHING v1` |
| ProductTypeAttribute | attributeId, attributeCode, dataType, scope, role, required, filterable, allowedOptionCodes | `color / SKU / VARIANT_DIMENSION` |
| Brand | id, code, name, logoMediaId, website, countryCode, status | `NIKE / Nike / US` |
| Category | id, code, parentCategoryId, name, slug, productTypePolicy, status | `APPAREL / 服装` |
| Spu | id, tenantId, code, productTypeId/version, brandId, name, attributes, categories, variantConfiguration, status | `NIKE-TEE-001` |
| VariantConfiguration | dimensions | `color=[RED,BLACK], size=[M,L,XL]` |
| VariantDimension | attributeId, attributeCode, optionCodes, displayOrder | `color=[RED,BLACK]` |
| SpuCategoryAssignment | categoryId, primary, assignedAt | `APPAREL / primary=true` |
| Sku | id, tenantId, spuId, code, variantKey, attributes, barcodes, packageSpecification, status | `NIKE-TEE-001-RED-M` |
| VariantKey | value | `color=RED|size=M` |
| Barcode | type, value | `EAN13 / 6901234567892` |
| PackageSpecification | weight, length, width, height | `0.25 KG / 30×22×2 CM` |
| Measure | value, unitCode | `0.25 / KG` |
| MediaReference | mediaId, role, altText, sortOrder | `media-spu-001 / MAIN` |
| AttributeValue | attributeId + typed value | `material=COTTON`, `size=M` |
