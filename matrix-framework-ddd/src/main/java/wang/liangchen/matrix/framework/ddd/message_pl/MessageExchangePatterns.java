package wang.liangchen.matrix.framework.ddd.message_pl;

/**
 * @author Liangchen.Wang 2022-05-20 11:50
 * Synchronous vs Asynchronous
 */
public enum MessageExchangePatterns {
    /**
     * QUERY
     */
    RequestResponse,
    /**
     * COMMAND
     */
    FireAndForget,
    /**
     * MQ publish/subscribe
     */
    RequestStream,
    /**
     * IM
     */
    RequestChannel;
}
