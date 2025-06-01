package common.pojo;

import lombok.*;
import java.io.Serializable;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/5/25 20:45
 * */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    // 客户端和服务端共有的
    private Integer id;
    private String userName;
    private Boolean sex;
}
