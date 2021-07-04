package com.lyuwalle.vhrmailserver;

import org.lyuwalle.vhr.model.MailConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VhrmailserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(VhrmailserverApplication.class, args);
    }

    /*创建一个消息队列，消息队列已经在rabbitConfig里面创建了,那么这里就不用再次创建了*/
//    @Bean
//    Queue queue(){
//        return new Queue(MailConstants.MAIL_QUEUE_NAME);
//    }
}
