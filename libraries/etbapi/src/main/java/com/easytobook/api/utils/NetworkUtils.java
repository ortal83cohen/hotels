package com.easytobook.api.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

/**
 * @author alex
 * @date 2015-04-28
 */
public class NetworkUtils {

    /**
     * // AndroidManifest.xml permissions
     * <uses-permission android:name="android.permission.INTERNET" />
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     * Get IP address from first non-localhost interface
     *
     * @return address or null
     */
    public static String getIPAddress() throws SocketException {
        List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        for (NetworkInterface intf : interfaces) {
            List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
            for (InetAddress addr : addrs) {
                if (!addr.isLoopbackAddress()) {
                    String sAddr = addr.getHostAddress().toUpperCase();
                    boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                    if (isIPv4) {
                        return sAddr;
                    }
                }
            }
        }
        return null;
    }

}
