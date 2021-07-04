package com.lyuwalle.vhrmailserver.receiver;

import com.rabbitmq.client.Channel;
import org.lyuwalle.vhr.model.Employee;
import org.lyuwalle.vhr.model.MailConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;


import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;

/**
 * @Author lyuWalle
 * @Date 2020/12/23 20:01
 * @Version 1.0
 */
/**
 * 主要作用：监听消息队列，收集消息，发送邮件。向消息队列里面发送消息是在EmployeeService里面来做的，在EmployeeService里面
 * 每添加一个新的员工，就往这个消息队列里面发送消息。
 */
@Component
public class MailReceiver {

    /*日志*/
    public static final Logger logger = LoggerFactory.getLogger(MailReceiver.class);

    @Autowired
    JavaMailSender javaMailSender;

    /*mailProperties表示配置文件里面邮件相关的配置的注入的类， 可以获得发件人的姓名，*/
    @Autowired
    MailProperties mailProperties;
    /*邮件模板*/
    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    StringRedisTemplate redisTemplate;

    /*监听这个消息队列:MAIL_QUEUE_NAME = "nurmann.mail.queue",只要消息队列里面有值，就取出来，整合成一个msg,
     * 设置好收件人的姓名之后就把这个msg发送出去：javaMailSender.send(msg)*/
    @RabbitListener(queues = MailConstants.MAIL_QUEUE_NAME)
    public void handler(Message message, Channel channel) throws IOException {
        Employee employee = (Employee) (message.getPayload());
        MessageHeaders headers = message.getHeaders();
        Long tag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        String msgId = (String) headers.get("spring_returned_message_correlation");

        if (redisTemplate.opsForHash().entries("mail_log").containsKey(msgId)) {
            //redis 中包含该 key，说明该消息已经被消费过
            logger.info(msgId + ":消息已经被消费");
            channel.basicAck(tag, false);//确认消息已消费
            return;
        }

        //收到消息，发送邮件
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg);
        try {
            helper.setTo(employee.getEmail());
            helper.setFrom(mailProperties.getUsername());
            helper.setSubject("hello欢迎");
            helper.setSentDate(new Date());
            /*org.thymeleaf.context*/
            /*给邮件模板里面的变量设置信息，把设置信息的邮件模板就可以当成一个mail来发送*/
            /*import org.thymeleaf.context.Context;*/
            Context context = new Context();
            context.setVariable("name", employee.getName());
            context.setVariable("posName", employee.getPosition().getName());
            context.setVariable("joblevelName", employee.getJobLevel().getName());
            context.setVariable("departmentName", employee.getDepartment().getName());
            String mail = templateEngine.process("mail", context);
            helper.setText(mail, true);
            /*发送邮件*/
            javaMailSender.send(msg);
            redisTemplate.opsForHash().put("mail_log", msgId, "nurmann");
            //确认消息已经消费
            channel.basicAck(tag, false);
            logger.info(msgId + ":邮件发送成功");
        } catch (MessagingException e) {
            channel.basicNack(tag, false, true);
            e.printStackTrace();
            logger.error("邮件发送失败：" + e.getMessage());
        }
    }
}
