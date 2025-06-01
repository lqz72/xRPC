package common.service;

import common.pojo.User;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/5/25 20:48
 * */
public interface UserService {
    // 客户端通过这个接口调用服务端的实现类
    User getUserByUserId(Integer id);
    //新增一个功能
    Integer insertUserId(User user);
}
