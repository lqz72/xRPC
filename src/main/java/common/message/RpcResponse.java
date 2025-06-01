package common.message;

import lombok.*;

import java.io.Serializable;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/5/25 20:58
 * */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {
    // 状态信息
    int code;
    String message;
    // 响应数据
    Object data;
    Class<?> dataType;

    public static RpcResponse success(Object data) {
        return RpcResponse.builder().code(200).message("请求成功")
                .data(data).dataType(data.getClass())
                .build();
    }

    public static RpcResponse fail() {
        return RpcResponse.builder().code(500).message("服务器发生错误").build();
    }
}