package Server.server;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/6/1 10:52
 */
public interface RpcServer {
    public void start(int port);
    public void stop();
}
