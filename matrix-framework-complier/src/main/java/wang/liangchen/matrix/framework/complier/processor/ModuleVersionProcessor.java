package wang.liangchen.matrix.framework.complier.processor;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import sun.misc.Unsafe;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-12-14 11:37
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("wang.liangchen.matrix.framework.complier.annotation.ModuleVersion")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ModuleVersionProcessor extends AbstractProcessor {
    private JavacTrees javacTrees;
    private TreeMaker treeMaker;
    private ProcessingEnvironment processingEnv;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get(null);

            Class<?> cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
        } catch (Throwable t) {
            // We shall ignore it; the effect of this code failing is that the user gets to see a warning they remove with various --add-opens magic.
        }
        super.init(processingEnv);
        this.processingEnv = processingEnv;
        this.javacTrees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement typeElement : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(typeElement)) {
                JCTree.JCVariableDecl jcv = (JCTree.JCVariableDecl) javacTrees.getTree(element);
                Type varType = jcv.vartype.type;
                if (!"java.lang.String".equals(varType.toString())) {
                    printErrorMessage(element, "Type '" + varType + "'" + " is not support.");
                }
                jcv.init = treeMaker.Literal(getVersion());
            }
        }
        return true;
    }

    private void printErrorMessage(Element element, String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }

    private String getVersion() {
        return "2.0.0";
    }
}
