package Server.server.impl;

import Server.provider.ServiceProvider;
import Server.server.RpcServer;
import Server.server.worker.WorkThread;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/6/1 10:53
 */
@Slf4j
public class SimpleSocketRpcServer implements RpcServer {

    private ServiceProvider serviceProvider;

    public SimpleSocketRpcServer(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void start(int port)  {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("服务器启动");
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new WorkThread(socket, serviceProvider)).start();
            }

        } catch (IOException e) {
            log.error("服务端出错", e);
        }
    }

    @Override
    public void stop() {

    }
}
