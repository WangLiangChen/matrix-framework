package wang.liangchen.matrix.framework.web.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.runtime.ReturnWrapper;

public final class JsonResponse<T> extends ReturnWrapper<T> {
    private final static Logger logger = LoggerFactory.getLogger(JsonResponse.class);
    private final static String DEFAULT_MESSAGE = "System error. Please try again later or contact your system administrator.";
    private final static String DEFAULT_MESSAGE_I18N = "SystemError";
    /**
     * 前端传递的requestId,原样返回。
     * 用于标识同一个请求
     */
    private String requestId;

    private JsonResponse(boolean success, T payload) {
        super(success, payload);
    }
}
