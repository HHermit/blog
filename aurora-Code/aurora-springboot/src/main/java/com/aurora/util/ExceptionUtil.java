package com.aurora.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

    public static String getTrace(Throwable t) {
        // 创建一个StringWriter实例，用于存储堆栈跟踪信息
        StringWriter stringWriter = new StringWriter();
        // 使用StringWriter创建一个PrintWriter实例，用于打印堆栈跟踪
        PrintWriter writer = new PrintWriter(stringWriter);
        // 将异常的堆栈跟踪打印到PrintWriter中
        t.printStackTrace(writer);
        // 获取StringWriter的内部字符缓冲区
        StringBuffer buffer = stringWriter.getBuffer();
        // 返回堆栈跟踪信息的字符串表示
        return buffer.toString();
    }

}
