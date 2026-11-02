package wang.liangchen.matrix.shop.product.domain.aggregate.spu;
public record MediaReference(String mediaId, String role, String altText, int sortOrder) {
    public MediaReference { if (mediaId == null || mediaId.isBlank()) throw new IllegalArgumentException("mediaId required"); if (role == null || role.isBlank()) throw new IllegalArgumentException("role required"); if (sortOrder < 0) throw new IllegalArgumentException("sortOrder must be >= 0"); }
}
