package wang.liangchen.matrix.framework.commons.network;

import java.net.URI;

public class URIResolver {
    private String host;
    private int port = 21;
    private String username = "anonymous";
    private String password = "";
    private String path;
    private String fileName = "";


    public void resolve(String url) {
        URI uri = URI.create(url);
        host = uri.getHost();
        port = uri.getPort();
        port = port == -1 ? 21 : port;
        path = uri.getPath();
        int index = path.lastIndexOf('/');
        if (index != -1) {
            fileName = path.substring(index + 1);
            path = path.substring(0, index);
        }
        String userInfo = uri.getUserInfo();
        if (null != userInfo) {
            index = userInfo.indexOf(':');
            if (index != -1) {
                username = userInfo.substring(index + 1);
                password = userInfo.substring(0, index);
            }
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "URIResolver{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", path='" + path + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
