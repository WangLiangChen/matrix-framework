package wang.liangchen.matrix.framework.data.commons.domain.inifite;

import wang.liangchen.matrix.framework.data.dao.entity.UniqueKey;

/**
 * @author Liangchen.Wang 2023-03-27 10:14
 */
public class InifiteKey extends UniqueKey {
    private final String infiniteGroup;
    private final String infiniteCode;

    public InifiteKey(String infiniteGroup, String infiniteCode) {
        this.infiniteGroup = infiniteGroup;
        this.infiniteCode = infiniteCode;
    }

    public static InifiteKey newInstance(String infiniteGroup, String infiniteCode) {
        return new InifiteKey(infiniteGroup, infiniteCode);
    }

    public String getInfiniteGroup() {
        return infiniteGroup;
    }

    public String getInfiniteCode() {
        return infiniteCode;
    }
}
