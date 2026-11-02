package wang.liangchen.matrix.product.service.seed;

import wang.liangchen.matrix.product.domain.aggregate.attribute.*;
import wang.liangchen.matrix.shop.product.domain.aggregate.brand.Brand;
import wang.liangchen.matrix.product.domain.aggregate.category.*;
import wang.liangchen.matrix.product.domain.aggregate.producttype.*;
import wang.liangchen.matrix.shop.product.domain.shared.attribute.AttributeDataType;
import wang.liangchen.matrix.product.domain.shared.id.*;
import wang.liangchen.matrix.product.infrastructure.south.adapter.memory.InMemoryCatalogAdapter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;
import java.util.*;

@Configuration
@Profile("demo")
public class DemoCatalogSeed {
    public static final UUID TENANT_ID = UUID.fromString("aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa");
    public static final UUID PRODUCT_TYPE_ID = UUID.fromString("10000000-0000-4000-8000-000000000001");
    public static final UUID BRAND_ID = UUID.fromString("20000000-0000-4000-8000-000000000001");
    public static final UUID CATEGORY_ID = UUID.fromString("30000000-0000-4000-8000-000000000001");
    public static final UUID MATERIAL_ATTRIBUTE_ID = UUID.fromString("40000000-0000-4000-8000-000000000001");
    public static final UUID GENDER_ATTRIBUTE_ID = UUID.fromString("40000000-0000-4000-8000-000000000002");
    public static final UUID COLOR_ATTRIBUTE_ID = UUID.fromString("40000000-0000-4000-8000-000000000003");
    public static final UUID SIZE_ATTRIBUTE_ID = UUID.fromString("40000000-0000-4000-8000-000000000004");

    @Bean ApplicationRunner seedDemoCatalog(InMemoryCatalogAdapter store) {
        return args -> {
            Attribute material = Attribute.create(new AttributeId(MATERIAL_ATTRIBUTE_ID), "material", "材质", "服装面料", AttributeDataType.ENUM, Attribute.ValueMode.ENUM_OPTIONS, Attribute.Cardinality.SINGLE, null, false,
                    List.of(new AttributeOption("COTTON", "棉", null, true), new AttributeOption("POLYESTER", "聚酯纤维", null, true)));
            Attribute gender = Attribute.create(new AttributeId(GENDER_ATTRIBUTE_ID), "gender", "适用性别", "目标人群", AttributeDataType.ENUM, Attribute.ValueMode.ENUM_OPTIONS, Attribute.Cardinality.SINGLE, null, false,
                    List.of(new AttributeOption("MEN", "男", null, true), new AttributeOption("WOMEN", "女", null, true), new AttributeOption("UNISEX", "通用", null, true)));
            Attribute color = Attribute.create(new AttributeId(COLOR_ATTRIBUTE_ID), "color", "颜色", "SKU 颜色", AttributeDataType.ENUM, Attribute.ValueMode.ENUM_OPTIONS, Attribute.Cardinality.SINGLE, null, false,
                    List.of(new AttributeOption("RED", "红色", "#e53935", true), new AttributeOption("BLACK", "黑色", "#111111", true), new AttributeOption("WHITE", "白色", "#ffffff", true)));
            Attribute size = Attribute.create(new AttributeId(SIZE_ATTRIBUTE_ID), "size", "尺码", "服装尺码", AttributeDataType.ENUM, Attribute.ValueMode.ENUM_OPTIONS, Attribute.Cardinality.SINGLE, null, false,
                    List.of(new AttributeOption("M", "M", null, true), new AttributeOption("L", "L", null, true), new AttributeOption("XL", "XL", null, true)));
            store.put(material); store.put(gender); store.put(color); store.put(size);
            ProductType type = ProductType.create(new ProductTypeId(PRODUCT_TYPE_ID), "CLOTHING", "服装", "服装类商品", List.of(
                    rule(material, ProductTypeAttribute.Scope.SPU, ProductTypeAttribute.Role.NORMAL, true, List.of("COTTON", "POLYESTER"), 10),
                    rule(gender, ProductTypeAttribute.Scope.SPU, ProductTypeAttribute.Role.NORMAL, true, List.of("MEN", "WOMEN", "UNISEX"), 20),
                    rule(color, ProductTypeAttribute.Scope.SKU, ProductTypeAttribute.Role.VARIANT_DIMENSION, true, List.of("RED", "BLACK", "WHITE"), 30),
                    rule(size, ProductTypeAttribute.Scope.SKU, ProductTypeAttribute.Role.VARIANT_DIMENSION, true, List.of("M", "L", "XL"), 40)));
            type.publishDraft(); store.put(type);
            store.put(Brand.create(new BrandId(BRAND_ID), "NIKE", "Nike", "Sportswear brand", null, "https://www.nike.com", "US"));
            store.put(Category.create(new CategoryId(CATEGORY_ID), "APPAREL", null, "服装", "apparel", "服装分类", null, 10,
                    new CategoryProductTypePolicy(true, Set.of(new ProductTypeId(PRODUCT_TYPE_ID)))));
        };
    }
    private static ProductTypeAttribute rule(Attribute a, ProductTypeAttribute.Scope scope, ProductTypeAttribute.Role role,
            boolean required, List<String> allowed, int order) {
        return new ProductTypeAttribute(a.id(), a.code(), a.dataType(), a.cardinality() == Attribute.Cardinality.SINGLE ? ProductTypeAttribute.Cardinality.SINGLE : ProductTypeAttribute.Cardinality.MULTIPLE,
                scope, role, required, true, true, true, false, order, allowed);
    }
}
