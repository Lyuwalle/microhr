package org.lyuwalle.vhr.task;

import org.lyuwalle.vhr.model.Employee;
import org.lyuwalle.vhr.model.MailConstants;
import org.lyuwalle.vhr.model.MailSendLog;
import org.lyuwalle.vhr.service.EmployeeService;
import org.lyuwalle.vhr.service.MailSendLogService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @Author lyuWalle
 * @Date 2020/12/27 19:25
 * @Version 1.0
 */
@Component
public class MailSendTask {
    @Autowired
    MailSendLogService mailSendLogService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Scheduled(cron = "0/10 * * * * ?")
    public void MailReSendTask(){
        /*只查询出status为0的MailSendLog。即目前还在投递中的消息*/
        List<MailSendLog> logs = mailSendLogService.getMailSendLogsByStatus();
        if (logs == null || logs.size() == 0) {
            return;
        }
        logs.forEach(mailSendLog -> {
            if(mailSendLog.getCount() > 2){
                mailSendLogService.updateMailSendLogStatus(mailSendLog.getMsgId(), 2); //重试次数超过3就把这条消息设置为发送失败
            }else{
                //重试
                mailSendLogService.updateCount(mailSendLog.getMsgId(), new Date());
                Employee emp = employeeService.getEmployeeById(mailSendLog.getEmpId());
                rabbitTemplate.convertAndSend(MailConstants.MAIL_EXCHANGE_NAME, MailConstants.MAIL_ROUTING_KEY_NAME, emp, new CorrelationData(mailSendLog.getMsgId()));
            }
        });
    }
}
