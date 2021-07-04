package org.lyuwalle.vhr.config;

import org.lyuwalle.vhr.model.Menu;
import org.lyuwalle.vhr.model.Role;
import org.lyuwalle.vhr.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;

import static org.springframework.security.access.SecurityConfig.createList;

/**
 * @Author lyuWalle
 * @Date 2020/12/17 0:30
 * @Version 1.0
 */
/**
 * 自定义权限拦截,
 * 这个类的作用，主要是根据用户传来的请求地址，分析出请求需要的角色,在getAttributes方法中返回的是一个角色数组。
 */
@Component
public class CustomFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    MenuService menuService;

    //类AntPathMatcher主要用来做类URLs字符串匹配
    AntPathMatcher antPathMatcher = new AntPathMatcher();
    /**
     * 我们可以从getAttributes(Object object)方法的参数object中提取出当前的请求url，然后将这个请求url和数据库中查询出来的所有
     * url pattern一一对照，看符合哪一个url pattern，然后就获取到该url pattern所对应的角色，当然这个角色可能有多个，
     * 所以遍历角色，最后利用SecurityConfig.createList方法来创建一个角色集合。
     * @param object
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        String requestUrl = ((FilterInvocation) object).getRequestUrl();
        /**
         * getAllMenusWithRole表示一个一对多的查询，前面的列是Menu，后面的列是Role,一个Menu对应多个Role
         */
        List<Menu> menus = menuService.getAllMenusWithRole();
        for(Menu menu : menus){
            if(antPathMatcher.match(menu.getUrl(), requestUrl)){
                List<Role> roles = menu.getRoles();         //List<Roles>是Menu类中的一个属性，表示这个菜单需要哪些角色才能访问
                String[] roleNames = new String[roles.size()];
                for(int i = 0; i < roles.size(); i++){
                    roleNames[i] = roles.get(i).getName();
                }
                return SecurityConfig.createList(roleNames);
            }
        }
        //只是一个标记，下一步如果遇到这个，那么就让他重新登录
        //具体是在CustomUrlDecisionManager里面的decide中判断
        return SecurityConfig.createList("ROLE_LOGIN");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
