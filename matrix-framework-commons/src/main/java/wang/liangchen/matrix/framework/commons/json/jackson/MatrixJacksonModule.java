package wang.liangchen.matrix.framework.commons.json.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import wang.liangchen.matrix.framework.commons.enumeration.ConstantEnum;
import wang.liangchen.matrix.framework.commons.object.Dictionary;

/**
 * @author Liangchen.Wang 2022-12-12 14:38
 */
public class MatrixJacksonModule extends SimpleModule {
    public MatrixJacksonModule() {
        super(PackageVersion.VERSION);
        this.addSerializer(ConstantEnum.class, new ConstantEnumSerializer());
        this.addSerializer(Dictionary.class, new JsonFieldSerializer());
        this.addDeserializer(Dictionary.class, new JsonFieldDeserializer());
    }
}
