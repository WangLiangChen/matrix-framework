package wang.liangchen.matrix.framework.data.entity;

import jakarta.persistence.Transient;
import wang.liangchen.matrix.framework.commons.object.EnhancedMap;
import wang.liangchen.matrix.framework.commons.object.EnhancedObject;

import java.util.Map;

/**
 * @author LiangChen.Wang 2024/10/12 16:27
 */
public abstract class RootEntity extends EnhancedObject {
    @Transient
    private transient final Map<String, Object> mandatoryUpdatedColumns = new EnhancedMap<>();
}
