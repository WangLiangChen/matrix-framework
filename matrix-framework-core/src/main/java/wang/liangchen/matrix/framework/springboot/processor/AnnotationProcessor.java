package wang.liangchen.matrix.framework.springboot.processor;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-12-13 14:00
 * append method 'fieldName'
 */
// @AutoService(Processor.class)
@SupportedAnnotationTypes({"wang.liangchen.matrix.framework.springboot.annotation.AutoFieldNames"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class AnnotationProcessor extends AbstractProcessor {
    private JavacTrees javacTrees;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.javacTrees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement typeElement : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(typeElement)) {
                printErrorMessage(element, "generate fieldNames method");
                JCTree jcTree = javacTrees.getTree(element);
                jcTree.accept(new TreeTranslator() {
                    @Override
                    public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                        List<JCTree.JCVariableDecl> jcVariableDeclList = List.nil();
                        // 所有属性
                        for (JCTree tree : jcClassDecl.defs) {
                            if (tree.getKind().equals(Tree.Kind.VARIABLE)) {
                                JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) tree;
                                jcVariableDeclList = jcVariableDeclList.append(jcVariableDecl);
                            }
                        }
                        JCTree.JCMethodDecl method = methodDecl(jcVariableDeclList);
                        jcClassDecl.defs = jcClassDecl.defs.append(method);
                        super.visitClassDef(jcClassDecl);
                    }
                });

            }
        }
        return true;
    }

    private void printErrorMessage(Element element, String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message, element);
    }

    private JCTree.JCMethodDecl methodDecl(List<JCTree.JCVariableDecl> jcVariableDeclList) {
        StringBuilder fieldNames = new StringBuilder();
        for (JCTree.JCVariableDecl jcVariableDecl : jcVariableDeclList) {
            // 属性名
            String declName = jcVariableDecl.getName().toString();
            // 将属性名转为下划线形式并用","分割
            fieldNames.append(",").append(declName);
        }
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        statements.append(treeMaker.Return(treeMaker.Literal(fieldNames.substring(1))));
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
        String methodName = "fieldNames";
        return treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), names.fromString(methodName),
                treeMaker.Ident(names.fromString("String")),
                List.nil(), List.nil(), List.nil(), body, null);
    }

}
