package wang.liangchen.matrix.framework.spring.boot.json.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import wang.liangchen.matrix.framework.spring.boot.json.JsonField;

/**
 * @author Liangchen.Wang 2022-12-12 14:38
 */
public class MatrixDataModule extends SimpleModule {
    public MatrixDataModule() {
        super(PackageVersion.VERSION);
        this.addSerializer(JsonField.class, new JsonFieldSerializer());
        this.addDeserializer(JsonField.class, new JsonFieldDeserializer());
    }
}
