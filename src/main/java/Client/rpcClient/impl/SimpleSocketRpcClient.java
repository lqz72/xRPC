package Client.rpcClient.impl;

import Client.rpcClient.RpcClient;
import common.message.RpcRequest;
import common.message.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/5/25 21:08
 */
@Slf4j
public class SimpleSocketRpcClient implements RpcClient {
    // 服务器地址
    private String ip;
    private int port;

    public SimpleSocketRpcClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        try {
            Socket socket = new Socket(ip, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            oos.writeObject(request);
            oos.flush();

            RpcResponse resp = (RpcResponse) ois.readObject();
            return resp;
        } catch (IOException | ClassNotFoundException e) {
            log.error("服务端出错", e);
            return null;
        }
    }
}
