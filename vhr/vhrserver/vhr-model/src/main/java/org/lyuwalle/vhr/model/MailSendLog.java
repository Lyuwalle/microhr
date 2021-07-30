package org.lyuwalle.vhr.model;

import lombok.Data;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Date;

//数据库中的Mail_Send_Log
@Data
public class MailSendLog {
    private String msgId;
    private Integer empId;
    //0 消息投递中   1 投递成功   2投递失败
    private Integer status;
    private String routeKey;
    private String exchange;
    //重试次数
    private Integer count;
    private Date tryTime;
    private Date createTime;
    private Date updateTime;

//    public MailSendLog(){
//        this.count = 0;
//    }
}
