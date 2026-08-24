package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.northbound.local;

import wang.liangchen.matrix.framework.ddd.assembler.IAssembler;

/** 违规：装配器实现类位于northbound.local包（放置规则：应在northbound.assembler） */
public final class AssemblerInWrongPackage implements IAssembler {
}
