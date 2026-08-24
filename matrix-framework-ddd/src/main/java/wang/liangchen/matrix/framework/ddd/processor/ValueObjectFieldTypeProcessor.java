package wang.liangchen.matrix.framework.ddd.processor;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.Set;

/**
 * 值对象字段类型编译期校验处理器：确保 @DomainModel 标注的且为IValueObject类型的非static实例字段
 * 只能使用不可变类型（基本类型、包装类型、String、时间类型、BigDecimal/BigInteger、枚举、
 *
 * <p>注册方式：通过 META-INF/services/javax.annotation.processing.Processor 自动注册，
 * 或在 Maven compiler 插件中通过 annotationProcessorPaths 指定。
 *
 * @author Liangchen.Wang
 */
public class ValueObjectFieldTypeProcessor extends AbstractProcessor {
    private static final String IVALUEOBJECT = "wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject";

    private static final Set<String> IMMUTABLE_TYPE_NAMES = Set.of(
            "boolean", "byte", "char", "short", "int", "long", "float", "double",
            "java.lang.Boolean", "java.lang.Byte", "java.lang.Character",
            "java.lang.Short", "java.lang.Integer", "java.lang.Long",
            "java.lang.Float", "java.lang.Double",
            "java.lang.String",
            "java.time.Instant", "java.time.LocalDate", "java.time.LocalTime",
            "java.time.LocalDateTime", "java.time.ZonedDateTime",
            "java.time.OffsetDateTime", "java.time.Year", "java.time.YearMonth",
            "java.time.MonthDay", "java.time.Duration", "java.time.Period",
            "java.math.BigDecimal", "java.math.BigInteger",
            "java.util.UUID", "java.util.Optional"
    );

    private TypeElement valueObjectTypeElement;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        valueObjectTypeElement = processingEnv.getElementUtils().getTypeElement(IVALUEOBJECT);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(DomainModel.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (valueObjectTypeElement == null) {
            return false;
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(DomainModel.class)) {
            DomainModel domainModel = element.getAnnotation(DomainModel.class);
            if (null == domainModel) {
                continue;
            }
            if (DomainMetaModel.ValueObject != domainModel.value()) {
                continue;
            }
            if (ElementKind.CLASS != element.getKind()) {
                continue;
            }
            checkFields((TypeElement) element);
        }
        return false;
    }

    private void checkFields(TypeElement classElement) {
        for (Element enclosed : classElement.getEnclosedElements()) {
            if (ElementKind.FIELD != enclosed.getKind()) {
                continue;
            }
            if (enclosed.getModifiers().contains(Modifier.STATIC)) {
                continue;
            }
            VariableElement field = (VariableElement) enclosed;
            TypeMirror fieldType = field.asType();
            if (isImmutableType(fieldType)) {
                continue;
            }

            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    String.format("Field '%s' of type '%s' is not an immutable type. " +
                                    "Value object instance fields must be: primitive types, wrapper types, String, temporal types, " +
                                    "BigDecimal/BigInteger, enums, or IValueObject subtypes",
                            field.getSimpleName(), fieldType),
                    field
            );

        }
    }

    private boolean isImmutableType(TypeMirror type) {
        String typeName = type.toString();
        if (isPrimitive(typeName)) {
            return true;
        }
        if (IMMUTABLE_TYPE_NAMES.contains(typeName)) {
            return true;
        }
        if (isEnum(type)) {
            return true;
        }
        if (isAssignableTo(type, valueObjectTypeElement)) {
            return true;
        }
        return false;
    }

    private boolean isPrimitive(String typeName) {
        return switch (typeName) {
            case "boolean", "byte", "char", "short", "int", "long", "float", "double" -> true;
            default -> false;
        };
    }

    private boolean isEnum(TypeMirror type) {
        return processingEnv.getTypeUtils().asElement(type) instanceof TypeElement te
                && te.getKind() == ElementKind.ENUM;
    }

    private boolean isAssignableTo(TypeMirror type, TypeElement target) {
        if (target == null) {
            return false;
        }
        return processingEnv.getTypeUtils().isAssignable(
                processingEnv.getTypeUtils().erasure(type),
                processingEnv.getTypeUtils().erasure(target.asType())
        );
    }
}