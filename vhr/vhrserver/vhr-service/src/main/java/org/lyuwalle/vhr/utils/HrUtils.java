package org.lyuwalle.vhr.utils;

import org.lyuwalle.vhr.model.Hr;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @Author lyuWalle
 * @Date 2020/12/20 21:03
 * @Version 1.0
 * 这是一个工具类，用于获取当前的用户
 */
public class HrUtils {
    public static Hr getCurrentHr(){
        return ((Hr) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
