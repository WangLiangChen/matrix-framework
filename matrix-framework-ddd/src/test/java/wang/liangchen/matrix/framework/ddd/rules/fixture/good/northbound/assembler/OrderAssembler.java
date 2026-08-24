package wang.liangchen.matrix.framework.ddd.rules.fixture.good.northbound.assembler;

import wang.liangchen.matrix.framework.ddd.assembler.Assembler;
import wang.liangchen.matrix.framework.ddd.assembler.IAssembler;

/** 合规装配器：消息契约与领域对象互转的唯一装配点（入站可兼任工厂创建领域对象） */
@Assembler
public final class OrderAssembler implements IAssembler {
}
