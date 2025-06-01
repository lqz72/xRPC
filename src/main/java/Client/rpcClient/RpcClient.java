package Client.rpcClient;

import common.message.RpcRequest;
import common.message.RpcResponse;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/5/25 21:10
 */
public interface RpcClient {
     public RpcResponse sendRequest(RpcRequest request);
}
