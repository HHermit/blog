package com.aurora.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailDTO {

    //收件人
    private String email;

    //邮件主题
    private String subject;

    //邮件中使用的相关参数变量
    private Map<String, Object> commentMap;

    //所要使用的HTML邮件模板
    private String template;

}
