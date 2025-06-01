package Server.server.worker;

import Server.provider.ServiceProvider;
import common.message.RpcRequest;
import common.message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/6/1 10:55
 */
public class WorkThread implements Runnable {

    private Socket socket;
    private ServiceProvider serviceProvider;

    public WorkThread(Socket socket, ServiceProvider serviceProvider) {
        this.socket = socket;
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            RpcRequest request = (RpcRequest) ois.readObject();
            Object obj = getResponse(request);

            oos.writeObject(obj);
            oos.flush();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Object getResponse(RpcRequest request)  {
        String serviceName = request.getInterfaceName();

        Method method = null;
        try {
            Object service = serviceProvider.getService(serviceName);
            method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            Object result = method.invoke(service, request.getParams());
            return RpcResponse.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("方法执行错误");
            return RpcResponse.fail();
        }
    }
}
