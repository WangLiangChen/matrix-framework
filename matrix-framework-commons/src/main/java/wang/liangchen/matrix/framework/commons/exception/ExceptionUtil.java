package wang.liangchen.matrix.framework.commons.exception;

import java.sql.SQLException;

public enum ExceptionUtil {
    INSTANCE;

    public String toMessage(Throwable throwable) {
        String result;
        try {
            throw throwable;
        } catch (MatrixRuntimeException ex) {
            result = ex.getMessage();
        } catch (ArithmeticException ex) {
            result = "错误01：运算条件异常";
        } catch (ArrayIndexOutOfBoundsException ex) {
            result = "错误02：数组索引越界";
        } catch (ArrayStoreException ex) {
            result = "错误03：试图将错误类型的对象存储到一个对象数组";
        } catch (ClassCastException ex) {
            result = "错误04：类型强制转换异常";
        } catch (ClassNotFoundException ex) {
            result = "错误05：引用不存在的类";
        } catch (CloneNotSupportedException ex) {
            result = "错误06：对象复制异常，该对象的类无法实现 Cloneable 接口";
        } catch (EnumConstantNotPresentException ex) {
            result = "错误07：枚举常量名称不存在";
        } catch (IllegalAccessException ex) {
            result = "错误08：使用反射时，无法访问指定类、字段、方法或构造方法";
        } catch (IllegalArgumentException ex) {
            result = "错误09：不合法或不正确的方法参数；线程没有处于请求操作所要求的适当状态；字符串转换数值异常；";
        } catch (IllegalMonitorStateException ex) {
            result = "错误10：线程监视器异常";
        } catch (IllegalStateException ex) {
            result = "错误11：非法或不适当的时间调用方法";
        } catch (IndexOutOfBoundsException ex) {
            result = "错误12：索引越界";
        } catch (InstantiationException ex) {
            result = "错误13：newInstance 方法无法实例化指定类";
        } catch (InterruptedException ex) {
            result = "错误14：正常线程被中断";
        } catch (NegativeArraySizeException ex) {
            result = "错误15：数组大小不能为负数";
        } catch (NoSuchFieldException ex) {
            result = "错误16：类不包含指定名称的字段";
        } catch (NoSuchMethodException ex) {
            result = "错误17：无法找到某方法:";
        } catch (NullPointerException ex) {
            result = "错误18：使用值为NULL的对象";
        } catch (TypeNotPresentException ex) {
            result = "错误19：使用字符串访问类型时产生错误";
        } catch (UnsupportedOperationException ex) {
            result = "错误20：不支持的请求";
        } catch (AbstractMethodError ex) {
            result = "错误21：抽象方法不能被调用";
        } catch (AssertionError ex) {
            result = "错误22：断言失败";
        } catch (ClassCircularityError ex) {
            result = "错误23：类初始化时发现循环调用";
        } catch (ClassFormatError ex) {
            result = "错误24：虚拟机无法读取文件，格式错误或者不能解释为类文件";
        } catch (ExceptionInInitializerError ex) {
            result = "错误25：静态初始化程序中发生意外异常";
        } catch (IllegalAccessError ex) {
            result = "错误26：应用程序试图访问或修改它不能访问的字段，或调用它不能访问的方法";
        } catch (IncompatibleClassChangeError ex) {
            result = "错误27：不兼容的类更改；应用程序试图使用 Java 的 new 结构来实例化一个抽象类或一个接口；";
        } catch (InternalError ex) {
            result = "错误28：虚拟机内部错误";
        } catch (LinkageError ex) {
            result = "错误29：相依赖的类发生不相容的改变；Java虚拟机或 ClassLoader无法找到该类的定义；试图访问或修改不存在的字段或方法；版本不支持；";
        } catch (OutOfMemoryError ex) {
            result = "错误30：内存溢出或没有可用的内存";
        } catch (StackOverflowError ex) {
            result = "错误31：递归太深，堆栈溢出";
        } catch (ThreadDeath ex) {
            result = "错误32：带有零个参数的stop方法";
        } catch (UnknownError ex) {
            result = "错误33：虚拟机发生未知严重错误";
        } catch (VirtualMachineError ex) {
            result = "错误34：Java 虚拟机崩溃或用尽了它继续操作所需的资源";
        } catch (SQLException ex) {
            result = "错误35：SQL异常";
        } catch (RuntimeException ex) {
            result = "错误36：运行时异常";
        } catch (Exception ex) {
            result = "错误37：程序异常Exception:" + ex.getMessage();
        } catch (Error ex) {
            result = "错误38：程序错误Error";
        } catch (Throwable t) {
            result = "错误40：其它异常";
        }
        return result;
    }
}
