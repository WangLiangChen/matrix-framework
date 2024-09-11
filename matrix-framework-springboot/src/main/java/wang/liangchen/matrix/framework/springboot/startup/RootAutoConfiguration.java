package wang.liangchen.matrix.framework.springboot.startup;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.core.PriorityOrdered;

@AutoConfiguration
@AutoConfigureOrder(PriorityOrdered.HIGHEST_PRECEDENCE)
public class RootAutoConfiguration {

}
