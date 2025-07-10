package Client;

import Client.proxy.ClientProxy;
import Client.rpcClient.RpcClient;
import Client.rpcClient.impl.SimpleSocketRpcClient;
import common.pojo.User;
import common.service.UserService;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/5/25 21:03
 */
public class TestClient {
    public static void main(String[] args) throws InterruptedException {
        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 8888, 1);
        UserService proxy = clientProxy.getProxy(UserService.class);

        User user = proxy.getUserByUserId(1);
        System.out.println("从服务端得到的user="+user.toString());

        User u=User.builder().id(100).userName("wxx").sex(true).build();
        Integer id = proxy.insertUserId(u);
        System.out.println("向服务端插入user的id"+id);
    }
}
