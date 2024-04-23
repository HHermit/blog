package com.aurora.util;

import com.aurora.model.dto.EmailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 *  @description: 邮件工具类
 *                用来发送邮件
*/
@Component
public class EmailUtil {

    /**
     * 注入配置文件中spring.mail.username的值，作为发件人邮箱地址。
     */
    @Value("${spring.mail.username}")
    private String email;

    /**
     * 邮件发送器
     * pom引入spring-boot-starter-mail，会对JavaMailSender完成自动化配置，根据yml配置文件中的相关信息
     */
    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * 通过TemplateEngine可以方便为模板设置动态参数
     */
    @Autowired
    private TemplateEngine templateEngine;

    /**
    * @Description: 发送邮件
    * @Param: [emailDTO]
    * @return: void
    */
    public void sendHtmlMail(EmailDTO emailDTO) {
        try {
            // 创建MimeMessage对象符合MIME协议：用于发送多格式邮件 ，主要可以解析HTML邮件模板
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            // 创建MimeMessageHelper对象，用于辅助设置MimeMessage
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            // 创建 thymeleaf引擎 的 Context对象，用于存放模板引擎处理时需要的变量
            Context context = new Context();
            // 设置模板引擎处理时所需的上下文变量
            context.setVariables(emailDTO.getCommentMap());
            // 使用模板引擎处理模板并生成HTML内容
            String process = templateEngine.process(emailDTO.getTemplate(), context);
            // 设置发件人
            mimeMessageHelper.setFrom(email);
            // 设置收件人
            mimeMessageHelper.setTo(emailDTO.getEmail());
            // 设置邮件主题
            mimeMessageHelper.setSubject(emailDTO.getSubject());
            // 设置邮件内容为HTML格式
            mimeMessageHelper.setText(process, true);
            // 发送邮件
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // 处理邮件发送过程中可能出现的异常
            e.printStackTrace();
        }
    }

}
