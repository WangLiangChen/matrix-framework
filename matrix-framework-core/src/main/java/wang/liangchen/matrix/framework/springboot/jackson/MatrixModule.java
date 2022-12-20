package wang.liangchen.matrix.framework.springboot.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import wang.liangchen.matrix.framework.commons.enumeration.ConstantEnum;

/**
 * @author Liangchen.Wang 2022-12-12 14:38
 */
public class MatrixModule extends SimpleModule {
    public MatrixModule() {
        super(PackageVersion.VERSION);
        this.addSerializer(ConstantEnum.class, new ConstantEnumSerializer());
    }
}
