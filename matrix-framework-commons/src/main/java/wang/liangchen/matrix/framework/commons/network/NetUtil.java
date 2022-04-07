package wang.liangchen.matrix.framework.commons.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Liangchen.Wang
 */
public enum NetUtil {
    INSTANCE;
    public boolean isConnectable(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
