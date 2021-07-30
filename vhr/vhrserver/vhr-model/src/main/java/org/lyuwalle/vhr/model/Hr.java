package org.lyuwalle.vhr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/*
 * UserDetails接口是提供用户信息的核心接口
 * UserDetails 默认提供了：
 *
 * 用户的权限集， 默认需要添加ROLE_ 前缀
 * 用户的加密后的密码， 不加密会使用{noop}前缀
 * 应用内唯一的用户名
 * 账户是否过期
 * 账户是否锁定
 * 凭证是否过期
 * 用户是否可用
 */
@Data
public class Hr implements UserDetails {
    private Integer id;

    private String name;

    private String phone;

    private String telephone;

    private String address;

    private Boolean enabled;

    private String username;

    private String password;

    private String userface;

    private String remark;

    /*setRoles方法在HrService里面实现*/
    private List<Role> roles;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hr hr = (Hr) o;
        return Objects.equals(username, hr.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }/*账户没有过期*/

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }/*账户没有锁定,return true表示账户不会被锁定*/

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }/*认证没有过期，return true表示账户凭证不会过期*/

    @Override
    public boolean isEnabled() {
        return enabled;
    }/*返回自己定义的enabled*/

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    /**
     * UserDetails中还有一个方法叫做getAuthorities，该方法用来获取当前用户所具有的角色
     * 直接从roles中获取当前用户所具有的角色，构造SimpleGrantedAuthority然后返回即可。
     * @return
     */
    @Override
    /*生成json忽略掉这个authorities*/
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(roles.size());
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }
}