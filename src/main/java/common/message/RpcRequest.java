package common.message;

import lombok.*;

import java.io.Serializable;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/5/25 20:54
 * */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
    // 接口名称
    String interfaceName;
    // 方法名称
    String methodName;
    // 方法参数
    Object[] params;
    // 方法参数类型
    Class<?>[] paramTypes;
}
