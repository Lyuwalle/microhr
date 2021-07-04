package org.lyuwalle.vhr.model;

import java.io.Serializable;

/**
 * 这个类主要保存keepAlive和requireAuth两个字段
 * 表示menu类中的属性，再把meta的对象放在menu中
 */
public class Meta implements Serializable {
    private Boolean keepAlive;

    private Boolean requireAuth;

    public Boolean getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(Boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public Boolean getRequireAuth() {
        return requireAuth;
    }

    public void setRequireAuth(Boolean requireAuth) {
        this.requireAuth = requireAuth;
    }
}
