# maven 依赖

```xml

<dependency>
    <groupId>wang.liangchen.matrix</groupId>
    <artifactId>matrix-framework-lock-spring-boot-starter</artifactId>
    <version>${version.matrix}</version>
</dependency>
```

# 启用锁

```java
import wang.liangchen.matrix.framework.lock.annotation.EnableLock;

@EnableLock
```

# 如何使用

## 普通Bean方法

返回值不能为void或基本类型

```java
import org.springframework.stereotype.Component;
import wang.liangchen.matrix.framework.lock.annotation.MatrixLock;

@Component
public class LockExample() {
    @MatrixLock(lockKey = "matrix", lockAtLeast = "1m", lockAtMost = "5m")
    public String executeInLock() {
        return null;
    }
}
```

## @Scheduled注解的Bean方法

返回值必须为void

```java
import org.springframework.stereotype.Component;
import wang.liangchen.matrix.framework.lock.annotation.MatrixLock;

@Component
public class LockExample() {
    @Scheduled(fixedDelay = 2000)
    @MatrixLock(lockKey = "matrix", lockAtLeast = "1m", lockAtMost = "5m")
    public void executeInLock() {

    }
}
```