package wang.liangchen.matrix.framework.complier.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-12-14 11:37
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("wang.liangchen.matrix.framework.complier.annotation.ModuleVersion")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ModuleVersionProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }
}
