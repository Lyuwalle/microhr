package org.lyuwalle.vhr.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 这个类主要保存keepAlive和requireAuth两个字段
 * 表示menu类中的属性，再把meta的对象放在menu中
 */
@Data
public class Meta implements Serializable {
    private Boolean keepAlive;

    private Boolean requireAuth;

}
