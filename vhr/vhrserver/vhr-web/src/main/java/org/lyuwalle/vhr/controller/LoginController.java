package org.lyuwalle.vhr.controller;

import org.lyuwalle.vhr.config.VerificationCode;
import org.lyuwalle.vhr.model.RespBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageFilter;
import java.io.IOException;

/**
 * @Author lyuWalle
 * @Date 2020/12/12 21:27
 * @Version 1.0
 */
/*RestController相当于ResponseBody和Controller注解的结合，ResponseBody表示返回一个Json*/
@RestController
public class LoginController {
    @GetMapping("/login")
    public RespBean login(){
        return RespBean.error("尚未登录，请登录");
    }

    //只要登录页面一打开，这个接口就会被立刻被访问
    /*这个接口应该是不用登录就可以访问，因此在SecurityConfig里面应该放进web.ignoring().antMatchers，表示不用登录就可以访问*/
    //前端的图片的src地址就是/verifyCode，后端已经把这个图片传给了前端
    @GetMapping("/verifyCode")
    public void verifyCode(HttpSession session, HttpServletResponse resp) throws IOException {
        VerificationCode code = new VerificationCode();
        BufferedImage image = code.getImage();
        String text = code.getText();
        //把后端生成的正确的验证码存在session里面
        session.setAttribute("verify_code", text);
        VerificationCode.output(image, resp.getOutputStream());
    }
}
