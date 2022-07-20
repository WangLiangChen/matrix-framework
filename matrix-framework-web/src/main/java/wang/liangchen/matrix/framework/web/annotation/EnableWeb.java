package wang.liangchen.matrix.framework.web.annotation;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import wang.liangchen.matrix.framework.commons.utils.PrettyPrinter;
import wang.liangchen.matrix.framework.web.configuration.WebFluxAutoConfiguration;
import wang.liangchen.matrix.framework.web.configuration.WebMvcAutoConfiguration;

import java.lang.annotation.*;

/**
 * @author LiangChen.Wang
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({EnableWeb.WebImportSelector.class})
@SuppressWarnings("NullableProblems")
public @interface EnableWeb {
    WebType webType() default WebType.WEBMVC;

    enum WebType {
        // webType
        WEBFLUX, WEBMVC
    }

    class WebImportSelector implements ImportSelector {


        @Override
        public synchronized String[] selectImports(AnnotationMetadata annotationMetadata) {
            Class<?> annotationType = EnableWeb.class;
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(annotationType.getName(), false));
            WebType webType = attributes == null ? WebType.WEBFLUX : (WebType) attributes.get("webType");
            PrettyPrinter.INSTANCE.buffer("@EnableWeb:{}", webType.name());
            PrettyPrinter.INSTANCE.buffer("@EnableWeb matchs: {}", annotationMetadata.getClassName());

            String[] imports;
            switch (webType) {
                case WEBMVC:
                    imports = new String[]{WebMvcAutoConfiguration.class.getName(), org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.class.getName(), DelegatingWebMvcConfiguration.class.getName()};
                    break;
                case WEBFLUX:
                    imports = new String[]{WebFluxAutoConfiguration.class.getName(), org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration.class.getName(), DelegatingWebFluxConfiguration.class.getName()};
                    break;
                default:
                    imports = new String[0];
                    break;
            }
            PrettyPrinter.INSTANCE.flush();
            return imports;
        }
    }
}
