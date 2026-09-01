package wang.liangchen.matrix.framework.ddd.southbound.port;

/**
 * 发布端口：隔离对事件总线的访问，提供领域事件/集成事件的发布能力。
 * 泛型T为待发布的事件消息类型，由业务端口（如OrderEventPublisherPort）指定具体类型。
 *
 * @param <T> 待发布的事件消息类型
 * @author Liangchen.Wang
 */
@Port(PortType.Publisher)
public interface IPublisherPort<T> extends IPort {
    /**
     * 发布事件消息至事件总线。
     *
     * @param event 待发布的事件消息
     */
    void publish(T event);
}