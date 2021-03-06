package org.lyuwalle.vhr.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author lyuWalle
 * @Date 2020/12/12 15:21
 * @Version 1.0
 *
 * 测试controller，前提是url不会被spring security拦截
 */
@RestController
public class HelloController {
    @RequestMapping("/hello")
    public String hello(){
        return "hello";
    }

    @RequestMapping("/employee/basic/hello")
    public String hello2(){
        return "/employee/basic/hello";
    }

    @RequestMapping("/employee/advanced/hello")
    public String hello3(){
        return "/employee/advanced/hello";
    }
}
