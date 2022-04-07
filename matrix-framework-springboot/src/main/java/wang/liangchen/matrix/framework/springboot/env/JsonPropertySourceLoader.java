package wang.liangchen.matrix.framework.springboot.env;

import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.util.Collections;
import java.util.List;

/**
 * Strategy to load '.json' files into a PropertySource.
 * @author Liangchen.Wang
 */
public class JsonPropertySourceLoader implements PropertySourceLoader {
    @Override
    public String[] getFileExtensions() {
        return new String[]{"json"};
    }

    @Override
    public List<PropertySource<?>> load(String name, Resource resource) {
        return Collections.emptyList();
    }
}
