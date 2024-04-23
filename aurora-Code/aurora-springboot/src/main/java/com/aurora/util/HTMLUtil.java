package com.aurora.util;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;

public class HTMLUtil {

    //敏感词设置
    // temporary unused
    private static final SensitiveWordBs sensitiveWordBs = SensitiveWordBs.newInstance()
            .ignoreCase(true)
            .ignoreWidth(true)
            .ignoreNumStyle(true)
            .ignoreChineseStyle(true)
            .ignoreEnglishStyle(true)
            .ignoreRepeat(true)
            .enableNumCheck(false)
            .enableEmailCheck(false)
            .enableUrlCheck(false)
            .init();

    /**
    * @Description: 过滤HTML标签和属性，返回过滤后的文本。
    * @Param: [source]
    * @return: java.lang.String
    */
    public static String filter(String source) {
        //使用正则表达式删除所有非<img>标签的标签。
        source = source.replaceAll("(?!<(img).*?>)<.*?>", "")
                //使用正则表达式删除所有onload属性。
                .replaceAll("(onload(.*?)=)", "")
                //使用正则表达式删除所有onerror属性。
                .replaceAll("(onerror(.*?)=)", "");
        return deleteHMTLTag(source);
    }

    /**
    * @Description: 删除HTML标签，返回一个没有HTML标签的字符串。
    * @Param: [source]
    * @return: java.lang.String
    */
    public static String deleteHMTLTag(String source) {
        //删除所有的HTML实体，例如&nbsp;、&lt;、&gt;等。
        source = source.replaceAll("&.{2,6}?;", "");
        //删除所有的<script>标签及其内容
        source = source.replaceAll("<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>", "");
        //删除所有的<style>标签及其内容。
        source = source.replaceAll("<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>", "");
        return source;
    }

}
