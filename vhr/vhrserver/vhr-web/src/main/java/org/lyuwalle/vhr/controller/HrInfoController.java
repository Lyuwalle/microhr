package org.lyuwalle.vhr.controller;

import org.csource.common.MyException;
import org.csource.fastdfs.ProtoCommon;
import org.lyuwalle.vhr.config.FastDFSUtils;
import org.lyuwalle.vhr.model.Hr;
import org.lyuwalle.vhr.model.RespBean;
import org.lyuwalle.vhr.service.HrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Map;

/**
 * @Author lyuWalle
 * @Date 2020/12/25 17:47
 * @Version 1.0
 */
@RestController
public class HrInfoController {

    @Autowired
    HrService hrService;

    //在配置文件里面注入过
    @Value("${fastdfs.nginx.host}")
    String nginxHost;

    @GetMapping("/hr/info")
    public Hr getCurrentHr(Authentication authentication) {
        return ((Hr) authentication.getPrincipal());
    }

    @PutMapping("/hr/info")
    public RespBean updateHr(@RequestBody Hr hr, Authentication authentication) {
        if (hrService.updateHr(hr) == 1) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                    hr, authentication.getCredentials(), authentication.getAuthorities()));
            return RespBean.ok("更新成功!");
        }
        return RespBean.error("更新失败!");
    }

    @PutMapping("/hr/pass")
    public RespBean updateHrPasswd(@RequestBody Map<String, Object> info) {
        String oldpass = (String) info.get("oldpass");
        String pass = (String) info.get("pass");
        Integer hrid = (Integer) info.get("hrid");
        if (hrService.updateHrPasswd(oldpass, pass, hrid)) {
            return RespBean.ok("更新成功!");
        }
        return RespBean.error("更新失败!");
    }

    @PostMapping("/hr/userface")
    public RespBean updateHrUserface(MultipartFile file, Integer id, Authentication authentication) throws UnsupportedEncodingException, NoSuchAlgorithmException, MyException {
        String fileId = FastDFSUtils.upload(file);
        String url = nginxHost + fileId;
//        System.out.println(url);
//        //获取一个时间戳
//        int ts = (int) Instant.now().getEpochSecond();
//        //第三个参数是配置文件中的密钥
//        String remote_filename = fileId.substring(fileId.indexOf("/")+1);
//        String token = ProtoCommon.getToken(remote_filename, ts, "FastDFS1234567890");
//        url += "?token=";
//        url += token;
//        url += "?ts=";
//        url += ts;
        if (hrService.updateUserface(url, id) == 1) {
            Hr hr = (Hr) authentication.getPrincipal();
            hr.setUserface(url);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                    hr, authentication.getCredentials(), authentication.getAuthorities()));
            return RespBean.ok("更新成功!", url);
        }
        return RespBean.error("更新失败!");
    }
}
