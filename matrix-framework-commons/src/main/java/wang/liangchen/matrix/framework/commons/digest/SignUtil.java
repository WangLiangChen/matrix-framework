package wang.liangchen.matrix.framework.commons.digest;


import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author LiangChen.Wang 2019/8/18 12:01
 */
public enum SignUtil {
    /**
     * INSTANCE
     */
    INSTANCE;

    public String dictionarySort(Map<String, Object> source, String... excludeKeys) {
        List<String> excludeList = CollectionUtil.INSTANCE.array2List(excludeKeys);
        return source.entrySet().stream().filter(e -> null != e.getValue() && !excludeList.contains(e.getKey())).sorted(Map.Entry.comparingByKey()).map(e -> String.format("%s=%s", e.getKey(), e.getValue())).collect(Collectors.joining("&"));
    }
}
