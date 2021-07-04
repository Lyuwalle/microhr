package org.lyuwalle.vhr.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lyuwalle.vhr.model.Hr;
import org.lyuwalle.vhr.model.RespBean;
import org.lyuwalle.vhr.service.HrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.session.ConcurrentSessionFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author lyuWalle
 * @Date 2020/12/12 15:16
 * @Version 1.0
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    HrService hrService;

    @Autowired
    CustomFilterInvocationSecurityMetadataSource cfs;

    @Autowired
    CustomUrlDecisionManager decisionManager;

    /*密码加密*/
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    VerificationCodeFilter verificationCodeFilter;

    /*以下这些urL不会经过SpringSecurity拦截，不用登录就可以访问这些接口*/
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/index.html", "/img/**", "/fonts/**", "/favicon.ico", "/verifyCode", "/login");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(hrService);
    }

    /**
     * 保存了所有认证成功后用户的SessionInformation信息，每次用户访问服务器的会从sessionRegistry中
     * 查询出当前用户的session信息 ，判断是否过期以及刷新最后一次方法时间，默认的实现类SessionRegistryImpl，
     * 监听了session的销毁事件，若销毁，那么删除掉session信息
     */
    @Bean
    SessionRegistryImpl sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    LoginFilter loginFilter() throws Exception{
        LoginFilter loginFilter = new LoginFilter();
        //登录成功
        loginFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {
                    response.setContentType("application/json;charset=utf-8");
                    //PrintWriter类可用来创建一个文件并向文本文件写入数据。可以理解为java中的文件输出
                    PrintWriter out = response.getWriter();
                    //登录成功之后把hr的信息写成一个json字符串返回出去,在这里用到了ObjectMapper这个类
                    Hr hr = (Hr) authentication.getPrincipal();
                    hr.setPassword(null);
                    RespBean ok = RespBean.ok("登录成功!", hr);
                    //ObjectMapper 是一个使用 Swift 语言编写的数据模型转换框架，
                    // 我们可以方便的将模型对象转换为JSON，或者JSON生成相应的模型类
                    String s = new ObjectMapper().writeValueAsString(ok);
                    out.write(s);
                    out.flush();
                    out.close();
                }
        );
        //登录失败
        loginFilter.setAuthenticationFailureHandler((request, response, exception) -> {
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    RespBean respBean = RespBean.error(exception.getMessage());
                    if (exception instanceof LockedException) {
                        respBean.setMsg("账户被锁定，请联系管理员!");
                    } else if (exception instanceof CredentialsExpiredException) {
                        respBean.setMsg("密码过期，请联系管理员!");
                    } else if (exception instanceof AccountExpiredException) {
                        respBean.setMsg("账户过期，请联系管理员!");
                    } else if (exception instanceof DisabledException) {
                        respBean.setMsg("账户被禁用，请联系管理员!");
                    } else if (exception instanceof BadCredentialsException) {
                        respBean.setMsg("用户名或者密码输入错误，请重新输入!");
                    }
                    out.write(new ObjectMapper().writeValueAsString(respBean));
                    out.flush();
                    out.close();
                }
        );
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        loginFilter.setFilterProcessesUrl("/doLogin");

        //session管理策略
        //要限制一个账户只能登录一次，不能在多个浏览器登录，也就是一个账户只能对应一个sessionid。
        //配置上默认的 session并发策略ConcurrentSessionControlAuthenticationStrategy类，设置maximumSessions值为1。
        //ConcurrentSessionControlAuthenticationStrategy是SessionAuthenticationStrategy的实现类
        ConcurrentSessionControlAuthenticationStrategy sessionStrategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
        sessionStrategy.setMaximumSessions(1);
        loginFilter.setSessionAuthenticationStrategy(sessionStrategy);
        return loginFilter;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        //把自定义的验证码过滤器加在UsernamePasswordAuthenticationFilter过滤器的前面，这样就会先走验证码过滤器
//        http.addFilterBefore(verificationCodeFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeRequests()
                //.anyRequest().authenticated()    这个表示任何的请求都会被授权
                /*下面的代码则加入了自己的权限授权判断*/
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {

                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setSecurityMetadataSource(cfs);
                        object.setAccessDecisionManager(decisionManager);
                        return object;
                    }
                })
                .and()
                .logout()
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        out.write(new ObjectMapper().writeValueAsString(RespBean.ok("注销成功！")));
                        out.flush();
                        out.close();
                    }
                })
                .permitAll()
                .and()
                .csrf().disable()
                .cors().disable()
                /*没有登录时，在这里处理结果，不需要重定向。那前面的.loginPage("/login")就不再需要
                前端重定向的结果就是localhost:8080/login，而不经过nodejs*/
                /*这是一个跨域的问题，解决办法就是直接提示重新登录*/

                /*在路由导航守卫已经解决了没有登录直接访问的问题，那下面这个还需要吗？？？？？？？？？？？？？？？？？？*/
                /*需要的。路由导航守卫解决的是window.sessionStorage里面没有user的缓存信息，则直接去登录页面*/
                /*当后端重启时，前端尚有user缓存，不注销登录的前提下如果再去访问一个页面，就会提示下面的信息*/
                .exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException, ServletException {
                resp.setContentType("application/json;charset=utf-8");
                resp.setStatus(401);
                PrintWriter out = resp.getWriter();
                RespBean error = RespBean.error();
                if(e instanceof InsufficientAuthenticationException){
                    /*那么下面这句话前端就看不到了，前端如果发现是401就会replace到登录页面，这句话只有在用postman测试时才会看到*/
                    error.setMsg("权限不足，请注销后重新登录！InsufficientAuthenticationException");
                }
                out.write(new ObjectMapper().writeValueAsString(error));
                out.flush();
                out.close();
            }
        });
        http.addFilterAt(new ConcurrentSessionFilter(sessionRegistry(), event -> {
            HttpServletResponse resp = event.getResponse();
            resp.setContentType("application/json;charset=utf-8");
            resp.setStatus(401);
            PrintWriter out = resp.getWriter();
            out.write(new ObjectMapper().writeValueAsString(RespBean.error("您已在另一台设备登录，本次登录已下线!")));
            out.flush();
            out.close();
        }), ConcurrentSessionFilter.class);
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);

    }
}
