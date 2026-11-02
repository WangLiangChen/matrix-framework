# Product Catalog Architecture

## 模块依赖方向

```text
                 contract
                 ^      ^
                 |      |
          interface    client-sdk
                 |
             application ----> domain <---- infrastructure
                 ^                         /
                 |________________________/
                         service (composition root)
```

更精确地说：Domain 不依赖任何业务模块；Application 依赖 Domain；Interface 依赖 Application + Contract；Client SDK 只依赖 Contract；Infrastructure 依赖 Domain 并实现南向 Port；Service 组合 Interface、Infrastructure、Application、Client SDK。

## 模型边界
- `Attribute` 是平台全局可复用的属性定义；`AttributeOption` 是其内部选项。
- `ProductType` 是平台级商品类型聚合；`ProductTypeVersion` 是不可变发布快照；`ProductTypeAttribute` 定义属性 scope/role/required/filtering 等。
- `Spu` 是租户级标准商品；保存商品公共属性值，绑定 ProductType 版本，管理分类和 VariantConfiguration。
- `Sku` 是租户级具体规格聚合；通过 SpuId 关联，保存 SKU 属性值、条码、包装规格和媒体。
- `Brand` 和 `Category` 是平台级主数据。Category 可以配置允许的 ProductType 集合。

## 属性流转

```text
Attribute (global definition/options)
          |
          v
ProductTypeVersion / ProductTypeAttribute (usage rules snapshot)
          |
          +------> Spu.attributes (SPU-scope values)
          |
          +------> Spu.variantConfiguration (chosen values for variant dimensions)
                              |
                              v
                       Sku.attributes (one actual combination)
```

## 关键不变量
- 一个 SPU 对应一个 ProductType + 版本；ProductType 可以有多个 SPU。
- 发布的 ProductType 版本不可变；SPU 不随新版本自动迁移。
- Variant dimension 必须是单值 SKU ENUM 属性。
- SKU 必须为 SPU 配置的每个规格维度提供一个已选值；VariantKey 由维度值规范化生成。
- SPU 发布只校验 SPU 自身完整性和规格配置，不依赖 SKU 数量。
- DB 唯一约束是并发正确性的最终保障。
