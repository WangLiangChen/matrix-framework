package wang.liangchen.matrix.framework.data.configuration;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.MapWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.LocalCacheScope;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.data.mybatis.handler.ConstantEnumTypeHandler;

import java.util.Map;

/**
 * @author Liangchen.Wang 2023-03-17 9:53
 */
public class MybatisCustomizerAutoConfiguration {
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            configuration.setCacheEnabled(false);
            configuration.setLocalCacheScope(LocalCacheScope.STATEMENT);
            configuration.setMapUnderscoreToCamelCase(true);
            configuration.setObjectWrapperFactory(objectWrapperFactory());
        };
    }

    @Bean
    public ConstantEnumTypeHandler constantEnumTypeHandler() {
        return new ConstantEnumTypeHandler();
    }

    private ObjectWrapperFactory objectWrapperFactory() {
        return new ObjectWrapperFactory() {
            @Override
            public boolean hasWrapperFor(Object object) {
                return object instanceof Map;
            }

            @Override
            @SuppressWarnings("unchecked")
            public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
                return new MapWrapper(metaObject, (Map<String, Object>) object) {
                    @Override
                    public String findProperty(String name, boolean useCamelCaseMapping) {
                        if (useCamelCaseMapping) {
                            return StringUtil.INSTANCE.underline2lowerCamelCase(name);
                        }
                        return name;
                    }
                };
            }
        };
    }
}
