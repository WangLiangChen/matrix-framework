package wang.liangchen.matrix.shop.product.domain.aggregate.spu;
public record SpuCode(String value) { public SpuCode { if (value == null || value.isBlank()) throw new IllegalArgumentException("SPU code required"); } }
