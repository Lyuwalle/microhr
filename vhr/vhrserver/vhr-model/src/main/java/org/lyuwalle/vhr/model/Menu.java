package org.lyuwalle.vhr.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Menu implements Serializable {
    private Integer id;

    private String url;

    private String path;

    private String component;

    private String name;

    private String iconCls;

//    把keepAlive和requireAuth保存在了Meta这个类中
    private Meta meta;

    private Integer parentId;

    private Boolean enabled;
    private List<Menu> children;        //表示这个菜单有几个子菜单
    private List<Role> roles;           //表示要访问这个菜单需要哪些角色

}