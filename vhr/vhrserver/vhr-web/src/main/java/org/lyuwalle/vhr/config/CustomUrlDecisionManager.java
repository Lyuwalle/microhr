package org.lyuwalle.vhr.config;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @Author lyuWalle
 * @Date 2020/12/17 1:14
 * @Version 1.0
 */
/**
 * 自定义权限管理：实现AccessDecisionManager接口
 * 判断当前用户是否具备CustomFilterInvocationSecurityMetadataSource分析出来的角色
 * 判断当前请求具有的权限和当前请求需要的权限的关系。
 */
@Component
public class CustomUrlDecisionManager implements AccessDecisionManager {
    /**
     *
     * @param authentication    保存了当前登录用户的角色信息
     *                          只有登录成功之后才有这个authentication，在SecurityConfig里面的successHandler
     *                          里面onAuthenticationSuccess的参数里面就有authentication
     * @param object
     * @param collection        CustomFilterInvocationSecurityMetadataSource中的getAttributes方法传来的，
     *                          表示当前请求需要的角色（可能有多个）
     * @throws AccessDeniedException
     * @throws InsufficientAuthenticationException
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        for(ConfigAttribute configAttribute : collection){
            String needRole = configAttribute.getAttribute();
            if ("ROLE_LOGIN".equals(needRole)) {
                if (authentication instanceof AnonymousAuthenticationToken) {
                    throw new AccessDeniedException("尚未登录，请登录1111111111111!");
                }else {
                    return;//?
                }
            }
            //当前用户所具有的权限，获取当前登录用户的角色
            //遍历collection，同时查看当前用户的角色列表中是否具备需要的权限，如果具备就直接返回，否则就抛异常。
            //用户类实现了UserDetails接口，那么必须要实现getAuthorities这个方法
            //该方法用来获取当前用户所具有的角色
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(needRole)) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("权限不足，请联系管理员!");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
