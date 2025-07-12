package Server;


import Server.provider.ServiceProvider;
import Server.server.RpcServer;
import Server.server.impl.NettyRpcServer;
import Server.server.impl.SimpleSocketRpcServer;
import common.service.UserService;
import common.service.impl.UserServiceImpl;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/5/25 21:04
 */
public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 8888);
        serviceProvider.register(userService, true);

        RpcServer server = new NettyRpcServer(serviceProvider);
        server.start(8888);
    }
}
