package org.lyuwalle.vhr.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lyuwalle.vhr.model.Hr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author lyuWalle
 * @Date 2021/1/2 17:58
 * @Version 1.0
 */
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    SessionRegistry sessionRegistry;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse resp) throws AuthenticationException {
        if(!req.getMethod().equals("POST")){
            throw new AuthenticationServiceException("Authentication method not supported: " + req.getMethod());
        }
        String verify_code = ((String) req.getSession().getAttribute("verify_code"));
        if (req.getContentType().contains(MediaType.APPLICATION_JSON_VALUE) || req.getContentType().contains(MediaType.APPLICATION_JSON_UTF8_VALUE)){
            Map<String, String> loginData = new HashMap<>();
            try {
                loginData = new ObjectMapper().readValue(req.getInputStream(), Map.class);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                String code = loginData.get("code");
                checkCode(resp, code, verify_code);
            }
            String username = loginData.get(getUsernameParameter());
            String password = loginData.get(getPasswordParameter());
            if (username == null) {
                username = "";
            }
            if (password == null) {
                password = "";
            }
            username = username.trim();
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            setDetails(req, authRequest);
            Hr principal = new Hr();
            principal.setUsername(username);
            /*request.getSession(true)：若存在会话则返回该会话，否则新建一个会话。*/
            sessionRegistry.registerNewSession(req.getSession(true).getId(), principal);
            return this.getAuthenticationManager().authenticate(authRequest);
        }else {
            checkCode(resp, req.getParameter("code"), verify_code);
            return super.attemptAuthentication(req, resp);
        }
    }

    public void checkCode(HttpServletResponse resp, String code, String verify_code) {
        if (code == null || verify_code == null || "".equals(code) || !verify_code.toLowerCase().equals(code.toLowerCase())) {
            //验证码不正确
            throw new AuthenticationServiceException("验证码不正确");
        }
    }
}
