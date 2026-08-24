package wang.liangchen.matrix.framework.ddd.assembler;

/**
 * @author Liangchen.Wang
 * Marker interface
 * Mark an assembler
 * 装配器：负责消息契约模型与领域对象之间的相互转换——
 * 入站将消息契约装配为领域对象（可兼任工厂），出站将领域对象装配为消息契约。
 * <p>
 * 装配器位于应用层（northbound.assembler包），是实现类必须自行标注@Assembler；
 * 消息契约不能引用领域模型，更不能担任工厂，领域对象的创建与装配统一由装配器承担。
 */
public interface IAssembler {
}
