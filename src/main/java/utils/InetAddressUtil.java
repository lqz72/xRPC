package utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/6/1 17:18
 */
public class InetAddressUtil {

    public static String InetAddress2String(InetSocketAddress address) {
        String hostName = address.getHostName();
        int port = address.getPort();
        return hostName + ":" + port;
    }

    public static InetSocketAddress String2InetAddress(String address) {
        String[] split = address.split(":");
        return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
    }
}
