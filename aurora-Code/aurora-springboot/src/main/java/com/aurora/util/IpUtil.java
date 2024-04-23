package com.aurora.util;

import com.aurora.constant.CommonConstant;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *  @description:基于ip获取对应位置信息
 *  
*/
@Slf4j
@Component
public class IpUtil {

    private static DbSearcher searcher;

    private static Method method;

    /**
     * 通过HttpServletRequest获取客户端IP地址
     * 优先从请求头中获取IP地址，按照一定的顺序尝试获取，
     * 如果都获取不到，则返回服务器端获取的IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        // 尝试从多个请求头中获取IP地址
        String ipAddress = request.getHeader("X-Real-IP");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("x-forwarded-for");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            // 如果是本地地址，则通过网卡获取本机配置的IP地址
            //127  ipv4  00000：1  ipv6
            if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    log.error("getIpAddress exception:", e);
                }
                assert inet != null;
                ipAddress = inet.getHostAddress();
            }
        }
        // 返回第一个IP地址，处理多IP地址情况
        return StringUtils.substringBefore(ipAddress, ",");
    }

    /**
     * 初始化IP数据库资源
     * 用于后续的IP地址查询
     */
    @PostConstruct
    private void initIp2regionResource() throws Exception {
        // 从资源路径加载IP数据库文件，转存为字节数组，并配置查询工具
        InputStream inputStream = new ClassPathResource("/ip/ip2region.db").getInputStream();
        byte[] dbBinStr = FileCopyUtils.copyToByteArray(inputStream);
        DbConfig dbConfig = new DbConfig();

        //解析ip的查询接口以及方法的封装
        searcher = new DbSearcher(dbConfig, dbBinStr);
        method = searcher.getClass().getMethod("memorySearch", String.class);
    }

    /**
     * 根据IP地址获取来源信息  国家|区域|省份|城市|ISP  中国一般不包含区域 ：中国|0|广东省|广州市|电信
     * 使用ip2region数据库进行查询，返回地区信息
     */
    public static String getIpSource(String ipAddress) {
        // 验证IP地址有效性，无效则记录错误并返回空
        if (ipAddress == null || !Util.isIpAddress(ipAddress)) {
            log.error("Error: Invalid ip address");
            return "";
        }
        try {
            // 使用反射调用查询方法，获取地区信息
            DataBlock dataBlock = (DataBlock) method.invoke(searcher, ipAddress);
            String ipInfo = dataBlock.getRegion();
            if (!StringUtils.isEmpty(ipInfo)) {
                // 处理区域信息，去除无用字段 eg：中国|0|广东省|广州市|电信  => 中国|广东省|广州市|电信
                ipInfo = ipInfo.replace("|0", "");
                ipInfo = ipInfo.replace("0|", "");
                return ipInfo;
            }
        } catch (Exception e) {
            log.error("getCityInfo exception:", e);
        }
        return "";
    }

    /**
     * 获取IP来源的省份信息  国家|区域|省份|城市|ISP
     * 从ipSource中解析出省份信息
     */
    public static String getIpProvince(String ipSource) {
        // 如果ipSource为空，则直接返回未知
        if (StringUtils.isBlank(ipSource)) {
            return CommonConstant.UNKNOWN;
        }
        // 根据|分割ipSource，尝试获取省份信息
        String[] strings = ipSource.split("\\|");
        if (strings.length > 1 && strings[1].endsWith("省")) {
            return StringUtils.substringBefore(strings[1], "省");
        }
        return strings[0];
    }

    /**
     * 获取用户代理信息
     * 从请求头的User-Agent字段解析出用户代理信息
     */
    public static UserAgent getUserAgent(HttpServletRequest request) {
        return UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
    }

}

